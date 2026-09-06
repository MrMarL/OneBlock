package oneblock.gui;

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

import oneblock.ChestItems;
import oneblock.Messages;
import oneblock.PlayerInfo;

public class GUI {
    public static boolean enabled = true;
    public static boolean legacy = false;

    private static final int SIZE = 54;
    private static Inventory topGUI = null;

    public static void openGUI(Player p) {
        if (!enabled || p == null) return;
        Inventory inv = Bukkit.createInventory(new GUIHolder(GUIHolder.GUIType.MAIN_MENU), SIZE,
                ChatColor.DARK_GREEN + Messages.baseGUI);
        fillBorder(inv);

        ItemStack profile = getPlayerHead(Bukkit.getOfflinePlayer(p.getUniqueId()),
                ChatColor.GREEN + "Island Profile");
        ItemMeta profileMeta = profile.getItemMeta();
        if (profileMeta != null) {
            profileMeta.setLore(Arrays.asList(
                    ChatColor.GRAY + "Owner: " + ChatColor.WHITE + p.getName()
            ));
            profile.setItemMeta(profileMeta);
        }
        inv.setItem(13, profile);

        inv.setItem(20, item(XMaterial.GRASS_BLOCK,
                ChatColor.GREEN + "/ob join",
                ChatColor.GRAY + "Teleport to your OneBlock island."));
        inv.setItem(21, item(XMaterial.PODZOL,
                ChatColor.GREEN + "/ob leave",
                ChatColor.GRAY + "Leave your OneBlock island."));
        if (p.hasPermission("Oneblock.visit"))
            inv.setItem(22, item(XMaterial.MELON,
                ChatColor.GREEN + "/ob visit",
                ChatColor.GRAY + "Visit other players' islands."));
        if (p.hasPermission("Oneblock.allow_visit"))
            inv.setItem(23, item(XMaterial.EMERALD_BLOCK,
                ChatColor.GREEN + "/ob allow_visit",
                ChatColor.GRAY + "Toggle island visiting."));
        inv.setItem(24, item(XMaterial.NETHER_STAR,
                ChatColor.GREEN + "/ob top",
                ChatColor.GRAY + "View the island leaderboard.",
                "",
                ChatColor.YELLOW + "Click to open"));
        inv.setItem(30, item(XMaterial.PAPER,
                ChatColor.GREEN + "/ob help",
                ChatColor.GRAY + "View all OneBlock commands."));

        if (p.hasPermission("Oneblock.idreset")) {
            inv.setItem(32, item(XMaterial.BARRIER,
                    ChatColor.RED + "/ob idreset",
                    ChatColor.GRAY + "Reset your island ID.",
                    Messages.idresetGUI));
        }

        inv.setItem(49, item(XMaterial.BARRIER, ChatColor.RED + "Close", ChatColor.GRAY + "Close this menu."));
        p.openInventory(inv);
    }

    public static void acceptGUI(Player p, String name) {
        if (!enabled || p == null) return;
        Inventory inv = Bukkit.createInventory(new GUIHolder(GUIHolder.GUIType.INVITE), 9, Messages.acceptGUI);
        inv.setItem(6, item(XMaterial.REDSTONE_BLOCK, Messages.acceptGUIignore));
        inv.setItem(2, item(XMaterial.EMERALD_BLOCK, String.format(Messages.acceptGUIjoin, name), Messages.idresetGUI));
        p.openInventory(inv);
    }

    public static void topGUI(Player p) {
       if (!enabled || p == null) return;
        if (topGUI == null) {
            topGUI = Bukkit.createInventory(new GUIHolder(GUIHolder.GUIType.TOP), 27, Messages.topGUI);
            fillBorder(topGUI);
            topGUI.setItem(18, item(XMaterial.ARROW, ChatColor.YELLOW + "/ob gui", ChatColor.GRAY + "Return to the island menu."));
            topGUI.setItem(26, item(XMaterial.BARRIER, ChatColor.RED + "Close", ChatColor.GRAY + "Close this menu."));
        }
        
        List<PlayerInfo> toplist = oneblock.OneBlock.getTopList();
        
        topGUI.setItem(2, getTop(0, XMaterial.NETHERITE_BLOCK, ChatColor.GOLD + "1st", toplist));
        topGUI.setItem(4, getTop( 1, XMaterial.DIAMOND_BLOCK, ChatColor.GRAY + "2nd", toplist));
        topGUI.setItem(6, getTop( 2, XMaterial.EMERALD_BLOCK, ChatColor.GREEN + "3rd", toplist));
        topGUI.setItem(11, getTop( 3, XMaterial.GOLD_BLOCK, ChatColor.YELLOW + "4th", toplist));
        topGUI.setItem(13, getTop( 4, XMaterial.IRON_BLOCK, ChatColor.WHITE + "5th", toplist));
        topGUI.setItem(15, getTop( 5, XMaterial.REDSTONE_BLOCK, ChatColor.RED + "6th", toplist));
        topGUI.setItem(20, getTop( 6, XMaterial.LAPIS_BLOCK, ChatColor.BLUE + "7th", toplist));
        topGUI.setItem(22, getTop( 7, XMaterial.AMETHYST_BLOCK, ChatColor.LIGHT_PURPLE + "8th", toplist));
        topGUI.setItem(24, getTop( 8, XMaterial.COAL_BLOCK, ChatColor.DARK_GRAY + "9th", toplist));
        
        p.openInventory(topGUI);
    }

