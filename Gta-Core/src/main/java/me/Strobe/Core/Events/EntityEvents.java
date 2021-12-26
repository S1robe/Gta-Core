package me.Strobe.Core.Events;

import me.Strobe.Core.Utils.StringUtils;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityTeleportEvent;

//unsure what to use here
public class EntityEvents implements Listener {

   @EventHandler //TODO, Enderman teleports are not canceled.
   public void onTeleport(EntityTeleportEvent e) {
      if(e.getEntity() instanceof Enderman) {
         e.setCancelled(true);
      }
   }

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
      if(e.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.NATURAL))
         if(entity instanceof Zombie || entity instanceof Skeleton || entity instanceof Enderman || entity instanceof Villager){
            switch(entity.getType()){
               case ZOMBIE:
                  entity.setCustomName(StringUtils.color("&6Crook"));
                  return;
               case SKELETON:
                  entity.setCustomName(StringUtils.color("&6Thug"));
                  return;
               case ENDERMAN:
                  entity.setCustomName(StringUtils.color("&4Assassin"));
                  return;
               case VILLAGER:
                  entity.setCustomName(StringUtils.color("&6Citizen"));
            }
         }
         else if(!(entity instanceof HumanEntity))
            e.setCancelled(true);

   }

}
