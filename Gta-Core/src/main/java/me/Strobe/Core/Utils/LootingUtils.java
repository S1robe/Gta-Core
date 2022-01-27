package me.Strobe.Core.Utils;

import me.Strobe.Core.Main;
import me.Strobe.Core.Utils.Looting.LootItem;
import me.Strobe.Core.Utils.Looting.WeightedRandomBag;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
public final class LootingUtils {


   @lombok.Getter private static double zomMinMonDrop;

   @lombok.Getter private static double zomMaxMonDrop;

   @lombok.Getter private static double skelMinMonDrop;

   @lombok.Getter private static double skelMaxMonDrop;

   @lombok.Getter private static double villMinMonDrop;

   @lombok.Getter private static double villMaxMonDrop;

   @lombok.Getter private static double pigCopMinMonDrop;

   @lombok.Getter private static double pigCopMaxMonDrop;

   @lombok.Getter private static double endMinMonDrop;

   @lombok.Getter private static double endMaxMonDrop;

   private static List<String> lootActiveWorlds = new ArrayList<>();
   private static List<String> mobActiveWorlds = new ArrayList<>();

   /**
    * Cooldown time in minutes to refresh chests
    */

   @lombok.Getter private static long chestResetTime; //Cooldown time in mintues
   /**
    * Minimum amount of items a chest can spawn
    */

   @lombok.Getter private static int minItemChestdrop;
   /**
    * Max amount of items a chest can spawn
    */

   @lombok.Getter private static int maxItemChestDrop;
   /**
    * min amount of items a mob can drop
    */

   @lombok.Getter private static int minItemDropMob;
   /**
    * Max amount of items a mob can drop
    */

   @lombok.Getter private static int maxItemDropMob;
   /**
    * loot table for the chests
    */
   private static final WeightedRandomBag<LootItem> lootTable = new WeightedRandomBag<LootItem>();
   /**
    * loot table for zombies
    */
   private static final WeightedRandomBag<LootItem> zomLootPool = new WeightedRandomBag<LootItem>();
   /**
    * loot table for skeletons
    */
   private static final WeightedRandomBag<LootItem> skelLootPool = new WeightedRandomBag<LootItem>();
   /**
    * loot table for endermen
    */
   private static final WeightedRandomBag<LootItem> enderLootPool = new WeightedRandomBag<LootItem>();
   /**
    * loot table for villagers
    */
   private static final WeightedRandomBag<LootItem> villLootPool = new WeightedRandomBag<LootItem>();
   /**
    * loot table for cops
    */
   private static final WeightedRandomBag<LootItem> copLootPool = new WeightedRandomBag<>();

   private LootingUtils() {}

   public static LootItem getRandom(String type) {
      switch(type) {
         case "Chest":
            return lootTable.getRandom();
         case "Zombie":
            return zomLootPool.getRandom();
         case "Skeleton":
            return skelLootPool.getRandom();
         case "Enderman":
            return enderLootPool.getRandom();
         case "Villager":
            return villLootPool.getRandom();
         case "Cop":
            return copLootPool.getRandom();
      }
      return null;
   }

   public static List<LootItem> getLootList(String type) {
      switch(type) {
         case "Chest":
            return lootTable.getInnerObjects();
         case "Zombie":
            return zomLootPool.getInnerObjects();
         case "Skeleton":
            return skelLootPool.getInnerObjects();
         case "Enderman":
            return enderLootPool.getInnerObjects();
         case "Villager":
            return villLootPool.getInnerObjects();
         case "Cop":
            return copLootPool.getInnerObjects();
      }
      return new ArrayList<>();
   }

   public static void placeRandomlyInChest(Inventory inv, List<ItemStack> loot) {
      Random r = new Random();
      for(int i = 0; i < loot.size() && i < inv.getSize(); i++) {
         int slot = r.nextInt(inv.getSize());
         while(inv.getItem(slot) != null)
            slot = r.nextInt(inv.getSize());
         inv.setItem(slot, loot.get(i));
      }
   }

