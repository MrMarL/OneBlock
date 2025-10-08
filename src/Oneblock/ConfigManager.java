package Oneblock;

import static Oneblock.Oneblock.*;

import java.io.File;
import java.util.List;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.cryptomorin.xseries.XMaterial;
import com.nexomc.nexo.api.NexoBlocks;

import Oneblock.PlData.LowerCaseYaml;
import Oneblock.Utils.Utils;
import Oneblock.WorldGuard.OBWorldGuard;
import Oneblock.gui.GUI;
import dev.lone.itemsadder.api.CustomBlock;
import io.th0rgal.oraxen.api.OraxenItems;
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks;
import net.momirealms.craftengine.core.util.Key;

public class ConfigManager {
	public YamlConfiguration config_temp;
	public RewardManager reward = new RewardManager();
	
    public void loadConfigFiles() {
        Configfile();
        Chestfile();
        Blockfile();
        Flowerfile();
        Messagefile();
        reward.loadRewards();
    }
	
	private void Configfile() {
    	File con = new File(plugin.getDataFolder(), "config.yml");
        if (!con.exists())
        	plugin.saveResource("config.yml", false);
        config = LowerCaseYaml.loadAndFixConfig(con);
        
        plugin.setPosition(
        		Bukkit.getWorld(Check("world", "world")),
        		(int)Check("x", (double) x), 
        		(int)Check("y", (double) y), 
        		(int)Check("z", (double) z));
        
        plugin.setLeave(
        		Bukkit.getWorld(Check("leaveworld", "world")), 
        		Check("xleave", .0), 
        		Check("yleave", .0), 
        		Check("zleave", .0), 
        		(float)Check("yawleave", .0));
        
        if (!superlegacy) {
        	Progress_bar = Check("progress_bar", true);
        	Level.max.color = BarColor.valueOf(Check("progress_bar_color", "GREEN"));
        	Level.max.style = BarStyle.valueOf(Check("progress_bar_style", "SOLID"));
	        TextP = Check("progress_bar_text", "level");
	        lvl_bar_mode = TextP.equals("level");
        }
        island_for_new_players = Check("island_for_new_players", true);
        Level.multiplier = Check("level_multiplier", Level.multiplier);
        max_players_team = Check("max_players_team", max_players_team);
        UpdateBoolParametrs();
        OBWorldGuard.setEnabled(Check("worldguard", OBWorldGuard.canUse));
        OBWorldGuard.flags = Check("wgflags", OBWorldGuard.flags);
        offset = Check("set", 100);
        if (config.isSet("custom_island") && !legacy)
        	Island.read(config);
        LegacyConfigSaver.Save(config, con);
    }
	 
    public void UpdateBoolParametrs() {
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
        rebirth = Check("rebirth_on_the_island", rebirth);
        if (isBorderSupported) Border = Check("border", Border);
    }
        
