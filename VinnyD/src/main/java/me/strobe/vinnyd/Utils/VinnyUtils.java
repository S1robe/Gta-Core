package me.strobe.vinnyd.Utils;

import lombok.Getter;
import me.Strobe.Core.Utils.*;
import me.Strobe.Core.Utils.Looting.WeightedRandomBag;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class VinnyUtils {

   private static CustomFile stockFile;
   private static FileConfiguration stockFileConfig;
   private static CustomFile upgradesFile;
   private static FileConfiguration upgradesFileConfig;
   private static CustomFile mainFile;
   private static FileConfiguration mainFileConfig;
   @Getter private static boolean canUpgrade;

   private static UUID vinnyID;
   private static Location location;
   private static long timeVinnySpawned;
   private static byte numCanSpawn;
   private static byte numHasSpawned;
   private static byte numStockToSell;

   @Getter private static VinnyD vinny;
   private static NPC npc;

   private VinnyUtils(){}

   public static void init(boolean isUpgrading){
      Main m             = Main.getMain();
      stockFile          = m.getStock();
      stockFile.reloadCustomConfig();
      stockFileConfig    = stockFile.getCustomConfig();
      upgradesFile       = m.getUpgrades();
      upgradesFile.reloadCustomConfig();
      upgradesFileConfig = upgradesFile.getCustomConfig();
      mainFile           = m.getConfig();
      mainFileConfig     = mainFile.getCustomConfig();
      canUpgrade         = isUpgrading;

      vinnyID            = UUID.fromString(mainFileConfig.getString("id"));
      location           = RegionUtils.locationDeserializer(mainFileConfig.getString("location"));
      timeVinnySpawned   = mainFileConfig.getLong("timeSpawnedAt");
      numCanSpawn        = (byte) mainFileConfig.getInt("numCanSpawn");
      numHasSpawned      = (byte) mainFileConfig.getInt("numHasSpawned");
      numStockToSell     = (byte) mainFileConfig.getInt("numStockToSell");
   }

   public static void spawnVinny(){
      npc = CitizensAPI.getNPCRegistry().getByUniqueIdGlobal(vinnyID);
      npc.spawn(location);
      vinny = new VinnyD();
      shoutVinnyArrival();
      numHasSpawned++;
      vinnyDespawnChecker.runTaskTimer(Main.getMain(), 0, 6000L);
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

   private static void shoutVinnyArrival(){
      User.sendAllUsersMessage("&a&lVinnyD&7: Hey all! Im in town, ive got about a 20 hours to spare. Come see me at spawn.");
   }
   private static void shoutVinnyDepart(){
      User.sendAllUsersMessage("&a&lVinnyD&7: Gotta run! Ill be back later.");
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

   public static class VinnyD{
      private final WeightedRandomBag<StockItem> stock = new WeightedRandomBag<>();
      @Getter private final List<Upgrade> upgrades     = new ArrayList<>();
      @Getter private List<StockItem> activeStock      = new ArrayList<>();

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
         saveActiveStock();
      }

      private void saveActiveStock(){
         stockFileConfig.set("Active", activeStock);
         stockFile.saveCustomConfig();
      }

      private void saveStock(){
         stockFileConfig.set("Stockpool", activeStock);
         stockFile.saveCustomConfig();
      }

      private void saveUpgrades(){
         upgradesFileConfig.set("Upgrades", activeStock);
         upgradesFile.saveCustomConfig();
      }

      public void performUpgrade(Upgrade u, Player p){
         PlayerUtils.takeMultipleSpecificItemsFromPlayer(p, u.getRequiredItems(), null, true);
         Main.getMain().getEcon().withdrawPlayer(p, u.getMoneyPrice());
         PlayerUtils.takeSpecificItemFromPlayer(p, ItemUtils.oddCurrency(1), u.getOddCurrencyPrice());
      }
      public void purchaseItem(StockItem i, Player p){
         Main.getMain().getEcon().withdrawPlayer(p, i.getMoneyPrice());
         PlayerUtils.takeSpecificItemFromPlayer(p, ItemUtils.oddCurrency(1), i.getOddCurrencyPrice());
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
         saveStock();
      }

      /**
       *
       * @param item the item to be removed
       * @apiNote this always reloads the stockpool.
       */
      public void removeEntryFromStock(StockItem item){
         stock.removeEntryByInner(item);
         rollNewVinnyLoot();
         saveStock();
      }

      public void showStock(Player p){

      }
      public void addUpgrade(Upgrade upgrade){
         upgrades.add(upgrade);
         saveUpgrades();
      }
      public void removeUpgrade(Upgrade upgrade){
         upgrades.remove(upgrade);
         saveUpgrades();
      }
      public void showUpgrades(Player p){}
      public boolean doesPlayerHaveEnoughForUpgrade(Upgrade u, Player p){
         return (Main.getMain().getEcon().getBalance(p) > u.getMoneyPrice())
                 && PlayerUtils.doesPlayerHaveEnoughOfAllItems(p, u.getRequiredItems(), null, true )
                 && PlayerUtils.doesPlayerHaveEnoughOfItem(p, ItemUtils.oddCurrency(1), u.getOddCurrencyPrice(), false);
      }
   }

}
