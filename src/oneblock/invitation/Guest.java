package oneblock.invitation;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import oneblock.PlayerInfo;

public class Guest extends AbstractInvitation {
	/**
	 * Active guest visits. Phase 4.2 swapped the previous {@code ArrayList}
	 * for a {@link CopyOnWriteArrayList} so that the {@code IslandBlockGenTask}
	 * iteration ({@link #check(UUID)} via {@link #getPlayerInfo(UUID)}) and a
	 * concurrent {@code Guest.list.add} from a future off-thread caller (or
	 * the in-loop {@link #remove(UUID)} call) cannot trigger a
	 * {@link java.util.ConcurrentModificationException}. All current writers
	 * remain on the main thread; this is a forward-looking guard documented
	 * by {@code CONTRIBUTING.md} as a tracked race before Phase 4.
	 */
	public static final List<Guest> list = new CopyOnWriteArrayList<>();
	
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