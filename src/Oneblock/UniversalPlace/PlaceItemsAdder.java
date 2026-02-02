package Oneblock.UniversalPlace;

import org.bukkit.Material;
import org.bukkit.block.Block;

import dev.lone.itemsadder.api.CustomBlock;

public class PlaceItemsAdder extends Place{

	@Override
	public boolean setType(Block block, Object material_, boolean physics) {
		if (material_ instanceof  Material) 
			block.setType((Material)material_, physics);
		else if (material_ instanceof CustomBlock) {
			((CustomBlock)material_).place(block.getLocation());
			return true;
		}
		else if (material_ instanceof String) 
			return setCustomType(block, (String)material_);

		return false;
	}
}