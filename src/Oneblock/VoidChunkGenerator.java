package Oneblock;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

@SuppressWarnings("deprecation")
public class VoidChunkGenerator extends ChunkGenerator {
	private ChunkData nullData = null;

	@Override
	public List<BlockPopulator> getDefaultPopulators(World world) {
		return Collections.<BlockPopulator>emptyList();
	}

	@Override
	public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome) {
		if (nullData != null)
			return nullData;
		return nullData = super.createChunkData(world);
	}
}