package me.Strobe.Core.Events;

import me.Strobe.Core.Utils.User;
import me.Strobe.Core.Utils.GenUtils;
import me.Strobe.Core.Utils.LootingUtils;
import me.Strobe.Core.Utils.StringUtils;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
               switch(entity.getType()) {
                  case WITCH:
                  case VILLAGER: {
                     user.modVillagerKill(1);
                     dropItems(EntityType.VILLAGER, GenUtils.getRandInt(LootingUtils.minItemDropMob, LootingUtils.maxItemDropMob)).forEach(i -> world.dropItem(entity.getLocation(), i));
                     break;
                  }
                  case ZOMBIE: {
                     user.modZomKill(1);
                     dropItems(EntityType.ZOMBIE, GenUtils.getRandInt(LootingUtils.minItemDropMob, LootingUtils.maxItemDropMob)).forEach(i -> world.dropItem(entity.getLocation(), i));
                     break;
                  }
                  //Cops
                  case PIG_ZOMBIE: {
                     user.modNPCCopKills(1);
                     dropItems(EntityType.PIG_ZOMBIE, GenUtils.getRandInt(LootingUtils.minItemDropMob, LootingUtils.maxItemDropMob)).forEach(i -> world.dropItem(entity.getLocation(), i));
                     break;
                  }
                  case ENDERMAN: {
                     user.modEndKill(1);
                     dropItems(EntityType.ENDERMAN, GenUtils.getRandInt(LootingUtils.minItemDropMob, LootingUtils.maxItemDropMob)).forEach(i -> world.dropItem(entity.getLocation(), i));
                     break;
                  }
                  //Counts both withers and skeletons
                  case SKELETON: {
                     if(((Skeleton) entity).getSkeletonType().equals(Skeleton.SkeletonType.WITHER))
                        user.modWithSkelKill(1);
                     else
                        user.modSkelKill(1);
                     dropItems(EntityType.SKELETON, GenUtils.getRandInt(LootingUtils.minItemDropMob, LootingUtils.maxItemDropMob)).forEach(i -> world.dropItem(entity.getLocation(), i));
                     break;
                  }
               }
            }
            catch(NullPointerException i){
               StringUtils.logFine(i.getMessage());
            }
         }
         //Looting idea: armor and money from mobs, weapons ammo parts/attatchments and food from chests.
      }
   }

   private List<ItemStack> dropItems(EntityType e, int draw) throws NullPointerException{
      List<ItemStack> loot = new ArrayList<>(draw);
      switch(e) {
         case VILLAGER:
            loot.add(LootingUtils.createMoneyItem(LootingUtils.villMinMonDrop, LootingUtils.villMaxMonDrop));
               for(int i = 0; i < draw; i++)
                  loot.add(LootingUtils.getRandom("Villager").getRandom());
            break;
         case ZOMBIE:
            loot.add(LootingUtils.createMoneyItem(LootingUtils.zomMinMonDrop, LootingUtils.zomMaxMonDrop));
               for(int i = 0; i < draw; i++)
                  loot.add(LootingUtils.getRandom("Zombie").getRandom());
            break;
         //Cops
         case PIG_ZOMBIE:
            loot.add(LootingUtils.createMoneyItem(LootingUtils.pigCopMinMonDrop, LootingUtils.pigCopMaxMonDrop));
               for(int i = 0; i < draw; i++)
                  loot.add(LootingUtils.getRandom("Cop").getRandom());
            break;
//         case ENDERMAN:
//               for(int i = 0; i < draw; i++)
//                  loot.add(LootingUtils.getRandom("Enderman").getRandom());
//            break;
         //Counts both withers and skeletons
         case SKELETON:
            loot.add(LootingUtils.createMoneyItem(LootingUtils.skelMinMonDrop, LootingUtils.skelMaxMonDrop));
               for(int i = 0; i < draw; i++)
                  loot.add(LootingUtils.getRandom("Skeleton").getRandom());
      }
      return loot;
   }
}
