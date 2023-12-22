package Oneblock;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ChestItems {
	public static File chest;
	
	public static List <ItemStack> s_ch = new ArrayList <>();
	public static List <ItemStack> m_ch = new ArrayList <>();
	public static List <ItemStack> h_ch = new ArrayList <>();
	
	public static enum type {
		SMALL, MEDIUM, HIGH
    }
	
	public static void save() {
		YamlConfiguration config = YamlConfiguration.loadConfiguration(chest);
		config.set("small_chest", s_ch);
		config.set("medium_chest", m_ch);
		config.set("high_chest", h_ch);
		try { config.save(chest); } catch (Exception e) { }
	}
    
	@SuppressWarnings("unchecked")
	public static void load() {
    	YamlConfiguration config = YamlConfiguration.loadConfiguration(chest);
    	s_ch.clear(); m_ch.clear(); h_ch.clear();
    	
    	if (!loadMaterial(s_ch, config.getStringList("small_chest"))) 
    		try {s_ch.addAll((List<ItemStack>) config.get("small_chest"));} catch(Exception e) {}
    		
    	if (!loadMaterial(m_ch, config.getStringList("medium_chest")))
    		try {m_ch.addAll((List<ItemStack>) config.get("medium_chest"));} catch(Exception e) {}
    	
    	if (!loadMaterial(h_ch, config.getStringList("high_chest")))
	    	try {h_ch.addAll((List<ItemStack>) config.get("high_chest"));} catch(Exception e) {}
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
	
	public static type getType(String chestType) {
		return type.valueOf(chestType);
	}
	
	public static List <ItemStack> getChest(type chestType) {
		switch (chestType) {
			case MEDIUM: return m_ch;
			case HIGH: return h_ch;
			default: return s_ch;
		}
	}
	
	public static boolean fillChest(Inventory inv, type chestType) {
		List <ItemStack> ch = getChest(chestType);
		
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
