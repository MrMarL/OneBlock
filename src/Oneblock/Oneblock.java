// Copyright © 2025 MrMarL. The MIT License (MIT).
package Oneblock;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import com.cryptomorin.xseries.XBlock;
import com.cryptomorin.xseries.XMaterial;

import Oneblock.Events.BlockEvent;
import Oneblock.Events.ItemsAdderEvent;
import Oneblock.Events.RespawnJoinEvent;
import Oneblock.Events.TeleportEvent;
import Oneblock.Events.TeleportNetherEvent;
import Oneblock.Invitation.Guest;
import Oneblock.PlData.*;
import Oneblock.UniversalPlace.*;
import Oneblock.Utils.*;
import Oneblock.WorldGuard.*;
import Oneblock.gui.GUI;
import Oneblock.gui.GUIListener;
import me.clip.placeholderapi.PlaceholderAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.meta.SkullMeta;

public class Oneblock extends JavaPlugin {
    public static Oneblock plugin;
    
    private static final int MOB_SPAWN_CHANCE = 9;
    private static final int FLOWER_CHANCE = 3;
    private static final double[][] PARTICLE_OFFSETS = {{0, 0}, {0, 1}, {1, 0}, {1, 1}};
    
    public static final Random rnd = new Random(System.currentTimeMillis());
    public static final XMaterial GRASS_BLOCK = XMaterial.GRASS_BLOCK, GRASS = XMaterial.SHORT_GRASS;
    public static final VoidChunkGenerator GenVoid = new VoidChunkGenerator();
    public static final boolean isBorderSupported = Utils.findMethod(Bukkit.class, "createWorldBorder");// Is virtual border supported?;
    public static final boolean legacy = !XMaterial.supports(13);// Is version 1.13 supported?
    public static final boolean superlegacy = !XMaterial.supports(9);// Is version 1.9 supported?
    
    public static ConfigManager configManager = new ConfigManager();
    
    public static int x = 0, y = 0, z = 0, offset = 100, max_players_team = 0;
    public static boolean island_for_new_players = false, rebirth = false, autojoin = false;
    public static boolean droptossup = true, physics = false;
    public static boolean lvl_bar_mode = false, particle = true;
    public static boolean allow_nether = true, protection = false;
    public static boolean saveplayerinventory = false;
    public static boolean Border = false;
    public static boolean CircleMode = true;
    public static boolean UseEmptyIslands = true;
    public static boolean Progress_bar = false;
    public static String TextP = "";
    
    public static YamlConfiguration config;
    
    public final String version = getDescription().getVersion();
    public OBWorldGuard OBWG = new OBWorldGuard();
    public Place.Type placetype = Place.Type.basic;
    private Place placer;
    
    World wor, leavewor;
    boolean enabled = false;
    boolean PAPI = false;
    
    public ArrayList <Object> blocks = new ArrayList<>();
    public ArrayList <EntityType> mobs = new ArrayList<>();
    public ArrayList <XMaterial> flowers = new ArrayList<>();
    public PlayerCache cache = new PlayerCache();
    
    public final static World getWorld() { return plugin.wor; }
    public boolean isPAPIEnabled() { return PAPI; }
    public int[] getIslandCoordinates(final int id) { return IslandCoordinateCalculator.getById(id, x, z, offset, CircleMode); }
    public int findNearestRegionId(final Location loc) { return IslandCoordinateCalculator.findNearestRegionId(loc); }
    
    @Override
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {return GenVoid;}
    
