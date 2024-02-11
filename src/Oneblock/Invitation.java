package Oneblock;

import java.util.ArrayList;
import java.util.UUID;

public class Invitation {
	public static ArrayList <Invitation> list = new ArrayList<>();
	
	public UUID Inviting;
	public UUID Invited;
	
	public Invitation(UUID inviting, UUID invited) {
		Inviting = inviting;
		Invited = invited;
	}
	
	public static Invitation check(UUID uuid) {
		for(Invitation item:Invitation.list)
			if (item.Invited.equals(uuid))
				return item;
		return null;
	}
	
	@Override
    public boolean equals(Object obj) {
        if (obj != null && obj.getClass() == getClass()) {
        	Invitation inv = (Invitation)obj;
        	return equals(inv.Inviting, inv.Invited);
        }
        return false;
    }
	
	public boolean equals(UUID inviting, UUID invited) {
		return Inviting.equals(inviting) && Invited.equals(invited);
	}
}