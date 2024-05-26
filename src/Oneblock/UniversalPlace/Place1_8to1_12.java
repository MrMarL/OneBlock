package Oneblock.UniversalPlace;

import org.bukkit.Material;
import org.bukkit.block.Block;

import com.cryptomorin.xseries.XBlock;
import com.cryptomorin.xseries.XMaterial;

public class Place1_8to1_12 extends Place{

	@Override
	public boolean setType(Block block, Object material_, boolean physics) {
		Class<?> matClass = material_.getClass();
    	
		if (matClass == XMaterial.class)
			XBlock.setType(block, (XMaterial)material_, physics);
		else if (matClass == Material.class)
			block.setType((Material)material_, physics);
		else if (matClass == String.class)
			return setCustomType(block, (String)material_);

		return false;
	}
}