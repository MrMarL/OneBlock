package Oneblock.UniversalPlace;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class Place1_13plus extends Place{

	@Override
	public boolean setType(Block block, Object material_, boolean physics) {
		Class<?> matClass = material_.getClass();
    	
		if (matClass == Material.class) 
			block.setType((Material)material_, physics);
		else if (matClass == String.class) 
			return setCustomType(block, (String)material_);

		return false;
	}
}