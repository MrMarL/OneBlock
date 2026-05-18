package oneblock.loot;

import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import oneblock.Oneblock;

/**
 * Places a vanilla chest at the given block and populates it with the contents
 * of a {@link LootTable} referenced by {@link NamespacedKey}. Guarded against
 */
public class LootTableDispatcher {
	private static final Logger LOG = Oneblock.plugin.getLogger();
	private static final String FALLBACK_KEY = "chests/simple_dungeon";
	
	public static boolean populate(Chest chest, NamespacedKey key, Random rnd) {
		if (key == null) return false;
		Inventory inv = chest.getInventory();
		
		LootTable table = getLootTable(key);
		if (table == null) return false;
		
		try {
			LootContext ctx = new LootContext.Builder(chest.getLocation()).build();
			table.fillInventory(inv, rnd, ctx);
			return true;
		} catch (Throwable t) {
			LOG.warning("Loot table '" + key + "' failed to populate: " + t.getMessage());
			return false;
		}
	}
	
	/**
	 * I don't know how to get LootTable in 1.8 - 1.12...
	 * Bukkit.getLootTable(key) for 1.13+
	 */
	public static LootTable getLootTable(NamespacedKey key) {
		if (key == null) return null;
	    if (Oneblock.legacy) return null;
	    
	    LootTable table = Bukkit.getLootTable(key);
	    if (table == null) {
	    	LOG.warning("Loot table '" + key + "' not found; using vanilla fallback '" + FALLBACK_KEY + "'.");
	    	table = Bukkit.getLootTable(NamespacedKey.minecraft(FALLBACK_KEY));
	    }
	    return table;  // 1.13+
	}
}
