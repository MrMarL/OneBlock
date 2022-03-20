// Copyright © 2022 MrMarL. All rights reserved.
package Oneblock;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import me.clip.placeholderapi.PlaceholderAPI;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.data.BlockData;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Oneblock extends JavaPlugin {
    boolean on = false;
    static int x = 0;
    static int y = 0;
    static int z = 0;
    Random rnd = new Random(System.currentTimeMillis());
    int id = 0;
    static ArrayList <PlayerInfo> pInf = new ArrayList <>();
    FileConfiguration config, newConfigz;
    static World wor;
	World leavewor;
    int random = 0;
    boolean superlegacy, legacy;
    String version = "";
    ArrayList <Object> blocks = new ArrayList <>();
    ArrayList <Material> s_ch, m_ch, h_ch;
    ArrayList <EntityType> mobs = new ArrayList <>();
    ArrayList <XMaterial> flowers = new ArrayList <>();
    static ArrayList <Level> levels = new ArrayList <>();
    static Level max_lvl = new Level("Level: MAX");
    static List <Player> plonl;
    static int lvl_mult = 5;
    String TextP = "";
    int sto = 100;
    Long fr;
    BarColor Progress_color;
    boolean il3x3 = false, rebirth = false, autojoin = false;
    boolean lvl_bar_mode = false, chat_alert = false;
    boolean protection = false;
    boolean PAPI = false;
    boolean WorldGuard = false;
    boolean Progress_bar = true;
    boolean СircleMode = false;
    OBWorldGuard OBWorldGuard;
    BlockData[][][] island = null;
    static ArrayList <Invitation> invite = new ArrayList<>();
    XMaterial GRASS_BLOCK = XMaterial.GRASS_BLOCK, GRASS = XMaterial.GRASS;
    String noperm = String.format("%sYou don't have permission [Oneblock.set].", ChatColor.RED);
    @Override
    public void onEnable() {
    	version = "1." + XMaterial.getVersion();
        superlegacy = !XMaterial.supports(9);// Is version 1.9 supported?
        legacy = !XMaterial.supports(13);// Is version 1.13 supported?
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
        	Bukkit.getConsoleSender().sendMessage("[OneBlock] PlaceholderAPI has been found!");
            PAPI = true;
            new OBP().register();
        }
        Configfile();
        Datafile();
        Blockfile();
        Flowerfile();
        Chestfile();
        Mobfile();
        if (config.getDouble("y") != 0) {
            if (wor == null || (config.getDouble("yleave") != 0 && leavewor == null)) {
                Bukkit.getScheduler().runTaskTimer((Plugin) this, (Runnable) new wor_null(), 32, 64);
            } 
            else wor_ok();
        }
        Bukkit.getPluginManager().registerEvents(new Resp_AutoJ(), this);
    }
    public class Resp_AutoJ implements Listener {
        @EventHandler
        public void Resp(PlayerRespawnEvent e) {
            if (rebirth) {
            	Player pl = e.getPlayer();
                if (pl.getWorld().equals(wor))
                    if (ExistId(pl.getName())) {
						int result[] = getFullCoord(GetId(pl.getName()), 0, 0);
						e.setRespawnLocation(new Location(wor, result[0] + 0.5, y + 1.2013, result[1] + 0.5));
                    }
            }
        }
        @EventHandler
        public void AutoJ(PlayerTeleportEvent e) {
        if (autojoin){
    		Location loc = e.getTo();
			World from = e.getFrom().getWorld();
    		World to = loc.getWorld();
        	if (!from.equals(wor) && to.equals(wor) && !(loc.getY() == y+1.2013)){
        		e.setCancelled(true);
        		e.getPlayer().performCommand("ob j");
        		}
        	}
        }
        @EventHandler
        public void JAuto(PlayerJoinEvent e) {
        if (autojoin){
        	Player pl = e.getPlayer();
        	if (pl.getWorld().equals(wor))
        		pl.performCommand("ob j");
        	}
        }
    }
    public class ChangedWorld implements Listener {
    	@EventHandler
        public void PlayerChangedWorldEvent(PlayerChangedWorldEvent e) {
    		Player p = e.getPlayer(); World from = e.getFrom();
        	if (from.equals(wor)) {
        		int i = GetId(p.getName());
        		if (i<pInf.size())
        			pInf.get(i).bar.removePlayer(p);
        	}
        }
    }
    public class wor_null implements Runnable {
        public void run() {
            if (wor == null) {
                Bukkit.getConsoleSender().sendMessage(String.format("\n%s\n%s",
                		"[OB] WORLD INITIALIZATION ERROR! world = null",
                		"[OB] Trying to initialize the world again..."));
                wor = Bukkit.getWorld(config.getString("world"));
                leavewor = Bukkit.getWorld(config.getString("leaveworld"));
            } else {
                Bukkit.getConsoleSender().sendMessage("[OB] The initialization of the world was successful!");
                wor_ok();
            }
        }
    }
    public void wor_ok() {
        Bukkit.getScheduler().cancelTasks(this);
        if (config.getDouble("y") != 0) {
            Bukkit.getScheduler().runTaskTimer((Plugin) this, (Runnable) new Task(), fr, fr * 2);
            on = true;
        }
        if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
			Bukkit.getConsoleSender().sendMessage("[OneBlock] WorldGuard has been found!");
			WorldGuard = true;
			if (legacy)
				OBWorldGuard = new OBWorldGuard6();
			else
				OBWorldGuard = new OBWorldGuard7();
        }
    }
    public void addinvite(String name, String to) {
    	for(Invitation item:invite) 
			if (item.equals(name, to))
				return;
    	Invitation inv_ = new Invitation(name, to);
    	invite.add(inv_);
    	Bukkit.getScheduler().runTaskLaterAsynchronously((Plugin) this, new Runnable() {
    		@Override
    		public void run() {invite.remove(inv_);}}, 300L);
    }
    public boolean checkinvite(Player pl) {
		String name = pl.getName();
		Invitation inv_ = null; 
		for(Invitation item:invite) 
			if (item.Invited.equals(name)) 
				inv_ = item; 
		
		if (inv_ == null || !ExistId(inv_.Inviting))
			return false;
		 
		if (ExistId(name)) {
			if (Progress_bar)
				pInf.get(GetId(name)).bar.removePlayer(pl);
			pl.performCommand("ob idreset /n");
		}
		pInf.get(GetId(inv_.Inviting)).nicks.add(name);
		pl.performCommand("ob j"); 
		invite.remove(inv_);
		return true; 
    }

	public int[] getFullCoord(int id, int x, int z) {
		if (!СircleMode)
			return new int[] {id * sto + Oneblock.x, Oneblock.z};
		for (int i = 0; i < id; i++) {
			if (x == z)
				if (z < 0)
					z++;
				else
					x++;
			else if (x > z && -z < x)
				z--;
			else if (x <= -z && x > z)
				x--;
			else if (-x > z)
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
            	if (!ExistId(name))
            		continue;
                int plID = GetId(name);
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
                		ponl.sendMessage(String.format("%sare you trying to go %soutside the island?", ChatColor.YELLOW, ChatColor.RED));
                		continue;
                	}
                }
                Block block = wor.getBlockAt(X_pl, y, Z_pl);
                if (block.getType().equals(Material.AIR)) {
                	PlayerInfo inf = pInf.get(plID);
                	Level lvl_inf = max_lvl; 
                	if (inf.lvl < levels.size())
                		lvl_inf = levels.get(inf.lvl);
                	inf.breaks++;
                    if (inf.breaks >= 16 + inf.lvl * lvl_mult) {
                    	inf.lvlup();
                    	lvl_inf = max_lvl; 
                    	if (inf.lvl < levels.size())
                    		lvl_inf = levels.get(inf.lvl);
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
                    Location loc = ponl.getLocation();
                    if (loc.getBlockX() == X_pl && loc.getY() - 1 < y && loc.getBlockZ() == Z_pl) {
                        loc.setY(y+1);
                        ponl.teleport(loc);
                    }
                    else
                    	for(Player pll :PlLst(plID)) {
                    		loc = pll.getLocation();
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
                            if (random < 26)
                            	ch_now = s_ch;
                            else if (random < 68)
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
                            Bukkit.getConsoleSender().sendMessage("[OB] Error when generating items for the chest! Pls redo chests.yml!");
                        }
                    } else
                    	XBlock.setType(block, blocks.get(random));

                    if (rnd.nextInt(9) == 0) {
                        if (inf.lvl < blocks.size() / 9)
                            random = rnd.nextInt(mobs.size() / 3);
                        else if (inf.lvl < blocks.size() / 9 * 2)
                            random = rnd.nextInt(mobs.size() / 3 * 2);
                        else
                            random = rnd.nextInt(mobs.size());
                        wor.spawnEntity(new Location(wor, X_pl, y + 1, Z_pl), mobs.get(random));
                    }
                }
            }
        }
    }

    public void onDisable() {
    	try {
    		File PlData = new File(getDataFolder(), "PlData.json");
    		JsonSimple.Write(id, pInf, PlData);
		} catch (Exception e) {}
	
        if (island != null) {
            HashMap <String, List <String>> map = new HashMap <String, List <String>>();
            List <String> y_now = new ArrayList <String>();
            for (int yy = 0;yy<3;yy++) {
            	y_now.clear();
	            for (int xx = 0; xx < 7; xx++)
	                for (int zz = 0; zz < 7; zz++)
	                	y_now.add(island[xx][yy][zz].getAsString());
	            map.put(String.format("y%d",yy), y_now);
            }
            config.set("custom_island", map);
        }
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
            switch (args[0].toLowerCase())
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
                if (!ExistId(name)) {
                	plID = id; //GenType
                    int result[] = getFullCoord(plID, X_pl, Z_pl);
                    X_pl = result[0]; Z_pl = result[1];
                    if (il3x3) {
                    	if (island != null) {
                    		int px = X_pl - 3;
                            for (int xx = 0; xx < 7; xx++)
                            	for (int yy = 0; yy < 3; yy++)
                                	for (int zz = 0; zz < 7; zz++)
                                    	wor.getBlockAt(px + xx, y + yy, Z_pl - 3 + zz).setBlockData(island[xx][yy][zz]);
                        } else {
                        	for (int i = -2;i<=2;i++)
                        		for (int q = -2;q<=2;q++)
                        			if (Math.abs(i)+Math.abs(q) < 3)
                        				XBlock.setType(wor.getBlockAt(X_pl + i, y, Z_pl+ q),GRASS_BLOCK);
                        }
                    }
                    //WorldGuard
                    if (WorldGuard && OBWorldGuard.canUse) {
                    	Vector Block1 = new Vector(X_pl - sto/2 + 1, 0, Z_pl - sto/2 + 1);
                    	Vector Block2 = new Vector(X_pl + sto/2 - 1, 255, Z_pl + sto/2 - 1);
	                    OBWorldGuard.CreateRegion(name, Block1, Block2, id);
                    }	
                    id++;
                    saveData();
                    PlayerInfo inf = new PlayerInfo();
                    pInf.add(inf);
                    inf.nick = name;
                    if (!superlegacy && Progress_bar) {
                    	String temp = TextP;
                        if (lvl_bar_mode)
                        	temp = levels.get(0).name;
                        else if (PAPI)
                        	temp = PlaceholderAPI.setPlaceholders(p, TextP);
                        inf.bar = (Bukkit.createBossBar(temp, levels.get(0).color, BarStyle.SEGMENTED_10, BarFlag.DARKEN_SKY));
                    }
                } 
                else {
                	plID = GetId(name); // GenType
                	int result[] = getFullCoord(plID, X_pl, Z_pl);
                    X_pl = result[0]; Z_pl = result[1];
                }
                if (!on) {
                    Bukkit.getScheduler().runTaskTimer((Plugin) this, (Runnable) new Task(), fr, fr * 2);
                    on = true;
                }
                if (Progress_bar)
                	pInf.get(plID).bar.setVisible(true);
                p.teleport(new Location(wor, X_pl + 0.5, y + 1.2013, Z_pl + 0.5));
                if (WorldGuard && OBWorldGuard.canUse) {
                	OBWorldGuard.addMember(name, plID);
                }
                return true;
            }
            case ("leave"):{
                Player p = (Player) sender;
                if (!superlegacy) {
                	PlayerInfo p_inf = pInf.get(GetId(p.getName()));
                	if (p_inf.bar != null)
                		p_inf.bar.removePlayer(p);
                	}
                if (config.getDouble("yleave") == 0 || leavewor == null)
                    return true;
                p.teleport(new Location(leavewor, config.getDouble("xleave"), config.getDouble("yleave"), config.getDouble("zleave"),
                		(float)config.getDouble("yawleave"), 0f));
                return true;
            }
            case ("set"):{
                if (!sender.hasPermission("Oneblock.set")) {
                    sender.sendMessage(noperm);
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
                if (OBWorldGuard == null) wor_ok();
                wor.getBlockAt(x, y, z).setType(GRASS_BLOCK.parseMaterial());
                ReCreateRegions();
                return true;
            }
            case ("setleave"):{
                if (!sender.hasPermission("Oneblock.set")) {
                    sender.sendMessage(noperm);
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
                    sender.sendMessage(String.format("%sYou don't have the permission to execute this command", ChatColor.RED));
                    return true;
                }
            	if (args.length < 2) {
            		sender.sendMessage(String.format("%sUsage: /ob invite <username>", ChatColor.RED));
            		return true;
            	}
            	Player inv = Bukkit.getPlayer(args[1]);
            	if (inv != null) {
            		if (inv == (Player) sender) {
            			sender.sendMessage(String.format("%sYou can't invite yourself.", ChatColor.YELLOW));
            			return true;
            		}
            		if (!ExistId(((Player)sender).getName())) {
            			sender.sendMessage(String.format("%sPlease create a island before you do this.", ChatColor.YELLOW));
            			return true;
            		}
            		addinvite(((Player) sender).getName(),inv.getName());
            		inv.sendMessage(String.format("%sYou were invited by player %s.\n%s/ob accept to accept).",
            				ChatColor.GREEN, ((Player) sender).getName(), ChatColor.RED));
            		sender.sendMessage(String.format("%sSuccesfully invited %s.", ChatColor.GREEN, inv.getName()));
            	}
            	return true;
            }
            case ("kick"):{
            	if (args.length < 2) {
            		sender.sendMessage(String.format("%sUsage: /ob invite <username>", ChatColor.RED));
            		return true;
            	}
            	Player inv = Bukkit.getPlayer(args[1]);
            	String name = ((Player) sender).getName();
            	if (!ExistNoInvaitId(name))
            		return true;
            	if (inv != null) {
            		if (inv == (Player) sender) {
            			sender.sendMessage(String.format("%sYou can't kick yourself.", ChatColor.YELLOW));
            			return true;
            		}
            		if (pInf.get(GetId(name)).nicks.contains(args[1])) {
            			pInf.get(GetId(name)).nicks.remove(args[1]);
            			if (WorldGuard && OBWorldGuard.canUse)
            				OBWorldGuard.removeMember(inv.getName(), GetId(name));
            			inv.performCommand("ob j");
            			return true;
            		}
            	}
            	else if (pInf.get(GetId(name)).nicks.contains(args[1])) {
            		pInf.get(GetId(name)).nicks.remove(args[1]);
            		sender.sendMessage(String.format("%sYou can't kick yourself.", ChatColor.YELLOW));
            	}
            	return true;
            }
            case ("accept"):{
            	Player pl = (Player) sender;
           	 	if (checkinvite(pl))
           	 		sender.sendMessage(String.format("%sSuccesfully accepted the invitation.", ChatColor.GREEN));
           	 	else
           	 		sender.sendMessage(String.format("%s[There is no Pending invitations for you.]",ChatColor.RED));
           		return true;
            }
            case ("idreset"):{
            	Player pl = (Player)sender;
            	String name = pl.getName();
            	if (!ExistId(name))
            		return true;
            	int PlId = GetId(name);
            	if (Progress_bar)
        			pInf.get(PlId).bar.removePlayer(pl);
            	PlayerInfo plp = pInf.get(PlId);
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
            		OBWorldGuard.removeMember(name, PlId);
            	if (!args[args.length-1].equals("/n"))
            		sender.sendMessage(String.format("%sNow your data has been reset. You can create a new island /ob join.", ChatColor.GREEN));
            	return true;
            }
            case ("protection"):{
            	if (!sender.hasPermission("Oneblock.set")) {
                    sender.sendMessage(noperm);
                    return true;
                }
            	if (args.length > 1 &&
                	(args[1].equals("true") || args[1].equals("false"))) {
                    	protection = Boolean.valueOf(args[1]);
                    	config.set("protection", protection);
                }
                else
                	sender.sendMessage(String.format("%senter a valid value true or false", ChatColor.YELLOW));
            	sender.sendMessage(String.format("%sthe protection is now %s", ChatColor.GREEN, (protection?"enabled.":"disabled.")));
           		return true;
            }
            case ("worldguard"):{
            	if (!sender.hasPermission("Oneblock.set")) {
                    sender.sendMessage(noperm);
                    return true;
                }
            	if (!Bukkit.getPluginManager().isPluginEnabled("WorldGuard")){
                    sender.sendMessage(String.format("%sThe WorldGuard plugin was not detected!", ChatColor.YELLOW));
                    return true;
                }
            	if (OBWorldGuard == null || !OBWorldGuard.canUse) {
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
                    		OBWorldGuard.RemoveRegions(id);
                }
                else
                	sender.sendMessage(String.format("%senter a valid value true or false", ChatColor.YELLOW));
            	sender.sendMessage(String.format("%sthe OBWorldGuard is now %s", ChatColor.GREEN, (WorldGuard?"enabled.":"disabled.")));
           		return true;
            }
            case ("autojoin"):{
            	if (!sender.hasPermission("Oneblock.set")) {
                    sender.sendMessage(noperm);
                    return true;
                }
            	if (args.length > 1 &&
                    	(args[1].equals("true") || args[1].equals("false"))) {
                    	autojoin = Boolean.valueOf(args[1]);
                    	config.set("autojoin", autojoin);	
                }
                else
                	sender.sendMessage(String.format("%senter a valid value true or false", ChatColor.YELLOW));
                sender.sendMessage(String.format("%sautojoin is now %s", ChatColor.GREEN, (autojoin?"enabled.":"disabled.")));
           		return true;
            }
            case ("circlemode"):{
            	if (!sender.hasPermission("Oneblock.set")) {
                    sender.sendMessage(noperm);
                    return true;
                }
            	if (args.length > 1 &&
						(args[1].equals("true") || args[1].equals("false"))) {
					СircleMode = Boolean.valueOf(args[1]);
					config.set("СircleMode", СircleMode);	
                }
                else
                	sender.sendMessage(String.format("%senter a valid value true or false", ChatColor.YELLOW));
                sender.sendMessage(String.format("%sСircleMode is now %s", ChatColor.GREEN, (СircleMode?"enabled.":"disabled.")));
           		return true;
            }
            //LVL
            case ("setlevel"):{
                if (!sender.hasPermission("Oneblock.set")) {
                    sender.sendMessage(noperm);
                    return true;
                }
                if (args.length <= 2) {
                    sender.sendMessage(String.format("%sinvalid format. try: /ob setlevel 'nickname' 'level'", ChatColor.RED));
                    return true;
                }
                if (ExistId(args[1])) {
                    int setlvl = 0;
                    try {
                        setlvl = Integer.parseInt(args[2]);
                    } catch (NumberFormatException nfe) {
                        sender.sendMessage(String.format("%sinvalid level value.", ChatColor.RED));
                        return true;
                    }
                    if (setlvl >= 0 && 10000 > setlvl) {
                        int i = GetId(args[1]);
                        PlayerInfo inf = pInf.get(i);
                        inf.breaks = 0;
                        inf.lvl = setlvl;
                        if (lvl_bar_mode) {
                        	Level lvl = max_lvl;
                        	if (inf.lvl < levels.size())
                        		lvl = levels.get(inf.lvl);
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
                    sender.sendMessage(noperm);
                    return true;
                }
                if (args.length <= 1) {
                    sender.sendMessage(String.format("%sinvalid format. try: /ob clear 'nickname'", ChatColor.RED));
                    return true;
                }
                if (ExistId(args[1])) {
                    int i = GetId(args[1]);
                    PlayerInfo inf = pInf.get(i);
                    inf.breaks = 0;
                    inf.lvl = 0;
                    if (Progress_bar)
                    	inf.bar.setVisible(false);
                    int x_now = x + i * 100 - 12, y_now = y - 6, z_now = z - 12;
                    if (y_now <= 1)
                        y_now = 1;
                    for (int xx = 0; xx < 24; xx++)
                        for (int yy = 0; yy < 16; yy++)
                            for (int zz = 0; zz < 24; zz++)
                                wor.getBlockAt(x_now + xx, y_now + yy, z_now + zz).setType(Material.AIR);
                    sender.sendMessage(String.format("%splayer %s island is destroyed! :D", ChatColor.GREEN, args[1]));
                    return true;
                }
                sender.sendMessage(String.format("%sa player named %s was not found.", ChatColor.RED, args[1]));
                return true;
            }
            case ("lvl_mult"):{
                if (!sender.hasPermission("Oneblock.set")) {
                    sender.sendMessage(noperm);
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
            case ("progress_bar"):{
                if (!sender.hasPermission("Oneblock.set")) {
                    sender.sendMessage(noperm);
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
                    for (PlayerInfo bb:pInf)
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
                        for (PlayerInfo bb:pInf)
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
                    if (!lvl_bar_mode) {
                        lvl_bar_mode = true;
                        for (PlayerInfo inf:pInf)
	                        if (inf.lvl >= levels.size())
	                        	inf.bar.setTitle(max_lvl.name);
	                    	else
	                    		inf.bar.setTitle(levels.get(inf.lvl).name);
                        config.set("Progress_bar_text", "level");
                        return true;
                    } else {
                        lvl_bar_mode = false;
                        for (PlayerInfo bb:pInf)
                            bb.bar.setTitle("Progress bar");
                        config.set("Progress_bar_text", "Progress bar");
                        return true;
                    }
                }
                if (args[1].equalsIgnoreCase("settext")) {
                	if (!Progress_bar)
                		return true;
                    String txt_bar = "";
                    for (int i = 2; i < args.length - 1; i++)
                        txt_bar += args[i] + " ";
                    txt_bar += args[args.length - 1];
                    lvl_bar_mode = false;
                    for (PlayerInfo bb:pInf)
                        bb.bar.setTitle(txt_bar);
                    config.set("Progress_bar_text", txt_bar);
                    TextP = txt_bar;
                    if (PAPI)
                        for (Player ponl: Bukkit.getOnlinePlayers())
                            pInf.get(GetId(ponl.getName())).bar.setTitle(PlaceholderAPI.setPlaceholders(ponl, txt_bar));
                    return true;
                }
                sender.sendMessage(String.format("%strue, false, settext or level only!", ChatColor.RED));
                return true;
            }
            case ("listlvl"):{
                if (!sender.hasPermission("Oneblock.set")) {
                    sender.sendMessage(noperm);
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
                    if (levels.size()<=temp||temp<0) {
                    	sender.sendMessage(String.format("%sundefined lvl", ChatColor.RED));
                    	return true;
                    }
                    sender.sendMessage(String.format("%s%s",ChatColor.GREEN, levels.get(temp).name));
                    int i = 0;
                    if (temp !=0)
                    	i = levels.get(temp-1).size;
                    for(;i<levels.get(temp).size;i++)
                    	if (blocks.get(i) == null)
                    		sender.sendMessage("Grass or undefined");
                    	else if (blocks.get(i).getClass() == XMaterial.class)
                    		sender.sendMessage(((XMaterial)blocks.get(i)).name());
                    	else
                    		sender.sendMessage((String)blocks.get(i));
                    return true;
                }
                for(int i = 0;i<levels.size();i++)
                	sender.sendMessage(String.format("%d: %s%s", i, ChatColor.GREEN, levels.get(i).name));
                return true;
            }
            case ("reload"):{
                if (!sender.hasPermission("Oneblock.set")) {
                    sender.sendMessage(noperm);
                    return true;
                }
                if (args.length == 1) {
                    sender.sendMessage(String.format("%sReloading Plugin & Plugin Modules.", ChatColor.YELLOW));
                    Blockfile();
                    Flowerfile();
                    Chestfile();
                    Mobfile();
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
                sender.sendMessage(String.format("%sTry blocks.yml or chests.yml", ChatColor.RED));
                return true;
            }
            case ("chat_alert"):{
                if (!sender.hasPermission("Oneblock.set")) {
                    sender.sendMessage(noperm);
                    return true;
                }
                chat_alert = !chat_alert;
                sender.sendMessage(ChatColor.GREEN + (chat_alert?"Alerts are now on!":"Alerts are now disabled!"));
                config.set("Chat_alert", chat_alert);
                return true;
            }
            case ("frequency"):{
                if (!sender.hasPermission("Oneblock.set")) {
                    sender.sendMessage(noperm);
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
                    Bukkit.getScheduler().cancelTasks(this);
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
                    Bukkit.getScheduler().runTaskTimer((Plugin) this, (Runnable) new Task(), fr, fr * 2);
                }
                sender.sendMessage(ChatColor.GREEN + "Now frequency = " + fr + Sfr);
                return true;
            }
            case ("islands"):{
                if (!sender.hasPermission("Oneblock.set")) {
                    sender.sendMessage(noperm);
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
                    if (ExistId(name)) {
                    	if (island == null)
                    		island = new BlockData[7][3][7];
                        int px = x + GetId(name) * sto - 3;
                        for (int xx = 0; xx < 7; xx++)
                            for (int yy = 0; yy < 3; yy++)
                                for (int zz = 0; zz < 7; zz++)
                                    island[xx][yy][zz] = wor.getBlockAt(px + xx, y + yy, z - 3 + zz).getBlockData();
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
                    config.set("custom_island", null);
                    island = null;
                    sender.sendMessage(ChatColor.GREEN + "The default island is installed.");
                    return true;
                }
                sender.sendMessage(ChatColor.YELLOW + "enter a valid value true or false");
                return true;
            }
            case ("island_rebirth"):{
                if (!sender.hasPermission("Oneblock.set")) {
                    sender.sendMessage(noperm);
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
            case ("help"):{
            	sender.sendMessage(ChatColor.GREEN + "OneBlock Plugin Help");
            	boolean admin = sender.hasPermission("Oneblock.set");
            	if (admin)
            	sender.sendMessage(ChatColor.GRAY + "/ob set" + ChatColor.WHITE+" - sets the location of the first island.");
            	sender.sendMessage(ChatColor.GRAY + "/ob j" + ChatColor.WHITE+" - join a new one or your own island.");
            	if (admin)
            	sender.sendMessage(ChatColor.GRAY + "/ob protection" + ChatColor.WHITE+" - does not allow players to leave their island.");
            	sender.sendMessage(ChatColor.GRAY + "/ob invite 'playername'" + ChatColor.WHITE+" - an invitation to the island.\n"+
            					ChatColor.GRAY + "/ob accept" + ChatColor.WHITE+" - to accept an invitation.");
            	if (admin) {
            	sender.sendMessage(ChatColor.GRAY + "/ob islands true" + ChatColor.WHITE+" - islands for new players.\n"+ 
            					ChatColor.GRAY + "/ob islands set_my_by_def" + ChatColor.WHITE+" - sets your island as default for new players.");}
            	sender.sendMessage(ChatColor.GRAY + "/ob IDreset" + ChatColor.WHITE+" - deletes the player's data.");
            	return true;
            }
            default:
            //ver
            sender.sendMessage(String.format("%s%s\n%s\n%s\n%s\n%s%s",
            	ChatColor.values()[rnd.nextInt(ChatColor.values().length)],
            	"  ▄▄    ▄▄",
            	"█    █  █▄▀",
            	"▀▄▄▀ █▄▀",
            	"Create by MrMarL\nPlugin version: v0.9.8",
            	"Server version: ", superlegacy?"super legacy(1.7 - 1.8)":(legacy?"legacy(1.9 - 1.12)":version)));
            return true;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Error.");
            return false;
        }
    }
    
    static int GetId(String name) {
    	for(int i = 0; i<pInf.size() ;i++) {
    		PlayerInfo pl = pInf.get(i);
    		if (pl.nick == null)
    			continue;
    		if (pl.nick.equals(name))
    			return i;
    		if (pl.nicks.contains(name))
    			return i;
    	}
    	return 0;
    }
    
    ArrayList<Player>PlLst(int id) {
    	ArrayList<Player> pls = new ArrayList<Player>();
    	for (Player ponl: plonl)
    		if (ExistId(ponl.getName()))
    			pls.add(ponl);
    	return pls;
    }
    
    boolean ExistNoInvaitId(String name) {
    	for(PlayerInfo pl:pInf) {
    		if (pl.nick == null)
    			continue;
    		if (pl.nick.equals(name))
    			return true;
    	}
    	return false;
    }
    
    boolean ExistId(String name) {
    	for(PlayerInfo pl:pInf) {
    		if (pl.nick == null)
    			continue;
    		if (pl.nick.equals(name))
    			return true;
    		if (pl.nicks.contains(name))
    			return true;
    	}
    	return false;
    }

    private void Datafile() {
    	File PlData = new File(getDataFolder(), "PlData.json");
		if (PlData.exists())
			pInf = JsonSimple.Read(PlData);
		else
			pInf = ReadOldData.Read(new File(getDataFolder(), "PlData.yml"));
		id = pInf.size();
		ReCreateRegions();
    }
    
    private void ReCreateRegions() {
    	if (!WorldGuard || !OBWorldGuard.canUse)
    		return;
    	OBWorldGuard.RemoveRegions(id);
		for (int i = 0; i < id; i++) {
			PlayerInfo owner = pInf.get(i);
			if (owner.nick == null)
    			continue;
			String name = owner.nick;
            int result[] = getFullCoord(GetId(name), 0, 0);
            int X_pl = result[0], Z_pl = result[1];
			Vector Block1 = new Vector(X_pl - sto/2 + 1, 0, Z_pl - sto/2 + 1);
        	Vector Block2 = new Vector(X_pl + sto/2 - 1, 255, Z_pl + sto/2 - 1);
            OBWorldGuard.CreateRegion(name, Block1, Block2, i);
            for (String member: owner.nicks) 
                OBWorldGuard.addMember(member, i);
        }
    }

    public void saveData() {
        try {
        	File PlData = new File(getDataFolder(), "PlData.json");
    		JsonSimple.Write(id, pInf, PlData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Blockfile() {
    	blocks.clear();
        levels.clear();
        File block = new File(getDataFolder(), "blocks.yml");
        if (!block.exists())
            saveResource("blocks.yml", false);
        newConfigz = YamlConfiguration.loadConfiguration(block);
        if (newConfigz.isString("MaxLevel"))
        	max_lvl.name = newConfigz.getString("MaxLevel");
        for (int i = 0; newConfigz.isList(String.format("%d", i)); i++) {
        	List <String> bl_temp = newConfigz.getStringList(String.format("%d", i));
        	Level level = new Level(bl_temp.get(0));
        	levels.add(level);
        	int q = 1;
        	if (Progress_bar && q<bl_temp.size())
        		try {
        			level.color = BarColor.valueOf(bl_temp.get(1));
        			q++;
        		} catch(Exception e) {level.color = Progress_color;}
        	for (;q<bl_temp.size();q++) {
        		String text = bl_temp.get(q);
        		Optional <XMaterial> a = XMaterial.matchXMaterial(text);
        		if (text.charAt(0) == '/') 
	            	blocks.add(text.replaceFirst("/", ""));
        		else if (!a.isPresent() || a.get() == GRASS_BLOCK)
	                blocks.add(null);
	            else
	                blocks.add(a.get());
        	}
        	level.size = blocks.size();
        }
        max_lvl.size = blocks.size();
        //Progress_bar
        if (!superlegacy && Progress_bar && pInf.size() > 0 && pInf.get(0).bar == null) {
            max_lvl.color = Progress_color;
            for (PlayerInfo inf:pInf) {
                Level lvl = max_lvl;
                if (inf.lvl < levels.size())
                	lvl = levels.get(inf.lvl);
                inf.bar = Bukkit.createBossBar(lvl_bar_mode?lvl.name:TextP, lvl.color, BarStyle.SEGMENTED_10, BarFlag.DARKEN_SKY);
            }
            Bukkit.getPluginManager().registerEvents(new ChangedWorld(), this);
    	}
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
            try {
                mobs.add(EntityType.valueOf((newConfigz.getString("id" + i))));
            } catch (Exception ex) {
                // not supported mob)
            }
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
        СircleMode = Check("СircleMode", СircleMode);
        protection = Check("protection", protection);
        if (WorldGuard && OBWorldGuard.canUse)
        	WorldGuard = Check("WorldGuard", WorldGuard);
        autojoin = Check("autojoin", autojoin);
        if (config.isSet("custom_island") && !legacy) {
        	island = new BlockData[7][3][7];
        	for (int yy = 0; yy < 3; yy++) {
	        	List<String> cust_s = config.getStringList(String.format("custom_island.y%d", yy));
	            for (int xx = 0; xx < 7; xx++)
	                for (int zz = 0; zz < 7; zz++)
	                	island[xx][yy][zz] = Bukkit.createBlockData(cust_s.get(7*xx+zz));
        	}
        }
        if (config.isInt("set"))
        	sto = config.getInt("set");
        Config.Save(config, con);
    }
    public static int getlvl(String pl_name) {
    	return pInf.get(GetId(pl_name)).lvl;
    }
    public static int getnextlvl(String pl_name) {
    	return getlvl(pl_name)+1;
    }
    public static String getlvlname(String pl_name) {
    	int lvl = getlvl(pl_name);
    	if (lvl < levels.size())
    		return levels.get(lvl).name;
    	return max_lvl.name;
    }
    public static String getnextlvlname(String pl_name) {
    	int lvl = getnextlvl(pl_name);
    	if (lvl < levels.size())
    		return levels.get(lvl).name;
    	return max_lvl.name;
    }
    public static int getblocks(String pl_name) {
        return pInf.get(GetId(pl_name)).breaks;
    }
    public static int getneed(String pl_name) {
        PlayerInfo id_pl = pInf.get(GetId(pl_name));
        return 16 + id_pl.lvl * lvl_mult - id_pl.breaks;
    }
    @SuppressWarnings("unchecked")
	public static PlayerInfo gettop(int i) {
    	if (pInf.size() <= i)
    		return new PlayerInfo();
    	ArrayList<PlayerInfo> ppii = (ArrayList<PlayerInfo>) pInf.clone();
    	Collections.sort(ppii, PlayerInfo.COMPARE_BY_LVL);
        return ppii.get(i);
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> commands = new ArrayList<>();

        if (args.length == 1) {
        	commands.addAll(Arrays.asList("j","join","leave","invite","accept","kick","ver","IDreset","help"));
            if (sender.hasPermission("Oneblock.set")) {
            	commands.addAll(Arrays.asList("set","setleave","Progress_bar","chat_alert","setlevel","clear", "circlemode",
            		"lvl_mult","reload","frequency","islands","island_rebirth","protection","worldguard","listlvl","autoJoin"));
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
	                break;
	            }
                case ("islands"):
	                commands.add("set_my_by_def");
	                commands.add("default");
                case ("island_rebirth"):
                case ("protection"):
                case ("circlemode"):
                case ("worldguard"):
                case ("autoJoin"):
	                commands.add("true");
	                commands.add("false");
	                break;
                case ("listlvl"):
	            	for(int i = 0;i<levels.size();)
	            		commands.add(String.format("%d", i++));
	            	break;
                case ("frequency"):
                	for(int i = 4;i<=20;)
	            		commands.add(String.format("%d", i++));
	            	break;
                case ("lvl_mult"):
                	for(int i = 0;i<=20;)
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
            	for (int i = 0;i<levels.size();)
            		commands.add(String.format("%d", i++));
        Collections.sort(commands);
        return commands;
    }
}