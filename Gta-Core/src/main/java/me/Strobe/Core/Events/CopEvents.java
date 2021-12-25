package me.Strobe.Core.Events;

import me.Strobe.Core.Utils.Displays.GUIS;
import me.Strobe.Core.Utils.StringUtils;
import me.Strobe.Core.Utils.User;
import org.bukkit.entity.Entity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class CopEvents implements Listener {

   public static void onCopDrop(User u, PlayerDropItemEvent e) {
      if(!e.isCancelled()) {
         e.setCancelled(true);
         u.sendPlayerMessage(StringUtils.deniedInCop);
      }
   }

   public static void onCompassClick(PlayerInteractEvent e){
      Player p = e.getPlayer();
      GUIS.wantedList(p);
   }

   public static void onCopDamage(User attacker, User defender, EntityDamageByEntityEvent e) {
      System.out.println(attacker.isAttackedCop());
      System.out.println(defender.isAttackedCop());
      if(attacker.isCop() && defender.isCop()) {
         e.setCancelled(true);
      }
      else if(attacker.isCop() && !defender.isAttackedCop()) {
         e.setCancelled(true);
      }
      else if(defender.isCop()) {
         if(!attacker.isAttackedCop()) {
            // TOOD: How to handle shotguns and multihit weapons.
            // perhaps resistance ?
            attacker.setAttackedCop(true);
         }
      }
   }

   @EventHandler
   public void onPigCopDamaged(EntityDamageEvent e) {
      Entity entity = e.getEntity();
      if(entity instanceof PigZombie) {
         if(entity.hasMetadata("INVULNERABLE"))
            e.setCancelled(true);
         else if(e.getCause().equals(EntityDamageEvent.DamageCause.FALL))
            e.setCancelled(true);
      }
   }

}
