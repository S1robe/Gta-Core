import lombok.*;
import org.bukkit.*;
import org.bukkit.configuration.serialization.*;
import org.bukkit.entity.*;

import java.util.*;

@Getter
@Setter
public class Gang implements ConfigurationSerializable {

   //TODO: Name, Members, Invites, Allies, Homes, Money, Kills, ARequests, FFToggle, FFKB Toggle
   private String name;
   private List<Member> members;
   private List<Player> onlineMembers;
   private List<Gang> allies;
   private List<Location> homes;
   private double money;
   private int kills;
   private List<Gang> allyRequests;
   private boolean FFToggle;
   private boolean FFKBToggle;

   @Override
   public Map<String, Object> serialize() {
      return null;
   }

   public enum Rank {
      MEMBER(ChatColor.GRAY), OFFICER(ChatColor.AQUA), CO_LEADER(ChatColor.GREEN), LEADER(ChatColor.RED);
      final ChatColor color;

      Rank(ChatColor color) {
         this.color = color;
      }

   }
}
