package oneblock.storage;

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

/**
 * JSON-backed player data store. Writes / reads the {@code PlData.json}
 * file in the plugin data folder. Used as the primary persistence layer
 * when no database is configured (or the database save returned false).
 */
public class JsonPlayerDataStore {
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
			if (pl.allowVisit) user.put("visit", pl.allowVisit);
			
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
		Object idObj = main.get("id");
		if (!(idObj instanceof Number)) { plugin.getLogger().warning("[Oneblock] PlData.json missing or non-numeric 'id' header; treating as empty."); return infs; }
		int id = ((Number) idObj).intValue();
		for(int i = 0; i<id ;i++) {
			JSONObject user = (JSONObject) main.get(""+i);
			if (user == null) {
				infs.add(nullable);
				continue;
			}
			PlayerInfo pl = readPlayerInfo(user, i);
			if (pl == null) { infs.add(nullable); continue; }
			infs.add(pl);
		}
		return infs;
	}

	private static PlayerInfo readPlayerInfo(JSONObject user, int row) {
		UUID owner = resolveOwner(user, row);
		if (owner == null) return null;
		PlayerInfo pl = new PlayerInfo(owner);
		Object lvlObj = user.get("lvl");
		Object brkObj = user.get("breaks");
		pl.lvl    = (lvlObj instanceof Number) ? ((Number) lvlObj).intValue() : 0;
		pl.breaks = (brkObj instanceof Number) ? ((Number) brkObj).intValue() : 0;
		pl.allowVisit = user.containsKey("visit");
		Object arrObj = user.containsKey("invated") ? user.get("invated") : user.get("invited");
		if (arrObj instanceof JSONArray) {
			JSONArray arr = (JSONArray) arrObj;
			for (int q = 0; q < arr.size(); q++) {
				Object raw = arr.get(q);
				if (!(raw instanceof String)) continue;
				UUID invited = resolveUuid((String) raw, row, "invited");
				if (invited != null) pl.uuids.add(invited);
			}
		}
		return pl;
	}

	private static UUID resolveOwner(JSONObject user, int row) {
		if (user.containsKey("uuid")) {
			Object raw = user.get("uuid");
			if (raw instanceof String) return resolveUuid((String) raw, row, "owner");
		}
		Object nickRaw = user.get("nick");
		if (nickRaw instanceof String) return resolveUuid((String) nickRaw, row, "owner");
		plugin.getLogger().warning("[Oneblock] PlData.json row " + row + " has neither 'uuid' nor 'nick'; skipping");
		return null;
	}

	private static UUID resolveUuid(String token, int row, String field) {
		if (token == null || token.isEmpty()) return null;
		if (p.matcher(token).matches()) {
			try { return UUID.fromString(token); }
			catch (IllegalArgumentException ex) {
				plugin.getLogger().warning("[Oneblock] PlData.json row " + row + " " + field + " invalid UUID '" + token + "'");
				return null;
			}
		}
		org.bukkit.OfflinePlayer off = Bukkit.getOfflinePlayer(token);
		if (off == null || off.getUniqueId() == null) {
			plugin.getLogger().warning("[Oneblock] PlData.json row " + row + " unresolved " + field + " nick '" + token + "'");
			return null;
		}
		return off.getUniqueId();
	}
}