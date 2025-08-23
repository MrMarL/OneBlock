package Oneblock.gui;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GUIListener implements Listener {

	@EventHandler
    public void onPlayerClickInventory(final InventoryClickEvent e){
        Inventory inv = e.getInventory();
        InventoryHolder _holder = inv.getHolder();
        if (_holder == null) return;
        if (!_holder.getClass().isAssignableFrom(GUIHolder.class)) return;
        
        e.setCancelled(true);
        GUIHolder holder = (GUIHolder)_holder;
        
        HumanEntity he = e.getWhoClicked();
	    if (!(he instanceof Player)) return;
	    Player pl = (Player) he;
		
		switch (holder.getGuiType()) {
		    case MAIN_MENU:
		        if (e.getClickedInventory() != inv) return;
		        ItemStack item = e.getCurrentItem();
		        if (item == null) return;
		        pl.closeInventory();
		        ItemMeta meta = item.getItemMeta();
		        if (meta == null) return;
		        String command = meta.getDisplayName();
		        if (command.contains("/"))
		            pl.performCommand(command.split("/")[1]);
		        return;
		        
		    case INVITE:
		        pl.closeInventory();
		        if (e.getRawSlot() == 2)
		            pl.performCommand("ob accept");
		        return;
		        
		    case VISIT:
		        if (e.getClickedInventory() != inv) return;
		        item = e.getCurrentItem();
		        if (item == null) return;
		        pl.closeInventory();
		        meta = item.getItemMeta();
		        if (meta == null) return;
		        command = meta.getDisplayName();
		        pl.performCommand("ob visit " + command);
		        return;
		        
		    default: break;
		}
    }
}