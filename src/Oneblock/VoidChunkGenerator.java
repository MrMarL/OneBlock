package Oneblock;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

@SuppressWarnings("deprecation")
public class VoidChunkGenerator extends ChunkGenerator {
	public ChunkData generateChunks(World world) {
		return createChunkData(world);
	}

	@Override
    public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biomes) {
        ChunkData chunkData = createChunkData(world);

        for (int x = 0; x < 16; x++) 
            for (int z = 0; z < 16; z++) 
                biomes.setBiome(x, z, Biome.PLAINS);

        return chunkData;
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(final World world) {
        return Collections.emptyList();
    }
}