package me.Strobe.Housing.Utils;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.Strobe.Core.Utils.ItemUtils;
import me.Strobe.Core.Utils.RegionUtils;
import me.Strobe.Core.Utils.User;
import me.Strobe.Housing.House;
import me.Strobe.Housing.Main;
import me.Strobe.Housing.Utils.Displays.GUIS;
import me.Strobe.Housing.Utils.Files.CustomFile;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class HouseUtils {

   public static int maxNumDays;
   private static final List<House> unownedHouses = new ArrayList<>();
   private static final List<House> ownedHouses = new ArrayList<>();
   private static CustomFile houseFile;
   private static FileConfiguration houseConfig;


   //File related Methods ---------------------------
   public static void saveAllOwnedHouses() {
      ownedHouses.forEach(h -> houseConfig.set(h.getName(), h));
      houseFile.saveCustomConfig();
   }
   public static void saveAllHouses() {
      ownedHouses.forEach(h -> houseConfig.set(h.getName(), h));
      unownedHouses.forEach(h -> houseConfig.set(h.getName(), h));
      houseFile.saveCustomConfig();
      unownedHouses.clear();
   }
   public static void loadAllHouses() {
      houseConfig.getKeys(false).forEach(id -> {
         House h = (House) houseConfig.get(id);
         if(h.isOwned())
            ownedHouses.add(h);
         else
            unownedHouses.add(h);
         Main.getMain().getLogger().log(Level.INFO, "Loaded house: " + h.getName());
      });
   }
   public static void loadAllOwnedHouses() {
      houseConfig.getKeys(false).forEach(id -> {
         House h = (House) houseConfig.get(id);
         if(h.isOwned())
            ownedHouses.add(h);
         Main.getMain().getLogger().log(Level.INFO, "Loaded house: " + h.getName());
      });
   }
   public static void loadSpecificHouse(String regionName) {
      FileConfiguration f = houseFile.getCustomConfig();
      House h = f.isSet(regionName)? (House) f.get(regionName) : null;
      if(h == null)
         me.Strobe.Core.Utils.StringUtils.logSevere("The house " + regionName + " does not exist or may not be formatted properly");
      else {
         if(h.isOwned())
            ownedHouses.add(h);
         else
            unownedHouses.add(h);
         me.Strobe.Core.Utils.StringUtils.logInfo("Loaded house: " + regionName);
      }
   }
   private static void saveSpecificHouse(House h) {
      FileConfiguration f = houseFile.getCustomConfig();
      f.set(h.getName(), h);
      houseFile.saveCustomConfig();
   }
   private static void deleteSpecificHouse(House h){
      FileConfiguration f = houseFile.getCustomConfig();
      f.set(h.getName(), null);
      houseFile.saveCustomConfig();
   }
   public static void reloadAll(){
      unownedHouses.clear();
      ownedHouses.clear();
      houseFile.reloadCustomConfig();
      loadAllHouses();
   }
   public static void reloadAllOwned(){
      ownedHouses.clear();
      houseFile.reloadCustomConfig();
      loadAllOwnedHouses();
   }
   public static void reloadSpecificHouse(House h){
      ownedHouses.remove(h);
      houseFile.reloadCustomConfig();
      String name = h.getName();
      loadSpecificHouse(name);
   }

   //Memory related methods ---------------------
   public static void addNewHouse(House h){
      saveSpecificHouse(h);
   }
   public static void deleteHouse(House h){
      ownedHouses.remove(h);
      Main.getWG().getRegionManager(h.getSignLocation().getWorld()).removeRegion(h.getName());
      deleteSpecificHouse(h);
   }
   public static void deleteHouse(String name){
      House h = getHouseByName(name);
      if(h != null) {
         deleteHouse(h);
      }
   }
   public static void buyHouse(Player p, House h) {
      h.buyHouse(User.getByPlayer(p));
      h.sendOwnerMessage(me.Strobe.Housing.Utils.StringUtils.purchaseHouse);
      ownedHouses.add(h);
      saveSpecificHouse(h);
      GUIS.home(p, h);
   }
   public static void unrentHouse(House h) {
      h.unrentHouse();
      ownedHouses.remove(h);
      saveSpecificHouse(h);
   }
   public static void extendHouse(Player p, House h, int amt){
      h.extendHouse(p, amt);
      if(!h.isPlayerAdded(p)) h.sendOwnerMessage(StringUtils.selfExtendedHouse);
      else h.sendAllMessage(StringUtils.memberExtendedHouseMsg.replace("{plr}", p.getName()).replace("{plr2}", h.getOwner().getName()));
      saveSpecificHouse(h);
   }
   public static void clearAll(){
      loadAllHouses();
      ownedHouses.forEach(h -> {
         h.setChestLocations(findChests(h.getSignLocation().getWorld(), h.getRegion()));
         h.clear();
      });
      unownedHouses.forEach(h -> {
         h.setChestLocations(findChests(h.getSignLocation().getWorld(), h.getRegion()));
         h.clear();
      });
      houseFile.saveCustomConfig();
   }
   public static void flagAll(String flag, String value){
      houseConfig.getKeys(false).forEach(h -> {
         World x = RegionUtils.locationDeserializer((String) houseConfig.get(h + ".sLocation")).getWorld();
         Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "rg flag " + h + " -w " + x.getName() + " " + flag + " " + value);
      });
   }

   //House specific methods
   public static House getHouseFromFileByName(String name) {
      House h = (House) houseConfig.get(name);
      if(h != null)
         return getHouseByName(name);
      else
         return null;
   }
   public static House getHouseByName(String name){
      for(House h : ownedHouses){
         if(h.getName().equalsIgnoreCase(name))
            return h;
      }
      return getHouseFromFileByName(name);
   }
   public static House getHouseByDisplayItem(Player p, ItemStack display) {
      for(House house : ownedHouses) {
         if(house.houseItem(p).isSimilar(display))
            return house;
      }
      return null;
   }
   public static House getHouseByPlayer(OfflinePlayer p) {
      for(House house : ownedHouses) {
         if(house.doesPlayerOwnHouse(p))
            return house;
      }
      return null;
   }
   public static House getHouseBySignBlock(Block x) {
      for(House house : ownedHouses) {
         if(house.getSignLocation().getBlock().equals(x))
            return house;
      }
      if(unownedHouses.size() - 1 >= 0)
         return unownedHouses.remove(unownedHouses.size() - 1);
      return null;
   }
   public static List<House> getHousesPlayerIsAddedTo(OfflinePlayer p) {
      ArrayList<House> housesAddedTo = new ArrayList<>();
      for(House house : ownedHouses) {
         if(house.isPlayerAdded(p))
            housesAddedTo.add(house);
      }
      return housesAddedTo;
   }
   public static House getHouseByChestLocation(Location chestLoc){
      for(House house : ownedHouses) {
         if(house.getChestLocations().contains(chestLoc))
            return house;
      }
      return null;
   }

   public static void updateHousesRunnable() {
      new BukkitRunnable() {
         @Override
         public void run() {
            ownedHouses.forEach(House::update);
         }
      }.runTaskTimer(Main.getMain(), 0L, 1L);
   }

   public static void updateItemInHouseGUI(Player p, ItemStack item, House h) {
      new BukkitRunnable() {
         @Override
         public void run() {
            if(item == null) {
               this.cancel();
               return;
            }
            if(p.getOpenInventory() != null ) {
               if(h != null) {
                  String[] x =  h.getTimeLeftString();
                  ItemUtils.changeLine(item, 0, x[0]);
                  ItemUtils.changeLine(item, 1, x[1]);
                  ItemUtils.changeLine(item, 2, x[2]);
                  ItemUtils.changeLine(item, 3, x[3]);
               }
            }
         }
      }.runTaskTimer(Main.getMain(), 1L, 1L);
   }

   public static boolean isHouseSign(Block b){
      Material m = b.getType();
      if(m.equals(Material.SIGN) || m.equals(Material.SIGN_POST) || m.equals(Material.WALL_SIGN)) {
         if(ownedHouses.stream().map(House::getSignLocation).anyMatch(e -> e.equals(b.getLocation()))) return true;
         Sign s = (Sign) b.getState();
         if(s.getLine(0).equals("FOR RENT") && !s.getLine(1).isEmpty()) {
            House h = (House) houseConfig.get(s.getLine(1));
            if(h != null)
              return unownedHouses.add(h);
         }
      }
      return false;
   }
   public static boolean isHouseBlockedChest(Block b){
      Material m = b.getType();
      return ((m.equals(Material.CHEST) || m.equals(Material.TRAPPED_CHEST)) && isChestBlocked((Chest)b.getState())) 
               && ownedHouses.stream().map(House::getChestLocations).anyMatch(list -> list.contains(b.getLocation()));

   }
   public static boolean isChestBlocked(Chest c){
      return !c.getLocation().add(0,1,0).getBlock().isEmpty();
   }
   public static List<Location> findChests(World world, ProtectedRegion pr){
      List<Location> chests = new ArrayList<>();
      Vector min = pr.getMinimumPoint();
      Vector max = pr.getMaximumPoint();
      for (int i = min.getBlockX(); i< max.getBlockX(); i++)
         for (int j = min.getBlockY(); j< max.getBlockY(); j++)
            for (int k = min.getBlockZ(); k< max.getBlockZ(); k++) {
               Block b = world.getBlockAt(i, j, k);
               if (b.getType().equals(Material.CHEST) ||b.getType().equals(Material.TRAPPED_CHEST) ){
                  chests.add(new Location(world,i,j,k));
               }
            }
      return chests;
   }
   
   public static void init(){
      houseFile = Main.getMain().getHousesFile();
      houseFile.reloadCustomConfig();
      houseConfig = houseFile.getCustomConfig();
      loadAllOwnedHouses();
   }
}
