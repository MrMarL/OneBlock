package oneblock.invitation;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import oneblock.CommandHandler;
import oneblock.Oneblock;
import oneblock.PlayerInfo;

public class Invitation extends AbstractInvitation {
	/**
	 * Pending invites awaiting {@code /ob accept}. Phase 4.2 swapped the
	 * previous {@code ArrayList} for a {@link CopyOnWriteArrayList} so that
	 * the {@link #add(UUID, UUID)} duplicate scan, the {@link #check(UUID)}
	 * and {@link #check(org.bukkit.entity.Player)} lookups, and the trailing
	 * {@code list.remove(inv_)} (delivered as a {@code runTaskLater}
	 * lambda 300 ticks after creation) no longer share an internal
	 * iterator with any concurrent mutator. All current writers remain on
	 * the main thread; this is the same forward-looking guard described
	 * in {@link Guest#list}.
	 */
	public static final List<Invitation> list = new CopyOnWriteArrayList<>();
	
	public Invitation(UUID inviting, UUID invited) {
		super(inviting, invited);
	}

	public static void add(UUID name, UUID to) {
    	for(Invitation item: Invitation.list) 
			if (item.equals(name, to))
				return;
    	Invitation inv_ = new Invitation(name, to);
    	Invitation.list.add(inv_);
    	Bukkit.getScheduler().runTaskLater(Oneblock.plugin, 
    			() -> { Invitation.list.remove(inv_); }, 300L);
    }
	
	public static Invitation check(UUID uuid) {
		for(Invitation item: list)
			if (item.Invited.equals(uuid))
				return item;
		return null;
	}
	
	public static boolean check(Player pl) {
		if (pl == null) return false; 
		UUID uuid = pl.getUniqueId();
		Invitation inv_ = check(uuid);
		if (inv_ == null) return false; 
		if (PlayerInfo.getId(inv_.Inviting) == -1) return false;
		
		CommandHandler.idresetCommand(pl);
		
		PlayerInfo.get(inv_.Inviting).addInvite(uuid);
		pl.performCommand("ob j");
		list.remove(inv_);
		return true; 
    }
}