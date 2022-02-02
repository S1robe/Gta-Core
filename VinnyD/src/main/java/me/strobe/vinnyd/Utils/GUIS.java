package me.strobe.vinnyd.Utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public class GUIS {

    private GUIS(){}

    private static void openShop(Player p, VinnyUtils.VinnyD vinny){
        List<ItemStack> stock = vinny.getActiveStock().stream().map(StockItem::getRepresentation).collect(Collectors.toList());
        Inventory i;
        if()
    }
}
