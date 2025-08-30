package Oneblock.Events;

import static Oneblock.Oneblock.*;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

public class TeleportNetherEvent implements Listener {
	@EventHandler
    public void NetherPortal(final PlayerPortalEvent e) {
    	if (allow_nether) return;
    	World from = e.getFrom().getWorld();
    	if (!from.equals(getWorld())) return;
    	
    	World to = e.getTo().getWorld();
        if (to.getEnvironment() == World.Environment.NETHER) 
        	e.setCancelled(true);
    }
}
