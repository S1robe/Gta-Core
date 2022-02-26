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

   public Gang(Player leader, String name){
      this.name  = name;
      Member m = new Member(leader, this, Rank.MASTERMIND);
      this.owner = m;
      this.members.put(leader.getUniqueId(), this.owner);
      MemberUtils.saveMember(m);
      GangUtils.saveGang(this);
      GangUtils.addGang(this);
   }

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
   }

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

   public boolean invite(Player p){
      Title.sendTitle(p, 0, 10, 3, null, "&7You have been invited to " + getName() + "!");
      p.setMetadata(Member.invitedToGang, new FixedMetadataValue(Main.getMain(), getName()));
      new BukkitRunnable(){
         @Override
         public void run() {
            p.removeMetadata(Member.invitedToGang, Main.getMain());
         }
      }.runTaskTimer(Main.getMain(), 0, 6000);
      return true;
   }

   public boolean toggleFF(){
      friendlyFire = !friendlyFire;
      return friendlyFire;
   }

   public boolean toggleFFGang(Gang g){
      if(g.getFriendlyFireGangs().contains(this)){
         g.getFriendlyFireGangs().remove(this);
         friendlyFireGangs.remove(g);
         return false;
      }
      else{
         g.getFriendlyFireGangs().add(this);
         return true;
      }
   }

   public void acceptRequest(String name){
      allies.putIfAbsent(name, inRequests.get(name));
      inRequests.remove(name);
   }

   public void requestAlly(String name){
      Gang g = GangUtils.getGangByName(name);
      g.inRequests.putIfAbsent(this.name, this);
      outRequests.putIfAbsent(name, g);
   }

   public void cancelRequest(String name){
      GangUtils.getGangByName(name).inRequests.remove(this.name);
      outRequests.remove(name);
   }

   public void enemy(String name){
      allies.get(name).allies.remove(this.name);
      allies.remove(name);
   }

   public void setName(String newName){
      String hand = this.name;
      this.name = newName;
      GangUtils.updateGangName(this, hand,  newName);
   }

   public void addMember(OfflinePlayer p){
      Member m = new Member(p, this, Rank.KNOWN);
      this.members.putIfAbsent(p.getUniqueId(), m);
   }

   public void kickMember(OfflinePlayer p){
      Member m = this.members.get(p.getUniqueId());
      this.members.remove(p.getUniqueId());
      MemberUtils.deleteMember(m);
   }

   public void kickMember(Member m){
      this.members.remove(m.getUuid());
      MemberUtils.deleteMember(m);
   }

   public void promoteMember(Member p, Gang.Rank r){
      if(r != null)
         p.setRank(r);
      else
         p.setRank(p.getRank().ordinal()+1);
   }

   //Owner
   public void demoteMember(Member p, Gang.Rank r){
      if(r != null)
         p.setRank(r);
      else
         p.setRank(p.getRank().ordinal()-1);
   }

   //Owner
   public void transferOwnership(Member p){
      p.setRank(Rank.MASTERMIND);
      owner.setRank(Rank.EXALTED);
      owner = p;
   }

   //Requires Recruit Status
   public void setGangHome(Location l, String name){
      gangHomes.putIfAbsent(name, l);
   }

   //Requires Officer Status
   public void deleteGangHome(String name){
      gangHomes.remove(name);
   }

   //Recruit
   public void deposit(double amt){
      this.balance += amt;
   }

   //Officer
   public void withdraw(double amt){
      this.balance = Math.max(balance - amt, 0);
   }

   public boolean isAlly(Gang g){
      return allies.get(g.getName()) != null;
   }

   public boolean isFriendlyFire(Gang g){
      return friendlyFireGangs.contains(g);
   }

   public static boolean areMembersOfSameGang(Member $1, Member $2){
      return $1.getGang().equals($2.getGang());
   }

   public static Gang deserialize(Map<String, Object> dict){
      return new Gang(dict);
   }

   public void incKills(Member m){
      m.incKillCont();
      this.totalKills++;
   }

   public void incPoints(Member m, int amt){
      m.incPointCont(amt);
      this.points += amt;
   }

   @Override
   public boolean equals(Object o) {
      if(o.getClass().equals(this.getClass()))
         return this.name.equalsIgnoreCase(((Gang) o).getName());
      return false;
   }

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
      KNOWN( "&8[&7Known&8]&r", Permission.KNOWN_PERMS),
      HONORED( "&8[&aHonored&8]", Permission.HONORED_PERMS),
      EXALTED("&8[&9&mExalted&8]", Permission.EXALTED),
      MASTERMIND("&8[&3&lMastermind&8]&r", Permission.MASTERMIND_PERMS)
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
