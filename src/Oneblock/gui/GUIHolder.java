package Oneblock.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class GUIHolder implements InventoryHolder {
    
    public enum GUIType {
        MAIN_MENU,
        TOP,
        PLAYER_STATS,
        INVITE,
        VISIT
    }
    
    private final GUIType guiType;
    
    public GUIHolder(GUIType guiType) {
        this.guiType = guiType;
    }
    
    @Override
    public Inventory getInventory() {
        return null;
    }
    
    public GUIType getGuiType() {
        return guiType;
    }
}