package me.strobe.gang;

import lombok.Getter;
import lombok.Setter;
import me.Strobe.Core.Utils.RegionUtils;
import me.Strobe.Core.Utils.Title;
import me.strobe.gang.Utils.GangUtils;
import me.strobe.gang.Utils.MemberUtils;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This is Gang.java in Project: (Gta-Core) : But you already knew that
 * @author G.P of Prentice Productions
 * @version 1.0
 * Created On    : 2/24/2022 4:38 PM
 * Last Edit     : 2/24/2022 4:38 PM(Update Me!)
 * Time to Write : (Rough Estimate)
 *
 * (Class Description)
*/
@SerializableAs("Gang")
public class Gang implements ConfigurationSerializable {

   @Getter private String name;
   @Getter private double balance = 0;
   @Getter private double points = 0;
   @Getter private transient int totalKills = 0;
   @Getter @Setter private Member owner;
   @Getter private final HashMap<UUID, Member> members = new HashMap<>();
   @Getter private final HashMap<String, Location> gangHomes = new HashMap<>();
   @Getter private final HashMap<String, Gang> allies = new HashMap<>();
   @Getter private final transient HashMap<String, Gang> inRequests = new HashMap<>();
   @Getter private final transient HashMap<String, Gang> outRequests = new HashMap<>();
   @Getter private final transient List<Gang> friendlyFireGangs = new ArrayList<>();
   @Getter @Setter private boolean friendlyFire;

   /**
    * Creates a new gang, also auto creates a member for the "leader", and also saves and adds this gang ot the list
    * of gangs
    *
    * @param leader The leader of this gang, also the sender of the command to create a new gang
    * @param name    The name of this gang, already checked for validity from {@link GangUtils#validateName(String)}.
    */
   public Gang(Player leader, String name){
      this.name  = name;
      Member m = new Member(leader, this, Rank.MASTERMIND);
      this.owner = m;
      this.members.put(leader.getUniqueId(), this.owner);
      MemberUtils.saveMember(m);
      GangUtils.saveGang(this);
      GangUtils.addGang(this);
   }

   /**
    * Creates a new Gang from a map, used by {@link ConfigurationSerializable}
    * @apiNote Not intended to be called normally, since this is used during deserialization
    *
    * @param map The mapping of internal data of a given gang
    */
   public Gang(Map<String, Object> map){
      this.name = (String) map.get("name");
      this.balance = (double) map.get("balance");
      this.owner = MemberUtils.getMemberFromUUID(UUID.fromString((String) map.get("owner")));

      List<String> mems = (List<String>) map.get("members");
      mems.stream().map(UUID::fromString).forEach(uuid -> members.put(uuid, MemberUtils.getMemberFromUUID(uuid)));

      Map<String, String> houses = (Map<String, String>) map.get("homes");
      houses.forEach((n, l) -> this.gangHomes.put(n, RegionUtils.locationDeserializer(l)));

      ((List<String>) map.get("allies")).forEach(n -> allies.put(n, GangUtils.getGangByName(n)));
      this.friendlyFire = (boolean) map.get("friendlyFire");

      GangUtils.saveGang(this);
      GangUtils.addGang(this);
   }

   /**
    * Disbands this gang, deletes it entirely, cleaning up all members
    * This also deletes it from the configuration files.
    */
   public void disband(){
      allies.forEach((gName, g) -> {
         g.getAllies().remove(this.getName());
      });
      inRequests.forEach((gName, g) -> {
         g.getOutRequests().remove(this.getName());
      });
      outRequests.forEach((gName, g) -> {
         g.getInRequests().remove(this.getName());
      });
      friendlyFireGangs.forEach(g -> g.getFriendlyFireGangs().remove(this));
      members.forEach((u, m) -> MemberUtils.deleteMember(m));
      members.clear();
      MemberUtils.deleteMember(owner);
      GangUtils.deleteGang(this);
   }

   /**
    * Invites the player specified to this Gang
    *
    * @param p The player being invited, Players are flagged internally for this gang's invite, for 30 minutes,
    *          They must respond within this time, or it will be removed, upon leaving this is removed automatically.
    */
   public void invite(Player p){
      Title.sendTitle(p, 0, 10, 3, null, "&7You have been invited to " + getName() + "!");
      p.setMetadata(Member.invitedToGang, new FixedMetadataValue(Main.getMain(), getName()));
      new BukkitRunnable(){
         @Override
         public void run() {
            p.removeMetadata(Member.invitedToGang, Main.getMain());
         }
      }.runTaskLater(Main.getMain(), 6000);
      //TOOD: make the delay configuraable in GangUtils via config.
   }

