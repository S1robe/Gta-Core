package me.strobe.gang;

import lombok.Getter;
import lombok.Setter;
import me.Strobe.Core.Utils.RegionUtils;
import me.strobe.gang.Utils.GangUtils;
import me.strobe.gang.Utils.MemberUtils;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

@SerializableAs("Gang")
public class Gang implements ConfigurationSerializable {

   @Getter private String name;
   @Getter private double balance = 0;
   @Getter private double points = 0;
   @Getter private transient int totalKills = 0;
   @Getter @Setter private Member owner;
   @Getter private final HashMap<UUID, Member> members = new HashMap<>();
   @Getter private final List<UUID> invitees = new ArrayList<>();
   @Getter private final HashMap<String, Location> gangHomes = new HashMap<>();
   @Getter private final HashMap<String, Gang> allies = new HashMap<>();
   @Getter private final HashMap<String, Gang> inRequests = new HashMap<>();
   @Getter private final HashMap<String, Gang> outRequests = new HashMap<>();
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
      ((List<String>) map.get("inRequests")).forEach(n -> inRequests.put(n, GangUtils.getGangByName(n)));
      ((List<String>) map.get("outRequests")).forEach(n -> outRequests.put(n, GangUtils.getGangByName(n)));
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
      if(invitees.contains(p.getUniqueId()) || members.containsKey(p.getUniqueId())){
         return false;
      }
      return invitees.add(p.getUniqueId());
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
         friendlyFireGangs.add(g);
         return true;
      }
   }

   public void acceptRequest(String name, Gang g){
      allies.putIfAbsent(name, g);
      inRequests.remove(name);
   }

   public void requestAlly(String name, Gang g){
      outRequests.putIfAbsent(name, g);
      g.inRequests.putIfAbsent(this.name, this);
   }

   public void cancelRequest(String name, Gang g){
      outRequests.remove(name);
      g.inRequests.remove(this.name);
   }

   public void enemy(String name, Gang g){
      allies.remove(name);
      g.allies.remove(this.name);
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

   public void removeMember(OfflinePlayer p){
      Member m = this.members.get(p.getUniqueId());
      this.members.remove(p.getUniqueId());
      MemberUtils.deleteMember(m);
   }

   public void removeMember(Member m){
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
   
   //Provides the breakdown for the gang of all categories
//   public String statsByKillsCont(){}
//   public String statsByMoneyCont(){}
//   public String statsByPointsCont(){}
//
//   @Override
//   public String toString(){}
//

   public boolean isAlly(Gang g){
      return allies.get(g.getName()) != null;
   }

   public boolean isFriendlyFire(Gang g){
      return friendlyFireGangs.contains(g);
   }

   public static Gang deserialize(Map<String, Object> dict){
      return new Gang(dict);
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
      x.put("inRequests", new ArrayList<>(inRequests.keySet()));
      x.put("outRequests", new ArrayList<>(outRequests.keySet()));
      x.put("friendlyFire", friendlyFire);
      return x;
   }

   public enum Rank{
      KNOWN( "&8[&7Known&8]&r"), //Deposit, Teleport, Leave, Gang stats,
      HONORED( "&8[&aHonored&8]"), //withdraw, upgrades, toggle ff
      EXALTED("&8[&9&mExalted&8]"), //sethome, delhome, kick, invite, ally, enemy, claim/unclaim
      MASTERMIND("&8[&3&lMastermind&8]&r") //Rename, delete,

      ;

      final String prefix;


      Rank(String prefix){
         this.prefix = prefix;
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
