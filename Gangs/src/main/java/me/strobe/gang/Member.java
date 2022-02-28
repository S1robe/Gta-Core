package me.strobe.gang;

import lombok.Getter;
import lombok.Setter;
import me.Strobe.Core.Utils.StringUtils;
import me.strobe.gang.Utils.GangUtils;
import me.strobe.gang.Utils.MemberUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This is Member.java in Project: (Gta-Core) : But you already knew that
 * @author G.P of Prentice Productions
 * @version 1.0
 * Created On    : 2/24/2022 4:09 PM
 * Last Edit     : 2/24/2022 4:09 PM(Update Me!)
 * Time to Write : (Rough Estimate)
 *
 * (Class Description)
*/
@SerializableAs("Member")
public class Member implements ConfigurationSerializable {

   /**
    * The fixed string for a metadatavalue representing if the player is in Gang Chat
    */
   public static final String inGangChatMeta = "GANGCHATMETA";
   /**
    * The fixed string for a metadata vlue representing if the player has any current invites to other gangs.
    */
   public static final String invitedToGang = "GangInvites";

   @Getter private final OfflinePlayer p;
   @Getter private final UUID uuid;
   @Getter private final Gang gang;
   @Getter private Gang.Rank rank;
   @Getter private double totalMoneyCont = 0;
   @Getter private int totalKillCont = 0;
   @Getter private int totalPointCont = 0;
   @Getter private short permission = 0b0;
   @Getter @Setter private boolean inGangChat = false;

   /**
    * Used to create a Member from players, typically on join and for the first time
    *
    * @param p The player they wrap
    * @param g The Gang they are apart of
    * @param r The rank they have within the gang.
    *
    * @apiNote Permissions are inherited from their rank.
    */
   protected Member(OfflinePlayer p, Gang g, Gang.Rank r){
      this.p = p;
      this.uuid = p.getUniqueId();
      this.gang = g;
      this.rank = r;
      MemberUtils.addNewMember(this);
   }

   /**
    * Strictly used to load members that have already been created from a file.
    *
    * @param x The map representing the field : data pair from the data file.
    */
   public Member(Map<String, Object> x) {
      this.uuid = UUID.fromString((String) x.get("uuid"));
      this.p = Bukkit.getOfflinePlayer(uuid);
      this.gang = GangUtils.getGangByName((String) x.get("gang"));
      this.rank = Gang.Rank.valueOf((String) x.get("rank"));
      this.totalMoneyCont = (double) x.get("totalMoneyCont");
      this.totalKillCont = (int) x.get("totalKillCont");
      this.totalPointCont = (int) x.get("totalPointCont");
   }

   /**
    * Sets the rank of this member to the rank specified
    * Used locally to handle Enum.ordinal calculations, primarily for one-rank promotion/demotion
    *
    * @param rank The integer representation of {@link Gang.Rank} that will be this member's new rank.
    */
   public void setRank(int rank){
      switch(rank){
         case 0: //Known
            this.rank = Gang.Rank.KNOWN;
            break;
         case 1: //Honored
            this.rank = Gang.Rank.HONORED;
            break;
         case 2: //Exalted
            this.rank = Gang.Rank.EXALTED;
            break;
      }
   }

   /**
    * The normal way to set a Member's rank, this is used for singular and multi-step promotion/demotion within the Gang
    *
    * @param r The {@link Gang.Rank} that is the new rank of this Member within the gang
    */
   public void setRank(Gang.Rank r){
      this.rank = r;
   }

   /**
    * Forces this member to the leave the current Gang, will also delete this object. Since it is no longer used.
    */
   public void leaveGang(){
      gang.kickMember(this);
      p.getPlayer().removeMetadata(inGangChatMeta, Main.getMain());
   }

   /**
    * Used to switch between global channel Minecraft chat and Gang chat
    */
   public void toggleGangChat(){
      if(!inGangChat)
         p.getPlayer().setMetadata(inGangChatMeta, new FixedMetadataValue(Main.getMain(), true));
      else
         p.getPlayer().removeMetadata(inGangChatMeta, Main.getMain());

      inGangChat = !inGangChat;
   }

   /**
    * Increases the amount of kills this member has individually contributed to the gang they are apart of by 1.
    *
    * @apiNote This method is called everytime a {@link org.bukkit.event.entity.PlayerDeathEvent} is called
    */
   public void incKillCont(){
      this.totalKillCont++;
   }

   /**
    * Increases the amount of money that this member has contributed to the specific gang they're apart of.
    *
    * @param amt The amount of money they have just contributed.
    */
   public void incMoneyCont(double amt){
      this.totalMoneyCont += amt;
   }

   /**
    * Increases the amount of points that this member has contributed to the specific gang they're apart of.
    *
    * @param amt The amount of points they have earned for the gang.
    */
   public void incPointCont(int amt){
      this.totalPointCont += amt;
   }

   /**
    * Decreases the money that this member has contributed to their gang.
    *
    * @param amt The amount they have lost due to withdrawal.
    * @apiNote This method is always called when a Member invokes the method {@link Gang#withdraw(double)}
    */
   public void decMoneyCont(double amt){
      this.totalMoneyCont = Math.max(0, totalMoneyCont - amt);
   }

