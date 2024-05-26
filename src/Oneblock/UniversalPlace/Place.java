package Oneblock.UniversalPlace;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;

public abstract class Place {
	public abstract boolean setType(Block block, Object material_, boolean physics);
	
	public boolean setCustomType(Block block, String command) {
    	Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
    			String.format(command, block.getX(), block.getY(), block.getZ()));
    	return true;
    }
}