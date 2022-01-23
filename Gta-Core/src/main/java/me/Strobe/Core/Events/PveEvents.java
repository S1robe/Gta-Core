package me.Strobe.Core.Events;

import me.Strobe.Core.Utils.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.*;

import java.util.*;

public class PveEvents implements Listener {

   private final Random rand = new Random();

   @EventHandler
   public void onMobDeath(EntityDeathEvent e) {
      if(!(e.getEntity() instanceof Player)) {
         e.getDrops().clear();
         LivingEntity entity = e.getEntity();
         Player player = entity.getKiller();
         World world = entity.getWorld();
         if(player != null && LootingUtils.isWorldMobActive(world)) {
            User user = User.getByPlayer(player);
            try {
               EntityType eT = entity.getType();
               switch(eT) {
                  case VILLAGER: {
                     user.addVillKills(1);
                     break;
                  }
                  case ZOMBIE: {
                     user.addZomKill(1);
                     break;
                  }
                  //Cops
                  case PIG_ZOMBIE: {
                     user.addNPCCopKill(1);
                     break;
                  }
                  case ENDERMAN: {
                     user.addEndKill(1);
                     break;
                  }
                  //Counts both withers and skeletons
                  case SKELETON: {
                     if(((Skeleton) entity).getSkeletonType().equals(Skeleton.SkeletonType.WITHER))
                        user.addWithSkelKills(1);
                     else
                        user.addSkelKills(1);
                     break;
                  }
               }
               dropItems(eT, GenUtils.getRandInt(LootingUtils.getMinItemDropMob(), LootingUtils.getMaxItemDropMob())).forEach(i -> world.dropItem(entity.getLocation(), i));
            }
            catch(NullPointerException i){
               StringUtils.logFine("No loot for mod: " +entity.getType() + " : dropping nothing.");
            }
         }
         //Looting idea: armor and money from mobs, weapons ammo parts/attatchments and food from chests.
      }
   }

   private List<ItemStack> dropItems(EntityType e, int draw) throws NullPointerException{
      List<ItemStack> loot = new ArrayList<>(draw);
      switch(e) {
         case VILLAGER:
            loot.add(ItemUtils.createMoneyItem(LootingUtils.getVillMinMonDrop(), LootingUtils.getVillMaxMonDrop()));
               for(int i = 0; i < draw; i++)
                  loot.add(LootingUtils.getRandom("Villager").getRandom());
            break;
         case ZOMBIE:
            loot.add(ItemUtils.createMoneyItem(LootingUtils.getZomMinMonDrop(), LootingUtils.getZomMaxMonDrop()));
               for(int i = 0; i < draw; i++)
                  loot.add(LootingUtils.getRandom("Zombie").getRandom());
            break;
         //Cops
         case PIG_ZOMBIE:
            loot.add(ItemUtils.createMoneyItem(LootingUtils.getPigCopMinMonDrop(), LootingUtils.getPigCopMaxMonDrop()));
               for(int i = 0; i < draw; i++)
                  loot.add(LootingUtils.getRandom("Cop").getRandom());
            break;
         case ENDERMAN:
            loot.add(ItemUtils.createMoneyItem(LootingUtils.getEndMinMonDrop(), LootingUtils.getEndMaxMonDrop()));
               for(int i = 0; i < draw; i++)
                  loot.add(LootingUtils.getRandom("Enderman").getRandom());
            break;
         //Counts both withers and skeletons
         case SKELETON:
            loot.add(ItemUtils.createMoneyItem(LootingUtils.getSkelMinMonDrop(), LootingUtils.getSkelMaxMonDrop()));
               for(int i = 0; i < draw; i++)
                  loot.add(LootingUtils.getRandom("Skeleton").getRandom());
      }
      return loot;
   }
}
