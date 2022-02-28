package me.strobe.gang;

import me.Strobe.Core.Utils.GenUtils;
import me.Strobe.Core.Utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

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
    }


    public static void openUpgradesGUI(Member m){
        Player p = m.getP().getPlayer();
        Gang g = m.getGang();

        Inventory inv = Bukkit.createInventory(null, 9, g.getName());
        GenUtils.fill(inv, ItemUtils.blankFill());


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
    }

    public static void openMemberPermissionsGUI(Member m, Member other){
        Player p = m.getP().getPlayer();
        Gang g = m.getGang();

        Inventory inv = Bukkit.createInventory(null, 1, g.getName());
        GenUtils.fill(inv, ItemUtils.blankFill());
    }

    public static void openHomesGUI(Member m){
        Player p = m.getP().getPlayer();
        Gang g = m.getGang();

        Inventory inv = Bukkit.createInventory(null, 9, g.getName());
        GenUtils.fill(inv, ItemUtils.blankFill());
    }

    public static void openAlliesGUI(Member m){
        Player p = m.getP().getPlayer();
        Gang g = m.getGang();

        Inventory inv = Bukkit.createInventory(null, 9, g.getName());
        GenUtils.fill(inv, ItemUtils.blankFill());
    }

}
