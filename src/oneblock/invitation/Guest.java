package oneblock.invitation;

import java.util.ArrayList;
import java.util.UUID;

import oneblock.PlayerInfo;

public class Guest extends AbstractInvitation {
	public static ArrayList <Guest> list = new ArrayList<>();
	
	public Guest(UUID inviting, UUID invited) {
		super(inviting, invited);
	}
	
	public static Guest check(UUID uuid) {
		for(Guest item: list)
			if (item.Invited.equals(uuid))
				return item;
		return null;
	}
	
	public static PlayerInfo getPlayerInfo(UUID uuid) {
		Guest g = check(uuid);
		if (g == null) return null;
		if (PlayerInfo.getId(g.Inviting) == -1) return null;
		return PlayerInfo.get(g.Inviting);
	}
	
	public static boolean remove(UUID uuid) {
		Guest r = null;
		for (Guest g : list)
			if (g.Invited.equals(uuid)) { r = g; break; }
		return list.remove(r);
	}
}