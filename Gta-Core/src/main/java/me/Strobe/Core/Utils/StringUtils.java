package me.Strobe.Core.Utils;

import me.Strobe.Core.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;

public final class StringUtils {

   public static final String droppedMoney = "&c&l(!) &7You dropped &c$ {amt}&7. Your balance is now &n{bal}&r&7.";
   public static final String gainedMoney = "&a&l(!) &7You pick up &a&n$ {amt}&7.";
   public static final String gainedMoneyForWanted = "&a&l(!) &7You gained &a${amt}&7 for killing someone with a wanted level of {wl}.";
   public static final String gainedMoneyFromCop = "&a&l(!) &7You earned $ {amt} for killing a cop!.";
   public static final String increasedWantedLevel = "&a&l(!) &7Your &c&lWanted Level &7has increased by $ {amt}, it is now {wl}";
   public static final String resetWantedLevel = "&c&l(!) &7Your wanted level has reset!";
   public static final String deniedInCop = "&c&l(!) &7You cannot do that in Cop Mode!";
   public static final String copsEngage = "&c&l(!) Start Running!";
   public static final String copsDisengage = "&a&l(!) You got away! &7This time....";
   public static final String invalidAmount = "&c&l(!) &7&n$ {amt}&7 is not a valid amount!";
   public static final String invalidPlayer = "&c&l(!) &7&n{plr}&7 has not joined this server!";
   public static final String invalidBoardType = "&c&l(!) &7That is not a stats type on this server. Types: &bPVP&7, &bPVE&7, &bALL&7.";
   public static final String invalidMobType = "&c&l(!) &7That is not a mob type on this server. Types: &bZombie, &bSkeleton, &bEnderman, &bVillager, &bCop.";
   public static final String invalidItemType = "&a&l(!) &7The item in your hand &c&ncannot&7 be added to the loot pool!";
   public static final String successAddedToPool = "&a&l(!) &7The item in your hand has been &a&nsuccessfully&7 added to the loot pool!";
   public static final String randomItemSuccessful = "&a&l(!) &7Boop! You received an item from the chest lootpool.";
   public static final String randomZomItemSuccessful = "&a&l(!) &7Boop! You received an item from the Zombie lootpool.";
   public static final String randomSkelItemSuccessful = "&a&l(!) &7Boop! You received an item from the Skeleton lootpool.";
   public static final String randomEndItemSuccessful = "&a&l(!) &7Boop! You received an item from the Enderman lootpool.";
   public static final String randomVillItemSuccessful = "&a&l(!) &7Boop! You received an item from the Villager lootpool.";
   public static final String randomCopItemSuccessful = "&a&l(!) &7Boop! You received an item from the Cop lootpool.";
   public static final String giveRandomItemUnSuccessful = "&a&l(!) &7That players inventory is full!";
   public static final String deniedAction = "&c&l(!) &7You cannot do that.";
   public static final String notUnderstnad = "&c&l(!) &7Sorry, I didn't understand that.";
   public static final String[] help = {"&c&l(!) &7Welcome to GTA-MC",
                                        ""};


   public static final String statsUsage = "&bUse the stats command like this:\n  /stats <PVP, PVE, All, OFF> (optional)[Player] ";
   public static final String copUsage = "&bUse the cop command like this:\n  /cop";
   public static final String copSetSafehouseUsage = "&e&l(!)&7 Use the cop setsafehouse command like this:\n 1. Stand where you want to set the safehouse\n 2. /cop setsafehouse";
   public static final String copSetInventoryUsage = "&e&l(!)&7 Use the cop setInventory command like this:\n 1. Organize your inventory for a cop, armor included\n 2. /cop setinventory";
   public static final String copSetInventory = "&e&l(!)&7 The cop's inventory is now set to yours!";

   public static final String copSetMin = "&e&l(!)&7 The minimum amount of cops to spawn is now {n}";
   public static final String copSetMax = "&e&l(!)&7 The minimum amount of cops to spawn is now {n}";
   public static final String copSetTimechange = "&e&l(!)&7 It now takes {n} seconds to change modes.";



   public static final String copSetMaxSpawnsUsage = "&e&l(!)&7 Use the cop setmaxspawns command like this:\n  /cop setmaxspawns <amt>";
   public static final String copSetMinSpawnsUsage = "&e&l(!)&7 Use the cop command like this:\n /cop setminspawns <amt>";
   public static final String copSetSafeHouse = "&e&l(!)&7 The spawn for cops has been set at {w} {x} {y} {z}!";
   public static final String chestlootUsage = "&e&l(!)&7 Use the command like this: \n1. Hold an item in your hand.\n2. Run: /chestloot add <minAmt> <maxAmt> <weight>";
   public static final String moblootUsage = "&e&l(!)&7 Use the command like this: \n1. Hold an item in your hand.\n2. Run: /mobloot add <Zombie, Skeleton, Cop, Villager> <minAmt> <maxAmt> <weight>";
   public static final String lootActivatedWorld = "&e&l(!)&7 The world {w} has been loot-activated!";
   public static final String notAWorld = "&e&l(!)&7 The world provided is not a world";
   public static final String mobActivatedWorld = "&e&l(!)&7 The world {w} has been mob-activated!";
   public static final String lootDeactivatedWorld  = "&e&l(!)&7 The world {w} has been loot-deactivated!";
   public static final String mobDeactivatedWorld = "&e&l(!)&7 The world {w} has been mob-deactivated!";
   public static final String worldUsage = "&e&l(!)&7 Use the command like this: /chestloot world activate/deactivate (worldName)\n/mobloot world activate/deactivate (worldName)";

