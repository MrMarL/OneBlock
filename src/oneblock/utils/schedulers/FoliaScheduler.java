package oneblock.utils.schedulers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/** Folia strategy: routes work to the correct region/entity/global scheduler. */
public class FoliaScheduler extends BukkitScheduler {
	private static final long MS_PER_TICK = 50L;

	public FoliaScheduler(Plugin plugin) {
		super(plugin);
	}

	@Override public void run(Runnable task) 							{ Bukkit.getGlobalRegionScheduler().run(plugin, st -> task.run()); }
	@Override public void runLater(Runnable task, long d) 				{ Bukkit.getGlobalRegionScheduler().runDelayed(plugin, st -> task.run(), d); }
	@Override public void runTimer(Runnable task, long d, long p) 		{ Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, st -> task.run(), Math.max(1, d), p); }
	@Override public void runTimerAsync(Runnable task, long d, long p) 	{ Bukkit.getAsyncScheduler().runAtFixedRate( plugin, st -> task.run(), d * MS_PER_TICK, p * MS_PER_TICK, TimeUnit.MILLISECONDS); }
	@Override public void runAt(Location loc, Runnable task) 			{ Bukkit.getRegionScheduler().run(plugin, loc, st -> task.run()); }
	@Override public void runAtLater(Location loc, Runnable t, long d) 	{ Bukkit.getRegionScheduler().runDelayed(plugin, loc, st -> t.run(), d); }
	@Override public void runFor(Entity e, Runnable task) 				{ e.getScheduler().run(plugin, st -> task.run(), null); }
	@Override public void runForLater(Entity e, Runnable t, long d) 	{ e.getScheduler().runDelayed(plugin, st -> t.run(), null, d); }
	@Override public void teleport(Entity e, Location loc) 				{ e.teleportAsync(loc); }
	
	@Override public void cancelTasks() {
		Bukkit.getGlobalRegionScheduler().cancelTasks(plugin);
		Bukkit.getAsyncScheduler().cancelTasks(plugin);
	}
	
	@Override public Collection<? extends Player> playersIn(World world) {
		List<Player> players = new ArrayList<>();
		for (Player p : Bukkit.getOnlinePlayers())
			if (world.equals(p.getWorld())) players.add(p);
		return players;
	}
}