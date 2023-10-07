package Oneblock.PlData;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import Oneblock.PlayerInfo;

public class JsonSimple {
	public static final Pattern p = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");

	@SuppressWarnings("unchecked")
	public static void Write(ArrayList<PlayerInfo> pls, File f) {
		JSONObject main = new JSONObject();
		
		for (int i = 0;pls.size() > i;i++) {
			JSONObject user = new JSONObject();
			PlayerInfo pl = pls.get(i);
			if (pl.nick == null) {
				main.put(i, null);
				continue;
			}
			user.put("nick", pl.nick);
			user.put("uuid", Bukkit.getServer().getOfflinePlayer(pl.nick).getUniqueId().toString());
			user.put("lvl", pl.lvl);
			user.put("breaks", pl.breaks);
			
			JSONArray arr = new JSONArray();
			for(String us: pl.nicks)
				arr.add(Bukkit.getServer().getOfflinePlayer(us).getUniqueId().toString());
			user.put("invated", arr);
			main.put(i, user);
		}

		main.put("id", pls.size());
		
		try {
			FileWriter file = new FileWriter(f);
			file.write(main.toJSONString());
			file.flush();
			file.close();
		} catch (Exception e) {}
	}

	public static ArrayList<PlayerInfo> Read(File f)  {
		JSONObject main = null;
		JSONParser parser = new JSONParser();
		try {
			main = (JSONObject) parser.parse(new FileReader(f));
		} catch (Exception e) {}
		
		ArrayList <PlayerInfo> infs = new ArrayList <PlayerInfo>();
		if (main == null)
			return infs;
		PlayerInfo nullable = new PlayerInfo(null);
		int id = ((Number) main.get("id")).intValue();
		for(int i = 0; i<id ;i++) {
			JSONObject user = (JSONObject) main.get(""+i);
			if (user == null) {
				infs.add(nullable);
				continue;
			}
			String nick;
			if (user.containsKey("uuid"))
				nick = Bukkit.getServer().getOfflinePlayer(UUID.fromString((String)user.get("uuid"))).getName();
			else
				nick = (String) user.get("nick");
			PlayerInfo pl = new PlayerInfo(nick);
			pl.lvl = ((Number) user.get("lvl")).intValue();
			pl.breaks = ((Number) user.get("breaks")).intValue();
			JSONArray arr = (JSONArray) user.get("invated");
			for(int q = 0;q<arr.size();q++) {
				String us = (String) arr.get(q);
				if (p.matcher(us).matches())
					us = Bukkit.getServer().getOfflinePlayer(UUID.fromString((us))).getName();
				pl.nicks.add(us);
			}
			infs.add(pl); 
		}
		return infs;
	}
}