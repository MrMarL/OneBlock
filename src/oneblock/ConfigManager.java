package oneblock;

import static oneblock.Oneblock.*;

import java.io.File;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.cryptomorin.xseries.XMaterial;
import com.nexomc.nexo.api.NexoBlocks;

import oneblock.gui.GUI;
import oneblock.migration.LegacyBlocksMigrator;
import oneblock.pldata.DatabaseManager;
import oneblock.utils.LowerCaseYaml;
import oneblock.utils.Utils;
import oneblock.worldguard.OBWorldGuard;
import dev.lone.itemsadder.api.CustomBlock;
import io.th0rgal.oraxen.api.OraxenItems;
import net.momirealms.craftengine.core.util.Key;

public class ConfigManager {
	public YamlConfiguration config_temp;
	public RewardManager reward = new RewardManager();
	
    public void loadConfigFiles() {
        Configfile();
        loadAdditionalConfigFiles();
    }
    
    public void loadAdditionalConfigFiles() {
    	Chestfile();
        Blockfile();
        Flowerfile();
        Messagefile();
        reward.loadRewards();
    }
	
    public void Configfile() {
    	File con = getFile("config.yml");
        config = LowerCaseYaml.loadAndFixConfig(con);
        
        plugin.setPosition(
        		Bukkit.getWorld(Check("world", "world")),
        		(int)Check("x", (double) getX()), 
        		(int)Check("y", (double) getY()), 
        		(int)Check("z", (double) getZ()));
        
        plugin.setLeave(
        		Bukkit.getWorld(Check("leaveworld", "world")), 
        		Check("xleave", .0), 
        		Check("yleave", .0), 
        		Check("zleave", .0), 
        		(float)Check("yawleave", .0));
        
        if (!superlegacy) {
        	progress_bar = Check("progress_bar", true);
        	Level.max.color = BarColor.valueOf(Check("progress_bar_color", "GREEN"));
        	Level.max.style = BarStyle.valueOf(Check("progress_bar_style", "SOLID"));
        	phText = Utils.translateColorCodes(Check("progress_bar_text", "level"));
	        lvl_bar_mode = phText.equals("level");
        }
        island_for_new_players = Check("island_for_new_players", true);
        Level.multiplier = Check("level_multiplier", Level.multiplier);
        max_players_team = Check("max_players_team", max_players_team);
        mob_spawn_chance = Check("mob_spawn_chance", mob_spawn_chance);
        mob_spawn_chance = mob_spawn_chance < 2 ? 9 : mob_spawn_chance;
        updateBoolParameters();
        OBWorldGuard.setEnabled(Check("worldguard", OBWorldGuard.canUse));
        OBWorldGuard.flags = Check("wgflags", OBWorldGuard.flags);
        plugin.setOffset(Check("set", 100));
        if (config.isSet("custom_island") && !legacy)
        	Island.read(config);
        
        DatabaseConfig();
        
        LegacyConfigSaver.Save(config, con);
    }
	 
    public void updateBoolParameters() {
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
        if (isBorderSupported) border = Check("border", border);
    }
    
    private void DatabaseConfig() {
        DatabaseManager.dbType = Check("database.type", DatabaseManager.dbType).toLowerCase();
        DatabaseManager.host = Check("database.host", DatabaseManager.host);
        DatabaseManager.port = Check("database.port", DatabaseManager.port);
        DatabaseManager.database = Check("database.name", DatabaseManager.database);
        DatabaseManager.username = Check("database.username",  DatabaseManager.username);
        DatabaseManager.password = Check("database.password", DatabaseManager.password);
        DatabaseManager.useSSL = Check("database.useSSL", DatabaseManager.useSSL);
        DatabaseManager.autoReconnect = Check("database.autoReconnect", DatabaseManager.autoReconnect);
    }
        
	public void Blockfile() {
    	Level.levels.clear();
    	PoolRegistry.clear();
        File block = getFile("blocks.yml");
        
        // Migrate legacy (cumulative-list) blocks.yml in-place before parsing.
        // Uses ChestItems.chest (already-loaded or legacy) to map old chest-alias
        // tokens to vanilla loot-table keys during flattening.
        LegacyBlocksMigrator.migrateBlocks(block, ChestItems.chest);
        
        config_temp = YamlConfiguration.loadConfiguration(block);
        
        // MaxLevel: may be scalar (name-only, legacy passthrough) or a full list entry.
        if (config_temp.isString("MaxLevel"))
        	Level.max.name = Utils.translateColorCodes(config_temp.getString("MaxLevel"));
        else if (config_temp.isList("MaxLevel"))
        	parseLevelFromList(config_temp.getList("MaxLevel"), Level.max);
        
        for (int i = 0; config_temp.isList(String.format("%d", i)); i++) {
        	List<?> bl_temp = config_temp.getList(String.format("%d", i));
        	if (bl_temp == null || bl_temp.isEmpty()) continue;
        	Object first = bl_temp.get(0);
        	Level level = new Level(first instanceof String
        			? Utils.translateColorCodes((String) first)
        			: "Level " + i);
        	Level.levels.add(level);
        	parseLevelFromList(bl_temp, level);
        }
        
        if (PoolRegistry.totalMobs() == 0) 
        	plugin.getLogger().warning("Mobs are not set in the blocks.yml");
        
        Level.max.blocks = PoolRegistry.totalBlocks();
        Level.max.mobs = PoolRegistry.totalMobs();
        
        SetupProgressBar();
    }
    
