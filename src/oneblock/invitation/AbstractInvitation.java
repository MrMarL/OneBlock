package oneblock.invitation;

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
        if (obj instanceof AbstractInvitation) {
        	AbstractInvitation inv = (AbstractInvitation)obj;
        	return equals(inv.Inviting, inv.Invited);
        }
        return false;
    }
	
	public boolean equals(UUID inviting, UUID invited) {
		return Inviting.equals(inviting) && Invited.equals(invited);
	}
}