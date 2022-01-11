package me.Strobe.Core.Utils;

import me.Strobe.Core.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;

public final class StringUtils {

   //  "&a&l(!) " positive
   //  "&c&l(!) " negative
   //  "&e&l(!) " info
   //  "&b&l(!) " command info

   public enum Text {
      //General stuff
      DROPPED_MONEY(false, "&c&l(!) &7You dropped &c$ {0}&7. Your balance is now &n{1}&r&7."),
      GAINED_MONEY(false, "&a&l(!) &7You pick up &a&n$ {0}&7."),
      GAINED_MONEY_FOR_KILL_COP(false, "&a&l(!) &7You gained &a${0}&7 for killing someone with a wanted level of {1}."),
      GAINED_MONEY_FOR_WANTED(false, "&a&l(!) &7You earned $ {0} for killing with a wanted level of {1}!."),

      INC_WL(false, "&a&l(!) &7Your &c&lWanted Level &7has increased by {0}"),
      R_WL(false, "&c&l(!) &7Your wanted level has reset!"),

      COPS_ENGAGE(false, "&e&l(!) &7Start Running!"),
      COPS_BACKUP(false, "&e&l(!) &7Looks like they called for backup!"),
      COPS_DISENGAGE(false, "&a&l(!) You got away! &7This time...."),

      DENY_ACTION(false, "&e&l(!) &7You cannot do that."),
      INVALID_AMOUNT(false, "&c&l(!) &b&n{0}&7 is not a valid amount!"),
      INVALID_PLAYER(false, "&c&l(!) &7&n{0}&7 has not joined this server!"),
      FAIL_ITEM_DELIVERED(true, "&a&l(!) &7{0}'s inventory is full!"),
      UNCERTAIN(true, "&c&l(!) &7Sorry, I didn't understand that."),
      HELP(false, "&7|----&8[&6GTA-MC&8]&7----|\n" +
                                          "&7 Welcome to GTA-MC, As you may have noticed there are some changes.\n" +
                                          "&7 Read through the following pages to learn more, the colors are clickable."),

      // Commands
      STATS_USAGE(true, "&b&l(!)&7 Use the stats command like this:\n" + " &B/stats <PVP, PVE, All, OFF, ON> \n" + " To view another player's stats add their name after. " + " &B/stats <PVP, PVE, ALL> <playername>"),
      INVALID_STATS(false, "&c&l(!) &7That is not a stats type on this server. Types: &bPVP&7, &bPVE&7, &bALL&7."),
      STATS_TO_PVP(false, "&b&l(!) &7Your scoreboard reflects your pvp stats now!"),
      STATS_TO_PVE(false, "&b&l(!) &7Your scoreboard reflects your pve stats now!"),
      STATS_TO_ALL(false, "&b&l(!) &7Your scoreboard reflects all your stats now!"),
      STATS_OFF(false, "&b&l(!) &7Your scoreboard is disabled now!"),
      STATS_ON(false, "&b&l(!) &7Your scoreboard is re-enabled now!"),
      STATS_ALREADY_ON(false, "&b&l(!) &7Your scoreboard is already on!"),

      COP_USAGE(true, "&b&l(!)&7 Use the cop command like this\n" + " &b/cop &7: Change into or out of cop mode.\n" +
                      " &b/cop setsafehouse &7: Stand where you want the spawn to be and run the command.\n" + " &b/cop setinventory &7: Organize your inventory how you wish the cop's to be, then run the command.\n" + " &b/cop setmaxspawns &6<amt> &7: Set the max amount of cops to spawn each wave\n" + " &b/cop setminspawns &6<amt> &7: Set the minimum amount of cops to spawn each wave.\n" + " &b/cop blockcmd &6<cmd> &7: Block a specific command provided in <cmd>\n" + " &b/cop allowcmd &6<cmd> &7: Allow a specific command in <cmd>\n" + " &b/cop settimeout &6<amt> &7: Set the time to change in seconds between cop mode and player mode\n" + " &b/cop viewinventory &7: View the current cop mode inventory."),

