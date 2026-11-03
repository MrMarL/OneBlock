package oneblock.utils.schedulers;

import java.util.Collection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/** Vanilla Paper/Bukkit strategy: everything runs on the caller thread or via BukkitScheduler. */
public class BukkitScheduler {
	protected final Plugin plugin;

	public BukkitScheduler(Plugin plugin) {
		this.plugin = plugin;
	}

	public void run(Runnable task)                    			{ task.run(); }
	public void runLater(Runnable task, long d)       			{ Bukkit.getScheduler().runTaskLater(plugin, task, d); }
	public void runTimer(Runnable task, long d, long p) 		{ Bukkit.getScheduler().runTaskTimer(plugin, task, d, p); }
	public void runTimerAsync(Runnable task, long d, long p) 	{ Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task, d, p); }
	public void runAt(Location loc, Runnable task)   			{ task.run(); }
	public void runAtLater(Location loc, Runnable t, long d) 	{ Bukkit.getScheduler().runTaskLater(plugin, t, d); }
	public void runFor(Entity e, Runnable task)       			{ task.run(); }
	public void runForLater(Entity e, Runnable t, long d) 		{ Bukkit.getScheduler().runTaskLater(plugin, t, d); }
	public void teleport(Entity e, Location loc)      			{ e.teleport(loc); }
	public void cancelTasks()                         			{ Bukkit.getScheduler().cancelTasks(plugin); }
	public Collection<? extends Player> playersIn(World w)	 	{ return w.getPlayers(); }
}