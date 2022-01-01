package me.Strobe.Housing.Utils;

import java.util.concurrent.TimeUnit;

public final class StringUtils {

   private StringUtils() {}

   public static final String unrentHouse = "&e&l(!)&7 You have unrented your house!";
   public static final String purchaseHouse = "&a&l(!)&7 You have purchased this house!";
   public static final String addedMember = "&e&l(!)&7 You have added &b{plr}&7 to your house!";
   public static final String removeMember = "&c&l(!)&7 You have removed &b{plr}&7 from your house!";
   public static final String gainedOwnership = "&a&l(!)&7 You are now the owner of &b{reg}&7!";
   public static final String lostOwnership = "&c&l(!)&7 You no longer own this house!";
   public static final String expiredHouse = "&c&l(!)&7 Your house's time has expired!";
   public static final String expiredMemberHouse = "&c&l(!)&7 A house you're added to has expired!";
   public static final String kickFromHouse = "&c&l(!)&7 You have been removed from &b{plr}'s&7 house!";
   public static final String memberLeftHouse = "&c&l(!)&b {plr}&7 has left your house!";
   public static final String addedToHouse = "&e&l(!)&7 You have been added to &b{plr}'s&7 house!";
   public static final String selfExtendedHouse = "&a&l(!)&7 You have extended your house's rent!";
   public static final String memberExtendedHouseMsg = "&a&l(!)&7 {plr} has extended {plr2} house's rent!";
   public static final String maxDaysReached = "&c&l(!)&7 You cannot add any more time to this house!";
   public static final String houseExpiringSoon = "&e&l(!)&7 Your house is expiring soon!";
   public static final String ownerSoldHouse = "&e&l(!)&7 The owner of a house you're added to has been sold!";
   public static final String noHouse = "&c&l(!)&7 You dont own a house!";
   public static final String notEnoughMoney = "&c&l(!)&7 You dont have enough for this house!";
   public static final String notAHouse = "&c&l(!)&7 There is no house by this name!";
   public static final String leftHouse = "&c&l(!)&7 You have left {plr}'s house!";
   public static final String alreadyOwnedHouse = "&c&l(!)&7 You already have a house!";

   //TODO: Command args helper yes.
   public static final String cmdSetSpawnSuccess = "&a&l(!)&7 You have set the spawn of this house to {x}, {y}, {z}";
   public static final String cmdSetSpawnFail = "&c&l(!)&7 You cannot set the spawn of your house here.";
   public static final String cmdAddMemFailAddedPrior = "&c&l(!)&7 You cannot add {plr} to your house because they're already added.";
   public static final String cmdRemMemFailNotAdded = "&c&l(!)&7 You cannot remove {plr} from your house because they're not added to your house.";
   public static final String cmdCreateHouseFailNoSelection = "&c&l(!)&7 You must make a world edit selection then create the house.";
   public static final String cmdCreateHouseFailRegionAlreadyExists = "&c&l(!)&7 The region you're trying to create already exists.";
   public static final String cmdCreateHouseFailPriceOrDaysInvalid = "&c&l(!)&7 The price or days you are assigning are invalid.";
   public static final String cmdCreateHouseSuccess = "&e&l(!)&7 House successfully created at {loc}";
   public static final String brokenHouseSign = "&c&l(!)&7 Your house has been removed by an admin!";
   public static final String brokenHouseSignMember = "&c&l(!)&7 The house {reg} that you were added to has been removed by an admin!";
   public static final String adminDestroyHouse = "&e&l(!)&7 You have removed the house and region {reg}.";

   public static final String houseReloadUsage = "&e&l(!)&7 You use the command like so: /house reload <houseID | all>";

   public static String[] msToDHMSColored(long timeLeft) {
      int days = (int) TimeUnit.MILLISECONDS.toDays(timeLeft);
      timeLeft -= TimeUnit.DAYS.toMillis(days);
      int hours = (int)TimeUnit.MILLISECONDS.toHours(timeLeft);
      timeLeft -= TimeUnit.HOURS.toMillis(hours);
      int min = (int)TimeUnit.MILLISECONDS.toMinutes(timeLeft);
      timeLeft -= TimeUnit.MINUTES.toMillis(min);
      int sec = (int)TimeUnit.MILLISECONDS.toSeconds(timeLeft);
      return me.Strobe.Core.Utils.StringUtils.colorBulk(
      "&aDays: &8" + days, "&aHours: &8" + hours, "&aMinutes: &8" + min, "&aSeconds: &8" + sec);
   }}
