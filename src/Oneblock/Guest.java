package Oneblock;

import java.util.ArrayList;
import java.util.UUID;

public class Guest extends Invitation {
	public static ArrayList <Guest> list = new ArrayList<>();
	
	public static Guest check(UUID uuid) {
		for(Guest item: list)
			if (item.Invited.equals(uuid))
				return item;
		return null;
	}
	
	public Guest(UUID inviting, UUID invited) { 
		super(inviting, invited);
	}
	
	public static boolean remove(UUID uuid) {
		Guest r = null;
		for (Guest g : list)
			if (g.Invited == uuid) { r = g; break; }
		return list.remove(r);
	}
}