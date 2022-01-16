package Oneblock;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class OBP extends PlaceholderExpansion {
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
        return "0.9.4";
    }
    @Override
    public String onPlaceholderRequest(Player p, String identifier) {
        // %OB_ver%
        if (identifier.equals("ver")) {
            return "0.9.4";
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
            return Oneblock.pInf.get(Oneblock.GetId(p.getName())).nick;
        }
        // %OB_top_%d_name%
        for(int i = 0;i<10;i++) {
        	if (identifier.equals(String.format("top_%d_name", i+1)))
                return Oneblock.gettop(i).nick;
        }
        // %OB_top_%d_lvl%
        for(int i = 0;i<10;i++) {
        	if (identifier.equals(String.format("top_%d_name", i+1)))
                return String.format("%d",Oneblock.gettop(i).lvl);
        }
        return null;
    }
}
