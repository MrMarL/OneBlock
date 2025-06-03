package Oneblock.WorldGuard;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.util.Vector;

public abstract class OBWorldGuard {
	public static final boolean canUse = false;
	public static final String regionName = "OB_WG_%d";
	public static List <String> flags = new ArrayList <>();
	
	public boolean CreateRegion(UUID pl, int x, int z, int offset, int id) {
		int radius = offset/2;
		
		Vector Block1 = new Vector(x - radius + 1, 0, z - radius + 1);
		Vector Block2 = new Vector(x + radius - 1, 255, z + radius - 1);
    	
		return CreateRegion(pl, Block1, Block2, id);
	}
	
	public abstract boolean CreateRegion(UUID pl, Vector coord1, Vector coord2, int id) ;
	
	public abstract boolean addMember(UUID pl, int id) ;
	
	public abstract boolean removeMember(UUID pl, int id) ;
	
	public abstract boolean RemoveRegions(int id) ;
}
