package Oneblock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.boss.BarColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class CommandTabCompleter implements TabCompleter {
	@Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> commands = new ArrayList<>();

        if (args.length == 1) {
        	commands.addAll(Arrays.asList("j","join","leave","invite","accept","kick","ver","IDreset","help","gui","top"));
        	if (sender.hasPermission("Oneblock.visit")) commands.addAll(Arrays.asList("v","visit","allow_visit"));
            if (sender.hasPermission("Oneblock.set")) {
            	commands.addAll(Arrays.asList("set","setleave","Progress_bar","chat_alert","setlevel","clear","circlemode","lvl_mult","max_players_team", "chest", "saveplayerinventory",
            		"reload","islands","island_rebirth","protection","worldguard","border","listlvl","autoJoin","droptossup","physics","particle","allow_nether","UseEmptyIslands"));
            }
        } else if (args.length == 2) {
        	if (args[0].equals("invite") || args[0].equals("kick") || args[0].equals("visit")) {
        		addOnlinePlayers(commands);
        	}
        	else if (sender.hasPermission("Oneblock.set")) {
        		switch (args[0])
                {
        		case ("chest"):
        			commands.addAll(ChestItems.getChestNames());
                case ("clear"):
                case ("setlevel"):{
                	addOnlinePlayers(commands);
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
                case ("islands"):
	                commands.add("set_my_by_def");
	                commands.add("default");
                case ("UseEmptyIslands"):
                case ("allow_nether"):
                case ("island_rebirth"):
                case ("saveplayerinventory"):
                case ("protection"):
                case ("circlemode"):
                case ("worldguard"):
                case ("border"):
                case ("autoJoin"):
                case ("droptossup"):
                case ("physics"):
                case ("gui"):
                case ("chat_alert"):
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
        			if (Oneblock.plugin.isPAPIEnabled())
        				commands.add("%OB_lvl_name%. There are %OB_need_to_lvl_up% block(s) left.");
        		}
        	}
        	else if (args[0].equals("setlevel"))
            	for (int i = 0; i < Level.size();)
            		commands.add(String.format("%d", i++));
        Collections.sort(commands);
        return commands;
    }
	
	// Auxiliary methods
    private void addOnlinePlayers(List<String> completions) {
    	for (Player ponl: Oneblock.plugin.cache.getPlayers())
    		completions.add(ponl.getName());
    }
}
