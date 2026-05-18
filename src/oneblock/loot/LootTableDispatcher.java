package oneblock.loot;

import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Chest;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import oneblock.Oneblock;

public class LootTableDispatcher {
	private static final Logger LOG = Oneblock.plugin.getLogger();
	private static final String FALLBACK_KEY = "chests/simple_dungeon";
	
	/**
	 * Populates the given chest using a vanilla {@link LootTable}.
	 * Returns {@code false} if the key is {@code null}, the table
	 * cannot be resolved, or the server is running on a legacy
	 * version that does not support loot tables.
	 */
	public static boolean populate(Chest chest, NamespacedKey key, Random rnd) {
		if (key == null) return false;
		LootTable table = getLootTable(key);
		if (table == null) return false;
		
		LootContext ctx = new LootContext.Builder(chest.getLocation()).build();
		table.fillInventory(chest.getInventory(), rnd, ctx);
		return true;
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
