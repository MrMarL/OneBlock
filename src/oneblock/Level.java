package oneblock;

import java.util.ArrayList;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.EntityType;

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
	public WeightedPool<PoolEntry> blockPool = new WeightedPool<>();
	public WeightedPool<EntityType> mobPool = new WeightedPool<>();
	public BarColor color;
	public BarStyle style;
	public int length = 100;
	
	public Level(String name) {
        this.name = name;
    }
	
	public int getId() {
		for (int i = 0; i < size(); i++) 
			if (get(i) == this)
				return i;
		return 1;
	}
	
	public int blockPoolSize() { return blockPool.size(); }
	public int mobPoolSize()   { return mobPool.size(); }
	
	public void resetPools() {
		blockPool = new WeightedPool<>();
		mobPool = new WeightedPool<>();
	}
}