// Copyright © 2021 MrMarL. All rights reserved.
package Oneblock;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;
import org.bukkit.util.Vector;

import me.clip.placeholderapi.PlaceholderAPI;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Oneblock extends JavaPlugin {
    boolean on = false;
    public String stroka;
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
    Player p;
    static World wor;
	World leafwor;
    int random = 0;
    static ArrayList <BossBar> b = new ArrayList <BossBar>();
    boolean Progress_bar = true;
    boolean superlegacy, legacy, is1_13, is1_14, is1_15, is1_16;
    boolean lvl_bar_mode = false;
    boolean chat_alert = false;
    ArrayList <Material> blocks;
    ArrayList <Material> s_ch, m_ch, h_ch;
    ArrayList <EntityType> mobs;
    List <Player> plonl;
    Long fr;
    int Probeg = 0;
    boolean il3x3 = false, rebirth = false;
    BarColor Progress_color;
    static int lvl_mult = 5;
    Material GRASS_BLOCK, GRASS;
    boolean PAPI = false;
    String TextP = "";
    BlockData[][][] island = null;
    static ArrayList <String> invite = new ArrayList<>();
    boolean protection = false;
    String noperm = ChatColor.RED + "You don't have permission [Oneblock.set].";
    Material flowers[] = new Material[10];
    ArrayList <String> lvl_names;
    ArrayList <Integer> lvl_sizes;
    @Override
    public void onEnable() {
        superlegacy = Bukkit.getBukkitVersion().contains("1.8") || Bukkit.getBukkitVersion().contains("1.7");
        legacy = Bukkit.getBukkitVersion().contains("1.12") || Bukkit.getBukkitVersion().contains("1.11") || Bukkit.getBukkitVersion().contains("1.10") || Bukkit.getBukkitVersion().contains("1.9") || superlegacy;
        is1_13 = Bukkit.getBukkitVersion().contains("1.13");
        is1_14 = Bukkit.getBukkitVersion().contains("1.14");
        is1_15 = Bukkit.getBukkitVersion().contains("1.15");
        is1_16 = Bukkit.getBukkitVersion().contains("1.16");
        flowers[9] = Material.SUGAR_CANE;
        if (legacy) {
            GRASS_BLOCK = Material.GRASS;
            GRASS = Material.valueOf("LONG_GRASS");
            //
            flowers[8] = Material.valueOf("YELLOW_FLOWER");
            flowers[7] = Material.valueOf("RED_ROSE");
            for(int i = 0;i<7;i++)
            	flowers[i] = GRASS;
        } else {
            Progress_color = BarColor.GREEN;
            GRASS_BLOCK = Material.GRASS_BLOCK;
            GRASS = Material.GRASS;
            //
            flowers[8] = Material.CORNFLOWER;
            flowers[7] = Material.OXEYE_DAISY;
            flowers[6] = Material.RED_TULIP;
            flowers[5] = Material.BLUE_ORCHID;
            flowers[4] = Material.DANDELION;
            flowers[3] = Material.POPPY;
            for(int i = 0;i<3;i++)
            	flowers[i] = GRASS;
        }
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            PAPI = true;
            new OBP().register();
            Bukkit.getConsoleSender().sendMessage("[OB] PlaceholderAPI has been found!");
        }
        Configfile();
        Datafile();
        Blockfile();
        Chestfile();
        Mobfile();
        config = this.getConfig();
        yroven.add(0);
        if (config.getDouble("y") != 0) {
            if (wor == null || (config.getDouble("yleaf") != 0 && leafwor == null)) {
                Bukkit.getScheduler().runTaskTimer((Plugin) this, (Runnable) new wor_null(), 20, 40);
            } else {
                Bukkit.getScheduler().runTaskTimer((Plugin) this, (Runnable) new Task(), fr, fr * 2);
                on = true;
            }
        }
        Bukkit.getPluginManager().registerEvents(new Respawn(), this);
    }
    public class Respawn implements Listener {
        @EventHandler
        public void onPlayerRespawn(PlayerRespawnEvent e) {
            if (rebirth) 
                if (e.getPlayer().getWorld().equals(wor))
                    if (data.isInt("_" + e.getPlayer().getName()))
                        e.setRespawnLocation(new Location(wor, x + data.getInt("_" + e.getPlayer().getName()) * 100 + 0.5, y + 1.2, z + 0.5));
        }
    }
    public class wor_null implements Runnable {
        public void run() {
            if (wor == null) {
                Bukkit.getConsoleSender().sendMessage("[OB] WORLD INITIALIZATION ERROR! world = null");
                Bukkit.getConsoleSender().sendMessage("[OB] Trying to initialize the world again...");
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
    public void addinvate(String name ,String to) {
    	if (invite.contains(name+" "+to))
    		return;
    	invite.add(name+" "+to);
    	Bukkit.getScheduler().runTaskLaterAsynchronously((Plugin) this, new Runnable() {
    	    @Override
    	     public void run() {
    	    	invite.remove(name+" "+to);
    	     }
    	}, 300L);
    }
    public boolean checkinvate(Player pl) {
    	String name = pl.getName();
    	String to = "";
    	for(String item:invite) {
    		if (item.split(" ")[1].equals(name))
    			to = item.split(" ")[0];
    	}
    	if (to!="" && invite.contains(to+" "+name)) {
    		if (Progress_bar && data.isInt("_"+name))
    			b.get(data.getInt("_"+name)).removePlayer(pl);;
    		data.set("_"+name, null);
    		data.set("_"+name, data.getInt("_"+to));
    		pl.teleport(new Location(wor, x + data.getInt("_" + name) * 100 + 0.5, y + 1.2, z + 0.5));
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
                Probeg = data.getInt("_" + ponl.getName()) * 100;
                if (protection) {
                	if (!data.isInt("_" + ponl.getName()))
                		continue;
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
                    slomano.set(Probeg / 100, slomano.get(Probeg / 100) + 1);
                    if (slomano.get(Probeg / 100) >= 16 + yroven.get(Probeg / 100) * lvl_mult) {
                        slomano.set(Probeg / 100, 0);
                        yroven.set(Probeg / 100, yroven.get(Probeg / 100) + 1);
                        if (lvl_bar_mode)
                        	if (yroven.get(Probeg / 100) >= lvl_sizes.size())
                        		b.get(Probeg / 100).setTitle("Level: MAX");
                        	else
                        		b.get(Probeg / 100).setTitle(lvl_names.get(yroven.get(Probeg / 100)));
                        if (chat_alert) {
                        	if (yroven.get(Probeg / 100) < lvl_sizes.size())
                        		ponl.sendMessage(ChatColor.GREEN + lvl_names.get(yroven.get(Probeg / 100)));
                        }
                    }
                    if (Progress_bar) {
                        if (!lvl_bar_mode)
                            if (PAPI)
                                b.get(Probeg / 100).setTitle(PlaceholderAPI.setPlaceholders(ponl, TextP));
                        if (slomano.get(Probeg / 100) > 0)
                            b.get(Probeg / 100).setProgress((double) slomano.get(Probeg / 100) / (16 + (double) yroven.get(Probeg / 100) * lvl_mult));
                        else
                            b.get(Probeg / 100).setProgress(0);
                        b.get(Probeg / 100).addPlayer(ponl);
                    }
                    if (ponl.getLocation().getBlockX() == x + Probeg && ponl.getLocation().getY() - 1 < y && ponl.getLocation().getBlockZ() == z) {
                        Location loc = new Location(wor, ponl.getLocation().getX(), y + 1, ponl.getLocation().getZ());
                        loc.setYaw(ponl.getLocation().getYaw());
                        loc.setPitch(ponl.getLocation().getPitch());
                        ponl.teleport(loc);
                    }
                    if (yroven.get(Probeg / 100) == 0)
                        random = 0;
                    else if (yroven.get(Probeg / 100) >= lvl_sizes.size())
                    	random = rnd.nextInt(lvl_sizes.size());
                    else
                        random = rnd.nextInt(lvl_sizes.get(yroven.get(Probeg / 100)));
                    if (random >= blocks.size() || blocks.get(random) == null) {
                        block.setType(GRASS_BLOCK);
                        if (rnd.nextInt(3) == 1) {
                            wor.getBlockAt(x + Probeg, y + 1, z).setType(flowers[rnd.nextInt(10)]);
                        }
                    } else if (blocks.get(random) == Material.CHEST) {
                        try {
                            block.setType(Material.CHEST);
                            Chest chest = (Chest) block.getState();
                            Inventory inv = chest.getInventory();
                            if (random < 26) {
                                inv.addItem(new ItemStack(s_ch.get(rnd.nextInt(s_ch.size())), rnd.nextInt(3)),
                                    new ItemStack(s_ch.get(rnd.nextInt(s_ch.size())), rnd.nextInt(4) + 1),
                                    new ItemStack(s_ch.get(rnd.nextInt(s_ch.size())), rnd.nextInt(3) + 1),
                                    new ItemStack(s_ch.get(rnd.nextInt(s_ch.size())), rnd.nextInt(3)));
                            } else if (random < 68) {
                                inv.addItem(new ItemStack(m_ch.get(rnd.nextInt(m_ch.size())), rnd.nextInt(3)),
                                    new ItemStack(m_ch.get(rnd.nextInt(m_ch.size())), rnd.nextInt(4) + 1),
                                    new ItemStack(m_ch.get(rnd.nextInt(m_ch.size())), rnd.nextInt(3) + 1),
                                    new ItemStack(m_ch.get(rnd.nextInt(m_ch.size())), rnd.nextInt(3) + 1));
                            } else {
                                inv.addItem(new ItemStack(h_ch.get(rnd.nextInt(h_ch.size())), rnd.nextInt(3)),
                                    new ItemStack(h_ch.get(rnd.nextInt(h_ch.size())), rnd.nextInt(4) + 1),
                                    new ItemStack(h_ch.get(rnd.nextInt(h_ch.size())), rnd.nextInt(3) + 1),
                                    new ItemStack(h_ch.get(rnd.nextInt(h_ch.size())), rnd.nextInt(3)));
                            }
                        } catch (Exception e) {
                            Bukkit.getConsoleSender().sendMessage("[OB] Error when generating items for the chest! Pls redo chests.yml!");
                        }
                    } else
                    	block.setType(blocks.get(random));

                    if (rnd.nextInt(9) == 0) {
                        if (yroven.get(Probeg / 100) < blocks.size() / 9)
                            random = rnd.nextInt(mobs.size() / 3);
                        else if (yroven.get(Probeg / 100) < blocks.size() / 9 * 2)
                            random = rnd.nextInt(mobs.size() / 3 * 2);
                        else
                            random = rnd.nextInt(mobs.size());
                        if (random < mobs.size());
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
            List <String> y0 = new ArrayList <String>();
            List <String> y1 = new ArrayList <String>();
            List <String> y2 = new ArrayList <String>();
            int yy = 0;
            for (int xx = 0; xx < 7; xx++)
                for (int zz = 0; zz < 7; zz++)
                    y0.add(island[xx][yy][zz].getAsString());
            yy++;
            for (int xx = 0; xx < 7; xx++)
                for (int zz = 0; zz < 7; zz++)
                    y1.add(island[xx][yy][zz].getAsString());
            yy++;
            for (int xx = 0; xx < 7; xx++)
                for (int zz = 0; zz < 7; zz++)
                    y2.add(island[xx][yy][zz].getAsString());
            map.put("y0", y0);
            map.put("y1", y1);
            map.put("y2", y2);
            config.set("custom_island", map);
        }
        savedata();
        this.saveConfig();
    }
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("oneblock")) {
            //
            if (args.length == 0) {
                sender.sendMessage(ChatColor.YELLOW + "pls try /ob join");
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
                p = (Player) sender;
                name = p.getName();
                if (!data.isInt("_" + name)) {
                    id = data.getInt("id");
                    if (data.isInt(name) && !name.matches(".*\\b" + "Score_" + "\\b.*")) {
                        data.set(("_" + name), data.getInt(name));
                        data.set(name, null);
                        savedata();
                    } else {
                        data.set("_" + name, id);
                        if (il3x3) {
                            if (island != null) {
                                int px = x + id * 100 - 3;
                                for (int xx = 0; xx < 7; xx++)
                                    for (int yy = 0; yy < 3; yy++)
                                        for (int zz = 0; zz < 7; zz++) {
                                            wor.getBlockAt(px + xx, y + yy, z - 3 + zz).setBlockData(island[xx][yy][zz]);
                                        }
                            } else {
                                wor.getBlockAt(x + id * 100 + 1, y, z).setType(GRASS_BLOCK);
                                wor.getBlockAt(x + id * 100 + 2, y, z).setType(GRASS_BLOCK);
                                wor.getBlockAt(x + id * 100 - 1, y, z).setType(GRASS_BLOCK);
                                wor.getBlockAt(x + id * 100 - 2, y, z).setType(GRASS_BLOCK);
                                wor.getBlockAt(x + id * 100, y, z + 1).setType(GRASS_BLOCK);
                                wor.getBlockAt(x + id * 100, y, z + 2).setType(GRASS_BLOCK);
                                wor.getBlockAt(x + id * 100, y, z - 1).setType(GRASS_BLOCK);
                                wor.getBlockAt(x + id * 100, y, z - 2).setType(GRASS_BLOCK);
                                wor.getBlockAt(x + id * 100 + 1, y, z + 1).setType(GRASS_BLOCK);
                                wor.getBlockAt(x + id * 100 - 1, y, z + 1).setType(GRASS_BLOCK);
                                wor.getBlockAt(x + id * 100 + 1, y, z - 1).setType(GRASS_BLOCK);
                                wor.getBlockAt(x + id * 100 - 1, y, z - 1).setType(GRASS_BLOCK);
                            }
                        }
                    }
                    id++;
                    data.set("id", id);
                    savedata();
                    yroven.add(0);
                    if (!superlegacy) {
                        if (lvl_bar_mode)
                            b.add(Bukkit.createBossBar(lvl_names.get(0), Progress_color, BarStyle.SEGMENTED_10, BarFlag.DARKEN_SKY));
                        else if (PAPI)
                            b.add(Bukkit.createBossBar((PlaceholderAPI.setPlaceholders(p, TextP)), Progress_color, BarStyle.SEGMENTED_10, BarFlag.DARKEN_SKY));
                        else
                            b.add(Bukkit.createBossBar((TextP), Progress_color, BarStyle.SEGMENTED_10, BarFlag.DARKEN_SKY));
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
                p.teleport(new Location(wor, x + data.getInt("_" + name) * 100 + 0.5, y + 1.2, z + 0.5));
                return true;
            }
            case ("leaf"):{
                config = this.getConfig();
                p = (Player) sender;
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
                p = (Player) sender;
                ((Entity) sender).getLocation();
                Location l = p.getLocation();
                x = (int) l.getX();
                y = (int) l.getY();
                z = (int) l.getZ();
                wor = l.getWorld();
                config = this.getConfig();
                config.set("world", wor.getName());
                config.set("x", (double) x);
                config.set("y", (double) y);
                config.set("z", (double) z);
                this.saveConfig();
                wor.getBlockAt(x, y, z).setType(GRASS_BLOCK);
                return true;
            }
            case ("setleaf"):{
                if (!sender.hasPermission("Oneblock.set")) {
                    sender.sendMessage(noperm);
                    return true;
                }
                p = (Player) sender;
                ((Entity) sender).getLocation();
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
            		 addinvate(((Player) sender).getName(),inv.getName());
            		 inv.sendMessage(ChatColor.GREEN +"you were invited by player "+inv.getName()+".");
            		 inv.sendMessage(ChatColor.RED +"/ob accept to accept).");
            		 sender.sendMessage(ChatColor.GREEN +"you invited "+inv.getName()+".");
            	 }
            	 return true;
            }
            case ("accept"):{
            	Player pl = (Player) sender;
           	 	if (checkinvate(pl))
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
                if (args.length > 1) {
                    if (args[1].equals("true")) {
                    	protection = true;
                    	config.set("protection", protection);	
                    }
                    else if (args[1].equals("false")) {
                    	protection = false;
                    	config.set("protection", protection);	
                    }
                    else
                    	sender.sendMessage(ChatColor.YELLOW + "enter a valid value true or false");
                }
                else
                	sender.sendMessage(ChatColor.YELLOW + "enter a valid value true or false");
                if (protection)
                	sender.sendMessage(ChatColor.GREEN + "the protection is now enabled.");
                else
                	sender.sendMessage(ChatColor.GREEN + "the protection is now disabled.");
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
                    data.set("Score_" + i, 0);
                    slomano.set(i, 0);
                    yroven.set(i, 0);
                    if (lvl_bar_mode)
                        b.get(i).setTitle(lvl_names.get(0));
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
                    sender.sendMessage(ChatColor.GREEN + "level multiplier now: " + lvl_mult);
                    sender.sendMessage(ChatColor.GREEN + "5 by default");
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
                sender.sendMessage(ChatColor.GREEN + "level multiplier now: " + lvl_mult);
                sender.sendMessage(ChatColor.GREEN + "5 by default");
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
                if (args[1].equalsIgnoreCase("true")) {
                    Progress_bar = true;
                    for (int i = 0; i < id; i++)
                        b.get(i).setVisible(true);
                    config.set("Progress_bar", Progress_bar);
                    return true;
                }
                if (args[1].equalsIgnoreCase("false")) {
                    Progress_bar = false;
                    for (int i = 0; i < id; i++)
                        b.get(i).setVisible(false);
                    config.set("Progress_bar", Progress_bar);
                    return true;
                }
                if (args[1].equalsIgnoreCase("color")) {
                    if (args.length == 2) {
                        sender.sendMessage(ChatColor.YELLOW + "enter a color name.");
                        return true;
                    }
                    try {
                        BarColor.valueOf(args[2]);
                        Progress_color = BarColor.valueOf(args[2]);
                        for (int i = 0; i < id; i++)
                            b.get(i).setColor(Progress_color);
                        config.set("Progress_bar_color", Progress_color.toString());
                    } catch (Exception e) {
                        Progress_color = BarColor.GREEN;
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
                if (chat_alert == false)
                    sender.sendMessage(ChatColor.GREEN + "Alerts are now disabled!");
                else
                    sender.sendMessage(ChatColor.GREEN + "Alerts are now on!");
                config.set("Chat_alert", chat_alert);
                return true;
            }
            case ("frequency"):{
                if (!sender.hasPermission("Oneblock.set")) {
                    sender.sendMessage(noperm);
                    return true;
                }
                if (args.length == 1) {
                    sender.sendMessage(ChatColor.YELLOW + "enter a valid value (5 to 20)");
                    sender.sendMessage(ChatColor.YELLOW + "7 by default");
                    return true;
                }
                Long fr_;
                String Sfr = "";
                try {
                    fr_ = Long.parseLong(args[1]);
                } catch (Exception e) {
                    sender.sendMessage(ChatColor.YELLOW + "enter a valid value (5 to 20)");
                    sender.sendMessage(ChatColor.YELLOW + "7 by default");
                    return true;
                }
                if (fr_ >= 5L && fr_ <= 20L && on) {
                    fr = fr_;
                    Bukkit.getScheduler().cancelTasks(this);
                    config.set("frequency", fr);
                    if (fr < 7L)
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
                if (args[1].equalsIgnoreCase("true")) {
                    il3x3 = true;
                    config.set("Island_for_new_players", true);
                    sender.sendMessage(ChatColor.GREEN + "Island_for_new_players = true");
                    return true;
                }
                if (args[1].equalsIgnoreCase("false")) {
                    il3x3 = false;
                    config.set("Island_for_new_players", false);
                    sender.sendMessage(ChatColor.GREEN + "Island_for_new_players = false");
                    return true;
                }
                if (args[1].equalsIgnoreCase("set_my_by_def")) {
                	if (legacy) {
                		sender.sendMessage(ChatColor.RED + "Not supported in legacy versions!");
                		return true;
                	}
                    p = (Player) sender;
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
                if (args[1].equalsIgnoreCase("true")) {
                    rebirth = true;
                    config.set("Rebirth_on_the_island", true);
                    sender.sendMessage(ChatColor.GREEN + "Rebirth_on_the_island = true");
                    return true;
                }
                if (args[1].equalsIgnoreCase("false")) {
                    rebirth = false;
                    config.set("Rebirth_on_the_island", false);
                    sender.sendMessage(ChatColor.GREEN + "Rebirth_on_the_island = false");
                    return true;
                }
                sender.sendMessage(ChatColor.YELLOW + "enter a valid value true or false");
                return true;
            }
            default:
            //ver
            sender.sendMessage(ChatColor.GREEN +
            	"  ▄▄    ▄▄\n"+
            	"█    █  █▄▀\n"+
            	"▀▄▄▀ █▄▀\n"+
            	"Create by MrMarL v0.8.1");
            if (superlegacy)
                sender.sendMessage(ChatColor.GREEN + "Server run super legacy(1.7 - 1.8)");
            else if (legacy)
                sender.sendMessage(ChatColor.GREEN + "Server run legacy(1.9 - 1.12)");
            else if (is1_13)
                sender.sendMessage(ChatColor.GREEN + "Server run 1.13");
            else if (is1_14)
                sender.sendMessage(ChatColor.GREEN + "Server run 1.14");
            else if (is1_15)
                sender.sendMessage(ChatColor.GREEN + "Server run 1.15");
            else if (is1_16)
                sender.sendMessage(ChatColor.GREEN + "Server run 1.16");
            else
                sender.sendMessage(ChatColor.GREEN + "Server run 1.17+");
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
        blocks = new ArrayList <Material>();
        File block = new File(getDataFolder(), "blocks.yml");
        if (!block.exists()) {
            saveResource("blocks.yml", false);
        }
        block = new File(getDataFolder(), "blocks.yml");
        newConfigz = YamlConfiguration.loadConfiguration(block);
        //conversion old lvl system to new
        if (!newConfigz.isList("0")) {
	        for (int i = 0; newConfigz.isString(i + ""); i++) {
	            blocks.add(Material.getMaterial(newConfigz.getString(i + "")));
	            newConfigz.set(i + "",null);
	        }
	        List <String> temp = new ArrayList <String>();
	        temp.add("Level: 0");
	        if (blocks.get(0) == null)
	        	temp.add(GRASS_BLOCK.name());
	        else
	        	temp.add(blocks.get(0).name());
	        newConfigz.set("0",temp);
	        temp = new ArrayList <String>();
	        temp.add("Level: 1");
	        int p = 1;
	        for(int i =1;blocks.size()>i;i++) {
	        	if (blocks.get(i) == null)
		        	temp.add(GRASS_BLOCK.name());
		        else
		        	temp.add(blocks.get(i).name());
	        	if (i%3 == 0) {
	        		if (i != 3 || i != blocks.size()-1)
	        			newConfigz.set(p + "",temp);
	        		temp = new ArrayList <String>();
	        		temp.add("Level: "+ ++p);
	        	}
	        }
	        newConfigz.set(p + "",temp);
	        try {
	        	newConfigz.save(new File(getDataFolder(), "blocks.yml"));
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        block = new File(getDataFolder(), "blocks.yml");
	        newConfigz = YamlConfiguration.loadConfiguration(block);
	        blocks = new ArrayList <Material>();
        }
        //
        lvl_names = new ArrayList <String>();
        lvl_sizes = new ArrayList <Integer>();
        for (int i = 0; newConfigz.isList(i + ""); i++) {
        	List <String> bl_temp = newConfigz.getStringList(i + "");
        	lvl_names.add(bl_temp.get(0));
        	for (int q = 1;q<bl_temp.size();q++) {
	        	if (Material.getMaterial(bl_temp.get(q)) != GRASS_BLOCK)
	                blocks.add(Material.getMaterial(bl_temp.get(q)));
	            else
	                blocks.add(null);
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
    private void Chestfile() {
        s_ch = new ArrayList <Material>();
        m_ch = new ArrayList <Material>();
        h_ch = new ArrayList <Material>();
        File chest = new File(getDataFolder(), "chests.yml");
        if (!chest.exists()) {
            saveResource("chests.yml", false);
        }
        chest = new File(getDataFolder(), "chests.yml");
        newConfigz = YamlConfiguration.loadConfiguration(chest);
        for (int i = 0; newConfigz.isString("s_ch" + i); i++) {
            s_ch.add(Material.getMaterial(newConfigz.getString("s_ch" + i)));
        }
        for (int i = 0; newConfigz.isString("m_ch" + i); i++) {
            m_ch.add(Material.getMaterial(newConfigz.getString("m_ch" + i)));
        }
        for (int i = 0; newConfigz.isString("h_ch" + i); i++) {
            h_ch.add(Material.getMaterial(newConfigz.getString("h_ch" + i)));
        }
    }
    private void Mobfile() {
        mobs = new ArrayList < EntityType > ();
        File mob = new File(getDataFolder(), "mobs.yml");
        if (!mob.exists()) {
            saveResource("mobs.yml", false);
        }
        newConfigz = YamlConfiguration.loadConfiguration(mob);
        for (int i = 0; newConfigz.isString("id" + i); i++) {
            try {
                mobs.add(EntityType.valueOf((newConfigz.getString("id" + i))));
            } catch (Exception ex) {
                // not supported mob)
            }
        }
    }
    private static void copyFileUsingStream(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            is.close();
            os.close();
        }
    }
    private void Configfile() {
        config = this.getConfig();
        if (config.isInt("id")) {
            File PlData = new File(getDataFolder(), "PlData.yml");
            try {
                copyFileUsingStream(new File(getDataFolder(), "config.yml"), PlData);
                id = config.getInt("id");
                for (int i = 0; i < id; i++)
                    config.set("Score_" + i, null);
                config.set("id", null);
                data = YamlConfiguration.loadConfiguration(PlData);
                data.set("x", null);
                data.set("y", null);
                data.set("z", null);
                data.set("world", null);
                data.set("leafworld", null);
                data.set("xleaf", null);
                data.set("yleaf", null);
                data.set("zleaf", null);
                data.set("Progress_bar", null);
                data.set("frequency", null);
                data.set("Progress_bar_text", null);
                savedata();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
            if (lvl_bar_mode)
                config.set("Chat_alert", false);
            else
                config.set("Chat_alert", true);
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
        if (config.isSet("custom_island") && !legacy) {
            List <String> cust_s = getConfig().getStringList("custom_island.y0");
            List <String> cust_s1 = getConfig().getStringList("custom_island.y1");
            List <String> cust_s2 = getConfig().getStringList("custom_island.y2");
            island = new BlockData[7][3][7];
            int yy = 0;
            for (int xx = 0, i = 0; xx < 7; xx++)
                for (int zz = 0; zz < 7; zz++, i++)
                    island[xx][yy][zz] = Bukkit.createBlockData(cust_s.get(i));
            yy++;
            for (int xx = 0, i = 0; xx < 7; xx++)
                for (int zz = 0; zz < 7; zz++, i++)
                    island[xx][yy][zz] = Bukkit.createBlockData(cust_s1.get(i));
            yy++;
            for (int xx = 0, i = 0; xx < 7; xx++)
                for (int zz = 0; zz < 7; zz++, i++)
                    island[xx][yy][zz] = Bukkit.createBlockData(cust_s2.get(i));
        }
        this.saveConfig();
    }
    public static int getlvl(String pl_name) {
        return yroven.get(data.getInt("_" + pl_name));
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
        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();

        if (args.length == 1) {
        	commands.add("j");
        	commands.add("join");
            commands.add("leaf");
        	commands.add("invite");
        	commands.add("accept");
        	commands.add("ver");
        	commands.add("accept");
        	commands.add("IDreset");
            if (sender.hasPermission("Oneblock.set")) {
                commands.add("set");
                commands.add("setleaf");
                commands.add("Progress_bar");
                commands.add("chat_alert");
                commands.add("setlevel");
                commands.add("clear");
                commands.add("lvl_mult");
                commands.add("reload");
                commands.add("frequency 7");
                commands.add("islands");
                commands.add("island_rebirth");
                commands.add("protection");
            }
            StringUtil.copyPartialMatches(args[0], commands, completions);
        } else if (args.length == 2) {
        	if (args[0].equals("invite")) {
        		for (Player ponl: plonl)
        			commands.add(ponl.getDisplayName());
        	}
        	else if (sender.hasPermission("Oneblock.set")) {
        		if (args[0].equals("setlevel")) {
            		for (Player ponl: plonl)
            			commands.add(ponl.getDisplayName());
            	}
        		else if (args[0].equals("Progress_bar")) {
	                commands.add("true");
	                commands.add("false");
	                commands.add("level");
	                commands.add("settext ...");
	                commands.add("color");
	            }
	            else if (args[0].equals("reload")) {
	                commands.add("blocks.yml");
	                commands.add("chests.yml");
	                commands.add("mobs.yml");
	            }
	            else if (args[0].equals("islands")) {
	                commands.add("true");
	                commands.add("false");
	                commands.add("mobs.yml");
	                commands.add("set_my_by_def");
	                commands.add("default");
	            }
	            else if (args[0].equals("island_rebirth")) {
	            	commands.add("true");
	                commands.add("false");
	            }
	            else if (args[0].equals("protection")) {
	                commands.add("true");
	                commands.add("false");
	            }
        	}
            StringUtil.copyPartialMatches(args[1], commands, completions);
        }
        Collections.sort(completions);
        return completions;
    }
}