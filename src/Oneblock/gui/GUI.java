package Oneblock.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.cryptomorin.xseries.XMaterial;

import Oneblock.ChestItems;
import Oneblock.Messages;
import Oneblock.PlayerInfo;
import Oneblock.WorldGuard.OBWorldGuard;

public class GUI {
	public static boolean enabled = true;
	public static boolean legacy = false;
	
	static Inventory topGUI = null;
	
	public static void openGUI(Player p) {
		if (!enabled) return;
		if (p == null) return;
		Inventory mainGUI = Bukkit.createInventory(new GUIHolder(GUIHolder.GUIType.MAIN_MENU), 18, Messages.baseGUI);
		mainGUI.addItem(setMeta(XMaterial.GRASS_BLOCK, ChatColor.GREEN + "/ob join"));
		mainGUI.setItem(9, setMeta(XMaterial.PODZOL, ChatColor.GREEN + "/ob leave"));
		if (p.hasPermission("Oneblock.visit")) mainGUI.setItem(2, setMeta(XMaterial.MELON, ChatColor.GREEN + "/ob visit"));
		if (p.hasPermission("Oneblock.allow_visit")) mainGUI.setItem(10, setMeta(XMaterial.EMERALD_BLOCK, ChatColor.GREEN + "/ob allow_visit"));
		mainGUI.setItem(4, setMeta(XMaterial.GOLD_BLOCK, ChatColor.GOLD + "/ob top"));
		mainGUI.setItem(6, setMeta(XMaterial.PAPER, ChatColor.GRAY + "/ob help"));
		if (p.hasPermission("Oneblock.idreset"))
			mainGUI.setItem(8, setMeta(XMaterial.BARRIER, ChatColor.RED + "/ob idreset", Messages.idresetGUI));

		p.openInventory(mainGUI);
	}
	
	public static void acceptGUI(Player p, String name) {
		if (!enabled) return;
		if (p == null) return;
		Inventory acceptGUI = Bukkit.createInventory(new GUIHolder(GUIHolder.GUIType.INVITE), 9, Messages.acceptGUI);
		acceptGUI.setItem(6, setMeta(XMaterial.REDSTONE_BLOCK, Messages.acceptGUIignore));
		acceptGUI.setItem(2, setMeta(XMaterial.EMERALD_BLOCK, String.format(Messages.acceptGUIjoin, name), Messages.idresetGUI));
        p.openInventory(acceptGUI);
	}
	
	public static void topGUI(Player p) {
		if (!enabled) return;
		if (p == null) return;
		if (topGUI == null)
			topGUI = Bukkit.createInventory(new GUIHolder(GUIHolder.GUIType.TOP), 27, Messages.topGUI);
		PlayerInfo inf = Oneblock.Oneblock.gettop(0);
		topGUI.setItem(4, setMeta(XMaterial.NETHERITE_BLOCK, ChatColor.GOLD + "1st - " + parseUUID(inf.uuid), inf.lvl, parseUUIDs(inf.uuids)));
		inf = Oneblock.Oneblock.gettop(1);
		topGUI.setItem(12, setMeta(XMaterial.DIAMOND_BLOCK, ChatColor.GRAY + "2nd - " + parseUUID(inf.uuid), inf.lvl, parseUUIDs(inf.uuids)));
		inf = Oneblock.Oneblock.gettop(2);
		topGUI.setItem(14, setMeta(XMaterial.IRON_BLOCK, ChatColor.GRAY + "3rd - " + parseUUID(inf.uuid), inf.lvl, parseUUIDs(inf.uuids)));
		inf = Oneblock.Oneblock.gettop(3);
		topGUI.setItem(20, setMeta(XMaterial.GOLD_BLOCK, ChatColor.DARK_RED + "4th - " + parseUUID(inf.uuid), inf.lvl, parseUUIDs(inf.uuids)));
		inf = Oneblock.Oneblock.gettop(4);
		topGUI.setItem(22, setMeta(XMaterial.COPPER_BLOCK, ChatColor.DARK_RED + "5th - " + parseUUID(inf.uuid), inf.lvl, parseUUIDs(inf.uuids)));
		inf = Oneblock.Oneblock.gettop(5);
		topGUI.setItem(24, setMeta(XMaterial.COAL_BLOCK, ChatColor.DARK_RED + "6th - " + parseUUID(inf.uuid), inf.lvl, parseUUIDs(inf.uuids)));
        p.openInventory(topGUI);
	}
	
	public static void visitGUI(Player p, OfflinePlayer[] offlinePlayers) {
		if (!enabled) return;
		if (p == null) return;
		Inventory visitGUI = Bukkit.createInventory(new GUIHolder(GUIHolder.GUIType.VISIT), 54, Messages.visitGUI);
		ArrayList <PlayerInfo> list = new ArrayList<>();
		int size = 0;
		for (OfflinePlayer pl: offlinePlayers) {
			PlayerInfo inf = PlayerInfo.get(pl.getUniqueId());
			if (inf == null) continue;
			if (!inf.allow_visit) continue;
			list.add(inf);
			size++;
		}
		size = size > 54 ? 54 : size;
		for (int i = 0; i < size; i++)
			visitGUI.setItem(i, getPlayerHead(offlinePlayers[i], parseUUID(list.get(i).uuid)));
        p.openInventory(visitGUI);
	}
	
	public static ItemStack getPlayerHead(OfflinePlayer player, String title) {
        ItemStack skull = XMaterial.PLAYER_HEAD.parseItem();
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        if (!legacy) skullMeta.setOwningPlayer(player);
        skullMeta.setDisplayName(title);
        skull.setItemMeta(skullMeta);
        return skull;
    }

	public static void chestGUI(Player p, String chestType) {
		if (p == null) return;
		List<ItemStack> list = ChestItems.getChest(chestType);
		
		Inventory chestGUI = Bukkit.createInventory(new ChestHolder(chestType), 54, String.format("%sEdit: %s%s %s", ChatColor.BLACK, ChatColor.DARK_GRAY, chestType
				, OBWorldGuard.canUse?"":"[Edit only in premium]"));
		for(ItemStack itm : list)
			if (itm != null)
				chestGUI.addItem(itm);
		p.openInventory(chestGUI);
	}
	
	private static String parseUUID(UUID uuid) {
		try { return Bukkit.getOfflinePlayer(uuid).getName();
		} catch (Exception e) {return "Unknown";}
	}
	
	private static String[] parseUUIDs(List<UUID> uuids) {
		String[] Lore = new String[uuids.size()];
		for (int i = 0; i < uuids.size(); i++)
			Lore[i] = parseUUID(uuids.get(i));
		return Lore;
	}
	
	private static ItemStack setMeta(XMaterial material, String title) {
		return setMeta(material, title, 1);
	}
	
	private static ItemStack setMeta(XMaterial material, String title, String ...Lore) {
		return setMeta(material, title, 1, Lore);
	}
	
	private static ItemStack setMeta(XMaterial material, String title, int amount, String ...Lore) {
		if (amount <= 0) amount = 1;
		Material m = material.get();
		ItemStack join = new ItemStack(m == null ? Material.EMERALD_BLOCK : m, amount);
        ItemMeta meta = join.getItemMeta();
        meta.setDisplayName(title);
	    meta.setLore(Arrays.asList(Lore));
        join.setItemMeta(meta);
		return join;
	}
}