    /**
     * Parse a level list (header + pool entries) into the given {@link Level}.
     * Header positions 0..3 are name/color/style/length (best-effort, same semantics
     * as the legacy parser). Pool entries beyond the header may be plain strings
     * (legacy, weight=1) or maps with {@code block|mob|loot_table|command} + optional
     * {@code weight}. Unresolved / malformed entries are skipped with a warning.
     */
    private void parseLevelFromList(List<?> bl_temp, Level level) {
    	if (bl_temp == null || bl_temp.isEmpty()) return;
    	int q = 0;
    	if (q < bl_temp.size() && bl_temp.get(q) instanceof String) {
    		level.name = Utils.translateColorCodes((String) bl_temp.get(q));
    		q++;
    	}
    	// Duck-type probe: the string at position q may be a BarColor, a BarStyle
    	// OR the next header field (length) OR the first pool entry. We attempt
    	// each shape in turn; on failure we DO NOT advance q, leaving the string
    	// for the next parser. This is legacy config compatibility, not a bug.
    	if (!superlegacy) {
    		level.color = Level.max.color;
    		if (q < bl_temp.size() && bl_temp.get(q) instanceof String) try {
    			level.color = BarColor.valueOf(((String) bl_temp.get(q)).toUpperCase());
    			q++;
    		} catch (Exception e) {}
    		level.style = Level.max.style;
    		if (q < bl_temp.size() && bl_temp.get(q) instanceof String)  try {
    			level.style = BarStyle.valueOf(((String) bl_temp.get(q)).toUpperCase());
    			q++;
    		} catch (Exception e) {}
    	}
    	if (q < bl_temp.size()) {
    		Object lenItem = bl_temp.get(q);
    		if (lenItem instanceof Number) {
    			level.length = Math.max(1, ((Number) lenItem).intValue());
    			q++;
    		} else if (lenItem instanceof String) {
    			// Duck-type probe (see above): if the string can't be parsed as an
    			// int, it's the first pool-entry token; leave q unchanged so the
    			// pool-entry loop below picks it up.
    			try {
    				level.length = Math.max(1, Integer.parseInt((String) lenItem));
    				q++;
    			} catch (Exception e) { level.length = 16 + level.getId() * Level.multiplier; }
    		} else {
    			level.length = 16 + level.getId() * Level.multiplier;
    		}
    	}
    	while (q < bl_temp.size()) {
    		Object raw = bl_temp.get(q++);
    		parsePoolEntry(raw, level);
    	}
    	level.blocks = PoolRegistry.totalBlocks();
    	level.mobs = PoolRegistry.totalMobs();
    }
    
    @SuppressWarnings("unchecked")
    private void parsePoolEntry(Object raw, Level level) {
    	if (raw == null) return;
    	int weight = 1;
    	String kind;
    	Object payload;
    	
    	if (raw instanceof Map) {
    		Map<String, Object> m = (Map<String, Object>) raw;
    		if (m.containsKey("weight")) {
    			Object w = m.get("weight");
    			if (w instanceof Number) weight = Math.max(1, ((Number) w).intValue());
    			else if (w != null) {
    				try { weight = Math.max(1, Integer.parseInt(w.toString())); }
    				catch (NumberFormatException nfe) {
    					plugin.getLogger().warning("blocks.yml: non-numeric weight '" + w + "' in entry " + m + "; defaulting to 1.");
    				}
    			}
    		}
    		if      (m.containsKey("block"))      { kind = "block";      payload = m.get("block"); }
    		else if (m.containsKey("mob"))        { kind = "mob";        payload = m.get("mob"); }
    		else if (m.containsKey("chest"))      { kind = "chest";      payload = m.get("chest"); }
    		else if (m.containsKey("command"))    { kind = "command";    payload = m.get("command"); }
    		else {
    			plugin.getLogger().warning("blocks.yml: entry has no recognized kind (expected one of block/mob/loot_table/command): " + m);
    			return;
    		}
    	} else if (raw instanceof String) {
    		String text = (String) raw;
    		if (text.isEmpty()) return;
    		if (text.charAt(0) == '/') { kind = "command"; payload = text; }
    		else {
    			try { EntityType.valueOf(text.toUpperCase()); kind = "mob"; payload = text.toUpperCase(); }
    			catch (Exception ignore) { kind = "block"; payload = text; }
    		}
    	} else {
    		return;
    	}
    	
    	if (payload == null) return;
    	switch (kind) {
    		case "block":
    			PoolRegistry.addBlock(resolveBlock(payload.toString()), weight);
    			break;
    		case "mob":
    			EntityType et;
    			try { et = EntityType.valueOf(payload.toString().toUpperCase()); }
    			catch (Exception e) {
    				plugin.getLogger().warning("blocks.yml: unknown mob '" + payload + "'");
    				return;
    			}
    			PoolRegistry.addMob(et, weight);
    			break;
    		case "chest":
    			String chest_name = payload.toString();
    			if (!ChestItems.hasChest(chest_name)) {
    				plugin.getLogger().warning("blocks.yml: chest name '" + payload + "' not found in chests.yml");
    				return;
    			}
    			PoolRegistry.addBlock(PoolEntry.chest(chest_name), weight);
    			break;
    		case "command":
    			String str = payload.toString();
    			try { String.format(str.substring(1), 99, 64, 99); } 
    			catch (Exception e) 
    			{
    				plugin.getLogger().warning("blocks.yml: invalid command '" + payload + "'"); 				
    				return;
    			}
    			PoolRegistry.addBlock(PoolEntry.command(str), weight);
    			break;
    	}
    }
    
