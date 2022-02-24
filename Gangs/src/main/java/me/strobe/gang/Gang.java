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
      p.setMetadata("GangInvite", new FixedMetadataValue(Main.getMain(), getName()));
      new BukkitRunnable(){
         @Override
         public void run() {
            p.removeMetadata("GangInvite", Main.getMain());
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

   public enum Rank{
      KNOWN( "&8[&7Known&8]&r"), //Deposit, Teleport, Leave, Gang stats,
      HONORED( "&8[&aHonored&8]"), //withdraw, upgrades, toggle ff, ally, enemy,
      EXALTED("&8[&9&mExalted&8]"), //sethome, delhome, kick, invite claim/unclaim
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
