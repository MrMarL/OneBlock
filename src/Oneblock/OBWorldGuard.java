package Oneblock;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.util.Vector;

public abstract class OBWorldGuard {
	public static final boolean canUse = false;
	public static final String regionName = "OB_WG_%d";
	public static List <String> flags = new ArrayList <>();
	
	public abstract boolean CreateRegion(String pl, Vector coord1, Vector coord2, int id) ;
	
	public abstract boolean addMember(String pl, int id) ;
	
	public abstract boolean removeMember(String pl, int id) ;
	
	public abstract boolean RemoveRegions(int id) ;
}
