package Oneblock.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class ChestHolder implements InventoryHolder {
	String type;
	
	public ChestHolder(String name) {
		type = name;
	}

	@Override
	public Inventory getInventory() {
		return null;
	}
}
