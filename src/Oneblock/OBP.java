package Oneblock;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import java.util.TreeMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class OBP extends PlaceholderExpansion {
	
	private static final TreeMap<Double, String> SCALE;
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
        // %OB_lvl%
        if (identifier.equals("lvl")) {
            return Integer.toString(Oneblock.getlvl(p.getUniqueId()));
        }
        // %OB_lvl_name%
        if (identifier.equals("lvl_name")) {
            return Oneblock.getlvlname(p.getUniqueId());
        }
        // %OB_next_lvl%
        if (identifier.equals("next_lvl")) {
            return Integer.toString(Oneblock.getnextlvl(p.getUniqueId()));
        }
        // %OB_next_lvl_name%
        if (identifier.equals("next_lvl_name")) {
            return Oneblock.getnextlvlname(p.getUniqueId());
        }
        // %OB_break_on_this_lvl%
        if (identifier.equals("break_on_this_lvl")) {
            return Integer.toString(Oneblock.getblocks(p.getUniqueId()));
        }
        // %OB_lvl_lenght%
        if (identifier.equals("lvl_lenght")) {
            return Integer.toString(Oneblock.getlenght(p.getUniqueId()));
        }
        // %OB_need_to_lvl_up%
        if (identifier.equals("need_to_lvl_up")) {
            return Integer.toString(Oneblock.getneed(p.getUniqueId()));
        }
        // %OB_player_count%
        if (identifier.equals("player_count")) {
            return Integer.toString(Oneblock.plugin.cache.getPlayers().size());
        }
        // %OB_owner_name%
        if (identifier.equals("owner_name")) {
            UUID uuid = PlayerInfo.get(p.getUniqueId()).uuid;
            if (uuid == null) return "[None]";
            String name = Bukkit.getOfflinePlayer(uuid).getName();
            if (name == null) return "[None]";
    		return name;
        }
        // %OB_owner_online%
        if (identifier.equals("owner_online")) {
            UUID uuid = PlayerInfo.get(p.getUniqueId()).uuid;
            if (uuid == null) return "offline";
            if (Bukkit.getPlayer(uuid) == null) return "offline";
    		return "online";
        }
        // %OB_visit_allowed%
        if (identifier.equals("visit_allowed")) {
        	return Boolean.toString(Oneblock.getvisitallowed(p));
        }
        // %OB_visits%
        if (identifier.equals("visits")) {
        	return Integer.toString(Oneblock.getvisits(p.getUniqueId()));
        }
        // %OB_percent%
        if (identifier.equals("percent")) {
        	PlayerInfo inf = PlayerInfo.get(p.getUniqueId());
        	return Integer.toString((int) (inf.getPercent() * 100)) + "%";
        }
        // %OB_scale%
        if (identifier.equals("scale")) {
        	PlayerInfo inf = PlayerInfo.get(p.getUniqueId());
        	return SCALE.floorEntry(inf.getPercent()).getValue().replace("╍", "█");
        }
        // %OB_top_%d_name%
        for(int i = 0;i<10;i++) {
        	if (identifier.equals("top_"+(i+1)+"_name")) {
        		UUID uuid = Oneblock.gettop(i).uuid;
        		if (uuid == null) return "[None]";
        		String name = Bukkit.getOfflinePlayer(uuid).getName();
        		if (name == null) return "[None]";
        		return name;
        	}
        }
        // %OB_top_%d_lvl%
        for(int i = 0;i<10;i++) {
        	if (identifier.equals("top_"+(i+1)+"_lvl"))
                return Integer.toString(Oneblock.gettop(i).lvl);
        }
        // %OB_number_of_invited%
        if (identifier.equals("number_of_invited")) {
            return Integer.toString(PlayerInfo.get(p.getUniqueId()).uuids.size());
        }
        // %OB_ver%
        if (identifier.equals("ver")) {
            return "1.3.0";
        }
        return null; 
    }
}