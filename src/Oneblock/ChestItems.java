package Oneblock;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ChestItems {
	public static File chest;
	private static Map<String, List<ItemStack>> chests = new HashMap<>();
	
	public static void save() {
		YamlConfiguration config = YamlConfiguration.loadConfiguration(chest);
		
		for (Map.Entry<String, List<ItemStack>> entry : chests.entrySet()) 
			config.set(entry.getKey(), entry.getValue());
		
		try { config.save(chest); } catch (Exception e) { }
	}
    
	@SuppressWarnings("unchecked")
	public static void load() {
    	YamlConfiguration config = YamlConfiguration.loadConfiguration(chest);
    	chests.clear();
    	
    	for(String name : config.getKeys(false)) {
    		List<ItemStack> items = new ArrayList<>();
    		
    		if (!loadMaterial(items, config.getStringList(name)))
    			try { items.addAll((List<ItemStack>) config.get(name)); } catch(Exception e) {}
    		
    		chests.put(name, items);
    	}
    }
	
	private static boolean loadMaterial(List<ItemStack> arr, List<String> data) {
		boolean result = false;
		for (String s: data) {
    		Material m = Material.getMaterial(s);
    		if (m != null)
    			result = arr.add(new ItemStack(m));
    	}
		return result;
	}
	
	public static ArrayList<String> getChestNames() {
		return new ArrayList<>(chests.keySet());
	}
	
	public static List <ItemStack> getChest(String chestType) {
		return chests.get(chestType);
	}
	
	public static boolean fillChest(Inventory inv, String chestType) {
		List <ItemStack> ch = getChest(chestType);
		
		final int max = Oneblock.plugin.rnd.nextInt(3) + 2;
        try { for (int i = 0; i < max; i++) {
        	ItemStack m = ch.get(Oneblock.plugin.rnd.nextInt(ch.size()));
            if (m.getMaxStackSize() == 1)
            	m.setAmount(1);
            else
            	m.setAmount(Oneblock.plugin.rnd.nextInt(4) + 2);
            inv.addItem(m);
            }
        } catch (Exception e) { return false; }
        return true;
	}
}
