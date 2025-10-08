package Oneblock;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import java.util.TreeMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class OBP extends PlaceholderExpansion {
	
	private static final TreeMap<Double, String> SCALE;
	private static final String SCALE_CHAR = "█";
	private static final String NONE_PLACEHOLDER = "[None]";
    static {
        SCALE = new TreeMap<>();
        SCALE.put(.0,	"&c╍╍╍╍╍╍╍╍");
        SCALE.put(.125,	"&a╍&c╍╍╍╍╍╍╍");
        SCALE.put(.25,	"&a╍╍&c╍╍╍╍╍╍");
        SCALE.put(.375,	"&a╍╍╍&c╍╍╍╍╍");
        SCALE.put(.5,	"&a╍╍╍╍&c╍╍╍╍");
        SCALE.put(.625,	"&a╍╍╍╍╍&c╍╍╍");
        SCALE.put(.75,	"&a╍╍╍╍╍╍&c╍╍");
        SCALE.put(.875,	"&a╍╍╍╍╍╍╍&c╍");
        SCALE.put(1.,	"&a╍╍╍╍╍╍╍╍");
    }

    @Override
    public boolean canRegister() {
        return true;
    }
    @Override
    public String getAuthor() {
        return "MrMarL";
    }
    @Override
    public String getIdentifier() {
        return "OB";
    }
    @Override
    public String getPlugin() {
        return null;
    }
    @Override
    public String getVersion() {
        return Oneblock.plugin.version;
    }
    @Override
    public String onRequest(OfflinePlayer p, String identifier) {
    	if (p == null) return null;
    	
		switch (identifier) {
			case "lvl":
				return Integer.toString(Oneblock.getlvl(p.getUniqueId()));
	
			case "lvl_name":
				return Oneblock.getlvlname(p.getUniqueId());
	
			case "next_lvl":
				return Integer.toString(Oneblock.getnextlvl(p.getUniqueId()));
	
			case "next_lvl_name":
				return Oneblock.getnextlvlname(p.getUniqueId());
	
			case "break_on_this_lvl":
				return Integer.toString(Oneblock.getblocks(p.getUniqueId()));
	
			case "lvl_lenght":
				return Integer.toString(Oneblock.getlenght(p.getUniqueId()));
	
			case "need_to_lvl_up":
				return Integer.toString(Oneblock.getneed(p.getUniqueId()));
	
			case "player_count":
				return Integer.toString(Oneblock.plugin.cache.getPlayers().size());
	
			case "visit_allowed":
				return Boolean.toString(Oneblock.getvisitallowed(p));
	
			case "visits":
				return Integer.toString(Oneblock.getvisits(p.getUniqueId()));
	
			case "percent":
				PlayerInfo inf0 = PlayerInfo.get(p.getUniqueId());
				return Integer.toString((int) (inf0.getPercent() * 100)) + "%";
	
			case "scale":
				PlayerInfo inf1 = PlayerInfo.get(p.getUniqueId());
				return SCALE.floorEntry(inf1.getPercent()).getValue().replace("╍", SCALE_CHAR);
	
			case "number_of_invited":
				return Integer.toString(PlayerInfo.get(p.getUniqueId()).uuids.size());
	
			case "owner_name":
				return getOwnerName(p.getUniqueId());
	
			case "owner_online":
				return getOwnerOnlineStatus(p.getUniqueId());
		}
    	
        // %OB_top_%d_...%
        if (identifier.startsWith("top_")) {
            return handleTopPlaceholder(identifier);
        }

        return null; 
    }
    
    private String getOwnerName(UUID playerUUID) {
        PlayerInfo playerInfo = PlayerInfo.get(playerUUID);
        UUID ownerUUID = playerInfo.uuid;
        
        if (ownerUUID == null)  return NONE_PLACEHOLDER;
        
        OfflinePlayer owner = Bukkit.getOfflinePlayer(ownerUUID);
        return owner.getName() != null ? owner.getName() : NONE_PLACEHOLDER;
    }
    
    private String getOwnerOnlineStatus(UUID playerUUID) {
        PlayerInfo playerInfo = PlayerInfo.get(playerUUID);
        UUID ownerUUID = playerInfo.uuid;
        
        if (ownerUUID == null) return "offline";
        
        return Bukkit.getPlayer(ownerUUID) != null ? "online" : "offline";
    }
    
    private String handleTopPlaceholder(String identifier) {
    	String[] parts = identifier.split("_", 3);
        if (parts.length != 3) return null;
        
        try {
            int position = Integer.parseInt(parts[1]) - 1;
            if (position < 0 || position >= 10) return null;
            
            PlayerInfo topPlayer = Oneblock.gettop(position);
            
            if (topPlayer.uuid == null) return NONE_PLACEHOLDER;
            
            switch (parts[2]) {
	            case "name":
	                OfflinePlayer player = Bukkit.getOfflinePlayer(topPlayer.uuid);
	                return player.getName() != null ? player.getName() : NONE_PLACEHOLDER;
	                
	            case "lvl":
	                return Integer.toString(topPlayer.lvl);
            }
        } catch (NumberFormatException e) { return null; }

        return null;
    }
}