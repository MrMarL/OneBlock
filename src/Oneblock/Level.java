package Oneblock;

import java.util.ArrayList;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

public class Level {
	public static Level max = new Level("Level: MAX");
	public static ArrayList <Level> levels = new ArrayList <>();
	public static int multiplier = 5;
	
	public static Level get(int i) {
		if (i < levels.size())
			return levels.get(i);
		return max;
	}
	
	public static int size() {
		return levels.size();
	}
	
	public String name;
	public int blocks = 0;
	public int mobs = 0;
	public BarColor color;
	public BarStyle style;
	public int length = 100;
	
	public Level(String name) {
        this.name = name;
    }
	
	public int getId() {
		for (int i = 0; i < size(); i++) {
			Level lvl = get(i);
			if (lvl == this)
				return i;
		}
		return 1;
	}
}