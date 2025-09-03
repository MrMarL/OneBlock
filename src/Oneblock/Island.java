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

import Oneblock.Utils.Utils;

public class Island {
    public static BlockData[][][] custom = null;
    private static final int SIZE = 7;
    private static final int HEIGHT = 12;
    private static final int OFFSET_Y = 5;
    private static final BlockData AIR_DATA = Material.AIR.createBlockData();

    public static HashMap<String, List<String>> map() {
        if (custom == null) return null;
        
        HashMap<String, List<String>> map = new HashMap<>();
        for (int y = 0; y < HEIGHT; y++) {
            if (isLayerEmpty(y)) continue; // Skipping the empty layers
            
            List<String> yData = new ArrayList<>();
            for (int x = 0; x < SIZE; x++) {
                for (int z = 0; z < SIZE; z++) {
                    yData.add(custom[x][y][z].getAsString());
                }
            }
            map.put(String.format("y%d", y - OFFSET_Y), yData);
        }
        return map;
    }

    public static void read(FileConfiguration config) {
        custom = new BlockData[SIZE][HEIGHT][SIZE];
        
        // Initializing the array with air
        for (int y = 0; y < HEIGHT; y++) {
            fillLayer(y, AIR_DATA);
        }
        
        for (int y = 0; y < HEIGHT; y++) {
            String configKey = String.format("custom_island.y%d", y - OFFSET_Y);
            
            if (config.isList(configKey)) {
                List<String> blockDataStrings = config.getStringList(configKey);
                for (int x = 0; x < SIZE; x++) {
                    for (int z = 0; z < SIZE; z++) {
                        custom[x][y][z] = Bukkit.createBlockData(blockDataStrings.get(SIZE * x + z));
                    }
                }
            }
        }
    }

    public static void scan(World world, int x, int y, int z) {
        custom = new BlockData[SIZE][HEIGHT][SIZE];
        
        for (int xx = 0; xx < SIZE; xx++) {
            for (int yy = 0; yy < HEIGHT; yy++) {
                for (int zz = 0; zz < SIZE; zz++) {
                    Block block = world.getBlockAt(x + xx - 3, y + yy - OFFSET_Y, z - 3 + zz);
                    custom[xx][yy][zz] = block.getBlockData();
                }
            }
        }
    }

    public static void clear(World world, int x, int y, int z, int radius) {
        int startX = x - radius;
        int startY = Math.max(y - 12, Utils.getWorldMinHeight(world) + 1);
        int startZ = z - radius;
        int diameter = radius * 2;

        for (int xx = 0; xx < diameter; xx++) {
            for (int yy = 0; yy < 24; yy++) {
                for (int zz = 0; zz < diameter; zz++) {
                    Block block = world.getBlockAt(startX + xx, startY + yy, startZ + zz);
                    if (!block.getType().equals(Material.AIR)) {
                        block.setType(Material.AIR);
                    }
                }
            }
        }
    }

    public static void place(World world, int x, int y, int z) {
        if (custom == null) {
            createDefaultIsland(world, x, y, z);
            return;
        }

        for (int xx = 0; xx < SIZE; xx++) {
            for (int yy = 0; yy < HEIGHT; yy++) {
                for (int zz = 0; zz < SIZE; zz++) {
                    BlockData blockData = custom[xx][yy][zz];
                    if (!blockData.getMaterial().equals(Material.AIR)) {
                        Block block = world.getBlockAt(x - 3 + xx, y + yy - OFFSET_Y, z - 3 + zz);
                        block.setBlockData(blockData);
                    }
                }
            }
        }
    }

    private static void fillLayer(int y, BlockData blockData) {
        for (int x = 0; x < SIZE; x++) {
            for (int z = 0; z < SIZE; z++) {
                custom[x][y][z] = blockData;
            }
        }
    }

    private static boolean isLayerEmpty(int y) {
        for (int x = 0; x < SIZE; x++) {
            for (int z = 0; z < SIZE; z++) {
                if (!custom[x][y][z].getMaterial().equals(Material.AIR)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static void createDefaultIsland(World world, int x, int y, int z) {
        for (int i = -2; i <= 2; i++) {
            for (int q = -2; q <= 2; q++) {
                if (Math.abs(i) + Math.abs(q) < 3) {
                    XBlock.setType(world.getBlockAt(x + i, y, z + q), XMaterial.GRASS_BLOCK);
                }
            }
        }
    }
}