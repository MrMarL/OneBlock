package Oneblock.Invitation;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import Oneblock.Oneblock;
import Oneblock.PlayerInfo;

public class Invitation extends AbstractInvitation {
	public static ArrayList <Invitation> list = new ArrayList<>();
	
	public Invitation(UUID inviting, UUID invited) {
		super(inviting, invited);
	}

	public static void add(UUID name, UUID to) {
    	for(Invitation item: Invitation.list) 
			if (item.equals(name, to))
				return;
    	Invitation inv_ = new Invitation(name, to);
    	Invitation.list.add(inv_);
    	Bukkit.getScheduler().runTaskLaterAsynchronously(Oneblock.plugin, 
    			() -> { Invitation.list.remove(inv_); }, 300L);
    }
	
	public static Invitation check(UUID uuid) {
		for(Invitation item: list)
			if (item.Invited.equals(uuid))
				return item;
		return null;
	}
	
	public static boolean check(Player pl) {
		UUID uuid = pl.getUniqueId();
		Invitation inv_ = check(uuid);
		if (inv_ == null) return false; 
		if (!PlayerInfo.ExistId(inv_.Inviting)) return false;
			
		if (PlayerInfo.ExistId(uuid)) {
			if (Oneblock.plugin.Progress_bar)
				PlayerInfo.get(uuid).bar.removePlayer(pl);
			pl.performCommand("ob idreset /n");
		}
		PlayerInfo.get(inv_.Inviting).uuids.add(uuid);
		pl.performCommand("ob j"); 
		list.remove(inv_);
		return true; 
    }
}