   public static final String teleporting = "&e&l(!)&7 Teleporting... Dont Move! ({t} sec...)";
   private StringUtils() {}

   public static String capitalizeFirst(String word) {
      word = word.toLowerCase();
      char[] cWord = word.toCharArray();
      cWord[0] -= 32;
      return new String(cWord);
   }

   /**
    * send player an auto translated chatcolor message
    *
    * @param p   player to send message to
    * @param msg message to send player
    */
   public static void sendCommandMessage(CommandSender p, String msg) {
      p.sendMessage(color(Main.PLUGINPREFIX + msg));
   }

   public static void logFine(String... message){
      for(String s : message) Main.getMain().getLogger().log(Level.FINE, color(s));
   }

   public static void logInfo(String... message){
      for(String s : message) Main.getMain().getLogger().log(Level.INFO, color(s));
   }
   public static void logSevere(String... message){
      for(String s : message) Main.getMain().getLogger().log(Level.SEVERE, color(s));
   }

   /**
    * color text
    *
    * @param text text to color
    *
    * @return colored text
    */
   public static String color(String text) {
      return ChatColor.translateAlternateColorCodes('&', text);
   }

   public static void sendMessage(Player p, String msg) {
      p.sendMessage(color(msg));
   }

   public static void sendBulkMessage(Player p, String... msg) {
      p.sendMessage(colorBulk(msg));
   }

   public static String[] colorBulk(String... text) {
      for(int i = 0; i < text.length; i++) {
         text[i] = color(text[i]);
      }
      return text;
   }

   public static List<String> colorBulk(List<String> text) {
      return text.stream().map(StringUtils::color).collect(Collectors.toList());
   }

   /**
    * format a long EX: 1000 -> 1,000
    *
    * @param l long to format
    *
    * @return formatted long string
    */
   public static String formatLong(long l) {
      DecimalFormat df = new DecimalFormat("#,###.##");
      return df.format(l);
   }

   // Rounds the provided double value at the provided place
   public static double roundDecimal(double value, int places) {
      if(places < 0)
         throw new IllegalArgumentException();

      long factor = (long) Math.pow(10, places);
      value = value * factor;
      long tmp = Math.round(value);
      return (double) tmp / factor;
   }

   /**
    * generate a random string with A-Z and 1-9 characters
    *
    * @param length amount of characters in generated string
    *
    * @return randomly generated string
    */
   public static String generateRandomString(int length) {
      String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
      StringBuilder salt = new StringBuilder();
      Random rnd = new Random();
      while(salt.length() < length) { // length of the random string.
         int index = (int) (rnd.nextFloat() * SALTCHARS.length());
         salt.append(SALTCHARS.charAt(index));
      }
      return salt.toString();
   }

   /**
    * convert seconds into a formatted string
    *
    * @param seconds seconds to format
    *
    * @return formatted time string
    */
   public static String convertSeconds(long seconds) {
      long h = seconds / 3600;
      long m = (seconds % 3600) / 60;
      long s = seconds % 60;
      String sh = (h > 0? h + " " + "hour(s)" : "");
      String sm = (m < 10 && m > 0 && h > 0? "0" : "") + (m > 0? (h > 0 && s == 0? String.valueOf(m) : m + " " + "minute(s)") : "");
      String ss = (s == 0 && (h > 0 || m > 0)? "" : (s < 10 && (h > 0 || m > 0)? "0" : "") + s + " " + "second(s)");
      return sh + (h > 0? " " : "") + sm + (m > 0? " " : "") + ss;
   }

   /**
    * return current formatted date MM/dd
    *
    * @return current formatted date
    */
   public static String getFormattedDate() {
      SimpleDateFormat formatter = new SimpleDateFormat("MM/dd");
      return formatter.format(new Date());
   }

   /**
    * format a double EX: 1000 -> 1,000
    *
    * @param d double to format
    *
    * @return formatted double string
    */
   public static String formatDouble(double d) {
      DecimalFormat df = new DecimalFormat("#,###.##");
      return df.format(d);
   }

   /*
    *   a very, very poor repeat implementation of the repeat method from java 16 to enhance usage.
    */
   public static String repeat(String base, int count) {
      byte[] value = base.getBytes();
      if(count < 0) {
         throw new IllegalArgumentException("count is negative: " + count);
      }
      if(count == 1) {
         return base;
      }
      final int len = value.length;
      if(len == 0 || count == 0) {
         return "";
      }
      if(Integer.MAX_VALUE / count < len) {
         throw new OutOfMemoryError("Required length exceeds implementation limit");
      }
      if(len == 1) {
         final byte[] single = new byte[count];
         Arrays.fill(single, value[0]);
         return new String(single);
      }
      final int limit = len * count;
      final byte[] multiple = new byte[limit];
      System.arraycopy(value, 0, multiple, 0, len);
      int copied = len;
      for(; copied < limit - copied; copied <<= 1) {
         System.arraycopy(multiple, 0, multiple, copied, copied);
      }
      System.arraycopy(multiple, 0, multiple, copied, limit - copied);
      return new String(multiple);
   }

   public static String msToDayHourMinSec(long timeLeft) {
      int days = (int) TimeUnit.MILLISECONDS.toDays(timeLeft);
      timeLeft = -TimeUnit.DAYS.toMillis(days);
      int hours = (int) TimeUnit.MILLISECONDS.toHours(timeLeft);
      timeLeft = -TimeUnit.HOURS.toMillis(hours);
      int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(timeLeft);
      timeLeft = -TimeUnit.MINUTES.toMillis(minutes);
      int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(timeLeft);
      return "Days: " + days + " Hours: " + hours + " Minutes: " + minutes + " Seconds: " + seconds;
   }

}
