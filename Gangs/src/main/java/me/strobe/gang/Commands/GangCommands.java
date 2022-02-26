package me.strobe.gang.Commands;

import me.Strobe.Core.Utils.PlayerUtils;
import me.Strobe.Core.Utils.Title;
import me.strobe.gang.Gang;
import me.strobe.gang.Main;
import me.strobe.gang.Member;
import me.strobe.gang.Utils.GangUtils;
import me.strobe.gang.Utils.MemberUtils;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.scheduler.BukkitRunnable;

import java.security.Permission;
import java.util.List;
import java.util.stream.Collectors;

public class GangCommands implements CommandExecutor {

   @Override
   public boolean onCommand(CommandSender sender, Command command, String cmd, String[] args) {
      if(sender instanceof Player){
         return processGangCommand((Player) sender, args);
      }
      return false;
   }

   private boolean processGangCommand(Player sender, String... args){
      Member m = MemberUtils.getMemberFromPlayer(sender);
      if(m == null){
         if(args.length != 0) {
            switch(args[0]) {
               case "join":
                  return join(sender, args[1]);

               case "create":
                  return create(sender, args[1]);
            }
         }
         return false;
      }
      if(args.length == 0) return noArg(m);

      switch(args[0]){
         case "leave":
            return leave(m);
         case "disband":
            return disband(m);
         case "promote":
            return promote(m, PlayerUtils.getPlayer(args[1]));
         case "invite":
            return invite(m, PlayerUtils.getPlayer(args[1]));
         case "kick":
            return kick(m, MemberUtils.getMemberFromPlayer(PlayerUtils.getPlayer(args[1])));
         case "ally":
            return ally(m, args[1]);
         case "enemy":
            return enemy(m, args[1]);
         case "ff":
            return fftoggle(m);
         case "ffOther":
            return ffToggleOther(m, args[1]);
         case "sethome":
            return sethome(m, sender.getLocation(), args[1]);
         case "homes":
            return homes(m);
         case "delhome":
            return delhome(m, args[1]);
         case "rename":
            GangUtils.updateGangName(m.getGang(), m.getGang().getName(), args[1]);
            return true;
         default:
            return noArg(m);
      }
   }

   private boolean noArg(Member sender){
      //open gui for gang if someone has it
      return false;
   }

   private boolean create(Player sender, String gangName){
      if(GangUtils.validateName(gangName)){
         Gang g = new Gang(sender, gangName);
         return true;
      }
      return false;
   }
   private boolean disband(Member sender){
      if(sender.getRank() == Gang.Rank.MASTERMIND){
         sender.getGang().disband();
         return true;
      }
      return false;
   }
   private boolean promote(Member sender, OfflinePlayer promotee){
      Member promoted = MemberUtils.getMemberFromPlayer(promotee);
      if(sender.isPermissionSet(Gang.Permission.PERMISSION)
              && promoted.getRank().ordinal() < sender.getRank().ordinal()){
         sender.getGang().promoteMember(promoted, null);
         return true;
      }
      return false;
   }
   private boolean invite(Member sender, Player invitee){
      if(sender.isPermissionSet(Gang.Permission.INVITE)){
         Member x = MemberUtils.getMemberFromPlayer(invitee);
         if(x == null)
            sender.getGang().invite(invitee);
      }
      return false;
   }
   private boolean kick(Member sender, Member kicked){
      if(sender.isPermissionSet(Gang.Permission.KICK)
              && Gang.areMembersOfSameGang(sender, kicked)
              && kicked.getRank().ordinal() < sender.getRank().ordinal()){

         sender.getGang().kickMember(kicked);
         return true;
      }
      return false;
   }
   private boolean ally(Member sender, String gangName){
      if(sender.isPermissionSet(Gang.Permission.ALLY)){
         sender.getGang().requestAlly(gangName);
         return true;
      }
      return false;
   }
   private boolean enemy(Member sender, String gangName){
      if(sender.isPermissionSet(Gang.Permission.ALLY)) {
         sender.getGang().enemy(gangName);
         return true;
      }
      return false;
   }
   private boolean fftoggle(Member sender){
      if(sender.isPermissionSet(Gang.Permission.FF)){
         sender.getGang().toggleFF();
         return true;
      }
      return false;
   }
   private boolean ffToggleOther(Member sender, String gangName){
      if(sender.isPermissionSet(Gang.Permission.FF)){
         Gang g = GangUtils.getGangByName(gangName);
         if(g == null) return false;
         sender.getGang().toggleFFGang(g);
         return true;
      }
      return false;
   }
   private boolean sethome(Member sender, Location loc, String homeName){
      if(sender.isPermissionSet(Gang.Permission.SETHOME)){
         sender.getGang().setGangHome(loc, homeName);
         return true;
      }
      return false;
   }
   private boolean homes(Member sender){
      return true;
   }
   private boolean delhome(Member sender, String homeName){
      if(sender.isPermissionSet(Gang.Permission.DELHOME)){
         sender.getGang().deleteGangHome(homeName);
         return true;
      }
      return false;
   }
   private boolean join(Player sender, String gangName){

      List<MetadataValue> invs = sender.getMetadata("GangInvite");
      List<Object> invites = invs.stream().map(MetadataValue::value).collect(Collectors.toList());
      for (Object invite : invites)
         if(((String) invite).equalsIgnoreCase(gangName)) {
            GangUtils.getGangByName((String) invite).addMember(sender);
            return true;
         }

      return false;
   }

   private boolean leave(Member sender){
      if(sender.getRank().equals(Gang.Rank.MASTERMIND)){
         return disband(sender);
      }
      sender.leaveGang();
      return true;
   }

}
