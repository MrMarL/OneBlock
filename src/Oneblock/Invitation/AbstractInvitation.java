package Oneblock.Invitation;

import java.util.UUID;

public abstract class AbstractInvitation {
	public UUID Inviting;
	public UUID Invited;
	
	public AbstractInvitation(UUID inviting, UUID invited) {
		Inviting = inviting;
		Invited = invited;
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