   /**
    * Decreases the amount of points this member has contributed to their gang.
    *
    * @param amt The amount of points lost, due to death to other player, or some other loss cause.
    * @apiNote This method is called in various Event methods apart of bukkit. Todo: add these methods here
    */
   public void decPointCont(int amt){
      this.totalPointCont = Math.max(0, totalPointCont - amt);
   }

   /**
    * Used to parse data during a desearlize call from {@link SerializableAs}
    *
    * @param serialized the map that contains all the data needed to construct a new Member instance.
    * @return A new Member Reference Object.
    */
   public static Member deserialize(Map<String, Object> serialized){
      return new Member(serialized);
   }

   /**
    * Auxillary method used to simplify and de-clutter the balance calls in various places.
    * Makes a call to {@link net.milkbowl.vault.economy.Economy#getBalance(OfflinePlayer)}
    *
    * @return The balance of this Member
    */
   public double getBalance(){
      return Main.getMain().getEcon().getBalance(this.p);
   }

   /**
    * Auxiliary method used to simplify and de-clutter the deposit calls in various places
    * @param amt the amount this player is receiving.
    * @apiNote makes a call to {@link net.milkbowl.vault.economy.Economy#depositPlayer(OfflinePlayer, double)}
    */
   public void depositPersonal(double amt){
      Main.getMain().getEcon().depositPlayer(p, amt);
   }

   /**
    * Auxiliary method used to simplify and de-clutter the withdraw calls in various places
    * @param amt the amount this player is losing.
    * @apiNote makes a call to {@link net.milkbowl.vault.economy.Economy#withdrawPlayer(OfflinePlayer, double)}
    */
   public void withdrawPersonal(double amt){
      Main.getMain().getEcon().withdrawPlayer(p, amt);
   }

   private short convertStringToPermission(String permissionTag){
      permissionTag = permissionTag.toUpperCase();
      try {
         Gang.Permission p = Gang.Permission.valueOf(permissionTag);
         return p.permissionBit;
      }
      catch(IllegalArgumentException e){
         return -1;
      }
   }

   /**
    * Checks if the permission at the specified bit index for the current member is set.
    *
    * @param bit the bit that is being checked for high position
    * @return if the requested bit has a value of 1 (true)
    */
   public boolean isPermissionSet(int bit){
      return ((this.permission >> bit )& 1) == 1;
   }

   /**
    * Converts a user provided string to a permission tag {@link Gang.Permission}
    *
    * @param permissionTag The tag we are looking for.
    * @return If the specified tag is set for this member.
    */
   public boolean isPermissionSet(String permissionTag){
      short x = convertStringToPermission(permissionTag);
      return x != -1 && isPermissionSet(x);
   }

   public boolean isPermissionSet(Gang.Permission permission){
      return (this.permission & permission.permissionBit) > 0b0;
   }
   /**
    * Assigns permissions based on an internal representation of bits, that represent a permission node
    * @see Gang.Rank#defPermissions
    *
    * @param bit The bit # to set starting from 0
    */
   public void assignPermission(byte bit){
      this.permission |= ((short) Math.pow(2, bit));
   }

   /**
    * Assigns permissions based on an internal representation of bits, that represent a permission node
    * @see Gang.Rank#defPermissions
    *
    * @param bits The bit # to set starting from 0
    */
   public void assignPermissions(byte... bits){
      for (byte bit : bits) {
         assignPermission(bit);
      }
   }

   /**
    * Inverts the permission bit at the specified index
    *
    * @param bit the bit to invert
    */
   public void invertPermission(byte bit){
      this.permission ^= (1 << bit);
   }

   /**
    * Inverts the permission bit at the specified index
    *
    * @param bits the bits to invert
    */
   public void invertPermissions(byte... bits){
      for (byte bit : bits) {
         invertPermission(bit);
      }
   }

   /**
    * Clears the bit at the specified bit position
    *
    * @param bit the bit index to be cleared starting from 0.
    */
   public void removePermission(byte bit){
      this.permission &= ~(1 << bit);
   }

   /**
    * Clears the bit at the specified bit position
    *
    * @param bits the bits indices to be cleared starting from 0.
    */
   public void removePermissions(byte... bits){
      for(byte bit : bits)
         removePermission(bit);
   }

   public boolean isOnline(){
      return p.isOnline();
   }

   /**
    * Used to compress the unique data of a given Member implementation so that it may be stored.
    *
    * @return A Map representing the storable data of this Member.
    */
   @Override
   public Map<String, Object> serialize() {
      Map<String, Object> x = new HashMap<>();
      x.put("uuid", uuid);
      x.put("gang", gang.getName());
      x.put("rank", rank.toString());
      x.put("totalMoneyCont", totalMoneyCont);
      x.put("totalKillCont", totalKillCont);
      x.put("totalPointCount", totalPointCont);
      return x;
   }

   public String[] stringify(){
      return new String[] {
         "UUID: " + uuid.toString(),
         "Rank: " + rank.prefix,
         "Money Contributed: " + totalMoneyCont,
         "Kills Contributed: " + totalKillCont,
         "Points Contributed: " + totalPointCont
      };
   }

   @Override
   public String toString(){
      if(p.isOnline())
         return StringUtils.color(this.rank.prefix + " &a" + this.p.getName());
      else
         return StringUtils.color(this.rank.prefix + " &7" + this.p.getName());
   }
}
