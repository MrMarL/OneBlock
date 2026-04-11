package Oneblock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class CommandTabCompleter implements TabCompleter {
	private final List<String> BASE_COMMANDS = Arrays.asList("j","join","leave","invite","accept","kick","help","gui","top");
	private final List<String> VISIT_COMMANDS = Arrays.asList("v","visit");
	private final List<String> ADMIN_COMMANDS = Arrays.asList("set","setleave","progress_bar","setlevel","clear","circlemode","lvl_mult","max_players_team", "chest", "saveplayerinventory",
            "reload","islands","rebirth_on_the_island","protection","worldguard","border","listlvl","autoJoin","droptossup","physics","particle","allow_nether","useEmptyIslands");
	
	@Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> commands = new ArrayList<>();
        
        boolean isAdmin = sender.hasPermission("Oneblock.set");

        if (args.length == 1) {
        	commands.addAll(BASE_COMMANDS);
        	if (sender.hasPermission("Oneblock.idreset")) commands.add("IDreset");
        	if (sender.hasPermission("Oneblock.visit")) commands.addAll(VISIT_COMMANDS);
        	if (sender.hasPermission("Oneblock.allow_visit")) commands.add("allow_visit");
        	if (isAdmin) commands.addAll(ADMIN_COMMANDS);
        }
        else if (args.length == 2) {
        	String arg = args[0].toLowerCase();
        	
        	if ("invite".equals(arg) || "kick".equals(arg) || "visit".equals(arg) || "v".equals(arg)) {
        		addOnlinePlayers(commands);
        	}
        	else if (isAdmin) {
        		switch (arg) {
	        		case ("chest"):
	        			commands.addAll(ChestItems.getChestNames());
	        			break;
	                case ("clear"):
	                case ("idreset"):
	                case ("setlevel"):
	                	addOnlinePlayers(commands);
	            		break;
	                case ("progress_bar"):
		                commands.add("true");
		                commands.add("false");
		                commands.add("level");
		                commands.add("settext");
		                commands.add("color");
		                commands.add("style");
		                break;
	                case ("islands"):
		                commands.add("set_my_by_def");
		                commands.add("default");
	                case ("useemptyislands"):
	                case ("allow_nether"):
	                case ("rebirth_on_the_island"):
	                case ("saveplayerinventory"):
	                case ("protection"):
	                case ("circlemode"):
	                case ("worldguard"):
	                case ("border"):
	                case ("autojoin"):
	                case ("droptossup"):
	                case ("physics"):
	                case ("gui"):
		                commands.add("true");
		                commands.add("false");
		                break;
	                case ("listlvl"):
		            	for (int i = 0; i < Level.size(); i++)
		            		commands.add(String.valueOf(i));
		            	break;
	                case ("lvl_mult"):
	                case ("max_players_team"):
	                	for (int i = 0; i < 4; i++)
	                		commands.add(String.valueOf(i));
	                case ("set"):
	                	commands.add("100");
	                	commands.add("500");
	                	commands.add("100 0 64 0");
	                	commands.add("500 0 64 0");
                }
        	}
        }
        else if (isAdmin && args.length == 3) {
        	String arg0 = args[0].toLowerCase();
        	String arg1 = args[1].toLowerCase();
        	
        	if ("progress_bar".equals(arg0)) {
        		if ("color".equals(arg1))
        			for (BarColor bc:BarColor.values())
        				commands.add(bc.name());
        		if ("style".equals(arg1))
        			for (BarStyle bc:BarStyle.values())
        				commands.add(bc.name());
        		if ("settext".equals(arg1)) {
        			commands.add("...");
        			commands.add("%OB_lvl% &8- %OB_lvl_name% &8| &fProgress: &e%OB_break_on_this_lvl%/%OB_lvl_lenght%");
        			commands.add("%OB_lvl_name% &8| &fProgress: &e%OB_break_on_this_lvl%/%OB_lvl_lenght% &8(&b%OB_need_to_lvl_up% left&8)");
        		}
        	}
        	else if ("setlevel".equals(arg0))
        		for (int i = 0; i < Level.size(); i++)
        			commands.add(String.valueOf(i));
        }
        Collections.sort(commands);
        return commands;
    }
	
	// Auxiliary methods
    private void addOnlinePlayers(List<String> completions) {
    	for (Player ponl: Oneblock.plugin.cache.getPlayers())
    		completions.add(ponl.getName());
    }
}
