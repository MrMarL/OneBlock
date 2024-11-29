package Oneblock.UniversalPlace;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;

import Oneblock.ChestItems;

public abstract class Place {
	
	public enum Type
	{
	    legacy,
	    basic,
	    ItemsAdder,
	    Oraxen,
	    Nexo
	}
	
	public static Place GetPlacerByType(Type type) {
		switch (type) {
		case legacy:
			return new Place1_8to1_12();
		case ItemsAdder:
			return new PlaceItemsAdder();
		case Oraxen:
			return new PlaceOraxen();
		case Nexo:
			return new PlaceNexo();
		default:
			return new Place1_13plus();
		}
	}
	
	public abstract boolean setType(Block block, Object material_, boolean physics);
	
	public boolean setCustomType(Block block, String command) {
		if (command.charAt(0) == '/') {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
	    			String.format(command.replaceFirst("/", ""), block.getX(), block.getY(), block.getZ()));
			return true;
		}
    	
		block.setType(Material.CHEST);
        Chest chest = (Chest) block.getState();
        Inventory inv = chest.getInventory();
        ChestItems.fillChest(inv, command);
    	return true;
    }
}