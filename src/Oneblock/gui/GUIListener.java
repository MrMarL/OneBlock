package Oneblock.gui;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GUIListener implements Listener {
  	 
    @EventHandler
    public void onPlayerClickInventory(InventoryClickEvent e){
    	if (!GUI.enabled)
    		return;
        Inventory inv = e.getClickedInventory();
        if (inv == null)
        	return;
        if (GUI.baseGUI != null && GUI.baseGUI.equals(inv)) {
	        HumanEntity he = e.getWhoClicked();
	        if (!(he instanceof Player))
	        	return;
	        Player pl = (Player) he;
	        
	        ItemStack item = e.getCurrentItem();
	        if (item == null)
	        	return;
	        String command = item.getItemMeta().getDisplayName().split("/")[1];
	        pl.closeInventory();
	        pl.performCommand(command);
	        e.setCancelled(true);
        }
        else if (GUI.acceptGUI != null && GUI.acceptGUI.equals(inv)) {
        	HumanEntity he = e.getWhoClicked();
	        Player pl = (Player) he;
	        
	        pl.closeInventory();
	        if (e.getRawSlot() == 2)
	        	pl.performCommand("ob accept");
	        e.setCancelled(true);
        }
        else if (GUI.topGUI != null && GUI.topGUI.equals(inv)) 
	        e.setCancelled(true);
    }
}