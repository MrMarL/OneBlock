package Oneblock;

import java.util.ArrayList;

import org.bukkit.boss.BossBar;

public class PlayerInfo {
	public String nick;
	public ArrayList<String> nicks = new ArrayList<String>();
	public int lvl = 0;
	public int breaks = 0;
	public BossBar bar = null;
	
	void lvlup() {
		++lvl;
		breaks = 0;
	}
}
