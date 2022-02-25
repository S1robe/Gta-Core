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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * This is VinnyUtils.java in Project: (Gta-Core) : But you already knew that
 * @author G.P of Prentice Productions
 * @version 1.0
 * Created On    : 2/24/2022 4:08 PM
 * Last Edit     : 2/24/2022 4:08 PM(Update Me!)
 * Time to Write : (Rough Estimate)
 *
 * (Class Description)
*/
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
   private static DayOfWeek today;
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
      today              = DayOfWeek.valueOf(mainFileConfig.getString("todayIs"));
      numCanSpawn        = (byte) mainFileConfig.getInt("numCanSpawn");
      numHasSpawned      = (byte) mainFileConfig.getInt("numHasSpawned");
      numStockToSell     = (byte) mainFileConfig.getInt("numStockToSell");
   }

   public static void spawnVinny(Location x){
      npc = CitizensAPI.getNPCRegistry().getByUniqueIdGlobal(vinnyID);

      if(x == null)
         npc.spawn(location);
      else {
         npc.spawn(x);
         location = x;
      }

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

   private static String[] getTimeLeft(){
      StringBuilder x = new StringBuilder();
      long timeLeftMS = System.currentTimeMillis() - timeVinnySpawned;
      long hold = TimeUnit.MILLISECONDS.toHours(timeLeftMS);
      x.append("&7Hours: &6").append(hold).append("\n");
      timeLeftMS -= TimeUnit.HOURS.toMillis(hold);
      x.append("&7Minutes: &6").append(TimeUnit.MILLISECONDS.toMinutes(timeLeftMS));
      return x.toString().split("\n");
   }

   public static boolean doesVinnySpawnToday(){
      DayOfWeek today = DayOfWeek.from(LocalDate.now());// today
      if(VinnyUtils.today == today) return false;
      if(numCanSpawn > numHasSpawned) {
         resetCounter(today); // resets if today is the last day of the week.
         if(7 - numCanSpawn > today.getValue()) {
            VinnyUtils.today = today;
            return true;
         }
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

   public static Upgrade getUpgradeByResult(ItemStack x){
      for(int i = 0; i < vinny.upgrades.size(); i++) {
         if(x.isSimilar(vinny.upgrades.get(i).getResultItem()))
            return vinny.upgrades.get(i);
      }
      return null;
   }

   public static StockItem getStockItemByDisplay(ItemStack x){
      for(int i = 0; i < vinny.activeStock.size(); i++) {
         if(x.isSimilar(vinny.activeStock.get(i).getRepresentation()))
            return vinny.activeStock.get(i);
      }
      return null;
   }

   public static void saveState(){
      mainFileConfig.set("id", vinnyID);
      mainFileConfig.set("location", RegionUtils.locationSerializer(location));
      mainFileConfig.set("timeSpawnedAt", timeVinnySpawned);
      mainFileConfig.set("todayIs", today);
      mainFileConfig.set("numCanSpawn", numCanSpawn);
      mainFileConfig.set("numHasSpawned", numHasSpawned);
      mainFileConfig.set("numStockToSell", numStockToSell);
      mainFile.saveCustomConfig();
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

      public boolean performUpgrade(Upgrade u, Player p){
         if(!doesPlayerHaveEnoughForUpgrade(u, p)) return false;
         PlayerUtils.takeMultipleSpecificItemsFromPlayer(p, u.getRequiredItems(), null, true);
         Main.getMain().getEcon().withdrawPlayer(p, u.getMoneyPrice());
         PlayerUtils.takeSpecificItemFromPlayer(p, ItemUtils.oddCurrency(1), u.getOddCurrencyPrice());
         PlayerUtils.offerPlayerItem(p, u.getResultItem());
         return true;
      }
      public boolean purchaseItem(StockItem i, Player p){
         if(!doesPlayerHaveEnoughForStockItem(i, p)) return false;
         Main.getMain().getEcon().withdrawPlayer(p, i.getMoneyPrice());
         PlayerUtils.takeSpecificItemFromPlayer(p, ItemUtils.oddCurrency(1), i.getOddCurrencyPrice());
         PlayerUtils.offerPlayerItem(p, i.getActualItem());
         return true;
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
      public void removeEntryFromStock(int stockIndex){
         StockItem item = activeStock.get(stockIndex);
         stock.removeEntryByInner(item);
         rollNewVinnyLoot();
         saveStock();
      }

      public void showStock(Player p){
         //Special non-interactable of the sellable items GUI.
         //Displays all the valid stocks
      }
      public void addUpgrade(Upgrade upgrade){
         upgrades.add(upgrade);
         saveUpgrades();
      }
      public void removeUpgrade(Upgrade upgrade){
         upgrades.remove(upgrade);
         saveUpgrades();
      }
      public void removeUpgrade(int upgradeIndex){
         upgrades.remove(upgradeIndex);
         saveUpgrades();
      }
      public void showUpgrades(Player p){
         //Special non-interactable of the upgrades items GUI.
         //Displays all the valid upgrades
      }
      public boolean doesPlayerHaveEnoughForUpgrade(Upgrade u, Player p){
         return (Main.getMain().getEcon().getBalance(p) > u.getMoneyPrice())
                 && PlayerUtils.doesPlayerHaveEnoughOfAllItems(p, u.getRequiredItems(), null, true )
                 && PlayerUtils.doesPlayerHaveEnoughOfItem(p, ItemUtils.oddCurrency(1), u.getOddCurrencyPrice(), false);
      }
      public boolean doesPlayerHaveEnoughForStockItem(StockItem s, Player p){
         return (Main.getMain().getEcon().getBalance(p) > s.getMoneyPrice())
                 && PlayerUtils.doesPlayerHaveEnoughOfItem(p, ItemUtils.oddCurrency(1), s.getOddCurrencyPrice(), false);
      }
   }

   public static class VinnyRunnable extends BukkitRunnable{

      private final Inventory inv;
      private final int slotClockIsIn;

      VinnyRunnable( Inventory inv, int slotClockIsIn){
         this.inv = inv;
         this.slotClockIsIn = slotClockIsIn;
      }

      @Override
      public void run() {
         ItemUtils.applyLore(inv.getItem(slotClockIsIn), getTimeLeft());
      }
   }

}
