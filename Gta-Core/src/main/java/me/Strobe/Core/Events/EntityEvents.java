package me.Strobe.Core.Events;

import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTeleportEvent;

//unsure what to use here
public class EntityEvents implements Listener {

   @EventHandler
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
   public void onSpawn(EntitySpawnEvent e) {
      Entity entity = e.getEntity();
      EntityType type = entity.getType();
      switch(type){
         case ZOMBIE:
         case PIG_ZOMBIE:
         case SKELETON:
         case VILLAGER:
      }

//      else {
//         e.setCancelled(true);
//         e.getEntity().remove();
//      }
   }

}