   public static void removeItemFromLootPoolByDisplay(String type, ItemStack displayItem) {
      switch(type) {
         case "Chest":
            for(LootItem object : lootTable.getInnerObjects())
               if(object.getDisplayItem().equals(displayItem))
                  lootTable.removeEntryByInner(object);
            break;
         case "Zombie":
            for(LootItem object : zomLootPool.getInnerObjects())
               if(object.getDisplayItem().equals(displayItem))
                  skelLootPool.removeEntryByInner(object);
            break;
         case "Skeleton":
            for(LootItem object : skelLootPool.getInnerObjects())
               if(object.getDisplayItem().equals(displayItem))
                  skelLootPool.removeEntryByInner(object);
            break;
         case "Enderman":
            for(LootItem object : enderLootPool.getInnerObjects())
               if(object.getDisplayItem().equals(displayItem))
                  enderLootPool.removeEntryByInner(object);
            break;
         case "Villager":
            for(LootItem object : villLootPool.getInnerObjects())
               if(object.getDisplayItem().equals(displayItem))
                  villLootPool.removeEntryByInner(object);
            break;
         case "Cop":
            for(LootItem object : copLootPool.getInnerObjects())
               if(object.getDisplayItem().equals(displayItem))
                  copLootPool.removeEntryByInner(object);
            break;
      }
   }

   public static void removeItemFromLootPoolByRealItem(String type, LootItem item) {
      switch(type){
         case "Chest":
            lootTable.removeEntryByInner(item);
            break;
         case "Zombie":
            zomLootPool.removeEntryByInner(item);
            break;
         case "Skeleton":
            skelLootPool.removeEntryByInner(item);
            break;
         case "Enderman":
            enderLootPool.removeEntryByInner(item);
            break;
         case "Villager":
            villLootPool.removeEntryByInner(item);
            break;
         case "Cop":
            copLootPool.removeEntryByInner(item);
      }
   }

   public static LootItem getItemFromLootPool(String type, ItemStack displayItem) {
      switch(type) {
         case "Chest":
            for(LootItem object : lootTable.getInnerObjects())
               if(object.getDisplayItem().equals(displayItem))
                  return object;
         case "Zombie":
            for(LootItem object : zomLootPool.getInnerObjects())
               if(object.getDisplayItem().equals(displayItem))
                  return object;
         case "Skeleton":
            for(LootItem object : skelLootPool.getInnerObjects())
               if(object.getDisplayItem().equals(displayItem))
                  return object;
         case "Enderman":
            for(LootItem object : enderLootPool.getInnerObjects())
               if(object.getDisplayItem().equals(displayItem))
                  return object;
         case "Villager":
            for(LootItem object : villLootPool.getInnerObjects())
               if(object.getDisplayItem().equals(displayItem))
                  return object;
         case "Cop":
            for(LootItem object : copLootPool.getInnerObjects())
               if(object.getDisplayItem().equals(displayItem))
                  return object;
      }
      return null;
   }

   public static LootItem getItemByItem(String type, ItemStack item){
      switch(type) {
         case "Chest":
            for(LootItem object : lootTable.getInnerObjects())
               if(object.getItem().equals(item))
                  return object;
         case "Zombie":
            for(LootItem object : zomLootPool.getInnerObjects())
               if(object.getItem().equals(item))
                  return object;
         case "Skeleton":
            for(LootItem object : skelLootPool.getInnerObjects())
               if(object.getItem().equals(item))
                  return object;
         case "Enderman":
            for(LootItem object : enderLootPool.getInnerObjects())
               if(object.getItem().equals(item))
                  return object;
         case "Villager":
            for(LootItem object : villLootPool.getInnerObjects())
               if(object.getItem().equals(item))
                  return object;
         case "Cop":
            for(LootItem object : copLootPool.getInnerObjects())
               if(object.getItem().equals(item))
                  return object;
      }
      return null;
   }

   public static double getTotalBagWeight(String type){
      switch(type) {
         case "Chest":
            return lootTable.getAccumulatedWeight();
         case "Zombie":
            return zomLootPool.getAccumulatedWeight();
         case "Skeleton":
            return skelLootPool.getAccumulatedWeight();
         case "Enderman":
            return enderLootPool.getAccumulatedWeight();
         case "Villager":
            return villLootPool.getAccumulatedWeight();
         case "Cop":
            return copLootPool.getAccumulatedWeight();
      }
      return 0;
   }

