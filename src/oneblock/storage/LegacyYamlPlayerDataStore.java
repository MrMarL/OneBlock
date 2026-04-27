package oneblock.storage;

import static oneblock.Oneblock.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import oneblock.PlayerInfo;

/**
 * Legacy YAML-backed player data store used only as a one-time migration
 * source on first load when no {@code PlData.json} exists yet. Reads the
 * old {@code PlData.yml} format (pre-1.x Oneblock, where nicknames were
 * the primary key) and returns the data shaped as modern
 * {@link PlayerInfo} objects keyed by {@link java.util.UUID}.
 */
public class LegacyYamlPlayerDataStore {
	public static File f = new File(plugin.getDataFolder(), "PlData.yml");
	
	public static ArrayList<PlayerInfo> read(){
		ArrayList<PlayerInfo> infs = new ArrayList<PlayerInfo>();
		ArrayList<String> nicks = new ArrayList<String>();
		if (!f.exists()) return infs;
		
		try (BufferedReader fileIn = new BufferedReader(new FileReader(f))) {
	        String line;
	        while ((line = fileIn.readLine()) != null)
	        	if (line.startsWith("_"))
	        		nicks.add(line.split(":")[0]);
		} catch (Exception e) {
			plugin.getLogger().warning("Failed to parse legacy PlData.yml: " + e.getMessage());
		}
		
		YamlConfiguration data = YamlConfiguration.loadConfiguration(f);
        if (!data.isInt("id"))
            return infs;
        int id = data.getInt("id");
        for (int i = 0; i < id; i++) {
        	String _nick = "";
        	for(String nick: nicks)
        		if (data.getInt(nick) == i)
        			_nick = nick;
        	if (_nick.equals(""))
        		continue;
        	String playerName = _nick.substring(1);
        	org.bukkit.OfflinePlayer off = Bukkit.getOfflinePlayer(playerName);
        	if (off == null || off.getUniqueId() == null) {
        		plugin.getLogger().warning("Legacy PlData.yml: unresolved nick '" + playerName + "' for island " + i + "; skipping row");
        		continue;
        	}
        	java.util.UUID uuid = off.getUniqueId();
        	String lvl = String.format("Score_%d", i);
        	String breaks = String.format("ScSlom_%d", i);

        	PlayerInfo newinf = null;
        	for(PlayerInfo inf:infs)
        		if (uuid.equals(inf.uuid))
        			newinf = inf;

        	if (newinf != null)
        		newinf.uuids.add(uuid);
        	else{
	        	newinf = new PlayerInfo(uuid);
	            if (data.isInt(lvl))
	            	newinf.lvl = data.getInt(lvl);
	            if (data.isInt(breaks))
	            	newinf.breaks = data.getInt(breaks);
	            infs.add(newinf);
            }
        }
		return infs;
	}
}