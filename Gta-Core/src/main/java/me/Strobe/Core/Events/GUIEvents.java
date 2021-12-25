package me.Strobe.Core.Events;

import me.Strobe.Core.Main;
import me.Strobe.Core.Utils.*;
import me.Strobe.Core.Utils.Displays.GUIS;
import me.Strobe.Core.Utils.Displays.TrackerRunnable;
import me.Strobe.Core.Utils.Looting.LootItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.UUID;

public class GUIEvents implements Listener {

   @EventHandler
   public void onClick(InventoryClickEvent e) {
      Inventory i = e.getClickedInventory();
      Player p = (Player) e.getWhoClicked();
      User u = User.getByPlayer(p);
      ItemStack clickedItem = e.getCurrentItem();
      int slot = e.getSlot();
      ClickType cT = e.getClick();
      if(i != null) {
         String title = ChatColor.stripColor(i.getTitle());
         if(title.contains(" Pool")) {
            e.setCancelled(true);
            handleViewingLoot(i, clickedItem, slot, cT, p);
         }
         else if(title.contains("Editing ")){
            e.setCancelled(true);
            handleEditingLoot(i, title, p, slot, cT);
         }
         else if(title.contains("Wanted Players")){
             e.setCancelled(true);
             handleWantedListClick(i, clickedItem, u, slot);
         }
      }
   }

   private void handleViewingLoot(Inventory i, ItemStack clickedItem, int slot, ClickType cT, Player p){
      if(clickedItem.equals(ItemUtils.forArrow()))
         GUIS.changePage(p, i.getTitle().replace(" Pool", ""), true);

      else if(clickedItem.equals(ItemUtils.backArrow()))
         GUIS.changePage(p, i.getTitle().replace(" Pool", ""), false);

      else if(p.hasPermission("gtacore.loot.edit"))
         if(slot < 49 && !clickedItem.isSimilar(ItemUtils.blankFill()))
            if(cT.equals(ClickType.LEFT)) {
               String type = i.getTitle().replace(" Pool", "");
               GUIS.alterLootItem(p, LootingUtils.getItemFromLootPool(type, clickedItem), type);
            }
            else if(cT.equals(ClickType.RIGHT)) {
               String type = i.getTitle().replace(" Pool", "");
               LootingUtils.removeItemFromLootPoolByDisplay(type, clickedItem);
               LootingUtils.saveSpecificLootFile(type);
               i.setItem(slot, ItemUtils.blankFill());
            }
   }

