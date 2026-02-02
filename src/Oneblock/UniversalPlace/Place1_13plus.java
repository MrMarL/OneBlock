package Oneblock.UniversalPlace;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class Place1_13plus extends Place{

	@Override
	public boolean setType(Block block, Object material_, boolean physics) {
		if (material_ instanceof Material) 
			block.setType((Material)material_, physics);
		else if (material_ instanceof String) 
			return setCustomType(block, (String)material_);

		return false;
	}
}