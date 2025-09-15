package Oneblock.Events;

import static Oneblock.Oneblock.*;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import Oneblock.PlayerInfo;

public class RespawnJoinEvent implements Listener {
	@EventHandler(priority = EventPriority.HIGHEST)
    public void Respawn(final PlayerRespawnEvent e) {
		if (!rebirth) return;
		Player pl = e.getPlayer();
		World world = getWorld();
		if (!pl.getWorld().equals(world)) return;
		int plID = PlayerInfo.GetId(pl.getUniqueId());
		if (plID == -1) return;
		
		int result[] = plugin.getIslandCoordinates(plID);
		Location loc = new Location(world, result[0] + .5, y + 1.75, result[1] + .5);
		e.setRespawnLocation(loc);
    }
    @EventHandler
    public void AutoJoin(final PlayerTeleportEvent e) {
		if (!autojoin)
			return;
		Location loc = e.getTo();
		World from = e.getFrom().getWorld();
		World to = loc.getWorld();
		World ob = getWorld();
		if (!from.equals(ob) && to.equals(ob) && loc.getY() != y + 1.2013) {
			e.setCancelled(true);
			e.getPlayer().performCommand("ob j");
		}
    }
    @EventHandler
    public void JoinAuto(final PlayerJoinEvent e) {
		Player pl = e.getPlayer();
		if (pl.getWorld().equals(getWorld())) {
			if (autojoin) pl.performCommand("ob j");
			if (Border) {
				plugin.UpdateBorderLocation(pl, pl.getLocation());
				plugin.UpdateBorder(pl);
			}
		}
	}
}
