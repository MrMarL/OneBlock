package oneblock.migration;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import oneblock.Oneblock;

/**
 * One-shot migrator that rewrites the legacy cumulative-pool {@code blocks.yml}
 * and the legacy {@code ItemStack}-list {@code chests.yml} into the new
 * strict-discrete, weighted-map schema.
 *
 * <p>The blocks migrator FLATTENS the cumulative pool: level N's new pool is
 * an explicit, self-contained entry list that represents the union of levels
 * 0..N from the legacy file, with duplicate entries collapsed into summed
 * integer weights. This preserves the pre-migration sampling distribution 1:1
 * while producing a strict-discrete runtime-friendly config.
 */
public class LegacyBlocksMigrator {
	private static final Map<String, String> DEFAULT_CHEST_LOOT_TABLES = new LinkedHashMap<>();
	static {
		DEFAULT_CHEST_LOOT_TABLES.put("small_chest",  "minecraft:chests/simple_dungeon");
		DEFAULT_CHEST_LOOT_TABLES.put("medium_chest", "minecraft:chests/abandoned_mineshaft");
		DEFAULT_CHEST_LOOT_TABLES.put("high_chest",   "minecraft:chests/end_city_treasure");
	}
	private static final String FALLBACK_LOOT = "minecraft:chests/simple_dungeon";
	
	// ---------- Detection ----------
	
	/** blocks.yml is legacy iff {@code MaxLevel} is a scalar, or any pool entry (idx >= header) is a raw String. */
	public static boolean isLegacyBlocks(YamlConfiguration config) {
		Object maxLevel = config.get("MaxLevel");
		if (maxLevel instanceof String) return true;
		for (String key : config.getKeys(false)) {
			if ("MaxLevel".equalsIgnoreCase(key)) continue;
			List<?> list = config.getList(key);
			if (list == null) continue;
			int headerEnd = detectHeaderEnd(list);
			for (int i = headerEnd; i < list.size(); i++) {
				Object it = list.get(i);
				if (it instanceof Map) return false;
				if (it instanceof String) return true;
			}
		}
		return false;
	}
	
	/** chests.yml is legacy iff any top-level value is a list (of ItemStacks / material names). */
	public static boolean isLegacyChests(YamlConfiguration config) {
		for (String key : config.getKeys(false)) {
			Object v = config.get(key);
			if (v instanceof List) return true;
			if (v instanceof String) return false;
		}
		return false;
	}
	
	// ---------- Public entry points ----------
	
	public static void migrateBlocks(File blocksFile, File chestsFile) {
		if (blocksFile == null || !blocksFile.exists()) return;
		YamlConfiguration legacy = YamlConfiguration.loadConfiguration(blocksFile);
		if (!isLegacyBlocks(legacy)) return;
		
		Oneblock.plugin.getLogger().info("Legacy blocks.yml detected. Migrating to weighted schema (original backed up to " + blocksFile.getName() + ".bak).");
		
		if (!backup(blocksFile)) return;
		
		Map<String, String> chestAliases = buildChestAliasMap(chestsFile);
		
		List<Integer> levelIds = new ArrayList<>();
		for (String key : legacy.getKeys(false)) {
			if ("MaxLevel".equalsIgnoreCase(key)) continue;
			try { levelIds.add(Integer.parseInt(key)); } catch (NumberFormatException ignore) {}
		}
		Collections.sort(levelIds);
		
		YamlConfiguration out = new YamlConfiguration();
		
		for (int id : levelIds) {
			LinkedHashMap<String, Integer> acc = new LinkedHashMap<>();
			String strKey = String.valueOf(id);
			List<?> raw = legacy.getList(strKey);
			if (raw == null || raw.isEmpty()) continue;
			
			int headerEnd = detectHeaderEnd(raw);
			List<Object> newList = new ArrayList<>();
			for (int i = 0; i < headerEnd && i < raw.size(); i++)
				newList.add(raw.get(i));
			
			// Merge this level's pool entries into the flattening accumulator.
			for (int i = headerEnd; i < raw.size(); i++) {
				Object o = raw.get(i);
				if (!(o instanceof String)) continue;
				String key = classify((String) o, chestAliases);
				if (key != null) acc.compute(key, (k, v) -> v == null ? 1 : v + 1);
			}
			
			int levelEntries = 0;
			int levelWeight  = 0;
			for (Map.Entry<String, Integer> e : acc.entrySet()) {
				newList.add(buildEntryMap(e.getKey(), e.getValue()));
				levelEntries++;
				levelWeight += e.getValue();
			}
			
			out.set(strKey, newList);
			Oneblock.plugin.getLogger().info(String.format("Migrated level %d: %d entries, total weight %d", id, levelEntries, levelWeight));
		}
		
		Object legacyMax = legacy.get("MaxLevel");
		String maxName = legacyMax instanceof String ? (String) legacyMax : "Level: MAX";
		
		List<Object> maxList = new ArrayList<>();
		maxList.add(maxName);
		//for (Map.Entry<String, Integer> e : acc.entrySet())
		//	maxList.add(buildEntryMap(e.getKey(), e.getValue()));
		out.set("MaxLevel", maxList);
		
		try { out.save(blocksFile); }
		catch (Exception e) {
			Oneblock.plugin.getLogger().warning("Failed to write migrated blocks.yml: " + e.getMessage());
		}
	}
	
