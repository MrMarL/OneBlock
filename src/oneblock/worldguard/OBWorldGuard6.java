package oneblock.worldguard;

import java.util.UUID;

import org.bukkit.util.Vector;

public class OBWorldGuard6 extends OBWorldGuard{
	@Override
	public boolean createRegion(UUID pl, Vector coord1, Vector coord2, int id) {
		return true;
	}
	
	@Override
	public boolean addMember(UUID pl, int id) {
		return true;
	}
	
	@Override
	public boolean removeMember(UUID pl, int id) {
		return true;
	}
	
	@Override
	public boolean removeRegions(int id) {
		return true;
	}
}
