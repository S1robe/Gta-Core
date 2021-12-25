package me.Strobe.Core.Commands;

import me.Strobe.Core.Main;
import me.Strobe.Core.Utils.CopUtils;
import me.Strobe.Core.Utils.Displays.ScoreboardManager;
import me.Strobe.Core.Utils.PlayerUtils;
import me.Strobe.Core.Utils.StringUtils;
import me.Strobe.Core.Utils.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.Arrays;
import java.util.Locale;

public class UserRelatedCommands implements CommandExecutor {

   @Override
   public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
      if(commandSender instanceof Player) {
         Player p = (Player) commandSender;
         User sender = User.getByPlayer(p);
         switch(s.toLowerCase()) {
            // /stats <PVP,PVE, ALL> [Player]
            case "stats": {
               if(args.length < 1) {
                  StringUtils.sendCommandMessage(commandSender, StringUtils.statsUsage);
                  return false;
               }
               else {
                  return stats(sender, args);
               }
            }
            // /cop
            case "cop": {
               //cop setsafehouse
               //cop setinventory
               //cop setmaxspawns <num>
               //cop setminspawns <num>
               if(args.length == 0){
                  return cop(sender);
               }
               switch(args[0].toLowerCase()){
                  case "setsafehouse":
                     CopUtils.safeHouse = p.getLocation();
                     sender.sendPlayerMessage(StringUtils.copSetSafeHouse.replace("{w}", p.getWorld().getName()).replace("{x}", ""+p.getLocation().getBlockX()).replace("{y}",""+ p.getLocation().getBlockY()).replace("{z}", ""+p.getLocation().getBlockZ()));
                     CopUtils.save();
                     break;
                  case "setinventory":
                     CopUtils.inventory.clear();
                     CopUtils.inventory.addAll(Arrays.asList(p.getInventory().getArmorContents()));
                     CopUtils.inventory.addAll(Arrays.asList(p.getInventory().getContents()));
                     Main.getMain().getCopDataFile().saveCustomConfig();
                     sender.sendPlayerMessage(StringUtils.copSetInventory);
                     CopUtils.save();
                     break;
                  case "setmaxspawns":
                     CopUtils.maxCopSpawn = Integer.parseInt(args[1]);
                     sender.sendPlayerMessage(StringUtils.copSetMax.replace("{n}", args[1]));
                     CopUtils.save();
                     break;
                  case "setminspawns":
                     CopUtils.minCopSpawn = Integer.parseInt(args[1].replace("{n}", args[1]));
                     sender.sendPlayerMessage(StringUtils.copSetMin.replace("{n}", args[1]));
                     CopUtils.save();
                     break;
                  case "blockcmd":
                     if(!CopUtils.blackListedCommands.contains(args[1])){
                        CopUtils.blackListedCommands.add(args[1]);
                        sender.sendPlayerMessage(CopUtils.blackListedCommands.toString());
                        CopUtils.save();
                     }
                     break;
                  case "allowcmd":
                     if(CopUtils.blackListedCommands.contains(args[1])){
                        CopUtils.blackListedCommands.remove(args[1]);
                        sender.sendPlayerMessage(CopUtils.blackListedCommands.toString());
                        CopUtils.save();
                     }
                     break;
                  case "settimeout":
                     CopUtils.modeChangeTimeout = Integer.parseInt(args[1].replace("{n}", args[1]));
                     sender.sendPlayerMessage(StringUtils.copSetTimechange.replace("{n}", args[1]));
                     CopUtils.save();
                     break;
                  case "viewinventory":
                     Inventory inv = Bukkit.createInventory(null, 45);
                     inv.setContents(CopUtils.inventory.toArray(new ItemStack[0]));
                     p.openInventory(inv);
                  default:
                     sender.sendPlayerMessage(StringUtils.copSetInventoryUsage);
                     sender.sendPlayerMessage(StringUtils.copSetMaxSpawnsUsage);
                     sender.sendPlayerMessage(StringUtils.copSetMaxSpawnsUsage);
                     sender.sendPlayerMessage(StringUtils.copSetSafehouseUsage);

               }
            }
            // /gta
            case "gta": {
               if(args.length == 0){
                  sender.sendPlayerBulkMessage(StringUtils.help);
               }
               switch(args[0].toLowerCase(Locale.ROOT)){
                  case "help":{

                  }
                  // /gta stats <stat> <add/remove/reset> (name)
                  case "stats":{
                     if(args.length < 2){
                        sender.sendPlayerBulkMessage(StringUtils.gtastatsUsage);
                        return false;
                     }
                     switch(args[1].toLowerCase()){
                        case "pigcopkill":
                        case "pck":
                           break;
                        case "death":
                        case "d":
                           break;
                        case "kills":
                        case "k":
                           break;
                        case "mobkills":
                        case "mk":
                           break;
                        case "skeletonkills":
                        case "sk":
                           break;
                        case "zombiekills":
                        case "zk":
                           break;
                        case "mobdeaths":
                        case "md":
                           break;
                        case "wantedlevel":
                        case "wanted":
                        case "wl":
                        case "w":
                           break;
                        case "witherskeletonkills":
                        case "wsk":
                           break;
                        case "villagerkills":
                        case "vk":
                           break;
                        case "copkills":
                        case "ck":
                           break;
                        case "endermankills":
                        case "ek":
                           break;
                        case "killstreak":
                        case "ks":
                           break;
                     }
                  }
                  case "":
               }
            }
         }
      }
      return false;
   }

   private boolean stats(User sender, String... args) {

      int type = args[0].equalsIgnoreCase("All")? 0:  args[0].equalsIgnoreCase("PVP")? 1 : args[0].equalsIgnoreCase("PVE")? 2 : args[0].equalsIgnoreCase("OFF")? 3 : args[0].equalsIgnoreCase("ON")? 4 : -1;
      if(args.length >= 2){
         OfflinePlayer other  = PlayerUtils.getOfflinePlayer(args[1]);
         if(other == null){
            sender.sendPlayerMessage(StringUtils.invalidPlayer.replace("{plr}", args[1]));
            return false;
         }
         if(type == 3 || type == 4 || type == -1) {
            sender.sendPlayerMessage(StringUtils.invalidBoardType);
            return false;
         }
         sender.viewOtherPlayerStats(other, type);
      }
      switch(type){
         case 3:
            sender.sendPlayerMessage("&e&l(!)&7 Stats disabled.");
            ScoreboardManager.getBoardAndRemoveByUser(sender);
            sender.getPLAYER().getPlayer().getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
            break;
         case 4:
            if(sender.getCurrentBoard() == 3){
               sender.setCurrentBoard(1);
               ScoreboardManager.createScoreBoard(sender);
               sender.sendPlayerMessage("&e&l(!)&7 Stats enabled.");
               return true;
            }
            break;
         case -1:
            sender.sendPlayerMessage(StringUtils.invalidBoardType);
            return false;
      }
      sender.setCurrentBoard(type);
      return true;
   }

   private boolean cop(User executor) {
      return CopUtils.attemptChangeToFromCop(executor);
   }

   private boolean gtastats(User sender, String type, String typeOfEdit, String PlayerName, double amt){
      User other = User.getByName(PlayerName);
      if(other == null && PlayerName!=null){
         sender.sendPlayerMessage(StringUtils.invalidPlayer.replace("{plr}", PlayerName));
         return false;
      }
      if(PlayerName == null)
         switch(type){
            case "pck":
               switch(typeOfEdit.toLowerCase()){

               }
               break;
            case "d":
               break;
            case "k":
               break;
            case "mk":
               break;
            case "sk":
               break;
            case "zk":
               break;
            case "md":
               break;
            case "w":
               break;
            case "wsk":
               break;
            case "vk":
               break;
            case "ck":
               break;
            case "ek":
               break;
            case "ks":
               break;
         }
      if(other != null)
         switch(type){
            case "pck":
               break;
            case "d":
               break;
            case "k":
               break;
            case "mk":
               break;
            case "sk":
               break;
            case "zk":
               break;
            case "md":
               break;
            case "w":
               break;
            case "wsk":
               break;
            case "vk":
               break;
            case "ck":
               break;
            case "ek":
               break;
            case "ks":
               break;
         }
   }

}