   /**
    * Toggles the Friendly Fire module for this gang.
    */
   public void toggleFF(){
      friendlyFire ^= true;
   }

   /**
    * Requests a toggling of Friendly Fire between the gangs, by default this setting is false, to prevent friendly fire.
    * There is no time limit for these requests, they last untill the server restarts, or never disappear.
    *
    * @param g The gang requesting a toggle, or the gang that is accepting a toggle request.
    */
   public void toggleFFGang(Gang g){
      if(g.getFriendlyFireGangs().contains(this)){
         g.getFriendlyFireGangs().remove(this);
         friendlyFireGangs.remove(g);
      }
      else
         g.getFriendlyFireGangs().add(this);
   }

   /**
    * Used to accept an ally request between gangs
    *
    * @param name The name of the gang that we are accepting the request of
    */
   public void acceptRequest(String name){
      allies.putIfAbsent(name, inRequests.get(name));
      inRequests.remove(name);
   }

   /**
    * Requests allying of this gang with another gang
    *
    * @param name The gang that we wish to ally with
    */
   public void requestAlly(String name){
      Gang g = GangUtils.getGangByName(name);
      if(isAlly(g)) return;
      if(g.outRequests.containsKey(this.name)){ //if you have a request active
         acceptRequest(name); // accept
         g.outRequests.remove(this.name); //remove the requests from your list
         inRequests.remove(name);         // remove from my receieved requests
         return;
      }
      g.inRequests.putIfAbsent(this.name, this);
      outRequests.putIfAbsent(name, g);
   }

   /**
    * Cancels an ally request to the specified gang.
    *
    * @param name The name of the gang we are revoking our ally request from.
    */
   public void cancelRequest(String name){
      GangUtils.getGangByName(name).inRequests.remove(this.name);
      outRequests.remove(name);
   }

   /**
    * Enemies a gang, will auto un ally them if they are an ally
    *
    * @param name The name of the gang we are enemying
    */
   public void enemy(String name){
      allies.get(name).allies.remove(this.name);
      allies.remove(name);
   }

   /**
    * Sets the name of this gang to the specified one.
    * @apiNote The parameter {@see newName} Should be checked with {@link GangUtils#validateName(String)}
    *    before calling this method
    *
    * @param newName The new name of the gang
    */
   public void setName(String newName){
      String hand = this.name;
      this.name = newName;
      GangUtils.updateGangName(this, hand,  newName);
   }

   /**
    * Adds a player as a member to this gang.
    *
    * @param p The player that accepted the invite to this gang
    */
   public void addMember(OfflinePlayer p){
      Member m = new Member(p, this, Rank.KNOWN);
      this.members.putIfAbsent(p.getUniqueId(), m);
   }

   /**
    * Kicks a member from this gang, used in cases where the player is offline
    *
    * @param p The player we wish to kick from this gang.
    */
   public void kickMember(OfflinePlayer p){
      kickMember(this.members.get(p.getUniqueId()));
   }

   /**
    * Kicks a member from this gang
    * @param m The member we are kicking
    */
   public void kickMember(Member m){
      this.members.remove(m.getUuid());
      MemberUtils.deleteMember(m);
   }

   /**
    * Promote a member by 1 rank or to a specific rank.
    *
    * @apiNote The parameter 'r' may be set to null to specify a singular promotion.
    * @param p The member we are promoting
    * @param r The Rank we are moving to.
    */
   public void promoteMember(Member p, Gang.Rank r){
      if(r != null)
         p.setRank(r);
      else
         p.setRank(p.getRank().ordinal()+1);
   }

   /**
    * Demote a member by 1 rank or to a specific rank.
    *
    * @apiNote The parameter 'r' may be set to null to specify a singular demotion.
    * @param p The member we are demoting
    * @param r The Rank we are moving to.
    */
   public void demoteMember(Member p, Gang.Rank r){
      if(r != null)
         p.setRank(r);
      else
         p.setRank(p.getRank().ordinal()-1);
   }

   /**
    * Transfers ownership of this gang to the specified member
    *
    * @apiNote This method sets the old owner to {@link Gang.Rank#EXALTED}
    * @param p The member we are transferring to
    */
   public void transferOwnership(Member p){
      p.setRank(Rank.MASTERMIND);
      owner.setRank(Rank.EXALTED);
      owner = p;
   }

