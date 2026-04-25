package oneblock;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Registry mapping chest-alias names (as referenced from {@code blocks.yml}
 * pool entries) to vanilla {@link NamespacedKey} loot-table identifiers.
 *
 * <p>Since the loot population was migrated to the native Bukkit LootTable API,
 * this class no longer stores {@code ItemStack} lists. Legacy {@code chests.yml}
 * files are converted by {@link oneblock.migration.LegacyBlocksMigrator} before
 * {@link #load()} runs.
 */
public class ChestItems {
	public static File chest;
	private static final Map<String, NamespacedKey> aliases = new LinkedHashMap<>();
	
	public static void save() {
		YamlConfiguration config = new YamlConfiguration();
		for (Map.Entry<String, NamespacedKey> e : aliases.entrySet())
			config.set(e.getKey(), e.getValue().toString());
		try { config.save(chest); } catch (Exception e) {
			Oneblock.plugin.getLogger().warning("Failed to save chests.yml: " + e.getMessage());
		}
	}
	
	public static void load() {
		aliases.clear();
		if (chest == null || !chest.exists()) return;
		YamlConfiguration config = YamlConfiguration.loadConfiguration(chest);
		for (String name : config.getKeys(false)) {
			String keyString = config.getString(name);
			if (keyString == null) continue;
			NamespacedKey key = parseKey(keyString);
			if (key != null)
				aliases.put(name, key);
			else
				Oneblock.plugin.getLogger().warning("Unknown loot-table key '" + keyString + "' for chest alias '" + name + "'");
		}
	}
	
	public static Set<String> getChestNames() {
		return Collections.unmodifiableSet(aliases.keySet());
	}
	
	public static NamespacedKey resolve(String aliasName) {
		if (aliasName == null) return null;
		return aliases.get(aliasName);
	}
	
	public static void setAlias(String name, NamespacedKey key) {
		if (name == null || key == null) return;
		aliases.put(name, key);
	}
	
	public static boolean removeAlias(String name) {
		return aliases.remove(name) != null;
	}
	
	/**
	 * Tolerant NamespacedKey parser. Accepts both {@code minecraft:path} and
	 * {@code namespace:path} forms. Falls back to {@link NamespacedKey#minecraft(String)}
	 * when the namespace is missing. Returns {@code null} on any validation failure.
	 */
	public static NamespacedKey parseKey(String s) {
		if (s == null || s.isEmpty()) return null;
		String lower = s.toLowerCase();
		String[] parts = lower.split(":", 2);
		String ns   = parts.length == 2 ? parts[0] : "minecraft";
		String path = parts.length == 2 ? parts[1] : parts[0];
		if ("minecraft".equals(ns)) {
			try { return NamespacedKey.minecraft(path); }
			catch (Throwable t) { return null; }
		}
		try { return NamespacedKey.fromString(lower); }
		catch (Throwable t) { return null; }
	}
}