    public static String getBarTitle(Player p, int lvl) {
		if (lvl_bar_mode) return Level.get(lvl).name;
		if (plugin.PAPI) return PlaceholderAPI.setPlaceholders(p, TextP);
        
		return TextP;
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
        pluginManager.registerEvents(new BlockEvent(), this);
        pluginManager.registerEvents(new GUIListener(), this);
        pluginManager.registerEvents(new TeleportNetherEvent(), this);
        if (placetype == Place.Type.ItemsAdder) pluginManager.registerEvents(new ItemsAdderEvent(), this);
        getCommand("oneblock").setExecutor(new CommandHandler());
        getCommand("oneblock").setTabCompleter(new CommandTabCompleter());
        
        if (config.getDouble("y") == 0) return;
        
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
    	OBWG.ReCreateRegions();
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
            if (wor == null) {
            	getLogger().info("Waiting for the initialization of the world");
            	getLogger().info("Trying to initialize the world again...");
                wor = Bukkit.getWorld(config.getString("world"));
                leavewor = Bukkit.getWorld(config.getString("leaveworld"));
            } else {
            	getLogger().info("The initialization of the world was successful!");
            	runMainTask();
            	reload();
            }
        }
    }
    
    public void runMainTask() {
    	Bukkit.getScheduler().cancelTasks(this);
		if (config.getDouble("y") == 0) return;
		Bukkit.getScheduler().runTaskTimerAsynchronously(this, new TaskUpdatePlayers(), 0, 120);
		Bukkit.getScheduler().runTaskTimerAsynchronously(this, new TaskSaveData(), 200, 6000);
		if (!superlegacy) Bukkit.getScheduler().runTaskTimerAsynchronously(this, new TaskParticle(), 40, 40);
		Bukkit.getScheduler().runTaskTimer(this, new Task(), 40, 80);
		enabled = true;
		
    	if (OBWorldGuard.canUse && Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
        	getLogger().info("WorldGuard has been found!");
        	OBWG = legacy ? new OBWorldGuard6() : new OBWorldGuard7();
        	OBWG.ReCreateRegions();
        }
        else OBWorldGuard.setEnabled(false);
    }

	public class TaskUpdatePlayers implements Runnable {
		public void run() { cache.updateCache(wor.getPlayers()); }
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
	            double baseY = y + 0.5;

	            for (double[] offset : PARTICLE_OFFSETS) {
	                Location loc = new Location(wor, X_pl + offset[0], baseY, Z_pl + offset[1]);
	                wor.spawnParticle(Particle.PORTAL, loc, 5, 0, 0, 0, 0);
	            }
	        }
	    }
	}

    public class Task implements Runnable {
        public void run() { // SubBlockGen
            for (Player player : cache.getPlayers()) {
            	if (player.getWorld() != wor) continue;
            	final UUID uuid = player.getUniqueId();
            	final int result[] = cache.getIslandCoordinates(player);
                final int X_pl = result[0], Z_pl = result[1], plID = result[2];
            	
                if (protection && !player.hasPermission("Oneblock.ignoreBarrier")) {
                	boolean CheckGuest = false;
                	Location loc = player.getLocation();
            		PlayerInfo inf = Guest.getPlayerInfo(uuid);
            		if (inf != null) {
                    	int crd[] = getIslandCoordinates(PlayerInfo.GetId(inf.uuid));
                        CheckGuest = isWithinIslandBounds(loc, crd[0], crd[1]);
                        if (!CheckGuest) Guest.remove(uuid);
            		}
            		if (!isWithinIslandBounds(loc, X_pl, Z_pl) && !CheckGuest) {
            			player.performCommand("ob j");
            			player.sendMessage(Messages.protection);
                    	continue;
                    }
                }
                
                final Block block = wor.getBlockAt(X_pl, y, Z_pl);
                if (block.getType() != Material.AIR) continue;
                if (PlayerInfo.GetId(uuid) == -1) continue;
                
                BlockGen(X_pl, Z_pl, plID, player, block);
            }
        }
    }
    
    public void BlockGen(final int X_pl, final int Z_pl, final int plID, final Player ponl, final Block block) {
    	final PlayerInfo inf = PlayerInfo.get(plID);
    	Level lvl_inf = Level.get(inf.lvl); 
        if (++inf.breaks >= inf.getNeed()) {
        	lvl_inf = inf.lvlup();
        	if (Progress_bar) inf.createBar();
        	configManager.reward.executeRewards(ponl, inf.lvl, lvl_inf.name);
        }
        if (Progress_bar) {
            inf.bar.setTitle(getBarTitle(ponl, inf.lvl));
            inf.bar.setProgress(inf.getPercent());
            inf.bar.addPlayer(ponl);
        }
        
        Object newblocktype = blocks.get(lvl_inf.blocks == 0 ? 0 : rnd.nextInt(lvl_inf.blocks));
        if (newblocktype == null) {
            XBlock.setType(block, GRASS_BLOCK);
            if (rnd.nextInt(FLOWER_CHANCE) == 1) XBlock.setType(wor.getBlockAt(X_pl, y + 1, Z_pl), flowers.get(rnd.nextInt(flowers.size())));
        }
        else placer.setType(block, newblocktype, physics);

        if (rnd.nextInt(MOB_SPAWN_CHANCE) == 0) spawnRandomMob(X_pl, Z_pl, lvl_inf);
	}
    
	public void spawnRandomMob(int pos_x, int pos_z, Level level) {
		if (level.mobs == 0) return;
		wor.spawnEntity(new Location(wor, pos_x + .5, y + 1, pos_z + .5), mobs.get(rnd.nextInt(level.mobs)));
	}
    
    public void UpdateBorderLocation(Player pl, Location loc) {
    	int plID = findNearestRegionId(loc);
		int result[] = getIslandCoordinates(plID);
        int X_pl = result[0], Z_pl = result[1];
		
		WorldBorder br = Bukkit.createWorldBorder();
    	br.setCenter(X_pl+.5, Z_pl+.5);
    	br.setSize(offset - 1 + (offset & 1));
    	pl.setWorldBorder(br);
    }
    
    public void UpdateBorder(final Player pl) {
    	WorldBorder border = pl.getWorldBorder();
    	Bukkit.getScheduler().runTaskLaterAsynchronously(this, 
    		() -> { pl.setWorldBorder(border); }, 10L);
    }
    
    public void ReloadBorders() {
    	if (!isBorderSupported) return;
    	if (Border) wor.getPlayers().forEach(pl -> plugin.UpdateBorderLocation(pl, pl.getLocation()));
    	else wor.getPlayers().forEach(pl -> pl.setWorldBorder(null));
    }
    
    public boolean isWithinIslandBounds(Location loc, int centerX, int centerZ) {
        int deltaX = loc.getBlockX() - centerX;
        int deltaZ = CircleMode ? loc.getBlockZ() - centerZ : 0;
        int radius = Math.abs(offset >> 1) + 1;
        
        return Math.abs(deltaX) <= radius && Math.abs(deltaZ) <= radius;
    }
    
    public void onDisable() { 
    	DatabaseManager.save(PlayerInfo.list);
    	DatabaseManager.close();
    	JsonSimple.Write(PlayerInfo.list);
    }
    
    public void SaveData() {
    	if (DatabaseManager.save(PlayerInfo.list)) return;
    	JsonSimple.Write(PlayerInfo.list);
    }

    private void Datafile() {
        DatabaseManager.initialize();
        PlayerInfo.list = DatabaseManager.load();
    	
    	if (PlayerInfo.list.size() > 0) {
    		getLogger().info("Player data has been successfully obtained from the " + DatabaseManager.dbType + " database.");
    		return;
    	}
    	
		if (JsonSimple.f.exists())
			PlayerInfo.list = JsonSimple.Read();
		else 
			PlayerInfo.list = ReadOldData.Read();
    }
    
    public void setPosition(Location loc) { setPosition(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()); }
    public void setPosition(World world, int x_, int y_, int z_) { 
    	if (world != null) config.set("world", (wor = world).getName());
        config.set("x", (double) (x = x_));
        config.set("y", (double) (y = y_));
        config.set("z", (double) (z = z_));
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
    
    public static int getlvl(UUID pl_uuid) {
    	return PlayerInfo.get(pl_uuid).lvl;
    }
    public static int getnextlvl(UUID pl_uuid) {
    	return getlvl(pl_uuid) + 1;
    }
    public static String getlvlname(UUID pl_uuid) {
    	int lvl = getlvl(pl_uuid);
    	return Level.get(lvl).name;
    }
    public static String getnextlvlname(UUID pl_uuid) {
    	int lvl = getnextlvl(pl_uuid);
    	return Level.get(lvl).name;
    }
    public static int getblocks(UUID pl_uuid) {
        return PlayerInfo.get(pl_uuid).breaks;
    }
    public static int getneed(UUID pl_uuid) {
    	PlayerInfo inf = PlayerInfo.get(pl_uuid);
    	return inf.getNeed() - inf.breaks;
    }
    public static int getlenght(UUID pl_uuid) {
    	return PlayerInfo.get(pl_uuid).getNeed();
    }
    public static boolean getvisitallowed(OfflinePlayer pl) {
    	Location loc = pl.getLocation();
    	if (loc == null) return false;
    	if (loc.getWorld() != plugin.wor) return false;
    	int id = plugin.findNearestRegionId(loc);
    	if (id < 0 || id >= PlayerInfo.size()) return false;
    	
    	return PlayerInfo.get(id).allow_visit;
    }
    public static int getvisits(UUID pl_uuid) {
    	int count = 0;
    	int reg_id = PlayerInfo.GetId(pl_uuid);
    	if (reg_id != -1)
	    	for (Player ponl: plugin.cache.getPlayers())
	    		if (plugin.findNearestRegionId(ponl.getLocation()) == reg_id)
	    			count++;
    	return count;
    }
    public static PlayerInfo gettop(int i) {
    	if (PlayerInfo.size() <= i) return PlayerInfo.not_found;
    	
    	List<PlayerInfo> sorted = new ArrayList<>(PlayerInfo.list);
    	sorted.sort(PlayerInfo.COMPARE_BY_LVL);
    	return sorted.get(i).uuid == null ? PlayerInfo.not_found : sorted.get(i);
    }
}