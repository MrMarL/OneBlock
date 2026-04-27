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

	@Override
	public int hashCode() {
		// Pair-based hash, consistent with the {@link #equals(Object)} contract
		// above: two invitations are equal iff both endpoint UUIDs match, so the
		// hash must blend both. Phase 3.7 added this so {@code Invitation.list}
		// containment checks work via {@code HashSet}/{@code HashMap} should a
		// future caller need O(1) membership instead of the current
		// {@code ArrayList} scan.
		return java.util.Objects.hash(Inviting, Invited);
	}
}