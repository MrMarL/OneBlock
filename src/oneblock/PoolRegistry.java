package oneblock;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.bukkit.entity.EntityType;

public class PoolRegistry {
    private static final List<PoolEntry> BLOCKS = new ArrayList<>();
    private static final List<EntityType> MOBS = new ArrayList<>();
    
    public static void addBlock(PoolEntry block, int weight) {
        for (int i = 0; i < weight; i++) {
            BLOCKS.add(block);
        }
    }
    
    public static void addMob(EntityType mob, int weight) {
        for (int i = 0; i < weight; i++) {
            MOBS.add(mob);
        }
    }
    
    public static int totalBlocks() {
        return BLOCKS.size();
    }
    
    public static int totalMobs() {
        return MOBS.size();
    }
    
    public static PoolEntry pickBlock(int bound, Random rnd) {
        if (bound <= 0) return null;
        return BLOCKS.get(rnd.nextInt(bound));
    }
    
    public static EntityType pickMob(int bound, Random rnd) {
        if (bound <= 0) return null;
        return MOBS.get(rnd.nextInt(bound));
    }
    
    public static Map<PoolEntry, Integer> getBlocks(Level level) {
        int start = level.getId() == 0 ? 0 : Level.get(level.getId() - 1).blocks;
        int end = level.blocks;
        Map<PoolEntry, Integer> map = new LinkedHashMap<>();
        for (int i = start; i < end; i++) {
            map.merge(BLOCKS.get(i), 1, Integer::sum);
        }
        return map;
    }
    
    public static Map<EntityType, Integer> getMobs(Level level) {
        int start = level.getId() == 0 ? 0 : Level.get(level.getId() - 1).mobs;
        int end = level.mobs;
        Map<EntityType, Integer> map = new LinkedHashMap<>();
        for (int i = start; i < end; i++) {
            map.merge(MOBS.get(i), 1, Integer::sum);
        }
        return map;
    }
    
    public static void clear() {
        BLOCKS.clear();
        MOBS.clear();
    }
}