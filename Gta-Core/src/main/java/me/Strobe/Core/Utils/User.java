package me.Strobe.Core.Utils;

import lombok.Getter;
import lombok.Setter;
import me.Strobe.Core.Main;
import me.Strobe.Core.Utils.Displays.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

import java.io.File;
import java.io.IOException;
import java.util.*;

@SuppressWarnings("unchecked")
@Getter
@Setter
@SerializableAs("User")
public class User implements ConfigurationSerializable {

   public static final transient Map<UUID, User> users = new HashMap<>();
   //FileHandling data
   private transient File player_File;
   private transient YamlConfiguration fileConfig;

   //General Player information
   private String player_Name = "$";
   private UUID playerUUID;
   private transient OfflinePlayer PLAYER;
   private int currentBoard = 1;

   //Looting Game-Side
   private Map<String, Long> stringChestLocations = new HashMap<>();
   private transient Map<Location, Long> chestLocations = new HashMap<>();
   private transient Map<Location, List<ItemStack>> chestLoot = new HashMap<>();
   private transient volatile Location viewedChestLocation;

   //PVP Game-Side
   private int pvpDeaths = 0;
   private int pvpKills = 0;
   private int copKills = 0;
   private int killstreak = 0;
   private int wantedlevel = 0;
   private boolean isCop = false;
   private List<ItemStack> savedInventory = new ArrayList<>();
   private boolean attackedCop = false;
   private int trackerID = -1;
   private transient User tracked;
   private transient User trackedBy;

   //PVE Game-Side
   private int mobDeaths = 0;
   private int mobKills = 0;
   private int skeletonKills = 0;
   private int witherSkeletonKills = 0;
   private int zombieNormalKills = 0;
   private int endermanKills = 0;
   private int villagerKills = 0;
   private int npcCopKills = 0;

   private User(){}

   public static User userJoined(UUID u) {
      User user = null;
      File f = new File(Main.getMain().getDataFolder() + System.getProperty("file.separator") + "PlayerData", u + ".u");
      YamlConfiguration y = YamlConfiguration.loadConfiguration(f);
      if(f.exists() && y.isSet("Data")){
         user = (User) y.get("Data");
      }
      else{
         try {
            f.createNewFile();
            y = YamlConfiguration.loadConfiguration(f);
            user = new User();
         }
         catch(IOException e) {
            e.printStackTrace();
         }
      }
      user.setPLAYER(Bukkit.getPlayer(u));
      user.setPlayer_File(f);
      user.setFileConfig(y);
      user.setPlayer_Name(user.getPLAYER().getName());
      user.setPlayerUUID(u);
      users.putIfAbsent(u, user);
      return user;
   }

   public User(Map<String, Object> serialized){
      deserialize(serialized);
   }

   @Override
   public Map<String, Object> serialize() {
      Map<String, Object> map = new HashMap<>();
      map.put("currentBoard", currentBoard);
      chestLocations.forEach((loc ,lon)-> stringChestLocations.put(RegionUtils.locationSerializer(loc), lon));
      map.put("chestLocations", new ArrayList<>(stringChestLocations.keySet()));
      map.put("chestLocationTimes", new ArrayList<>(stringChestLocations.values()));

      map.put("chestLoot", new ArrayList<>(chestLoot.values()));

      map.put("pvpDeaths", pvpDeaths);
      map.put("pvpKills", pvpKills);
      map.put("copKills", copKills);
      map.put("killstreak", killstreak);
      map.put("wantedLevel", wantedlevel);
      map.put("isCop", isCop);
      map.put("savedInventory", savedInventory);
      map.put("attackedCop", attackedCop);
      map.put("mobDeaths", mobDeaths);
      map.put("mobKills", mobKills);
      map.put("skeletonKills", skeletonKills);
      map.put("witherSkeletonKills", witherSkeletonKills);
      map.put("zombieNormalKills", zombieNormalKills);
      map.put("endermanKills", endermanKills);
      map.put("villagerKills", villagerKills);
      map.put("npcCopKills", npcCopKills);
      return map;
   }

   public static User deserialize(Map<String, Object> serialized) {
      User u = new User();
      List<Location> locs = RegionUtils.locationDeserializer((List<String>) serialized.get("chestLocations"));
      List<Long> times = (List<Long>) serialized.get("chestLocationTimes");
      List<List<ItemStack>> loots = (List<List<ItemStack>>) serialized.get("chestLoot");
      createHashMaps(u ,locs, times, loots);
      u.currentBoard = (int) serialized.get("currentBoard");
      u.pvpDeaths = (int) serialized.get("pvpDeaths");
      u.pvpKills = (int) serialized.get("pvpKills");
      u.copKills = (int) serialized.get("copKills");
      u.killstreak = (int) serialized.get("killstreak");
      u.wantedlevel = (int) serialized.get("wantedLevel");
      u.isCop = (boolean) serialized.get("isCop");
      u.savedInventory = (List<ItemStack>) serialized.get("savedInventory");

      u.attackedCop = (boolean) serialized.get("attackedCop");
      u.mobDeaths = (int) serialized.get("mobDeaths");
      u.mobKills = (int) serialized.get("mobKills");
      u.skeletonKills = (int) serialized.get("skeletonKills");
      u.witherSkeletonKills = (int) serialized.get("witherSkeletonKills");
      u.zombieNormalKills = (int) serialized.get("zombieNormalKills");
      u.endermanKills = (int) serialized.get("endermanKills");
      u.villagerKills = (int) serialized.get("villagerKills");
      u.npcCopKills = (int) serialized.get("npcCopKills");
      return u;
   }

