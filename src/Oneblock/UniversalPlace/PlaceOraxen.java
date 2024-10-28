package Oneblock.UniversalPlace;

import org.bukkit.Material;
import org.bukkit.block.Block;

import io.th0rgal.oraxen.api.OraxenBlocks;
import io.th0rgal.oraxen.api.OraxenItems;

public class PlaceOraxen extends Place{

	@Override
	public boolean setType(Block block, Object material_, boolean physics) {
		Class<?> matClass = material_.getClass();
    	
		if (matClass == Material.class) 
			block.setType((Material)material_, physics);
		else if (matClass == String.class) {
			String material = (String)material_;
			if (OraxenItems.exists(material)) {
				OraxenBlocks.place(material, block.getLocation());
				return true;
			}
			return setCustomType(block, (String)material_);
		}

		return false;
	}
}