   /**
    * Sets a home at this location if it doesnt not already exist, with the specified name
    *
    * @param l The location to set the home (likely always the player's locations)
    * @param name the name identifying this home from others.
    */
   public void setGangHome(Location l, String name){
      gangHomes.putIfAbsent(name, l);
   }

   /**
    * Deletes a gang home from this gang with the specified name
    *
    * @param name The name of the home to delete.
    */
   public void deleteGangHome(String name){
      gangHomes.remove(name);
   }

   /**
    * Deposit money into the gang's bank
    *
    * @apiNote amt is not checked for validity here, it should be checked prior to this call.
    * @param amt the amount to deposit
    */
   public void deposit(double amt){
      this.balance += amt;
   }

   /**
    * Withdraws a set amount from the gang's back
    *
    * @apiNote amt is not checked for validity, it should be checked prior to calling this.
    * @param amt the amount to take from the gang bank.
    */
   public void withdraw(double amt){
      this.balance = Math.max(balance - amt, 0);
   }

   /**
    * Checks if the other gang is an ally
    * @param g The gang to check
    * @return if the two gangs are allies, i.e they both have each others names in {@link Gang#allies}
    */
   public boolean isAlly(Gang g){
      return allies.get(g.getName()) != null;
   }

   /**
    * Checks if friendly fire is on for the other gang
    * @param g The gang to check
    * @return if the two gangs agreed to friendly fire, i.e both have each others names in {@link Gang#friendlyFireGangs}
    */
   public boolean isFriendlyFire(Gang g){
      return friendlyFireGangs.contains(g);
   }

   /**
    * Checks if both members are from the same gang
    *
    * @param $1 The first member, supplying the gang as well
    * @param $2 The seocnd member being checked.
    * @return if the two members are apart of the same gang
    */
   public static boolean areMembersOfSameGang(Member $1, Member $2){
      return $1.getGang().equals($2.getGang());
   }

   /**
    * Deserializes a gang from the provided map
    *
    * @apiNote Used typically by {@link ConfigurationSerializable}, not intended to be called normally.
    * @param dict The mapping of internal data for the gang
    * @return A new gang reference, completly initioalliez
    */
   public static Gang deserialize(Map<String, Object> dict){
      return new Gang(dict);
   }

   /**
    * Increase the kills of this gang
    * @param m The member that supplied the kills
    */
   public void incKills(Member m){
      m.incKillCont();
      this.totalKills++;
   }

   /**
    * Increase the points of this gang
    * @param m The member that earned the points
    * @param amt how many points earned
    */
   public void incPoints(Member m, int amt){
      m.incPointCont(amt);
      this.points += amt;
   }

   /**
    * Returns if this gang and another gang are equal
    *
    * @apiNote Equality is tested by comparing the names of the two gangs, if they match they are the same.
    * @param o The other object that we suspect is a gang
    * @return If this gang and the other object are in fact the same.q
    */
   @Override
   public boolean equals(Object o) {
      if(o.getClass().equals(this.getClass()))
         return this.name.equalsIgnoreCase(((Gang) o).getName());
      return false;
   }

   /**
    * Used to serialize a gang
    *
    * @apiNote Not intended to be called normally, called by {@link org.bukkit.configuration.file.FileConfiguration} when
    * having an object of this type put into a file.
    * @return The inner mapping of this Gang.
    */
   @Override
   public Map<String, Object> serialize() {
      Map<String, Object> x = new HashMap<>();
      x.put("name", name);
      x.put("balance", balance);
      x.put("owner", owner.getUuid().toString());
      x.put("members", members.keySet().stream().map(UUID::toString).collect(Collectors.toList()));
      Map<String, String> homes = new HashMap<>();
      gangHomes.forEach((s, l) -> homes.put(s, RegionUtils.locationSerializer(l)));
      x.put("homes", homes);
      x.put("allies", new ArrayList<>(allies.keySet()));
      x.put("friendlyFire", friendlyFire);
      return x;
   }

   /**
    * Permissions set for all gangs,
    * Permissions are made to be flexible and reusable and easily testable.
    */
   public enum Permission {
      MASTERMIND_PERMS  ((short) 0b111111111111110),
      EXALTED_PERMS     ((short) 0b011111111110110),
      HONORED_PERMS     ((short) 0b001001011000000),
      KNOWN_PERMS       ((short) 0b000000000000000),
      MASTERMIND        ((short) 0b100000000000000),
      EXALTED           ((short) 0b010000000000000),
      HONORED           ((short) 0b001000000000000),
      WITHDRAW          ((short) 0b000100000000000),
      UPGRADE           ((short) 0b000010000000000),
      FF                ((short) 0b000001000000000),
      ALLY              ((short) 0b000000100000000),
      SETHOME           ((short) 0b000000010000000),
      DELHOME           ((short) 0b000000001000000),
      KICK              ((short) 0b000000000100000),
      INVITE            ((short) 0b000000000010000),
      RENAME            ((short) 0b000000000001000),
      MANAGEMENT        ((short) 0b000000000000100),
      PERMISSION        ((short) 0b000000000000010),
      LOCKOUT           ((short) 0b000000000000001)

