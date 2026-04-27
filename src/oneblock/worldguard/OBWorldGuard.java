package oneblock.worldguard;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.util.Vector;

import oneblock.Oneblock;
import oneblock.PlayerInfo;

public class OBWorldGuard {
	public static final boolean canUse = false;
	public static final String regionName = "OB_WG_%d";

	public static List<String> flags = new ArrayList<>();

	private static boolean enabled = canUse;
	
	public static boolean isEnabled() {
	    return enabled;
	}

	public static void setEnabled(boolean value) {
		if (!canUse) return;
		enabled = value;
	}
	
	public void recreateRegions() {
		if (!enabled) return;
		
		int maxId = PlayerInfo.size();
		removeRegions(maxId);
    	
		for (int i = 0; i < maxId; i++) {
			PlayerInfo owner = PlayerInfo.get(i);
			if (owner.uuid == null) continue;
			
			int pos[] = Oneblock.plugin.getIslandCoordinates(i);
			createRegionInternal(owner.uuid, pos[0], pos[1], Oneblock.getOffset(), i);
			for (UUID member: owner.uuids) 
				addMember(member, i);
		}
	}
	
	public boolean createRegion(UUID pl, int x, int z, int offset, int id) {
		if (!enabled) return false;
		return createRegionInternal(pl, x, z, offset, id);
	}
	
	private boolean createRegionInternal(UUID pl, int x, int z, int offset, int id) {
		int radius = (offset + (offset & 1)) / 2;
		
		Vector Block1 = new Vector(x - radius + 1, 0, z - radius + 1);
		Vector Block2 = new Vector(x + radius - 1, 255, z + radius - 1);
    	
		return createRegion(pl, Block1, Block2, id);
	}
	
	public boolean createRegion(UUID pl, Vector coord1, Vector coord2, int id) {return true;}
	
	public boolean addMember(UUID pl, int id) {return true;}
	
	public boolean removeMember(UUID pl, int id) {return true;}
	
	public boolean removeRegions(int id) {return true;}
}
