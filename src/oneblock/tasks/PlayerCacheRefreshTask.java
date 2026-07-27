package oneblock.tasks;

import org.bukkit.World;

import oneblock.OneBlock;

/**
 * Async-scheduled refresh of {@link oneblock.PlayerCache} contents from
 * the live online-player snapshot of the island world. Runs every six
 * seconds (120 ticks) so the slower per-tick block-gen and particle
 * runners can iterate the cache instead of re-querying Bukkit each time.
 */
public final class PlayerCacheRefreshTask implements Runnable {
    private final OneBlock plugin;

    public PlayerCacheRefreshTask(OneBlock plugin) { this.plugin = plugin; }

    @Override
    public void run() {
        World w = OneBlock.getWorld();
        if (w != null) plugin.cache.updateCache(w.getPlayers());
    }
}
