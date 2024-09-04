package Oneblock.UniversalPlace;

import org.bukkit.Material;
import org.bukkit.block.Block;

import dev.lone.itemsadder.api.CustomBlock;

public class PlaceItemsAdder extends Place{

	@Override
	public boolean setType(Block block, Object material_, boolean physics) {
		Class<?> matClass = material_.getClass();
    	
		if (matClass == Material.class) 
			block.setType((Material)material_, physics);
		else if (matClass == CustomBlock.class) {
			((CustomBlock)material_).place(block.getLocation());
			return true;
		}
		else if (matClass == String.class) 
			return setCustomType(block, (String)material_);

		return false;
	}
}