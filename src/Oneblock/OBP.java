package Oneblock;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import java.util.TreeMap;

import org.bukkit.entity.Player;

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
        return "1.0.5p";
    }
    @Override
    public String onPlaceholderRequest(Player p, String identifier) {
        // %OB_ver%
        if (identifier.equals("ver")) {
            return "1.0.5p";
        }
        // %OB_lvl%
        if (identifier.equals("lvl")) {
            return String.format("%d", Oneblock.getlvl(p.getName()));
        }
        // %OB_lvl_name%
        if (identifier.equals("lvl_name")) {
            return Oneblock.getlvlname(p.getName());
        }
        // %OB_next_lvl%
        if (identifier.equals("next_lvl")) {
            return String.format("%d", Oneblock.getnextlvl(p.getName()));
        }
        // %OB_next_lvl_name%
        if (identifier.equals("next_lvl_name")) {
            return Oneblock.getnextlvlname(p.getName());
        }
        // %OB_break_on_this_lvl%
        if (identifier.equals("break_on_this_lvl")) {
            return String.format("%d", Oneblock.getblocks(p.getName()));
        }
        // %OB_need_to_lvl_up%
        if (identifier.equals("need_to_lvl_up")) {
            return String.format("%d", Oneblock.getneed(p.getName()));
        }
        // %OB_player_count%
        if (identifier.equals("player_count")) {
            return String.format("%d", Oneblock.plonl.size());
        }
        // %OB_owner_name%
        if (identifier.equals("owner_name")) {
            return PlayerInfo.get(p.getName()).nick;
        }
        // %OB_percent%
        if (identifier.equals("percent")) {
        	PlayerInfo inf = PlayerInfo.get(p.getName());
        	return String.format("%d%%", (int) (inf.getPercent() * 100));
        }
        // %OB_scale%
        if (identifier.equals("scale")) {
        	PlayerInfo inf = PlayerInfo.get(p.getName());
        	return SCALE.floorEntry(inf.getPercent()).getValue().replace("╍", "█");
        }
        // %OB_top_%d_name%
        for(int i = 0;i<10;i++) {
        	if (identifier.equals(String.format("top_%d_name", i + 1)))
                return Oneblock.gettop(i).nick;
        }
        // %OB_top_%d_lvl%
        for(int i = 0;i<10;i++) {
        	if (identifier.equals(String.format("top_%d_lvl", i+1)))
                return String.format("%d",Oneblock.gettop(i).lvl);
        }
        // %OB_number_of_invited%
        if (identifier.equals("number_of_invited")) {
            return String.format("%d", PlayerInfo.get(p.getName()).nicks.size());
        }
        return null; 
    }
}