   public static boolean addItemToLootPool(LootItem item, String type) {
      boolean result = false;
      switch(type) {
         case "Chest":
            result = lootTable.addEntry(item, item.getWeight());
            break;
         case "Zombie":
            result = zomLootPool.addEntry(item, item.getWeight());
            break;
         case "Skeleton":
            result = skelLootPool.addEntry(item, item.getWeight());
            break;
         case "Enderman":
            result = enderLootPool.addEntry(item, item.getWeight());
            break;
         case "Villager":
            result = villLootPool.addEntry(item, item.getWeight());
            break;
         case "Cop":
            result = copLootPool.addEntry(item, item.getWeight());
      }
      saveSpecificLootFile(type);
      return result;
   }

   public static void saveSpecificLootFile(String type)  {
      FileConfiguration f = Main.getMain().getLootFile().getCustomConfig();
      switch(type) {
         case "Zombie": {
            f.set(type+ ".Loot", zomLootPool.getInnerObjects());
            f.set(type+".minMon", zomMinMonDrop);
            f.set(type+".maxMon", zomMaxMonDrop);
            break;
         }
         case "Skeleton": {
            f.set(type+ ".Loot", skelLootPool.getInnerObjects());
            f.set(type+".minMon", skelMinMonDrop);
            f.set(type+".maxMon", skelMaxMonDrop);
            break;
         }
         case "Enderman": {
            f.set(type+ ".Loot", enderLootPool.getInnerObjects());
            f.set(type+".minMon", endMinMonDrop);
            f.set(type+".maxMon", endMaxMonDrop);
            break;
         }
         case "Villager": {
            f.set(type+ ".Loot", villLootPool.getInnerObjects());
            f.set(type+".minMon", villMinMonDrop);
            f.set(type+".maxMon", villMaxMonDrop);
            break;
         }
         case "Cop": {
            f.set(type+ ".Loot", copLootPool.getInnerObjects());
            f.set(type+".minMon", pigCopMinMonDrop);
            f.set(type+".maxMon", pigCopMaxMonDrop);
            break;
         }
         case "Chest": {
            f.set(type+ ".Loot", lootTable.getInnerObjects());
            Main.getMain().getLootFile().saveCustomConfig();
            return;
         }
      }
      Main.getMain().getLootFile().saveCustomConfig();
   }

   public static void reloadAllLoot(){
      lootTable.clear();
      zomLootPool.clear();
      skelLootPool.clear();
      enderLootPool.clear();
      villLootPool.clear();
      copLootPool.clear();
      Main.getMain().getLootFile().reloadCustomConfig();
      loadLoot("Chest");
      loadLoot("Zombie");
      loadLoot("Skeleton");
      loadLoot("Enderman");
      loadLoot("Villager");
      loadLoot("Cop");
   }

   public static void reloadSpecificLoot(String type){
      switch(type){
         case "Chest":
            lootTable.clear();
            break;
         case "Zombie":
            zomLootPool.clear();
            break;
         case "Skeleton":
            skelLootPool.clear();
            break;
         case "Enderman":
            enderLootPool.clear();
            break;
         case "Villager":
            villLootPool.clear();
            break;
         case "Cop":
            copLootPool.clear();
            break;
      }
      Main.getMain().getLootFile().reloadCustomConfig();
      loadLoot(type);
   }

   private static void loadLoot(String type) {
      FileConfiguration f = Main.getMain().getLootFile().getCustomConfig();
      if(f.isSet(type +".Loot"))
         f.getList(type+ ".Loot").forEach(map -> {
            LootItem y = (LootItem) map;
            switch(type) {
               case "Chest":
                  lootTable.addEntry(y, y.getWeight());
                  break;
               case "Zombie":
                  zomLootPool.addEntry(y, y.getWeight());
                  break;
               case "Skeleton":
                  skelLootPool.addEntry(y, y.getWeight());
                  break;
               case "Enderman":
                  enderLootPool.addEntry(y, y.getWeight());
                  break;
               case "Villager":
                  villLootPool.addEntry(y, y.getWeight());
                  break;
               case "Cop":
                  copLootPool.addEntry(y, y.getWeight());
            }
         });
   }







   public static void loadAllLoot() {
      loadLoot("Chest");
      loadLoot("Zombie");
      loadLoot("Skeleton");
      loadLoot( "Enderman");
      loadLoot("Villager");
      loadLoot("Cop");
   }

