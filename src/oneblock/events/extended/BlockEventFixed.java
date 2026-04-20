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
		if (loc.getBlockY() != y) return;
		if ((x - loc.getBlockX()) % offset != 0) return;
		if ((z - loc.getBlockZ()) % offset != 0) return;
		
		e.setCancelled(true);
		
		loc.add(0, DROP_TELEPORT_HEIGHT_OFFSET, 0);
		drop.copy(loc).setVelocity(UPWARD_VELOCITY);
    }
}