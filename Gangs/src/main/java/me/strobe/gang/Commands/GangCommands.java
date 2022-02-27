package me.strobe.gang.Commands;

import me.Strobe.Core.Utils.PlayerUtils;
import me.strobe.gang.GUIS;
import me.strobe.gang.Gang;
import me.strobe.gang.Member;
import me.strobe.gang.Utils.GangUtils;
import me.strobe.gang.Utils.MemberUtils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

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
            return promote(m, MemberUtils.getMemberFromPlayer(PlayerUtils.getPlayer(args[1])));
         case "demote":
            return demote(m, MemberUtils.getMemberFromPlayer(PlayerUtils.getPlayer(args[1])));
         case "transfer":
            return transfer(m, MemberUtils.getMemberFromPlayer(PlayerUtils.getPlayer(args[1])));
         case "withdraw":
            return withdraw(m, Double.parseDouble(args[1]));
         case "deposit":
            return deposit(m, Double.parseDouble(args[1]));
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
            return rename(m, args[1]);
         case "permission":
            return permission(m, MemberUtils.getMemberFromPlayer(PlayerUtils.getPlayer(args[1])));
         default:
            return noArg(m);
      }
   }

   private boolean noArg(Member sender){
      GUIS.openMainGUI(sender);
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
   private boolean promote(Member sender, Member promotee){
      if(sender.isPermissionSet(Gang.Permission.LOCKOUT)) return false;
      if(promotee == null) return false;
      if(sender.getRank().ordinal() < promotee.getRank().ordinal()) return false;
      if(sender.isPermissionSet(Gang.Permission.PERMISSION)){
         sender.getGang().promoteMember(promotee, null);
         return true;
      }
      return false;
   }
   private boolean demote(Member sender, Member demotee){
      if(sender.isPermissionSet(Gang.Permission.LOCKOUT)) return false;
      if(demotee == null) return false;
      if(sender.getRank().ordinal() < demotee.getRank().ordinal()) return false;
      if(sender.isPermissionSet(Gang.Permission.PERMISSION)){
         sender.getGang().demoteMember(demotee, null);
         return true;
      }
      return false;
   }
   private boolean invite(Member sender, Player invitee){
      if(sender.isPermissionSet(Gang.Permission.LOCKOUT)) return false;
      if(sender.isPermissionSet(Gang.Permission.INVITE)){
         sender.getGang().invite(invitee);
         return true;
      }
      return false;
   }
   private boolean kick(Member sender, Member kicked){
      if(sender.isPermissionSet(Gang.Permission.LOCKOUT)) return false;
      if(kicked == null) return false;
      if(!Gang.areMembersOfSameGang(sender, kicked)) return false;
      if(sender.getRank().ordinal() < kicked.getRank().ordinal() ) return false;
      if(sender.isPermissionSet(Gang.Permission.KICK)){
         sender.getGang().kickMember(kicked);
         return true;
      }
      return false;
   }
   private boolean ally(Member sender, String gangName){
      if(sender.isPermissionSet(Gang.Permission.LOCKOUT)) return false;
      if(sender.isPermissionSet(Gang.Permission.ALLY)){
         sender.getGang().requestAlly(gangName);
         return true;
      }
      return false;
   }
   private boolean allies(Member sender){
      GUIS.openAlliesGUI(sender);
      return true;
   }
   private boolean enemy(Member sender, String gangName){
      if(sender.isPermissionSet(Gang.Permission.LOCKOUT)) return false;
      if(sender.isPermissionSet(Gang.Permission.ALLY)) {
         sender.getGang().enemy(gangName);
         return true;
      }
      return false;
   }
   private boolean fftoggle(Member sender){
      if(sender.isPermissionSet(Gang.Permission.LOCKOUT)) return false;
      if(sender.isPermissionSet(Gang.Permission.FF)){
         sender.getGang().toggleFF();
         return true;
      }
      return false;
   }
   private boolean ffToggleOther(Member sender, String gangName){
      if(sender.isPermissionSet(Gang.Permission.LOCKOUT)) return false;
      if(sender.isPermissionSet(Gang.Permission.FF)){
         Gang g = GangUtils.getGangByName(gangName);
         if(g == null) return false;
         sender.getGang().toggleFFGang(g);
         return true;
      }
      return false;
   }
   private boolean sethome(Member sender, Location loc, String homeName){
      if(sender.isPermissionSet(Gang.Permission.LOCKOUT)) return false;
      if(sender.isPermissionSet(Gang.Permission.SETHOME)){
         sender.getGang().setGangHome(loc, homeName);
         return true;
      }
      return false;
   }
   private boolean homes(Member sender){
      GUIS.openHomesGUI(sender);
      return true;
   }
   private boolean transfer(Member sender, Member newOwner){
      if(sender.isPermissionSet(Gang.Permission.LOCKOUT)) return false;
      if(sender.getRank() == Gang.Rank.MASTERMIND){
         sender.getGang().transferOwnership(newOwner);
         return true;
      }
      return false;
   }
   private boolean withdraw(Member sender, double amt){
      if(sender.isPermissionSet(Gang.Permission.LOCKOUT)) return false;
      if(amt < 0) return false;
      if(amt > sender.getGang().getBalance()) return false;
      if(sender.isPermissionSet(Gang.Permission.WITHDRAW)){
         sender.getGang().withdraw(amt);
         sender.decMoneyCont(amt);
         return true;
      }
      return false;
   }
   private boolean deposit(Member sender, double amt){
      if(amt < 0) return false;
      if(amt > sender.getBalance()) return false;
      sender.getGang().deposit(amt);
      sender.incMoneyCont(amt);
      return true;
   }
   private boolean delhome(Member sender, String homeName){
      if(sender.isPermissionSet(Gang.Permission.LOCKOUT)) return false;
      if(sender.isPermissionSet(Gang.Permission.DELHOME)){
         sender.getGang().deleteGangHome(homeName);
         return true;
      }
      return false;
   }
   private boolean rename(Member sender, String newName){
      if(sender.isPermissionSet(Gang.Permission.LOCKOUT)) return false;
      if(GangUtils.validateName(newName))
         if(sender.isPermissionSet(Gang.Permission.RENAME)){
            sender.getGang().setName(newName);
            return true;
         }
      return false;
   }
   private boolean permission(Member sender, Member other){
      if(sender.isPermissionSet(Gang.Permission.LOCKOUT)) return false;
      if(sender.equals(other)) return false;
      if(sender.getRank().ordinal() < other.getRank().ordinal()) return false;
      if(sender.isPermissionSet(Gang.Permission.PERMISSION)){
         GUIS.openMemberPermissionsGUI(sender, other);
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