      ;

      final short permissionBit;

      Permission(short bit){
         permissionBit = bit;
      }
   }

   /**
    * The ranks of any gang each rank has its repsective perms, however, each permission can be enablekd or disabled
    * individually per rank, and per member in the gang.
    */
   public enum Rank{
      KNOWN( "&8[&7Known&8]&7", Permission.KNOWN_PERMS),
      HONORED( "&8[&aHonored&8]&7", Permission.HONORED_PERMS),
      EXALTED("&8[&9&mExalted&8]&7", Permission.EXALTED),
      MASTERMIND("&8[&3&lMastermind&8]&7", Permission.MASTERMIND_PERMS)
      ;

      /**
       * Prefix used in gang chat.
       */
      final String prefix;
      /**
       * This is a byte representation of a general member's permissions
       * each bit within this number represents a permission, a value of 1 is enabled
       * and 0 is disabled:
       *
       *       The first 3 bits are used for detemining rank within the rank, they are modifable externally via owner
       *       permissions.
       *
       *       Those with the permission bit will be granted the ability to set these bits. They are only intended as a
       *       quick way of setting up permissisons.
       *
       *       The remaining 12 bits are for individual Permissions, this allows for every member/rank to be modified as
       *       seen fit.
       *       I.e You could have a lowest rank (Member) with sethome abilities, but have the rest untrustworthy members
       *       without this permission
       *
       *    0b :
       *      15 : Mastermind Bit (Psuedo Mastermind)
       *      14 : Exalted Bit    (Psuedo Exalted)
       *      13 : Honored Bit    (Psuedo Honored)
       *
       *      12 : Withdraw bit   ( Enables Withdrawing from the bank without restriction )
       *      11 : Upgrades Bit   ( Enable buying Upgrades with the gang's money )
       *      10 : FF Bit         ( Enables Toggling ff for allies, and current gang )
       *      9  : Ally/Enemy Bit ( Enables Allying and Enemying Other gangs )
       *
       *      8  : Sethome Bit    ( Enables Setting homes for the gang )
       *      7  : DelHome BIt    ( Enables Deleting homes of the gang )
       *      6  : Kick Bit       ( Enables kicking other members from the gang [they must be lower in rank] )
       *      5  : Invite Bit     ( Enables inviting other players to this gang )
       *
       *      4  : Rename Bit     ( Enables renaming of the gang )
       *      3  : Management Bit ( Enables Accepting Withdraw Requests )
       *      2  : Permission Bit ( Enables regulation of permissions for members )
       *      1  : Lockout Bit    ( Disables all permissions, regardless of rank or other set permissions )
       *
       *   Default Values with permissions:
       *   Known:       0b000 0000 0000 0000 : No auxillary permissions
       *   Honored:     0b001 0010 1100 0000 : Access to toggle FF, set/del homes
       *   Exalted:     0b011 1111 1111 0110 : All Permissions except renaming.
       *   Mastermind:  0b111 1111 1111 1110 : All permissions, full control.
       */
      final Permission defPermissions;

      /**
       * Wraps the ranks for members, to give a general idea of permissions and hierarchy.
       *
       * @param prefix colorcode formatted (&8) title to be used as a prefix in gang chat
       * @param permissions default short-binary formatted numbers with bits that represent permissions.
       */
      Rank(String prefix, Permission permissions){
         this.prefix = prefix;
         this.defPermissions = permissions;
      }

   }

   public enum Upgrades{
      HOMES1, HOMES2, HOMES3, HOMES4, HOMES5,
      CLAIM1, CLAIM2, CLAIM3, CLAIM4, CLAIM5,
      MEMBERS1, MEMBERS2, MEMBERS3, MEMBERS4, MEMBERS5,
      TP1, TP2, TP3, TP4, TP5,
      SPEED1, SPEED2,
      DEFENSE1, DEFENSE2,
      SPAWNS1, SPAWNS2

      ;
   }


}
