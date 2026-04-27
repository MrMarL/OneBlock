// Copyright 2026 MrMarL. The MIT License (MIT).
package oneblock;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import com.cryptomorin.xseries.XBlock;
import com.cryptomorin.xseries.XMaterial;

import oneblock.events.BlockEvent;
import oneblock.events.ItemsAdderEvent;
import oneblock.events.RespawnJoinEvent;
import oneblock.events.TeleportEvent;
import oneblock.events.TeleportNetherEvent;
import oneblock.events.extended.BlockEventFixed;
import oneblock.gui.GUI;
import oneblock.gui.GUIListener;
import oneblock.invitation.Guest;
import oneblock.loot.LootTableDispatcher;
import oneblock.placement.*;
import oneblock.storage.*;
import oneblock.utils.*;
import oneblock.worldguard.*;
import me.clip.placeholderapi.PlaceholderAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.meta.SkullMeta;

public class Oneblock extends JavaPlugin {
    public static Oneblock plugin;
    
    private static final int FLOWER_CHANCE = 3;
    private static final double[][] PARTICLE_OFFSETS = {{0, 0}, {0, 1}, {1, 0}, {1, 1}};
    
    private static final int BORDER_WARNING_DISTANCE = 2;
    private static final double BORDER_DAMAGE_AMOUNT = .2;
    private static final double BORDER_DAMAGE_BUFFER = 1;
    
    public static final Random rnd = new Random();
    public static final XMaterial GRASS_BLOCK = XMaterial.GRASS_BLOCK, GRASS = XMaterial.SHORT_GRASS;
    public static final VoidChunkGenerator GenVoid = new VoidChunkGenerator();
    public static final boolean isBorderSupported = Utils.findMethod(Bukkit.class, "createWorldBorder");// Is virtual border supported?;
    public static final boolean legacy = !XMaterial.supports(1,13);// Is version 1.13 supported?
    public static final boolean superlegacy = !XMaterial.supports(1,9);// Is version 1.9 supported?
    public static final boolean needDropFix = XMaterial.supports(1,21);// Is version 1.21 supported?
    
    public static ConfigManager configManager = new ConfigManager();
    
    /**
     * Island layout origin: {@code (world, x, y, z, offset)} treated as a single
     * atomically-replaceable unit. Writers mutate the origin via
     * {@link #setPosition} / {@link #setOffset} which perform one atomic swap
     * each; readers call {@link #origin()} once and use the captured snapshot
     * so that a concurrent {@code /ob set} cannot produce a mixed (torn) view
     * (e.g. new {@code x} with old {@code offset}).
     *
     * <p>Package-private so test code can reflect it; callers outside the
     * package use the getter helpers ({@link #getX()}, {@link #getOffset()},
     * ...) or {@link #origin()}.
     */
    static final AtomicReference<IslandOrigin> ORIGIN =
            new AtomicReference<>(IslandOrigin.EMPTY);

    public static volatile int max_players_team = 0, mob_spawn_chance = 9;
    public static volatile boolean island_for_new_players = false, rebirth = false, autojoin = false;
    public static volatile boolean droptossup = true, physics = false;
    public static volatile boolean lvl_bar_mode = false, particle = true;
    public static volatile boolean allow_nether = true, protection = false;
    public static volatile boolean saveplayerinventory = false;
    public static volatile boolean border = false;
    public static volatile boolean CircleMode = true;
    public static volatile boolean UseEmptyIslands = true;
    public static volatile boolean progress_bar = false;
    public static volatile String phText = "";
    
    public static volatile YamlConfiguration config;
    
    public final String version = getDescription().getVersion();
    public OBWorldGuard worldGuard = new OBWorldGuard();
    public Place.Type placetype = Place.Type.basic;
    private Place placer;
    
    /**
     * Leave-world reference. Written only by the main thread (
     * {@link #setLeave} + admin commands); {@code volatile} so the async
     * {@link Initialization} task's first read picks up a freshly-assigned
     * value. Separate from {@link #ORIGIN} because it belongs to a different
     * workflow (player teleport destination) and is updated independently.
     */
    public static volatile World leavewor;
    boolean PAPI = false;
    boolean enabled = false;
    
    public ArrayList <XMaterial> flowers = new ArrayList<>();
    public PlayerCache cache = new PlayerCache();
    
