package Oneblock;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerInfo {
	public static ArrayList<PlayerInfo> list = new ArrayList<>();
	
	public UUID uuid;
	public ArrayList<UUID> uuids = new ArrayList<UUID>();
	public int lvl = 0;
	public int breaks = 0;
	public BossBar bar = null;
	public boolean allow_visit = false;
	
	public PlayerInfo(UUID uuid) {
		this.uuid = uuid;
	}
	
	public Level lvlup() {
		++lvl;
		breaks = 0;
		return Level.get(lvl);
	}
	
	public void createBar() {
		Level level = Level.get(lvl);
		createBar(level.name, level.color, level.style);
	}
	
	public void createBar(String title) {
		Level level = Level.get(lvl);
		createBar(title, level.color, level.style);
	}
	
	private void createBar(String text, BarColor color, BarStyle style) {
		if (bar == null) {
			bar = Bukkit.createBossBar(text, color, style, BarFlag.DARKEN_SKY);
			return;
		}
		bar.setTitle(text);
		bar.setColor(color);
		bar.setStyle(style);
	}
	
	public void removeBar(Player p) {
		if (bar == null) return;
		bar.removePlayer(p);
	}
	
	public void removeUUID(UUID deleted) {
		if (uuid.equals(deleted)) {
			if (uuids.size() > 0) {
				uuid = uuids.get(0);
				uuids.remove(0);
			}
			else uuid = null;
		}
		else uuids.remove(deleted);
	}
	
	public int getNeed() {
        return Level.get(lvl).length;
    }
	
	public double getPercent() {
		return (double) breaks / getNeed();
	}
	
	public static void removeBarStatic(Player p) {
		if (list.size() == 0) return;
		get(p.getUniqueId()).removeBar(p);
	}
	
	public static int GetId(UUID name) {
    	for(int i = 0; i<PlayerInfo.size() ;i++) {
    		PlayerInfo pl = PlayerInfo.get(i);
    		if (pl.uuid == null)
    			continue;
    		if (pl.uuid.equals(name))
    			return i;
    		if (pl.uuids.contains(name))
    			return i;
    	}
    	return -1;
    }
	
	public static boolean ExistNoInvaitId(UUID name) {
    	for(PlayerInfo pl:PlayerInfo.list) {
    		if (pl.uuid == null)
    			continue;
    		if (pl.uuid.equals(name))
    			return true;
    	}
    	return false;
    }
	
	public static PlayerInfo get(int id) {
		return list.get(id);
	}
	
	public static PlayerInfo get(UUID uuid) {
		int plID = GetId(uuid);
		if (plID == -1) return new PlayerInfo(null);	
		return list.get(plID);
	}
	
	public static void set(int id, PlayerInfo pInf) {
		if (id < list.size())
			list.set(id, pInf);
		else
			list.add(pInf);
	}
	
	public static int getFreeId(boolean UseEmptyIslands) {
		if (UseEmptyIslands)
			return PlayerInfo.getNull();
		return PlayerInfo.size();
	}
	
	public static int size() {
		return list.size();
	}
	
	public static int getNull() {
		for (int i = 0; list.size() > i; i++)
			if (list.get(i).uuid == null) 
				return i;
		return list.size();
	}
	
	public static final Comparator<PlayerInfo> COMPARE_BY_LVL = new Comparator<PlayerInfo>() {
		@Override
		public int compare(PlayerInfo lhs, PlayerInfo rhs) {
			if (rhs.uuid == null)
				return -1;
			return rhs.lvl - lhs.lvl;
		}
	};
}