package me.Strobe.Core.Events;

import me.Strobe.Core.Utils.StringUtils;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

//unsure what to use here
public class EntityEvents implements Listener {

   /**
    * Might cause issues with spawning cars and etc.
    * For now, prevents anything that isnt a With Villager Zombie(Pigzombie) Player Projectile or Enderman from
    * spawning.
    *
    * @param e the spawn event linked to an entity
    */
   @EventHandler
   public void onSpawn(CreatureSpawnEvent e) {
      Entity entity = e.getEntity();
      if(e.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.NATURAL)) {
         if(entity instanceof Zombie || entity instanceof Skeleton || entity instanceof Enderman) {
            switch(entity.getType()) {
               case ZOMBIE:
                  entity.setCustomName(StringUtils.color("&6Crook"));
                  return;
               case SKELETON:
                  entity.setCustomName(StringUtils.color("&6Thug"));
                  return;
               case ENDERMAN:
                  entity.setCustomName(StringUtils.color("&4Enderman"));
            }
         }
         else if(!(entity instanceof HumanEntity))
            e.setCancelled(true);
      }
   }

   @EventHandler
   public void mobBurning(EntityCombustEvent e) {
      if(e.getEntityType().equals(EntityType.SKELETON) || e.getEntityType().equals(EntityType.ZOMBIE)) {
         e.setCancelled(true);
      }
   }

   @EventHandler
   public void onMobClick(PlayerInteractEntityEvent e){
      if(e.getRightClicked() instanceof Villager)
         e.setCancelled(true);
   }

}
