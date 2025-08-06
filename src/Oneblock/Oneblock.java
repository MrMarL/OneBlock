// Copyright © 2025 MrMarL. The MIT License (MIT).
package Oneblock;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import com.cryptomorin.xseries.XBlock;
import com.cryptomorin.xseries.XMaterial;
import com.nexomc.nexo.api.NexoBlocks;

import Oneblock.Invitation.Guest;
import Oneblock.Invitation.Invitation;
import Oneblock.PlData.*;
import Oneblock.UniversalPlace.*;
import Oneblock.WorldGuard.*;
import Oneblock.gui.GUI;
import Oneblock.gui.GUIListener;
import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import io.th0rgal.oraxen.api.OraxenItems;
import me.clip.placeholderapi.PlaceholderAPI;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
import org.bukkit.boss.BarColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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

public class Oneblock extends JavaPlugin {
    public static Oneblock plugin;
    
    final Random rnd = new Random(System.currentTimeMillis());
    final XMaterial GRASS_BLOCK = XMaterial.GRASS_BLOCK, GRASS = XMaterial.SHORT_GRASS;
    final VoidChunkGenerator GenVoid = new VoidChunkGenerator();
    Place placer;
    Place.Type placetype = Place.Type.basic;
    OBWorldGuard OBWG;
    YamlConfiguration config, config_temp;
    boolean on = false;
    boolean superlegacy, legacy;
    
    World wor, leavewor;
    int x = 0, y = 0, z = 0, sto = 100, max_players_team = 0;
    boolean il3x3 = false, rebirth = false, autojoin = false;
    boolean droptossup = true, physics = false;
    boolean lvl_bar_mode = false, chat_alert = false, particle = true;
    boolean allow_nether = true, protection = false;
    boolean saveplayerinventory = false;
    boolean PAPI = false;
    boolean WorldGuard = OBWorldGuard.canUse;
    boolean Border = true;
    boolean CircleMode = true;
    boolean UseEmptyIslands = true;
    boolean Progress_bar = false;
    BarColor Progress_color;
    String TextP = "";
    
    ArrayList <Object> blocks = new ArrayList<>();
    ArrayList <EntityType> mobs = new ArrayList<>();
    ArrayList <XMaterial> flowers = new ArrayList<>();
    public PlayerCache cache = new PlayerCache();
    
    public World getWorld() { return plugin.wor; }
    public boolean isPAPIEnabled() { return PAPI; }
    public boolean isProgressBarEnabled() { return Progress_bar; }
    
	public int[] getFullCoord(final int id) { return IslandCoordinateCalculator.getById(id, x, z, sto, CircleMode); }
    
	public String getBarTitle(Player p, int lvl) {
		if (lvl_bar_mode) return Level.get(lvl).name;
		if (PAPI) return PlaceholderAPI.setPlaceholders(p, TextP);
        
		return TextP;
	}
    
	@Override
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {return GenVoid;}
	
	public boolean findMethod(final Class<?> cl, String name) {
		return Arrays.stream(cl.getMethods()).anyMatch(m -> m.getName().equals(name));
	}
    
