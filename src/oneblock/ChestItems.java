package oneblock;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Dual-mode chest registry: supports both legacy item lists and vanilla loot tables.
 * <p>
 * Legacy entries are stored as {@code List<ItemStack>} and used directly for
 * populating chests. Modern entries store a {@link NamespacedKey} pointing to
 * a vanilla loot table. The mode is auto-detected during loading: if the value
 * contains a colon, it's treated as a loot-table key; otherwise it's parsed as
 * a list of items.
 */
public final class ChestItems {
    public static File chest;

    private static final Map<String, NamespacedKey> aliases = new LinkedHashMap<>();
    private static final Map<String, List<ItemStack>> aliasesLegacy = new LinkedHashMap<>();

    public static void load() {
        aliasesLegacy.clear();
        aliases.clear();

        if (chest == null || !chest.exists()) return;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(chest);
        for (String name : config.getKeys(false)) {
            Object value = config.get(name);
            if (value == null) continue;

            // Loot-table mode: string containing a colon
            if (value instanceof String) {
                String str = (String) value;
                if (str.contains(":")) {
                    NamespacedKey key = parseKey(str);
                    if (key != null) {
                        aliases.put(name, key);
                    } else {
                        Oneblock.plugin.getLogger().warning(
                            "Unknown loot-table key '" + str + "' for chest alias '" + name + "'");
                    }
                    continue;
                }
                // Single item as string (legacy format with a single material name)
                List<ItemStack> items = new ArrayList<>();
                Material m = Material.getMaterial(str);
                if (m != null) items.add(new ItemStack(m));
                aliasesLegacy.put(name, items);
                continue;
            }

            // Legacy list of items
            if (value instanceof List) {
                List<ItemStack> items = new ArrayList<>();
                loadItems(items, (List<?>) value);
                aliasesLegacy.put(name, items);
            }
        }
    }

    public static void save() {
        YamlConfiguration config = new YamlConfiguration();

        // Save legacy item chests
        for (Map.Entry<String, List<ItemStack>> entry : aliasesLegacy.entrySet()) {
            List<Object> simplified = new ArrayList<>();
            for (ItemStack item : entry.getValue()) {
                if (item == null || item.getType() == Material.AIR) continue;
                if (!item.hasItemMeta()) {
                    simplified.add(item.getType().name());
                } else {
                    simplified.add(item);
                }
            }
            config.set(entry.getKey(), simplified);
        }

        // Save loot-table chests
        for (Map.Entry<String, NamespacedKey> e : aliases.entrySet())
			config.set(e.getKey(), e.getValue().toString());

        try { config.save(chest); } catch (Exception e) {
            Oneblock.plugin.getLogger().warning("Failed to save chests.yml: " + e.getMessage());
        }
    }

    /** Legacy item parsing */
    private static void loadItems(List<ItemStack> arr, List<?> data) {
        if (data == null) return;
        for (Object entry : data) {
            if (entry instanceof ItemStack) {
                arr.add((ItemStack) entry);
            } else if (entry instanceof String) {
                Material m = Material.getMaterial((String) entry);
                if (m != null) arr.add(new ItemStack(m));
            }
        }
    }

    /** Returns all chest alias names (both legacy and loot-table). */
    public static Set<String> getChestNames() {
        Set<String> names = new java.util.LinkedHashSet<>();
        names.addAll(aliasesLegacy.keySet());
        names.addAll(aliases.keySet());
        return Collections.unmodifiableSet(names);
    }

    /** Returns the legacy item list for the given alias, or null if it's a loot-table chest. */
    public static List<ItemStack> getItems(String chestType) {
        return aliasesLegacy.get(chestType);
    }

    /** Returns the loot-table key for the given alias, or null if it's a legacy chest. */
    public static NamespacedKey getNamespacedKey(String chestType) {
        return aliases.get(chestType);
    }

    /**
     * Checks whether a chest alias exists (in either legacy or loot-table storage).
     * @return true if the chest is registered, false otherwise
     */
    public static boolean hasChest(String chestType) {
        return aliases.containsKey(chestType) || aliasesLegacy.containsKey(chestType);
    }

    /** Add or update a legacy item chest. */
    public static void setItems(String name, List<ItemStack> items) {
        aliases.remove(name);
        aliasesLegacy.put(name, new ArrayList<>(items));
    }

    /** Add or update a loot-table chest. */
    public static void setLootTable(String name, NamespacedKey key) {
        aliasesLegacy.remove(name);
        aliases.put(name, key);
    }

    /** Remove a chest alias entirely. */
    public static boolean remove(String name) {
        boolean removed = aliasesLegacy.remove(name) != null;
        removed |= aliases.remove(name) != null;
        return removed;
    }
    
    /**
     * Fill an inventory from a legacy chest by name.
     * @return true if the chest exists (legacy) and at least one item was added, false otherwise
     */
    public static boolean fillLegacyChest(Inventory inv, String chestType,  Random rnd) {
        List<ItemStack> items = aliasesLegacy.get(chestType);
        if (items == null || items.isEmpty()) return false;
        return fillLegacyChest(inv, items, rnd);
    }

    private static boolean fillLegacyChest(Inventory inv, List<ItemStack> items, Random rnd) {
        if (items.isEmpty()) return false;
        final int max = rnd.nextInt(3) + 2;
        try {
            for (int i = 0; i < max; i++) {
                ItemStack m = items.get(rnd.nextInt(items.size())).clone();
                if (m.getMaxStackSize() == 1) {
                    m.setAmount(1);
                } else {
                    m.setAmount(rnd.nextInt(4) + 2);
                }
                inv.addItem(m);
            }
        } catch (Exception e) { return false; }
        return true;
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