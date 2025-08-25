package Oneblock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.FileConfiguration;

import com.cryptomorin.xseries.XBlock;
import com.cryptomorin.xseries.XMaterial;

public class Island {
	public static BlockData[][][] custom = null;
	
	public static HashMap<String, List<String>> map() {
		if (custom == null)
            return null;
		
		HashMap <String, List <String>> map = new HashMap <String, List <String>>();
        for (int yy = 0; yy < 5; yy++) {
        	List <String> y_now = new ArrayList <String>();
            for (int xx = 0; xx < 7; xx++)
                for (int zz = 0; zz < 7; zz++)
                	y_now.add(custom[xx][yy][zz].getAsString());
            map.put(String.format("y%d", yy-1), y_now);
        }
        return map;
	}
	
	public static void read(FileConfiguration config) {
		custom = new BlockData[7][5][7];
    	BlockData airData = Material.AIR.createBlockData();
    	for (int yy = 0; yy < 5; yy++) {
    		String check = String.format("custom_island.y%d", yy-1);
    		if (!config.isList(check)) {
    			for (int xx = 0; xx < 7; xx++)
	                for (int zz = 0; zz < 7; zz++)
	                	custom[xx][yy][zz] = airData;
    			continue;
    		}
        	List<String> cust_s = config.getStringList(check);
            for (int xx = 0; xx < 7; xx++)
                for (int zz = 0; zz < 7; zz++)
                	Island.custom[xx][yy][zz] = Bukkit.createBlockData(cust_s.get(7*xx+zz));
    	}
	}
	
	public static void scan(World wor, int x, int y, int z) {
		if (custom == null)
			custom = new BlockData[7][5][7];
        for (int xx = 0; xx < 7; xx++)
            for (int yy = 0; yy < 5; yy++)
                for (int zz = 0; zz < 7; zz++)
                	custom[xx][yy][zz] = wor.getBlockAt(x + xx - 3, y + yy - 1, z - 3 + zz).getBlockData();
	}
	
	public static void clear(World wor, int x, int y, int z, int radius) {
		x -= radius; y -= 12; z -= radius;
		radius*=2;
        if (y <= 1)
            y = 1;
        for (int xx = 0; xx < radius; xx++)
            for (int yy = 0; yy < 24; yy++)
                for (int zz = 0; zz < radius; zz++) {
                	Block bl = wor.getBlockAt(x + xx, y + yy, z + zz);
                	if (bl.getType().equals(Material.AIR))
						continue;
                	bl.setType(Material.AIR);
                }
	}
	
	public static void place(World wor, int x, int y, int z) {
		if (custom == null) {
			for (int i = -2; i <= 2; i++)
				for (int q = -2; q <= 2; q++)
					if (Math.abs(i) + Math.abs(q) < 3)
						XBlock.setType(wor.getBlockAt(x + i, y, z + q), XMaterial.GRASS_BLOCK);
			return;
		}

		for (int xx = 0; xx < 7; xx++)
			for (int yy = 0; yy < 5; yy++)
				for (int zz = 0; zz < 7; zz++) {
					if (custom[xx][yy][zz].getMaterial().equals(Material.AIR))
						continue;
					wor.getBlockAt(x - 3 + xx, y + yy - 1, z - 3 + zz).setBlockData(custom[xx][yy][zz]);
				}
	}
}