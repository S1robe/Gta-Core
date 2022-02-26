package me.strobe.gang.Events;

import me.strobe.gang.Member;
import me.strobe.gang.Utils.GangUtils;
import me.strobe.gang.Utils.MemberUtils;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEvents implements Listener {

   @EventHandler
   public void onJoin(PlayerJoinEvent e){
      Player p = e.getPlayer();
      Member m = MemberUtils.getMemberFromPlayer(p);
      if(m == null) return;

   }

   @EventHandler
   public void onLeave(PlayerQuitEvent e){
      MemberUtils.handleOnQuit(e.getPlayer());
   }

   @EventHandler
   public void onDamage(EntityDamageByEntityEvent e){
      if(e.getDamager() instanceof Player){
         Player damager = (Player) e.getDamager();
         if(e.getEntity() instanceof Player){
            Player receiver = (Player) e.getEntity();
            e.setCancelled(handleDamage(damager, receiver));
         }
      }
      else if(e.getDamager() instanceof Projectile){
         Projectile p = (Projectile) e.getDamager();
         if(p.getShooter() instanceof Player) {
            Player damager = (Player) p.getShooter();
            if(e.getEntity() instanceof Player){
               Player receiver = (Player) e.getEntity();
               e.setCancelled(handleDamage(damager, receiver));
            }
         }
      }
   }

   private boolean handleDamage(Player damager, Player reciever){
      if(MemberUtils.getMemberFromPlayer(damager) != null){
         Member d = MemberUtils.getMemberFromPlayer(damager);
         if(MemberUtils.getMemberFromPlayer(reciever) != null){
            Member r = MemberUtils.getMemberFromPlayer(reciever);
            if(d.getGang().equals(r.getGang()))
               return !d.getGang().isFriendlyFire();
            else if(d.getGang().isAlly(r.getGang()))
               return !d.getGang().isFriendlyFire(r.getGang());
         }
      }
      return false;
   }

   @EventHandler
   public void onDeath(PlayerDeathEvent e){
      Player dead = e.getEntity();
      if(dead.getKiller() != null) {
         Player killer = dead.getKiller();
         if(MemberUtils.getMemberFromPlayer(killer) != null){
            Member mKiller = MemberUtils.getMemberFromPlayer(killer);
            mKiller.incKillCont();
            mKiller.incPointCont(1);
         }
      }
   }

}