	public static void migrateChests(File chestsFile) {
		if (chestsFile == null || !chestsFile.exists()) return;
		YamlConfiguration legacy = YamlConfiguration.loadConfiguration(chestsFile);
		if (!isLegacyChests(legacy)) return;
		
		Bukkit.getLogger().info("[Oneblock] Legacy chests.yml detected. Migrating to LootTable alias map (original backed up to " + chestsFile.getName() + ".bak).");
		if (!backup(chestsFile)) return;
		
		YamlConfiguration out = new YamlConfiguration();
		for (String name : legacy.getKeys(false)) {
			String key = DEFAULT_CHEST_LOOT_TABLES.getOrDefault(name.toLowerCase(), FALLBACK_LOOT);
			out.set(name, key);
			if (!DEFAULT_CHEST_LOOT_TABLES.containsKey(name.toLowerCase())) {
				Oneblock.plugin.getLogger().warning("Unknown legacy chest '" + name + "'; mapped to " + FALLBACK_LOOT + " — customize in chests.yml if desired.");
			}
		}
		try { out.save(chestsFile); }
		catch (Exception e) {
			Oneblock.plugin.getLogger().warning("Failed to write migrated chests.yml: " + e.getMessage());
		}
	}
	
	// ---------- Helpers ----------
	
	private static boolean backup(File f) {
		try {
			Path src = f.toPath();
			Path dst = Paths.get(f.getAbsolutePath() + ".bak");
			Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING);
			return true;
		} catch (Exception e) {
			Oneblock.plugin.getLogger().warning("Failed to back up " + f + ": " + e.getMessage());
			return false;
		}
	}
	
	/** Replicates the positional header detection used by the live blocks.yml parser. */
	private static int detectHeaderEnd(List<?> raw) {
		int q = 0;
		if (q < raw.size() && raw.get(q) instanceof String) q++; // name
		if (!Oneblock.superlegacy) {
			if (q < raw.size() && raw.get(q) instanceof String) {
				String s = (String) raw.get(q);
				try { BarColor.valueOf(s.toUpperCase()); q++; } catch (Exception ignore) {}
			}
			if (q < raw.size() && raw.get(q) instanceof String) {
				String s = (String) raw.get(q);
				try { BarStyle.valueOf(s.toUpperCase()); q++; } catch (Exception ignore) {}
			}
		}
		if (q < raw.size()) {
			Object o = raw.get(q);
			if (o instanceof Number) q++;
			else if (o instanceof String) {
				try { Integer.parseInt((String) o); q++; } catch (Exception ignore) {}
			}
		}
		return q;
	}
	
	/** Build a chest-alias → loot-table-string lookup from the legacy chests.yml keys. */
	private static Map<String, String> buildChestAliasMap(File chestsFile) {
		Map<String, String> map = new LinkedHashMap<>();
		if (chestsFile == null || !chestsFile.exists()) return map;
		YamlConfiguration cfg = YamlConfiguration.loadConfiguration(chestsFile);
		for (String name : cfg.getKeys(false)) {
			// Prefer the already-migrated value if the user ran chests.yml migration first.
			Object v = cfg.get(name);
			String lt;
			if (v instanceof String) {
				lt = (String) v;
			} else {
				lt = DEFAULT_CHEST_LOOT_TABLES.getOrDefault(name.toLowerCase(), FALLBACK_LOOT);
			}
			// Both case forms needed because `classify()` tries a case-sensitive
			// lookup first then falls back to lowercase. Skip the second put if
			// the user's key is already lowercase (else we'd overwrite with the
			// same value).
			map.put(name, lt);
			String lower = name.toLowerCase();
			if (!lower.equals(name)) map.put(lower, lt);
		}
		return map;
	}
	
	/** Classify a legacy pool string into a kind|value accumulator key. */
	private static String classify(String text, Map<String, String> chestMap) {
		if (text == null || text.isEmpty()) return null;
		if (text.charAt(0) == '/') return "command|" + text;
		
		String lt = chestMap.get(text);
		if (lt == null) lt = chestMap.get(text.toLowerCase());
		if (lt != null) return "loot_table|" + lt;
		
		try { EntityType.valueOf(text.toUpperCase()); return "mob|" + text.toUpperCase(); }
		catch (Exception ignore) {}
		
		return "block|" + text;
	}
	
	private static Map<String, Object> buildEntryMap(String accKey, int weight) {
		int bar = accKey.indexOf('|');
		String kind = accKey.substring(0, bar);
		String val  = accKey.substring(bar + 1);
		Map<String, Object> m = new LinkedHashMap<>();
		m.put(kind, val);
		m.put("weight", weight);
		return m;
	}
}
