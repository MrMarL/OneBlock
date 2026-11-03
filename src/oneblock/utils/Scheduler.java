package oneblock.utils;

import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import io.papermc.paper.ServerBuildInfo;
import oneblock.utils.schedulers.BukkitScheduler;
import oneblock.utils.schedulers.FoliaScheduler;

/**
 * Bukkit / Folia scheduling bridge.
 *
 * <p>The concrete strategy is chosen once in {@link #init(Plugin)}:
 * {@link BukkitScheduler} for vanilla Paper/Bukkit, {@link FoliaScheduler}
 * when running on Folia. All public methods stay static and simply delegate.
 */
public final class Scheduler {
	private static BukkitScheduler impl;

	public static void init(Plugin pl) {
		boolean folia = false;
		try {
			folia = ServerBuildInfo.buildInfo().isBrandCompatible(net.kyori.adventure.key.Key.key("papermc", "folia"));
		} catch(Exception | NoClassDefFoundError e) {}
		impl = folia ? new FoliaScheduler(pl) : new BukkitScheduler(pl);
	}
	
	public static boolean isFolia() { return impl instanceof FoliaScheduler; }

	public static void run(Runnable task)                          { impl.run(task); }
	public static void runLater(Runnable task, long delay)         { impl.runLater(task, delay); }
	public static void runTimer(Runnable task, long d, long p)     { impl.runTimer(task, d, p); }
	public static void runTimerAsync(Runnable task, long d, long p){ impl.runTimerAsync(task, d, p); }
	public static void runAt(Location loc, Runnable task)          { impl.runAt(loc, task); }
	public static void runAtLater(Location loc, Runnable t, long d){ impl.runAtLater(loc, t, d); }
	public static void runFor(Entity e, Runnable task)             { impl.runFor(e, task); }
	public static void runForLater(Entity e, Runnable t, long d)   { impl.runForLater(e, t, d); }
	public static void teleport(Entity e, Location loc)            { impl.teleport(e, loc); }
	public static void cancelTasks()                               { impl.cancelTasks(); }
	public static Collection<? extends Player> playersIn(World w)  { return impl.playersIn(w); }
}