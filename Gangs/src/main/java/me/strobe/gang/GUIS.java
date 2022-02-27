package me.strobe.gang;

import me.Strobe.Core.Utils.GenUtils;
import me.Strobe.Core.Utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

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

        Inventory inv = Bukkit.createInventory(null, 1, g.getName());
        GenUtils.fill(inv, ItemUtils.blankFill());

        //Upgrades if perm is set
        //Permisisons if perm is set
        //Homes by default
        if(m.isPermissionSet(Gang.Permission.PERMISSION))
            inv.setItem(1, ItemUtils.createItem(Material.REDSTONE, "&cUser Permissions"));
        if(m.isPermissionSet(Gang.Permission.UPGRADE))
            inv.setItem(3, ItemUtils.createItem(Material.EMERALD, "&aUpgrades"));
        inv.setItem(5, ItemUtils.createItem(Material.WOODEN_DOOR, "&eHomes"));
        inv.setItem(7, ItemUtils.createItem(Material.SKULL, 1, (byte) 0, "&9Members:", ));




    }


    public static void openUpgradesGUI(Member m){
        Player p = m.getP().getPlayer();
        Gang g = m.getGang();

        Inventory inv = Bukkit.createInventory(null, 1, g.getName());
        GenUtils.fill(inv, ItemUtils.blankFill());
    }

    public static void openPermissionsGUI(Member m){
        Player p = m.getP().getPlayer();
        Gang g = m.getGang();

        Inventory inv = Bukkit.createInventory(null, 1, g.getName());
        GenUtils.fill(inv, ItemUtils.blankFill());
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

        Inventory inv = Bukkit.createInventory(null, 1, g.getName());
        GenUtils.fill(inv, ItemUtils.blankFill());
    }

    public static void openAlliesGUI(Member m){

    }

}
