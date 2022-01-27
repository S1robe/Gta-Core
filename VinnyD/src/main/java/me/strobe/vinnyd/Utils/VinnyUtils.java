package me.strobe.vinnyd.Utils;

import me.Strobe.Core.Utils.Looting.WeightedRandomBag;
import me.strobe.vinnyd.Events.VinnyEvents;
import me.strobe.vinnyd.Files.CustomFile;
import me.strobe.vinnyd.Main;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.DayOfWeek;
import java.util.*;

public class VinnyUtils {

   private static CustomFile stockFile;
   private static FileConfiguration stockFileConfig;
   private static CustomFile upgradesFile;
   private static FileConfiguration upgradesFileConfig;

   private static UUID vinnyID;
   private static Location spawedAt;
   private static long timeVinnySpawned;
   private static short timeToSpawnAt; //might not evne use this and just have it spawn for a full 24 hours
   private static byte numCanSpawn;
   private static byte numHasSpawned;
   private static DayOfWeek beginningOfWeek;
   private static byte numStockToSell;

   private static VinnyD vinny;

   private VinnyUtils(){}

   public static void init(){
      Main m = Main.getMain();
      stockFile = m.getStock();
      stockFile.reloadCustomConfig();
      stockFileConfig = stockFile.getCustomConfig();
      upgradesFile = m.getUpgrades();
      upgradesFile.reloadCustomConfig();
      upgradesFileConfig = upgradesFile.getCustomConfig();
   }

   public static void shoutVinnyArrival(){}
   public static void shoutVinnyDepart(){}

   public static void spawnVinny(){}
   public static void despawnVinny(){}


   private static final BukkitRunnable vinnyDespawnChecker = new BukkitRunnable() {
         @Override
         public void run() {
            if(System.currentTimeMillis() - timeVinnySpawned >= 86400000){
               //Dont forget to check and count times spawned
               despawnVinny();
            }
         }
      };

   private static class VinnyD{
      private WeightedRandomBag<StockItem> stock;
      private final boolean canDoUpgrades;
      private List<Upgrade> upgrades;
      private List<StockItem> activeStock;

      private boolean isSpawned;

      /**
       * @param canUpgrade    This is whether or not he can upgrade weapons: determined at plugin load.
       */
      protected VinnyD(boolean canUpgrade){
         this.canDoUpgrades = canUpgrade;
         if(canUpgrade) loadUpgrades();
         loadStock();
      }

      private void loadUpgrades(){
         upgradesFileConfig.getList("Upgrades").forEach(map -> upgrades.add((Upgrade) map));
      }

      private void loadStock(){
         stockFileConfig.getList("Stockpool").forEach(map -> {
            StockItem s = (StockItem) map;
            stock.addEntry(s, s.getWeightToBeChosenForSale());
         });
         activeStock = stock.getRandomAmtUnique(numStockToSell);
      }

      public void performUpgrade(Upgrade u, Player p){}
      public void purchaseItem(StockItem i, Player p){}

      public void rollNewVinnyLoot(){
         VinnyEvents.playersLookingAtVinny.forEach((p, i) -> p.closeInventory());
         VinnyEvents.playersLookingAtVinny.clear();
         stock.clear();
         loadStock();
      }

      public void addEntryToStock(StockItem stock, boolean reload){
         this.stock.addEntry(stock, stock.getWeightToBeChosenForSale());
         if(reload)
            rollNewVinnyLoot();
      }

      /**
       *
       * @param item the item to be removed
       * @apiNote this always reloads the stockpool.
       */
      public void removeEntryFromStock(StockItem item){
         stock.removeEntryByInner(item);
         rollNewVinnyLoot();
      }

      public void showStock(Player p){}
      public void addUpgrade(Upgrade upgrade){}
      public void removeUpgrade(){}
      public void showUpgrades(Player p){}
      public boolean doesPlayerHaveEnoughForUpgrade(Player p){}

   }

}
