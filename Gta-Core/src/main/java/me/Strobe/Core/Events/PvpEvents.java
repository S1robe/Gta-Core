package me.Strobe.Core.Events;

import me.Strobe.Core.Utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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
         assert UDamager != null && UReceiver != null;
         if(UReceiver.isCop()) {
            CopEvents.onCopDamage(UDamager, UReceiver, e);
         }
      }
      else if( e.getDamager() instanceof Projectile && e.getEntity() instanceof Player){
         handleProjectileDamage(e);
      }
   }

   private void handleProjectileDamage(EntityDamageByEntityEvent e) {
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

   @EventHandler
   public void onDeath(PlayerDeathEvent e) {
      Player killed = e.getEntity();
      User Ukilled = User.getByPlayer(killed);
      if(Ukilled.isCop())
         e.setKeepInventory(true);
      Integer id = CopUtils.playerRunnableIds.get(killed.getUniqueId());
      if(id != null){
         Bukkit.getScheduler().cancelTask(id);
         CopUtils.despawnCopsOnPlayer(Ukilled.getPlayerUUID());
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
      killed.modPvpDeaths(1);
      int wl = killed.getWantedlevel();
      killed.modWantedLevel(-1 * killed.getWantedlevel());
      killed.sendPlayerMessage(StringUtils.resetWantedLevel);
      double changeBal = killed.getBalance() * CopUtils.percentMoneyToDropOnDeath;
      killed.removeMoney(changeBal);
      killed.sendPlayerMessage(StringUtils.droppedMoney.replace("{amt}", StringUtils.formatDouble(changeBal)).replace("{bal}", "$" + StringUtils.formatDouble(killed.getBalance())));
      if(!self) {
         killer.modPvPKills(1);
         if(killer.isCop()){
            changeBal = wl * CopUtils.moneyForWanted;
            killer.addMoney(changeBal);
            killer.sendPlayerMessage(StringUtils.gainedMoneyForWanted.replace("{amt}", ""+changeBal).replace("{wl}", "" + wl));
            return changeBal;
         }
         int change = GenUtils.getRandInt(1, Math.max(1, killed.getWantedlevel() / 2));
         if(killed.isCop()) {
            change = GenUtils.getRandInt(1, Math.max(CopUtils.lowWantedForCop, CopUtils.highWantedForCop));
            killer.modCopKills(1);
            killer.addMoney(CopUtils.moneyForKillCop);
            killer.sendPlayerMessage(StringUtils.gainedMoneyFromCop.replace("{amt}", ""+ CopUtils.moneyForKillCop));
         }
         killer.modWantedLevel(change); //increase the killers WL by at most 1/2 the wanted level of the killed person.
         killer.sendPlayerMessage(StringUtils.increasedWantedLevel.replace("{amt}", "" + change).replace("{wl}", "" + killer.getWantedlevel()));
         killed.setAttackedCop(false);
         CopUtils.spawnCopsOnPlayer(killer);
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
