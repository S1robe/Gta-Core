package me.Strobe.Core.Utils;

import me.Strobe.Core.Main;
import me.Strobe.Core.Utils.Looting.LootItem;
import me.Strobe.Core.Utils.Looting.WeightedRandomBag;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class LootingUtils {

   public static double zomMinMonDrop;
   public static double zomMaxMonDrop;
   public static double skelMinMonDrop;
   public static double skelMaxMonDrop;
   public static double villMinMonDrop;
   public static double villMaxMonDrop;
   public static double pigCopMinMonDrop;
   public static double pigCopMaxMonDrop;
   public static double endMinMonDrop;
   public static double endMaxMonDrop;

   private static List<String> lootActiveWorlds = new ArrayList<>();
   private static List<String> mobActiveWorlds = new ArrayList<>();

   /**
    * Cooldown time in minutes to refresh chests
    */
   public static long chestResetTime; //Cooldown time in mintues
   /**
    * Minimum amount of items a chest can spawn
    */
   public static int minItemChestdrop;
   /**
    * Max amount of items a chest can spawn
    */
   public static int maxItemChestDrop;
   /**
    * min amount of items a mob can drop
    */
   public static int minItemDropMob;
   /**
    * Max amount of items a mob can drop
    */
   public static int maxItemDropMob;
   /**
    * loot table for the chests
    */
   private static WeightedRandomBag lootTable = new WeightedRandomBag();
   /**
    * loot table for zombies
    */
   private static WeightedRandomBag zomLootPool = new WeightedRandomBag();
   /**
    * loot table for skeletons
    */
   private static WeightedRandomBag skelLootPool = new WeightedRandomBag();
   /**
    * loot table for endermen
    */
   private static WeightedRandomBag enderLootPool = new WeightedRandomBag();
   /**
    * loot table for villagers
    */
   private static WeightedRandomBag villLootPool = new WeightedRandomBag();
   /**
    * loot table for cops
    */
   private static WeightedRandomBag copLootPool = new WeightedRandomBag();

   private LootingUtils() {}

   //TODO figure out why the serialization of loot items is weird.
   public static LootItem getRandom(String type) {
      switch(type) {
         case "Loot":
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
         case "Loot":
            return lootTable.innerObjects;
         case "Zombie":
            return zomLootPool.innerObjects;
         case "Skeleton":
            return skelLootPool.innerObjects;
         case "Enderman":
            return enderLootPool.innerObjects;
         case "Villager":
            return villLootPool.innerObjects;
         case "Cop":
            return copLootPool.innerObjects;
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
         case "Loot":
            lootTable.removeEntry(displayItem);
            break;
         case "Zombie":
            zomLootPool.removeEntry(displayItem);
            break;
         case "Skeleton":
            skelLootPool.removeEntry(displayItem);
            break;
         case "Enderman":
            enderLootPool.removeEntry(displayItem);
            break;
         case "Villager":
            villLootPool.removeEntry(displayItem);
            break;
         case "Cop":
            copLootPool.removeEntry(displayItem);
            break;
      }
   }

   public static void removeItemFromLootPoolByRealItem(String type, LootItem item) {
      switch(type) {
         case "Loot":
            lootTable.removeEntry(item);
            break;
         case "Zombie":
            zomLootPool.removeEntry(item);
            break;
         case "Skeleton":
            skelLootPool.removeEntry(item);
            break;
         case "Enderman":
            enderLootPool.removeEntry(item);
            break;
         case "Villager":
            villLootPool.removeEntry(item);
            break;
         case "Cop":
            copLootPool.removeEntry(item);
            break;
      }
   }

   public static LootItem getItemFromLootPool(String type, ItemStack displayItem) {
      switch(type) {
         case "Loot":
            return lootTable.getItemByDisplay(displayItem);
         case "Zombie":
            return zomLootPool.getItemByDisplay(displayItem);
         case "Skeleton":
            return skelLootPool.getItemByDisplay(displayItem);
         case "Enderman":
            return enderLootPool.getItemByDisplay(displayItem);
         case "Villager":
            return villLootPool.getItemByDisplay(displayItem);
         case "Cop":
            return copLootPool.getItemByDisplay(displayItem);
      }
      return null;
   }

   public static LootItem getItemByItem(String type, ItemStack item){
      switch(type) {
         case "Loot":
            return lootTable.getItemByRep(item);
         case "Zombie":
            return zomLootPool.getItemByRep(item);
         case "Skeleton":
            return skelLootPool.getItemByRep(item);
         case "Enderman":
            return enderLootPool.getItemByRep(item);
         case "Villager":
            return villLootPool.getItemByRep(item);
         case "Cop":
            return copLootPool.getItemByRep(item);
      }
      return null;
   }

   public static double getTotalBagWeight(String type){
      switch(type) {
         case "Loot":
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
         case "Loot":
            result = lootTable.addEntry(item);
            break;
         case "Zombie":
            result = zomLootPool.addEntry(item);
            break;
         case "Skeleton":
            result = skelLootPool.addEntry(item);
            break;
         case "Enderman":
            result = enderLootPool.addEntry(item);
            break;
         case "Villager":
            result = villLootPool.addEntry(item);
            break;
         case "Cop":
            result = copLootPool.addEntry(item);
      }
      saveSpecificLootFile(type);
      return result;
   }

   public static void saveSpecificLootFile(String type)  {
      FileConfiguration f = Main.getMain().getLootFile().getCustomConfig();
      switch(type) {
         case "Zombie": {
            f.set(type+ ".Loot", zomLootPool.innerObjects);
            f.set(type+".minMon", zomMinMonDrop);
            f.set(type+".maxMon", zomMaxMonDrop);
            break;
         }
         case "Skeleton": {
            f.set(type+ ".Loot", skelLootPool.innerObjects);
            f.set(type+".minMon", skelMinMonDrop);
            f.set(type+".maxMon", skelMaxMonDrop);
            break;
         }
//         case "Enderman": {
//            f.set(type+ ".Loot", enderLootPool.innerObjects);
//            f.set(type+".minMon", MinMonDrop);
//            f.set(type+".maxMon", zomMinMonDrop);
//            break;
//         }
         case "Villager": {
            f.set(type+ ".Loot", villLootPool.innerObjects);
            f.set(type+".minMon", villMinMonDrop);
            f.set(type+".maxMon", villMaxMonDrop);
            break;
         }
         case "Cop": {
            f.set(type+ ".Loot", copLootPool.innerObjects);
            f.set(type+".minMon", pigCopMinMonDrop);
            f.set(type+".maxMon", pigCopMaxMonDrop);
            break;
         }
         case "Loot": {
            Main.getMain().getLootFile().getCustomConfig().set(type+ ".Loot", lootTable.innerObjects);
            Main.getMain().getLootFile().saveCustomConfig();
            return;
         }
      }
      Main.getMain().getLootFile().saveCustomConfig();
   }

   public static void reloadMobLoot() {
      zomLootPool.clear();
      skelLootPool.clear();
      //enderLootPool.clear();
      villLootPool.clear();
      copLootPool.clear();
      loadLoot("Zombie");
      loadLoot("Skeleton");
      //loadLoo, "Enderman");
      loadLoot("Villager");
      loadLoot("Cop");
   }

   private static void loadLoot(String type) {
      FileConfiguration f = Main.getMain().getLootFile().getCustomConfig();
      if(f.isSet(type +".Loot"))
         f.getList(type+ ".Loot").forEach(map -> {
            LootItem y = (LootItem) map;
            switch(type) {
               case "Loot":
                  lootTable.addEntry(y);
                  break;
               case "Zombie":
                  zomLootPool.addEntry(y);
                  break;
               case "Skeleton":
                  skelLootPool.addEntry(y);
                  break;
               case "Enderman":
                  enderLootPool.addEntry(y);
                  break;
               case "Villager":
                  villLootPool.addEntry(y);
                  break;
               case "Cop":
                  copLootPool.addEntry(y);
            }
         });
   }

   public static void loadAllLoot() {
      loadLoot("Loot");
      loadLoot("Zombie");
      loadLoot("Skeleton");
      //loadLoot(f, "Enderman");
      loadLoot("Villager");
      loadLoot("Cop");
   }

   public static void saveAllLoot(){
      saveSpecificLootFile("Loot");
      saveSpecificLootFile("Zombie");
      saveSpecificLootFile("Skeleton");
      saveSpecificLootFile("Villager");
      saveSpecificLootFile("Cop");
      //saveSpecificLootFile("Enderman");
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

   public static void reloadSpecificMobLoot(String type) {
      switch(type) {
         case "Zombie": {
            zomLootPool.clear();
            break;
         }
         case "Skeleton": {
            skelLootPool.clear();
            break;
         }
         case "Enderman": {
            enderLootPool.clear();
            break;
         }
         case "Villager": {
            villLootPool.clear();
            break;
         }
         case "Cop": {
            copLootPool.clear();
            break;
         }
      }
      loadLoot(type);
   }

   public static void reloadChestLoot() {
      lootTable.clear();
      Main.getMain().getLootFile().reloadCustomConfig();
      loadLoot( "Loot");
   }

   public static ItemStack createMoneyItem(double min, double max){
      double amt = GenUtils.getRandDouble(min, max);
      return ItemUtils.createItem(Material.GOLD_NUGGET, 1, (byte)0, true, "" + amt);
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
}