	public void Blockfile() {
		plugin.blocks.clear();
		plugin.mobs.clear();
    	Level.levels.clear();
        File block = new File(plugin.getDataFolder(), "blocks.yml");
        if (!block.exists())
        	plugin.saveResource("blocks.yml", false);
        config_temp = YamlConfiguration.loadConfiguration(block);
        if (config_temp.isString("MaxLevel"))
        	Level.max.name = Utils.translateColorCodes(config_temp.getString("MaxLevel"));
        for (int i = 0; config_temp.isList(String.format("%d", i)); i++) {
        	List <String> bl_temp = config_temp.getStringList(String.format("%d", i));
        	Level level = new Level(Utils.translateColorCodes(bl_temp.get(0)));
        	Level.levels.add(level);
        	int q = 1;
        	if (!superlegacy && q < bl_temp.size()) {
        		try {//reading a custom color for the level.
        			level.color = BarColor.valueOf(bl_temp.get(q).toUpperCase());
        			q++;
        		} catch(Exception e) {level.color = Level.max.color;}
	        	try {//reading a custom style for the level.
	    			level.style = BarStyle.valueOf(bl_temp.get(q).toUpperCase());
	    			q++;
	    		} catch(Exception e) {level.style = Level.max.style;}
	        } try {//reading a custom size for the level.
	        	int value = Integer.parseInt(bl_temp.get(q));
	    		level.length = value > 0 ? value : 1;
	    		q++;
	    	} catch(Exception e) {level.length = 16 + level.getId() * Level.multiplier;}
        	while (q < bl_temp.size()) {
        		String text = bl_temp.get(q++);
        		//reading a custom block (command).
        		if (text.charAt(0) == '/') {
        			plugin.blocks.add(text);
	            	continue;
        		}
        		//reading a custom chest.
        		boolean check = false;
        		for (String str : ChestItems.getChestNames())
	        		if (text.equals(str)) {
	        			check = plugin.blocks.add(str); break;
	        		}
        		if (check) continue;
        		//reading a mob.
        		try { plugin.mobs.add(EntityType.valueOf(text)); continue; }
        		catch (Exception e) {}
        		//read a material
        		if (legacy){ //XMaterial lib
        			Optional <XMaterial> a = XMaterial.matchXMaterial(text);
	        		if (!a.isPresent()) {
	        			plugin.blocks.add(null);
	        			continue;
	        		}
	        		XMaterial xmt = a.get();
	        		if (xmt == GRASS_BLOCK)
	        			plugin.blocks.add(null);
	        		else if (xmt == XMaterial.CHEST)
	        			plugin.blocks.add(Material.CHEST);
	        		else
	        			plugin.blocks.add(xmt);
        		}
        		else {
        			Object a = Material.matchMaterial(text);
        			if (a != null && a.equals(Material.GRASS_BLOCK))
        				a = null;
        			if (a == null) {
        				switch (plugin.placetype) {
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
						case CraftEngine:
							String[] pcid = text.split(":", 2);
							if (pcid.length == 2) {
								Key key = Key.of(pcid);
								if (CraftEngineBlocks.byId(key) != null)
									a = key;
							}
							break;
						default: break;
        				}
        			}
        			plugin.blocks.add(a);
        		}
        	}
        	level.blocks = plugin.blocks.size();
        	level.mobs = plugin.mobs.size();
        }
        Level.max.blocks = plugin.blocks.size();
        if ((Level.max.mobs = plugin.mobs.size()) == 0) 
        	plugin.getLogger().warning("Mobs are not set in the blocks.yml");
        
        SetupProgressBar();
    }
	
    public void SetupProgressBar() {
		if (superlegacy) return;
		if (PlayerInfo.size() == 0) return;
		
		if (Level.max.color == null) Level.max.color = BarColor.GREEN;
		if (Level.max.style == null) Level.max.style = BarStyle.SOLID;
		
		PlayerInfo.list.forEach(inf -> {if (inf.uuid != null){
			Player p = Bukkit.getPlayer(inf.uuid);
			if (p == null)
				inf.createBar();
			else
				inf.createBar(getBarTitle(p, inf.lvl));
        	        	
			inf.bar.setVisible(Progress_bar);
        }});
	}
	
    private void Messagefile() {
        File message = new File(plugin.getDataFolder(), "messages.yml");
        if (!message.exists())
        	plugin.saveResource("messages.yml", false);
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
        
        File gui = new File(plugin.getDataFolder(), "gui.yml");
        if (!gui.exists())
        	plugin.saveResource("gui.yml", false);
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
        	return Utils.translateColorCodes(config_temp.getString(name));
    	return def_message;
    }
    
    private void Flowerfile() {
        plugin.flowers.clear();
        File flower = new File(plugin.getDataFolder(), "flowers.yml");
        if (!flower.exists())
        	plugin.saveResource("flowers.yml", false);
        config_temp = YamlConfiguration.loadConfiguration(flower);
        plugin.flowers.add(GRASS);
        for(String list:config_temp.getStringList("flowers"))
        	if (!XMaterial.matchXMaterial(list).isPresent())
        		plugin.flowers.add(GRASS);
        	else
        		plugin.flowers.add(XMaterial.matchXMaterial(list).get());
    }
    
    private void Chestfile() {
        File chest = new File(plugin.getDataFolder(), "chests.yml");
        if (!chest.exists())
        	plugin.saveResource("chests.yml", false);
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
}
