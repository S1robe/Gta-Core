package me.Strobe.Housing.Commands;

import me.Strobe.Core.Utils.PlayerUtils;
import me.Strobe.Housing.House;
import me.Strobe.Housing.Utils.Displays.GUIS;
import me.Strobe.Housing.Utils.HouseUtils;
import me.Strobe.Housing.Utils.StringUtils;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HouseCommands implements CommandExecutor {

   @Override
   public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
      if(sender instanceof Player){
         Player p = (Player) sender;
         if(args.length >= 1) {
            House h = HouseUtils.getHouseByPlayer(p);
            switch(args[0]) {
               case "setspawn":
                  if(h == null) {
                     me.Strobe.Core.Utils.StringUtils.sendMessage(p, StringUtils.noHouse);
                     return false;
                  }
                  return houseSetSpawn(h, p.getLocation());
               case "add":
                  if(h == null) {
                     me.Strobe.Core.Utils.StringUtils.sendMessage(p, StringUtils.noHouse);
                     return false;
                  }
                  return houseAddMember(h, args[1]);
               case "remove":
                  if(h == null) {
                     me.Strobe.Core.Utils.StringUtils.sendMessage(p, StringUtils.noHouse);
                     return false;
                  }
                  return houseRemMember(h, args[1]);
               case "gift":
                  if(h == null) {
                     me.Strobe.Core.Utils.StringUtils.sendMessage(p, StringUtils.noHouse);
                     return false;
                  }
                  return giftHouse(h, args[1]);
               case "reload":
                  if(!sender.hasPermission("houses.admin")) return false;
                  if(args.length >= 2){
                     if(args[1].equalsIgnoreCase("all"))
                        HouseUtils.reloadAllOwned();
                     else if(args[1].equalsIgnoreCase("all -u"))
                        HouseUtils.reloadAll();
                     else
                        reloadSpecificHouse(HouseUtils.getHouseByName(args[1]));
                     return true;
                  }
                  else
                     me.Strobe.Core.Utils.StringUtils.sendMessage(p, StringUtils.houseReloadUsage);
                  return false;
               case "clear":{
                  if(!sender.hasPermission("houses.admin")) return false;
                  //house clear <name>
                  if(args.length == 2){
                     if(args[1].equalsIgnoreCase("all")){
                        HouseUtils.clearAll();
                        return true;
                     }
                     h = HouseUtils.getHouseByName(args[1]);
                     if(h == null){
                        me.Strobe.Core.Utils.StringUtils.sendMessage(p, StringUtils.notAHouse);
                        return false;
                     }
                     else{
                        return clearHouse(h);
                     }
                  }
               }
               return true;
               case "flagall":{
                  if(!sender.hasPermission("houses.admin")) return false;
                  HouseUtils.flagAll(args[1], args[2]);
                  me.Strobe.Core.Utils.StringUtils.sendMessage(p, "&b&l(!) &7 You have flagged all houses with the tags: " + args[1] + " to " + args[2]);
                  return true;
               }
               case "loadall":{
                  if(!sender.hasPermission("houses.admin")) return false;
                  HouseUtils.loadAllHouses();
                  return true;
               }
               case "saveall":{
                  if(!sender.hasPermission("houses.admin")) return false;
                  HouseUtils.saveAllHouses();
               }
               default:
                  me.Strobe.Core.Utils.StringUtils.sendMessage(p, StringUtils.houseUsage);
                  return true;
            }
         }
         else
            GUIS.homes(p);
            return true;
      }
      return false;
   }

   // /house setspawn (Standing where spawn is)
   // /house opens gui
   // /house add <name>
   // /house remove <name>
   // /house gift <name>
   // /house reload all
   // /house reload <region>
   // /house clear <region>
   // /house create <name> <price> <startdays>


   //Must be owner to do this, applies to all members and owner, not valid outside of the region.
   private boolean houseSetSpawn(House h, Location l) {
      if(h.getRegion().contains(l.getBlockX(), l.getBlockY(), l.getBlockZ())) {
         h.setSpawnLocation(l);
         h.sendOwnerMessage(StringUtils.cmdSetSpawnSuccess.replace("{x}",""+l.getBlockX())
                                                          .replace("{y}",""+l.getBlockY())
                                                          .replace("{z}",""+l.getBlockZ()));
         return true;
      }
      else{
         h.sendOwnerMessage(StringUtils.cmdSetSpawnFail);
         return false;
      }
   }

   //will add the player to the house as a member if properly spelt, fails if they are already added.
   private boolean houseRemMember(House h, String otherPlayerName) {
      OfflinePlayer plr = h.getMemberByName(otherPlayerName);
      if(plr == null) {
         h.sendOwnerMessage(StringUtils.cmdRemMemFailNotAdded.replace("{plr}", otherPlayerName));
         return false;
      }
      else{
         h.removeMember(plr);
         h.sendOwnerMessage(StringUtils.removeMember.replace("{plr}", otherPlayerName));
         if(plr.isOnline())
            me.Strobe.Core.Utils.StringUtils.sendMessage((Player) plr, StringUtils.kickFromHouse.replace("{plr}", h.getOwner().getName()));
         return true;
      }
   }

   //will remove the player from the house if properly spelt, fails if they are not added
   private boolean houseAddMember(House h, String otherPlayerName) {
      if(!h.isPlayerAdded(otherPlayerName)){
         OfflinePlayer plr = PlayerUtils.getOfflinePlayer(otherPlayerName);
         if(plr == null) {
            h.sendOwnerMessage(me.Strobe.Core.Utils.StringUtils.Text.INVALID_PLAYER.create(otherPlayerName));
            return false;
         }
         else{
            h.addMember(plr);
            return true;
         }
      }
      else{
         h.sendOwnerMessage(StringUtils.cmdAddMemFailAddedPrior.replace("{plr}", otherPlayerName));
         return false;
      }
   }

   //gifts the house to the specific player, fails if they already own a house
   private boolean giftHouse(House h, String otherPlayerName) {
      OfflinePlayer plr = PlayerUtils.getOfflinePlayer(otherPlayerName);
      if(plr == null) {
         h.sendOwnerMessage(me.Strobe.Core.Utils.StringUtils.Text.INVALID_PLAYER.create(otherPlayerName));
         return false;
      }
      else
         return h.assignOwner(plr);
   }

   //Admin commmands

   private boolean reloadAllHouses() {
      return false;
   }

   private void reloadSpecificHouse(House h) {
      HouseUtils.reloadSpecificHouse(h);
   }

   private boolean clearHouse(House h) {
      h.clear();
      return true;
   }


}