   public static void saveAllLoot(){
      saveSpecificLootFile("Chest");
      saveSpecificLootFile("Zombie");
      saveSpecificLootFile("Skeleton");
      saveSpecificLootFile("Villager");
      saveSpecificLootFile("Cop");
      saveSpecificLootFile("Enderman");
   }

   public static void loadActiveWorlds(){
      FileConfiguration f = Main.getMain().getMainDataFile().getCustomConfig();
      lootActiveWorlds = f.getStringList("loot-enabled-worlds");
      mobActiveWorlds = f.getStringList("mob-enabled-worlds");
   }

   public static void saveActiveWorlds(){
      FileConfiguration f = Main.getMain().getMainDataFile().getCustomConfig();
      f.set("loot-enabled-worlds", lootActiveWorlds);
      f.set("mob-enabled-worlds", mobActiveWorlds);
      Main.getMain().getMainDataFile().saveCustomConfig();
   }

   public static boolean isWorldLootActive(World world){
      return lootActiveWorlds.contains(world.getName());
   }

   public static boolean isWorldMobActive(World world){
      return lootActiveWorlds.contains(world.getName());
   }

   public static void lootActivateWorld(World world){
      if(lootActiveWorlds.contains(world.getName())) return;
      lootActiveWorlds.add(world.getName());
   }

   public static void lootDeactivateWorld(World world){
      lootActiveWorlds.remove(world.getName());
   }

   public static void mobActivateWorld(World world){
      if(lootActiveWorlds.contains(world.getName())) return;
      lootActiveWorlds.add(world.getName());
   }

   public static void mobDeactivateWorld(World world){
      lootActiveWorlds.remove(world.getName());
   }

   public static void setChestResetTime(int timeInMinutes){
      chestResetTime = timeInMinutes * 60000L;
   }

   public static void setMinDrop(String type, int min){
      switch(type){
         case "Zombie":
            zomMinMonDrop = Math.max(0, min);
            return;
         case "Skeleton":
            skelMinMonDrop = Math.max(0, min);
            return;
         case "Enderman":
            endMinMonDrop = Math.max(0, min);
            return;
         case "Villager":
            villMinMonDrop = Math.max(0, min);
            return;
         case "Cop":
            pigCopMinMonDrop = Math.max(0, min);
            return;
         case "Chest":
            minItemChestdrop = Math.max(0, min);
      }
   }

   public static void setMaxDrop(String type, int max){
      switch(type){
         case "Zombie":
            zomMaxMonDrop = Math.max(zomMinMonDrop, max);
            return;
         case "Skeleton":
            skelMaxMonDrop = Math.max(skelMinMonDrop, max);
            return;
         case "Enderman":
            endMaxMonDrop = Math.max(endMinMonDrop, max);
            return;
         case "Villager":
            villMaxMonDrop = Math.max(villMinMonDrop, max);
            return;
         case "Cop":
            pigCopMaxMonDrop = Math.max(pigCopMinMonDrop, max);
            return;
         case "Chest":
            maxItemChestDrop = Math.max(minItemChestdrop, max);
      }
   }

   public static void init(){
      loadAllLoot();
      FileConfiguration f = Main.getMain().getMainDataFile().getCustomConfig();
      chestResetTime      = f.getInt("chest-reset-time") * 60000L;
      maxItemDropMob      = f.getInt("max-mob-drop");
      minItemDropMob      = f.getInt("min-mob-drop");
                        f = Main.getMain().getLootFile().getCustomConfig();
      minItemChestdrop    = f.getInt("Chest.min-chest-drop");
      maxItemChestDrop    = f.getInt("Chest.max-chest-drop");
      zomMinMonDrop       = f.getDouble("Zombie.minMon");
      zomMaxMonDrop       = f.getDouble("Zombie.maxMon");
      skelMinMonDrop      = f.getDouble("Skeleton.minMon");
      skelMaxMonDrop      = f.getDouble("Skeleton.maxMon");
      villMinMonDrop      = f.getDouble("Villager.minMon");
      villMaxMonDrop      = f.getDouble("Villager.maxMon");
      endMinMonDrop       = f.getDouble("Enderman.minMon");
      endMaxMonDrop       = f.getDouble("Enderman.maxMon");
      pigCopMinMonDrop    = f.getDouble("Cop.minMon");
      pigCopMaxMonDrop    = f.getDouble("Cop.maxMon");
      loadActiveWorlds();
   }
}
