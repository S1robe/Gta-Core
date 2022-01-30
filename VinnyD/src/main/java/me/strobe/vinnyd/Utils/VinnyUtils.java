package me.strobe.vinnyd.Utils;

import me.Strobe.Core.Utils.ItemUtils;
import me.Strobe.Core.Utils.Looting.WeightedRandomBag;
import me.Strobe.Core.Utils.PlayerUtils;
import me.Strobe.Core.Utils.StringUtils;
import me.Strobe.Core.Utils.User;
import me.strobe.vinnyd.Events.VinnyEvents;
import me.strobe.vinnyd.Files.CustomFile;
import me.strobe.vinnyd.Main;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class VinnyUtils {

   private static CustomFile stockFile;
   private static FileConfiguration stockFileConfig;
   private static CustomFile upgradesFile;
   private static FileConfiguration upgradesFileConfig;
   private static boolean canUpgrade;

   private static UUID vinnyID;
   private static Location location;
   private static long timeVinnySpawned;
   private static byte numCanSpawn;
   private static byte numHasSpawned;
   private static byte numStockToSell;

   private static VinnyD vinny;
   private static NPC npc;

   private VinnyUtils(){}

   public static void init(boolean isUpgrading){
      Main m = Main.getMain();
      stockFile = m.getStock();
      stockFile.reloadCustomConfig();
      stockFileConfig = stockFile.getCustomConfig();
      upgradesFile = m.getUpgrades();
      upgradesFile.reloadCustomConfig();
      upgradesFileConfig = upgradesFile.getCustomConfig();
      canUpgrade = isUpgrading;
   }

   public static void shoutVinnyArrival(){
      User.sendAllUsersMessage("&a&lVinnyD&7: Hey all! Im in town, ive got about a 20 hours to spare. Come see me at spawn.");
   }
   public static void shoutVinnyDepart(){
      User.sendAllUsersMessage("&a&lVinnyD&7: Gotta run! Ill be back later.");
   }

   public static void spawnVinny(){
      npc = CitizensAPI.getNPCRegistry().getByUniqueIdGlobal(vinnyID);
      npc.spawn(location);
      vinny = new VinnyD();
      shoutVinnyArrival();
      numHasSpawned++;
   }

   public static void despawnVinny(){
      npc.despawn();
      VinnyEvents.playersLookingAtVinny.forEach((p, i) -> {
         Bukkit.getScheduler().cancelTask(i);
         StringUtils.sendMessage(p, "&a&lVinnyD&7: Gotta run! See ya later.");
      });
      VinnyEvents.playersLookingAtVinny.clear();
      vinny = null;
      shoutVinnyDepart();
   }

   private static final BukkitRunnable vinnyDespawnChecker = new BukkitRunnable() {
         @Override
         public void run() {
            if(System.currentTimeMillis() - timeVinnySpawned >= 86400000){
               //Dont forget to check and count times spawned
               despawnVinny();
            }
         }
      };

   private static boolean doesVinnySpawnToday(){
      DayOfWeek today = DayOfWeek.from(LocalDate.now());// today
      if(numCanSpawn > numHasSpawned) {
         resetCounter(today); // resets if today is the last day of the week.
         if(7 - numCanSpawn > today.getValue())
            return true;
         return ThreadLocalRandom.current().nextBoolean();
      }
      resetCounter(today);
      return false;
   }

   private static void resetCounter(DayOfWeek today){
      if(today.getValue() == 7) {
         numHasSpawned = 0;
      }
   }

   private static class VinnyD{
      private WeightedRandomBag<StockItem> stock;
      private List<Upgrade> upgrades;
      private List<StockItem> activeStock;

      protected VinnyD(){
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

      public void performUpgrade(Upgrade u, Player p){
         PlayerUtils.takeItemsFromPlayer(p, u.getRequiredItems());
         Main.getMain().getEcon().withdrawPlayer(p, u.getMoneyPrice());
      }
      public void purchaseItem(StockItem i, Player p){
         Main.getMain().getEcon().withdrawPlayer(p, i.getMoneyPrice());
      }

      public void rollNewVinnyLoot(){
         npc.despawn();
         VinnyEvents.playersLookingAtVinny.forEach((p, i) -> {
            p.closeInventory();
            Bukkit.getScheduler().cancelTask(i);
         });
         VinnyEvents.playersLookingAtVinny.clear();
         stock.clear();
         loadStock();
         npc.spawn(location);
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

      public void showStock(Player p){

      }
      public void addUpgrade(Upgrade upgrade){
         upgrades.add(upgrade);
      }
      public void removeUpgrade(Upgrade upgrade){
         upgrades.remove(upgrade);
      }
      public void showUpgrades(Player p){}
      public boolean doesPlayerHaveEnoughForUpgrade(Upgrade u, Player p){
         return (Main.getMain().getEcon().getBalance(p) > u.getMoneyPrice())
                && PlayerUtils.doesPlayerHaveItems(p, ItemUtils.oddCurrency(u.getOddCurrencyPrice()))
                 && PlayerUtils.doesPlayerHaveItems(p, u.getRequiredItems());
      }





   }

}
