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

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import oneblock.ChestItems;
import oneblock.Oneblock;

/**
 * One-shot migrator that rewrites the legacy cumulative-pool {@code blocks.yml}
 * into the new strict-discrete, weighted-map schema.
 *
 * <p>The blocks migrator FLATTENS the cumulative pool: level N's new pool is
 * an explicit, self-contained entry list that represents the union of levels
 * 0..N from the legacy file, with duplicate entries collapsed into summed
 * integer weights. This preserves the pre-migration sampling distribution 1:1
 * while producing a strict-discrete runtime-friendly config.
 */
public class LegacyBlocksMigrator {
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
	
	// ---------- Public entry points ----------
	
	public static void migrateBlocks(File blocksFile, File chestsFile) {
		if (blocksFile == null || !blocksFile.exists()) return;
		YamlConfiguration legacy = YamlConfiguration.loadConfiguration(blocksFile);
		if (!isLegacyBlocks(legacy)) return;
		
		Oneblock.plugin.getLogger().info("Legacy blocks.yml detected. Migrating to weighted schema (original backed up to " + blocksFile.getName() + ".bak).");
		
		if (!backup(blocksFile)) return;
		

		
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
				String key = classify((String) o);
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
		out.set("MaxLevel", maxList);
		
		try { out.save(blocksFile); }
		catch (Exception e) {
			Oneblock.plugin.getLogger().warning("Failed to write migrated blocks.yml: " + e.getMessage());
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

	
	/** Classify a legacy pool string into a kind|value accumulator key. */
	private static String classify(String text) {
		if (text == null || text.isEmpty()) return null;
		if (text.charAt(0) == '/') return "command|" + text;
		if (ChestItems.hasChest(text)) return "chest|" + text;
		
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
