package Oneblock;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class JsonSimple {

	@SuppressWarnings("unchecked")
	public static void Write(int id, ArrayList<PlayerInfo> pls, File f) 
			 {
		JSONObject main = new JSONObject();
		
		for (int i = 0;pls.size() > i;i++) {
			JSONObject user = new JSONObject();
			PlayerInfo pl = pls.get(i);
			user.put("nick", pl.nick);
			user.put("lvl", pl.lvl);
			user.put("breaks", pl.breaks);
			
			JSONArray arr = new JSONArray();
			for(String us: pl.nicks)
				arr.add(us);
			user.put("invated", arr);
			main.put(i, user);
		}

		main.put("id", id);
		
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
		if(main == null)
			return infs;
		int id = ((Number) main.get("id")).intValue();
		for(int i = 0; i<id ;i++) {
			PlayerInfo pl = new PlayerInfo();
			JSONObject user = (JSONObject) main.get(""+i);
			pl.nick = (String) user.get("nick");
			pl.lvl = ((Number) user.get("lvl")).intValue();
			pl.breaks =  ((Number) user.get("breaks")).intValue();
			JSONArray arr = (JSONArray) user.get("invated");
			for(int q = 0;q<arr.size();q++)
				pl.nicks.add((String) arr.get(q));
			infs.add(pl); 
		}
		return infs;
	}
}