      COP_SETSAFEHOUSE_USAGE(true,"&b&l(!) &7Use the cop command like this\n" + " &b/cop setsafehouse &7: Stand where you want the spawn to be and run the command."),
      COP_MODE_ON(false, "&a&l(!)&7 You are now a &bCop&7."),
      COP_MODE_OFF(false, "&c&l(!)&7 You are no longer a &bCop&7."),
      COP_SET_INV(true, "&b&l(!)&7 The cop mode inventory is now yours!"),
      COP_SET_INV_USAGE(true, "&b&l(!) &7Use the cop command like this\n" + " &b/cop setinventory &7: Organize your inventory how you wish the cop's to be, then run the command.\n"),
      COP_SET_MIN(true, "&b&l(!)&7 The minimum amount of cops to spawn is now {0}"),
      COP_SET_MIN_USAGE(true, "&b&l(!) &7Use the cop command like this\n" + " &b/cop setminspawns &6<amt> &7: Set the minimum amount of cops to spawn each wave.\n"),
      COP_SET_MAX(true, "&b&l(!)&7 The maximum amount of cops to spawn is now {0}"),
      COP_SET_MAX_USAGE(true, "&b&l(!)&7 Use the cop command like this\n" + " &b/cop setmaxspawns &6<amt> &7: Set the max amount of cops to spawn each wave\n"),
      COP_SUCCESS_SPAWN(true, "&b&l(!) &7Successfully spawned {0} cop(s) on {1}"),


      COP_BLOCKCMD(true, "&b&l(!)&7 You have blocked the command &b{0}&7 in cop mode."),
      COP_BLOCKCMD_USAGE(true, "&b&l(!) &7Use the cop command like this\n" + " &b/cop blockcmd &6<cmd> &7: Block a specific command provided in <cmd>\n"),

      COP_ALLOWCMD_USAGE(true, "&b&l(!)&7 Use the cop command like this\n" + " &b/cop allowcmd &6<cmd> &7: Allow a specific command in <cmd>\n"),
      COP_ALLOWCMD(true, "&b&l(!)&7 You have allowed the command &b{0}&7 in cop mode."),

      COP_SET_TIMEOUT(true, "&b&l(!)&7 The time to chage between modes is now {0}"),
      COP_SET_TIMEOUT_USAGE(true, "&b&l(!) &7Use the cop command like this\n" +
                                  " &b/cop settimeout &6<amt> &7: Set the time to change in seconds between cop mode and player mode."),


      LOOT_USAGE(true, "&b&l(!)&7 Use the loot command list this\n" +
                         " &b/loot reload &7: Reloads all loot tables.\n"
                       + " &b/loot time <amt> &7: Set the time in minutes for chests to restock.\n"
                       + " &b/loot world (mob) (worldname) <activate/deactivate> &7: enable or disable worlds from using chest and mob drops.\n"
                       + " &b/loot (mobtype) add <min> <max> <weight> &7: Add the current held item to the chest loot pool or the optional mobloot pool.\n"
                       + " &b/loot (mobtype) view  &7: View the chest loot pool or the optional mob's loot pool.\n"
                       + " &b/loot (mobtype) giverandom &7: Give yourself a random drop from the chest or optional mob's loot pool.\n"
                       + " &b/loot (mobtype) min <amt> &7: Set the minimum number of items in a chest or to drop from a mob.\n"
                       + " &b/loot (mobtype) max <amt> &7: Set the maximum number of items in a chest or to drop from a mob.\n"),
      ALL_LOOT_RELOAD(true, "&b&l(!)&7 All loot tables have been reloaded."),
      LOOT_TIME_SET(true, "&b&l(!)&7 The time for chests to restock is now {0}"),
      LOOT_TIME_USAGE(true, "&b&l(!) Use the loot command list this\n &b/loot time <amt> &7: Set the time in minutes for chests to restock."),
      LOOT_WORLD_ACTIVE(true, "&b&l(!)&7 The world {0} is now loot activated!"),
      LOOT_WORLD_DEACTIVE(true, "&b&l(!)&7 The world {0} is now loot de-activated!"),
      NOT_WORLD(true, "&b&L(!)&7 That is not a valid world."),
      LOOT_WORLD_USAGE(true, "&b&l(!) Use the loot command list this\n" + " &b/loot world (mob) (worldname) <activate/deactivate> &7: enable or disable worlds from using chest and mob drops.\n"),
      LOOT_VIEW_USAGE(false, "&b&l(!)&7 Use the loot command list this\n" + " &b/loot (mobtype) or \"chest\": view  &7: View the chest loot pool or the optional mob's loot pool.\n"),
      MOB_WORLD_ACTIVE(true, "&b&l(!)&7 The world {0} is now loot activated!"),
      MOB_WORLD_DEACTIVE(true, "&b&l(!)&7 The world {0} is now mob-loot activated!"),

