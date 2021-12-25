import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;

public class Commands implements CommandExecutor {
   @Override
   public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
      if(commandSender instanceof Player) {
         Player sender = (Player) commandSender;
         switch(s.toLowerCase()) {
            case "g": {
            }
            case "gang": {
            }
         }
      }

      return false;
   }

   private boolean setHome(Location location, Gang gang) {
   }

   private boolean delHome(Location location, Gang gang) {
   }

   private boolean listMembers(Gang gang) {
   }

   private boolean kickFromGang(Member member, Gang gang) {
   }

   private boolean inviteToGang(Member member, Gang gang) {
   }

   private boolean cancelInvite(Member member, Gang gang) {
   }

   private boolean listInvites(Gang gang) {
   }

   private boolean changeGangName(Gang gang) {
   }

   private boolean changeLeader(Member member, Gang gang) {
   }

   private boolean gangChat(Member member) {
   }

   private boolean promote(Member member, Gang gang) {
   }

   private boolean demote(Member member, Gang gang) {
   }

   private boolean assignRank(Member member, Gang gang, Gang.Rank rank) {
   }

   private boolean forceDisbandGang(Gang gang) {
   }

   private boolean forceRemoveMemberFromGang(Member member, Gang gang) {
   }

   private boolean forceAssignRank(Member member, Gang gang) {
   }

}
