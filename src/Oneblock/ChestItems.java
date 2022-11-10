package Oneblock;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ChestItems {
	static List <Material> s_ch = new ArrayList <>();
	static List <Material> m_ch = new ArrayList <>();
	static List <Material> h_ch = new ArrayList <>();
	
	public static enum type {
		SMALL, MEDIUM, HIGH
    }
    
	public static void loadFromString(
			List<String> small_chest,
			List<String> medium_chest,
			List<String> high_chest) {
		
    	s_ch.clear(); m_ch.clear(); h_ch.clear();
    	
    	for (String s: small_chest) 
        	s_ch.add(Material.getMaterial(s));
        for (String s: medium_chest) 
        	m_ch.add(Material.getMaterial(s));
        for (String s: high_chest) 
        	h_ch.add(Material.getMaterial(s));
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
