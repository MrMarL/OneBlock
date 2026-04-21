package oneblock.events.extended;

import static oneblock.Oneblock.*;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ItemSpawnEvent;

import oneblock.events.BlockEvent;

public class BlockEventFixed extends BlockEvent {
	@EventHandler
	public void ItemStackSpawn(final ItemSpawnEvent e) {
		if (!droptossup) return;
		World world = getWorld();
		if (world == null) return;
        
		Entity drop = e.getEntity();
		Location loc = drop.getLocation();
		
		if (!world.equals(loc.getWorld())) return;
		if (loc.getBlockY() != getY()) return;
		if ((getX() - loc.getBlockX()) % getOffset() != 0) return;
		if ((getZ() - loc.getBlockZ()) % getOffset() != 0) return;
		
		loc.add(0, DROP_TELEPORT_HEIGHT_OFFSET, 0);

		// 1.21+ reworked item spawning so the old `teleport` path drops silently
		// lose their Z-axis velocity. Use the new copy() + setVelocity() API
		// on modern servers.
		e.setCancelled(true);
		drop.copy(loc).setVelocity(UPWARD_VELOCITY);
    }
}