    @Override
    public void onEnable() {
    	plugin = this;
        superlegacy = !XMaterial.supports(9);// Is version 1.9 supported?
        legacy = !XMaterial.supports(13);// Is version 1.13 supported?
        Border = findMethod(Bukkit.class, "createWorldBorder");// Is virtual border supported?
        final Metrics metrics = new Metrics(this, 14477);
        final PluginManager pluginManager = Bukkit.getPluginManager();
        
        getLogger().info(
        		  "\n┏━┓····┏━━┓·····┏┓\n"
        		  + "┃┃┣━┳┳━┫┏┓┣┓┏━┳━┫┣┓\n"
        		  + "┃┃┃┃┃┃┻┫┏┓┃┗┫╋┃━┫━┫\n"
        		  + "┗━┻┻━┻━┻━━┻━┻━┻━┻┻┛ by MrMarL");
        if (PAPI = pluginManager.isPluginEnabled("PlaceholderAPI")) {
        	getLogger().info("PlaceholderAPI has been found!");
            new OBP().register();
        }
        
        placetype = determinePlaceType(pluginManager);
        placer = Place.GetPlacerByType(placetype);
        getLogger().info(placetype.name());
        
        Datafile();
        loadConfigFiles();
        setupMetrics(metrics);
        
        pluginManager.registerEvents(new RespawnJoinEvent(), this);
        pluginManager.registerEvents(new TeleportEvent(), this);
        pluginManager.registerEvents(new BlockEvent(), this);
        pluginManager.registerEvents(new GUIListener(), this);
        if (!superlegacy) pluginManager.registerEvents(new ChangedWorld(), this);
        if (placetype == Place.Type.ItemsAdder) pluginManager.registerEvents(new ItemsAdderEvent(), this);
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
    
    private void loadConfigFiles() {
        Configfile();
        Chestfile();
        Blockfile();
        Flowerfile();
        Messagefile();
    }
    
    private void reload() {
    	loadConfigFiles();
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
    		Blockfile();
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
		on = true;
		
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
    
	private void ReCreateRegions() {
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
    
    private void SetupProgressBar() {
		if (superlegacy) return;
		if (PlayerInfo.size() == 0) return;
		
		if (Progress_color == null)
			Progress_color = BarColor.GREEN;
		
		Level.max.color = Progress_color;
		PlayerInfo.list.forEach(inf -> {if (inf.uuid != null){
			Player p = Bukkit.getPlayer(inf.uuid);
			if (p == null)
				inf.createBar();
			else
				inf.createBar(getBarTitle(p, inf.lvl));
        	        	
			inf.bar.setVisible(Progress_bar);
        }});
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
    
    public boolean CheckPosition(Location loc, int X_pl, int Z_pl) {
    	X_pl = loc.getBlockX()-X_pl;
    	Z_pl = CircleMode ? loc.getBlockZ()-Z_pl : 0;
    	int val = Math.abs(sto/2) + 1;
    	return (Math.abs(X_pl) <= val && Math.abs(Z_pl) <= val);
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	if (!cmd.getName().equalsIgnoreCase("oneblock")) return false;
        if (args.length == 0) return ((Player)sender).performCommand("ob j");
        
        if (!sender.hasPermission("Oneblock.join")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission [Oneblock.join].");
            return true;
        }
        
        String parametr = args[0].toLowerCase();
        switch (parametr) 
        {
	        case ("j"):
	        case ("join"):{
	            if (y == 0 || wor == null) {
	            	sender.sendMessage(ChatColor.YELLOW + "First you need to set the reference coordinates '/ob set'.");
	            	return true;
	            }
	            Player p = (Player) sender;
	            UUID uuid = p.getUniqueId();
	            int X_pl = 0, Z_pl = 0;
	            int plID = PlayerInfo.GetId(uuid);
	            if (plID == -1) {
	            	PlayerInfo inf = new PlayerInfo(uuid);
	            	plID = PlayerInfo.getFreeId(UseEmptyIslands);
	            	int result[] = getFullCoord(plID);
	            	X_pl = result[0]; Z_pl = result[1];
	            	if (plID != PlayerInfo.size())
	            		Island.clear(wor, X_pl, y, Z_pl, sto/4);
	            	XBlock.setType(wor.getBlockAt(X_pl, y, Z_pl), XMaterial.GRASS_BLOCK);
	                if (il3x3)
	                	Island.place(wor, X_pl, y, Z_pl);
	                if (WorldGuard) 
	                	OBWG.CreateRegion(uuid, X_pl, Z_pl, sto, plID);
					PlayerInfo.set(plID, inf);
					if (!superlegacy)
						inf.createBar(getBarTitle(p, 0));
	            } 
	            else {
	            	int result[] = getFullCoord(plID);
	                X_pl = result[0]; Z_pl = result[1];
	            }
	            if (!on) runMainTask();
	            if (Progress_bar) PlayerInfo.get(plID).bar.setVisible(true);
	            p.teleport(new Location(wor, X_pl + 0.5, y + 1.2013, Z_pl + 0.5));
	            if (WorldGuard) OBWG.addMember(uuid, plID);
	            return true;
	        }
	        case ("leave"):{
	            Player p = (Player) sender;
	            PlayerInfo.removeBarStatic(p);
	            if (config.getDouble("yleave") == 0 || leavewor == null) {
	            	if (!args[args.length-1].equals("/n"))
	            		sender.sendMessage(Messages.leave_not_set);
	            	return true;
	            }
	            p.teleport(new Location(leavewor, config.getDouble("xleave"), config.getDouble("yleave"), config.getDouble("zleave"),
	            		(float)config.getDouble("yawleave"), 0f));
	            return true;
	        }
	        case ("visit"):{
	        	if (!sender.hasPermission("Oneblock.visit")) {
	                sender.sendMessage(Messages.noperm_inv);
	                return true;
	            }
	        	if (OBWG == null || !WorldGuard) {
	                sender.sendMessage(ChatColor.YELLOW + "This feature is only available when worldguard is enabled.");
	                return true;
	            }
	        	Player pl = (Player) sender;
	            if (args.length < 2) {
	        		GUI.visitGUI(pl, Bukkit.getOfflinePlayers());
	        		return true;
	        	}
	            OfflinePlayer inv = Bukkit.getOfflinePlayer(args[1]);
	        	if (inv == null) return true;
	    		if (inv == pl) {
	    			pl.performCommand("ob j");
	    			return true;
	    		}
	    		UUID uuid = inv.getUniqueId();
	    		final int plID = PlayerInfo.GetId(uuid);
	    		if (plID == -1) {
	    			sender.sendMessage(Messages.invite_no_island);
	    			return true;
	    		}
	    		PlayerInfo pinf = PlayerInfo.get(uuid);
	    		if (!pinf.allow_visit) {
	    			sender.sendMessage(Messages.not_allow_visit);
	    			return true;
	    		}
	        	final int result[] = getFullCoord(plID);
	            final int X_pl = result[0], Z_pl = result[1];
	    		
	            if (protection) Guest.list.add(new Guest(uuid, pl.getUniqueId()));
	            pl.teleport(new Location(wor, X_pl + 0.5, y + 1.2013, Z_pl + 0.5));
	    		PlayerInfo.removeBarStatic(pl);
	            return true;
	        }
	        case ("allow_visit"):{
	        	if (!sender.hasPermission("Oneblock.visit")) {
	                sender.sendMessage(Messages.noperm_inv);
	                return true;
	            }
	        	Player pl = (Player) sender;
	        	UUID uuid = pl.getUniqueId();
	        	if (PlayerInfo.GetId(uuid) == -1) return true;
	        	PlayerInfo inf = PlayerInfo.get(uuid);
	        	inf.allow_visit = !inf.allow_visit;
	        	pl.sendMessage(inf.allow_visit ? Messages.allowed_visit : Messages.forbidden_visit);
	        	return true;
	        }
	        case ("invite"):{
	        	if (!sender.hasPermission("Oneblock.invite")) {
	                sender.sendMessage(Messages.noperm_inv);
	                return true;
	            }
	        	if (args.length < 2) {
	        		sender.sendMessage(Messages.invite_usage);
	        		return true;
	        	}
	        	Player inv = Bukkit.getPlayer(args[1]);
	        	if (inv == null) return true;
	        	Player pl = (Player) sender;
	    		if (inv == pl) {
	    			sender.sendMessage(Messages.invite_yourself);
	    			return true;
	    		}
	    		UUID uuid = pl.getUniqueId();
	    		if (PlayerInfo.GetId(uuid) == -1) {
	    			sender.sendMessage(Messages.invite_no_island);
	    			return true;
	    		}
	    		if (max_players_team != 0) {
	    			PlayerInfo pinf = PlayerInfo.get(uuid);
	    			if (pinf.uuids.size() >= max_players_team) {
	        			sender.sendMessage(String.format(Messages.invite_team, max_players_team));
	        			return true;
	    			}
	    		}
	    		Invitation.add(uuid, inv.getUniqueId());
	    		String name = pl.getName();
	    		GUI.acceptGUI(inv, name);
	    		inv.sendMessage(String.format(Messages.invited, name));
	    		sender.sendMessage(String.format(Messages.invited_succes, inv.getName()));
	        	return true;
	        }
	        case ("kick"):{
	        	if (args.length < 2) {
	        		sender.sendMessage(Messages.kick_usage);
	        		return true;
	        	}
	        	OfflinePlayer member = Bukkit.getOfflinePlayer(args[1]);
	        	if (member == null) return true;
	        	Player owner = (Player) sender;
	        	if (member == owner) {
	        		sender.sendMessage(Messages.kick_yourself);
	        		return true;
	        	}
	        	UUID owner_uuid = owner.getUniqueId(), member_uuid = member.getUniqueId();
	        	if (!PlayerInfo.ExistNoInvaitId(owner_uuid))
	        		return true;
	        	int ownerID = PlayerInfo.GetId(owner_uuid);
	        	PlayerInfo info = PlayerInfo.get(ownerID);
	        	if (info.uuids.contains(member_uuid)) {
	        		info.uuids.remove(member_uuid);
	        		if (WorldGuard)
	    				OBWG.removeMember(member_uuid, ownerID);
	        	}
	        	if (!(member instanceof Player)) return true;
	        	Player member_ex = (Player) member;
	        	int memberID = findNeastRegionId(member_ex.getLocation());
	        	if (memberID == ownerID) {
	        		if (!member_ex.hasPermission("Oneblock.set"))
	        			member_ex.performCommand("ob j");
	        		info.removeBar(member_ex);
	        		sender.sendMessage(member.getName() + Messages.kicked);
	        	}
	        	return true;
	        }
	        case ("accept"):{
	        	Player pl = (Player) sender;
	       	 	if (Invitation.check(pl))
	       	 		sender.sendMessage(Messages.accept_succes);
	       	 	else
	       	 		sender.sendMessage(Messages.accept_none);
	       		return true;
	        }
	        case ("idreset"):{
	        	Player pl = (Player)sender;
	        	UUID uuid = pl.getUniqueId();
	        	int PlId = PlayerInfo.GetId(uuid);
	        	if (PlId == -1) return true;
	        	PlayerInfo plp = PlayerInfo.get(PlId);
	        	plp.removeBar(pl);
	        	plp.removeUUID(uuid);
	        	
	        	if (!saveplayerinventory) pl.getInventory().clear();
	        		
	        	if (WorldGuard) 
	        		OBWG.removeMember(uuid, PlId);
	        	if (!args[args.length-1].equals("/n"))
	        		sender.sendMessage(Messages.idreset);
	        	pl.performCommand("ob leave /n");
	        	return true;
	        }
	        case ("top"):{
	        	GUI.topGUI((Player) sender);
	        	return true;
	        }
	        case ("help"):{
	        	sender.sendMessage(sender.hasPermission("Oneblock.set") ? Messages.help_adm:Messages.help);
	        	return true;
	        }
	        case ("gui"):{
	        	if (args.length == 1) {
	        		GUI.openGUI((Player) sender);
	        		return true;
	        	} //else admin commands
	        }
	        default: {//admin commands
	        	if (!sender.hasPermission("Oneblock.set"))
	                sender.sendMessage(Messages.noperm);
	        	else 
		        {
	        		config = YamlConfiguration.loadConfiguration(LegacyConfigSaver.file); // Loading the config.yml file before making changes.
	        		Bukkit.getScheduler().runTaskLater(Oneblock.this, () -> { LegacyConfigSaver.Save(config); }, 1L); // Saving the config.yml file after making changes.
		        	switch (parametr) {
			            case ("set"):{
			                Player p = (Player) sender;
			                Location l = p.getLocation();
			                x = l.getBlockX();
			                y = l.getBlockY();
			                z = l.getBlockZ();
			                wor = l.getWorld();
			                int temp = 100;
			                if (args.length >= 2) {
			                    try {
			                    	temp = Integer.parseInt(args[1]);
			                    } catch (NumberFormatException nfe) {
			                    	sender.sendMessage(Messages.invalid_value);
			                    	return true;
			                    }
			                    if (temp > 1000 || temp < -1000) {
			                    	sender.sendMessage(String.format("%spossible values are from -1000 to 1000", ChatColor.RED));
			                    	return true;
			                    }
			                    sto = temp;
			                    config.set("set", sto);
			                }
			                config.set("world", wor.getName());
			                config.set("x", (double) x);
			                config.set("y", (double) y);
			                config.set("z", (double) z);
			                if (!on) runMainTask();
			                wor.getBlockAt(x, y, z).setType(GRASS_BLOCK.parseMaterial());
			                ReCreateRegions();
			                LegacyConfigSaver.Save(config);
			                return true;
			            }
			            case ("setleave"):{
			                Player p = (Player) sender;
			                Location l = p.getLocation();
			                leavewor = l.getWorld();
			                config.set("leaveworld", leavewor.getName());
			                config.set("xleave", l.getX());
			                config.set("yleave", l.getY());
			                config.set("zleave", l.getZ());
			                config.set("yawleave", l.getYaw());
			                return true;
			            }
			            case ("worldguard"):{
			            	if (!Bukkit.getPluginManager().isPluginEnabled("WorldGuard")){
			                    sender.sendMessage(String.format("%sThe WorldGuard plugin was not detected!", ChatColor.YELLOW));
			                    return true;
			                }
			            	if (OBWG == null || !OBWorldGuard.canUse) {
			                    sender.sendMessage(String.format("%sThis feature is only available in the premium version of the plugin!", ChatColor.YELLOW));
			                    return true;
			                }
			            	if (args.length > 1 &&
			                	(args[1].equals("true") || args[1].equals("false"))) {
			                    	WorldGuard = Boolean.valueOf(args[1]);
			                    	config.set("WorldGuard", WorldGuard);
			                    	if (WorldGuard)
			                    		ReCreateRegions();
			                    	else
			                    		OBWG.RemoveRegions(PlayerInfo.size());
			                }
			                else sender.sendMessage(Messages.bool_format);
			            	sender.sendMessage(String.format("%sthe OBWorldGuard is now %s", ChatColor.GREEN, (WorldGuard?"enabled.":"disabled.")));
			           		return true;
			            }
			            case ("border"):{
			            	if (!findMethod(Bukkit.class, "createWorldBorder")){
			                    sender.sendMessage(String.format("%sThe border can only be used on version 1.18.2 and above!", ChatColor.YELLOW));
			                    return true;
			                }
			            	if (args.length > 1 &&
			                	(args[1].equals("true") || args[1].equals("false"))) {
			            			Border = Boolean.valueOf(args[1]);
			                    	config.set("Border", Border);
			                    	if (Border) 
			                    		wor.getPlayers().forEach(pl -> UpdateBorderLocation(pl, pl.getLocation()));
			                    	else wor.getPlayers().forEach(pl -> pl.setWorldBorder(null));
			                }
			                else sender.sendMessage(Messages.bool_format);
			            	sender.sendMessage(String.format("%sthe Border is now %s", ChatColor.GREEN, (Border?"enabled.":"disabled.")));
			           		return true;
			            }
			            case ("circlemode"):
			            case ("useemptyislands"):
			            case ("protection"):
			            case ("droptossup"):
			            case ("physics"):
			            case ("autojoin"):
			            case ("particle"):
			            case ("allow_nether"):
			            case ("saveplayerinventory"):
			            case ("gui"):
			            case ("chat_alert"):
			            case ("rebirth_on_the_island"):{
			            	if (args.length > 1 &&
			                    	(args[1].equals("true") || args[1].equals("false"))) {
			                    	config.set(parametr, Boolean.valueOf(args[1]));
			                    	UpdateParametrs();
			                }
			                else sender.sendMessage(Messages.bool_format);
			                sender.sendMessage(String.format("%s%s is now %s", ChatColor.GREEN, parametr, (config.getBoolean(parametr)?"enabled.":"disabled.")));
			           		return true;
			            }
			            //LVL
			            case ("setlevel"):{
			                if (args.length <= 2) {
			                    sender.sendMessage(String.format("%sinvalid format. try: /ob setlevel 'nickname' 'level'", ChatColor.RED));
			                    return true;
			                }
			                OfflinePlayer offpl = Bukkit.getOfflinePlayer(args[1]);
			                UUID uuid = offpl.getUniqueId();
			                int plID = PlayerInfo.GetId(uuid);
			                if (plID != -1) {
			                    int setlvl = 0;
			                    try {
			                        setlvl = Integer.parseInt(args[2]);
			                    } catch (NumberFormatException nfe) {
			                        sender.sendMessage(String.format("%sinvalid level value.", ChatColor.RED));
			                        return true;
			                    }
			                    if (setlvl >= 0 && 10000 > setlvl) {
			                        PlayerInfo inf = PlayerInfo.get(plID);
			                        inf.breaks = 0;
			                        inf.lvl = setlvl;
			                        if (Progress_bar && offpl instanceof Player) {
			                        	inf.createBar(getBarTitle((Player) offpl, inf.lvl));
		                                inf.bar.setProgress(inf.getPercent());
		                            }
			                        sender.sendMessage(String.format("%sfor player %s, level %s is set.", ChatColor.GREEN, args[1], args[2]));
			                        return true;
			                    }
			                    sender.sendMessage(String.format("%sinvalid level value.", ChatColor.RED));
			                    return true;
			                }
			                sender.sendMessage(String.format("%sa player named %s was not found.", ChatColor.RED, args[1]));
			                return true;
			            }
			            case ("clear"):{
			                if (args.length <= 1) {
			                    sender.sendMessage(String.format("%sinvalid format. try: /ob clear 'nickname'", ChatColor.RED));
			                    return true;
			                }
			                UUID uuid = Bukkit.getOfflinePlayer(args[1]).getUniqueId();
			                if (PlayerInfo.GetId(uuid) != -1) {
			                    int i = PlayerInfo.GetId(uuid);
			                    PlayerInfo inf = PlayerInfo.get(i);
			                    inf.breaks = 0;
			                    inf.lvl = 0;
			                    if (Progress_bar)
			                    	inf.bar.setVisible(false);
			                    int result[] = getFullCoord(i);
			                    Island.clear(wor, result[0], y, result[1], sto/4);
			                    sender.sendMessage(String.format("%splayer %s island is destroyed! :D", ChatColor.GREEN, args[1]));
			                    return true;
			                }
			                sender.sendMessage(String.format("%sa player named %s was not found.", ChatColor.RED, args[1]));
			                return true;
			            }
			            case ("lvl_mult"):{
			                if (args.length <= 1) {
			                    sender.sendMessage(String.format("%slevel multiplier now: %d\n5 by default", ChatColor.GREEN, Level.multiplier));
			                    return true;
			                }
			                int lvl = Level.multiplier;
			                try {
			                    lvl = Integer.parseInt(args[1]);
			                } catch (NumberFormatException nfe) {
			                    sender.sendMessage(String.format("%sinvalid multiplier value.", ChatColor.RED));
			                    return true;
			                }
			                if (lvl <= 20 && lvl >= 0) {
			                	Level.multiplier = lvl;
			                    config.set("level_multiplier", Level.multiplier);
			                    Blockfile();
			                } else
			                    sender.sendMessage(String.format("%spossible values: from 0 to 20.", ChatColor.RED));
			                sender.sendMessage(String.format("%slevel multiplier now: %d\n5 by default", ChatColor.GREEN, Level.multiplier));
			                return true;
			            }
			            case ("max_players_team"):{
			                if (args.length <= 1) {
			                    sender.sendMessage(String.format("%smax_players_team now: %d\n5 by default", ChatColor.GREEN, max_players_team));
			                    return true;
			                }
			                int mpt = max_players_team;
			                try {
			                	mpt = Integer.parseInt(args[1]);
			                } catch (NumberFormatException nfe) {
			                    sender.sendMessage(String.format("%sinvalid max_players_team value.", ChatColor.RED));
			                    return true;
			                }
			                if (mpt <= 20 && mpt >= 0) 
			                    config.set("max_players_team", max_players_team = mpt);
			                else
			                    sender.sendMessage(String.format("%spossible values: from 0 to 20.", ChatColor.RED));
			                sender.sendMessage(String.format("%smax_players_team now: %d", ChatColor.GREEN, max_players_team));
			                return true;
			            }
			            case ("progress_bar"):{
			                if (superlegacy) {
			                    sender.sendMessage(String.format("%sYou server version is super legacy! ProgressBar unsupported!", ChatColor.RED));
			                    return true;
			                }
			                if (args.length == 1) {
			                    sender.sendMessage(String.format("%sand?", ChatColor.YELLOW));
			                    return true;
			                }
			                if (args[1].equals("true") || args[1].equals("false")) {
			                    Progress_bar = Boolean.valueOf(args[1]);
			                    Blockfile();
			                    config.set("Progress_bar", Progress_bar);
			                    return true;
			                }
			                
			                if (!Progress_bar) return true;
			                
			                if (args[1].equalsIgnoreCase("color")) {
			                    if (args.length == 2) {
			                        sender.sendMessage(String.format("%senter a color name.", ChatColor.YELLOW));
			                        return true;
			                    }
			                    try {
			                        Progress_color = BarColor.valueOf(args[2]);
			                        Blockfile();
			                        config.set("Progress_bar_color", Progress_color.toString());
			                    } catch (Exception e) {
			                        sender.sendMessage(String.format("%sPlease enter a valid color. For example: RED", ChatColor.YELLOW));
			                    }
			                    sender.sendMessage(String.format("%sProgress bar color = %s", ChatColor.GREEN, Progress_color.toString()));
			                    return true;
			                }
			                if (args[1].equalsIgnoreCase("level")) {
			                	lvl_bar_mode = true;
			                    config.set("Progress_bar_text", "level");
			                    SetupProgressBar();
			                    return true;
			                }
			                if (args[1].equalsIgnoreCase("settext")) {
			                    String txt_bar = "";
								for (int i = 2; i < args.length; i++)
									txt_bar = i == 2 ? args[i] : String.format("%s %s", txt_bar, args[i]);
			                    lvl_bar_mode = false;
			                    config.set("Progress_bar_text", TextP = txt_bar);
			                    SetupProgressBar();
			                    return true;
			                }
			                sender.sendMessage(String.format("%strue, false, settext or level only!", ChatColor.RED));
			                return true;
			            }
			            case ("listlvl"):{
			                if (args.length >= 2) {
			                	int temp = 0;
			                    try {
			                    	temp = Integer.parseInt(args[1]);
			                    } catch (NumberFormatException nfe) {
			                    	sender.sendMessage(Messages.invalid_value);
			                    	return true;
			                    }
			                    if (Level.size()<=temp||temp<0) {
			                    	sender.sendMessage(String.format("%sundefined lvl", ChatColor.RED));
			                    	return true;
			                    }
			                    sender.sendMessage(String.format("%s%s",ChatColor.GREEN, Level.get(temp).name));
			                    int i = 0;
			                    if (temp !=0)
			                    	i = Level.get(temp-1).blocks;
			                    for(;i<Level.get(temp).blocks;i++)
			                    	if (blocks.get(i) == null)
			                    		sender.sendMessage("Grass or undefined");
			                    	else if (blocks.get(i).getClass() == Material.class)
			                    		sender.sendMessage(((Material)blocks.get(i)).name());
			                    	else if (blocks.get(i).getClass() == XMaterial.class)
			                    		sender.sendMessage(((XMaterial)blocks.get(i)).name());
			                    	else if (Place.Type.ItemsAdder == placetype && blocks.get(i).getClass() == CustomBlock.class)
			                    		sender.sendMessage(((CustomBlock)blocks.get(i)).getId());
			                    	else
			                    		sender.sendMessage((String)blocks.get(i));
			                    return true;
			                }
			                for(int i = 0;i<Level.size();i++)
			                	sender.sendMessage(String.format("%d: %s%s", i, ChatColor.GREEN, Level.get(i).name));
			                return true;
			            }
			            case ("reload"):{
			            	sender.sendMessage(String.format("%sReloading Plugin & Plugin Modules.", ChatColor.YELLOW));
			            	reload();
			            	sender.sendMessage(String.format("%sAll *.yml reloaded!", ChatColor.GREEN));
			            	return true;
			            }
			            case ("islands"):{
			                if (args.length == 1) {
			                    sender.sendMessage(Messages.bool_format);
			                    return true;
			                }
			                if (args[1].equals("true") || args[1].equals("false")) {
			                    il3x3 = Boolean.valueOf(args[1]);
			                    config.set("Island_for_new_players", il3x3);
			                    sender.sendMessage(ChatColor.GREEN + "Island_for_new_players = " + il3x3);
			                    return true;
			                }
			                if (args[1].equals("set_my_by_def")) {
			                	if (legacy) {
			                		sender.sendMessage(ChatColor.RED + "Not supported in legacy versions!");
			                		return true;
			                	}
			                	Player p = (Player) sender;
			                	UUID uuid = p.getUniqueId();
			                    if (PlayerInfo.GetId(uuid) != -1) {
			                        int result[] = getFullCoord(PlayerInfo.GetId(uuid));
			                        Island.scan(wor, result[0], y, result[1]);
			                        sender.sendMessage(ChatColor.GREEN + "Your island has been successfully saved and set as default for new players!");
			                        config.set("custom_island", Island.map());
			                    } else
			                        sender.sendMessage(ChatColor.RED + "You don't have an island!");
			                    return true;
			                }
			                if (args[1].equalsIgnoreCase("default")) {
			                	if (legacy) {
			                		sender.sendMessage(ChatColor.RED + "Not supported in legacy versions!");
			                		return true;
			                	}
			                    config.set("custom_island", Island.island = null);
			                    sender.sendMessage(ChatColor.GREEN + "The default island is installed.");
			                    return true;
			                }
			                sender.sendMessage(Messages.bool_format);
			                return true;
			            }
			            case ("chest"):{
			            	if (args.length < 2) {
			            		ChestItems.getChestNames().forEach(t -> sender.sendMessage(t));
			            		return true;
			            	}
			            	for (String t : ChestItems.getChestNames())
			            		if (args[1].equals(t))
			            			GUI.chestGUI((Player) sender, t);
			            	return true;
			            }
			        }
	        	}
	        	sender.sendMessage(
	        		    ChatColor.values()[rnd.nextInt(ChatColor.values().length)] + 
	        		    "  ▄▄    ▄▄\n" +
	        		    "█    █  █▄▀\n" +
	        		    "▀▄▄▀ █▄▀\n" +
	        		    "Created by MrMarL\nPlugin version: v1.3.3f\n" +
	        		    "Server version: " + (superlegacy ? "super legacy " : (legacy ? "legacy " : "")) + "1." + XMaterial.getVersion() + ".X");
    		     return true;
		    }
	    }
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

	private void Blockfile() {
    	blocks.clear();
    	mobs.clear();
    	Level.levels.clear();
        File block = new File(getDataFolder(), "blocks.yml");
        if (!block.exists())
            saveResource("blocks.yml", false);
        config_temp = YamlConfiguration.loadConfiguration(block);
        if (config_temp.isString("MaxLevel"))
        	Level.max.name = config_temp.getString("MaxLevel");
        for (int i = 0; config_temp.isList(String.format("%d", i)); i++) {
        	List <String> bl_temp = config_temp.getStringList(String.format("%d", i));
        	Level level = new Level(bl_temp.get(0));
        	Level.levels.add(level);
        	int q = 1;
        	if (!superlegacy && q < bl_temp.size())
        		try {//reading a custom color for the level.
        			level.color = BarColor.valueOf(bl_temp.get(q).toUpperCase());
        			q++;
        		} catch(Exception e) {level.color = Progress_color;}
	        	try {//reading a custom size for the level.
	        		int value = Integer.parseInt(bl_temp.get(q));
	    			level.length = value > 0 ? value : 1;
	    			q++;
	    		} catch(Exception e) {level.length = 16 + level.getId() * Level.multiplier;}
        	while (q < bl_temp.size()) {
        		String text = bl_temp.get(q++);
        		//reading a custom block (command).
        		if (text.charAt(0) == '/') {
	            	blocks.add(text);
	            	continue;
        		}
        		//reading a custom chest.
        		boolean check = false;
        		for (String str : ChestItems.getChestNames())
	        		if (text.equals(str)) {
	        			check = blocks.add(str); break;
	        		}
        		if (check) continue;
        		//reading a mob.
        		try { mobs.add(EntityType.valueOf(text)); continue; }
        		catch (Exception e) {}
        		//read a material
        		if (legacy){ //XMaterial lib
        			Optional <XMaterial> a = XMaterial.matchXMaterial(text);
	        		if (!a.isPresent()) {
	        			blocks.add(null);
	        			continue;
	        		}
	        		XMaterial xmt = a.get();
	        		if (xmt == GRASS_BLOCK)
		                blocks.add(null);
	        		else if (xmt == XMaterial.CHEST)
		                blocks.add(Material.CHEST);
	        		else
	        			blocks.add(xmt);
        		}
        		else {
        			Object a = Material.matchMaterial(text);
        			if (a != null && a.equals(Material.GRASS_BLOCK))
        				a = null;
        			if (a == null) {
        				switch (placetype) {
						case ItemsAdder:
							CustomBlock customBlock = CustomBlock.getInstance(text);
	        				if(customBlock != null) 
	        					a = customBlock;
							break;
						case Oraxen:
							if (OraxenItems.exists(text))
	        					a = text;
							break;
						case Nexo:
							if (NexoBlocks.isCustomBlock(text))
	        					a = text;
							break;
						default: break;
        				}
        			}
        			blocks.add(a);
        		}
        	}
        	level.blocks = blocks.size();
        	level.mobs = mobs.size();
        }
        Level.max.blocks = blocks.size();
        if ((Level.max.mobs = mobs.size()) == 0) 
        	getLogger().warning("Mobs are not set in the blocks.yml");
        
        SetupProgressBar();
    }
	
    private void Messagefile() {
        File message = new File(getDataFolder(), "messages.yml");
        if (!message.exists())
            saveResource("messages.yml", false);
        config_temp = YamlConfiguration.loadConfiguration(message);
        
        Messages.noperm = MessageCheck("noperm", Messages.noperm);
        Messages.noperm_inv = MessageCheck("noperm_inv", Messages.noperm_inv);
        Messages.help = MessageCheck("help", Messages.help);
        Messages.help_adm = MessageCheck("help_adm", Messages.help_adm);
        Messages.invite_usage = MessageCheck("invite_usage", Messages.invite_usage);
        Messages.invite_yourself = MessageCheck("invite_yourself", Messages.invite_yourself);
        Messages.invite_no_island = MessageCheck("invite_no_island", Messages.invite_no_island);
        Messages.invite_team = MessageCheck("invite_team", Messages.invite_team);
        Messages.invited = MessageCheck("invited", Messages.invited);
        Messages.invited_succes = MessageCheck("invited_succes", Messages.invited_succes);
        Messages.kicked = MessageCheck("kicked", Messages.kicked);
        Messages.kick_usage = MessageCheck("kick_usage", Messages.kick_usage);
        Messages.kick_yourself = MessageCheck("kick_yourself", Messages.kick_yourself);
        Messages.accept_succes = MessageCheck("accept_succes", Messages.accept_succes);
        Messages.accept_none = MessageCheck("accept_none", Messages.accept_none);
        Messages.idreset = MessageCheck("idreset", Messages.idreset);
        Messages.protection = MessageCheck("protection", Messages.protection);
        Messages.leave_not_set = MessageCheck("leave_not_set", Messages.leave_not_set);
        Messages.not_allow_visit = MessageCheck("not_allow_visit", Messages.not_allow_visit);
        Messages.allowed_visit = MessageCheck("allowed_visit", Messages.allowed_visit);
        Messages.forbidden_visit = MessageCheck("forbidden_visit", Messages.forbidden_visit);
        
        File gui = new File(getDataFolder(), "gui.yml");
        if (!gui.exists())
            saveResource("gui.yml", false);
        config_temp = YamlConfiguration.loadConfiguration(gui);
        
        Messages.baseGUI = MessageCheck("baseGUI", Messages.baseGUI);
        Messages.acceptGUI = MessageCheck("acceptGUI", Messages.acceptGUI);
        Messages.acceptGUIignore = MessageCheck("acceptGUIignore", Messages.acceptGUIignore);
        Messages.acceptGUIjoin = MessageCheck("acceptGUIjoin", Messages.acceptGUIjoin);
        Messages.topGUI = MessageCheck("topGUI", Messages.topGUI);
        Messages.visitGUI = MessageCheck("visitGUI", Messages.visitGUI);
        Messages.idresetGUI = MessageCheck("idresetGUI", Messages.idresetGUI);
    }
    private String MessageCheck(String name, String def_message) {
    	if (config_temp.isString(name))
        	return ChatColor.translateAlternateColorCodes('&',config_temp.getString(name));
    	return def_message;
    }
    private void Flowerfile() {
        flowers.clear();
        File flower = new File(getDataFolder(), "flowers.yml");
        if (!flower.exists())
            saveResource("flowers.yml", false);
        config_temp = YamlConfiguration.loadConfiguration(flower);
        flowers.add(GRASS);
        for(String list:config_temp.getStringList("flowers"))
        	if (!XMaterial.matchXMaterial(list).isPresent())
        		flowers.add(GRASS);
        	else
        		flowers.add(XMaterial.matchXMaterial(list).get());
    }
    private void Chestfile() {
        File chest = new File(getDataFolder(), "chests.yml");
        if (!chest.exists())
            saveResource("chests.yml", false);
        ChestItems.chest = chest;
        ChestItems.load();
    }
    
    String Check(String type, String data) {
    	if (!config.isString(type))
            config.set(type, data);
    	return config.getString(type);
    }
    int Check(String type, int data) {
    	if (!config.isInt(type))
            config.set(type, data);
    	return config.getInt(type);
    }
    double Check(String type, double data) {
    	if (!config.isDouble(type))
            config.set(type, data);
    	return config.getDouble(type);
    }
    boolean Check(String type, boolean data) {
    	if (!config.isBoolean(type))
            config.set(type, data);
    	return config.getBoolean(type);
    }
    List<String> Check(String type, List<String> data) {
    	if (!config.isList(type))
            config.set(type, data);
    	return config.getStringList(type);
    }

    private void Configfile() {
    	File con = new File(getDataFolder(), "config.yml");
        if (!con.exists())
            saveResource("config.yml", false);
        config = LowerCaseYaml.loadAndFixConfig(con);
        wor = Bukkit.getWorld(Check("world", "world"));
        x = (int) Check("x", (double) x);
        y = (int) Check("y", (double) y);
        z = (int) Check("z", (double) z);
        leavewor = Bukkit.getWorld(Check("leaveworld", "world"));
        Check("xleave", .0);
        Check("yleave", .0);
        Check("zleave", .0);
        Check("yawleave", .0);
        if (!superlegacy) {
        	Progress_bar = Check("progress_bar", true);
        	Progress_color = BarColor.valueOf(Check("progress_bar_color", "GREEN"));
	        TextP = Check("progress_bar_text", "level");
	        lvl_bar_mode = TextP.equals("level");
        }
        il3x3 = Check("island_for_new_players", true);
        Level.multiplier = Check("level_multiplier", Level.multiplier);
        max_players_team = Check("max_players_team", max_players_team);
        UpdateParametrs();// СircleMode;protection;autojoin;droptossup
        if (OBWorldGuard.canUse)
        	WorldGuard = Check("worldguard", OBWorldGuard.canUse);
        OBWorldGuard.flags = Check("wgflags", OBWorldGuard.flags);
        if (Border) Border = Check("border", Border);
        sto = Check("set", 100);
        if (config.isSet("custom_island") && !legacy)
        	Island.read(config);
        LegacyConfigSaver.Save(config, con);
    }
    
    public void UpdateParametrs() {
    	CircleMode = Check("circlemode", CircleMode);
    	UseEmptyIslands = Check("useemptyislands", UseEmptyIslands);
    	saveplayerinventory = Check("saveplayerinventory", saveplayerinventory);
        protection = Check("protection", protection);
        autojoin = Check("autojoin", autojoin);
        droptossup = Check("droptossup", droptossup);
        physics = Check("physics", physics);
        particle = Check("particle", particle);
        allow_nether = Check("allow_nether", allow_nether);
        GUI.enabled = Check("gui", GUI.enabled);
        chat_alert = Check("chat_alert", chat_alert);
        rebirth = Check("rebirth_on_the_island", rebirth);
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