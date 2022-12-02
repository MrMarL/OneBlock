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
	
	static List <Material> s_ch = new ArrayList <>();
	static List <Material> m_ch = new ArrayList <>();
	static List <Material> h_ch = new ArrayList <>();
	
	public static enum type {
		SMALL, MEDIUM, HIGH
    }
    
	public static void load() {
		YamlConfiguration config = YamlConfiguration.loadConfiguration(chest);
    	s_ch.clear(); m_ch.clear(); h_ch.clear();
		
    	s_ch.clear(); m_ch.clear(); h_ch.clear();
    	
    	loadMaterial(s_ch, config.getStringList("small_chest"));
    	loadMaterial(s_ch, config.getStringList("medium_chest"));
    	loadMaterial(s_ch, config.getStringList("high_chest"));
    }
	
	private static void loadMaterial(List<Material> arr, List<String> data) {
		for (String s: data) {
    		Material m = Material.getMaterial(s);
    		if (m != null)
    			arr.add(m);
    	}
	}
	
	public static boolean fillChest(Inventory inv, type chestType) {
		List <Material> ch;
		switch (chestType) {
			case MEDIUM: ch = m_ch; break;
			case HIGH: ch = h_ch; break;
			default: ch = s_ch;
		}
		
		final int max = Oneblock.rnd.nextInt(3) + 2;
        try { for (int i = 0; i < max; i++) {
            Material m = ch.get(Oneblock.rnd.nextInt(ch.size()));
            if (m.getMaxStackSize() == 1)
                inv.addItem(new ItemStack(m, 1));
            else
                inv.addItem(new ItemStack(m, Oneblock.rnd.nextInt(4) + 2));
            }
        } catch (Exception e) { return false; }
        return true;
	}
}
