package Oneblock;

import static Oneblock.Oneblock.*;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.cryptomorin.xseries.XMaterial;

import Oneblock.Invitation.Guest;
import Oneblock.Invitation.Invitation;
import Oneblock.WorldGuard.OBWorldGuard;
import Oneblock.gui.GUI;

public class CommandHandler implements CommandExecutor {
	
	public static boolean idresetCommand(Player pl) {
    	UUID uuid = pl.getUniqueId();
    	int PlId = PlayerInfo.GetId(uuid);
    	if (PlId == -1) return false;
    	PlayerInfo plp = PlayerInfo.get(PlId);
    	plp.removeBar(pl);
    	plp.removeUUID(uuid);
    	
    	if (!saveplayerinventory) pl.getInventory().clear();
    		
    	if (OBWorldGuard.isEnabled()) 
    		plugin.OBWG.removeMember(uuid, PlId);
    	
    	return true;
	}
	
    @Override
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
	            if (y == 0 || getWorld() == null) {
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
	            	int result[] = plugin.getIslandCoordinates(plID);
	            	X_pl = result[0]; Z_pl = result[1];
	            	if (plID != PlayerInfo.size())
	            		Island.clear(getWorld(), X_pl, y, Z_pl, offset/4);
	                Island.place(getWorld(), X_pl, y, Z_pl);
	                plugin.OBWG.CreateRegion(uuid, X_pl, Z_pl, offset, plID);
					PlayerInfo.set(plID, inf);
					if (!superlegacy)
						inf.createBar(getBarTitle(p, 0));
	            } 
	            else {
	            	int result[] = plugin.getIslandCoordinates(plID);
	                X_pl = result[0]; Z_pl = result[1];
	            }
	            if (!plugin.enabled) plugin.runMainTask();
	            if (Progress_bar) PlayerInfo.get(plID).bar.setVisible(true);
	            p.teleport(new Location(getWorld(), X_pl + 0.5, y + 1.2013, Z_pl + 0.5));
	            if (OBWorldGuard.isEnabled()) plugin.OBWG.addMember(uuid, plID);
	            return true;
	        }
	        case ("leave"):{
	            Player p = (Player) sender;
	            PlayerInfo.removeBarStatic(p);
	            if (plugin.leavewor == null || config.getDouble("yleave") == 0) {
	            	if (!args[args.length-1].equals("/n"))
	            		sender.sendMessage(Messages.leave_not_set);
	            	return true;
	            }
	            p.teleport(plugin.getLeave());
	            return true;
	        }
	        case ("v"):
	        case ("visit"):{
	        	if (!sender.hasPermission("Oneblock.visit")) {
	                sender.sendMessage(Messages.noperm_inv);
	                return true;
	            }
	        	if (!OBWorldGuard.isEnabled()) {
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
	        	final int result[] = plugin.getIslandCoordinates(plID);
	            final int X_pl = result[0], Z_pl = result[1];
	    		
	            if (protection) Guest.list.add(new Guest(uuid, pl.getUniqueId()));
	            pl.teleport(new Location(getWorld(), X_pl + 0.5, y + 1.2013, Z_pl + 0.5));
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
	        		if (OBWorldGuard.isEnabled())
	        			plugin.OBWG.removeMember(member_uuid, ownerID);
	        	}
	        	if (!(member instanceof Player)) return true;
	        	Player member_ex = (Player) member;
	        	int memberID = plugin.findNearestRegionId(member_ex.getLocation());
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
	        	if (!idresetCommand(pl)) return true;
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
	        		Bukkit.getScheduler().runTaskLater(plugin, () -> { LegacyConfigSaver.Save(config); }, 2L); // Saving the config.yml file after making changes.
		        	switch (parametr) {
			            case ("set"):{
			                Player p = (Player) sender;
			                int temp = 100;
			                if (args.length >= 2) {
			                    try {
			                    	temp = Integer.parseInt(args[1]);
			                    	if (temp > 10000 || temp < -10000) throw new NumberFormatException();
			                    } 
			                    catch (NumberFormatException nfe) {
			                    	sender.sendMessage(Messages.invalid_value);
			                    	return true;
			                    }
			                    offset = temp;
			                }
			                config.set("set", offset);
			                plugin.setPosition(p.getLocation());
			                if (!plugin.enabled) plugin.runMainTask();
			                getWorld().getBlockAt(x, y, z).setType(GRASS_BLOCK.get());
			                plugin.OBWG.ReCreateRegions();
			                LegacyConfigSaver.Save(config);
			                return true;
			            }
			            case ("setleave"):{
			                Player p = (Player) sender;
			                plugin.setLeave(p.getLocation());
			                return true;
			            }
			            case ("worldguard"):{
			            	if (!Bukkit.getPluginManager().isPluginEnabled("WorldGuard")){
			                    sender.sendMessage(String.format("%sThe WorldGuard plugin was not detected!", ChatColor.YELLOW));
			                    return true;
			                }
			            	if (!OBWorldGuard.canUse) {
			                    sender.sendMessage(String.format("%sThis feature is only available in the premium version of the plugin!", ChatColor.YELLOW));
			                    return true;
			                }
			            	if (args.length > 1 &&
			                	(args[1].equals("true") || args[1].equals("false"))) {
			            			OBWorldGuard.setEnabled(Boolean.valueOf(args[1]));
			                    	config.set("WorldGuard", OBWorldGuard.isEnabled());
			                    	if (OBWorldGuard.isEnabled())
			                    		plugin.OBWG.ReCreateRegions();
			                    	else
			                    		plugin.OBWG.RemoveRegions(PlayerInfo.size());
			                }
			                else sender.sendMessage(Messages.bool_format);
			            	sender.sendMessage(String.format("%sthe OBWorldGuard is now %s", ChatColor.GREEN, (OBWorldGuard.isEnabled()?"enabled.":"disabled.")));
			           		return true;
			            }
			            case ("border"):
			            	if (!isBorderSupported){
			                    sender.sendMessage(String.format("%sThe border can only be used on version 1.18.2 and above!", ChatColor.YELLOW));
			                    return true;
			                }
			            	Bukkit.getScheduler().runTaskLater(plugin, () -> { plugin.ReloadBorders(); }, 2L);
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
			            case ("rebirth_on_the_island"):{
			            	if (args.length > 1 &&
			                    	(args[1].equals("true") || args[1].equals("false"))) {
			                    	config.set(parametr, Boolean.valueOf(args[1]));
			                    	configManager.UpdateBoolParametrs();
			                }
			                else sender.sendMessage(Messages.bool_format);
			                sender.sendMessage(String.format("%s%s is now %s", ChatColor.GREEN, parametr, (config.getBoolean(parametr)?"enabled.":"disabled.")));
			           		return true;
			            }
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
			                    	if (setlvl < 0 || setlvl > 10000) throw new NumberFormatException();
			                    } 
			                    catch (NumberFormatException nfe) {
			                    	sender.sendMessage(String.format("%sinvalid level value.", ChatColor.RED));
			                    	return true;
			                    }
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
			                    int result[] = plugin.getIslandCoordinates(i);
			                    Island.clear(getWorld(), result[0], y, result[1], offset/4);
			                    sender.sendMessage(String.format("%splayer %s island is destroyed! :D", ChatColor.GREEN, args[1]));
			                    return true;
			                }
			                sender.sendMessage(String.format("%sa player named %s was not found.", ChatColor.RED, args[1]));
			                return true;
			            }
			            case ("lvl_mult"): {
			                if (args.length > 1) {
				                try {
				                    int lvl = Integer.parseInt(args[1]);
				                    if (lvl < 0 || lvl > 20) throw new NumberFormatException();
				                    config.set("level_multiplier", Level.multiplier = lvl);
				                    configManager.Blockfile();
				                } 
				                catch (NumberFormatException nfe) {
				                    sender.sendMessage(String.format("%sinvalid multiplier value. Possible values: from 0 to 20.", ChatColor.RED));
				                    return true;
				                }
			                }
			                sender.sendMessage(String.format("%slevel multiplier now: %d\n5 by default", ChatColor.GREEN, Level.multiplier));
			                return true;
			            }
			            case ("max_players_team"): {
			                if (args.length > 1) {
				                try {
				                    int mpt = Integer.parseInt(args[1]);
				                    if (mpt < 0 || mpt > 20) throw new NumberFormatException();
				                    config.set("max_players_team", max_players_team = mpt);
				                } 
				                catch (NumberFormatException nfe) {
				                    sender.sendMessage(String.format("%sinvalid max_players_team value. Possible values: from 0 to 20.", ChatColor.RED));
				                    return true;
				                }
			                }
			                sender.sendMessage(String.format("%smax_players_team now: %d\n0 is unlimited", ChatColor.GREEN, max_players_team));
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
			                    configManager.Blockfile();
			                    config.set("progress_bar", Progress_bar);
			                    return true;
			                }
			                
			                if (!Progress_bar) return true;
			                
			                if (args[1].equalsIgnoreCase("color")) {
			                    if (args.length == 2) {
			                        sender.sendMessage(String.format("%senter a color name.", ChatColor.YELLOW));
			                        return true;
			                    }
			                    try {
			                    	Level.max.color = BarColor.valueOf(args[2]);
			                    	configManager.Blockfile();
			                        config.set("progress_bar_color", Level.max.color.toString());
			                    } catch (Exception e) {
			                        sender.sendMessage(String.format("%sPlease enter a valid color. For example: RED", ChatColor.YELLOW));
			                    }
			                    sender.sendMessage(String.format("%sProgress bar color = %s", ChatColor.GREEN, Level.max.color.toString()));
			                    return true;
			                }
			                if (args[1].equalsIgnoreCase("style")) {
			                    if (args.length == 2) {
			                        sender.sendMessage(String.format("%senter a style name.", ChatColor.YELLOW));
			                        return true;
			                    }
			                    try {
			                    	Level.max.style = BarStyle.valueOf(args[2]);
			                    	configManager.Blockfile();
			                        config.set("progress_bar_style", Level.max.style.toString());
			                    } catch (Exception e) {
			                        sender.sendMessage(String.format("%sPlease enter a valid style. For example: SOLID", ChatColor.YELLOW));
			                    }
			                    sender.sendMessage(String.format("%sProgress bar style = %s", ChatColor.GREEN, Level.max.style.toString()));
			                    return true;
			                }
			                if (args[1].equalsIgnoreCase("level")) {
			                	lvl_bar_mode = true;
			                    config.set("progress_bar_text", "level");
			                    configManager.SetupProgressBar();
			                    return true;
			                }
			                if (args[1].equalsIgnoreCase("settext")) {
			                    String txt_bar = "";
								for (int i = 2; i < args.length; i++)
									txt_bar = i == 2 ? args[i] : String.format("%s %s", txt_bar, args[i]);
			                    lvl_bar_mode = false;
			                    config.set("progress_bar_text", TextP = txt_bar);
			                    configManager.SetupProgressBar();
			                    return true;
			                }
			                sender.sendMessage(String.format("%strue, false, settext or level only!", ChatColor.RED));
			                return true;
			            }
			            case ("listlvl"):{
			                if (args.length > 1) {
			                	int temp = 0;
			                    try {
			                    	temp = Integer.parseInt(args[1]);
			                    	if (temp < 0 || temp >= Level.size()) throw new NumberFormatException();
			                    } 
			                    catch (NumberFormatException nfe) {
			                    	sender.sendMessage(String.format("%sundefined lvl", ChatColor.RED));
			                    	return true;
			                    }
			                    sender.sendMessage(String.format("%s%s",ChatColor.GREEN, Level.get(temp).name));
			                    int i = (temp == 0) ? 0 : Level.get(temp-1).blocks;
			                    for(;i<Level.get(temp).blocks;i++)
			                    	if (plugin.blocks.get(i) == null)
			                    		sender.sendMessage("Grass (undefined)");
			                    	else
			                    		sender.sendMessage(plugin.blocks.get(i).toString());
			                    return true;
			                }
			                for(int i = 0;i<Level.size();i++)
			                	sender.sendMessage(String.format("%d: %s%s", i, ChatColor.GREEN, Level.get(i).name));
			                return true;
			            }
			            case ("reload"):{
			            	sender.sendMessage(String.format("%sReloading Plugin & Plugin Modules.", ChatColor.YELLOW));
			            	plugin.reload();
			            	sender.sendMessage(String.format("%sAll *.yml reloaded!", ChatColor.GREEN));
			            	return true;
			            }
			            case ("islands"):{
			                if (args.length == 1) {
			                    sender.sendMessage(Messages.bool_format);
			                    return true;
			                }
			                if (args[1].equals("true") || args[1].equals("false")) {
			                    island_for_new_players = Boolean.valueOf(args[1]);
			                    config.set("Island_for_new_players", island_for_new_players);
			                    sender.sendMessage(ChatColor.GREEN + "Island_for_new_players = " + island_for_new_players);
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
			                        int result[] = plugin.getIslandCoordinates(PlayerInfo.GetId(uuid));
			                        Island.scan(getWorld(), result[0], y, result[1]);
			                        sender.sendMessage(ChatColor.GREEN + "A copy of your island has been successfully saved!");
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
			                    config.set("custom_island", Island.custom = null);
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
	        		    "\n▄▄▄ ▄▄ " +
	        		    "\n█░█ █▄▀" +
	        		    "\n█▄█ █▄▀ by MrMarL" +
	        		    "\nPlugin version: v" + plugin.version +
	        		    "\nServer version: " + (superlegacy ? "super legacy " : (legacy ? "legacy " : "")) + "1." + XMaterial.getVersion() + ".X");
    		     return true;
		    }
	    }
    }
}