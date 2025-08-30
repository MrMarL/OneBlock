// Copyright © 2025 MrMarL. The MIT License (MIT).
package Oneblock;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import com.cryptomorin.xseries.XBlock;
import com.cryptomorin.xseries.XMaterial;
import Oneblock.Invitation.Guest;
import Oneblock.PlData.*;
import Oneblock.UniversalPlace.*;
import Oneblock.Utils.*;
import Oneblock.WorldGuard.*;
import Oneblock.gui.GUI;
import Oneblock.gui.GUIListener;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import me.clip.placeholderapi.PlaceholderAPI;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.meta.SkullMeta;

public class Oneblock extends JavaPlugin {
    public static Oneblock plugin;
    
    public static final Random rnd = new Random(System.currentTimeMillis());
    public static final XMaterial GRASS_BLOCK = XMaterial.GRASS_BLOCK, GRASS = XMaterial.SHORT_GRASS;
    public static final VoidChunkGenerator GenVoid = new VoidChunkGenerator();
    public static final boolean isBorderSupported = Utils.findMethod(Bukkit.class, "createWorldBorder");// Is virtual border supported?;
    public static final boolean legacy = !XMaterial.supports(13);// Is version 1.13 supported?
    public static final boolean superlegacy = !XMaterial.supports(9);// Is version 1.9 supported?
    
    public static ConfigManager configManager = new ConfigManager();
    
    public static int x = 0, y = 0, z = 0, sto = 100, max_players_team = 0;
    public static boolean il3x3 = false, rebirth = false, autojoin = false;
    public static boolean droptossup = true, physics = false;
    public static boolean lvl_bar_mode = false, chat_alert = false, particle = true;
    public static boolean allow_nether = true, protection = false;
    public static boolean saveplayerinventory = false;
    public static boolean WorldGuard = OBWorldGuard.canUse;
    public static boolean Border = true;
    public static boolean CircleMode = true;
    public static boolean UseEmptyIslands = true;
    public static boolean Progress_bar = false;
    public static String TextP = "";
    
    public static YamlConfiguration config;
    
    OBWorldGuard OBWG;
    Place placer;
    Place.Type placetype = Place.Type.basic;
    
    World wor, leavewor;
    boolean enabled = false;
    boolean PAPI = false;
    
    public ArrayList <Object> blocks = new ArrayList<>();
    public ArrayList <EntityType> mobs = new ArrayList<>();
    public ArrayList <XMaterial> flowers = new ArrayList<>();
    public PlayerCache cache = new PlayerCache();
    
    public boolean isPAPIEnabled() { return PAPI; }
    public static int[] getFullCoord(final int id) { return IslandCoordinateCalculator.getById(id, x, z, sto, CircleMode); }
    public static World getWorld() { return plugin.wor; }
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
        getLogger().info(placetype.name());
        
        Datafile();
        configManager.loadConfigFiles();
        setupMetrics(metrics);
        