    public static void visitGUI(Player p, OfflinePlayer[] offlinePlayers) {
        if (!enabled || p == null) return;
        Inventory visitGUI = Bukkit.createInventory(new GUIHolder(GUIHolder.GUIType.VISIT), 54, Messages.visitGUI);
        ArrayList<OfflinePlayer> matchedPlayers = new ArrayList<>();
        for (OfflinePlayer pl: offlinePlayers) {
            PlayerInfo inf = PlayerInfo.get(pl.getUniqueId());
            if (inf == null) continue;
            if (!inf.allowVisit) continue;
            matchedPlayers.add(pl);
        }
        int size = Math.min(matchedPlayers.size(), 54);
        for (int i = 0; i < size; i++) {
            OfflinePlayer pl = matchedPlayers.get(i);
            visitGUI.setItem(i, getPlayerHead(pl, pl.getName() != null ? pl.getName() : "Unknown"));
        }
        p.openInventory(visitGUI);
    }

    public static ItemStack getPlayerHead(OfflinePlayer player, String title) {
        ItemStack skull = XMaterial.PLAYER_HEAD.parseItem();
        if (skull == null) skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        if (meta != null) {
            if (!legacy && player != null) meta.setOwningPlayer(player);
            meta.setDisplayName(title);
            skull.setItemMeta(meta);
        }
        return skull;
    }

    public static void chestGUI(Player p, String chestType) {
        if (p == null) return;
        List<ItemStack> list = ChestItems.getItems(chestType);
        if (list == null) return;
        
        Inventory inv = Bukkit.createInventory(new ChestHolder(chestType), 54,
                String.format("%sEdit: %s%s", ChatColor.BLACK, ChatColor.DARK_GRAY, chestType));
        
        for (ItemStack itm : list) {
            if (itm != null) inv.addItem(itm);
        }
        p.openInventory(inv);
    }

    private static ItemStack getTop(int index, XMaterial material, String prefix, List<PlayerInfo> toplist) {
        if (index >= toplist.size()) return null;
        PlayerInfo info = toplist.get(index);
        if (info == null) return null;
        
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Level: " + ChatColor.WHITE + info.lvl);
        lore.add(ChatColor.GRAY + "Members: " + ChatColor.WHITE + (info.uuids.size() + 1));
        if (info.uuids != null && !info.uuids.isEmpty()) {
            lore.add("");
            lore.add(ChatColor.GRAY + "Members:");
            for (UUID uuid : info.uuids) {
                lore.add(ChatColor.DARK_GRAY + "- " + nameOf(uuid));
            }
        }
        
        return item(material, prefix + " - " + nameOf(info.uuid), lore.toArray(new String[0]));
    }

    private static String nameOf(UUID uuid) {
        try {
            String name = Bukkit.getOfflinePlayer(uuid).getName();
            return name == null ? "Unknown" : name;
        } catch (Exception e) {
            return "Unknown";
        }
    }

    private static ItemStack item(XMaterial material, String title, String... lore) {
        Material m = material.get();
        ItemStack stack = new ItemStack(m == null ? Material.EMERALD_BLOCK : m);
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(title);
            if (lore.length > 0) meta.setLore(Arrays.asList(lore));
            stack.setItemMeta(meta);
        }
        return stack;
    }

    private static void fillBorder(Inventory inv) {
        int size = inv.getSize();
        int row = size / 9;

        ItemStack pane = item(XMaterial.GRAY_STAINED_GLASS_PANE, "");

        for (int i = 0; i < 9; i++) {
            inv.setItem(i, pane);
            inv.setItem(size - 1 - i, pane);
        }

        for (int i = 1; i < row - 1; i++) {
            inv.setItem(i * 9, pane);
            inv.setItem(i * 9 + 8, pane);
        }
    }
}