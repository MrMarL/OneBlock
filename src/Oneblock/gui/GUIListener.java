package Oneblock.gui;

import java.util.List;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import Oneblock.ChestItems;

public class GUIListener implements Listener {

	@EventHandler
    public void onPlayerClickInventory(final InventoryClickEvent e){
    	if (!GUI.enabled)
    		return;
        Inventory inv = e.getInventory();
        if (inv.getHolder() == null)
        	return;
        if (!inv.getHolder().getClass().isAssignableFrom(GUIHolder.class))
        	return;
        
        e.setCancelled(true);
	        
	    if (inv.getSize() == 9) {
	    	HumanEntity he = e.getWhoClicked();
		    if (!(he instanceof Player))
		        return;
		    Player pl = (Player) he;
		    
		    if (GUI.baseGUI != null && GUI.baseGUI.equals(inv)) {
		    	if (e.getClickedInventory() != inv)
		    		return;
		        ItemStack item = e.getCurrentItem();
		        if (item == null)
		        	return;
		        pl.closeInventory();
		        ItemMeta meta = item.getItemMeta();
		        if (meta == null)
		        	return;
		        String command = meta.getDisplayName();
		        if (command.contains("/")) 
		        	pl.performCommand(command.split("/")[1]);
		    }
		    else {
		    	pl.closeInventory();
		        if (e.getRawSlot() == 2)
		        	pl.performCommand("ob accept");
		    }
        }
    }
}