package oneblock.tasks;

import oneblock.IslandOrigin;
import oneblock.OneBlock;
import org.bukkit.Bukkit;
import org.bukkit.World;

/**
 * Async-scheduled poll that resolves the configured island {@code world} once Bukkit has finished
 * loading worlds. On success it folds the resolved {@link World} into {@link OneBlock#origin()}
 * (preserving the already-loaded {@code x/y/z/offset}), kicks off the four steady-state runners via
 * {@link OneBlock#runMainTask()} and triggers a {@link OneBlock#reload()} so all dependent caches
 * see the new world.
 */
public final class WorldInitTask implements Runnable {
  private final OneBlock plugin;

  public WorldInitTask(OneBlock plugin) {
    this.plugin = plugin;
  }

  @Override
  public void run() {
      if (OneBlock.getWor() != null) return;
      final World w = Bukkit.getWorld(OneBlock.config.getString("world"));
      
      if (w != null) {
    	  OneBlock.ORIGIN.updateAndGet(prev -> new IslandOrigin(w, prev.x(), prev.y(), prev.z(), prev.offset()));
    	  OneBlock.leavewor = Bukkit.getWorld(OneBlock.config.getString("leaveworld"));
    	  plugin.getLogger().info("The initialization of the world was successful!");
    	  plugin.runMainTask();
    	  plugin.reload();
      } else {
    	  plugin.getLogger().info("Waiting for initialization of world '" + OneBlock.config.getString("world") + "'...");
      }
  }
}
