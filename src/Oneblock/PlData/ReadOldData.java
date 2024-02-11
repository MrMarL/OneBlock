package Oneblock.PlData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.file.YamlConfiguration;

import Oneblock.PlayerInfo;

public class ReadOldData {
	public static ArrayList<PlayerInfo> Read(final File f){
		ArrayList<PlayerInfo> infs = new ArrayList<PlayerInfo>();
		ArrayList<String> nicks = new ArrayList<String>();
		if (!f.exists())
			return infs;
		
		try(FileReader fileReader = new FileReader(f)) {
			@SuppressWarnings("resource")
			BufferedReader fileIn = new BufferedReader(new FileReader(f));
	        String line;
	        while ((line = fileIn.readLine()) != null)
	        	if (line.startsWith("_"))
	        		nicks.add(line.split(":")[0]);
		} catch (Exception e) {}
		
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
        	String lvl = String.format("Score_%d", i);
        	String breaks = String.format("ScSlom_%d", i);
        	
        	Server server = Bukkit.getServer();
        	
        	PlayerInfo newinf = null;
        	for(PlayerInfo inf:infs) 
        		if (inf.uuid.equals(server.getOfflinePlayer(_nick).getUniqueId()))
        			newinf = inf;

        	if (newinf != null)
        		newinf.uuids.add(server.getOfflinePlayer(_nick.substring(1)).getUniqueId());
        	else{
	        	newinf = new PlayerInfo(server.getOfflinePlayer(_nick.substring(1)).getUniqueId());
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