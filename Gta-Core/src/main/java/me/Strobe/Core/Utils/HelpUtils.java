package me.Strobe.Core.Utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import static me.Strobe.Core.Utils.StringUtils.color;

public class HelpUtils {
   public static void sendCategoryMessage(Player p, String category, String hoverCommand, ChatColor color) {
      TextComponent message = new TextComponent(category);
      message.setColor( color );
      message.setBold( true );
      message.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, hoverCommand ));
      message.setHoverEvent( new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(color + hoverCommand).create()));
      p.spigot().sendMessage(message);
   }

   public static void sendHelpMessage(Player p, int page) {
      if(page == 1) {
         p.sendMessage("");
         p.sendMessage(color("&b&lGTA-MC Tutorial (&d&l1&b&l/&d&l2&b&l)"));
         sendCategoryMessage(p, "Guns", "/help guns", ChatColor.RED);
         sendCategoryMessage(p, "Cheatcodes", "/help cheatcodes", ChatColor.GOLD);
         sendCategoryMessage(p, "Housing", "/help housing", ChatColor.YELLOW);
         sendCategoryMessage(p, "Looting", "/help looting", ChatColor.GREEN);
         //sendCategoryMessage(p, "Gangs", "/gangs", ChatColor.AQUA);
         //sendCategoryMessage(p, "Coin Flip", "/cf help", ChatColor.DARK_AQUA);
         //sendCategoryMessage(p, "Bank Block Manager", "/help bankblock", ChatColor.BLUE);
         sendCategoryMessage(p, "Trading", "/help trading", ChatColor.DARK_PURPLE);
         sendCategoryMessage(p, "Bank (Ender Chests)", "/help bank", ChatColor.LIGHT_PURPLE);
         p.sendMessage(color("&e&l(!) &eClick a category to get more information."));
         p.sendMessage(color("&7&o(Use /help 2 to view the 2nd help page)"));
         p.sendMessage("");
         return;
      }
      p.sendMessage("");
      p.sendMessage(color("&b&lGTA-MC Tutorial (&d&l2&b&l/&d&l2&b&l)"));
      sendCategoryMessage(p, "Discord", "/discord", ChatColor.RED);
      sendCategoryMessage(p, "Store", "/buy", ChatColor.GOLD);
      sendCategoryMessage(p, "Voting", "/vote", ChatColor.YELLOW);
      //sendCategoryMessage(p, "Daily Challenges", "/help challenges", ChatColor.GREEN);
      //sendCategoryMessage(p, "Wanted Levels", "/wanted", ChatColor.AQUA);
      sendCategoryMessage(p, "Store Robbery", "/help robbery", ChatColor.DARK_AQUA);
      //sendCategoryMessage(p, "Bartender", "/barnpc", ChatColor.DARK_PURPLE);
      //sendCategoryMessage(p, "Warp Locations", "/warp", ChatColor.LIGHT_PURPLE);
      p.sendMessage(color("&e&l(!) &eClick a category to get more information."));
      p.sendMessage("");
   }

   public static void sendGunsHelp(Player p) {
      p.sendMessage(color("&c&lGuns"));
      p.sendMessage(color("&7Once you start looting and obtaining some guns, you'll " +
                             "notice that each gun has a tier described in its lore. There are &n5 Weapon Tiers&7, however " +
                             "&bbasic combat in GTA-MC revolves primarily around the &bTier 5 &7Weapons&7, which are the most " +
                             "powerful gun tier and do NOT drop on death."));
      p.sendMessage("");
      p.sendMessage(color("&7&oThe weaker and lower-tier weapons (such as &dtier 4's&7&o and under) are mainly" +
                             " used as a source of income&7. You can sell them at the &6&lGun Store&7 located at /spawn for" +
                             " some quick cash. Of course, selling a &dtier 4 &7weapon will produce more money than selling a &6tier 2 &7weapon."));
   }

   public static void sendLootingHelp(Player p) {
      p.sendMessage(color("&a&lLooting"));
      p.sendMessage(color("&7Looting is an essential part of GTA-MC, as it is the primary method of finding " +
                             "the supplies you need to hold your own against other players! In &aGreenfield (the open world)&7, you’ll " +
                             "find many buildings or locations with &6chests&7, which you can open up to find some of the supplies you’ll " +
                             "need, such as guns, ammo packs, armor, bandages, crate keys, and even some money and liquor! " +
                             "&d(You can get to Greenfield by right-clicking the teleport sign at spawn)&7. " +
                             "While chests are scattered across the city, there are a few distinct locations which hold a great" +
                             " number of chests, and, as such, yield the greatest amount of loot. You can always type &b/loot &7for a list" +
                             " of high-yield looting locations. However, be warned that other players will also seek out these loot hotspots," +
                             " and you may be targeted by other players looking to loot or kill. &7&oAlways keep a watchful eye when looting (or, maybe just bring a buddy to watch your back!)"));
   }

   public static void sendCheatcodeHelp(Player p) {
      p.sendMessage(color("&6&lCheatcodes"));
      p.sendMessage(color("&7Cheatcodes are nonessential add-on weapons/tools that are obtainable either in-game using &3Odd Currency&7 or through our &bbuycraft&7 using &7&n/buy&7! You’ll " +
                             "have a wide selection of useful and fun items that offer unique benefits, separate from the &bTier 5 &7weapons. And of course, they don’t drop on death!"));
   }

   public static void sendHousingHelp(Player p) {
      p.sendMessage(color("&e&lHousing"));
      p.sendMessage(color("&7As you progress further and start to collect more &bTier 5’s &7and armor," +
                             " you may find yourself running short on places to store your items besides the &6Bank&7. That’s" +
                             " where housing comes in! In GTA-MC, you can &7&orent houses in different areas across &a&oGreenfield&7!" +
                             " There are only &7&n5 main housing districts&7, with each district offering a &eunique amount of storage" +
                             " &7and &arent price &7&o(more storage means a higher rent price). The 5 main housing districts are &eApartments," +
                             " &6Ghetto, &bUpperclass, &3Upperclass 2, &7and &cMansions&7. Of course, &c&lPvP is disabled&7 in areas where rentable" +
                             " housing is located, so you won’t have to worry about defending yourself in your own house! But outside is a different story!" +
                             " You can also add other players to your house to give them access to your housing chests at no additional cost. Once" +
                             " you’ve rented a house or are added to one, you can always type &7&n/house&7 and select your house to warp there" +
                             " instantly! You must find each housing area to get there, so stay sharp!"));
   }

   public static void sendTradeHelp(Player p) {
      p.sendMessage(color("&5&lTrading"));
      p.sendMessage(color("&7Because &bTier 5’s &7are unique in that they do not drop on death and are the most powerful of all" +
                             " the tiers, they are not sellable in the &6Gun Store &7at spawn. Instead, &bTier 5 &7weapons are traded among other players" +
                             " in exchange for hefty amounts of money or other valuable items! What you charge is up to you! Just keep in mind that" +
                              " not all Tier 5’s go for the same price. You can enter trades with other players by typing \"&d/trade [playername]&7\", or by" +
                             " clicking \"&aAccept&7\" when invited to a trade."));
   }

   //    public static void sendBankBlockHelp(Player p) {
   //        p.sendMessage(color("&9&lBank Block Manager"));
   //        p.sendMessage(color("&7The Bank Block Manager is an NPC located at the spawn’s Bank in which players invest in shares of money." +
   //                " Players can deposit up to &a$50,000&7, which can increase as you level up your share! Once you have deposited money into the block" +
   //                " manager, it will gain &n+3.25%&7 interest every time you kill &c10+&7 players! You can withdraw your money at any time, but there is a" +
   //                " penalty for withdrawing too soon. Players who are under a &a75 killstreak&7 will have to pay a &c25% withdrawal fee&7 for taking your" +
   //                " investment early! A deduction of &c-1.25%&7 interest will be taken when you are killed."));
   //    }

   public static void sendBankHelp(Player p) {
      p.sendMessage(color("&d&lBank"));
      p.sendMessage(color("&7Once you start selling weapons and getting some of that sweet, sweet money, you may be a bit perplexed about where exactly" +
                             " to store it. Your Balance (displayed on your player HUD to the right) shows the amount of money you have next to the word \"&aBalance&7\" However," +
                             " if you die with points in your Balance, you’ll lose a portion of them on death. To prevent losing your hard-earned money to other players out" +
                             " in the city, you can store your money by turning your Balance into a holdable currency at the Bank (located in /spawn). At the bank, you can" +
                             " choose to materialize your balance into a &c$100&7, &c$1000&7, or &c$5000&7 coin, depending on how much money you need to store."));
      p.sendMessage("");
      p.sendMessage(color("&7Within the &6Bank &7at spawn, you’ll also find &dEnder Chests &7in which you can safely store not only your valuable coins, but also" +
                             " other items such as guns, ammo, or even &3Mystery Tokens&7! Additionally, you can expand your &dEnder Chest &7by purchasing it from the &3Mystery Man&7 (the &7Mystery Token&7 dealer)" +
                             " or by supporting us through our buycraft using &n/buy&7! With the expansions, you can upgrade your &dEnder Chest &7to be double the size of the standard &dEnder Chest&7!"));
   }

   public static void sendRobberyHelp(Player p) {
      p.sendMessage(color("&3&lStore Robberies"));
      p.sendMessage(color("&7Across the city, there are a number of &6stores &7that can be robbed for loot. Drops from robberies are random, and can range from a few $100 coins to thousands" +
                             " of dollars and even Tier 5 Weapons! Just right-click the NPC and left-click the coin to start robbing away!"));
      p.sendMessage(color("&7Be mindful that, when robbing a store, your location will be displayed in public chat, and you’ll have around a minute until the robbery is finished.  To cancel a robbery you can Shift+Right-Click them. " +
                          "This wont display in public chat *wink*."));
      p.sendMessage(color("&7If you complete a robbery or kill a player, your Wanted Level (displayed on your HUD to the right) will increase. Players will be awarded money for killing those who have" +
                             " a higher wanted level. Players in Cop Mode (available through the Mystery Man or through /buy) can also track your location in the city and will receive a considerable money bonus for killing players with a  high Wanted Level."));
      p.sendMessage(color("&7If you are ever curious about the loot a robbery can give you, just punch the npc!"));
   }

}
