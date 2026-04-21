package oneblock.loot;

import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;

import oneblock.Oneblock;

/**
 * Places a vanilla chest at the given block and populates it with the contents
 * of a {@link LootTable} referenced by {@link NamespacedKey}. Guarded against
 * pre-1.9 servers where the LootTable API is unavailable.
 */
public class LootTableDispatcher {
	private static final Logger LOG = Bukkit.getLogger();
	private static final NamespacedKey FALLBACK_KEY = NamespacedKey.minecraft("chests/simple_dungeon");
	private static boolean legacyWarned = false;
	
	public static boolean populate(Block block, NamespacedKey key, Random rnd) {
		if (block == null) return false;
		block.setType(Material.CHEST);
		if (Oneblock.superlegacy) {
			if (!legacyWarned) {
				LOG.warning("[Oneblock] LootTable API is unsupported on this server version; chests will spawn empty.");
				legacyWarned = true;
			}
			return true;
		}
		if (!(block.getState() instanceof Chest)) return false;
		Chest chest = (Chest) block.getState();
		Inventory inv = chest.getInventory();
		
		LootTable table = key == null ? null : Bukkit.getLootTable(key);
		if (table == null) {
			LOG.warning("[Oneblock] Loot table '" + key + "' not found; using vanilla fallback '" + FALLBACK_KEY + "'.");
			table = Bukkit.getLootTable(FALLBACK_KEY);
			if (table == null) return false;
		}
		
		try {
			LootContext ctx = new LootContext.Builder(block.getLocation()).build();
			table.fillInventory(inv, rnd, ctx);
			return true;
		} catch (Throwable t) {
			LOG.warning("[Oneblock] Loot table '" + key + "' failed to populate: " + t.getMessage());
			return false;
		}
	}
}
