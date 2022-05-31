package me.Strobe.Housing.Events;

import me.Strobe.Core.Utils.DelayedTeleport;
import me.Strobe.Core.Utils.StringUtils;
import me.Strobe.Core.Utils.User;
import me.Strobe.Housing.House;
import me.Strobe.Housing.Main;
import me.Strobe.Housing.Utils.Displays.GUIS;
import me.Strobe.Housing.Utils.HouseUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class HouseEvents implements Listener {

   @EventHandler
   public void onInteract(PlayerInteractEvent e) {
      Block b = e.getClickedBlock();
      System.out.println(b);
      if(b != null){
         House h = HouseUtils.getHouseByChestLocation(b.getLocation());
         if(h == null) return;
         if(HouseUtils.isHouseSign(b) && e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            onHouseSignClick(e.getPlayer(), h);
         }
         else if(b.getState() instanceof DoubleChest) {
            if(h.isPlayerApartOfHouse(e.getPlayer())) {
               Chest c = (Chest) b.getState();
               if(c.getInventory().getHolder() instanceof DoubleChest)
                  openCoveredHouseDoubleChest(e.getPlayer(), (DoubleChest) c.getInventory().getHolder());
            }
         }
         else if(HouseUtils.isHouseBlockedChest(b) && e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if(h.isPlayerApartOfHouse(e.getPlayer())) {
               Chest c = (Chest) b.getState();
               openCoveredHouseChest(e.getPlayer(), c);
            }
         }
      }
   }

   @EventHandler
   public void onBlockBreak(BlockBreakEvent e){
      Block b = e.getBlock();
      if(e.getPlayer().hasPermission("houses.admin")) {
         if(b != null && HouseUtils.isHouseSign(b)) {
            onHouseSignBreak(e.getPlayer(), HouseUtils.getHouseBySignBlock(b));
         }
      }
   }

   private void onHouseSignClick(Player p, House h) {
      if(h.isOwned() && h.isPlayerOwner(p))
         GUIS.home(p, h);
      else if(h.isOwned() && h.isPlayerAdded(p))
         GUIS.notOwnerView(p, h, true);
      else if(h.isOwned())
         GUIS.notOwnerView(p, h, false);
      else if(!h.isOwned()) {
         User u = User.getByPlayer(p);
         if(u.getBalance() >= h.getPrice())
            if(HouseUtils.getHouseByPlayer(p) == null)
               HouseUtils.buyHouse(p, h);
            else
               u.sendPlayerMessage(me.Strobe.Housing.Utils.StringUtils.alreadyOwnedHouse);
         else
            u.sendPlayerMessage(me.Strobe.Housing.Utils.StringUtils.notEnoughMoney);
      }
   }

   private void onHouseSignBreak(Player p, House h) {
      if(h.isOwned()) {
         h.sendOwnerMessage(me.Strobe.Housing.Utils.StringUtils.brokenHouseSign);
         h.sendMembersAMessage(me.Strobe.Housing.Utils.StringUtils.brokenHouseSignMember.replace("{reg}", h.getName()));
      }
      StringUtils.sendMessage(p, me.Strobe.Housing.Utils.StringUtils.adminDestroyHouse.replace("{reg}", h.getName()));
      HouseUtils.deleteHouse(h);
   }

   @EventHandler
   public void onInventoryInteract(InventoryClickEvent e) {
      Inventory i = e.getClickedInventory();
      if(i != null) {
         ItemStack item = e.getCurrentItem();
         Player p = (Player) e.getWhoClicked();
         User u = User.getByPlayer(p);
         String inventoryTitle = ChatColor.stripColor(i.getTitle());
         if(inventoryTitle.contains("Select a home")) {
            e.setCancelled(true);
            onHomesGUIInteract(p, item);
         }
         else if(inventoryTitle.contains("'s House:")) {
            e.setCancelled(true);
            House h = HouseUtils.getHouseByName(inventoryTitle.substring(inventoryTitle.lastIndexOf(" ") + 1));
            assert h != null;
            if(h.isPlayerAdded(p))
               onMemGUIInteract(p, h, item);
         }
         else if(inventoryTitle.contains("House: ")) {
            House h = HouseUtils.getHouseByPlayer(p);
            e.setCancelled(true);
            onHomeGUIInteract(p, h, item);
         }
         else if(inventoryTitle.contains("Members of ")){
            e.setCancelled(true);
            House h = HouseUtils.getHouseByPlayer(p);
            i.setItem(e.getSlot(), onMemEditGUIInteract(p, h, item, e.getClick()));
         }
      }
   }

   private void onHomesGUIInteract(Player p, ItemStack clicked) {
      if(!(clicked.isSimilar(GUIS.filler()) || clicked.isSimilar(GUIS.noHouse()) || clicked.isSimilar(GUIS.noMemberOf()) || clicked.isSimilar(GUIS.divider()))) {
         House h = HouseUtils.getHouseByName(ChatColor.stripColor(clicked.getItemMeta().getDisplayName()));
         if(h != null)
            if(h.isPlayerOwner(p))
               GUIS.home(p, h);
            else
               GUIS.notOwnerView(p, h, true);
      }
   }

   private void onHomeGUIInteract(Player p, House h, ItemStack clicked) {
      User u = User.getByPlayer(p);
      String itemDisplayName = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
      switch(itemDisplayName) {
         case "Travel Home":
            p.closeInventory();
            DelayedTeleport.doDelayedTeleport(Main.getMain(), p, h.getSpawnLocation(), 5);
            break;
         case "Extend Your Stay":
            if(u.getBalance() < (h.getPrice()/h.getStartDays())){
               u.sendPlayerMessage(me.Strobe.Housing.Utils.StringUtils.notEnoughMoney);
               return;
            }
            if(h.getDaysRemaining() >= HouseUtils.maxNumDays) {
               u.sendPlayerMessage(me.Strobe.Housing.Utils.StringUtils.maxDaysReached);
               return;
            }
            HouseUtils.extendHouse(p, h, 1);
            break;
         case "Leave Home":
            p.closeInventory();
            HouseUtils.unrentHouse(h);
            break;
         case "Edit Members":
            p.closeInventory();
            GUIS.editMembers(p, h);
            break;
      }
   }

   private ItemStack onMemEditGUIInteract(Player p, House h, ItemStack clicked, ClickType cT){
      if(clicked.getType().equals(Material.SKULL_ITEM)) {
         User u = User.getByPlayer(p);
         OfflinePlayer o = Bukkit.getOfflinePlayer(UUID.fromString(((SkullMeta) clicked.getItemMeta()).getOwner()));

         //TODO later on add permission and GUI here for editing permissions.

      }
      else if(clicked.getType().equals(Material.POTION)){
         GUIS.home(p, h);
      }
      return clicked;
   }

   private void onMemGUIInteract(Player p, House h, ItemStack clicked) {
      User u = User.getByPlayer(p);
      String itemDisplayName = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
      switch(itemDisplayName) {
         case "Travel Home":
            DelayedTeleport.doDelayedTeleport(Main.getMain(), p, h.getSpawnLocation(), 5);
            break;
         case "Extend Your Stay":
            if(u.getBalance() < (h.getPrice() / h.getStartDays())){
               u.sendPlayerMessage(me.Strobe.Housing.Utils.StringUtils.notEnoughMoney);
               return;
            }
            if(h.getDaysRemaining() >= HouseUtils.maxNumDays) {
               u.sendPlayerMessage(me.Strobe.Housing.Utils.StringUtils.maxDaysReached);
               return;
            }
            HouseUtils.extendHouse(p, h, 1);
            break;
         case "Leave Home":
            h.removeMember(p);
            p.closeInventory();
            StringUtils.sendMessage(p, me.Strobe.Housing.Utils.StringUtils.leftHouse.replace("{plr}", h.getOwner().getName()));
            h.sendAllMessage(me.Strobe.Housing.Utils.StringUtils.memberLeftHouse.replace("{plr}", p.getName()));
            break;
      }
   }

   private void openCoveredHouseChest(Player p, Chest clicked){
      p.closeInventory();
      p.openInventory(clicked.getInventory());
   }
   private void openCoveredHouseDoubleChest(Player p, DoubleChest clicked){
      p.closeInventory();
      p.openInventory(clicked.getInventory());
   }




}
