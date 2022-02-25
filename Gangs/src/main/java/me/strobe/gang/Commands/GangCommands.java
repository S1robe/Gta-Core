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
                  List<MetadataValue> invs = sender.getMetadata("GangInvite");
                  List<Object> invites = invs.stream().map(MetadataValue::value).collect(Collectors.toList());
                  for (Object invite : invites)
                     if(((String) invite).equalsIgnoreCase(args[1])) {
                        GangUtils.getGangByName((String) invite).addMember(sender);
                        return true;
                     }
                  break;
               case "create":
                  if(GangUtils.validateName(args[1]))
                     new Gang(sender, args[1]);
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
            return promote(m);
         case "invite":
            return invite(m);
         case "kick":
            return kick(m);
         case "ally":
            return ally(m);
         case "ff":
            return fftoggle(m);
         case "ffOther":
            return ffToggleOther(m);
         case "sethome":
            return sethome(m);
         case "homes":
            return homes(m);
         case "delhome":
            return delhome(m);
         default:
            return noArg(m);
      }
   }

   private boolean noArg(Member sender){
      //open gui for gang if someone has it
   }

   private boolean create(Player sender, String gangName){
      if(GangUtils.validateName(gangName)){
         Gang g = new Gang(sender, gangName);
         return true;
      }
      return false;
   }
   private boolean disband(Member sender){
      if(sender.getRank().compareTo(Gang.Rank.MASTERMIND) == 0){
         sender.getGang().disband();
         return true;
      }
      return false;
   }
   private boolean promote(Member sender, OfflinePlayer promotee){
      Member promoted = MemberUtils.getMemberFromPlayer(promotee);
      if(sender.getRank().compareTo(Gang.Rank.EXALTED) >= 0 && promoted.getRank().compareTo(Gang.Rank.EXALTED) < 0){
         sender.getGang().promoteMember(promoted, null);
         return true;
      }
      return false;
   }
   private boolean invite(Member sender, Player invitee){
      if(sender.getRank().compareTo(Gang.Rank.EXALTED) >= 0){
         Member x = MemberUtils.getMemberFromPlayer(invitee);
         if(x == null)
            sender.getGang().invite(invitee);
      }
      return false;
   }
   private boolean kick(Member sender, Member kicked){
      if(sender.getRank().compareTo(Gang.Rank.EXALTED) >= 0
            && kicked.getRank().compareTo(Gang.Rank.EXALTED) < 0
            && Gang.areMembersOfSameGang(sender, kicked)){

         sender.getGang().kickMember(kicked);
         return true;
      }
      return false;
   }
   private boolean ally(Member sender, String gangName){
      if(sender.getRank().compareTo(Gang.Rank.HONORED) >= 0){
         sender.getGang().requestAlly(gangName);
         return true;
      }
      return false;
   }
   private boolean enemy(Member sender, String gangName){
      if(sender.getRank().compareTo(Gang.Rank.HONORED) >= 0){
         sender.getGang().enemy(gangName);
         return true;
      }
      return false;
   }
   private boolean fftoggle(Member sender){
      if(sender.getRank().compareTo(Gang.Rank.HONORED) >= 0){
         sender.getGang().toggleFF();
         return true;
      }
      return false;
   }
   private boolean ffToggleOther(Member sender, String gangName){
      if(sender.getRank().compareTo(Gang.Rank.HONORED) >= 0){
         Gang g = GangUtils.getGangByName(gangName);
         if(g == null) return false;
         sender.getGang().toggleFFGang(g);
         return true;
      }
      return false;
   }
   private boolean sethome(Member sender, Location loc){
      if(sender.getRank().compareTo())
   }
   private boolean homes(Member sender){

   }
   private boolean delhome(Member sender, String homeName){

   }
   private boolean join(Player sender, String gangName){
      Gang g = GangUtils.getGangByName(gangName);
      g.addMember(sender);
      return true;
   }
   private boolean leave(Member sender){
      if(sender.getRank().equals(Gang.Rank.MASTERMIND)){
         return disband(sender);
      }
      sender.leaveGang();
      return true;
   }

}
