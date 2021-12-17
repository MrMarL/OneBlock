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
        return "0.9";
    }
    @Override
    public String onPlaceholderRequest(Player p, String identifier) {
        // %OB_ver%
        if (identifier.equals("ver")) {
            return "0.9";
        }
        // %OB_lvl%
        if (identifier.equals("lvl")) {
            return ""+Oneblock.getlvl(p.getName());
        }
        // %OB_lvl_name%
        if (identifier.equals("lvl_name")) {
            return ""+Oneblock.getlvlname(p.getName());
        }
        // %OB_break_on_this_lvl%
        if (identifier.equals("break_on_this_lvl")) {
            return ""+Oneblock.getblocks(p.getName());
        }
        // %OB_need_to_lvl_up%
        if (identifier.equals("need_to_lvl_up")) {
            return ""+Oneblock.getneed(p.getName());
        }
        // %OB_player_count%
        if (identifier.equals("need_to_lvl_up")) {
            return ""+Oneblock.plonl.size();
        }
        return null;
    }
}
