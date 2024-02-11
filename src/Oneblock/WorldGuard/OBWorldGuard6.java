package Oneblock.WorldGuard;

import java.util.UUID;

import org.bukkit.util.Vector;

public class OBWorldGuard6 extends OBWorldGuard{
	
	public boolean CreateRegion(UUID pl, Vector coord1, Vector coord2, int id) {
		return true;
	}
	
	public boolean addMember(UUID pl, int id) {
		return true;
	}
	
	public boolean removeMember(UUID pl, int id) {
		return true;
	}
	
	public boolean RemoveRegions(int id) {
		return true;
	}
}
