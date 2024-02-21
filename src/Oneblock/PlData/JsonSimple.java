package Oneblock.PlData;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Server;
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
			if (pl.uuid == null) {
				main.put(i, null);
				continue;
			}
			user.put("uuid", pl.uuid.toString());
			user.put("lvl", pl.lvl);
			user.put("breaks", pl.breaks);
			if (pl.allow_visit) user.put("visit", pl.allow_visit);
			
			JSONArray arr = new JSONArray();
			
			for(UUID us: pl.uuids)
				arr.add(us.toString());
			user.put("invited", arr);
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
		Server server = Bukkit.getServer();
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
			PlayerInfo pl;
			if (user.containsKey("uuid"))
				pl = new PlayerInfo(UUID.fromString((String)user.get("uuid")));
			else
				pl = new PlayerInfo(server.getOfflinePlayer((String) user.get("nick")).getUniqueId());
			pl.lvl = ((Number) user.get("lvl")).intValue();
			pl.breaks = ((Number) user.get("breaks")).intValue();
			pl.allow_visit = user.containsKey("visit");
			JSONArray arr = (JSONArray) (user.containsKey("invated")? user.get("invated"): user.get("invited"));
			for(int q = 0;q<arr.size();q++) {
				String us = (String) arr.get(q);
				if (p.matcher(us).matches())
					pl.uuids.add(UUID.fromString(us));
				else
					pl.uuids.add(server.getOfflinePlayer(us).getUniqueId());
			}
			infs.add(pl); 
		}
		return infs;
	}
}