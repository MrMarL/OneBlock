package Oneblock;

import java.util.ArrayList;

public class Invitation {
	public static ArrayList <Invitation> list = new ArrayList<>();
	
	public String Inviting;
	public String Invited;
	
	public Invitation(String inviting, String invited) {
		Inviting = inviting;
		Invited = invited;
	}
	
	public static Invitation check(String name) {
		for(Invitation item:Invitation.list)
			if (item.Invited.equals(name))
				return item;
		return null;
	}
	
	@Override
    public boolean equals(Object obj) {
        if (obj != null && obj.getClass() == getClass()) {
        	Invitation inv = (Invitation)obj;
        	return Inviting.equals(inv.Inviting) && Invited.equals(inv.Invited);
        }
        return false;
    }
	
	public boolean equals(String inviting, String invited) {
		return Inviting.equals(inviting) && Invited.equals(invited);
	}
}