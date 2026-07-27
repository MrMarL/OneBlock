package oneblock.tasks;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

import oneblock.OneBlock;

public final class IslandParticleTask implements Runnable {
    private static final double[][] PARTICLE_OFFSETS = {{0, 0}, {0, 1}, {1, 0}, {1, 1}};
    private static final int PARTICLE_COUNT = 5;

    private final OneBlock plugin;

    public IslandParticleTask(OneBlock plugin) { this.plugin = plugin; }

    public void run() {
        if (!OneBlock.particle) return;
        
        World wor = OneBlock.getWor();

        for (Player ponl: plugin.cache.getPlayers()) {
            int[] result = plugin.cache.getIslandCoordinates(ponl);
            if (result == null) continue;
            int X_pl = result[0], Z_pl = result[1];
            double baseY = OneBlock.getY() + 0.5;
            

            for (double[] offset : PARTICLE_OFFSETS) {
                Location loc = new Location(wor, X_pl + offset[0], baseY, Z_pl + offset[1]);
                wor.spawnParticle(Particle.PORTAL, loc, PARTICLE_COUNT, 0, 0, 0, 0);
            }
        }
	}
}
