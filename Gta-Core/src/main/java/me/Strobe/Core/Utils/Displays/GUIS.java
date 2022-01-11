package me.Strobe.Core.Utils.Displays;

import me.Strobe.Core.Utils.*;
import me.Strobe.Core.Utils.Looting.LootItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static me.Strobe.Core.Utils.ItemUtils.*;

public final class GUIS {

   private static final Map<Player, Integer> pageViewing = new HashMap<>(); //page 1 is labeled as 1;

   private GUIS() {
   }

   public static void changePage(Player viewer, String type, boolean forward) {
      Inventory inv = Bukkit.createInventory(viewer, 54, type + " Pool");
      GenUtils.fill(inv, blankFill());
      if(forward)
         pageViewing.put(viewer, pageViewing.get(viewer) + 1);
      else if(pageViewing.get(viewer) > 1)
         pageViewing.put(viewer, pageViewing.get(viewer) - 1);
      int loopamount = 49 * pageViewing.get(viewer);
      fillInvWithLoot(inv, loopamount, type);
      finalizeInventory(viewer, inv);
   }

   private static void fillInvWithLoot(Inventory inv, int loopamount, String type) {
      int slot = 0;
      List<LootItem> lootList = LootingUtils.getLootList(type);
      if(lootList.size() < loopamount)
         for(int i = loopamount - 49; i < lootList.size(); i++) {
            ItemStack item = lootList.get(i).getDisplayItem();
            inv.setItem(slot, item);
            slot++;
         }
      else
         for(int i = loopamount - 49; i < loopamount; i++) {
            ItemStack item = lootList.get(i).getDisplayItem();
            inv.setItem(slot, item);
            slot++;
         }
   }

   private static void finalizeInventory(Player viewer, Inventory inv) {
      inv.setItem(49, secretGlass());
      inv.setItem(50, secretGlass());
      inv.setItem(51, secretGlass());
      inv.setItem(52, backArrow());
      inv.setItem(53, forArrow());
      viewer.closeInventory();
      viewer.openInventory(inv);
   }

   public static void firstPage(Player viewer, String type) {
      pageViewing.put(viewer, 1);
      Inventory inv = Bukkit.createInventory(viewer, 54, type + " Pool");
      GenUtils.fill(inv, blankFill());
      fillInvWithLoot(inv, 49, type);
      finalizeInventory(viewer, inv);
   }

   public static void alterLootItem(Player viewer, LootItem item, String type) {
      viewer.closeInventory();
      Inventory inv;
      ItemStack i = item.getDisplayItem();
      if(i.hasItemMeta())
         inv = Bukkit.createInventory(viewer, 27, "Editing " + type + ": " + i.getItemMeta().getDisplayName());
      else
         inv = Bukkit.createInventory(viewer, 27, "Editing " + type + ": " + i.getType());
      GenUtils.fill(inv, blankFill());
      inv.setItem(10, changeAmtType(item.getMin(), "Min Item Drop"));
      inv.setItem(4, item.getDisplayItem());
      inv.setItem(22, item.getItem());
      inv.setItem(1, changeAmtType(item.getMax(), "Max Item Drop"));
      inv.setItem(7, saveItem());
      inv.setItem(25, cancelItem());
      inv.setItem(19, changeAmtType(1, "Item Weight"));
      ItemUtils.changeLine(inv.getItem(4), 3, "§9DropRate: §7" + StringUtils.formatDouble((item.getWeight() / (LootingUtils.getTotalBagWeight(type))) * 100, 4));
      viewer.closeInventory();
      viewer.openInventory(inv);
   }

   private static ItemStack changeAmtType(int amt, String type){
      return ItemUtils.createItem(Material.EMERALD, amt, (byte)0, true, type, "&6Left-Click: &a+1 &7| &6Right-Click: &c-1",
                                  "&6Shift-Left-Click &a+10 &7| &6Shift-Right-Click &c-10",
                                  "&6Drop: &c-0.1 &7| &6Middle-Click: &c+0.1",
                                  "&6Control+Drop: &c-0.01 &7| &6Number-Key-Click: &c+0.01"
                                 );
   }

   private static ItemStack saveItem(){
      return ItemUtils.createItem(Material.STAINED_GLASS_PANE, 1, (byte)5, true, "&a&lSave!");
   }
   private static ItemStack cancelItem(){
      return ItemUtils.createItem(Material.STAINED_GLASS_PANE, 1, (byte)14, true, "&c&lCancel.");
   }

   public static void wantedList(Player p) {
      Inventory inv = Bukkit.createInventory(null, 54, "Wanted Players");
      User u = User.getByPlayer(p);
      ArrayList<User> wantedPlayers = User.users.values().stream().filter(user -> user.getWantedlevel() > 0).collect(Collectors.toCollection(ArrayList::new));
      GenUtils.fill(inv, ItemUtils.blankFill());

      for(int i = 0; i < wantedPlayers.size() && i < 44; i++) {
         inv.setItem(i, CopUtils.wantedPlayersHead(wantedPlayers.get(i)));
      }

      inv.setItem(49, CopUtils.getTrackedPlayerItem(u));
      inv.setItem(53, ItemUtils.createItem(Material.BOOK, 1, (byte) 0, "&9&lGTA-MC Cop Mode", "&7Use /cop mode to track and kill", "&7wanted players on the server.", "", "&6&lNOTES", "&6&l1. &7If your player tracker turns", "&7back into a normal player tracking", "&7compass, the player you were recently", "&7tracking may have logged off, went", "&7into another world (/spawn) OR the", "&7player was killed and has a wanted", "&7level of 0.", "", "&7Wanted Kill Money: &a&l$"+CopUtils.moneyForWanted+" (&7x Wanted Level&a&l)"));
      p.closeInventory();
      p.openInventory(inv);
   }
}
