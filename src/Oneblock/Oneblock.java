// Copyright © 2021 MrMarL. All rights reserved.
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
import org.bukkit.boss.BossBar;
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
    static ArrayList <Integer> yroven = new ArrayList <Integer>();
    static ArrayList <Integer> slomano = new ArrayList <Integer>();
    String name = "MrMarL";
    FileConfiguration config = this.getConfig();
    static FileConfiguration data;
    FileConfiguration newConfigz;
    static World wor;
	World leafwor;
    int random = 0;
    static ArrayList <BossBar> b = new ArrayList <BossBar>();
    boolean Progress_bar = true;
    boolean superlegacy, legacy;
    String version = "1.17+";
    boolean lvl_bar_mode = false;
    boolean chat_alert = false;
    ArrayList <XMaterial> blocks;
    ArrayList <Material> s_ch, m_ch, h_ch;
    ArrayList <EntityType> mobs;
    List <Player> plonl;
    Long fr;
    int Probeg = 0, Prob = 0;
    boolean il3x3 = false, rebirth = false, autojoin = false;
    BarColor Progress_color;
    static int lvl_mult = 5;
    XMaterial GRASS_BLOCK, GRASS;
    boolean PAPI = false;
    String TextP = "";
    BlockData[][][] island = null;
    static ArrayList <String> invite = new ArrayList<>();
    boolean protection = false;
    String noperm = ChatColor.RED + "You don't have permission [Oneblock.set].";
    ArrayList <XMaterial> flowers;
    static ArrayList <String> lvl_names;
    ArrayList <Integer> lvl_sizes;
    int sto = 100;
    @Override
    public void onEnable() {
    	String bVer = Bukkit.getBukkitVersion();
        superlegacy = bVer.contains("1.8") || bVer.contains("1.7");
        legacy = bVer.contains("1.12") || bVer.contains("1.11") || bVer.contains("1.10") || bVer.contains("1.9") || superlegacy;
        version = bVer.contains("1.13")?"1.13":version;
        version = bVer.contains("1.14")?"1.14":version;
        version = bVer.contains("1.15")?"1.15":version;
        version = bVer.contains("1.16")?"1.16":version;
        if (!legacy) 
        	Progress_color = BarColor.GREEN;
        GRASS_BLOCK = XMaterial.GRASS_BLOCK;
        GRASS = XMaterial.GRASS;
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            PAPI = true;
            new OBP().register();
            Bukkit.getConsoleSender().sendMessage("[OB] PlaceholderAPI has been found!");
        }
        Configfile();
        Datafile();
        Blockfile();
        Flowerfile();
        Chestfile();
        Mobfile();
        config = this.getConfig();
        yroven.add(0);
        if (config.getDouble("y") != 0) {
            if (wor == null || (config.getDouble("yleaf") != 0 && leafwor == null)) {
                Bukkit.getScheduler().runTaskTimer((Plugin) this, (Runnable) new wor_null(), 32, 64);
            } else {
                Bukkit.getScheduler().runTaskTimer((Plugin) this, (Runnable) new Task(), fr, fr * 2);
                on = true;
            }
        }
        Bukkit.getPluginManager().registerEvents(new Resp_AutoJ(), this);
        if (!superlegacy)
        	Bukkit.getPluginManager().registerEvents(new ChangedWorld(), this);
    }
    public class Resp_AutoJ implements Listener {
        @EventHandler
        public void Resp(PlayerRespawnEvent e) {
            if (rebirth) 
                if (e.getPlayer().getWorld().equals(wor))
                    if (data.isInt("_" + e.getPlayer().getName()))
                        e.setRespawnLocation(new Location(wor, x + data.getInt("_" + e.getPlayer().getName()) * sto + 0.5, y + 1.2, z + 0.5));
        }
        @EventHandler
        public void AutoJ(PlayerTeleportEvent e) {
        if (autojoin){
    		Location loc = e.getTo();
			World from = e.getFrom().getWorld();
    		World to = loc.getWorld();
        	if (!from.equals(wor) && to.equals(wor) &&
        			!(loc.getY() == y+1.2 && loc.getZ() == z+0.5)){
        		e.setCancelled(true);
        		e.getPlayer().performCommand("ob j");
        		}
        	}
        }
        @EventHandler
        public void JAuto(PlayerJoinEvent e) {
        if (autojoin){
        	Player pl = e.getPlayer();
			World nowwor = pl.getWorld();
        	if (nowwor.equals(wor))
        		pl.performCommand("ob j");
        	}
        }
    }
    public class ChangedWorld implements Listener {
    	@EventHandler
        public void PlayerChangedWorldEvent(PlayerChangedWorldEvent e) {
    		Player p = e.getPlayer(); World from = e.getFrom();
    		int i = data.getInt("_" + p.getName());
        	if (from.equals(wor))
        		if (i<b.size())
        			b.get(i).removePlayer(p);
        }
    }
    public class wor_null implements Runnable {
        public void run() {
            if (wor == null) {
                Bukkit.getConsoleSender().sendMessage(
                		"\n[OB] WORLD INITIALIZATION ERROR! world = null"+
                		"\n[OB] Trying to initialize the world again...");
                wor = Bukkit.getWorld(config.getString("world"));
                leafwor = Bukkit.getWorld(config.getString("leafworld"));
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
    }
    public void addinvite(String name ,String to) {
    	if (invite.contains(name+" "+to))
    		return;
    	invite.add(name+" "+to);
    	Bukkit.getScheduler().runTaskLaterAsynchronously((Plugin) this, new Runnable() {
    		@Override
    		public void run() {invite.remove(name+" "+to);}}, 300L);
    }
    public boolean checkinvite(Player pl) {
    	String name = pl.getName();
    	String to = "";
    	for(String item:invite) {
    		if (item.split(" ")[1].equals(name))
    			to = item.split(" ")[0];
    	}
    	if (to!="" && invite.contains(to+" "+name)) {
    		if (Progress_bar && data.isInt("_"+name))
    			b.get(data.getInt("_"+name)).removePlayer(pl);
    		data.set("_"+name, null);
    		data.set("_"+name, data.getInt("_"+to));
    		pl.performCommand("ob j");
    		invite.remove(to+" "+name);
    		return true;
    	}
    	return false;
    }
    public class Task implements Runnable {
        public void run() {
            if (id >= slomano.size())
                slomano.add(0);
            plonl = wor.getPlayers();
            Collections.shuffle(plonl);
            for (Player ponl: plonl) {
            	if (!data.isInt("_" + ponl.getName()))
            		continue;
                Prob = data.getInt("_" + ponl.getName());
                Probeg =  Prob * sto;
                if (protection) {
                	int check = ponl.getLocation().getBlockX()-Probeg-x;
                	if (check>50 || check<-50) {
                		if (check>200 || check<-200) {
                			ponl.teleport(new Location(wor, x + Probeg + 0.5, y + 1.2, z + 0.5));
                			continue;
                		}
                		ponl.setVelocity(new Vector(-check/30, 0, 0));
                		ponl.sendMessage(ChatColor.YELLOW +"are you trying to go "+ChatColor.RED+"outside the island?");
                		continue;
                	}
                }
                Block block = wor.getBlockAt(x + Probeg, y, z);
                if (block.getType().equals(Material.AIR)) {
                	int sl_now = slomano.get(Prob) + 1;
                    int yr_now = yroven.get(Prob);
                    if (sl_now >= 16 + yr_now * lvl_mult) {
                    	sl_now = 0;
                        yroven.set(Prob, ++yr_now);
                        String lvl_name = "Level: MAX";
                        if (yr_now < lvl_sizes.size())
                        	lvl_name = lvl_names.get(yr_now);
                        if (lvl_bar_mode)
                        	b.get(Prob).setTitle(lvl_name);
                        if (chat_alert)
                        	ponl.sendMessage(ChatColor.GREEN + lvl_name);
                    }
                    slomano.set(Prob, sl_now);
                    if (Progress_bar) {
                    	BossBar b_now = b.get(Prob);
                        if (!lvl_bar_mode && PAPI)
                        	b_now.setTitle(PlaceholderAPI.setPlaceholders(ponl, TextP));
                        if (sl_now > 0)
                        	b_now.setProgress((double) sl_now / (16 + (double) yr_now * lvl_mult));
                        else
                        	b_now.setProgress(0);
                        b_now.addPlayer(ponl);
                    }
                    Location loc = ponl.getLocation();
                    if (loc.getBlockX() == x + Probeg && loc.getY() - 1 < y && loc.getBlockZ() == z) {
                        loc.setY(y+1);
                        ponl.teleport(loc);
                    }
                    if (yr_now == 0)
                        random = 0;
                    else if (yr_now >= lvl_sizes.size())
                    	random = rnd.nextInt(blocks.size());
                    else
                        random = rnd.nextInt(lvl_sizes.get(yr_now));
                    if (blocks.get(random) == null) {
                        XBlock.setType(block, GRASS_BLOCK);
                        if (rnd.nextInt(3) == 1)
                            XBlock.setType(wor.getBlockAt(x + Probeg, y + 1, z),(flowers.get(rnd.nextInt(flowers.size()))));
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
                        if (yr_now < blocks.size() / 9)
                            random = rnd.nextInt(mobs.size() / 3);
                        else if (yr_now < blocks.size() / 9 * 2)
                            random = rnd.nextInt(mobs.size() / 3 * 2);
                        else
                            random = rnd.nextInt(mobs.size());
                        wor.spawnEntity(new Location(wor, x + Probeg, y + 1, z), mobs.get(random));
                    }
                }
            }
        }
    }

    public void onDisable() {
        for (int i = 0; i < id; i++) {
            data.set("Score_" + i, yroven.get(i));
            if (slomano.get(i)==0)
            	data.set("ScSlom_" + i, null);
            else
            	data.set("ScSlom_" + i, slomano.get(i));
        }
        if (island != null) {
            HashMap <String, List <String>> map = new HashMap <String, List <String>>();
            List <String> y_now;
            for (int yy = 0;yy<3;yy++) {
            	y_now = new ArrayList <String>();
	            for (int xx = 0; xx < 7; xx++)
	                for (int zz = 0; zz < 7; zz++)
	                	y_now.add(island[xx][yy][zz].getAsString());
	            map.put("y"+yy, y_now);
            }
            config.set("custom_island", map);
        }
        savedata();
        this.saveConfig();
    }
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("oneblock")) {
            //
            if (args.length == 0) {
            	((Player)sender).performCommand("ob j");
                return true;
            }
            if (!sender.hasPermission("Oneblock.join")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission [Oneblock.join].");
                return true;
            }
            //
            switch (args[0].toLowerCase())
            {
            case ("j"):
            case ("join"):{
                config = this.getConfig();
                if (config.getInt("y") == 0 || wor == null)
                    return true;
                Player p = (Player) sender;
                name = p.getName();
                if (!data.isInt("_" + name)) {
                    id = data.getInt("id");
                    data.set("_" + name, id);
                    if (il3x3) {
                    	if (island != null) {
                    		int px = x + id * sto - 3;
                            for (int xx = 0; xx < 7; xx++)
                            	for (int yy = 0; yy < 3; yy++)
                                	for (int zz = 0; zz < 7; zz++) {
                                    	wor.getBlockAt(px + xx, y + yy, z - 3 + zz).setBlockData(island[xx][yy][zz]);
                                    }
                        } else {
                        	for (int i = -2;i<=2;i++)
                        		for (int q = -2;q<=2;q++)
                        			if (Math.abs(i)+Math.abs(q) < 3)
                        				XBlock.setType(wor.getBlockAt(x + id * sto + i, y, z+ q),GRASS_BLOCK);
                        }
                    }
                    id++;
                    data.set("id", id);
                    savedata();
                    yroven.add(0);
                    if (!superlegacy) {
                    	String temp;
                        if (lvl_bar_mode)
                        	temp = lvl_names.get(0);
                        else if (PAPI)
                        	temp = PlaceholderAPI.setPlaceholders(p, TextP);
                        else
                        	temp = TextP;
                        b.add(Bukkit.createBossBar(temp, Progress_color, BarStyle.SEGMENTED_10, BarFlag.DARKEN_SKY));
                    }
                }
                if (!on) {
                    Bukkit.getScheduler().runTaskTimer((Plugin) this, (Runnable) new Task(), fr, fr * 2);
                    on = true;
                }
                if (Progress_bar) {
                    if (PAPI)
                        b.add(Bukkit.createBossBar((PlaceholderAPI.setPlaceholders(p, TextP)), Progress_color, BarStyle.SEGMENTED_10, BarFlag.DARKEN_SKY));
                    b.get(data.getInt(p.getName())).setVisible(true);
                }
                p.teleport(new Location(wor, x + data.getInt("_" + name) * sto + 0.5, y + 1.2, z + 0.5));
                return true;
            }
            case ("leaf"):{
                config = this.getConfig();
                Player p = (Player) sender;
                if (!superlegacy)
                	b.get(data.getInt("_" + p.getName())).removePlayer(p);
                if (config.getDouble("yleaf") == 0 || leafwor == null)
                    return true;
                p.teleport(new Location(leafwor, config.getDouble("xleaf"), config.getDouble("yleaf"), config.getDouble("zleaf")));
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
                config = this.getConfig();
                int temp = 100;
                if (args.length >= 2) {
                    try {
                    	temp = Integer.parseInt(args[1]);
                    } catch (NumberFormatException nfe) {
                    	sender.sendMessage(ChatColor.RED + "invalid value");
                    	return true;
                    }
                    if (temp > 1000 || temp < 100) {
                    	sender.sendMessage(ChatColor.RED + "possible values are from 100 to 1000");
                    	return true;
                    }
                    sto = temp;
                    config.set("set", sto);
                }
                config.set("world", wor.getName());
                config.set("x", (double) x);
                config.set("y", (double) y);
                config.set("z", (double) z);
                this.saveConfig();
                wor.getBlockAt(x, y, z).setType(GRASS_BLOCK.parseMaterial());
                return true;
            }
            case ("setleaf"):{
                if (!sender.hasPermission("Oneblock.set")) {
                    sender.sendMessage(noperm);
                    return true;
                }
                Player p = (Player) sender;
                Location l = p.getLocation();
                leafwor = l.getWorld();
                config = this.getConfig();
                config.set("leafworld", leafwor.getName());
                config.set("xleaf", l.getX());
                config.set("yleaf", l.getY());
                config.set("zleaf", l.getZ());
                this.saveConfig();
                return true;
            }
            case ("invite"):{
            	if (!sender.hasPermission("Oneblock.invite")) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission [Oneblock.invite].");
                    return true;
                }
            	if (args.length < 2) {
            		sender.sendMessage(ChatColor.RED + "invalid format. try: /ob invite 'nickname'");
            		return true;
            	}
            	Player inv = Bukkit.getPlayer(args[1]);
            	if (inv != null) {
            		if (inv == (Player) sender) {
            			sender.sendMessage(ChatColor.YELLOW + "wtf? you can't invite yourself.");
            			return true;
            		}
            		if (!data.isInt("_" + ((Player)sender).getName())) {
            			sender.sendMessage(ChatColor.YELLOW + "wtf? you don't have an island.");
            			return true;
            		}
            		addinvite(((Player) sender).getName(),inv.getName());
            		inv.sendMessage(ChatColor.GREEN +"you were invited by player "+inv.getName()+".\n"+
            				ChatColor.RED +"/ob accept to accept).");
            		sender.sendMessage(ChatColor.GREEN +"you invited "+inv.getName()+".");
            	}
            	return true;
            }
            case ("accept"):{
            	Player pl = (Player) sender;
           	 	if (checkinvite(pl))
           	 		sender.sendMessage(ChatColor.GREEN + "[ok]");
           	 	else
           	 		sender.sendMessage(ChatColor.RED + "[you have no invitations.]");
           		return true;
            }
            case ("idreset"):{
            	Player pl = (Player)sender;
            	if (Progress_bar && data.isInt("_"+name))
        			b.get(data.getInt("_"+name)).removePlayer(pl);;
            	data.set("_"+pl.getName(), null);
            	sender.sendMessage(ChatColor.GREEN +"Now your data has been reset. You can create a new island /ob join.");
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
                	sender.sendMessage(ChatColor.YELLOW + "enter a valid value true or false");
                sender.sendMessage(ChatColor.GREEN + "the protection is now " + (protection?"enabled.":"disabled."));
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
                	sender.sendMessage(ChatColor.YELLOW + "enter a valid value true or false");
                sender.sendMessage(ChatColor.GREEN + "autojoin is now " + (autojoin?"enabled.":"disabled."));
           		return true;
            }
            //LVL
            case ("setlevel"):{
                if (!sender.hasPermission("Oneblock.set")) {
                    sender.sendMessage(noperm);
                    return true;
                }
                if (args.length <= 2) {
                    sender.sendMessage(ChatColor.RED + "invalid format. try: /ob setlevel 'nickname' 'level'");
                    return true;
                }
                if (data.isInt("_" + args[1])) {
                    int setlvl = 0;
                    try {
                        setlvl = Integer.parseInt(args[2]);
                    } catch (NumberFormatException nfe) {
                        sender.sendMessage(ChatColor.RED + "invalid level value.");
                        return true;
                    }
                    if (setlvl >= 0 && 10000 > setlvl) {
                        int i = data.getInt("_" + args[1]);
                        data.set("Score_" + i, setlvl);
                        slomano.set(i, 0);
                        yroven.set(i, setlvl);
                        if (lvl_bar_mode)
	                        if (yroven.get(i) >= lvl_sizes.size())
	                    		b.get(i).setTitle("Level: MAX");
	                    	else
	                    		b.get(i).setTitle(lvl_names.get(yroven.get(i)));
                        sender.sendMessage(ChatColor.GREEN + "for player " + args[1] + ", level " + args[2] + " is set.");
                        return true;
                    }
                    sender.sendMessage(ChatColor.RED + "invalid level value.");
                    return true;
                }
                sender.sendMessage(ChatColor.RED + "a player named " + args[1] + " was not found.");
                return true;
            }
            case ("clear"):{
                if (!sender.hasPermission("Oneblock.set")) {
                    sender.sendMessage(noperm);
                    return true;
                }
                if (args.length <= 1) {
                    sender.sendMessage(ChatColor.RED + "invalid format. try: /ob clear 'nickname'");
                    return true;
                }
                if (data.isInt("_" + args[1])) {
                    int i = data.getInt("_" + args[1]);
                    data.set("Score_" + i, null);
                    data.set("ScSlom_" + i, null);
                    data.set("_" + args[1], null);
                    slomano.set(i, 0);
                    yroven.set(i, 0);
                    if (Progress_bar)
            			b.get(i).setVisible(false);
                    int x_now = x + i * 100 - 12, y_now = y - 6, z_now = z - 12;
                    if (y_now <= 1)
                        y_now = 1;
                    for (int xx = 0; xx < 24; xx++)
                        for (int yy = 0; yy < 16; yy++)
                            for (int zz = 0; zz < 24; zz++)
                                wor.getBlockAt(x_now + xx, y_now + yy, z_now + zz).setType(Material.AIR);
                    sender.sendMessage(ChatColor.GREEN + "player " + args[1] + " island is destroyed! :D");
                    return true;
                }
                sender.sendMessage(ChatColor.RED + "a player named " + args[1] + " was not found.");
                return true;
            }
            case ("lvl_mult"):{
                if (!sender.hasPermission("Oneblock.set")) {
                    sender.sendMessage(noperm);
                    return true;
                }
                if (args.length <= 1) {
                    sender.sendMessage(ChatColor.GREEN + "level multiplier now: " + lvl_mult +
                    		"\n5 by default");
                    return true;
                }
                int lvl = lvl_mult;
                try {
                    lvl = Integer.parseInt(args[1]);
                } catch (NumberFormatException nfe) {
                    sender.sendMessage(ChatColor.RED + "invalid multiplier value.");
                    return true;
                }
                if (lvl <= 20 && lvl >= 0) {
                    lvl_mult = lvl;
                    config.set("level_multiplier", lvl_mult);
                } else
                    sender.sendMessage(ChatColor.RED + "possible values: from 0 to 20.");
                sender.sendMessage(ChatColor.GREEN + "level multiplier now: " + lvl_mult +
                		"\n5 by default");
                return true;
            }
            case ("progress_bar"):{
                if (!sender.hasPermission("Oneblock.set")) {
                    sender.sendMessage(noperm);
                    return true;
                }
                if (superlegacy) {
                    sender.sendMessage(ChatColor.RED + "You server version is super legacy! ProgressBar unsupported!");
                    return true;
                }
                if (args.length == 1) {
                    sender.sendMessage(ChatColor.YELLOW + "and?");
                    return true;
                }
                if (args[1].equals("true") || args[1].equals("false")) {
                    Progress_bar = Boolean.valueOf(args[1]);
                    for (int i = 0; i < id; i++)
                        b.get(i).setVisible(Progress_bar);
                    config.set("Progress_bar", Progress_bar);
                    return true;
                }
                if (args[1].equalsIgnoreCase("color")) {
                    if (args.length == 2) {
                        sender.sendMessage(ChatColor.YELLOW + "enter a color name.");
                        return true;
                    }
                    try {
                        Progress_color = BarColor.valueOf(args[2]);
                        for (int i = 0; i < id; i++)
                            b.get(i).setColor(Progress_color);
                        config.set("Progress_bar_color", Progress_color.toString());
                    } catch (Exception e) {
                        sender.sendMessage(ChatColor.YELLOW + "pls enter a valid color. For example: RED");
                    }
                    sender.sendMessage(ChatColor.GREEN + "Progress bar color = " + Progress_color.toString());
                    return true;
                }
                if (args[1].equalsIgnoreCase("level")) {
                    if (!lvl_bar_mode) {
                        lvl_bar_mode = true;
                        for (int i = 0; i < id; i++)
	                        if (yroven.get(i) >= lvl_sizes.size())
	                    		b.get(i).setTitle("Level: MAX");
	                    	else
	                    		b.get(i).setTitle(lvl_names.get(yroven.get(i)));
                        config.set("Progress_bar_text", "level");
                        return true;
                    } else {
                        lvl_bar_mode = false;
                        for (int i = 0; i < id; i++)
                            b.get(i).setTitle("Progress bar");
                        config.set("Progress_bar_text", "Progress bar");
                        return true;
                    }
                }
                if (args[1].equalsIgnoreCase("settext")) {
                    String txt_bar = "";
                    for (int i = 2; i < args.length - 1; i++)
                        txt_bar += args[i] + " ";
                    txt_bar += args[args.length - 1];
                    lvl_bar_mode = false;
                    for (int i = 0; i < id; i++)
                        b.get(i).setTitle(txt_bar);
                    config.set("Progress_bar_text", txt_bar);
                    TextP = txt_bar;
                    if (PAPI)
                        for (Player ponl: Bukkit.getOnlinePlayers())
                            b.get(data.getInt("_" + ponl.getName())).setTitle(PlaceholderAPI.setPlaceholders(ponl, txt_bar));
                    return true;
                }
                sender.sendMessage(ChatColor.RED + "true, false, settext or level only!");
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
                    	sender.sendMessage(ChatColor.RED + "invalid value");
                    	return true;
                    }
                    if (lvl_names.size()<=temp||temp<0) {
                    	sender.sendMessage(ChatColor.RED + "undefined lvl");
                    	return true;
                    }
                    sender.sendMessage(ChatColor.GREEN+lvl_names.get(temp));
                    int i = 0;
                    if (temp !=0)
                    	i = lvl_sizes.get(temp-1);
                    for(;i<lvl_sizes.get(temp);i++)
                    	sender.sendMessage(blocks.get(i)==null?"Grass or undefined":blocks.get(i).name());
                    return true;
                }
                for(int i = 0;i<lvl_names.size();i++)
                	sender.sendMessage(i+": "+ChatColor.GREEN+lvl_names.get(i));
                return true;
            }
            case ("reload"):{
                if (!sender.hasPermission("Oneblock.set")) {
                    sender.sendMessage(noperm);
                    return true;
                }
                if (args.length == 1) {
                    sender.sendMessage(ChatColor.YELLOW + "and?");
                    return true;
                }
                if (args[1].equalsIgnoreCase("blocks.yml")) {
                    Blockfile();
                    sender.sendMessage(ChatColor.GREEN + "blocks.yml reloaded!");
                    return true;
                }
                if (args[1].equalsIgnoreCase("flowers.yml")) {
                	Flowerfile();
                    sender.sendMessage(ChatColor.GREEN + "flowers.yml reloaded!");
                    return true;
                }
                if (args[1].equalsIgnoreCase("chests.yml")) {
                    Chestfile();
                    sender.sendMessage(ChatColor.GREEN + "chests.yml reloaded!");
                    return true;
                }
                if (args[1].equalsIgnoreCase("mobs.yml")) {
                    Mobfile();
                    sender.sendMessage(ChatColor.GREEN + "mobs.yml reloaded!");
                    return true;
                }
                sender.sendMessage(ChatColor.RED + "try blocks.yml or chests.yml");
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
                    name = p.getName();
                    if (data.isInt("_" + name)) {
                        island = new BlockData[7][3][7];
                        int px = x + data.getInt("_" + name) * 100 - 3;
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
            	sender.sendMessage(ChatColor.GREEN + "▀▀▀▀▀▀▀▀OB - help▀▀▀▀▀▀▀▀");
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
            sender.sendMessage(ChatColor.values()[rnd.nextInt(ChatColor.values().length)] +
            	"  ▄▄    ▄▄\n"+
            	"█    █  █▄▀\n"+
            	"▀▄▄▀ █▄▀\n"+
            	"Create by MrMarL v0.9.2\n" + 
            	"Server run "+ (superlegacy?"super legacy(1.7 - 1.8)":(legacy?"legacy(1.9 - 1.12)":version)));
            return true;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Error.");
            return false;
        }
    }

    private void Datafile() {
        File PlData = new File(getDataFolder(), "PlData.yml");
        data = YamlConfiguration.loadConfiguration(PlData);
        if (!data.isInt("id"))
            data.set("id", 0);
        id = data.getInt("id");
        for (int i = 0; i < id; i++) {
            if (!data.isInt("Score_" + i))
                data.set("Score_" + i, 1);
            yroven.add(data.getInt("Score_" + i));
        }
        if (!on)
        	for (int i = 0; i <= id; i++)
        		if (data.isInt("Score_" + i))
        			slomano.add(data.getInt("ScSlom_" + i));
        		else
        			slomano.add(0);
        savedata();
    }

    public void savedata() {
        try {
            data.save(new File(getDataFolder(), "PlData.yml"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Blockfile() {
        blocks = new ArrayList <XMaterial>();
        File block = new File(getDataFolder(), "blocks.yml");
        if (!block.exists())
            saveResource("blocks.yml", false);
        block = new File(getDataFolder(), "blocks.yml");
        newConfigz = YamlConfiguration.loadConfiguration(block);
        lvl_names = new ArrayList <String>();
        lvl_sizes = new ArrayList <Integer>();
        for (int i = 0; newConfigz.isList(i + ""); i++) {
        	List <String> bl_temp = newConfigz.getStringList(i + "");
        	lvl_names.add(bl_temp.get(0));
        	for (int q = 1;q<bl_temp.size();q++) {
        		Optional <XMaterial> a = XMaterial.matchXMaterial(bl_temp.get(q));
	        	if (!a.isPresent() || a.get() == GRASS_BLOCK)
	                blocks.add(null);
	            else
	                blocks.add(a.get());
        	}
        	lvl_sizes.add(blocks.size());
        }
        //
        if (!on && !superlegacy) {
            if (Progress_color.equals(null))
                Progress_color = BarColor.GREEN;
            if (lvl_bar_mode)
                for (int i = 0; i < yroven.size(); i++)
                	if (yroven.get(i) >= lvl_sizes.size())
                		b.add(i, Bukkit.createBossBar("Level: MAX", Progress_color, BarStyle.SEGMENTED_10, BarFlag.DARKEN_SKY));
                	else
                		b.add(i, Bukkit.createBossBar(lvl_names.get(yroven.get(i)), Progress_color, BarStyle.SEGMENTED_10, BarFlag.DARKEN_SKY));
            else
                for (int i = 0; i < yroven.size(); i++)
                    b.add(i, Bukkit.createBossBar((TextP), Progress_color, BarStyle.SEGMENTED_10, BarFlag.DARKEN_SKY));
        }
    }
    private void Flowerfile() {
        flowers = new ArrayList <XMaterial>();
        File flower = new File(getDataFolder(), "flowers.yml");
        if (!flower.exists())
            saveResource("flowers.yml", false);
        flower = new File(getDataFolder(), "flowers.yml");
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
        chest = new File(getDataFolder(), "chests.yml");
        newConfigz = YamlConfiguration.loadConfiguration(chest);
        for (String s: newConfigz.getStringList("small_chest")) 
        	s_ch.add(Material.getMaterial(s));
        for (String s: newConfigz.getStringList("medium_chest")) 
        	m_ch.add(Material.getMaterial(s));
        for (String s: newConfigz.getStringList("high_chest")) 
        	h_ch.add(Material.getMaterial(s));
    }
    private void Mobfile() {
        mobs = new ArrayList <EntityType>();
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

    private void Configfile() {
        config = this.getConfig();
        if (!config.isString("world"))
            config.set("world", "world");
        wor = Bukkit.getWorld(config.getString("world"));
        if (!config.isDouble("x"))
            config.set("x", (double) x);
        x = (int) config.getDouble("x");
        if (!config.isDouble("y"))
            config.set("y", (double) y);
        y = (int) config.getDouble("y");
        if (!config.isDouble("z"))
            config.set("z", (double) z);
        z = (int) config.getDouble("z");
        //leaf
        if (!config.isString("leafworld"))
            config.set("leafworld", "world");
        leafwor = Bukkit.getWorld(config.getString("leafworld"));
        if (!config.isDouble("xleaf"))
            config.set("xleaf", 0);
        if (!config.isDouble("yleaf"))
            config.set("yleaf", 0);
        if (!config.isDouble("zleaf"))
            config.set("zleaf", 0);
        if (!config.isBoolean("Progress_bar"))
            config.set("Progress_bar", true);
        Progress_bar = config.getBoolean("Progress_bar");
        if (superlegacy)
            Progress_bar = false;
        if (!config.isInt("frequency"))
            config.set("frequency", 7L);
        fr = config.getLong("frequency");
        //Text
        if (!config.isString("Progress_bar_text"))
            config.set("Progress_bar_text", "level");
        TextP = config.getString("Progress_bar_text");
        if (TextP.equals("level"))
            lvl_bar_mode = true;
        if (superlegacy)
            lvl_bar_mode = false;
        //alert
        if (!config.isBoolean("Chat_alert"))
        	config.set("Chat_alert", !lvl_bar_mode);
        chat_alert = config.getBoolean("Chat_alert");
        if (!config.isString("Progress_bar_color"))
            config.set("Progress_bar_color", "GREEN");
        if (Progress_bar)
            Progress_color = BarColor.valueOf(config.getString("Progress_bar_color"));
        if (!config.isBoolean("Island_for_new_players"))
            config.set("Island_for_new_players", true);
        il3x3 = config.getBoolean("Island_for_new_players");
        if (!config.isBoolean("Rebirth_on_the_island"))
            config.set("Rebirth_on_the_island", true);
        rebirth = config.getBoolean("Rebirth_on_the_island");
        if (!config.isInt("level_multiplier"))
            config.set("level_multiplier", lvl_mult);
        lvl_mult = config.getInt("level_multiplier");
        if (!config.isBoolean("protection"))
            config.set("protection", protection);
        protection = config.getBoolean("protection");
        if (config.isBoolean("autojoin"))
        	autojoin = config.getBoolean("autojoin");
        if (config.isSet("custom_island") && !legacy) {
            List <String> cust_s = getConfig().getStringList("custom_island.y0");
            List <String> cust_s1 = getConfig().getStringList("custom_island.y1");
            List <String> cust_s2 = getConfig().getStringList("custom_island.y2");
            island = new BlockData[7][3][7];
            for (int xx = 0, i = 0; xx < 7; xx++)
                for (int zz = 0; zz < 7; zz++, i++) {
                    island[xx][0][zz] = Bukkit.createBlockData(cust_s.get(i));
                    island[xx][1][zz] = Bukkit.createBlockData(cust_s1.get(i));
                    island[xx][2][zz] = Bukkit.createBlockData(cust_s2.get(i));
                }
        }
        if (config.isInt("set"))
        	sto = config.getInt("set");
        this.saveConfig();
    }
    public static int getlvl(String pl_name) {
        return yroven.get(data.getInt("_" + pl_name));
    }
    public static String getlvlname(String pl_name) {
        return lvl_names.get(getlvl(pl_name));
    }
    public static int getblocks(String pl_name) {
        return slomano.get(data.getInt("_" + pl_name));
    }
    public static int getneed(String pl_name) {
        int id_pl = data.getInt("_" + pl_name);
        return 16 + yroven.get(id_pl) * lvl_mult - slomano.get(id_pl);
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> commands = new ArrayList<>();

        if (args.length == 1) {
        	commands.addAll(Arrays.asList("j","join","leaf","invite","accept","ver","IDreset","help"));
            if (sender.hasPermission("Oneblock.set")) {
            	commands.addAll(Arrays.asList("set","setleaf","Progress_bar","chat_alert","setlevel","clear",
            		"lvl_mult","reload","frequency","islands","island_rebirth","protection","listlvl","autoJoin"));
            }
        } else if (args.length == 2) {
        	if (args[0].equals("invite")) {
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
                case ("autoJoin"):
	                commands.add("true");
	                commands.add("false");
	                break;
                case ("listlvl"):
	            	for(int i = 0;i<lvl_names.size();)
	            		commands.add(""+i++);
	            	break;
                case ("frequency"):
                	for(int i = 4;i<=20;)
	            		commands.add(""+i++);
	            	break;
                case ("lvl_mult"):
                	for(int i = 0;i<=20;)
	            		commands.add(""+i++);
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
            	for (int i = 0;i<lvl_names.size();)
            		commands.add(""+i++);
        Collections.sort(commands);
        return commands;
    }
}