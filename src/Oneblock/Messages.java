package Oneblock;

import org.bukkit.ChatColor;

public class Messages {
	public static final String bool_format = String.format("%sEnter a valid value true or false", ChatColor.YELLOW);
	public static final String invalid_value = String.format("%sinvalid value", ChatColor.RED);
	
	public static String noperm = String.format("%sYou don't have permission [Oneblock.set].", ChatColor.RED);
	public static String noperm_inv = String.format("%sYou don't have the permission to execute this command", ChatColor.RED);
	public static String help = "none";
	public static String help_adm = "none";
	public static String invite_usage = String.format("%sUsage: /ob invite <username>", ChatColor.RED);
	public static String invite_yourself = String.format("%sYou can't invite yourself.", ChatColor.YELLOW);
	public static String invite_no_island = String.format("%sPlease create a island before you do this.", ChatColor.YELLOW);
	public static String invite_team = String.format("%sMaximum number of seats on the island: %s", ChatColor.YELLOW, "%d");
	public static String invited = String.format("%sYou were invited by player %s.\n%s/ob accept to accept).", 
			ChatColor.GREEN, "%s", ChatColor.RED);
	public static String invited_succes = String.format("%sSuccesfully invited %s.", ChatColor.GREEN, "%s");
	public static String kicked = String.format("%s has been kicked off your island!", ChatColor.YELLOW);
	public static String kick_usage = String.format("%sUsage: /ob kick <username>", ChatColor.RED);
	public static String kick_yourself = String.format("%sYou can't kick yourself.", ChatColor.YELLOW);
	public static String accept_succes = String.format("%sSuccesfully accepted the invitation.", ChatColor.GREEN);
	public static String accept_none = String.format("%s[There is no Pending invitations for you.]", ChatColor.RED);
	public static String idreset = String.format("%sNow your data has been reset. You can create a new island /ob join.", ChatColor.GREEN);
	public static String protection = String.format("%sare you trying to go %soutside the island?", ChatColor.YELLOW, ChatColor.RED);
	public static String leave_not_set = String.format("%sSorry, but the position was not set.", ChatColor.YELLOW);
	public static String not_allow_visit = String.format("%sThe player did not allow visits.", ChatColor.YELLOW);
	
	//GUI
	public static String baseGUI = "";
	public static String acceptGUI = "", acceptGUIignore = "", acceptGUIjoin = "%s";
	public static String topGUI = "";
	public static String visitGUI = "";
	public static String idresetGUI = "";
}