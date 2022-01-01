package me.Strobe.Housing.Utils.Displays;

import me.Strobe.Core.Utils.GenUtils;
import me.Strobe.Core.Utils.ItemUtils;
import me.Strobe.Core.Utils.StringUtils;
import me.Strobe.Housing.House;
import me.Strobe.Housing.Utils.HouseUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class GUIS {


   public static ItemStack divider() {
      return ItemUtils.createItem(Material.STICK, 1, 0, "&a<&m---&r &a&nOwn&r &8: &6&nAdded&r &6&m---&r&6>");
   }

   public static ItemStack noHouse() {
      return ItemUtils.createItem(Material.IRON_DOOR, 1, (byte)0, true, "&9You dont own any houses!");
   }

   public static ItemStack noMemberOf() {
      return ItemUtils.createItem(Material.IRON_DOOR, 1,  (byte)0, true, "&9You're not added to any houses!");
   }

   public static ItemStack filler() {
      return ItemUtils.createItem(Material.STAINED_GLASS_PANE, 1, 15, "&9&l???");
   }

   public static void homes(Player p) {
      House h = HouseUtils.getHouseByPlayer(p);
      ItemStack houseItemOwned = null;
      if(h != null)
         houseItemOwned = h.houseItem(p);
      ArrayList<ItemStack> houseItemAddedTo = new ArrayList<>();
      HouseUtils.getHousesPlayerIsAddedTo(p).forEach(house -> houseItemAddedTo.add(house.houseItem(p)));

      int size = houseItemAddedTo.size() + (houseItemOwned == null? 0 : 1);
      Inventory inv;

      if(size <= 9)
         inv = Bukkit.createInventory(p, 9, StringUtils.color("&9Select a home to travel to..."));
      else if(size <= 18)
         inv = Bukkit.createInventory(p, 18, StringUtils.color("&9Select a home to travel to..."));
      else if(size <= 27)
         inv = Bukkit.createInventory(p, 27, StringUtils.color("&9Select a home to travel to..."));
      else if(size <= 36)
         inv = Bukkit.createInventory(p, 36, StringUtils.color("&9Select a home to travel to..."));
      else if(size <= 45)
         inv = Bukkit.createInventory(p, 45, StringUtils.color("&9Select a home to travel to..."));
      else if(size <= 54)
         inv = Bukkit.createInventory(p, 54, StringUtils.color("&9Select a home to travel to..."));
      else
         inv = Bukkit.createInventory(p, 63, StringUtils.color("&9Select a home to travel to..."));

      GenUtils.fill(inv, filler());
      GenUtils.setMiddle(inv, divider());
      p.closeInventory();
      if(size == 0) {
         inv.setItem(0, noHouse());
         inv.setItem(5, noMemberOf());
      }
      else if(houseItemAddedTo.size() == 0) {
         inv.setItem(0, houseItemOwned);
         inv.setItem(5, noMemberOf());
         HouseUtils.updateItemInHouseGUI(p, inv.getItem(0), h);
      }
      else if(houseItemOwned == null) {
         inv.setItem(0, noHouse());
         GenUtils.setRight(inv, filler(), houseItemAddedTo);
         houseItemAddedTo.forEach(i -> HouseUtils.updateItemInHouseGUI(p, i, h));
      }
      else {
         inv.setItem(0, houseItemOwned);
         GenUtils.setRight(inv, filler(), houseItemAddedTo);
         HouseUtils.updateItemInHouseGUI(p, inv.getItem(0), h);
         houseItemAddedTo.forEach(i -> HouseUtils.updateItemInHouseGUI(p, i, h));
      }
      p.openInventory(inv);
   }

   public static void home(Player p, House h) {
      p.closeInventory();
      Inventory inv = Bukkit.createInventory(null, 9,  StringUtils.color("&8House: &9" + h.getName()));
      GenUtils.fill(inv, filler());
      inv.setItem(0, ItemUtils.createItem(Material.DARK_OAK_DOOR_ITEM, "&6Travel Home"));
      inv.setItem(1, ItemUtils.createItem(Material.NETHER_STAR,  "&aExtend Your Stay"));
      inv.setItem(2, ItemUtils.createItem(Material.TRAP_DOOR,  "&cLeave Home"));
      inv.setItem(4, ItemUtils.createItem(Material.BED, "&eEdit Members"));
      inv.setItem(7, ItemUtils.getSkullOf(p.getUniqueId()));
      HouseUtils.updateItemInHouseGUI(p, inv.getItem(7), h);
      p.openInventory(inv);

   }

   public static void notOwnerView(Player p, House h, boolean isAdded) {
      p.closeInventory();
      OfflinePlayer ow = h.getOwner();
      Inventory inv = Bukkit.createInventory(null, 9,  StringUtils.color("&8" + ow.getName() + "'s House: &9" + h.getName()));
      GenUtils.fill(inv, filler());
      if(isAdded){
         inv.setItem(0, ItemUtils.createItem(Material.DARK_OAK_DOOR_ITEM, "&6Travel Home"));
         inv.setItem(1, ItemUtils.createItem(Material.NETHER_STAR,  "&aExtend Your Stay"));
         inv.setItem(2, ItemUtils.createItem(Material.TRAP_DOOR,  "&cLeave Home"));
         inv.setItem(7, ItemUtils.getSkullOf(ow.getUniqueId()));
         HouseUtils.updateItemInHouseGUI(p, inv.getItem(7), h);
      }
      else {
         inv.setItem(4, ItemUtils.createItem(Material.IRON_DOOR, 1, 0, "&cYou dont own this house!"));
         HouseUtils.updateItemInHouseGUI(p, inv.getItem(4), h);
      }
      p.openInventory(inv);
   }

   public static void editMembers(Player p, House h) {
      p.closeInventory();
      int numMems = h.getMembers().size();
      Inventory inv;
      if(numMems <= 9)
         inv = Bukkit.createInventory(null, 18, StringUtils.color("&9Members of " + h.getName()));
      else if(numMems <= 18)
         inv = Bukkit.createInventory(null, 27,  StringUtils.color("&9Members of " + h.getName()));
      else if(numMems <= 27)
         inv = Bukkit.createInventory(null, 36,  StringUtils.color("&9Members of " + h.getName()));
      else if(numMems <= 36)
         inv = Bukkit.createInventory(null, 45,  StringUtils.color("&9Members of " + h.getName()));
      else if(numMems <= 45)
         inv = Bukkit.createInventory(null, 54,  StringUtils.color("&9Members of " + h.getName()));
      else
         inv = Bukkit.createInventory(null, 64,  StringUtils.color("&9Members of " + h.getName()));
      GenUtils.fill(inv, filler());
      inv.setItem(4, ItemUtils.createItem(Material.POTION, 1, (byte) 8201, "&cBack"));
      for(int s = 0; s < h.getMembers().size() && s < inv.getSize(); s++) {
         inv.setItem(s, ItemUtils.getSkullOf(h.getMembers().get(s).getUniqueId()));
      }
      p.openInventory(inv);
   }

}
