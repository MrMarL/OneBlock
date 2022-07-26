package Oneblock.gui;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import Oneblock.PlayerInfo;
import main.java.xseriesoneblock.XMaterial;

public class GUI {
	public static boolean enabled = true;
	
	static Inventory baseGUI = null;
	static Inventory acceptGUI = null;
	static Inventory topGUI = null;
	
	public static void openGUI(Player p) {
		if (!enabled) return;
		if (baseGUI == null) {
			baseGUI = Bukkit.createInventory(null, 9, String.format("%s[OneBlock GUI] %s- main", ChatColor.GREEN, ChatColor.WHITE ));
	        baseGUI.addItem(setMeta(XMaterial.GRASS_BLOCK, ChatColor.GREEN + "/ob join"));
	        baseGUI.setItem(2, setMeta(XMaterial.PODZOL, ChatColor.GREEN + "/ob leave"));
	        baseGUI.setItem(4, setMeta(XMaterial.GOLD_BLOCK, ChatColor.GOLD + "/ob top"));
	        baseGUI.setItem(8, setMeta(XMaterial.BARRIER, ChatColor.RED + "/ob idreset"));
        }
        p.openInventory(baseGUI);
	}
	
	public static void acceptGUI(Player p, String name) {
		if (!enabled) return;
		if (acceptGUI == null) {
			acceptGUI = Bukkit.createInventory(null, 9, String.format("%sYou are invited to the island.", ChatColor.WHITE));
			acceptGUI.setItem(6, setMeta(XMaterial.REDSTONE_BLOCK, ChatColor.RED + "Ignore"));
		}
		acceptGUI.setItem(2, setMeta(XMaterial.EMERALD_BLOCK, String.format("%sJoin %s%s%s's Island", ChatColor.GREEN, ChatColor.DARK_GREEN, name, ChatColor.GREEN), String.format("%s[your island's data will be lost]", ChatColor.RED)));
        p.openInventory(acceptGUI);
	}
	
	public static void topGUI(Player p) {
		if (!enabled) return;
		if (topGUI == null)
			topGUI = Bukkit.createInventory(null, 27, String.format("%s[OneBlock GUI] %s- %sTop", ChatColor.GREEN, ChatColor.WHITE, ChatColor.BOLD));
		PlayerInfo inf = Oneblock.Oneblock.gettop(0);
		topGUI.setItem(4, setMeta(XMaterial.NETHERITE_BLOCK, String.format("%s1st - %s", ChatColor.GOLD, inf.nick), inf.lvl));
		inf = Oneblock.Oneblock.gettop(1);
		topGUI.setItem(12, setMeta(XMaterial.DIAMOND_BLOCK, String.format("%s2nd - %s", ChatColor.GRAY, inf.nick), inf.lvl));
		inf = Oneblock.Oneblock.gettop(2);
		topGUI.setItem(14, setMeta(XMaterial.IRON_BLOCK, String.format("%s3rd - %s", ChatColor.GRAY, inf.nick), inf.lvl));
		inf = Oneblock.Oneblock.gettop(3);
		topGUI.setItem(20, setMeta(XMaterial.GOLD_BLOCK, String.format("%s4th - %s", ChatColor.DARK_RED, inf.nick), inf.lvl));
		inf = Oneblock.Oneblock.gettop(4);
		topGUI.setItem(22, setMeta(XMaterial.COPPER_BLOCK, String.format("%s5th - %s", ChatColor.DARK_RED, inf.nick), inf.lvl));
		inf = Oneblock.Oneblock.gettop(5);
		topGUI.setItem(24, setMeta(XMaterial.COAL_BLOCK, String.format("%s6th - %s", ChatColor.DARK_RED, inf.nick), inf.lvl));
        p.openInventory(topGUI);
	}
	
	private static ItemStack setMeta(XMaterial material, String title) {
		return setMeta(material, title, 1, null);
	}
	
	private static ItemStack setMeta(XMaterial material, String title, int amount) {
		return setMeta(material, title, amount, null);
	}
	
	private static ItemStack setMeta(XMaterial material, String title, String Lore) {
		return setMeta(material, title, 1, Lore);
	}
	
	private static ItemStack setMeta(XMaterial material, String title, int amount, String Lore) {
		Material m = material.parseMaterial();
		ItemStack join = new ItemStack(m == null ? Material.EMERALD_BLOCK : m, amount);
        ItemMeta meta = join.getItemMeta();
        meta.setDisplayName(title);
        if (Lore != null) {
	        ArrayList<String> lore = new ArrayList<String>();
	        lore.add(Lore);
	        meta.setLore(lore);
        }
        join.setItemMeta(meta);
		return join;
	}
}