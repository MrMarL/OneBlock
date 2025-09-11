package Oneblock.Events;

import static Oneblock.Oneblock.*;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.util.Vector;

import Oneblock.PlayerInfo;

public class BlockEvent implements Listener {
	private static final double DROP_TELEPORT_HEIGHT_OFFSET = 0.8;
	
	@EventHandler(ignoreCancelled = true)
	public void ItemStackSpawn(final EntitySpawnEvent e) {
		if (!droptossup) return;
		World world = getWorld();
		if (world == null) return;
		if (!EntityType.DROPPED_ITEM.equals(e.getEntityType())) return;
            
		Location loc = e.getLocation();
		if (!world.equals(loc.getWorld())) return;
		if (loc.getBlockY() != y) return;
		if ((x - loc.getBlockX()) % offset != 0) return;
		if ((z - loc.getBlockZ()) % offset != 0) return;

		Entity drop = e.getEntity();
		drop.teleport(loc.add(0, DROP_TELEPORT_HEIGHT_OFFSET, 0));
		drop.setVelocity(new Vector(0, .1, 0));
    }
	@EventHandler
	public void BlockBreak(final BlockBreakEvent e) {
		World world = getWorld();
		if (world == null) return;
		final Block block = e.getBlock();
		if (block.getWorld() != world) return;
		if (block.getY() != y) return;
		final Player ponl = e.getPlayer();
		final UUID uuid = ponl.getUniqueId();
		final int plID = PlayerInfo.GetId(uuid);
		if (plID == -1) return;
		final int result[] = plugin.getIslandCoordinates(plID);
		if (block.getX() != result[0]) return;
		if (block.getZ() != result[1]) return;

		Bukkit.getScheduler().runTaskLater(plugin, () -> { plugin.BlockGen(result[0], result[1], plID, ponl, block); }, 1L);
	}
}
