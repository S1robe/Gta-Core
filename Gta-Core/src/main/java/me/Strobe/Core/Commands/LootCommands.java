package me.Strobe.Core.Commands;

import me.Strobe.Core.Utils.User;
import me.Strobe.Core.Utils.Displays.GUIS;
import me.Strobe.Core.Utils.Looting.LootItem;
import me.Strobe.Core.Utils.LootingUtils;
import me.Strobe.Core.Utils.PlayerUtils;
import me.Strobe.Core.Utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LootCommands implements CommandExecutor {

   @Override
   public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
      if(commandSender instanceof Player) {
         if(args.length == 0) {
            if(s.equalsIgnoreCase("chestloot")){
               StringUtils.sendCommandMessage(commandSender, StringUtils.chestlootUsage);
            }
            else if(s.equalsIgnoreCase("mobloot")){
               StringUtils.sendCommandMessage(commandSender, StringUtils.moblootUsage);
            }
            return true;
         }
         Player sendr = (Player) commandSender;
         User sender = User.getByPlayer(sendr);
         switch(s.toLowerCase()) {
            case "chestloot": {
               switch(args[0].toLowerCase()) {
                  // chestloot add <minAmt> <maxAmt> <weight> //pulls from item in hand, copies the item exactly.
                  case "a":
                  case "+":
                  case "add": {
                     if(args.length != 4)
                        return false;
                     try {
                        short minAmt = Short.parseShort(args[1]);
                        try {
                           short maxAmt = Short.parseShort(args[2]);
                           try {
                              double weight = Double.parseDouble(args[3]);
                              ItemStack item = sendr.getItemInHand();
                              if(sendr.getItemInHand().getType().equals(Material.AIR)) {
                                 sender.sendPlayerMessage(StringUtils.invalidItemType);
                                 return false;
                              }
                              sender.sendPlayerMessage(StringUtils.successAddedToPool);
                              return addLootChest(item, minAmt, maxAmt, weight);
                           }
                           catch(NumberFormatException e) {
                              sender.sendPlayerMessage(StringUtils.invalidAmount.replace("{amt}", args[3]));
                           }
                        }
                        catch(NumberFormatException e) {
                           sender.sendPlayerMessage(StringUtils.invalidAmount.replace("{amt}", args[2]));
                        }
                     }
                     catch(NumberFormatException e) {
                        sender.sendPlayerMessage(StringUtils.invalidAmount.replace("{amt}", args[1]));
                     }
                     return false;
                  }
                  case "v":
                  case "d":
                  case "display":
                  case "view": {
                     displayLoot(sendr, "Loot");
                     return true;
                  }
                  case "g":
                  case "give":
                  case "giverandom": {
                     return giveRandomChestLoot(sender);
                  }
                  case "rel":
                  case "reload": {
                     LootingUtils.reloadChestLoot();
                     return true;
                  }
                  case "w":
                     //chestloot world activate (worldname)
                  case "world": {
                     if(args.length == 2){
                        if(args[1].equalsIgnoreCase("activate")){
                           return world(sender, sendr.getWorld(),true, true);
                        }
                        else if(args[1].equalsIgnoreCase("deactivate")){
                           return world(sender, sendr.getWorld(),false, true);
                        }
                     }
                     else if(args.length == 3){
                        if(args[1].equalsIgnoreCase("activate")){
                           return world(sender, Bukkit.getWorld(args[2]),true, false);

                        }
                        else if(args[1].equalsIgnoreCase("deactivate")){
                           return world(sender, Bukkit.getWorld(args[2]),false, false);
                        }
                     }
                     else{
                        sender.sendPlayerMessage(StringUtils.worldUsage);
                     }
                  }
                  case "min":{
                     LootingUtils.minItemChestdrop = Integer.parseInt(args[1]);
                  }
                  case "max":
                     LootingUtils.maxItemChestDrop = Integer.parseInt(args[1]);
                  case "time":
                     LootingUtils.chestResetTime = Integer.parseInt(args[1]) * 60000L;
               }
            }
            case "mobloot": {
               switch(args[0].toLowerCase()) {
                  // mobloot add <type> <minAmt> <maxAmt> <weight> //pulls from item in hand, copies the item exactly.
                  case "a":
                  case "+":
                  case "add": {
                     if(args.length != 5)
                        return false;
                     String type = args[1].toLowerCase();
                     if(type.equalsIgnoreCase("zombie") || type.equalsIgnoreCase("skeleton") || type.equalsIgnoreCase("enderman") || type.equalsIgnoreCase("villager") || type.equalsIgnoreCase("cop"))
                        try {
                           short minAmt = Short.parseShort(args[2]);
                           try {
                              short maxAmt = Short.parseShort(args[3]);
                              try {
                                 double weight = Double.parseDouble(args[4]);
                                 ItemStack item = sendr.getItemInHand();
                                 sender.sendPlayerMessage(StringUtils.successAddedToPool);
                                 return addMobLoot(type, item, minAmt, maxAmt, weight);
                              }
                              catch(NumberFormatException e) {
                                 sender.sendPlayerMessage(StringUtils.invalidAmount.replace("{amt}", args[4]));
                              }
                           }
                           catch(NumberFormatException e) {
                              sender.sendPlayerMessage(StringUtils.invalidAmount.replace("{amt}", args[3]));
                           }
                        }
                        catch(NumberFormatException e) {
                           sender.sendPlayerMessage(StringUtils.invalidAmount.replace("{amt}", args[2]));
                        }
                     else
                        sender.sendPlayerMessage(StringUtils.invalidMobType);

                     return false;
                  }
                  case "v":
                  case "d":
                  case "display":
                  case "view": {
                     String type = args[1].toLowerCase();
                     if(type.equalsIgnoreCase("zombie") || type.equalsIgnoreCase("skeleton") || type.equalsIgnoreCase("enderman") || type.equalsIgnoreCase("villager") || type.equalsIgnoreCase("cop")) {
                        type = StringUtils.capitalizeFirst(type);
                        displayLoot(sendr, type);
                        return true;
                     }
                     sender.sendPlayerMessage(StringUtils.invalidMobType);
                     return false;
                  }
                  case "gr":
                  case "giver":
                  case "giverandom": {
                     String type = args[1].toLowerCase();
                     if(type.equalsIgnoreCase("zombie") || type.equalsIgnoreCase("skeleton") || type.equalsIgnoreCase("enderman") || type.equalsIgnoreCase("villager") || type.equalsIgnoreCase("cop")) {
                        type = StringUtils.capitalizeFirst(type);
                        if(!giveRandomMobLoot(type, sender)) {
                           sender.sendPlayerMessage(StringUtils.giveRandomItemUnSuccessful);
                           return false;
                        }
                     }
                     sender.sendPlayerMessage(StringUtils.invalidMobType);
                     return false;
                  }
                  case "rel":
                  case "reload": {
                     if(args.length == 1) {
                        LootingUtils.reloadMobLoot();
                        return true;
                     }
                     String type = args[1].toLowerCase();
                     if(type.equalsIgnoreCase("zombie") || type.equalsIgnoreCase("skeleton") || type.equalsIgnoreCase("enderman") || type.equalsIgnoreCase("villager") || type.equalsIgnoreCase("cop")) {
                        type = StringUtils.capitalizeFirst(type);
                        LootingUtils.reloadSpecificMobLoot(type);
                        return true;
                     }
                     sender.sendPlayerMessage(StringUtils.invalidMobType);
                     return false;
                  }
                  case "w":
                  case "world": {
                     if(args.length == 2){
                        if(args[1].equalsIgnoreCase("activate")){
                           return world(sender, sendr.getWorld(),true, true);
                        }
                        else if(args[1].equalsIgnoreCase("deactivate")){
                           return world(sender, sendr.getWorld(),false, true);
                        }
                     }
                     else if(args.length == 3){
                        if(args[1].equalsIgnoreCase("activate")){
                           return world(sender, Bukkit.getWorld(args[2]),true, false);

                        }
                        else if(args[1].equalsIgnoreCase("deactivate")){
                           return world(sender, Bukkit.getWorld(args[2]),false, false);
                        }
                     }
                     else{
                        sender.sendPlayerMessage(StringUtils.worldUsage);
                     }
                  }
               }
            }
         }
      }
      return false;
   }

   /**
    * Adds a new LootItem to the loot table with the specified characteristics
    *
    * @param item    the ItemStack that will be given to the player in the loot chest
    * @param minAmt  the minimum amount of the item to be given
    * @param maxAmt  the maximum amount of the item to be given
    * @param weight  the weight of this item in the loot pool
    * @return     false if the exact item is in the loot pool already, true otherwise.
    */
   private boolean addLootChest(ItemStack item, short minAmt, short maxAmt, double weight) {
      LootItem x = new LootItem(item, minAmt, maxAmt, weight);
      return LootingUtils.addItemToLootPool(x, "Loot");
   }

   /**
    * Opens the speicifed type of GUI or the viewing Player.
    *
    * @param viewer  The player viewing this inventory
    * @param type    The type of loot pool that is going to be viewed.
    */
   private void displayLoot(Player viewer, String type) {
      GUIS.firstPage(viewer, type);
   }

   /**
    * Offers the specified user a random item from the chest loot pool.
    *
    * @param receiver   The user that is receiving the item
    * @return  true if the user received the item, false otherwise
    */
   private boolean giveRandomChestLoot(User receiver) {
      if(PlayerUtils.offerPlayerItem((Player) receiver.getPLAYER(), LootingUtils.getRandom("Loot").getItem())) {
         receiver.sendPlayerMessage(StringUtils.randomItemSuccessful);
         return true;
      }
      return false;
   }

   /**
    * Adds a new LootITem to the specified mob loot type with the specified characteristics
    *
    * @param type    The type of mob that we are adding this loot poool to.
    * @param minAmt  the minimum amount of the item to be given
    * @param maxAmt  the maximum amount of the item to be given
    * @param weight  the weight of this item in the loot pool
    * @return     false if the exact item is in the loot pool already, true otherwise.
    */
   private boolean addMobLoot(String type, ItemStack item, short minAmt, short maxAmt, double weight) {
      LootItem x = new LootItem(item, minAmt, maxAmt, weight);
      type = StringUtils.capitalizeFirst(type);
      return LootingUtils.addItemToLootPool(x, type);
   }

   /**
    * Offers the specified user a random item from the specified mob loot pool.
    *
    * @param type       The type of mob that we are pulling from.
    * @param receiver   The user that is receiving the item
    * @return  true if the user received the item, false otherwise
    */
   private boolean giveRandomMobLoot(String type, User receiver) {
      if(PlayerUtils.offerPlayerItem((Player) receiver.getPLAYER(), LootingUtils.getRandom(type).getItem())) {
         switch(type) {
            case "Zombie": {
               receiver.sendPlayerMessage(StringUtils.randomZomItemSuccessful);
               return true;
            }
            case "Skeleton": {
               receiver.sendPlayerMessage(StringUtils.randomSkelItemSuccessful);
               return true;
            }
            case "Enderman": {
               receiver.sendPlayerMessage(StringUtils.randomEndItemSuccessful);
               return true;
            }
            case "Villager": {
               receiver.sendPlayerMessage(StringUtils.randomVillItemSuccessful);
               return true;
            }
            case "Cop": {
               receiver.sendPlayerMessage(StringUtils.randomCopItemSuccessful);
               return true;
            }
         }
      }

      return false;
   }

   private boolean world(User u, World world, boolean activate, boolean lootEnabling) {
      if(lootEnabling) {
         if(world != null) {
            if(activate) {
               LootingUtils.lootActivateWorld(world);
               u.sendPlayerMessage(StringUtils.lootActivatedWorld.replace("{w}", world.getName()));
            }
            else {
               LootingUtils.lootDeactivateWorld(world);
               u.sendPlayerMessage(StringUtils.lootDeactivatedWorld.replace("{w}", world.getName()));
            }
            return true;
         }
         else {
            u.sendPlayerMessage(StringUtils.notAWorld);
         }
      }
      else{
         if(world != null) {
            if(activate) {
               LootingUtils.mobActivateWorld(world);
               u.sendPlayerMessage(StringUtils.mobDeactivatedWorld.replace("{w}", world.getName()));
            }
            else {
               LootingUtils.mobDeactivateWorld(world);
               u.sendPlayerMessage(StringUtils.mobDeactivatedWorld.replace("{w}", world.getName()));
            }
            return true;
         }
         else {
            u.sendPlayerMessage(StringUtils.notAWorld);
         }
      }
      return false;
   }
}
