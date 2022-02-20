package me.strobe.vinnyd.Utils;

import me.Strobe.Core.Utils.GenUtils;
import me.Strobe.Core.Utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public class GUI {
   private GUI(){}

   public static void shopGUI(VinnyUtils.VinnyD vinny, Player p){
      List<ItemStack> stock = vinny.getActiveStock().stream().map(StockItem::getRepresentation).collect(Collectors.toList());
      int size = stock.size();
      Inventory inv;
      if(size <= 9)
         inv = Bukkit.createInventory(null, 9, "Vinny's Supply");
      else if(size <= 18)
         inv = Bukkit.createInventory(null, 18, "Vinny's Supply");
      else if(size <= 27)
         inv = Bukkit.createInventory(null, 27, "Vinny's Supply");
      else if(size <= 36)
         inv = Bukkit.createInventory(null, 36, "Vinny's Supply");
      else if(size <= 45)
         inv = Bukkit.createInventory(null, 45, "Vinny's Supply");
      else if(size <= 54)
         inv = Bukkit.createInventory(null, 54, "Vinny's Supply");
      else
         inv = Bukkit.createInventory(null, 63, "Vinny's Supply");
      GenUtils.setMiddle(inv, ItemUtils.blankFill(), stock);
      p.closeInventory();
      p.openInventory(inv);
   }

   public static void upgradeGUI(VinnyUtils.VinnyD vinny, Player p){
      List<ItemStack> stock = vinny.getUpgrades().stream().map(Upgrade::getResultItem).collect(Collectors.toList());
      int size = stock.size();
      Inventory inv;
      if(size <= 9)
         inv = Bukkit.createInventory(null, 9, "Vinny's Upgrades");
      else if(size <= 18)
         inv = Bukkit.createInventory(null, 18, "Vinny's Upgrades");
      else if(size <= 27)
         inv = Bukkit.createInventory(null, 27, "Vinny's Upgrades");
      else if(size <= 36)
         inv = Bukkit.createInventory(null, 36, "Vinny's Upgrades");
      else if(size <= 45)
         inv = Bukkit.createInventory(null, 45, "Vinny's Upgrades");
      else if(size <= 54)
         inv = Bukkit.createInventory(null, 54, "Vinny's Upgrades");
      else
         inv = Bukkit.createInventory(null, 63, "Vinny's Upgrades");
      GenUtils.setMiddle(inv, ItemUtils.blankFill(), stock);
      p.closeInventory();
      p.openInventory(inv);
   }

   public static void initialGUI(VinnyUtils.VinnyD vinny, Player p){
      Inventory inv = Bukkit.createInventory(null, 9, "Vinny's Upgrades");
      GenUtils.fill(inv, ItemUtils.blankFill());
      if(VinnyUtils.isCanUpgrade()){
         inv.setItem(2, ItemUtils.createItem(Material.EMERALD));
      }
      else{

      }



      p.closeInventory();
      p.openInventory(inv);
   }


}