    /** Shorthand for {@code origin().world()}. Pre-config returns {@code null}. */
    public final static World getWorld() { return ORIGIN.get().world(); }
    /** Snapshot of the current island origin. Always non-null. */
    public static IslandOrigin origin()  { return ORIGIN.get(); }
    /** Shorthand for {@code origin().world()}. */
    public static World getWor()  { return ORIGIN.get().world(); }
    /** Shorthand for {@code origin().x()}. */
    public static int getX()      { return ORIGIN.get().x(); }
    /** Shorthand for {@code origin().y()}. */
    public static int getY()      { return ORIGIN.get().y(); }
    /** Shorthand for {@code origin().z()}. */
    public static int getZ()      { return ORIGIN.get().z(); }
    /** Shorthand for {@code origin().offset()}; {@code 0} means "not configured". */
    public static int getOffset() { return ORIGIN.get().offset(); }
    public boolean isPAPIEnabled() { return PAPI; }
    public int[] getIslandCoordinates(final int id) {
    	IslandOrigin o = ORIGIN.get();
    	return IslandCoordinateCalculator.getById(id, o.x(), o.z(), o.offset(), CircleMode);
    }
    public int findNearestRegionId(final Location loc) { return IslandCoordinateCalculator.findNearestRegionId(loc); }
    
    @Override
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {return GenVoid;}
    
    public static String getBarTitle(Player p, int lvl) {
		if (lvl_bar_mode) return Level.get(lvl).name;
		if (plugin.PAPI) return PlaceholderAPI.setPlaceholders(p, phText);
        
		return phText;
	}
    
