package me.Strobe.Core.Commands;

import me.Strobe.Core.Utils.Displays.GUIS;
import me.Strobe.Core.Utils.Looting.LootItem;
import me.Strobe.Core.Utils.LootingUtils;
import me.Strobe.Core.Utils.PlayerUtils;
import me.Strobe.Core.Utils.StringUtils;
import me.Strobe.Core.Utils.User;
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
            if(s.equalsIgnoreCase("loot")){
               StringUtils.sendCommandMessage(commandSender, StringUtils.Text.LOOT_USAGE.create());
            }
            return true;
         }
         Player sendr = (Player) commandSender;
         User sender = User.getByPlayer(sendr);
         if(s.equalsIgnoreCase("loot")){
            String type = StringUtils.capitalizeFirst(args[0].toLowerCase());
            switch(type){
               case "Reload":{
                  if(!sendr.hasPermission("loot.admin")) return false;
                  LootingUtils.reloadAllLoot();
                  sender.sendPlayerMessage(StringUtils.Text.ALL_LOOT_RELOAD.create());
                  break;
               }
               case "Time": {
                  if(!sendr.hasPermission("loot.admin")) return false;
                  try {
                     LootingUtils.setChestResetTime(Integer.parseInt(args[1]));
                     sender.sendPlayerMessage(StringUtils.Text.LOOT_TIME_SET.create(args[1]));
                  }
                  catch(NumberFormatException e) {
                     sender.sendPlayerMessage(StringUtils.Text.INVALID_AMOUNT.create(args[1]));
                  }
                  break;
               }
               case "World": {
                  if(!sendr.hasPermission("loot.admin")) return false;
                  if(args.length == 3){
                     if(args[1].equalsIgnoreCase("activate"))
                        return world(sender, sendr.getWorld(),true, Boolean.parseBoolean(args[2]));
                     else if(args[1].equalsIgnoreCase("deactivate"))
                        return world(sender, sendr.getWorld(),false, Boolean.parseBoolean(args[2]));
                     else
                        sender.sendPlayerMessage(StringUtils.Text.LOOT_WORLD_USAGE.create());
                  }
                  else if(args.length == 4){
                     if(args[1].equalsIgnoreCase("activate"))
                        return world(sender, Bukkit.getWorld(args[2]),true, Boolean.parseBoolean(args[3]));
                     else if(args[1].equalsIgnoreCase("deactivate"))
                        return world(sender, Bukkit.getWorld(args[2]),false, Boolean.parseBoolean(args[3]));
                     else
                        sender.sendPlayerMessage(StringUtils.Text.LOOT_WORLD_USAGE.create());
                  }
                  else
                     sender.sendPlayerMessage(StringUtils.Text.LOOT_WORLD_USAGE.create());
                  break;
               }
               case "Z":
               case "Zombie":
                  return processLootCommand("Zombie", sendr, sender, args);
               case "S":
               case "Skeleton":
                  return processLootCommand("Skeleton", sendr, sender, args);
               case "E":
               case "Enderman":
                  return processLootCommand("Enderman", sendr, sender, args);
               case "V":
               case "Villager":
                  return processLootCommand("Villager", sendr, sender, args);
               case "C":
               case "Chest":
                  return processLootCommand("Chest", sendr, sender, args);
               default:
                  sender.sendPlayerMessage(StringUtils.Text.LOOT_USAGE.create());
            }
         }
      }
      return false;
   }

   private boolean processLootCommand(String type, Player sendr, User sender, String[] args){
      if(args.length < 2) {
         if(sendr.hasPermission("loot.admin"))
            sender.sendPlayerMessage(StringUtils.Text.LOOT_USAGE.create());
         else
            sender.sendPlayerMessage(StringUtils.Text.LOOT_VIEW_USAGE.create());
         return false;
      }
      switch(args[1].toLowerCase()) {
         // /loot <type> add min max weight
         case "add": {
            if(!sendr.hasPermission("loot.admin")) return false;
            try {
               short minAmt = Short.parseShort(args[2]);
               try {
                  short maxAmt = Short.parseShort(args[3]);
                  try {
                     double weight = Double.parseDouble(args[4]);
                     ItemStack item = sendr.getItemInHand();
                     if(sendr.getItemInHand().getType().equals(Material.AIR)) {
                        sender.sendPlayerMessage(StringUtils.Text.INVALID_ITEM.create());
                        return false;
                     }
                     sender.sendPlayerMessage(StringUtils.Text.SUCCESS_LOOT_ADD.create(type));
                     return addLoot(item, type, minAmt, maxAmt, weight);
                  }
                  catch(NumberFormatException e) {
                     sender.sendPlayerMessage(StringUtils.Text.INVALID_AMOUNT.create( args[3]));
                  }
               }
               catch(NumberFormatException e) {
                  sender.sendPlayerMessage(StringUtils.Text.INVALID_AMOUNT.create( args[2]));
               }
            }
            catch(NumberFormatException e) {
               sender.sendPlayerMessage(StringUtils.Text.INVALID_AMOUNT.create( args[1]));
            }
            return false;
         }
         // /loot <type> view
         case "view": {
            displayLoot(sendr, type);
            return true;
         }
         // /loot giverandom <type>
         case "giverandom": {
            if(!sendr.hasPermission("loot.admin")) return false;
            if(giveRandomLoot(sender, type)) {
               sender.sendPlayerMessage(StringUtils.Text.SUCCESS_ITEM_DELIVERED.create());
               return true;
            }
            else
               sender.sendPlayerMessage(StringUtils.Text.FAIL_ITEM_DELIVERED.create());
            return false;
         }
         // /loot reload <type>
         case "reload": {
            if(!sendr.hasPermission("loot.admin")) return false;
            LootingUtils.reloadSpecificLoot(type);
            sender.sendPlayerMessage(StringUtils.Text.LOOT_POOL_RELOADED.create(type));
            return true;
         }
         // /loot <type> min <amt>
         case "min":
            if(!sendr.hasPermission("loot.admin")) return false;
            try{
               LootingUtils.setMinDrop(type, Integer.parseInt(args[3]));
               sender.sendPlayerMessage(StringUtils.Text.LOOT_SET_MIN.create(args[3]));
            }
            catch(NumberFormatException e) {
               sender.sendPlayerMessage(StringUtils.Text.INVALID_AMOUNT.create( args[3]));
            }
            return true;
         // /loot <type> max  <amt>
         case "max":
            if(!sendr.hasPermission("loot.admin")) return false;
            try{
               LootingUtils.setMaxDrop(type, Integer.parseInt(args[3]));
               sender.sendPlayerMessage(StringUtils.Text.LOOT_SET_MAX.create(args[3]));
            }
            catch(NumberFormatException e) {
               sender.sendPlayerMessage(StringUtils.Text.INVALID_AMOUNT.create( args[3]));
            }
            return true;
         default:
            if(sendr.hasPermission("loot.admin"))
               sender.sendPlayerMessage(StringUtils.Text.LOOT_USAGE.create());
            else
               sender.sendPlayerMessage(StringUtils.Text.LOOT_VIEW_USAGE.create());
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
   private boolean addLoot(ItemStack item, String type, short minAmt, short maxAmt, double weight) {
      LootItem x = new LootItem(item, minAmt, maxAmt, weight);
      return LootingUtils.addItemToLootPool(x, type);
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
   private boolean giveRandomLoot(User receiver, String type) {
      if(PlayerUtils.offerPlayerItem((Player) receiver.getPLAYER(), LootingUtils.getRandom(type).getItem())) {
         receiver.sendPlayerMessage(StringUtils.Text.ITEM_DELIVERED_LOOTPOOL.create(type));
         return true;
      }
      return false;
   }

   private boolean world(User u, World world, boolean activate, boolean lootEnabling) {
      if(lootEnabling)
         if(world != null) {
            if(activate) {
               LootingUtils.lootActivateWorld(world);
               u.sendPlayerMessage(StringUtils.Text.LOOT_WORLD_ACTIVE.create(world.getName()));
            }
            else {
               LootingUtils.lootDeactivateWorld(world);
               u.sendPlayerMessage(StringUtils.Text.LOOT_WORLD_DEACTIVE.create(world.getName()));
            }
            return true;
         }
         else
            u.sendPlayerMessage(StringUtils.Text.NOT_WORLD.create());
      else
         if(world != null) {
            if(activate) {
               LootingUtils.mobActivateWorld(world);
               u.sendPlayerMessage(StringUtils.Text.MOB_WORLD_ACTIVE.create( world.getName()));
            }
            else {
               LootingUtils.mobDeactivateWorld(world);
               u.sendPlayerMessage(StringUtils.Text.MOB_WORLD_DEACTIVE.create( world.getName()));
            }
            return true;
         }
         else
            u.sendPlayerMessage(StringUtils.Text.NOT_WORLD.create());
      return false;
   }
}
