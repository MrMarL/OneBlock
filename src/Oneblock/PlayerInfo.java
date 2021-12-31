package Oneblock;

import org.bukkit.boss.BossBar;

public class PlayerInfo {
	public int lvl = 0;
	public int breaks = 0;
	public BossBar bar = null;
	
	void lvlup() {
		++lvl;
		breaks = 0;
	}
}