        pluginManager.registerEvents(new RespawnJoinEvent(), this);
        pluginManager.registerEvents(new TeleportEvent(), this);
        pluginManager.registerEvents(new BlockEvent(), this);
        pluginManager.registerEvents(new GUIListener(), this);
        if (!superlegacy) pluginManager.registerEvents(new ChangedWorld(), this);
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
        return legacy ? Place.Type.legacy : Place.Type.basic;
    }
    
    public void reload() {
    	configManager.loadConfigFiles();
    	ReCreateRegions();
    }
    
    private void setupMetrics(Metrics metrics) {
        metrics.addCustomChart(new SimplePie("premium", () -> String.valueOf(OBWorldGuard.canUse)));
        metrics.addCustomChart(new SimplePie("circle_mode", () -> String.valueOf(CircleMode)));
        metrics.addCustomChart(new SimplePie("use_empty_islands", () -> String.valueOf(UseEmptyIslands)));
        metrics.addCustomChart(new SimplePie("gui", () -> String.valueOf(GUI.enabled)));
        metrics.addCustomChart(new SimplePie("place_type", () -> String.valueOf(placetype)));
    }
    
    public class ItemsAdderEvent implements Listener {
    	@EventHandler
        public void ItemsAdderLoad(ItemsAdderLoadDataEvent event) {
    		configManager.Blockfile();
        }
    }
    
    public class RespawnJoinEvent implements Listener {
    	@EventHandler(priority = EventPriority.HIGHEST)
        public void Respawn(final PlayerRespawnEvent e) {
			if (!rebirth) return;
			Player pl = e.getPlayer();
			if (!pl.getWorld().equals(wor)) return;
			int plID = PlayerInfo.GetId(pl.getUniqueId());
			if (plID == -1) return;
			
			int result[] = getFullCoord(plID);
			Location loc = new Location(wor, result[0] + 0.5, y + 1.2013, result[1] + 0.5);
			e.setRespawnLocation(loc);
        }
        @EventHandler
        public void AutoJoin(final PlayerTeleportEvent e) {
			if (!autojoin)
				return;
			Location loc = e.getTo();
			World from = e.getFrom().getWorld();
			World to = loc.getWorld();
			if (!from.equals(wor) && to.equals(wor) && loc.getY() != y + 1.2013) {
				e.setCancelled(true);
				e.getPlayer().performCommand("ob j");
			}
        }
        @EventHandler
        public void JoinAuto(final PlayerJoinEvent e) {
			Player pl = e.getPlayer();
			if (pl.getWorld().equals(wor)) {
				if (autojoin) pl.performCommand("ob j");
				if (Border) {
					UpdateBorderLocation(pl, pl.getLocation());
					UpdateBorder(pl);
				}
			}
		}
    }
    
    public class TeleportEvent implements Listener {
        @EventHandler
        public void Teleport(final PlayerTeleportEvent e) {
        	if (!Border) return;
        	Location loc = e.getTo();
        	World to = loc.getWorld();
        	Player p = e.getPlayer();
        	
        	if (!to.equals(wor)) {
        		p.setWorldBorder(null);
        		return;
        	}
        	UpdateBorderLocation(p, loc);
        	UpdateBorder(p);
        }
        
        @EventHandler(priority = EventPriority.MONITOR)
        public void Respawn(final PlayerRespawnEvent e) {
			if (!Border) return;
			Location loc = e.getRespawnLocation();
			Player p = e.getPlayer();
			if (wor.equals(loc.getWorld())) {
				UpdateBorderLocation(p, loc);
				UpdateBorder(p);
			}
			else
				p.setWorldBorder(null);
        }
        
        @EventHandler
        public void NetherPortal(final PlayerPortalEvent e) {
        	if (allow_nether) return;
        	World from = e.getFrom().getWorld();
        	if (!from.equals(wor)) return;
        	
        	World to = e.getTo().getWorld();
            if (to.getEnvironment() == World.Environment.NETHER) 
            	e.setCancelled(true);
        }
    }
    
    public class ChangedWorld implements Listener {
    	@EventHandler
        public void PlayerChangedWorldEvent(PlayerChangedWorldEvent e) {
    		if (PlayerInfo.size() == 0) return;
        	if (e.getFrom().equals(wor))
        		PlayerInfo.removeBarStatic(e.getPlayer());
        }
    }
    
    public class BlockEvent implements Listener {
    	@EventHandler(ignoreCancelled = true)
        public void ItemStackSpawn(final EntitySpawnEvent e) {
    		if (!droptossup) return;
    		if (wor == null) return;
            if (!EntityType.DROPPED_ITEM.equals(e.getEntityType())) return;
                
            Location loc = e.getLocation();
            if (!wor.equals(loc.getWorld())) return;
            if (loc.getBlockY() != y) return;
            if ((x - loc.getBlockX()) % sto != 0) return;
            if ((z - loc.getBlockZ()) % sto != 0) return;

            Entity drop = e.getEntity();
            drop.teleport(loc.add(0, .8, 0));
            drop.setVelocity(new Vector(0, .1, 0));
        }
    	@EventHandler
    	public void BlockBreak(final BlockBreakEvent e) {
    		if (wor == null) return;
    		final Block block = e.getBlock();
    		if (block.getWorld() != wor) return;
    		if (block.getY() != y) return;
    		final Player ponl = e.getPlayer();
    		final UUID uuid = ponl.getUniqueId();
        	final int plID = PlayerInfo.GetId(uuid);
        	if (plID == -1) return;
        	final int result[] = getFullCoord(plID);
        	if (block.getX() != result[0]) return;
        	if (block.getZ() != result[1]) return;

            Bukkit.getScheduler().runTaskLater(Oneblock.this, () -> { BlockGen(result[0], result[1], plID, ponl, block); }, 1L);
    	}
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
        	if (legacy) OBWG = new OBWorldGuard6();
			else OBWG = new OBWorldGuard7();
        	ReCreateRegions();
        }
        else WorldGuard = false;
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
	            int[] result = cache.getFullCoord(ponl);
	            if (result == null) continue;
	            int X_pl = result[0], Z_pl = result[1];

	            Arrays.asList(
	                new Location(wor, X_pl, y + .5, Z_pl),
	                new Location(wor, X_pl + 1, y + .5, Z_pl),
	                new Location(wor, X_pl, y + .5, Z_pl + 1),
	                new Location(wor, X_pl + 1, y + .5, Z_pl + 1)
	            ).forEach(loc -> wor.spawnParticle(Particle.PORTAL, loc, 5, 0, 0, 0, 0));
	        }
	    }
	}

    public class Task implements Runnable {
        public void run() { // SubBlockGen
            for (Player ponl: cache.getPlayers()) {
            	if (!ponl.getWorld().equals(wor)) continue;
            	final UUID uuid = ponl.getUniqueId();
            	final int result[] = cache.getFullCoord(ponl);
                final int X_pl = result[0], Z_pl = result[1], plID = result[2];
            	
                if (protection && !ponl.hasPermission("Oneblock.ignoreBarrier")) {
                	boolean CheckGuest = false;
                	Location loc = ponl.getLocation();
            		PlayerInfo inf = Guest.getPlayerInfo(ponl.getUniqueId());
            		if (inf != null) {
                    	int crd[] = getFullCoord(PlayerInfo.GetId(inf.uuid));
                        CheckGuest = CheckPosition(loc, crd[0], crd[1]);
                        if (!CheckGuest) Guest.remove(uuid);
            		}
            		if (!CheckPosition(loc, X_pl, Z_pl) && !CheckGuest) {
                    	ponl.performCommand("ob j");
                		ponl.sendMessage(Messages.protection);
                    	continue;
                    }
                }
                
                final Block block = wor.getBlockAt(X_pl, y, Z_pl);
                if (!block.getType().equals(Material.AIR)) continue;
                if (PlayerInfo.GetId(uuid) == -1) continue;
                
                BlockGen(X_pl, Z_pl, plID, ponl, block);
            }
        }
    }
    
    public void BlockGen(final int X_pl, final int Z_pl, final int plID, final Player ponl, final Block block) {
    	final PlayerInfo inf = PlayerInfo.get(plID);
    	Level lvl_inf = Level.get(inf.lvl); 
        if (++inf.breaks >= inf.getNeed()) {
        	lvl_inf = inf.lvlup();
        	if (Progress_bar) inf.bar.setColor(lvl_inf.color);
        	if (chat_alert) ponl.sendMessage(ChatColor.GREEN + lvl_inf.name);
        }
        if (Progress_bar) {
            inf.bar.setTitle(getBarTitle(ponl, inf.lvl));
            inf.bar.setProgress(inf.getPercent());
            inf.bar.addPlayer(ponl);
        }
        
        Object newblocktype = blocks.get(lvl_inf.blocks == 0 ? 0 : rnd.nextInt(lvl_inf.blocks));
        if (newblocktype == null) {
            XBlock.setType(block, GRASS_BLOCK);
            if (rnd.nextInt(3) == 1) XBlock.setType(wor.getBlockAt(X_pl, y + 1, Z_pl), flowers.get(rnd.nextInt(flowers.size())));
        }
        else placer.setType(block, newblocktype, physics);

        if (rnd.nextInt(9) == 0) spawnRandomMob(X_pl, Z_pl, lvl_inf);
	}
    
	public void spawnRandomMob(int pos_x, int pos_z, Level level) {
		if (level.mobs == 0) return;
		wor.spawnEntity(new Location(wor, pos_x + .5, y + 1, pos_z + .5), mobs.get(rnd.nextInt(level.mobs)));
	}
    
    public int findNeastRegionId(Location loc) {
    	int id_ = 0, neast = Integer.MAX_VALUE;
    	
    	for (int i = 0; i < PlayerInfo.size() ;i++) {
    		int coord[] = getFullCoord(i);
            int distance = (int)Math.sqrt(Math.pow(coord[0] - loc.getBlockX(), 2) + Math.pow(coord[1] - loc.getBlockZ(), 2));
            if (distance > neast) continue;
            
            neast = distance;
            id_ = i;
    	}
    	return id_;
	}
    
	public void ReCreateRegions() {
		if (!WorldGuard || !OBWorldGuard.canUse || OBWG == null) return;
		
		int id = PlayerInfo.size();
		OBWG.RemoveRegions(id);
    	
		for (int i = 0; i < id; i++) {
			PlayerInfo owner = PlayerInfo.get(i);
			if (owner.uuid == null) continue;
			
			int pos[] = getFullCoord(i);
			OBWG.CreateRegion(owner.uuid, pos[0], pos[1], sto, i);
			for (UUID member: owner.uuids) 
				OBWG.addMember(member, i);
		}
	}
    
    public void UpdateBorderLocation(Player pl, Location loc) {
    	int plID = findNeastRegionId(loc);
		int result[] = getFullCoord(plID);
        int X_pl = result[0], Z_pl = result[1];
		
		WorldBorder br = Bukkit.createWorldBorder();
    	br.setCenter(X_pl+.5, Z_pl+.5);
    	br.setSize(sto);
    	pl.setWorldBorder(br);
    }
    
    public void UpdateBorder(final Player pl) {
    	WorldBorder border = pl.getWorldBorder();
    	Bukkit.getScheduler().runTaskLaterAsynchronously(this, 
    		() -> { pl.setWorldBorder(border); }, 10L);
    }
    
    public void ReloadBorders() {
    	if (Border) getWorld().getPlayers().forEach(pl -> plugin.UpdateBorderLocation(pl, pl.getLocation()));
    	else getWorld().getPlayers().forEach(pl -> pl.setWorldBorder(null));
    }
    
    public boolean CheckPosition(Location loc, int X_pl, int Z_pl) {
    	X_pl = loc.getBlockX()-X_pl;
    	Z_pl = CircleMode ? loc.getBlockZ()-Z_pl : 0;
    	int val = Math.abs(sto/2) + 1;
    	return (Math.abs(X_pl) <= val && Math.abs(Z_pl) <= val);
    }
    
    public void onDisable() { SaveData(); }
    
    public void SaveData() {
    	File PlData = new File(getDataFolder(), "PlData.json");
    	JsonSimple.Write(PlayerInfo.list, PlData);
    }

    private void Datafile() {
    	File PlData = new File(getDataFolder(), "PlData.json");
		if (PlData.exists())
			PlayerInfo.list = JsonSimple.Read(PlData);
		else
			PlayerInfo.list = ReadOldData.Read(new File(getDataFolder(), "PlData.yml"));
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
    	if (!loc.getWorld().equals(plugin.wor)) return false;
    	int id = plugin.findNeastRegionId(loc);
    	if (id < 0 || id >= PlayerInfo.size()) return false;
    	
    	return PlayerInfo.get(id).allow_visit;
    }
    public static int getvisits(UUID pl_uuid) {
    	int count = 0;
    	int reg_id = PlayerInfo.GetId(pl_uuid);
    	if (reg_id != -1)
	    	for (Player ponl: plugin.cache.getPlayers())
	    		if (plugin.findNeastRegionId(ponl.getLocation()) == reg_id)
	    			count++;
    	return count;
    }
    public static PlayerInfo gettop(int i) {
    	if (PlayerInfo.size() <= i) return new PlayerInfo(null);
    	
    	List<PlayerInfo> sorted = new ArrayList<>(PlayerInfo.list);
    	sorted.sort(PlayerInfo.COMPARE_BY_LVL);
    	return sorted.get(i).uuid == null ? new PlayerInfo(null) : sorted.get(i);
    }
}