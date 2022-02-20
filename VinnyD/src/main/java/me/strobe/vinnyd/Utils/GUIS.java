package me.strobe.vinnyd.Utils;

import me.Strobe.Core.Utils.GenUtils;
import me.Strobe.Core.Utils.ItemUtils;
import me.strobe.vinnyd.Events.VinnyEvents;
import me.strobe.vinnyd.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public class GUIS {

    private GUIS(){}

    public static void openShop(Player p, VinnyUtils.VinnyD vinny){
        List<ItemStack> stock = vinny.getActiveStock().stream().map(StockItem::getRepresentation).collect(Collectors.toList());
        Inventory i;

        if(stock.size() <= 9)
            i = Bukkit.createInventory(null, 9, "Vinny Stock");
        if(stock.size() <= 18)
            i = Bukkit.createInventory(null, 18, "Vinny Stock");
        if(stock.size() <= 27)
            i = Bukkit.createInventory(null, 27, "Vinny Stock");
        if(stock.size() <= 36)
            i = Bukkit.createInventory(null, 36, "Vinny Stock");
        if(stock.size() <= 45)
            i = Bukkit.createInventory(null, 45, "Vinny Stock");
        if(stock.size() <= 54)
            i = Bukkit.createInventory(null, 54, "Vinny Stock");
        else
            i = Bukkit.createInventory(null, 63, "Vinny Stock");

        GenUtils.fill(i, ItemUtils.blankFill());
        GenUtils.setMiddle(i, ItemUtils.blankFill(), stock);
        p.closeInventory();
        p.openInventory(i);
    }

    public static void openUpgrades(Player p, VinnyUtils.VinnyD vinny){
        List<ItemStack> stock = vinny.getActiveStock().stream().map(StockItem::getRepresentation).collect(Collectors.toList());
        Inventory i;

        if(stock.size() <= 9)
            i = Bukkit.createInventory(null, 9, "Vinny's Upgrade Supply");
        if(stock.size() <= 18)
            i = Bukkit.createInventory(null, 18, "Vinny's Upgrade Supply");
        if(stock.size() <= 27)
            i = Bukkit.createInventory(null, 27, "Vinny's Upgrade Supply");
        if(stock.size() <= 36)
            i = Bukkit.createInventory(null, 36, "Vinny's Upgrade Supply");
        if(stock.size() <= 45)
            i = Bukkit.createInventory(null, 45, "Vinny's Upgrade Supply");
        if(stock.size() <= 54)
            i = Bukkit.createInventory(null, 54, "Vinny's Upgrade Supply");
        else
            i = Bukkit.createInventory(null, 63, "Vinny's Upgrade Supply");

        GenUtils.fill(i, ItemUtils.blankFill());
        GenUtils.setMiddle(i, ItemUtils.blankFill(), stock);
        p.closeInventory();
        p.openInventory(i);
    }

    public static void openVinnyMenu(Player p){
        Inventory i = Bukkit.createInventory(null, 9, "Vinny's Shop");
        GenUtils.fill(i, ItemUtils.blankFill());
        if(VinnyUtils.isCanUpgrade()){
            i.setItem(1, ItemUtils.createItem(Material.EMERALD, 1, (byte) 0 , true, "&aVinny's Shop"));
            i.setItem(4, ItemUtils.createItem(Material.WATCH, "Vinny's Time Left:"));
            i.setItem(8, ItemUtils.createItem(Material.SLIME_BALL, 1, (byte) 0, true, "&eVinny's Upgrades") );
            VinnyEvents.playersLookingAtVinny.put(p, new VinnyUtils.VinnyRunnable(i, 4).runTaskTimer(Main.getMain(), 20L, 120L).getTaskId());
        }
        else{
            i.setItem(3, ItemUtils.createItem(Material.EMERALD, 1, (byte) 0 , true, "&aVinny's Shop"));
            i.setItem(6, ItemUtils.createItem(Material.WATCH, "Vinny's Time Left:"));
            VinnyEvents.playersLookingAtVinny.put(p, new VinnyUtils.VinnyRunnable(i, 6).runTaskTimer(Main.getMain(), 20L, 120L).getTaskId());
        }
    }
}
