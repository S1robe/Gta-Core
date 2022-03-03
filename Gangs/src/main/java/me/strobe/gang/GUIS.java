package me.strobe.gang;

import me.Strobe.Core.Utils.GenUtils;
import me.Strobe.Core.Utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This is GUIS in Project: (Gta-Core) : But you already knew that
 *
 * @author G.P of Prentice Productions
 * @version 1.0
 * Created On    : 2/26/2022, 11:04 AM
 * Last Edit     : 2/26/2022, 11:04 AM (Update Me!)
 * Time to Write : (Rough Estimate)
 * <p>
 * (Class Description)
 */
public class GUIS {
    private GUIS(){}

    //TOOD GUIS here, needs home GUI, upgrade GUI, base GUI, permission gui (members)

    public static void openMainGUI(Member m){
        Player p = m.getP().getPlayer();
        Gang g = m.getGang();

        Inventory inv = Bukkit.createInventory(null, 9, g.getName());
        GenUtils.fill(inv, ItemUtils.blankFill());

        //Upgrades if perm is set
        //Permisisons if perm is set
        //Homes by default
        if(m.isPermissionSet(Gang.Permission.PERMISSION))
            inv.setItem(1, ItemUtils.createItem(Material.REDSTONE, "&cUser Permissions"));

        if(m.isPermissionSet(Gang.Permission.UPGRADE))
            inv.setItem(3, ItemUtils.createItem(Material.EMERALD, "&aUpgrades"));

        inv.setItem(5, ItemUtils.createItem(Material.WOODEN_DOOR, "&eHomes"));
        inv.setItem(7, ItemUtils.createItem(Material.SKULL, 1, (byte) 0, "&9Members:",
                                            (String[]) g.getMembers().values().stream().map(Member::toString).toArray()));

        p.closeInventory();
        p.openInventory(inv);
    }

    public static void openUpgradesGUI(Member m){
        Player p = m.getP().getPlayer();
        Gang g = m.getGang();

        Inventory inv = Bukkit.createInventory(null, 9, g.getName());
        GenUtils.fill(inv, ItemUtils.blankFill());

        //TOOD: Put the upgrades yes

        p.closeInventory();
        p.openInventory(inv);
    }

    public static void openPermissionsGUI(Member m){
        Player p = m.getP().getPlayer();
        Gang g = m.getGang();

        Inventory inv;
        int size = g.getMembers().size();
        if(size <= 9)
            inv = Bukkit.createInventory(null, 9, "Gang Permissions");
        if(size <= 18)
            inv = Bukkit.createInventory(null, 18, "Gang Permissions");
        if(size <= 27)
            inv = Bukkit.createInventory(null, 27, "Gang Permissions");
        if(size <= 36)
            inv = Bukkit.createInventory(null, 36, "Gang Permissions");
        if(size <= 45)
            inv = Bukkit.createInventory(null, 45, "Gang Permissions");
        if(size <= 54)
            inv = Bukkit.createInventory(null, 54, "Gang Permissions");
        else
            inv = Bukkit.createInventory(null, 63, "Gang Permissions");
        GenUtils.fill(inv, ItemUtils.blankFill());

        ArrayList<Member> members = new ArrayList<>(g.getMembers().values());
        for(int i = 0; i < members.size(); i++) {
            Member x = members.get(i);
            if(x == m) continue;
            ItemStack skull = ItemUtils.getSkullOf(x.getUuid());
            ItemUtils.setDisplayName(skull, p.isOnline()? "&a"+x.getP().getName() : "&7"+x.getP().getName());
            ItemUtils.applyLore(skull, x.stringify());
            inv.setItem(i, skull);
        }
        p.closeInventory();
        p.openInventory(inv);
    }

