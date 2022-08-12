// Copyright © 2022 MrMarL. All rights reserved.
package Oneblock;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import Oneblock.WorldGuard.OBWorldGuard;
import Oneblock.WorldGuard.OBWorldGuard6;
import Oneblock.WorldGuard.OBWorldGuard7;
import Oneblock.gui.GUI;
import Oneblock.gui.GUIListener;
import XSeriesOneBlock.XBlock;
import XSeriesOneBlock.XMaterial;
import me.clip.placeholderapi.PlaceholderAPI;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Oneblock extends JavaPlugin {
    boolean on = false;
    static int x = 0;
    static int y = 0;
    static int z = 0;
    final Random rnd = new Random(System.currentTimeMillis());
    FileConfiguration config, newConfigz;
    static World wor;
	World leavewor;
    int random = 0;
    boolean superlegacy, legacy;
    ArrayList <Object> blocks = new ArrayList <>();
    ArrayList <Material> s_ch, m_ch, h_ch;
    ArrayList <EntityType> mobs = new ArrayList <>();
    ArrayList <XMaterial> flowers = new ArrayList <>();
    static List <Player> plonl;
    static int lvl_mult = 5;
    String TextP = "";
    int sto = 100;
    Long fr;
    BarColor Progress_color;
    boolean il3x3 = false, rebirth = false, autojoin = false;
    boolean droptossup = true, physics = false;
    boolean lvl_bar_mode = false, chat_alert = false;
    boolean protection = false;
    boolean PAPI = false;
    boolean WorldGuard = false;
    boolean Progress_bar = true;
    boolean СircleMode = false;
    boolean UseEmptyIslands = true;
    int max_players_team = 0;
    OBWorldGuard OBWG;
    final XMaterial GRASS_BLOCK = XMaterial.GRASS_BLOCK, GRASS = XMaterial.GRASS;
    final VoidChunkGenerator GenVoid = new VoidChunkGenerator();
    
    public static World wor() { return wor; }
    
	@Override
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {return GenVoid;}
    
    @Override
    public void onEnable() {
        superlegacy = !XMaterial.supports(9);// Is version 1.9 supported?
        legacy = !XMaterial.supports(13);// Is version 1.13 supported?
        Metrics metrics = new Metrics(this, 14477);
        final PluginManager pluginManager = Bukkit.getPluginManager();
        getLogger().info(
        		  "\n┏━┓····┏━━┓·····┏┓\n"
        		  + "┃┃┣━┳┳━┫┏┓┣┓┏━┳━┫┣┓\n"
        		  + "┃┃┃┃┃┃┻┫┏┓┃┗┫╋┃━┫━┫\n"
        		  + "┗━┻┻━┻━┻━━┻━┻━┻━┻┻┛ by MrMarL");
        if (pluginManager.isPluginEnabled("PlaceholderAPI")) {
        	getLogger().info("PlaceholderAPI has been found!");
            PAPI = true;
            new OBP().register();
        }
        Configfile();
        Datafile();
        Blockfile();
        Flowerfile();
        Chestfile();
        Mobfile();
        Messagefile();
        metrics.addCustomChart(new SimplePie("premium", () -> String.valueOf(OBWorldGuard.canUse)));
        metrics.addCustomChart(new SimplePie("circle_mode", () -> String.valueOf(СircleMode)));
        metrics.addCustomChart(new SimplePie("use_empty_islands", () -> String.valueOf(UseEmptyIslands)));
        metrics.addCustomChart(new SimplePie("gui", () -> String.valueOf(GUI.enabled)));
        if (config.getDouble("y") != 0) {
            if (wor == null || (config.getDouble("yleave") != 0 && leavewor == null)) {
                Bukkit.getScheduler().runTaskTimerAsynchronously(this, (Runnable) new wor_null(), 32, 64);
            } 
            else runMainTaskWG();
        }
        pluginManager.registerEvents(new RespawnJoinEvent(), this);
        pluginManager.registerEvents(new BlockEvent(), this);
        pluginManager.registerEvents(new GUIListener(), this);
    }
    public class RespawnJoinEvent implements Listener {
        @EventHandler
        public void Respawn(PlayerRespawnEvent e) {
			if (!rebirth)
				return;
			Player pl = e.getPlayer();
			if (pl.getWorld().equals(wor) && PlayerInfo.ExistId(pl.getName())) {
				int result[] = getFullCoord(PlayerInfo.GetId(pl.getName()), 0, 0);
				e.setRespawnLocation(new Location(wor, result[0] + 0.5, y + 1.2013, result[1] + 0.5));
			}
        }
        @EventHandler
        public void AutoJoin(PlayerTeleportEvent e) {
			if (!autojoin)
				return;
			Location loc = e.getTo();
			World from = e.getFrom().getWorld();
			World to = loc.getWorld();
			if (!from.equals(wor) && to.equals(wor) && !(loc.getY() == y + 1.2013)) {
				e.setCancelled(true);
				e.getPlayer().performCommand("ob j");
			}
        }
        @EventHandler
        public void JoinAuto(PlayerJoinEvent e) {
			if (!autojoin)
				return;
			Player pl = e.getPlayer();
			if (pl.getWorld().equals(wor))
				pl.performCommand("ob j");
		}
    }
    public class ChangedWorld implements Listener {
    	@EventHandler
        public void PlayerChangedWorldEvent(PlayerChangedWorldEvent e) {
    		if (PlayerInfo.size() == 0)
    			return;
        	if (e.getFrom().equals(wor))
        		PlayerInfo.removeBarStatic(e.getPlayer());
        }
    }
    
    public class BlockEvent implements Listener {
    	@EventHandler(ignoreCancelled = true)
        public void ItemStackSpawn(EntitySpawnEvent e)
        {
    		if (!droptossup) return;
    		
    		if (wor == null) return;
    		
            if (!EntityType.DROPPED_ITEM.equals(e.getEntityType()))
                return;
            
            Location loc = e.getLocation();
            
            if (!wor.equals(loc.getWorld()))
                return;
            
            if (loc.getBlockY() != y) return;
            
            if((x - loc.getBlockX()) % sto != 0 || (z - loc.getBlockZ()) % sto != 0)
            	return;

            Entity drop = e.getEntity();
            drop.teleport(loc.add(0, .75, 0));
            drop.setVelocity(new Vector(0, .1, 0));
        }
    }
    public class wor_null implements Runnable {
        public void run() {
            if (wor == null) {
            	getLogger().info("Waiting for the initialization of the world");
            	getLogger().info("Trying to initialize the world again...");
                wor = Bukkit.getWorld(config.getString("world"));
                leavewor = Bukkit.getWorld(config.getString("leaveworld"));
            } else {
            	getLogger().info("The initialization of the world was successful!");
            	runMainTaskWG();
            }
        }
    }
    public void runMainTaskWG() {
    	runMainTask();
        boolean WGpl = Bukkit.getPluginManager().isPluginEnabled("WorldGuard");
        if (WGpl) {
        	getLogger().info("WorldGuard has been found!");
        	if (legacy)
        		OBWG = new OBWorldGuard6();
			else
				OBWG = new OBWorldGuard7();
        }
        if (!WGpl && WorldGuard)
        	WorldGuard = false;
        ReCreateRegions();
    }
    public void runMainTask() {
        Bukkit.getScheduler().cancelTasks(this);
        if (config.getDouble("y") != 0) {
            Bukkit.getScheduler().runTaskTimer(this, (Runnable) new Task(), fr, fr * 2);
            on = true;
        }
    }
    public void addinvite(String name, String to) {
    	for(Invitation item: Invitation.list) 
			if (item.equals(name, to))
				return;
    	Invitation inv_ = new Invitation(name, to);
    	Invitation.list.add(inv_);
    	Bukkit.getScheduler().runTaskLaterAsynchronously((Plugin) this, new Runnable() {
    		@Override
    		public void run() {Invitation.list.remove(inv_);}}, 300L);
    }
    public boolean checkinvite(Player pl) {
		String name = pl.getName();
		Invitation inv_ = Invitation.check(name);
		
		if (inv_ == null || !PlayerInfo.ExistId(inv_.Inviting))
			return false;
		 
		if (PlayerInfo.ExistId(name)) {
			if (Progress_bar)
				PlayerInfo.get(name).bar.removePlayer(pl);
			pl.performCommand("ob idreset /n");
		}
		PlayerInfo.get(inv_.Inviting).nicks.add(name);
		pl.performCommand("ob j"); 
		Invitation.list.remove(inv_);
		return true; 
    }

	public int[] getFullCoord(int id, int x, int z) {
		if (!СircleMode)
			return new int[] {id * sto + Oneblock.x, Oneblock.z};
		for (int i = 0; i < id; i++) {
			if (x > z)
			    if (x > -z)
				    z--;
				else
				    x--;
			else if (-x > z || x == z && z < 0)
				z++;
			else
				x++;
		}
		x = x * sto + Oneblock.x;
		z = z * sto + Oneblock.z;
		return new int[] {x, z};
	}
    
    public class Task implements Runnable {
        public void run() {
            plonl = wor.getPlayers();
            Collections.shuffle(plonl);
            for (Player ponl: plonl) {
            	String name = ponl.getName();
            	if (!PlayerInfo.ExistId(name))
            		continue;
                int plID = PlayerInfo.GetId(name);
                int result[] = getFullCoord(plID, 0, 0);
                int X_pl = result[0], Z_pl = result[1];
                if (protection && !ponl.hasPermission("Oneblock.ignoreBarrier")) {
                	int checkX = ponl.getLocation().getBlockX()-X_pl;
                	int checkZ = СircleMode ? ponl.getLocation().getBlockZ()-Z_pl : 0;
                	if (Math.abs(checkX) > 50 || Math.abs(checkZ) > 50) {
                		if (Math.abs(checkX) > 200 || Math.abs(checkZ) > 200) {
                			ponl.performCommand("ob j");
                			continue;
                		}
                		ponl.setVelocity(new Vector(-checkX/30, 0, -checkZ/30));
                		ponl.sendMessage(Messages.protection);
                		continue;
                	}
                }
                Block block = wor.getBlockAt(X_pl, y, Z_pl);
                if (block.getType().equals(Material.AIR)) {
                	PlayerInfo inf = PlayerInfo.get(plID);
                	Level lvl_inf = Level.get(inf.lvl); 
                    if (++inf.breaks >= 16 + inf.lvl * lvl_mult) {
                    	inf.lvlup();
                    	lvl_inf = Level.get(inf.lvl); 
                        if (Progress_bar) {
                        	inf.bar.setColor(lvl_inf.color);
                        	if (lvl_bar_mode)
                        		inf.bar.setTitle(lvl_inf.name);
                        }
                        if (chat_alert)
                        	ponl.sendMessage(String.format("%s%s", ChatColor.GREEN, lvl_inf.name));
                    }
                    if (Progress_bar) {
                        if (!lvl_bar_mode && PAPI)
                        	inf.bar.setTitle(PlaceholderAPI.setPlaceholders(ponl, TextP));
                        inf.bar.setProgress((double) inf.breaks / (16 + inf.lvl * lvl_mult));
                        inf.bar.addPlayer(ponl);
                    }
                    for(Player pll :PlLst(plID)) {
                    	Location loc = pll.getLocation();
                    	if (loc.getBlockX() == X_pl && loc.getY() - 1 < y && loc.getBlockZ() == Z_pl) {
                    		loc.setY(y+1);
                    		pll.teleport(loc);
                    		break;
                    	}
                    }
                    random = lvl_inf.size;
                    if (random != 0) random = rnd.nextInt(random);
                    if (blocks.get(random) == null) {
                        XBlock.setType(block, GRASS_BLOCK);
                        if (rnd.nextInt(3) == 1)
                            XBlock.setType(wor.getBlockAt(X_pl, y + 1, Z_pl),flowers.get(rnd.nextInt(flowers.size())));
                    } else if (blocks.get(random) == XMaterial.CHEST) {
                        try {
                            block.setType(Material.CHEST);
                            Chest chest = (Chest) block.getState();
                            Inventory inv = chest.getInventory();
                            ArrayList <Material> ch_now;
                            if (random < blocks.size() / 3)
                            	ch_now = s_ch;
                            else if (random < blocks.size() / 1.5)
                            	ch_now = m_ch;
                            else
                            	ch_now = h_ch;
                            int max = rnd.nextInt(3)+2; 
                            for(int i = 0;i<max;i++) {
                            	Material m = ch_now.get(rnd.nextInt(ch_now.size()));
                            	if (m.getMaxStackSize() == 1)
                            		inv.addItem(new ItemStack(m, 1));
                            	else
                            		inv.addItem(new ItemStack(m, rnd.nextInt(4)+2));
                            }
                        } catch (Exception e) {
                        	getLogger().warning("Error when generating items for the chest! Pls redo chests.yml!");
                        }
                    } else
                    	XBlock.setType(block, blocks.get(random), physics);

                    if (rnd.nextInt(9) == 0) {
                        if (inf.lvl < blocks.size() / 9)
                            random = rnd.nextInt(mobs.size() / 3);
                        else if (inf.lvl < blocks.size() / 9 * 2)
                            random = rnd.nextInt(mobs.size() / 3 * 2);
                        else
                            random = rnd.nextInt(mobs.size());
                        wor.spawnEntity(new Location(wor, X_pl + .5, y + 1, Z_pl + .5), mobs.get(random));
                    }
                }
            }
        }
    }

    public void onDisable() {
        config.set("custom_island", Island.map());
        saveData();
        Config.Save(config);
    }
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("oneblock")) {
            //
            if (args.length == 0)
                return ((Player)sender).performCommand("ob j");
            
            if (!sender.hasPermission("Oneblock.join")) {
                sender.sendMessage(String.format("%sYou don't have permission [Oneblock.join].", ChatColor.RED));
                return true;
            }
            //
            String parametr = args[0].toLowerCase();
            switch (parametr)
            {
            case ("j"):
            case ("join"):{
                if (config.getInt("y") == 0 || wor == null) {
                	sender.sendMessage(String.format("%sFirst you need to set the reference coordinates '/ob set'.", ChatColor.YELLOW));
                	return true;
                }
                Player p = (Player) sender;
                String name = p.getName();
                int plID = 0;
                int X_pl = 0, Z_pl = 0;
                if (!PlayerInfo.ExistId(name)) {
                	PlayerInfo inf = new PlayerInfo(name);
                	if (UseEmptyIslands)
                		plID = PlayerInfo.getNull();
                	else
                		plID = PlayerInfo.size();
                	int result[] = getFullCoord(plID, X_pl, Z_pl);
                	X_pl = result[0]; Z_pl = result[1];
                	if (plID != PlayerInfo.size())
                		Island.clear(wor, X_pl, y, Z_pl, sto/4);
                    if (il3x3)
                    	Island.place(wor, X_pl, y, Z_pl);
                    //WorldGuard
                    if (WorldGuard && OBWorldGuard.canUse) {
                    	Vector Block1 = new Vector(X_pl - sto/2 + 1, 0, Z_pl - sto/2 + 1);
                    	Vector Block2 = new Vector(X_pl + sto/2 - 1, 255, Z_pl + sto/2 - 1);
                    	OBWG.CreateRegion(name, Block1, Block2, plID);
                    }	
					saveData();
					PlayerInfo.set(plID, inf);
                    if (!superlegacy && Progress_bar) {
                    	String temp = TextP;
                        if (lvl_bar_mode)
                        	temp = Level.get(0).name;
                        else if (PAPI)
                        	temp = PlaceholderAPI.setPlaceholders(p, TextP);
                        inf.bar = Bukkit.createBossBar(temp, Level.get(0).color, BarStyle.SEGMENTED_10, BarFlag.DARKEN_SKY);
                    }
                } 
                else {
                	plID = PlayerInfo.GetId(name); // GenType
                	int result[] = getFullCoord(plID, X_pl, Z_pl);
                    X_pl = result[0]; Z_pl = result[1];
                }
                if (!on)
                	runMainTaskWG();
                if (Progress_bar)
                	PlayerInfo.get(plID).bar.setVisible(true);
                p.teleport(new Location(wor, X_pl + 0.5, y + 1.2013, Z_pl + 0.5));
                if (WorldGuard && OBWorldGuard.canUse)
                	OBWG.addMember(name, plID);
                return true;
            }
            case ("leave"):{
                Player p = (Player) sender;
                PlayerInfo.removeBarStatic(p);
                if (config.getDouble("yleave") == 0 || leavewor == null)
                    return true;
                p.teleport(new Location(leavewor, config.getDouble("xleave"), config.getDouble("yleave"), config.getDouble("zleave"),
                		(float)config.getDouble("yawleave"), 0f));
                return true;
            }
            case ("set"):{
                if (!sender.hasPermission("Oneblock.set")) {
                    sender.sendMessage(Messages.noperm);
                    return true;
                }
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
                    	sender.sendMessage(String.format("%sinvalid value", ChatColor.RED));
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
                Config.Save(config);
                if (OBWG == null) runMainTaskWG();
                wor.getBlockAt(x, y, z).setType(GRASS_BLOCK.parseMaterial());
                ReCreateRegions();
                return true;
            }
            case ("setleave"):{
                if (!sender.hasPermission("Oneblock.set")) {
                    sender.sendMessage(Messages.noperm);
                    return true;
                }
                Player p = (Player) sender;
                Location l = p.getLocation();
                leavewor = l.getWorld();
                config.set("leaveworld", leavewor.getName());
                config.set("xleave", l.getX());
                config.set("yleave", l.getY());
                config.set("zleave", l.getZ());
                config.set("yawleave", l.getYaw());
                Config.Save(config);
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
            	if (inv == null) 
            		return true;
            	Player pl = (Player) sender;
        		String name = pl.getName();
        		if (inv == pl) {
        			sender.sendMessage(Messages.invite_yourself);
        			return true;
        		}
        		if (!PlayerInfo.ExistId(name)) {
        			sender.sendMessage(Messages.invite_no_island);
        			return true;
        		}
        		if (max_players_team != 0) {
        			PlayerInfo pinf = PlayerInfo.get(name);
        			if (pinf.nicks.size() >= max_players_team) {
            			sender.sendMessage(String.format(Messages.invite_team, max_players_team));
            			return true;
        			}
        		}
        		addinvite(name, inv.getName());
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
            	String name = args[1];
            	if (!PlayerInfo.ExistNoInvaitId(name))
            		return true;
            	Player inv = Bukkit.getPlayer(name);
            	if (inv == (Player) sender) {
            		sender.sendMessage(Messages.kick_yourself);
            		return true;
            	}
            	int plID = PlayerInfo.GetId(name);
            	PlayerInfo info = PlayerInfo.get(plID);
            	if (info.nicks.contains(name)) {
            		info.nicks.remove(name);
            		if (WorldGuard && OBWorldGuard.canUse)
        				OBWG.removeMember(name, plID);
            		if (inv != null)
            			inv.performCommand("ob j");
            	}
            	return true;
            }
            case ("accept"):{
            	Player pl = (Player) sender;
           	 	if (checkinvite(pl))
           	 		sender.sendMessage(Messages.accept_succes);
           	 	else
           	 		sender.sendMessage(Messages.accept_none);
           		return true;
            }
            case ("idreset"):{
            	Player pl = (Player)sender;
            	String name = pl.getName();
            	if (!PlayerInfo.ExistId(name))
            		return true;
            	int PlId = PlayerInfo.GetId(name);
            	PlayerInfo plp = PlayerInfo.get(PlId);
            	if (Progress_bar)
            		plp.bar.removePlayer(pl);
            	if (plp.nick.equals(name)) {
            		if (plp.nicks.size() > 0) {
            			plp.nick = plp.nicks.get(0);
            			plp.nicks.remove(0);
            		}
            		else
            			plp.nick = null;
            	}
            	else
            		plp.nicks.remove(name);
            	if (WorldGuard && OBWorldGuard.canUse)
            		OBWG.removeMember(name, PlId);
            	if (!args[args.length-1].equals("/n"))
            		sender.sendMessage(Messages.idreset);
            	return true;
            }
            case ("worldguard"):{
            	if (!sender.hasPermission("Oneblock.set")) {
                    sender.sendMessage(Messages.noperm);
                    return true;
                }
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
                else
                	sender.sendMessage(String.format("%senter a valid value true or false", ChatColor.YELLOW));
            	sender.sendMessage(String.format("%sthe OBWorldGuard is now %s", ChatColor.GREEN, (WorldGuard?"enabled.":"disabled.")));
           		return true;
            }
            case ("circlemode"):
            	parametr = "СircleMode";
            case ("useemptyislands"):
            case ("protection"):
            case ("droptossup"):
            case ("physics"):
            case ("autojoin"):{
            	if (!sender.hasPermission("Oneblock.set")) {
                    sender.sendMessage(Messages.noperm);
                    return true;
                }
            	if (args.length > 1 &&
                    	(args[1].equals("true") || args[1].equals("false"))) {
                    	config.set(parametr, Boolean.valueOf(args[1]));
                    	UpdateParametrs();
                }
                else
                	sender.sendMessage(String.format("%senter a valid value true or false", ChatColor.YELLOW));
                sender.sendMessage(String.format("%s%s is now %s", ChatColor.GREEN, parametr, (config.getBoolean(parametr)?"enabled.":"disabled.")));
           		return true;
            }
            //LVL
            case ("setlevel"):{
                if (!sender.hasPermission("Oneblock.set")) {
                    sender.sendMessage(Messages.noperm);
                    return true;
                }
                if (args.length <= 2) {
                    sender.sendMessage(String.format("%sinvalid format. try: /ob setlevel 'nickname' 'level'", ChatColor.RED));
                    return true;
                }
                if (PlayerInfo.ExistId(args[1])) {
                    int setlvl = 0;
                    try {
                        setlvl = Integer.parseInt(args[2]);
                    } catch (NumberFormatException nfe) {
                        sender.sendMessage(String.format("%sinvalid level value.", ChatColor.RED));
                        return true;
                    }
                    if (setlvl >= 0 && 10000 > setlvl) {
                        int i = PlayerInfo.GetId(args[1]);
                        PlayerInfo inf = PlayerInfo.get(i);
                        inf.breaks = 0;
                        inf.lvl = setlvl;
                        if (lvl_bar_mode) {
                        	Level lvl = Level.get(inf.lvl);
	                    	inf.bar.setTitle(lvl.name);
	                    	inf.bar.setColor(lvl.color);
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
                if (!sender.hasPermission("Oneblock.set")) {
                    sender.sendMessage(Messages.noperm);
                    return true;
                }
                if (args.length <= 1) {
                    sender.sendMessage(String.format("%sinvalid format. try: /ob clear 'nickname'", ChatColor.RED));
                    return true;
                }
                if (PlayerInfo.ExistId(args[1])) {
                    int i = PlayerInfo.GetId(args[1]);
                    PlayerInfo inf = PlayerInfo.get(i);
                    inf.breaks = 0;
                    inf.lvl = 0;
                    if (Progress_bar)
                    	inf.bar.setVisible(false);
                    int result[] = getFullCoord(i, 0, 0);
                    Island.clear(wor, result[0], y, result[1], sto/4);
                    sender.sendMessage(String.format("%splayer %s island is destroyed! :D", ChatColor.GREEN, args[1]));
                    return true;
                }
                sender.sendMessage(String.format("%sa player named %s was not found.", ChatColor.RED, args[1]));
                return true;
            }
            case ("lvl_mult"):{
                if (!sender.hasPermission("Oneblock.set")) {
                    sender.sendMessage(Messages.noperm);
                    return true;
                }
                if (args.length <= 1) {
                    sender.sendMessage(String.format("%slevel multiplier now: %d\n5 by default", ChatColor.GREEN, lvl_mult));
                    return true;
                }
                int lvl = lvl_mult;
                try {
                    lvl = Integer.parseInt(args[1]);
                } catch (NumberFormatException nfe) {
                    sender.sendMessage(String.format("%sinvalid multiplier value.", ChatColor.RED));
                    return true;
                }
                if (lvl <= 20 && lvl >= 0) {
                    lvl_mult = lvl;
                    config.set("level_multiplier", lvl_mult);
                } else
                    sender.sendMessage(String.format("%spossible values: from 0 to 20.", ChatColor.RED));
                sender.sendMessage(String.format("%slevel multiplier now: %d\n5 by default", ChatColor.GREEN, lvl_mult));
                return true;
            }
            case ("max_players_team"):{
                if (!sender.hasPermission("Oneblock.set")) {
                    sender.sendMessage(Messages.noperm);
                    return true;
                }
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
                if (!sender.hasPermission("Oneblock.set")) {
                    sender.sendMessage(Messages.noperm);
                    return true;
                }
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
                    if (Progress_bar) {
                        if (Progress_color == null)
                            Progress_color = BarColor.GREEN;
                    	Blockfile();
                    }
                    for (PlayerInfo bb:PlayerInfo.list)
                    	if (bb.bar != null)
                    		bb.bar.setVisible(Progress_bar);
                    config.set("Progress_bar", Progress_bar);
                    return true;
                }
                if (args[1].equalsIgnoreCase("color")) {
                    if (args.length == 2) {
                        sender.sendMessage(String.format("%senter a color name.", ChatColor.YELLOW));
                        return true;
                    }
                    try {
                        Progress_color = BarColor.valueOf(args[2]);
                        for (PlayerInfo bb:PlayerInfo.list)
                            bb.bar.setColor(Progress_color);
                        Blockfile();
                        config.set("Progress_bar_color", Progress_color.toString());
                    } catch (Exception e) {
                        sender.sendMessage(String.format("%sPlease enter a valid color. For example: RED", ChatColor.YELLOW));
                    }
                    sender.sendMessage(String.format("%sProgress bar color = %s", ChatColor.GREEN, Progress_color.toString()));
                    return true;
                }
                if (args[1].equalsIgnoreCase("level")) {
                	if (!Progress_bar)
                		return true;
                    if (lvl_bar_mode = !lvl_bar_mode) {
                        for (PlayerInfo inf:PlayerInfo.list)
                        	inf.bar.setTitle(Level.get(inf.lvl).name);
                        config.set("Progress_bar_text", "level");
                        return true;
                    } else {
                        for (PlayerInfo inf:PlayerInfo.list)
                        	inf.bar.setTitle("Progress bar");
                        config.set("Progress_bar_text", "Progress bar");
                        return true;
                    }
                }
                if (args[1].equalsIgnoreCase("settext")) {
                	if (!Progress_bar)
                		return true;
                    String txt_bar = "";
					for (int i = 2; i < args.length; i++)
						txt_bar = String.format("%s %s", txt_bar, args[i]);
                    lvl_bar_mode = false;
                    for (PlayerInfo bb:PlayerInfo.list)
                        bb.bar.setTitle(txt_bar);
                    config.set("Progress_bar_text", txt_bar);
                    TextP = txt_bar;
                    if (PAPI)
                        for (Player ponl: plonl)
                            PlayerInfo.get(ponl.getName()).bar.setTitle(PlaceholderAPI.setPlaceholders(ponl, txt_bar));
                    return true;
                }
                sender.sendMessage(String.format("%strue, false, settext or level only!", ChatColor.RED));
                return true;
            }
            case ("listlvl"):{
                if (!sender.hasPermission("Oneblock.set")) {
                    sender.sendMessage(Messages.noperm);
                    return true;
                }
                if (args.length >= 2) {
                	int temp = 0;
                    try {
                    	temp = Integer.parseInt(args[1]);
                    } catch (NumberFormatException nfe) {
                    	sender.sendMessage(String.format("%sinvalid value", ChatColor.RED));
                    	return true;
                    }
                    if (Level.size()<=temp||temp<0) {
                    	sender.sendMessage(String.format("%sundefined lvl", ChatColor.RED));
                    	return true;
                    }
                    sender.sendMessage(String.format("%s%s",ChatColor.GREEN, Level.get(temp).name));
                    int i = 0;
                    if (temp !=0)
                    	i = Level.get(temp-1).size;
                    for(;i<Level.get(temp).size;i++)
                    	if (blocks.get(i) == null)
                    		sender.sendMessage("Grass or undefined");
                    	else if (blocks.get(i).getClass() == XMaterial.class)
                    		sender.sendMessage(((XMaterial)blocks.get(i)).name());
                    	else
                    		sender.sendMessage((String)blocks.get(i));
                    return true;
                }
                for(int i = 0;i<Level.size();i++)
                	sender.sendMessage(String.format("%d: %s%s", i, ChatColor.GREEN, Level.get(i).name));
                return true;
            }
            case ("reload"):{
                if (!sender.hasPermission("Oneblock.set")) {
                    sender.sendMessage(Messages.noperm);
                    return true;
                }
                if (args.length == 1) {
                    sender.sendMessage(String.format("%sReloading Plugin & Plugin Modules.", ChatColor.YELLOW));
                    Blockfile();
                    Flowerfile();
                    Chestfile();
                    Mobfile();
                    Messagefile();
                    ReCreateRegions();
                    sender.sendMessage(String.format("%sAll .yml reloaded!", ChatColor.GREEN));
                    return true;
                }
                if (args[1].equalsIgnoreCase("blocks.yml")) {
                    Blockfile();
                    sender.sendMessage(String.format("%sBlocks.yml reloaded!", ChatColor.GREEN));
                    return true;
                }
                if (args[1].equalsIgnoreCase("flowers.yml")) {
                	Flowerfile();
                    sender.sendMessage(String.format("%sFlowers.yml reloaded!", ChatColor.GREEN));
                    return true;
                }
                if (args[1].equalsIgnoreCase("chests.yml")) {
                    Chestfile();
                    sender.sendMessage(String.format("%sChests.yml reloaded!", ChatColor.GREEN));
                    return true;
                }
                if (args[1].equalsIgnoreCase("mobs.yml")) {
                    Mobfile();
                    sender.sendMessage(String.format("%sMobs.yml reloaded!", ChatColor.GREEN));
                    return true;
                }
                if (args[1].equalsIgnoreCase("messages.yml")) {
                	Messagefile();
                    sender.sendMessage(String.format("%sMessages.yml reloaded!", ChatColor.GREEN));
                    return true;
                }
                sender.sendMessage(String.format("%sTry blocks.yml or chests.yml", ChatColor.RED));
                return true;
            }
            case ("chat_alert"):{
                if (!sender.hasPermission("Oneblock.set")) {
                    sender.sendMessage(Messages.noperm);
                    return true;
                }
                chat_alert = !chat_alert;
                sender.sendMessage(ChatColor.GREEN + (chat_alert?"Alerts are now on!":"Alerts are now disabled!"));
                config.set("Chat_alert", chat_alert);
                return true;
            }
            case ("frequency"):{
                if (!sender.hasPermission("Oneblock.set")) {
                    sender.sendMessage(Messages.noperm);
                    return true;
                }
                if (args.length == 1) {
                    sender.sendMessage(ChatColor.YELLOW + "enter a valid value (4 to 20)\n7 by default");
                    return true;
                }
                Long fr_;
                String Sfr = "";
                try {
                    fr_ = Long.parseLong(args[1]);
                } catch (Exception e) {
                	sender.sendMessage(ChatColor.YELLOW + "enter a valid value (4 to 20)\n7 by default");
                    return true;
                }
                if (fr_ >= 4L && fr_ <= 20L && on) {
                    fr = fr_;
                    config.set("frequency", fr);
                    if (fr == 4L)
                        Sfr = " (Extreme)";
                    else if (fr < 7L)
                        Sfr = " (Fast)";
                    else if (fr == 7L)
                        Sfr = " (Default)";
                    else if (fr < 9L)
                        Sfr = " (Normal)";
                    else if (fr < 13L)
                        Sfr = " (Slow)";
                    else if (fr < 17L)
                        Sfr = " (Slower)";
                    else
                        Sfr = " (Max TPS)";
                    runMainTask();
                }
                sender.sendMessage(ChatColor.GREEN + "Now frequency = " + fr + Sfr);
                return true;
            }
            case ("islands"):{
                if (!sender.hasPermission("Oneblock.set")) {
                    sender.sendMessage(Messages.noperm);
                    return true;
                }
                if (args.length == 1) {
                    sender.sendMessage(ChatColor.YELLOW + "enter a valid value true or false");
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
                    String name = p.getName();
                    if (PlayerInfo.ExistId(name)) {
                        int result[] = getFullCoord(PlayerInfo.GetId(name), 0, 0);
                        Island.scan(wor, result[0], y, result[1]);
                        sender.sendMessage(ChatColor.GREEN + "Your island has been successfully saved and set as default for new players!");
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
                sender.sendMessage(ChatColor.YELLOW + "enter a valid value true or false");
                return true;
            }
            case ("island_rebirth"):{
                if (!sender.hasPermission("Oneblock.set")) {
                    sender.sendMessage(Messages.noperm);
                    return true;
                }
                if (args.length == 1) {
                    sender.sendMessage(ChatColor.YELLOW + "enter a valid value true or false");
                    return true;
                }
                if (args[1].equals("true") || args[1].equals("false")) {
                    rebirth = Boolean.valueOf(args[1]);
                    config.set("Rebirth_on_the_island", rebirth);
                    sender.sendMessage(ChatColor.GREEN + "Rebirth_on_the_island = " + rebirth);
                    return true;
                }
                sender.sendMessage(ChatColor.YELLOW + "enter a valid value true or false");
                return true;
            }
            case ("gui"):{
            	if (args.length == 1) {
            		GUI.openGUI((Player) sender);
            		return true;
            	}
            	if (!sender.hasPermission("Oneblock.set")) {
                    sender.sendMessage(Messages.noperm);
                    return true;
                }
            	if (args.length > 1 &&
                    	(args[1].equals("true") || args[1].equals("false"))) {
                    	config.set(parametr, Boolean.valueOf(args[1]));
                        GUI.enabled = Check("gui", GUI.enabled);
                }
                else
                	sender.sendMessage(String.format("%senter a valid value true or false", ChatColor.YELLOW));
            	if (!GUI.enabled)
            		for (Player pl : plonl)
            			pl.closeInventory();
                sender.sendMessage(String.format("%s%s is now %s", ChatColor.GREEN, parametr, (GUI.enabled?"enabled.":"disabled.")));
            	return true;
            }
            case ("top"):{
            	GUI.topGUI((Player) sender);
            	return true;
            }
            case ("help"):{
            	if (sender.hasPermission("Oneblock.set"))
            		sender.sendMessage(Messages.help_adm);
            	else
            		sender.sendMessage(Messages.help);

            	return true;
            }
            default:
            //ver
            sender.sendMessage(String.format("%s%s\n%s\n%s\n%s\n%s%s 1.%d",
            	ChatColor.values()[rnd.nextInt(ChatColor.values().length)],
            	"  ▄▄    ▄▄",
            	"█    █  █▄▀",
            	"▀▄▄▀ █▄▀",
            	"Create by MrMarL\nPlugin version: v1.0.4",
            	"Server version: ", superlegacy?"super legacy":(legacy?"legacy":""), XMaterial.getVersion()));
            return true;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Error.");
            return false;
        }
    }
    
    ArrayList<Player> PlLst(int id) {
    	ArrayList<Player> pls = new ArrayList<Player>();
    	for (Player ponl: plonl)
    		if (id == PlayerInfo.GetId(ponl.getName()))
    			pls.add(ponl);
    	return pls;
    }

    private void Datafile() {
    	File PlData = new File(getDataFolder(), "PlData.json");
		if (PlData.exists())
			PlayerInfo.list = JsonSimple.Read(PlData);
		else
			PlayerInfo.list = ReadOldData.Read(new File(getDataFolder(), "PlData.yml"));
    }
    
    private void ReCreateRegions() {
    	if (!WorldGuard || !OBWorldGuard.canUse)
    		return;
    	int id = PlayerInfo.size();
    	OBWG.RemoveRegions(id);
		for (int i = 0; i < id; i++) {
			PlayerInfo owner = PlayerInfo.get(i);
			if (owner.nick == null)
    			continue;
            int result[] = getFullCoord(i, 0, 0);
            int X_pl = result[0], Z_pl = result[1];
			Vector Block1 = new Vector(X_pl - sto/2 + 1, 0, Z_pl - sto/2 + 1);
        	Vector Block2 = new Vector(X_pl + sto/2 - 1, 255, Z_pl + sto/2 - 1);
        	OBWG.CreateRegion(owner.nick, Block1, Block2, i);
            for (String member: owner.nicks) 
            	OBWG.addMember(member, i);
        }
    }

    public void saveData() {
        try {
        	File PlData = new File(getDataFolder(), "PlData.json");
    		JsonSimple.Write(PlayerInfo.list, PlData);
        } 
        catch (Exception e) { e.printStackTrace(); }
    }

    private void Blockfile() {
    	blocks.clear();
    	Level.levels.clear();
        File block = new File(getDataFolder(), "blocks.yml");
        if (!block.exists())
            saveResource("blocks.yml", false);
        newConfigz = YamlConfiguration.loadConfiguration(block);
        if (newConfigz.isString("MaxLevel"))
        	Level.max.name = newConfigz.getString("MaxLevel");
        for (int i = 0; newConfigz.isList(String.format("%d", i)); i++) {
        	List <String> bl_temp = newConfigz.getStringList(String.format("%d", i));
        	Level level = new Level(bl_temp.get(0));
        	Level.levels.add(level);
        	int q = 1;
        	if (Progress_bar && q<bl_temp.size())
        		try {
        			level.color = BarColor.valueOf(bl_temp.get(1));
        			q++;
        		} catch(Exception e) {level.color = Progress_color;}
        	while (q < bl_temp.size()) {
        		String text = bl_temp.get(q++);
        		Optional <XMaterial> a = XMaterial.matchXMaterial(text);
        		if (text.charAt(0) == '/') 
	            	blocks.add(text.replaceFirst("/", ""));
        		else if (!a.isPresent() || a.get() == GRASS_BLOCK)
	                blocks.add(null);//Material.matchMaterial(text)
	            else
	                blocks.add(a.get());
        	}
        	level.size = blocks.size();
        }
        Level.max.size = blocks.size();
        //Progress_bar
        if (!superlegacy && Progress_bar && PlayerInfo.size() > 0 && PlayerInfo.get(0).bar == null) {
        	Level.max.color = Progress_color;
            for (PlayerInfo inf:PlayerInfo.list) {
                Level lvl = Level.get(inf.lvl);
                inf.bar = Bukkit.createBossBar(lvl_bar_mode?lvl.name:TextP, lvl.color, BarStyle.SEGMENTED_10, BarFlag.DARKEN_SKY);
            }
            Bukkit.getPluginManager().registerEvents(new ChangedWorld(), this);
    	}
    }
    private void Messagefile() {
        File message = new File(getDataFolder(), "messages.yml");
        if (!message.exists())
            saveResource("messages.yml", false);
        newConfigz = YamlConfiguration.loadConfiguration(message);
        
        Messages.noperm = MessageCheck("noperm", Messages.noperm);
        Messages.help = MessageCheck("help", Messages.help);
        Messages.help_adm = MessageCheck("help_adm", Messages.help_adm);
        Messages.invite_usage = MessageCheck("invite_usage", Messages.invite_usage);
        Messages.invite_yourself = MessageCheck("invite_yourself", Messages.invite_yourself);
        Messages.invite_no_island = MessageCheck("invite_no_island", Messages.invite_no_island);
        Messages.invite_team = MessageCheck("invite_team", Messages.invite_team);
        Messages.invited = MessageCheck("invited", Messages.invited);
        Messages.invited_succes = MessageCheck("invited_succes", Messages.invited_succes);
        Messages.kick_usage = MessageCheck("kick_usage", Messages.kick_usage);
        Messages.kick_yourself = MessageCheck("kick_yourself", Messages.kick_yourself);
        Messages.accept_succes = MessageCheck("accept_succes", Messages.accept_succes);
        Messages.accept_none = MessageCheck("accept_none", Messages.accept_none);
        Messages.idreset = MessageCheck("idreset", Messages.idreset);
        Messages.protection = MessageCheck("protection", Messages.protection);
    }
    private String MessageCheck(String name, String def_message) {
    	if (newConfigz.isString(name))
        	return ChatColor.translateAlternateColorCodes('&',newConfigz.getString(name));
    	return def_message;
    }
    private void Flowerfile() {
        flowers.clear();
        File flower = new File(getDataFolder(), "flowers.yml");
        if (!flower.exists())
            saveResource("flowers.yml", false);
        newConfigz = YamlConfiguration.loadConfiguration(flower);
        flowers.add(GRASS);
        for(String list:newConfigz.getStringList("flowers"))
        	if (!XMaterial.matchXMaterial(list).isPresent())
        		flowers.add(GRASS);
        	else
        		flowers.add(XMaterial.matchXMaterial(list).get());
    }
    private void Chestfile() {
        s_ch = new ArrayList <Material>();
        m_ch = new ArrayList <Material>();
        h_ch = new ArrayList <Material>();
        File chest = new File(getDataFolder(), "chests.yml");
        if (!chest.exists())
            saveResource("chests.yml", false);
        newConfigz = YamlConfiguration.loadConfiguration(chest);
        for (String s: newConfigz.getStringList("small_chest")) 
        	s_ch.add(Material.getMaterial(s));
        for (String s: newConfigz.getStringList("medium_chest")) 
        	m_ch.add(Material.getMaterial(s));
        for (String s: newConfigz.getStringList("high_chest")) 
        	h_ch.add(Material.getMaterial(s));
    }
    private void Mobfile() {
        mobs.clear();
        File mob = new File(getDataFolder(), "mobs.yml");
        if (!mob.exists())
            saveResource("mobs.yml", false);
        newConfigz = YamlConfiguration.loadConfiguration(mob);
        for (int i = 0; newConfigz.isString("id" + i); i++) {
        	try { mobs.add(EntityType.valueOf((newConfigz.getString("id" + i))));
            } catch (Exception ex) {/* not supported mob */}
        }
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
        config = this.getConfig();
        wor = Bukkit.getWorld(Check("world", "world"));
        x = (int) Check("x", (double) x);
        y = (int) Check("y", (double) y);
        z = (int) Check("z", (double) z);
        //leave - leaf
        if (config.isString("leafworld")) {
        	config.set("leaveworld", config.getString("leafworld"));
        	config.set("leafworld", null);
        }
        if (config.isSet("xleaf")) {
        	config.set("xleave", config.getDouble("xleaf"));
        	config.set("xleaf", null);
        }
        if (config.isSet("yleaf")) {
        	config.set("yleave", config.getDouble("yleaf"));
        	config.set("yleaf", null);
        }
        if (config.isSet("zleaf")) {
        	config.set("zleave", config.getDouble("zleaf"));
        	config.set("zleaf", null);
        }
        leavewor = Bukkit.getWorld(Check("leaveworld", "world"));
        Check("xleave", 0.0);
        Check("yleave", 0.0);
        Check("zleave", 0.0);
        Check("yawleave", 0.0);
        Progress_bar = Check("Progress_bar", true);
        if (superlegacy)
            Progress_bar = false;
        if (!config.isInt("frequency"))
            config.set("frequency", 7L);
        fr = config.getLong("frequency");
        //Text
        if (!superlegacy) {
	        TextP = Check("Progress_bar_text", "level");
	        if (TextP.equals("level"))
	            lvl_bar_mode = true;
        }
        //alert
        chat_alert = Check("Chat_alert", !lvl_bar_mode);
        if (Progress_bar)
            Progress_color = BarColor.valueOf(Check("Progress_bar_color", "GREEN"));
        il3x3 = Check("Island_for_new_players", true);
        rebirth = Check("Rebirth_on_the_island", true);
        lvl_mult = Check("level_multiplier", lvl_mult);
        max_players_team = Check("max_players_team", max_players_team);
        UpdateParametrs();// СircleMode;protection;autojoin;droptossup
        GUI.enabled = Check("gui", GUI.enabled);
        WorldGuard = Check("WorldGuard", WorldGuard);
        OBWorldGuard.flags = Check("WGflags", OBWorldGuard.flags);
        sto = Check("set", 100);
        if (config.isSet("custom_island") && !legacy)
        	Island.read(config);
        Config.Save(config, con);
    }
    
    public void UpdateParametrs() {
    	СircleMode = Check("СircleMode", СircleMode);
    	UseEmptyIslands = Check("useemptyislands", UseEmptyIslands);
        protection = Check("protection", protection);
        autojoin = Check("autojoin", autojoin);
        droptossup = Check("droptossup", droptossup);
        physics = Check("physics", physics);
    }
    
    public static int getlvl(String pl_name) {
    	return PlayerInfo.get(pl_name).lvl;
    }
    public static int getnextlvl(String pl_name) {
    	return getlvl(pl_name)+1;
    }
    public static String getlvlname(String pl_name) {
    	int lvl = getlvl(pl_name);
    	return Level.get(lvl).name;
    }
    public static String getnextlvlname(String pl_name) {
    	int lvl = getnextlvl(pl_name);
    	return Level.get(lvl).name;
    }
    public static int getblocks(String pl_name) {
        return PlayerInfo.get(pl_name).breaks;
    }
    public static int getneed(String pl_name) {
        PlayerInfo id_pl = PlayerInfo.get(pl_name);
        return 16 + id_pl.lvl * lvl_mult - id_pl.breaks;
    }
    @SuppressWarnings("unchecked")
	public static PlayerInfo gettop(int i) {
    	if (PlayerInfo.size() <= i)
    		return new PlayerInfo("[None]");
    	ArrayList<PlayerInfo> ppii = (ArrayList<PlayerInfo>) PlayerInfo.list.clone();
    	Collections.sort(ppii, PlayerInfo.COMPARE_BY_LVL);
    	if (ppii.get(i).nick == null)
    		return new PlayerInfo("[None]");
        return ppii.get(i);
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> commands = new ArrayList<>();

        if (args.length == 1) {
        	commands.addAll(Arrays.asList("j","join","leave","invite","accept","kick","ver","IDreset","help","gui","top"));
            if (sender.hasPermission("Oneblock.set")) {
            	commands.addAll(Arrays.asList("set","setleave","Progress_bar","chat_alert","setlevel","clear","circlemode","lvl_mult","max_players_team",
            		"reload","frequency","islands","island_rebirth","protection","worldguard","listlvl","autoJoin","droptossup","physics","UseEmptyIslands"));
            }
        } else if (args.length == 2) {
        	if (args[0].equals("invite") || args[0].equals("kick")) {
        		for (Player ponl: plonl)
        			commands.add(ponl.getName());
        	}
        	else if (sender.hasPermission("Oneblock.set")) {
        		switch (args[0])
                {
                case ("clear"):
                case ("setlevel"):{
            		for (Player ponl: plonl)
            			commands.add(ponl.getName());
            		break;
            	}
                case ("Progress_bar"):{
	                commands.add("true");
	                commands.add("false");
	                commands.add("level");
	                commands.add("settext");
	                commands.add("color");
	                break;
	            }
                case ("reload"):{
	                commands.add("blocks.yml");
	                commands.add("chests.yml");
	                commands.add("mobs.yml");
	                commands.add("flowers.yml");
	                commands.add("messages.yml");
	                break;
	            }
                case ("islands"):
	                commands.add("set_my_by_def");
	                commands.add("default");
                case ("UseEmptyIslands"):
                case ("island_rebirth"):
                case ("protection"):
                case ("circlemode"):
                case ("worldguard"):
                case ("autoJoin"):
                case ("droptossup"):
                case ("physics"):
                case ("gui"):
	                commands.add("true");
	                commands.add("false");
	                break;
                case ("listlvl"):
	            	for(int i = 0;i<Level.size();)
	            		commands.add(String.format("%d", i++));
	            	break;
                case ("lvl_mult"):
                case ("max_players_team"):
                	for(int i = 0;i<4;)
	            		commands.add(String.format("%d", i++));
                case ("frequency"):
                	for(int i = 4;i<=20;)
	            		commands.add(String.format("%d", i++));
	            	break;
                case ("set"):
                	commands.add("100");
                	commands.add("500");
                }
        	}
        }
        else if (sender.hasPermission("Oneblock.set") && args.length == 3) 
        	if (args[0].equals("Progress_bar")) {
        		if (args[1].equals("color"))
        			for (BarColor bc:BarColor.values())
        				commands.add(bc.name());
        		if (args[1].equals("settext")) {
        			commands.add("...");
        			if (PAPI)
        				commands.add("%OB_lvl_name%. There are %OB_need_to_lvl_up% block(s) left.");
        		}
        	}
        	else if (args[0].equals("setlevel"))
            	for (int i = 0;i<Level.size();)
            		commands.add(String.format("%d", i++));
        Collections.sort(commands);
        return commands;
    }
}