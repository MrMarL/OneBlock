package Oneblock;

import org.bukkit.util.Vector;

public abstract class OBWorldGuard {
	public final boolean canUse = false;
	public final String regionName = "OB_WG_%d";
	
	public abstract boolean CreateRegion(String pl, Vector coord1, Vector coord2, int id) ;
	
	public abstract boolean addMember(String pl, int id) ;
	
	public abstract boolean removeMember(String pl, int id) ;
	
	public abstract boolean RemoveRegions(int id) ;
}
