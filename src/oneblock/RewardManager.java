package oneblock;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import oneblock.utils.Utils;

import static oneblock.Oneblock.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class RewardManager {
    // Reject anything that is not a Mojang-legal player name; blocks command-injection
    // attempts via offline-mode / proxy-forwarded nicknames being substituted into
    // reward templates that are dispatched as console.
    private static final Pattern SAFE_PLAYER_NAME = Pattern.compile("^[A-Za-z0-9_]{1,16}$");

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
        if (playerName == null || !SAFE_PLAYER_NAME.matcher(playerName).matches()) {
            plugin.getLogger().warning("Skipping reward dispatch for player with unsafe name: '" + playerName + "'. Expected " + SAFE_PLAYER_NAME.pattern() + ".");
            return;
        }
        
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