    /**
     * Resolve a block-name string to a {@link PoolEntry}. Mirrors the legacy resolver
     * chain: Material → custom block (ItemsAdder / Oraxen / Nexo / CraftEngine) →
     * XMaterial (legacy servers). Unresolved names fall back to {@link PoolEntry#GRASS}
     * which renders as grass + chance of flower at runtime.
     */
    private PoolEntry resolveBlock(String text) {
    	if (text == null || text.isEmpty()) return PoolEntry.GRASS;
    	Object mt = Material.matchMaterial(text);
    	if (mt == null || mt == GRASS_BLOCK || !((Material) mt).isBlock())
    		mt = getCustomBlock(text);
    	if (legacy && mt == null) {
    		mt = XMaterial.matchXMaterial(text)
    				.map(xmt -> xmt == GRASS_BLOCK ? null : xmt)
    				.orElse(null);
    	}
    	if (mt == null) return PoolEntry.GRASS;
    	return PoolEntry.block(mt);
    }
	
	private Object getCustomBlock(String text) {
	    switch (plugin.placetype) {
	        case ItemsAdder: return CustomBlock.getInstance(text);
	        case Oraxen: return OraxenItems.exists(text) ? text : null;
	        case Nexo: return NexoBlocks.isCustomBlock(text) ? text : null;
	        case CraftEngine:
	            String[] pcid = text.split(":", 2);
	            return pcid.length == 2 ? Key.of(pcid) : null;
	        default: return null;
	    }
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
        	        	
			inf.bar.setVisible(progress_bar);
        }});
	}
	
    private void Messagefile() {
        File message = getFile("messages.yml");
        config_temp = YamlConfiguration.loadConfiguration(message);
        
        Messages.help = MessageCheck("help", Messages.help);
        Messages.help_adm = MessageCheck("help_adm", Messages.help_adm);
        Messages.invite_usage = MessageCheck("invite_usage", Messages.invite_usage);
        Messages.invite_yourself = MessageCheck("invite_yourself", Messages.invite_yourself);
        Messages.invite_no_island = MessageCheck("invite_no_island", Messages.invite_no_island);
        Messages.invite_team = MessageCheck("invite_team", Messages.invite_team);
        Messages.invited = MessageCheck("invited", Messages.invited);
        Messages.invited_success = MessageCheck("invited_success", Messages.invited_success);
        Messages.kicked = MessageCheck("kicked", Messages.kicked);
        Messages.kick_usage = MessageCheck("kick_usage", Messages.kick_usage);
        Messages.kick_yourself = MessageCheck("kick_yourself", Messages.kick_yourself);
        Messages.accept_success = MessageCheck("accept_success", Messages.accept_success);
        Messages.accept_none = MessageCheck("accept_none", Messages.accept_none);
        Messages.idreset = MessageCheck("idreset", Messages.idreset);
        Messages.protection = MessageCheck("protection", Messages.protection);
        Messages.leave_not_set = MessageCheck("leave_not_set", Messages.leave_not_set);
        Messages.not_allow_visit = MessageCheck("not_allow_visit", Messages.not_allow_visit);
        Messages.allowed_visit = MessageCheck("allowed_visit", Messages.allowed_visit);
        Messages.forbidden_visit = MessageCheck("forbidden_visit", Messages.forbidden_visit);
        
        File gui = getFile("gui.yml");
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
        File flower = getFile("flowers.yml");
        config_temp = YamlConfiguration.loadConfiguration(flower);
        plugin.flowers.add(GRASS);
        for(String list:config_temp.getStringList("flowers"))
        	plugin.flowers.add(XMaterial.matchXMaterial(list).orElse(GRASS));
    }
    
    private void Chestfile() {
        ChestItems.chest = getFile("chests.yml");
        ChestItems.load();
    }
    
    File getFile(String name) {
    	File file = new File(plugin.getDataFolder(), name);
        if (!file.exists())
        	plugin.saveResource(name, false);
        return file;
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
