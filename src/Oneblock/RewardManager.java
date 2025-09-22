package Oneblock;

import static Oneblock.Oneblock.*;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import Oneblock.Utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RewardManager {
    private List<String> allRewards = new ArrayList<>();
    private Map<Integer, List<String>> levelRewards = new HashMap<>();
    
    public void loadRewards() {
        allRewards.clear();
        levelRewards.clear();
        
        File rewardsFile = new File(plugin.getDataFolder(), "rewards.yml");
        if (!rewardsFile.exists()) {
            plugin.saveResource("rewards.yml", false);
        }
        
        YamlConfiguration config = YamlConfiguration.loadConfiguration(rewardsFile);
        
        // Load general rewards
        if (config.isList("all")) {
            List<String> rawRewards = config.getStringList("all");
            allRewards = new ArrayList<>();
            for (String reward : rawRewards) 
                allRewards.add(Utils.translateColorCodes(reward));
        }
        
        // Load level-specific rewards
        if (config.isConfigurationSection("levels")) {
            for (String levelStr : config.getConfigurationSection("levels").getKeys(false)) {
                try {
                    int level = Integer.parseInt(levelStr);
                    List<String> rawRewards = config.getStringList("levels." + levelStr);
                    List<String> processedRewards = new ArrayList<>();
                    
                    for (String reward : rawRewards) 
                        processedRewards.add(Utils.translateColorCodes(reward));
                    
                    levelRewards.put(level, processedRewards);
                } catch (NumberFormatException e) {
                    plugin.getLogger().warning("Invalid level number in rewards.yml: " + levelStr);
                }
            }
        }
        
        plugin.getLogger().info("Loaded " + allRewards.size() + " general rewards and " + 
                levelRewards.size() + " level-specific reward sets");
    }
    
    public void executeRewards(Player player, int level, String levelName) {
        String playerName = player.getName();
        
        // Replace placeholders
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("%nick%", playerName);
        placeholders.put("%lvl_number%", String.valueOf(level));
        placeholders.put("%lvl_name%", levelName);
        
        // Execute general rewards
        executeCommandList(player, allRewards, placeholders);
        
        // Execute level-specific rewards
        if (levelRewards.containsKey(level)) {
            executeCommandList(player, levelRewards.get(level), placeholders);
        }
    }
    
    private void executeCommandList(Player player, List<String> commands, Map<String, String> placeholders) {
        for (String command : commands) {
        	 // Replace placeholders
            String finalCommand = command;
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                finalCommand = finalCommand.replace(entry.getKey(), entry.getValue());
            }
            
            // Execute command as console
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
        }
    }
    
    public void reload() {
        loadRewards();
    }
}