package oneblock.pldata;

import static oneblock.Oneblock.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import oneblock.PlayerInfo;

public class JsonSimple {
	public static final Pattern p = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");
	public static final File f = new File(plugin.getDataFolder(), "PlData.json");

	@SuppressWarnings("unchecked")
	public static void Write(List<PlayerInfo> pls) {
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
		
		try (FileWriter file = new FileWriter(f)) {
			file.write(main.toJSONString());
			file.flush();
		} catch (Exception e) {
			plugin.getLogger().warning("Failed to write player data to JSON: " + e.getMessage());
		}
	}

	public static List<PlayerInfo> Read()  {
		JSONObject main = null;
		JSONParser parser = new JSONParser();
		try (FileReader reader = new FileReader(f)) {
			main = (JSONObject) parser.parse(reader);
		} catch (Exception e) {
			plugin.getLogger().warning("Failed to read player data from JSON: " + e.getMessage());
		}
		
		List <PlayerInfo> infs = new ArrayList <PlayerInfo>();
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
				pl = new PlayerInfo(Bukkit.getOfflinePlayer((String) user.get("nick")).getUniqueId());
			pl.lvl = ((Number) user.get("lvl")).intValue();
			pl.breaks = ((Number) user.get("breaks")).intValue();
			pl.allow_visit = user.containsKey("visit");
			JSONArray arr = (JSONArray) (user.containsKey("invated")? user.get("invated"): user.get("invited"));
			for(int q = 0;q<arr.size();q++) {
				String us = (String) arr.get(q);
				if (p.matcher(us).matches())
					pl.uuids.add(UUID.fromString(us));
				else
					pl.uuids.add(Bukkit.getOfflinePlayer(us).getUniqueId());
			}
			infs.add(pl); 
		}
		return infs;
	}
}