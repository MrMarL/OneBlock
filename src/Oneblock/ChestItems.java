package Oneblock;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ChestItems {
	public static File chest;
	private static Map<String, List<ItemStack>> chests = new HashMap<>();
	
	public static void save() {
		YamlConfiguration config = new YamlConfiguration();
		
		for (Map.Entry<String, List<ItemStack>> entry : chests.entrySet()) {
			List<Object> simplifiedItems = new ArrayList<>();
            
			for (ItemStack item : entry.getValue()) {
				if (item == null || item.getType() == Material.AIR) continue;
                    
				if (!item.hasItemMeta()) 
					simplifiedItems.add(item.getType().name());
				else 
					simplifiedItems.add(item);
			}
			config.set(entry.getKey(), simplifiedItems);
		}
		
		try { config.save(chest); } catch (Exception e) { }
	}
    
	public static void load() {
    	YamlConfiguration config = YamlConfiguration.loadConfiguration(chest);
    	chests.clear();
    	
    	for(String name : config.getKeys(false)) {
    		List<ItemStack> items = new ArrayList<>();
    		loadItems(items, config.getList(name));
    		chests.put(name, items);
    	}
    }
	
	private static void loadItems(List<ItemStack> arr, List<?> data) {
		if (data == null) return;
		for (Object entry : data) {
	        if (entry instanceof ItemStack) 
	        	arr.add((ItemStack) entry);
	        else if (entry instanceof String) {
	        	Material m = Material.getMaterial((String) entry);
	    		if (m != null) 
	    			arr.add(new ItemStack(m));
	        }
	    }
	}
	
	public static Set<String> getChestNames() {
		return chests.keySet();
	}
	
	public static List<ItemStack> getChest(String chestType) {
		return chests.get(chestType);
	}
	
	public static boolean fillChest(Inventory inv, String chestType) {
		List<ItemStack> ch = getChest(chestType);
		
		final int max = Oneblock.rnd.nextInt(3) + 2;
        try { for (int i = 0; i < max; i++) {
        	ItemStack m = ch.get(Oneblock.rnd.nextInt(ch.size()));
            if (m.getMaxStackSize() == 1)
            	m.setAmount(1);
            else
            	m.setAmount(Oneblock.rnd.nextInt(4) + 2);
            inv.addItem(m);
            }
        } catch (Exception e) { return false; }
        return true;
	}
}