    public static void openMemberPermissionsGUI(Member m, Member other){
        Player p = m.getP().getPlayer();
        Gang g = m.getGang();

        Inventory inv = Bukkit.createInventory(null, 18, g.getName());
        GenUtils.fill(inv, ItemUtils.blankFill());

        // Psuedo Rank Permissions
        inv.setItem(0,  ItemUtils.createItem(Material.REDSTONE_LAMP_ON, m.isPermissionSet(Gang.Permission.MASTERMIND)? "&aMastermind" : "&cMastermind"));
        inv.setItem(1,  ItemUtils.createItem(Material.NETHER_STAR, m.isPermissionSet(Gang.Permission.EXALTED)        ? "&aExalted" : "&cExalted"));
        inv.setItem(9,  ItemUtils.createItem(Material.DIAMOND, m.isPermissionSet(Gang.Permission.HONORED)            ? "&aHonored" : "&cHonored"));
        inv.setItem(10, ItemUtils.createItem(Material.BARRIER , m.isPermissionSet(Gang.Permission.LOCKOUT)           ? "&aLocked Out" : "&cLocked Out"));

        //Money perms
        inv.setItem(3,  ItemUtils.createItem(Material.DISPENSER, m.isPermissionSet(Gang.Permission.WITHDRAW)         ? "&aWithdraw" : "&cWithdraw"));
        inv.setItem(12, ItemUtils.createItem(Material.EMERALD, m.isPermissionSet(Gang.Permission.UPGRADE)            ? "&aUpgrade" : "&cUpgrade"));

        //Internal Affairs
        inv.setItem(5,  ItemUtils.createItem(Material.RABBIT_FOOT, m.isPermissionSet(Gang.Permission.KICK)           ? "&aKick" : "&cKick"));
        inv.setItem(6,  ItemUtils.createItem(Material.HOPPER, m.isPermissionSet(Gang.Permission.INVITE)              ? "&aInvite" : "&cInvite"));
        inv.setItem(14, ItemUtils.createItem(Material.COMMAND, m.isPermissionSet(Gang.Permission.MANAGEMENT)         ? "&aManagement" : "&cManagement"));
        inv.setItem(15, ItemUtils.createItem(Material.COMMAND, m.isPermissionSet(Gang.Permission.PERMISSION)         ? "&aPermissions" : "&cPermissions"));

        //External AFfairs
        inv.setItem(7,  ItemUtils.createItem(Material.GOLD_SWORD, m.isPermissionSet(Gang.Permission.FF)              ? "&aFriendly Fire" : "&cFriendly Fire"));
        inv.setItem(8,  ItemUtils.createItem(Material.DIAMOND_AXE, m.isPermissionSet(Gang.Permission.ALLY)           ? "&aAlly/Enemy" : "&cAlly/Enemy"));
        inv.setItem(16, ItemUtils.createItem(Material.DARK_OAK_DOOR, m.isPermissionSet(Gang.Permission.SETHOME)      ? "&aSet Homes" : "&cSet Homes"));
        inv.setItem(17, ItemUtils.createItem(Material.WOODEN_DOOR, m.isPermissionSet(Gang.Permission.DELHOME)        ? "&aDel Homes" : "&cDel Homes"));

        p.closeInventory();
        p.openInventory(inv);
    }

    public static void openHomesGUI(Member m){
        Player p = m.getP().getPlayer();
        Gang g = m.getGang();

        Inventory inv;
        int size = g.getMembers().size();
        if(size <= 9)
            inv = Bukkit.createInventory(null, 9, "Gang Homes");
        if(size <= 18)
            inv = Bukkit.createInventory(null, 18, "Gang Homes");
        if(size <= 27)
            inv = Bukkit.createInventory(null, 27, "Gang Homes");
        if(size <= 36)
            inv = Bukkit.createInventory(null, 36, "Gang Homes");
        if(size <= 45)
            inv = Bukkit.createInventory(null, 45, "Gang Homes");
        if(size <= 54)
            inv = Bukkit.createInventory(null, 54, "Gang Homes");
        else
            inv = Bukkit.createInventory(null, 63, "Gang Homes");
        GenUtils.fill(inv, ItemUtils.blankFill());

        List<ItemStack> items = new ArrayList<>();
        g.getGangHomes().forEach((name, loc) -> {
            items.add(ItemUtils.createItem(Material.HAY_BLOCK, 1,  (byte) 0, name,
                                           "&7X: &e" + loc.getBlockX(),
                                                     "&7Y: &e" + loc.getBlockY(),
                                                     "&7Z: &e" + loc.getBlockZ()));
        });

        for(int i = 0; i < items.size(); i++)
            inv.setItem(i, items.get(i));

        p.closeInventory();
        p.openInventory(inv);
    }

    public static void openAlliesGUI(Member m){
        Player p = m.getP().getPlayer();
        Gang g = m.getGang();

        Inventory inv;
        int size = g.getMembers().size();
        if(size <= 9)
            inv = Bukkit.createInventory(null, 9, "Gang Homes");
        if(size <= 18)
            inv = Bukkit.createInventory(null, 18, "Gang Homes");
        if(size <= 27)
            inv = Bukkit.createInventory(null, 27, "Gang Homes");
        if(size <= 36)
            inv = Bukkit.createInventory(null, 36, "Gang Homes");
        if(size <= 45)
            inv = Bukkit.createInventory(null, 45, "Gang Homes");
        if(size <= 54)
            inv = Bukkit.createInventory(null, 54, "Gang Homes");
        else
            inv = Bukkit.createInventory(null, 63, "Gang Homes");
        GenUtils.fill(inv, ItemUtils.blankFill());

        List<ItemStack> x = g.getAllies().values().stream().map(Gang::item).collect(Collectors.toList());
        for(int i = 0; i < x.size(); i++)
            inv.setItem(i, x.get(i));
    }

}
