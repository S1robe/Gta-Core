package me.strobe.gang.Commands;

import me.strobe.gang.Gang;
import me.strobe.gang.Member;
import me.strobe.gang.Utils.GangUtils;
import me.strobe.gang.Utils.MemberUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
               case "create":
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
      if(sender.getRank().equals(Gang.Rank.MASTERMIND)){
         sender.getGang().disband();
         return true;
      }
      return false;
   }
   private boolean promote(Member sender){
      if()
   }
   private boolean invite(Member sender){}
   private boolean kick(Member sender){}
   private boolean ally(Member sender){}
   private boolean enemy(Member sender){}
   private boolean fftoggle(Member sender){}
   private boolean ffToggleOther(Member sender){}
   private boolean sethome(Member sender){}
   private boolean homes(Member sender){}
   private boolean delhome(Member sender){}
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
