package Oneblock;

import java.util.ArrayList;
import java.util.Comparator;

import org.bukkit.boss.BossBar;

public class PlayerInfo {
	public String nick;
	public ArrayList<String> nicks = new ArrayList<String>();
	public int lvl = 0;
	public int breaks = 0;
	public BossBar bar = null;
	
	public PlayerInfo(String name) {
		nick = name;
	}
	
	void lvlup() {
		++lvl;
		breaks = 0;
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