      SUCCESS_LOOT_ADD(true, "&b&l(!)&7 The item in your hand was successfully added to the {0} pool"),
      ITEM_DELIVERED_LOOTPOOL(false, "&a&l(!) &7Boop! You received an item from the {0} loot pool."),
      INVALID_ITEM(true, "&b&l(!) &7The item in your hand &c&ncannot&7 be added to the loot pool!"),
      SUCCESS_ITEM_DELIVERED(true, "&b&l(!) &7{0} delivered successfully."),
      INVALID_MOB(false, "&c&l(!) &7That is not a valid type on this server. Types: &bZombie&7, &bSkeleton&7, &bEnderman&7, &bVillager&7, &bCop&7, &bChest&7."),

      LOOT_POOL_RELOADED(true, "&b&l(!)&7 The pool {0} has been reloaded."),
      LOOT_RELOAD_USAGE(true, "&b&l(!) Use the loot command list this\n " + "&b/loot reload <type> : reload a specific loot table"),
      LOOT_SET_MIN(true, "&b&l(!)&7 The minimum amount of items in a chest is now {0}"),
      LOOT_SETMIN_USAGE(true, "&b&l(!) Use the loot command list this\n " + " &b/loot (mobtype) min <amt> &7: Set the minimum number of items in a chest or to drop from a mob."),
      LOOT_SET_MAX(true, "&b&l(!)&7 The maximim amount of items in a chest is now {0}"),
      LOOT_SETMAX_USAGE(true, "&b&l(!) Use the loot command list this\n " + " &b/loot (mobtype) max <amt> &7: Set the maximum number of items in a chest or to drop from a mob."),


      TELEPORTING(false,  "&e&l(!)&7 Teleporting... Dont Move! ({0} sec...)"),
      RULES(false, "&e&l(!)&7 The rules of this server are as follows more may exist on the discord.\n" +
                   "&e1. &7No Harassment\n" +
                   "&e2.&7 No Advertising\n" +
                   "&e3.&7 No IRL Deal Scamming \n" +
                   "&e4.&7 No Racism or Discrimination \n" +
                   "&e5.&7 No Alternative Accounts (Alt's) \n" +
                   "&e6. &7No Abusing Glitches/Exploits \n" +
                   "&e7.&7 No Hacking/Unfair Advantages - e.g: Macros, Player Mini-Map \n" +
                   "&e8.&7 No Malicious Messages \n" +
                   "&e9.&7 No Ban Evading \n" +
                   "&e10.&7 No Spamming/Excessive Caps")


      ;


      String text;
      boolean pT;

      Text(boolean pluginTagged, String message) {
         pT = pluginTagged;
         text = StringUtils.color(message);
      }

      public String create(String... replacements) {
         String hand = MessageFormat.format(text, (Object[]) replacements);
         return pT? Main.PLUGINPREFIX + hand : hand;
      }
   }
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
      p.sendMessage(color(msg));
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

   public static String formatDouble(double d, int places){
      return String.format("%,."+ places+"f", d);
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
