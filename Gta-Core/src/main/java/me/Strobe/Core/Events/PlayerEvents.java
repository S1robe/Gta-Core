package me.Strobe.Core.Events;

import me.Strobe.Core.Utils.CopUtils;
import me.Strobe.Core.Utils.Displays.ScoreboardManager;
import me.Strobe.Core.Utils.RegionUtils;
import me.Strobe.Core.Utils.StringUtils;
import me.Strobe.Core.Utils.User;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;


public class PlayerEvents implements Listener {

   @EventHandler
   public void onJoin(PlayerJoinEvent p) {
      Player player = p.getPlayer();
      User user = User.userJoined(player.getUniqueId());
      ScoreboardManager.createScoreBoard(user);
   }

   @EventHandler
   public void onQuit(PlayerQuitEvent e) {
      Player player = e.getPlayer();
      User user = User.removeUser(player.getUniqueId());
      if(CopUtils.playerRunnableIds.containsKey(player.getUniqueId())){
         Bukkit.getScheduler().cancelTask(CopUtils.playerRunnableIds.get(player.getUniqueId()));
         CopUtils.despawnAllCops(player.getUniqueId());
         CopUtils.playerRunnableIds.remove(player.getUniqueId());
      }

      if(user.getTrackedBy() != null)
         user.getTrackedBy().setTracked(null);
      user.savePlayerData();
   }

   @EventHandler
   public void onTeleport(PlayerTeleportEvent e){
      Player player = e.getPlayer();
      if(!RegionUtils.allowsPVP(e.getTo()) && CopUtils.playerRunnableIds.containsKey(player.getUniqueId())){
         StringUtils.sendMessage(player, StringUtils.Text.COPS_DISENGAGE.create());
         Bukkit.getScheduler().cancelTask(CopUtils.playerRunnableIds.get(player.getUniqueId()));
         CopUtils.despawnAllCops(player.getUniqueId());
         CopUtils.playerRunnableIds.remove(player.getUniqueId());
      }
   }

   @EventHandler
   public void onPickup(PlayerPickupItemEvent e) {
      ItemStack item = e.getItem().getItemStack();
      Player player = e.getPlayer();
      User Uplayer = User.getByPlayer(player);
      if(item.getType().equals(Material.GOLD_NUGGET) && item.hasItemMeta()) {
         e.setCancelled(true);
         double money = StringUtils.roundDecimal(Double.parseDouble(item.getItemMeta().getDisplayName()), 2);
         Uplayer.sendPlayerMessage(StringUtils.Text.GAINED_MONEY.create(""+ money));
         e.getItem().remove();
         player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 0.2F, 2f);
         Uplayer.addMoney(money);
      }
      if(Uplayer.isCop()) {
         e.setCancelled(true);
      }
   }

   @EventHandler
   public void onDrop(PlayerDropItemEvent e) {
      User u = User.getByPlayer(e.getPlayer());
      if(u.isCop()) {
         CopEvents.onCopDrop(u, e);
      }
   }

   @EventHandler
   public void onCommand(PlayerCommandPreprocessEvent e) {
      final Player p = e.getPlayer();
      final User u = User.getByPlayer(p);
      if(u.isCop() && CopUtils.blackListedCommands.contains(e.getMessage().split(" ")[0].toLowerCase().replace("/", ""))) {
         e.setCancelled(true);
         u.sendPlayerMessage(StringUtils.Text.DENY_ACTION.create());
      }
   }

   @EventHandler
   public void onHungerChange(FoodLevelChangeEvent e) {
      if(e.getEntity() instanceof Player && CopUtils.cops.containsKey(e.getEntity().getUniqueId()))
         e.setCancelled(true);
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void commitEnchant(final EnchantItemEvent e) {
      e.getEnchanter().setLevel(e.getEnchanter().getLevel() - e.getExpLevelCost() + (e.whichButton() + 1));
      e.setExpLevelCost(0);
   }


}
