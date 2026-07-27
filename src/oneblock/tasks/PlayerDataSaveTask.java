package oneblock.tasks;

import oneblock.OneBlock;

/**
 * Async-scheduled persistence pulse: every five minutes (6000 ticks) it
 * snapshots {@code PlayerInfo.list} and writes the snapshot through
 * either the configured database (HikariCP, thread-safe) or the
 * fallback {@link oneblock.storage.JsonPlayerDataStore}.
 */
public final class PlayerDataSaveTask implements Runnable {
    private final OneBlock plugin;

    public PlayerDataSaveTask(OneBlock plugin) { this.plugin = plugin; }

    @Override
    public void run() { plugin.saveData(); }
}
