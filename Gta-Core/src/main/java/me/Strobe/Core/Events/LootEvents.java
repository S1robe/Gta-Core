package me.Strobe.Core.Events;

import me.Strobe.Core.Utils.*;
import me.Strobe.Core.Utils.Looting.LootItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Furnace;
import org.bukkit.block.Hopper;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.EnderChest;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LootEvents implements Listener {

   /**
    * @param e The specific event that fires whenever a player interacts with an object, this event is
    *          narrowed down only to those that are chests, and if the player and chest are within pvp-enabled areas.
    *          the player must be in a pvp enabled location to loot these chests for now because if they are allowed to
    *          loot chest while being safe then they can potentally farm while being safe.
    *          Chests must also be inside a pvp-enabled area because some houses may be surrounded or bordered by a PVP
    *          area.
    */
   @EventHandler
   public void onChestLoot(PlayerInteractEvent e) {
      Block b = e.getClickedBlock();
      if(b != null) {
         Location bL = b.getLocation();
         if(e.getAction() == Action.RIGHT_CLICK_BLOCK && b.getState() instanceof Chest && LootingUtils.isWorldLootActive(bL.getWorld()) && RegionUtils.allowsPVP(bL)) {
            Player p = e.getPlayer();
            User u = User.getByPlayer(p);
            if(u.isCop()) {
               e.setCancelled(true);
               u.sendPlayerMessage(StringUtils.Text.DENY_ACTION.create());
               return;
            }
            if(RegionUtils.allowsPVP(p.getLocation())) {
               e.setCancelled(true);
               Long xpirationTime = u.getChestLocations().get(b.getLocation());
               p.closeInventory();
               RegionUtils.playSound(p.getLocation(), Sound.CHEST_OPEN, u.getChestVolume()/100, 1);
               if(xpirationTime != null)
                  if(System.currentTimeMillis() >= xpirationTime)
                     openNewChest(bL, u);
                  else
                     openOldChest(bL, u);
               else
                  openNewChest(bL, u);
            }
            else {
               StringUtils.sendMessage(p, "&c&l(!) &7&lYou must be in a PVP-Enabled area to open this.");
            }
         }
         else if(b.getType().equals(Material.BREWING_STAND)) onBrewingClick(e);
         else if(b.getType().equals(Material.WORKBENCH)) onCraftEvent(e);
         else if(b.getType().equals(Material.DROPPER)) onDropperClick(e);
         else if(b.getType().equals(Material.DISPENSER)) onDispenserClick(e);
         else if(b.getType().equals(Material.ANVIL)) onAnvilClick(e);
         else if(b.getState() instanceof Furnace) onFuranceClick(e);
         else if(b.getState() instanceof Painting) onPaintingtingInteract(e);
         else if(b.getState() instanceof ItemFrame) onItemFrameInteract(e);
         else if(b.getState() instanceof Hopper) onHopperClick(e);
         else if(b.getState() instanceof EnderChest) onEnderChestClick(e);
      }
      else if(e.getItem() != null && User.getByPlayer(e.getPlayer()).isCop() && e.getItem().getType().equals(Material.COMPASS)){
         CopEvents.onCompassClick(e);
         RegionUtils.playSound(e.getPlayer().getLocation(), Sound.CLICK, 1, 1);
      }
   }

   private void onEnderChestClick(PlayerInteractEvent e) {
      User u = User.getByPlayer(e.getPlayer());
      if(u.isCop()){
         e.setCancelled(true);
         u.sendPlayerMessage(StringUtils.Text.DENY_ACTION.create());
      }
   }

   /**
    * @param chestLoc Location of the chest that is being looted from
    * @param user     The user that is both online and doing the looting, may not ever be null.
    */
   private void openNewChest(@NotNull Location chestLoc, @NotNull User user) {
      final int draw = GenUtils.getRandInt(LootingUtils.getMinItemChestdrop(), LootingUtils.getMaxItemChestDrop());
      List<ItemStack> loot = drawNewLoot(draw);
      user.addChestLocation(chestLoc, System.currentTimeMillis() + LootingUtils.getChestResetTime(), loot);
      user.setViewedChestLocation(chestLoc);
      displayChest(loot, user.getPLAYER().getPlayer(), true);
   }

    /**
    * @param chestLoc Location of the chest that is being looted from.
    * @param user     The user that is both online and doing the looting, never null.
    */
   private void openOldChest(@NotNull Location chestLoc, @NotNull User user) {
      Map<Location, List<ItemStack>> chestLoot = user.getChestLoot();
      user.setViewedChestLocation(chestLoc);
      displayChest(chestLoot.get(chestLoc),  user.getPLAYER().getPlayer(), false);
   }

   /**
    * @param draw The amount of items to draw
    *
    * @return a new array of itemstacks of quantity draw.
    */
   private List<ItemStack> drawNewLoot(int draw) {
      List<ItemStack> loot = new ArrayList<>(draw);
      for(int i = 0; i < draw; i++) {
         LootItem x = LootingUtils.getRandom("Chest");
         if(x != null)
            loot.add(x.getRandom());
      }
      return loot;
   }

   /**
    * @param loot   The Loot itemstack to display to the player in question
    * @param viewer The player that will be viewing the inventory.
    */
   private void displayChest(List<ItemStack> loot, @NotNull Player viewer, boolean random) {
      Inventory inv = Bukkit.createInventory(viewer, InventoryType.CHEST, "Loot Chest");
      if(random)
         LootingUtils.placeRandomlyInChest(inv, loot);
      else
         inv.setContents(loot.toArray(new ItemStack[0]));
      viewer.openInventory(inv);
   }

   private void onFuranceClick(PlayerInteractEvent e){
      RegionUtils.playSound(e.getPlayer().getLocation(), Sound.CLICK, 1, 1);
      if(!e.getPlayer().hasPermission("gtacore.admin"))
         e.setCancelled(true);}
   private void onCraftEvent(PlayerInteractEvent e){
      RegionUtils.playSound(e.getPlayer().getLocation(), Sound.CLICK, 1, 1);
      if(!e.getPlayer().hasPermission("gtacore.admin"))
         e.setCancelled(true);}
   private void onDropperClick(PlayerInteractEvent e){
      RegionUtils.playSound(e.getPlayer().getLocation(), Sound.CLICK, 1, 1);
      if(!e.getPlayer().hasPermission("gtacore.admin"))
         e.setCancelled(true);}
   private void onBrewingClick(PlayerInteractEvent e){
      RegionUtils.playSound(e.getPlayer().getLocation(), Sound.CLICK, 1, 1);
      if(!e.getPlayer().hasPermission("gtacore.admin"))
         e.setCancelled(true);}
   private void onHopperClick(PlayerInteractEvent e){
      RegionUtils.playSound(e.getPlayer().getLocation(), Sound.CLICK, 1, 1);
      if(!e.getPlayer().hasPermission("gtacore.admin"))
         e.setCancelled(true);}
   private void onDispenserClick(PlayerInteractEvent e){
      RegionUtils.playSound(e.getPlayer().getLocation(), Sound.CLICK, 1, 1);
      if(!e.getPlayer().hasPermission("gtacore.admin"))
         e.setCancelled(true);}
   private void onAnvilClick(PlayerInteractEvent e){
      RegionUtils.playSound(e.getPlayer().getLocation(), Sound.CLICK, 1, 1);
      if(!e.getPlayer().hasPermission("gtacore.admin"))
         e.setCancelled(true);
   }
   private void onItemFrameInteract(PlayerInteractEvent e){
      RegionUtils.playSound(e.getPlayer().getLocation(), Sound.CLICK, 1, 1);
      if(!e.getPlayer().hasPermission("gtacore.admin"))
         e.setCancelled(true);
   }
   private void onPaintingtingInteract(PlayerInteractEvent e){
      RegionUtils.playSound(e.getPlayer().getLocation(), Sound.CLICK, 1, 1);
      if(!e.getPlayer().hasPermission("gtacore.admin"))
         e.setCancelled(true);
   }
}
