package Oneblock;

import java.util.ArrayList;
import java.util.Comparator;

import org.bukkit.boss.BossBar;

public class PlayerInfo {
	public static ArrayList <PlayerInfo> list = new ArrayList <>();
	
	public String nick;
	public ArrayList<String> nicks = new ArrayList<String>();
	public int lvl = 0;
	public int breaks = 0;
	public BossBar bar = null;
	
	public PlayerInfo(String name) {
		nick = name;
	}
	
	public void lvlup() {
		++lvl;
		breaks = 0;
	}
	
	public static PlayerInfo get(int id) {
		return list.get(id);
	}
	
	public static void set(int id, PlayerInfo pInf) {
		if (id < list.size())
			list.set(id, pInf);
		else
			list.add(pInf);
	}
	
	public static int size() {
		return list.size();
	}
	
	public static int getNull() {
		for (int i = 0; list.size() > i; i++)
			if (list.get(i).nick == null) 
				return i;
		return list.size();
	}
	
	public static final Comparator<PlayerInfo> COMPARE_BY_LVL = new Comparator<PlayerInfo>() {
		@Override
		public int compare(PlayerInfo lhs, PlayerInfo rhs) {
			if (rhs.nick == null)
				return -1;
			return rhs.lvl - lhs.lvl;
		}
	};
}
