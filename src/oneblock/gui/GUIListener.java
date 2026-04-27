package oneblock.gui;

import java.util.ArrayList;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import oneblock.ChestItems;

public class GUIListener implements Listener {

	@EventHandler
    public void onPlayerClickInventory(final InventoryClickEvent e){
        Inventory inv = e.getInventory();
        InventoryHolder _holder = inv.getHolder();
        if (_holder == null) return;
        if (!(_holder instanceof GUIHolder)) return;
        
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
		        String[] parts = command.split("/", 2);
		        if (parts.length > 1) 
		            pl.performCommand(parts[1]);
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
	
	@EventHandler
	public void onPlayerCloseInventory(final InventoryCloseEvent e) {
	    Inventory inv = e.getInventory();
	    InventoryHolder _holder = inv.getHolder();
	    if (_holder == null) return;
	    if (!(_holder instanceof ChestHolder)) return;

	    String type = ((ChestHolder)_holder).getType();
	    
	    ArrayList<ItemStack> newList = new ArrayList<>();
	    for (ItemStack itm : inv.getContents()) 
	        if (itm != null) 
	            newList.add(itm);
	    
	    ChestItems.setItems(type, newList);
	    ChestItems.save();
	}
}