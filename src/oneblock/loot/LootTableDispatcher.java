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
import org.bukkit.loot.LootTables;

import oneblock.Oneblock;

/**
 * Places a vanilla chest at the given block and populates it with the contents
 * of a {@link LootTable} referenced by {@link NamespacedKey}. Guarded against
 */
public class LootTableDispatcher {
	private static final Logger LOG = Bukkit.getLogger();
	private static final NamespacedKey FALLBACK_KEY = LootTables.SIMPLE_DUNGEON.getKey();
	
	public static boolean populate(Block block, NamespacedKey key, Random rnd) {
		if (block == null) return false;
		block.setType(Material.CHEST);

		if (!(block.getState() instanceof Chest)) return false;
		Chest chest = (Chest) block.getState();
		Inventory inv = chest.getInventory();
		
		LootTable table = getLootTable(key);
		if (table == null) {
			LOG.warning("[Oneblock] Loot table '" + key + "' not found; using vanilla fallback '" + FALLBACK_KEY + "'.");
			table = getLootTable(FALLBACK_KEY);
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
	
	
	/**
	 * I don't know how to get LootTable in 1.8 - 1.12...
	 */
	public static LootTable getLootTable(NamespacedKey key) {
		if (key == null) return null;
		if (!Oneblock.legacy) Bukkit.getLootTable(key);
		
		return null;
	}
}
