package me.strobe.vinnyd.commands;

import lombok.NonNull;
import me.strobe.vinnyd.Utils.StockItem;
import me.strobe.vinnyd.Utils.Upgrade;
import me.strobe.vinnyd.Utils.VinnyUtils;
import org.apache.commons.lang3.Validate;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Locale;

/**
 * This is VinnyCommands.java in Project: (Gta-Core) : But you already knew that
 * @author G.P of Prentice Productions
 * @version 1.0
 * Created On    : 2/24/2022 4:08 PM
 * Last Edit     : 2/24/2022 4:08 PM(Update Me!)
 * Time to Write : (Rough Estimate)
 *
 * (Class Description)
*/
public class VinnyCommands implements CommandExecutor {

   //Root : /vinny or /vin
   @Override
   public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
      if(sender instanceof Player)
         process((Player) sender, args);

      return false;
   }

   private void process(Player sender, String... args){
      String root = args[0].toLowerCase(Locale.ROOT);
      switch(root){
         case "stock":
            stock(sender);
            break;
         case "upgrades":
            upgrades(sender);
            break;
         case "addupgrade":
            addUpgrade(args[1], Double.parseDouble(args[2]), Integer.parseInt(args[3]), sender.getInventory().getContents());
            break;
         case "addStock":
            addStock(sender.getItemInHand(), Double.parseDouble(args[1]), Integer.parseInt(args[2]), Double.parseDouble(args[3]), Boolean.parseBoolean(args[4]));
            break;
         case "remupgrade":
            removeUpgrade(Integer.parseInt(args[1]));
            break;
         case "remstock":
            removeStock(Integer.parseInt(args[1]));
            break;
         case "reroll":
            rerollStock();
            break;
         case "despawn":
            despawn();
            break;
         case "spawn":
            int x,y,z;
            if(args.length == 4){
                x = Integer.parseInt(args[1]);
                y = Integer.parseInt(args[2]);
                z = Integer.parseInt(args[3]);
                spawn(new Location(sender.getWorld(), x ,y, z));
            }
            else
               spawn(null);
         default:
      }
   }

   //Commands

   //vinny stock
   /**
    * This method displays a list of all the possible stock items that vinny can sell with their weights
    *
    * @param sender The player that sent the command.
    */
   private void stock(Player sender){
      VinnyUtils.getVinny().showStock(sender);
   }

   //vinny upgrades
   /**
    * Displays the full list of upgrades for the player in an easier to read GUI
    *
    * @param sender The player that the GUI will be opened for
    */
   private void upgrades(Player sender){
      VinnyUtils.getVinny().showUpgrades(sender);
   }

   //vinny addupgrade <string> <num> <num> <player inventory>
   /**
    * This method attempts to add a new upgrade to Vinny's shop
    *
    * @param weaponToUpgradeTo This is the crackshot formatted name of the new weapon, it must be there or this will not complete
    * @param moneyPrice The price in in-game currency for this upgrade
    * @param oddCurrencyPrice The price in Odd Currency (tokens) for this upgrade
    * @param requiredItems An array of items required for the upgrade. These are supplied by the players inventory in a previous call.
    */
   private void addUpgrade(String weaponToUpgradeTo, double moneyPrice, int oddCurrencyPrice, ItemStack... requiredItems){
      Validate.isTrue(moneyPrice >= 0);
      Validate.isTrue(oddCurrencyPrice >= 0);
      Upgrade x = new Upgrade(weaponToUpgradeTo, moneyPrice, oddCurrencyPrice, requiredItems);
      VinnyUtils.getVinny().addUpgrade(x);
   }

   //vinny addstock <held item> <num> <num> <num> <boolean>
   /**
    * Attempts to add a new Stock Item to the loot pool for Vinny to sell
    *
    * @param item The actual item to be sold in exchange for the following prices
    * @param moneyPrice The price in in-game currency for this StockItem
    * @param oddCurrencyPrice the price in Odd Currency (Tokens) for this upgrade
    * @param weightToBeChosenForSale The indiviudual weight that this item is chosed for sale
    * @param reload If the loot pool should be reloaded to potentially add this to the active stock
    */
   private void addStock(@NonNull ItemStack item, double moneyPrice, int oddCurrencyPrice, double weightToBeChosenForSale, boolean reload){
      Validate.isTrue(moneyPrice >= 0);
      Validate.isTrue(oddCurrencyPrice >= 0);
      Validate.isTrue(weightToBeChosenForSale >= 0);
      StockItem x = new StockItem(item, moneyPrice, oddCurrencyPrice, weightToBeChosenForSale);
      VinnyUtils.getVinny().addEntryToStock(x, reload);
   }

   //vinny removeupgrade <num>
   /**
    * Removes an upgrade if it is present based on its numerical position with the list
    *
    * @param upgradeIndex The positional index within the GUI {@link VinnyUtils.VinnyD#showUpgrades(Player)}
    */
   private void removeUpgrade(int upgradeIndex){
      Validate.isTrue(upgradeIndex >= 0);
      VinnyUtils.getVinny().removeUpgrade(upgradeIndex);
   }

   //vinny removestock <num>
   /**
    * Removes a StockItem if it is present based on its numerical position within its list
    *
    * @param stockIndex The positional index within the GUI {@link VinnyUtils.VinnyD#showStock(Player)}
    */
   private void removeStock(int stockIndex){
      Validate.isTrue(stockIndex >= 0);
      VinnyUtils.getVinny().removeEntryFromStock(stockIndex);
   }

   //vinny reroll
   /**
    * Rerolls vinny's active stock
    */
   private void rerollStock(){
      VinnyUtils.getVinny().rollNewVinnyLoot();
   }

   //vinny despawn

   private void despawn(){
      VinnyUtils.despawnVinny();
   }

   //vinny spawn <x> <y> <z>

   /**
    * Spawns an NPC representing Vinny at the provided location.
    *
    * @param loc The location for vinny to be spawned at. If null is supplied, it will use the interal location specified in config.yml
    */
   private void spawn(@Nullable Location loc){
      VinnyUtils.spawnVinny(loc);
   }

}