    @Override
    public void onEnable() {
    	plugin = this;
        GUI.legacy = !Utils.findMethod(SkullMeta.class, "setOwningPlayer");
        final Metrics metrics = new Metrics(this, 14477);
        final PluginManager pluginManager = Bukkit.getPluginManager();
        
        getLogger().info(
        		"\n█▀█ ░░░░ ░░░ █▀▄ ░░░ ░░░ ░░░ ░░░" + 
        		"\n█░█ █▄░█ █▀▀ █▄▀ █░░ █▀█ █▀▀ █▄▀" + 
        		"\n█▄█ █░▀█ ██▄ █▄▀ █▄▄ █▄█ █▄▄ █░█\n" + 
        		"\nby MrMarL");
        if (PAPI = pluginManager.isPluginEnabled("PlaceholderAPI")) {
        	getLogger().info("PlaceholderAPI has been found!");
            new OBP().register();
        }
        
        placetype = determinePlaceType(pluginManager);
        placer = Place.GetPlacerByType(placetype);
        getLogger().info("Custom block support mode: " + placetype.name());
        
        configManager.Configfile();
        Datafile();
        configManager.loadAdditionalConfigFiles();
        
        setupMetrics(metrics);
        
        pluginManager.registerEvents(new RespawnJoinEvent(), this);
        if (!superlegacy) pluginManager.registerEvents(new TeleportEvent(), this);
        BlockEvent blockEvent = needDropFix? new BlockEventFixed() : new BlockEvent();
        pluginManager.registerEvents(blockEvent, this);
        pluginManager.registerEvents(new GUIListener(), this);
        pluginManager.registerEvents(new TeleportNetherEvent(), this);
        if (placetype == Place.Type.ItemsAdder) pluginManager.registerEvents(new ItemsAdderEvent(), this);
        getCommand("oneblock").setExecutor(new CommandHandler());
        getCommand("oneblock").setTabCompleter(new CommandTabCompleter());
        
        if (getOffset() == 0) return;
        
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new Initialization(), 32, 80);
    }
    
    private Place.Type determinePlaceType(PluginManager pluginManager) {
        if (pluginManager.isPluginEnabled("ItemsAdder")) return Place.Type.ItemsAdder;
        if (pluginManager.isPluginEnabled("Oraxen")) return Place.Type.Oraxen;
        if (pluginManager.isPluginEnabled("Nexo")) return Place.Type.Nexo;
        if (pluginManager.isPluginEnabled("CraftEngine")) return Place.Type.CraftEngine;
        return legacy ? Place.Type.legacy : Place.Type.basic;
    }
    
    public void reload() {
    	configManager.loadConfigFiles();
    	worldGuard.recreateRegions();
    	ReloadBorders();
    }
    
    private void setupMetrics(Metrics metrics) {
        metrics.addCustomChart(new SimplePie("premium", () -> String.valueOf(OBWorldGuard.canUse)));
        metrics.addCustomChart(new SimplePie("circle_mode", () -> String.valueOf(CircleMode)));
        metrics.addCustomChart(new SimplePie("use_empty_islands", () -> String.valueOf(UseEmptyIslands)));
        metrics.addCustomChart(new SimplePie("gui", () -> String.valueOf(GUI.enabled)));
        metrics.addCustomChart(new SimplePie("place_type", () -> String.valueOf(placetype)));
    }
    
    public class Initialization implements Runnable {
        public void run() {
            if (getWor() != null) return;
            final World w = Bukkit.getWorld(config.getString("world"));
            leavewor = Bukkit.getWorld(config.getString("leaveworld"));
            if (w != null) {
                // Atomic swap: fold the freshly-resolved world into ORIGIN while
                // preserving the existing x/y/z/offset loaded earlier by
                // ConfigManager.Configfile(). Runs on the async scheduler thread.
                ORIGIN.updateAndGet(prev -> new IslandOrigin(w, prev.x(), prev.y(), prev.z(), prev.offset()));
                getLogger().info("The initialization of the world was successful!");
                runMainTask();
                reload();
            } else {
                getLogger().info("Waiting for initialization of world '" + config.getString("world") + "'...");
            }
        }
    }
    
    public void runMainTask() {
    	Bukkit.getScheduler().cancelTasks(this);
		if (getOffset() == 0) return;
		Bukkit.getScheduler().runTaskTimerAsynchronously(this, new TaskUpdatePlayers(), 0, 120);
		Bukkit.getScheduler().runTaskTimerAsynchronously(this, new TaskSaveData(), 200, 6000);
		if (!superlegacy) Bukkit.getScheduler().runTaskTimerAsynchronously(this, new TaskParticle(), 40, 40);
		Bukkit.getScheduler().runTaskTimer(this, new Task(), 40, 80);
		enabled = true;
		
    	if (OBWorldGuard.canUse && Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
        	getLogger().info("WorldGuard has been found!");
        	worldGuard.recreateRegions();
        }
        else OBWorldGuard.setEnabled(false);
    }

	public class TaskUpdatePlayers implements Runnable {
		public void run() {
			World w = getWor();
			if (w != null) cache.updateCache(w.getPlayers());
		}
	}
	
	public class TaskSaveData implements Runnable {
		public void run() { SaveData(); }
	}
	
	public class TaskParticle implements Runnable {
	    public void run() {
	        if (!particle) return;

	        for (Player ponl: cache.getPlayers()) {
	            int[] result = cache.getIslandCoordinates(ponl);
	            if (result == null) continue;
	            int X_pl = result[0], Z_pl = result[1];
	            double baseY = getY() + 0.5;

	            for (double[] offset : PARTICLE_OFFSETS) {
	                Location loc = new Location(getWor(), X_pl + offset[0], baseY, Z_pl + offset[1]);
	                getWor().spawnParticle(Particle.PORTAL, loc, 5, 0, 0, 0, 0);
	            }
	        }
	    }
	}

    public class Task implements Runnable {
        public void run() { // SubBlockGen
            for (Player player : cache.getPlayers()) {
            	if (player.getWorld() != getWor()) continue;
            	final UUID uuid = player.getUniqueId();
            	final int result[] = cache.getIslandCoordinates(player);
                final int X_pl = result[0], Z_pl = result[1], plID = result[2];
            	
                if (protection && !player.hasPermission("Oneblock.ignoreBarrier")) {
                	boolean CheckGuest = false;
                	Location loc = player.getLocation();
            		PlayerInfo inf = Guest.getPlayerInfo(uuid);
            		if (inf != null) {
                    	int crd[] = getIslandCoordinates(PlayerInfo.getId(inf.uuid));
                        CheckGuest = isWithinIslandBounds(loc, crd[0], crd[1]);
                        if (!CheckGuest) Guest.remove(uuid);
            		}
            		if (!isWithinIslandBounds(loc, X_pl, Z_pl) && !CheckGuest) {
            			player.performCommand("ob j");
            			player.sendMessage(Messages.protection);
                    	continue;
                    }
                }
                
                final Block block = getWor().getBlockAt(X_pl, getY(), Z_pl);
                if (block.getType() != Material.AIR) continue;
                if (PlayerInfo.getId(uuid) == -1) continue;
                
                BlockGen(X_pl, Z_pl, plID, player, block);
            }
        }
    }
    
    public void BlockGen(final int X_pl, final int Z_pl, final int plID, final Player ponl, final Block block) {
    	final PlayerInfo inf = PlayerInfo.get(plID);
    	Level lvl_inf = Level.get(inf.lvl); 
        if (++inf.breaks >= inf.getRequiredBreaks()) {
        	lvl_inf = inf.lvlup();
        	if (progress_bar) inf.createBar();
        	configManager.reward.executeRewards(ponl, inf.lvl, lvl_inf.name);
        }
        if (progress_bar) {
            inf.bar.setTitle(getBarTitle(ponl, inf.lvl));
            inf.bar.setProgress(inf.getPercent());
            inf.bar.addPlayer(ponl);
        }
        
        PoolEntry entry = PoolRegistry.pickBlock(lvl_inf.blocks, rnd);
        
        if (entry == null || entry.kind == PoolEntry.Kind.DEFAULT_GRASS) {
            XBlock.setType(block, GRASS_BLOCK);
            if (rnd.nextInt(FLOWER_CHANCE) == 1)
                XBlock.setType(getWor().getBlockAt(X_pl, getY() + 1, Z_pl), flowers.get(rnd.nextInt(flowers.size())));
        }
        else switch (entry.kind) {
            case BLOCK:
                placer.setType(block, entry.value, physics);
                break;
            case CHEST:
            	String chest_name = (String)entry.value;
                if (LootTableDispatcher.populate(block, ChestItems.getNamespacedKey(chest_name), rnd)) {
                	BlockState bs = block.getState();
            		if (!(bs instanceof Chest)) break;
                	ChestItems.fillLegacyChest(((Chest)bs).getInventory(), chest_name, rnd);
                }
                break;
            case COMMAND:
            	placer.executeCommand(block, (String)entry.value);
                break;
            default:
                break;
        }

        if (rnd.nextInt(mob_spawn_chance) == 0) spawnRandomMob(X_pl, Z_pl, lvl_inf);
	}
    
	public void spawnRandomMob(int pos_x, int pos_z, Level level) {
		EntityType type = PoolRegistry.pickMob(level.mobs, rnd);
		if (type == null) return;
		getWor().spawnEntity(new Location(getWor(), pos_x + .5, getY() + 1, pos_z + .5), type);
	}
    
    public void UpdateBorderLocation(Player pl, Location loc) {
    	int plID = findNearestRegionId(loc);
		int result[] = getIslandCoordinates(plID);
        int X_pl = result[0], Z_pl = result[1];
		
		WorldBorder br = Bukkit.createWorldBorder();
    	br.setCenter(X_pl+.5, Z_pl+.5);
    	int _off = getOffset();
    	br.setSize(_off - 1 + (_off & 1));
    	br.setWarningDistance(BORDER_WARNING_DISTANCE);
    	br.setDamageAmount(BORDER_DAMAGE_AMOUNT);
    	br.setDamageBuffer(BORDER_DAMAGE_BUFFER);
    	pl.setWorldBorder(br);
    }
    
    public void UpdateBorder(final Player pl) {
    	WorldBorder border = pl.getWorldBorder();
    	Bukkit.getScheduler().runTaskLaterAsynchronously(this, 
    		() -> { pl.setWorldBorder(border); }, 10L);
    }
    
    public void ReloadBorders() {
    	if (!isBorderSupported) return;
    	World w = getWor();
    	if (w == null) return;
    	if (border) w.getPlayers().forEach(pl -> plugin.UpdateBorderLocation(pl, pl.getLocation()));
    	else w.getPlayers().forEach(pl -> pl.setWorldBorder(null));
    }
    
    public boolean isWithinIslandBounds(Location loc, int centerX, int centerZ) {
        int deltaX = loc.getBlockX() - centerX;
        int deltaZ = loc.getBlockZ() - centerZ;
        int radius = Math.abs(getOffset() >> 1) + 1;
        
        return Math.abs(deltaX) <= radius && Math.abs(deltaZ) <= radius;
    }
    
    @Override
    public void onDisable() {
    	SaveData();
    	DatabaseManager.close();
    }
    
    public void SaveData() {
    	if (DatabaseManager.save(PlayerInfo.list)) return;
    	JsonPlayerDataStore.write(PlayerInfo.list);
    }

    private void Datafile() {
        DatabaseManager.initialize();
        PlayerInfo.replaceAll(DatabaseManager.load());

    	if (!PlayerInfo.list.isEmpty()) {
    		getLogger().info("Player data has been successfully obtained from the " + DatabaseManager.dbType + " database.");
    		return;
    	}

		if (JsonPlayerDataStore.f.exists())
			PlayerInfo.replaceAll(JsonPlayerDataStore.read());
		else
			PlayerInfo.replaceAll(LegacyYamlPlayerDataStore.read());
    }
    
    public void setPosition(Location loc) { setPosition(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()); }
    public void setPosition(World world, int x_, int y_, int z_) {
    	// Single atomic swap: readers on async threads either see the pre-call
    	// origin in full, or the post-call origin in full — never a mix.
    	IslandOrigin next = ORIGIN.updateAndGet(prev -> new IslandOrigin(
    			world != null ? world : prev.world(), x_, y_, z_, prev.offset()));
    	if (next.world() != null) config.set("world", next.world().getName());
        config.set("x", (double) x_);
        config.set("y", (double) y_);
        config.set("z", (double) z_);
    }

    /**
     * Atomically set the cell-edge length ({@code offset}) that drives the
     * island grid. Also persists the new value to {@code config.yml} under
     * the key {@code set}. Safe to call only from the main thread (admin
     * {@code /ob set}) — config writes assume main-thread ownership.
     */
    public void setOffset(int off) {
    	ORIGIN.updateAndGet(prev -> prev.withOffset(off));
    	if (config != null) config.set("set", off);
    }
    
    public Location getLeave() { return new Location(leavewor, config.getDouble("xleave"), config.getDouble("yleave"), config.getDouble("zleave"), (float)config.getDouble("yawleave"), 0f); }
    public void setLeave(Location loc) { setLeave(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getYaw()); }
    public void setLeave(World world, double x_, double y_, double z_, float yaw) {
    	if (world == null) return;
    	leavewor = world;
        config.set("leaveworld", leavewor.getName());
        config.set("xleave", x_);
        config.set("yleave", y_);
        config.set("zleave", z_);
        config.set("yawleave", yaw);
    }
    
    public static int getLevel(UUID pl_uuid) {
    	return PlayerInfo.get(pl_uuid).lvl;
    }
    public static int getNextLevel(UUID pl_uuid) {
    	return getLevel(pl_uuid) + 1;
    }
    public static String getLevelName(UUID pl_uuid) {
    	int lvl = getLevel(pl_uuid);
    	return Level.get(lvl).name;
    }
    public static String getNextLevelName(UUID pl_uuid) {
    	int lvl = getNextLevel(pl_uuid);
    	return Level.get(lvl).name;
    }
    public static int getBroken(UUID pl_uuid) {
        return PlayerInfo.get(pl_uuid).breaks;
    }
    public static int getRemaining(UUID pl_uuid) {
    	PlayerInfo inf = PlayerInfo.get(pl_uuid);
    	return inf.getRequiredBreaks() - inf.breaks;
    }
    public static int getLevelLength(UUID pl_uuid) {
    	return PlayerInfo.get(pl_uuid).getRequiredBreaks();
    }
    public static boolean isVisitAllowed(UUID pl_uuid) {
    	return PlayerInfo.get(pl_uuid).allowVisit;
    }
    public static int countVisitors(UUID pl_uuid) {
    	int count = 0;
    	int reg_id = PlayerInfo.getId(pl_uuid);
    	if (reg_id != -1)
	    	for (Player ponl: plugin.cache.getPlayers())
	    		if (plugin.findNearestRegionId(ponl.getLocation()) == reg_id)
	    			count++;
    	return count;
    }
    public static PlayerInfo getTop(int i) {
    	if (PlayerInfo.size() <= i) return PlayerInfo.not_found;
    	return getTop(i,getTopList());
    }
    public static PlayerInfo getTop(int i, List<PlayerInfo> sorted) {
    	if (sorted.size() <= i) return PlayerInfo.not_found;
    	return sorted.get(i).uuid == null ? PlayerInfo.not_found : sorted.get(i);
    }
    public static int getTopPosition(PlayerInfo player) {
        if (player == null || player.uuid == null) return -1;

        List<PlayerInfo> sorted = getTopList();
        for (int i = 0; i < sorted.size(); i++) {
            PlayerInfo entry = sorted.get(i);
            if (entry != null && player.uuid.equals(entry.uuid))
                return i;
        }

        return -1;
    }
    // Sorted top-list cache. Invalidated via PlayerInfo.topVersion() which bumps
    // on level-up, slot assignment, and bulk reload. Under light contention we
    // may re-sort twice from two threads simultaneously; that's benign duplicate
    // work and cheaper than a global lock in the hot placeholder path.
    private static volatile long topCacheVersion = -1;
    private static volatile List<PlayerInfo> topCache = java.util.Collections.emptyList();

    public static List<PlayerInfo> getTopList() {
    	long v = PlayerInfo.topVersion();
    	if (v != topCacheVersion) {
    		List<PlayerInfo> sorted = new ArrayList<>(PlayerInfo.list);
    		sorted.sort(PlayerInfo.COMPARE_BY_LVL);
    		topCache = java.util.Collections.unmodifiableList(sorted);
    		topCacheVersion = v;
    	}
    	return topCache;
    }
}