   private void handleEditingLoot(Inventory i, String title, Player p, int slot, ClickType cT){
      ItemStack displayItem = i.getItem(4);
      String type = title.substring(title.indexOf(" "), title.indexOf(": ")).trim();
      int oldMin = Integer.parseInt(ChatColor.stripColor(displayItem.getItemMeta().getLore().get(2)).trim().substring(10));
      int oldMax = Integer.parseInt(ChatColor.stripColor(displayItem.getItemMeta().getLore().get(1)).trim().substring(10));
      double oldWeight = Double.parseDouble(ChatColor.stripColor(displayItem.getItemMeta().getLore().get(0)).trim().substring(8));
      int newMin = oldMin;
      int newMax = oldMax;
      double newWeight = oldWeight;
      if(slot == 10){
         if(cT == ClickType.SHIFT_LEFT){
            newMin = Math.min(64, oldMin + 10);
            if(newMin > oldMax) newMin = oldMax;
            ItemUtils.changeLine(displayItem, 2, "§cMin Drop: §7" + newMin);
            i.getItem(10).setAmount(newMin);
         }
         else if(cT == ClickType.LEFT){
            newMin = Math.min(64, oldMin + 1);
            if(newMin > oldMax) newMin = oldMax;
            ItemUtils.changeLine(displayItem, 2, "§cMin Drop: §7" + newMin);
            i.getItem(10).setAmount(newMin);
         }
         else if(cT == ClickType.RIGHT){
            newMin = Math.max(1, oldMin - 1);
            ItemUtils.changeLine(displayItem, 2, "§cMin Drop: §7" + newMin);
            i.getItem(10).setAmount(newMin);
         }
         else if(cT == ClickType.SHIFT_RIGHT){
            newMin = Math.max(1, oldMin - 10);
            ItemUtils.changeLine(displayItem, 2, "§cMin Drop: §7" + newMin);
            i.getItem(10).setAmount(newMin);
         }
      }
      else if(slot == 1){
         if(cT == ClickType.SHIFT_LEFT){
            newMax = Math.min(64, oldMax + 10);
            ItemUtils.changeLine(displayItem, 1, "§eMax Drop: §7" + newMax );
            i.getItem(1).setAmount(newMax);
         }
         else if(cT == ClickType.LEFT){
            newMax = Math.min(64, oldMax + 1);
            ItemUtils.changeLine(displayItem, 1, "§eMax Drop: §7" + newMax );
            i.getItem(1).setAmount(newMax);
         }
         else if(cT == ClickType.RIGHT){
            newMax = Math.max(0, oldMax - 1);
            if(newMax < oldMin) newMax = oldMin;
            ItemUtils.changeLine(displayItem, 1, "§eMax Drop: §7" + newMax );
            i.getItem(1).setAmount(newMax);
         }
         else if(cT == ClickType.SHIFT_RIGHT){
            newMax = Math.max(0, oldMax - 10);
            if(newMax < oldMin) newMax = oldMin;
            ItemUtils.changeLine(displayItem, 1, "§eMax Drop: §7" + newMax );
            i.getItem(1).setAmount(newMax);
         }
      }
      else if(slot == 7){
         LootItem l = LootingUtils.getItemByItem(type, i.getItem(22));
         LootingUtils.removeItemFromLootPoolByRealItem(type, l);
         LootingUtils.addItemToLootPool(new LootItem(l.getItem(), newMin, newMax, newWeight), type);
         p.closeInventory();
         GUIS.firstPage(p, type);
      }
      else if(slot == 25){
         p.closeInventory();
         GUIS.firstPage(p, type);
      }
      else if(slot == 19){
         if(cT == ClickType.SHIFT_LEFT){
            newWeight = oldWeight + 10;
            ItemUtils.changeLine(displayItem, 0, "§aWeight: §7" + newWeight);
         }
         else if(cT == ClickType.LEFT){
            newWeight = oldWeight + 1;
            ItemUtils.changeLine(displayItem, 0, "§aWeight: §7" + newWeight);
         }
         else if(cT == ClickType.RIGHT){
            newWeight = Math.max(0, oldWeight - 1);
            ItemUtils.changeLine(displayItem, 0, "§aWeight: §7" + newWeight);
         }
         else if(cT == ClickType.SHIFT_RIGHT){
            newWeight = Math.max(0, oldWeight - 10);
            ItemUtils.changeLine(displayItem, 0, "§aWeight: §7" + newWeight);
         }
         else if(cT == ClickType.DROP){
            newWeight = Math.max(0, oldWeight - 0.1);
            ItemUtils.changeLine(displayItem, 0, "§aWeight: §7" + newWeight);
         }
         else if(cT == ClickType.CONTROL_DROP){
            newWeight = Math.max(0, oldWeight - 0.01);
            ItemUtils.changeLine(displayItem, 0, "§aWeight: §7" + newWeight);
         }
         else if(cT == ClickType.MIDDLE){
            newWeight = oldWeight + 0.1;
            ItemUtils.changeLine(displayItem, 0, "§aWeight: §7" + newWeight);
         }
         else if(cT == ClickType.NUMBER_KEY){
            newWeight = oldWeight + 0.01;
            ItemUtils.changeLine(displayItem, 0, "§aWeight: §7" + newWeight);
         }
         ItemUtils.changeLine(displayItem, 3, "§9DropRate: §7" + StringUtils.formatDouble((newWeight / (LootingUtils.getTotalBagWeight(type) + newWeight)) * 100));
      }
   }

   private void handleWantedListClick(Inventory i, ItemStack clicked, User u, int slot){
      u.getPLAYER().getPlayer().closeInventory();
      if(slot == 49 && u.getTracked() != null) {
         u.getTracked().setTrackedBy(null);
         u.setTracked(null);
         i.setItem(slot, CopUtils.getTrackedPlayerItem(u));
         Bukkit.getScheduler().cancelTask(u.getTrackerID());
         u.setTrackerID(-1);
      }
      else if(slot < 44 && clicked.getType().equals(Material.SKULL_ITEM)) {
         String uuid = ItemUtils.getStrippedLoreLine(clicked, 0);
         System.out.println(uuid);
         User tracked = User.getByUUID(UUID.fromString(uuid.substring(uuid.indexOf(" "))));
         u.setTracked(tracked);
         tracked.setTrackedBy(u);
         TrackerRunnable x = new TrackerRunnable(u);
         x.runTaskTimer(Main.getMain(), 0L, 20L);
         if(u.getTrackerID() != -1)
            u.setTrackerID(x.getTaskId());
      }
   }

   @EventHandler
   public void onLootChestClose(InventoryCloseEvent e){
      Inventory i = e.getInventory();
      Player p = (Player) e.getPlayer();
      String title = ChatColor.stripColor(i.getTitle());
      if(title.matches("Loot Chest")){
         User u = User.getByPlayer(p);
         u.getChestLoot().put(u.getViewedChestLocation(), Arrays.asList(i.getContents()));
      }
   }

}
