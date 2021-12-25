import lombok.*;
import org.bukkit.*;
import org.bukkit.configuration.serialization.*;
import org.bukkit.entity.*;

import java.util.*;

@Getter
@Setter
public class Member implements ConfigurationSerializable {

   public static Map<UUID, Member> members = new HashMap<>();

   //TODO: add parameters for the class, name UUID, gang, total contribution, rank,
   private UUID uuid;
   private Player player;
   private Gang gang;
   private Gang.Rank rank;

   public Member(Player player) {
      this.player = player;
      this.uuid = player.getUniqueId();
   }


   /**
    * @param player the player to search for
    *
    * @return the Member found, otherwise null.
    */
   public static Member getByPlayer(OfflinePlayer player) {
      return members.get(player.getUniqueId());
   }

   /**
    * @param playerName the player's name to search for
    *
    * @return the Member found, otherwise null.
    */
   public static Member getByName(String playerName) {
      return members.get(PlayerUtils.getPlayer(playerName).getUniqueId());
   }

   /**
    * @param player_UUID the player's UUID to search for
    *
    * @return the Member found, otherwise null.
    */
   public static Member getByUUID(UUID player_UUID) {
      return Members.get(player_UUID);
   }


   @Override
   public Map<String, Object> serialize() {
      return null;
   }
}
