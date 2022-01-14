package Oneblock;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class JsonSimple {

	@SuppressWarnings("unchecked")
	public static void Write(int id, ArrayList<PlayerInfo> pls, File f) 
			throws FileNotFoundException, ParseException, IOException {
		JSONObject obj = new JSONObject();
		
		for (int i = 0;pls.size() > i;i++) {
			PlayerInfo pl = pls.get(i);
			JSONArray data = new JSONArray();
			data.add(pl.nick);
			data.add(pl.lvl);
			data.add(pl.breaks);
			for(String n:pl.nicks)
				data.add(n);
			obj.put(i, data);
		}

		obj.put("id", id);
		
		FileWriter file = new FileWriter(f);
		file.write(obj.toJSONString());
		file.flush();
		file.close();
	}

	public static ArrayList<PlayerInfo> Read(File f) throws ParseException, FileNotFoundException, IOException {
		JSONParser parser = new JSONParser();
		JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(f));
		ArrayList <PlayerInfo> infs = new ArrayList <PlayerInfo>();
		int id = ((Number) jsonObject.get("id")).intValue();
		for(int i = 0; i<id ;i++) { 
			PlayerInfo pl = new PlayerInfo(); 
			JSONArray arr = (JSONArray) jsonObject.get(i);
			@SuppressWarnings("rawtypes")
			Iterator it = arr.iterator();
			//pl.nick = (String) it.next();
			//pl.lvl = ((Number) it.next()).intValue(); 
			//pl.breaks = ((Number) it.next()).intValue(); 
			//while (it.hasNext()) 
			//	pl.nicks.add((String) it.next());
			infs.add(pl); 
		}
		return infs;
	}
}
