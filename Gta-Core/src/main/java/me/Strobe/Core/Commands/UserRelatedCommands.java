package me.Strobe.Core.Commands;

import me.Strobe.Core.Main;
import me.Strobe.Core.Utils.*;
import me.Strobe.Core.Utils.Displays.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Arrays;

public class UserRelatedCommands implements CommandExecutor {

   @Override
   public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
      if(commandSender instanceof Player) {
         Player p = (Player) commandSender;
         User sender = User.getByPlayer(p);
         switch(s.toLowerCase()) {
            case "stats": {
               if(args.length < 1) {
                  StringUtils.sendCommandMessage(commandSender, StringUtils.Text.STATS_USAGE.create());
                  return false;
               }
               else {
                  int type = args[0].equalsIgnoreCase("All")? 0:
                                     args[0].equalsIgnoreCase("PVP")? 1 :
                                             args[0].equalsIgnoreCase("PVE")? 2 :
                                                     args[0].equalsIgnoreCase("OFF")? 3 :
                                                             args[0].equalsIgnoreCase("ON")? 4 :
                                                                     -1;
                  if(type == -1) {
                     sender.sendPlayerMessage(StringUtils.Text.INVALID_STATS.create());
                     return false;
                  }
                  OfflinePlayer x =  args.length >= 2 ? PlayerUtils.getOfflinePlayer(args[1]) : null;
                  User other = x != null? User.getByOfflinePlayer(x) : null;
                  return stats(sender, type, other);
               }
            }
            case "cop": {
               //cop setsafehouse
               //cop setinventory
               //cop setmaxspawns <num>
               //cop setminspawns <num>
               if(args.length == 0) {
                  return cop(sender);
               }
               switch(args[0].toLowerCase()) {
                  case "setsafehouse":
                     CopUtils.safeHouse = p.getLocation();
                     sender.sendPlayerMessage(StringUtils.Text.COP_SET_INV.create(p.getWorld().getName(), "" + p.getLocation().getBlockX(), "" + p.getLocation().getBlockY(), "" + p.getLocation().getBlockZ()));
                     CopUtils.save();
                     break;
                  case "setinventory":
                     CopUtils.inventory.clear();
                     CopUtils.inventory.addAll(Arrays.asList(p.getInventory().getArmorContents()));
                     CopUtils.inventory.addAll(Arrays.asList(p.getInventory().getContents()));
                     Main.getMain().getCopDataFile().saveCustomConfig();
                     sender.sendPlayerMessage(StringUtils.Text.COP_SET_INV.create());
                     CopUtils.save();
                     break;
                  case "setmaxspawns":
                     CopUtils.maxCopSpawn = Integer.parseInt(args[1]);
                     sender.sendPlayerMessage(StringUtils.Text.COP_SET_MAX.create("{n}", args[1]));
                     CopUtils.save();
                     break;
                  case "setminspawns":
                     CopUtils.minCopSpawn = Integer.parseInt(args[1].replace("{n}", args[1]));
                     sender.sendPlayerMessage(StringUtils.Text.COP_SET_MIN.create("{n}", args[1]));
                     CopUtils.save();
                     break;
                  case "blockcmd":
                     if(!CopUtils.blackListedCommands.contains(args[1])) {
                        CopUtils.blackListedCommands.add(args[1]);
                        sender.sendPlayerMessage(StringUtils.Text.COP_BLOCKCMD.create(args[1]));
                        CopUtils.save();
                     }
                     break;
                  case "allowcmd":
                     if(CopUtils.blackListedCommands.contains(args[1])) {
                        CopUtils.blackListedCommands.remove(args[1]);
                        sender.sendPlayerMessage(StringUtils.Text.COP_ALLOWCMD.create(args[1]));
                        CopUtils.save();
                     }
                     break;
                  case "settimeout":
                     CopUtils.modeChangeTimeout = Integer.parseInt(args[1].replace("{n}", args[1]));
                     sender.sendPlayerMessage(StringUtils.Text.COP_SET_TIMEOUT.create( args[1]));
                     CopUtils.save();
                     break;
                  case "viewinventory":
                     Inventory inv = Bukkit.createInventory(null, 45);
                     inv.setContents(CopUtils.inventory.toArray(new ItemStack[0]));
                     p.openInventory(inv);
                     break;
                  case "spawn":
                     Player x = Bukkit.getPlayerExact(args[1]);
                     User u = x != null? User.getByPlayer(x) : null;
                     if(u == null){
                        sender.sendPlayerMessage(StringUtils.Text.INVALID_PLAYER.create(args[1]));
                        return false;
                     }
                     else{
                        try {
                           // /cop spawn <amt> <waves>
                           CopUtils.spawnCopsOnPlayer(u, Integer.parseInt(args[2]), Integer.parseInt(args[3]));
                           sender.sendPlayerMessage(StringUtils.Text.COP_SUCCESS_SPAWN.create(args[2], args[1]));
                           return true;
                        }
                        catch(NumberFormatException e){
                           sender.sendPlayerMessage(StringUtils.Text.INVALID_AMOUNT.create());
                           return false;
                        }
                     }

                  default:
                     sender.sendPlayerMessage(StringUtils.Text.COP_USAGE.create());
               }
            }
            case "gta": {
               // /gta pck add 2
               if(args.length == 3)
                  return gta(sender, args[0], args[1], null, Integer.parseInt(args[2]));
               else if(args.length == 4)
                  return gta(sender, args[0], args[1], args[2], Integer.parseInt(args[3]));
            }
            case "help":{
               if(args.length > 0)
                  switch(args[0].toLowerCase()){
                     case "guns":
                        HelpUtils.sendGunsHelp(p);
                        break;
                     case "cheatcodes":
                        HelpUtils.sendCheatcodeHelp(p);
                        break;
                     case "housing":
                        HelpUtils.sendHousingHelp(p);
                        break;
                     case "looting":
                        HelpUtils.sendLootingHelp(p);
                        break;
                     case "trading":
                        HelpUtils.sendTradeHelp(p);
                        break;
                     case "bank":
                        HelpUtils.sendBankHelp(p);
                        break;
                     case "robbery":
                        HelpUtils.sendRobberyHelp(p);
                        break;
                     case "2":
                        HelpUtils.sendHelpMessage(p, 2);
                        break;
                     default:
                        sender.sendPlayerMessage(StringUtils.Text.HELP.create());
                        HelpUtils.sendHelpMessage(p, 1);
                  }
               else {
                  sender.sendPlayerMessage(StringUtils.Text.HELP.create());
                  HelpUtils.sendHelpMessage(p, 1);
               }
               return true;
            }
            // /gta
            //            case "gta": {
            //               if(args.length == 0){
            //                  sender.sendPlayerBulkMessage(StringUtils.help);
            //               }
            //               switch(args[0].toLowerCase(Locale.ROOT)){
            //                  case "help":{
            //
            //                  }
            //                  // /gta stats <stat> <add/remove/reset> (name)
            //                  case "stats":{
            //                     if(args.length < 2){
            //                        sender.sendPlayerBulkMessage(StringUtils.gtastatsUsage);
            //                        return false;
            //                     }
            //                     switch(args[1].toLowerCase()){
            //                        case "pigcopkill":
            //                        case "pck":
            //                           break;
            //                        case "death":
            //                        case "d":
            //                           break;
            //                        case "kills":
            //                        case "k":
            //                           break;
            //                        case "mobkills":
            //                        case "mk":
            //                           break;
            //                        case "skeletonkills":
            //                        case "sk":
            //                           break;
            //                        case "zombiekills":
            //                        case "zk":
            //                           break;
            //                        case "mobdeaths":
            //                        case "md":
            //                           break;
            //                        case "wantedlevel":
            //                        case "wanted":
            //                        case "wl":
            //                        case "w":
            //                           break;
            //                        case "witherskeletonkills":
            //                        case "wsk":
            //                           break;
            //                        case "villagerkills":
            //                        case "vk":
            //                           break;
            //                        case "copkills":
            //                        case "ck":
            //                           break;
            //                        case "endermankills":
            //                        case "ek":
            //                           break;
            //                        case "killstreak":
            //                        case "ks":
            //                           break;
            //                     }
            //                  }
         }
      }
      return false;
   }

   private boolean stats(User sender, int type, User other) {
      if(sender.getPLAYER().getPlayer().hasMetadata("VIEWINGOTHER")) return false;
      if(other == null){
         switch(type){
            //all
            case 0:
               if(sender.getCurrentBoard() == 3){
                  ScoreboardManager.createScoreBoard(sender);
               }
               sender.sendPlayerMessage(StringUtils.Text.STATS_TO_ALL.create());
               break;
               // pvp
            case 1:
               if(sender.getCurrentBoard() == 3){
                  ScoreboardManager.createScoreBoard(sender);
               }
               sender.sendPlayerMessage(StringUtils.Text.STATS_TO_PVP.create());
               break;
               //pve
            case 2:
               if(sender.getCurrentBoard() == 3){
                  ScoreboardManager.createScoreBoard(sender);
               }
               sender.sendPlayerMessage(StringUtils.Text.STATS_TO_PVE.create());
               break;
               //off
            case 3:
               Scoreboard score = ScoreboardManager.getBoardAndRemoveByUser(sender);
               score.clearSlot(DisplaySlot.SIDEBAR);
               score.getObjectives().forEach(Objective::unregister);
               sender.setCurrentBoard(type);
               sender.sendPlayerMessage(StringUtils.Text.STATS_OFF.create());
               return true;
               //on -> pvp
            case 4:
               if(sender.getCurrentBoard() == 3){
                  sender.setCurrentBoard(1);
                  ScoreboardManager.createScoreBoard(sender);
                  sender.sendPlayerMessage(StringUtils.Text.STATS_ON.create());
               }
               else
                  sender.sendPlayerMessage(StringUtils.Text.STATS_ALREADY_ON.create());
               return true;
            default:
               sender.sendPlayerMessage(StringUtils.Text.STATS_USAGE.create());
               return false;
         }
         sender.setCurrentBoard(type);
         return true;
      }
      else {
         if(type == 3 || type == 4){
            sender.sendPlayerMessage(StringUtils.Text.INVALID_STATS.create());
            return false;
         }
         if(sender.getCurrentBoard() == 3){
            sender.setCurrentBoard(1);
            ScoreboardManager.createScoreBoard(sender);
         }
         sender.viewOtherPlayerStats(sender, other, type);
      }
      return false;
   }

   private boolean cop(User executor) {
      return CopUtils.attemptChangeToFromCop(executor);
   }


   private boolean gta(User sender, String stat, String addOrSub, String PlayerName, int amt){
      if(PlayerName == null)
         switch(stat){
            case "pigcopkill":
            case "pck":
               if(addOrSub.equalsIgnoreCase("add"))
                  sender.addNPCCopKill(amt);
               else
                  sender.removeNPCCopKills(amt);
               break;
            case "death":
            case "d":
               if(addOrSub.equalsIgnoreCase("add"))
                  sender.addDeaths(amt);
               else
                  sender.removeDeaths(amt);
               break;
            case "kills":
            case "k":
               if(addOrSub.equalsIgnoreCase("add"))
                  sender.addPVPKills(amt);
               else
                  sender.removePVPKills(amt);
               break;
            case "skeletonkills":
            case "sk":
               if(addOrSub.equalsIgnoreCase("add"))
                  sender.addSkelKills(amt);
               else
                  sender.removeSkelKills(amt);
               break;
            case "zombiekills":
            case "zk":
               if(addOrSub.equalsIgnoreCase("add"))
                  sender.addZomKill(amt);
               else
                  sender.removeNPCCopKills(amt);
               break;
            case "mobdeaths":
            case "md":
               if(addOrSub.equalsIgnoreCase("add"))
                  sender.addMobDeaths(amt);
               else
                  sender.removeMobDeath(amt);
               break;
            case "wantedlevel":
            case "wanted":
            case "wl":
            case "w":
               if(addOrSub.equalsIgnoreCase("add"))
                  sender.addWantedLevel(amt);
               else
                  sender.removeWantedLevel(amt);
               break;
            case "witherskeletonkills":
            case "wsk":
               if(addOrSub.equalsIgnoreCase("add"))
                  sender.addWithSkelKills(amt);
               else
                  sender.removeWithSkelKills(amt);
               break;
            case "villagerkills":
            case "vk":
               if(addOrSub.equalsIgnoreCase("add"))
                  sender.addVillKills(amt);
               else
                  sender.removeVillagerKills(amt);
               break;
            case "copkills":
            case "ck":
               if(addOrSub.equalsIgnoreCase("add"))
                  sender.addCopKills(amt);
               else
                  sender.removeCopKills(amt);
               break;
            case "endermankills":
            case "ek":
               if(addOrSub.equalsIgnoreCase("add"))
                  sender.addEndKill(amt);
               else
                  sender.removeEndKills(amt);
               break;
         }
      OfflinePlayer other = PlayerUtils.getOfflinePlayer(PlayerName);

      if(other != null) {
         User Uother = User.getByOfflinePlayer(other);
         if(Uother != null)
            switch(stat) {
               case "pigcopkill":
               case "pck":
                  if(addOrSub.equalsIgnoreCase("add"))
                     Uother.addNPCCopKill(amt);
                  else
                     Uother.removeNPCCopKills(amt);
                  break;
               case "death":
               case "d":
                  if(addOrSub.equalsIgnoreCase("add"))
                     Uother.addDeaths(amt);
                  else
                     Uother.removeDeaths(amt);
                  break;
               case "kills":
               case "k":
                  if(addOrSub.equalsIgnoreCase("add"))
                     Uother.addPVPKills(amt);
                  else
                     Uother.removePVPKills(amt);
                  break;
               case "skeletonkills":
               case "sk":
                  if(addOrSub.equalsIgnoreCase("add"))
                     Uother.addSkelKills(amt);
                  else
                     Uother.removeSkelKills(amt);
                  break;
               case "zombiekills":
               case "zk":
                  if(addOrSub.equalsIgnoreCase("add"))
                     Uother.addZomKill(amt);
                  else
                     Uother.removeZomKills(amt);
                  break;
               case "mobdeaths":
               case "md":
                  if(addOrSub.equalsIgnoreCase("add"))
                     Uother.addMobDeaths(amt);
                  else
                     Uother.removeMobDeath(amt);
                  break;
               case "wantedlevel":
               case "wanted":
               case "wl":
               case "w":
                  if(addOrSub.equalsIgnoreCase("add"))
                     Uother.addWantedLevel(amt);
                  else
                     Uother.removeWantedLevel(amt);
                  break;
               case "witherskeletonkills":
               case "wsk":
                  if(addOrSub.equalsIgnoreCase("add"))
                     Uother.addWithSkelKills(amt);
                  else
                     Uother.removeWithSkelKills(amt);
                  break;
               case "villagerkills":
               case "vk":
                  if(addOrSub.equalsIgnoreCase("add"))
                     Uother.addVillKills(amt);
                  else
                     Uother.removeVillagerKills(amt);
                  break;
               case "copkills":
               case "ck":
                  if(addOrSub.equalsIgnoreCase("add"))
                     Uother.addCopKills(amt);
                  else
                     Uother.removeCopKills(amt);
                  break;
               case "endermankills":
               case "ek":
                  if(addOrSub.equalsIgnoreCase("add"))
                     Uother.addEndKill(amt);
                  else
                     Uother.removeEndKills(amt);
                  break;
            }
      }
      return false;
   }

}
