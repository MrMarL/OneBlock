package oneblock.tasks;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import oneblock.Messages;
import oneblock.OneBlock;
import oneblock.PlayerInfo;
import oneblock.invitation.Guest;

/**
 * Main-thread block-generation pulse: every four seconds (80 ticks) it
 * walks every online island player and, if the generation block is air,
 * delegates to {@link OneBlock#generateBlock} to (re)materialise it. Also
 * enforces the {@code protection} flag - players outside their cell are
 * teleported back via {@code /ob j} after a {@link Messages#protection}
 * message.
 */
public final class IslandBlockGenTask implements Runnable {
    private final OneBlock plugin;

    public IslandBlockGenTask(OneBlock plugin) { this.plugin = plugin; }

    @Override
    public void run() { // SubBlockGen
        for (Player player : plugin.cache.getPlayers()) {
        	final World wor = OneBlock.getWor();
        	if (player.getWorld() != wor) continue;
        	final UUID uuid = player.getUniqueId();
        	final int result[] = plugin.cache.getIslandCoordinates(player);
            final int X_pl = result[0], Z_pl = result[1], plID = result[2];
        	
            if (OneBlock.protection && !player.hasPermission("Oneblock.ignoreBarrier")) {
            	boolean CheckGuest = false;
            	Location loc = player.getLocation();
        		PlayerInfo inf = Guest.getPlayerInfo(uuid);
        		if (inf != null) {
                	int crd[] = plugin.getIslandCoordinates(PlayerInfo.getId(inf.uuid));
                    CheckGuest = plugin.isWithinIslandBounds(loc, crd[0], crd[1]);
                    if (!CheckGuest) Guest.remove(uuid);
        		}
        		if (!plugin.isWithinIslandBounds(loc, X_pl, Z_pl) && !CheckGuest) {
        			player.performCommand("ob j");
        			player.sendMessage(Messages.protection);
                	continue;
                }
            }
            
            final Block block = wor.getBlockAt(X_pl, OneBlock.getY(), Z_pl);
            if (block.getType() != Material.AIR) continue;
            if (PlayerInfo.getId(uuid) == -1) continue;
            
            plugin.BlockGen(X_pl, Z_pl, plID, player, block);
        }
    }
}