   private static void createHashMaps(User u, List<Location> locs, List<Long> times, List<List<ItemStack>> loot){
      int i = 0;
      for(Long time : times) {
         if(time >= System.currentTimeMillis()) {
            u.chestLoot.put(locs.get(i), loot.get(i));
            u.chestLocations.put(locs.get(i), time);
         }
         i++;
      }
   }

   public void setScoreBoard(Scoreboard board){
      this.PLAYER.getPlayer().setScoreboard(board);
   }

   public void addChestLocation(Location loc, Long endTime, List<ItemStack> loot){
      chestLocations.put(loc, endTime);
      chestLoot.put(loc, loot);
   }

   public double getBalance(){
      return Main.getMain().getEcon().getBalance(PLAYER);
   }

   public void addMoney(double amt){
      Main.getMain().getEcon().depositPlayer(PLAYER, amt);
   }

   public void removeMoney(double amt){
      Main.getMain().getEcon().withdrawPlayer(PLAYER, amt);
   }

   public void modPvpDeaths(int amt) {
      this.pvpDeaths = Math.max((this.pvpDeaths + amt), 0);
   }

   public void modPvPKills(int amt) {
      this.pvpKills = Math.max((this.pvpKills + amt), 0);
   }

   public void modWantedLevel(int amt) {
      this.wantedlevel = Math.max((this.wantedlevel + amt), 0);
   }

   public void modMobDeaths(int amt) {
      this.mobDeaths = Math.max((this.mobDeaths + amt), 0);
   }

   public void modMobKill(int amt) {
      this.mobKills = Math.max((this.mobKills + amt), 0);
   }

   public void modSkelKill(int amt) {
      this.skeletonKills = Math.max((this.skeletonKills + amt), 0);
      modMobKill(amt);
   }

   public void modWithSkelKill(int amt) {
      this.witherSkeletonKills = Math.max((this.witherSkeletonKills + amt), 0);
      modMobKill(amt);
   }

   public void modZomKill(int amt) {
      this.zombieNormalKills = Math.max((this.zombieNormalKills + amt), 0);
      modMobKill(amt);
   }

   public void modEndKill(int amt) {
      this.endermanKills = Math.max((this.endermanKills + amt), 0);
      modMobKill(amt);
   }

   public void modVillagerKill(int amt) {
      this.villagerKills = Math.max((this.villagerKills + amt), 0);
   }

   public void modNPCCopKills(int amt) {
      this.npcCopKills = Math.max((this.npcCopKills + amt), 0);
   }

   public void modCopKills(int amt) {
      this.copKills = Math.max((this.copKills + amt), 0);
   }


   //potentially problematic methods that might need fixing or tweaking.
   public static void saveAllPlayerData() {
      users.forEach((uuid, user) -> user.savePlayerData());
   }

   public void savePlayerData() {
      this.fileConfig.set("Data", this);
      try {
         this.fileConfig.save(this.player_File);
      }
      catch(IOException e) {
         e.printStackTrace();
      }
   }

   public static void addUser(User u) {
      users.put(u.getPlayerUUID(), u);
   }

   public static User removeUser(UUID uuid) {
      return users.remove(uuid);
   }

   public void sendPlayerBulkMessage(String... message) {
      for(String s : message) {
         sendPlayerMessage(s);
      }
   }

   public void sendPlayerMessage(String message) {
      StringUtils.sendMessage(this.PLAYER.getPlayer(), message);
   }

   public void viewOtherPlayerStats(OfflinePlayer other, int type){
      Scoreboard hold = ScoreboardManager.getBoardAndRemoveByUser(this);
      User u = User.getByPlayer(other);
      if(type == 0) {
         ScoreboardManager.allBoardText(u, hold);
         hold.getObjective(DisplaySlot.SIDEBAR).setDisplayName(StringUtils.color("&b" +other.getName() + "'s Stats"));
      }
      else if(type == 1) {
         ScoreboardManager.PVPBoardText(u, hold);
         hold.getObjective(DisplaySlot.SIDEBAR).setDisplayName(StringUtils.color("&b" +other.getName() + "'s PVP Stats"));
      }
      else {
         ScoreboardManager.PVEBoardText(u, hold);
         hold.getObjective(DisplaySlot.SIDEBAR).setDisplayName(StringUtils.color("&b" +other.getName() + "'s PVE Stats"));
      }
      new BukkitRunnable() {
         @Override
         public void run() {
            ScoreboardManager.createScoreBoard(User.this);
         }
      }.runTaskLater(Main.getMain(), 200L);
      setScoreBoard(hold);
   }

   /**
    * @param player the player to search for
    *
    * @return the User found, otherwise null.
    */
   public static User getByPlayer(OfflinePlayer player) {
      return users.get(player.getUniqueId());
   }

   /**
    * @param playerName the player's name to search for
    *
    * @return the User found, otherwise null.
    */
   public static User getByName(String playerName) {
      OfflinePlayer p = PlayerUtils.getPlayer(playerName);
      UUID u;
      if(p != null)
         u = p.getUniqueId();
      else
         return null;
      return users.get(u);
   }

   /**
    * @param player_UUID the player's UUID to search for
    *
    * @return the User found, otherwise null.
    */
   public static User getByUUID(UUID player_UUID) {
      return users.get(player_UUID);
   }

   public ItemStack[] getSavedInventory(){
      return savedInventory.toArray(new ItemStack[0]);
   }

   public ItemStack getUserItem(){
      ItemStack item = ItemUtils.getSkullOf(playerUUID);
      ItemUtils.applyLore(item, "&8UUID: &7" + playerUUID,"&2Wanted Level: &7" + wantedlevel, "&bKillstreak: &7" + killstreak, "&cKills: &7" + pvpKills, "&dDeaths: &7" + pvpDeaths, "&3Cop-Kills: &7" + copKills);
      return item;
   }
}
