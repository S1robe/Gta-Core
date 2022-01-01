package me.Strobe.Core.Events;

import me.Strobe.Core.Utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Random;

/**
 * this is the events listener for any possible Plauer Vs Player event.
 */
public class PvpEvents implements Listener {

   private static final Random rand = new Random();

   @EventHandler
   public void onDamage(EntityDamageByEntityEvent e) {
      //increments damage dealt and accumulated.
      if(e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
         Player damager = (Player) e.getDamager();
         Player receiver = (Player) e.getEntity();
         User UDamager = User.getByPlayer(damager);
         User UReceiver = User.getByPlayer(receiver);
         if(UReceiver != null && UDamager != null && UReceiver.isCop()) {
            CopEvents.onCopDamage(UDamager, UReceiver, e);
         }
      }
      else if( e.getDamager() instanceof Projectile && e.getEntity() instanceof Player){
         handleProjectileDamage(e);
      }
   }

   private void handleProjectileDamage(EntityDamageByEntityEvent e) {
      if(e.getEntity().hasMetadata("NPC")) {
         handleNPC(e.getEntity());
         return;
      }
      if (e.getDamager() instanceof Projectile) {
         final Projectile projectile = (Projectile) e.getDamager();
         if (projectile.getShooter() instanceof Player) {
            Player damager = (Player) projectile.getShooter();
            LivingEntity target = (LivingEntity) e.getEntity();

            if (target instanceof Player) {
               Player receiver = (Player) e.getEntity();
               User UDamager = User.getByPlayer(damager);
               User UReceiver = User.getByPlayer(receiver);
               assert UDamager != null && UReceiver != null;
               if(UReceiver.isCop()) {
                  CopEvents.onCopDamage(UDamager, UReceiver, e);
               }
            }
         }
      }
   }

   private void handleNPC(Entity e){

   }

   @EventHandler
   public void onDeath(PlayerDeathEvent e) {
      Player killed = e.getEntity();
      User Ukilled = User.getByPlayer(killed);
      if(Ukilled.isCop())
         e.setKeepInventory(true);
      if(CopUtils.playerRunnableIds.containsKey(killed.getUniqueId())){
         Bukkit.getScheduler().cancelTask(CopUtils.playerRunnableIds.get(killed.getUniqueId()));
         CopUtils.despawnAllCops(killed.getUniqueId());
         CopUtils.playerRunnableIds.remove(killed.getUniqueId());
      }
      if(killed.equals(killed.getKiller()) || killed.getKiller() == null) {
         //Drop the money item with 15% of the deceased's current bal.
         killed.getWorld().dropItem(killed.getLocation(), moneyItem(processDeath(null, Ukilled, true)));
      }
      else {
         User Ukiller = User.getByPlayer(killed.getKiller());
         killed.getWorld().dropItem(killed.getLocation(), moneyItem(processDeath(Ukiller, Ukilled, false)));
      }
   }

   private ItemStack moneyItem(double amt) {
      ItemStack money = new ItemStack(Material.GOLD_NUGGET);
      ItemMeta meta = money.getItemMeta();
      meta.setDisplayName("" + amt);
      money.setItemMeta(meta);
      return money;
   }

   /**
    * @param killer The user object of the player that did the killing
    * @param killed The user object of the player that died
    * @param self   If this is a suicide or not
    *
    * @return The percentage of Crux to be dropped.
    */
   private double processDeath(User killer, User killed, boolean self) {
      killed.addDeaths(1);
      int wl = killed.getWantedlevel();
      killed.removeWantedLevel(killed.getWantedlevel());
      killed.sendPlayerMessage(StringUtils.Text.R_WL.create());
      double changeBal = killed.getBalance() * CopUtils.percentMoneyToDropOnDeath;
      killed.removeMoney(changeBal);
      killed.sendPlayerMessage(StringUtils.Text.DROPPED_MONEY.create(StringUtils.formatDouble(changeBal), "$" + StringUtils.formatDouble(killed.getBalance())));
      if(!self) {
         killer.addPVPKills(1);
         if(killer.isCop()){
            changeBal = wl * CopUtils.moneyForWanted;
            killer.addMoney(changeBal);
            killer.sendPlayerMessage(StringUtils.Text.GAINED_MONEY_FOR_WANTED.create(""+changeBal,"" + wl) );
            return changeBal;
         }
         int change = GenUtils.getRandInt(1, Math.max(1, killed.getWantedlevel() / 2));
         if(killed.isCop()) {
            change = GenUtils.getRandInt(1, Math.max(CopUtils.lowWantedForCop, CopUtils.highWantedForCop));
            killer.addCopKills(1);
            killer.addMoney(CopUtils.moneyForKillCop);
            killer.sendPlayerMessage(StringUtils.Text.GAINED_MONEY_FOR_KILL_COP.create(""+ CopUtils.moneyForKillCop));
         }
         killer.addWantedLevel(change); //increase the killers WL by at most 1/2 the wanted level of the killed person.
         killer.sendPlayerMessage(StringUtils.Text.INC_WL.create("" + change, "" + killer.getWantedlevel()));
         killed.setAttackedCop(false);
         CopUtils.spawnCopsOnPlayer(killer, -1, 0);
      }
      return changeBal;
   }

   @EventHandler
   public void onItemDamage(PlayerItemDamageEvent e) {
      Player player = e.getPlayer();
      User u = User.getByPlayer(player);
      if(u.isCop() || !RegionUtils.allowsPVP(player.getLocation())) {
         e.setCancelled(true);
      }
   }
}
