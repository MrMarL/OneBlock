package Oneblock;

public class Invitation {
	public String Inviting;
	public String Invited;
	
	public Invitation(String inviting, String invited) {
		Inviting = inviting;
		Invited = invited;
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