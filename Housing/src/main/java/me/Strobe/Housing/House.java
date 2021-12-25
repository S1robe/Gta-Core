package me.Strobe.Housing;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import lombok.Setter;
import me.Strobe.Core.Utils.ItemUtils;
import me.Strobe.Core.Utils.RegionUtils;
import me.Strobe.Core.Utils.StringUtils;
import me.Strobe.Core.Utils.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Getter
@Setter
@SerializableAs("House")
public class House implements ConfigurationSerializable {

   //before purchase
   private Location signLocation;
   private int startDays;
   private double price;
   private String name;
   private transient ProtectedRegion region;

   //after purchase
   private long startTime = -1;
   private long endTime = -1;
   private OfflinePlayer owner;
   private List<OfflinePlayer> members = new ArrayList<>();
   private Location spawnLocation;
   private transient String[] timeLeftString;
   private transient ItemStack GUIHOUSEITEM;
   private List<Location> chestLocations;

   private House() {
   }

   //For when running the command to make it.
   public House(Location signLocation, int startDays, double price, ProtectedRegion region, List<Location> chests) {
      this.startDays = startDays;
      this.name = region.getId();
      this.price = price;
      this.signLocation = signLocation;
      this.region = region;
      this.chestLocations = chests;
      updateSign(StringUtils.colorBulk("FOR RENT", "&r" + this.name, "&r" + startDays + " Days", "&r$" + price));
   }

   //loading from the file.
   public House(Map<String, Object> serialized) {
      this.startDays = (int) serialized.get("startDays");
      this.name = (String) serialized.get("name");
      this.price = (double) serialized.get("price");
      this.signLocation = RegionUtils.locationDeserializer((String) serialized.get("sLocation"));
      this.chestLocations = (List<Location>) serialized.get("chestLocations");

      this.region = Main.getWG().getRegionManager(signLocation.getWorld()).getRegion(name);
      if(serialized.containsKey("startTime"))
         this.startTime = (long) serialized.get("startTime");
      if(serialized.containsKey("endTime"))
         this.endTime = (long) serialized.get("endTime");
      if(serialized.containsKey("ownerUUID"))
         this.owner = Bukkit.getOfflinePlayer(UUID.fromString((String) serialized.get("ownerUUID")));
      if(serialized.containsKey("memberUUIDs"))
         ((List<String>) serialized.get("memberUUIDs")).forEach(s -> members.add(Bukkit.getOfflinePlayer(UUID.fromString(s))));
      if(serialized.containsKey("spawnLocation"))
         spawnLocation = RegionUtils.locationDeserializer((String) serialized.get("spawnLocation"));
   }

   public static House deserialize(Map<String, Object> serialized) {
      return new House(serialized);
   }

   public void buyHouse(User purchaser) {
      startTime = System.currentTimeMillis();
      endTime = startTime + startDays * 24 * 60 * 60 * 1000L;
      this.spawnLocation = this.signLocation;
      this.owner = purchaser.getPLAYER();
      region.getOwners().addPlayer(owner.getUniqueId());
      members = new ArrayList<>();
      purchaser.removeMoney(price);
      updateSign(StringUtils.colorBulk("&r" + owner.getName(), "&8&nClick Me!", "", "&r" + this.getName()));
   }

   public void updateSign(String[] lines) {
      Block sBlock = signLocation.getBlock();
      Sign sign = (Sign) sBlock.getState();
      StringUtils.colorBulk(lines);
      for(int i = 0; i < lines.length; i++)
         sign.setLine(i, lines[i]);
      sign.update(true);
   }

   public void extendHouse(Player p, int numDays) {
      this.endTime += numDays * 24 * 60 * 60 * 1000L;
      User u = User.getByPlayer(p);
      u.removeMoney( (price / startDays));
   }

   public void addMember(OfflinePlayer member) {
      region.getMembers().addPlayer(member.getUniqueId());
      members.add(member);
      sendOwnerMessage(me.Strobe.Housing.Utils.StringUtils.addedMember.replace("{plr}", member.getName()));
      if(member.isOnline())
         me.Strobe.Core.Utils.StringUtils.sendMessage(member.getPlayer(), me.Strobe.Housing.Utils.StringUtils.addedToHouse.replace("{plr}", owner.getName()));

   }

   public void sendOwnerMessage(String... message) {
      if(owner.isOnline())
         StringUtils.sendBulkMessage(owner.getPlayer(), message);
   }

   public void removeMember(OfflinePlayer member) {
      region.getMembers().removePlayer(member.getUniqueId());
      members.remove(member);
      sendOwnerMessage(me.Strobe.Housing.Utils.StringUtils.removeMember.replace("{plr}", member.getName()));
      if(member.isOnline())
         me.Strobe.Core.Utils.StringUtils.sendMessage(member.getPlayer(), me.Strobe.Housing.Utils.StringUtils.kickFromHouse.replace("{plr}", owner.getName()));
   }

   public boolean assignOwner(OfflinePlayer newOwner) {
      if(!region.getOwners().contains(newOwner.getUniqueId())) {
         region.getOwners().removeAll();
         region.getOwners().addPlayer(newOwner.getUniqueId());
         region.getMembers().addPlayer(owner.getUniqueId());
         sendOwnerMessage(me.Strobe.Housing.Utils.StringUtils.lostOwnership);
         if(newOwner.isOnline())
            StringUtils.sendMessage(newOwner.getPlayer(), me.Strobe.Housing.Utils.StringUtils.gainedOwnership.replace("{plr}", owner.getName()).replace("{reg}", name));
         owner = newOwner;
         members.remove(newOwner);
         members.add(owner);
         updateSign(StringUtils.colorBulk("&r" + owner.getName()));
         return true;
      }
      return false;
   }

   public void unrentHouse() {
      User.getByPlayer(owner).addMoney((price / (double) startDays) * getDaysRemaining());
      sendMembersAMessage(me.Strobe.Housing.Utils.StringUtils.ownerSoldHouse);
      sendOwnerMessage(me.Strobe.Housing.Utils.StringUtils.unrentHouse);
      resetHouse();
   }

   private void resetHouse() {
      startTime = -1;
      region.getOwners().removeAll();
      region.getMembers().removeAll();
      owner = null;
      members = null;
      updateSign(StringUtils.colorBulk("FOR RENT", "&r" + this.name, "&r" + startDays + " Day(s)", "&r$" + price));
   }

   public void sendMembersAMessage(String... message) {
      members.stream().filter(OfflinePlayer::isOnline).forEach(p -> {
         StringUtils.sendBulkMessage(p.getPlayer(), message);
      });
   }

   public void update() {
         long timeleft = endTime - System.currentTimeMillis();
         if(timeleft <= 0)
            expireHouse();
         else {
            timeLeftString = me.Strobe.Housing.Utils.StringUtils.msToDHMSColored(timeleft);
         }
   }

   public void expireHouse() {
      sendMembersAMessage(me.Strobe.Housing.Utils.StringUtils.expiredMemberHouse);
      sendOwnerMessage(me.Strobe.Housing.Utils.StringUtils.expiredHouse);
      resetHouse();
   }

   public ItemStack houseItem(Player p) {
      ItemStack houseItem = ItemUtils.createItem(Material.TRIPWIRE_HOOK, 1, (byte) 0, true, "&9" + this.name);
      ItemUtils.applyLore(houseItem, getTimeLeftString());
      ItemUtils.appendToLore(houseItem, "&9Price of your house&7: &a" + this.price, "&9Members: ");
      if(p.getUniqueId() != owner.getUniqueId()) {
         this.members.stream().filter(player -> (p.getUniqueId() != player.getUniqueId())).forEach(m -> {
            if(m.isOnline())
               ItemUtils.appendToLore(houseItem, "&a" + m.getName());
            else
               ItemUtils.appendToLore(houseItem, "&8" + m.getName());
         });
         if(owner.isOnline())
            ItemUtils.appendToLore(houseItem, "&e&lOwner: &a" + owner.getName());
         else
            ItemUtils.appendToLore(houseItem, "&e&lOwner: &8" + owner.getName());
      }
      else
         this.members.forEach(m -> {
            if(m.isOnline())
               ItemUtils.appendToLore(houseItem, "&a" + m.getName());
            else
               ItemUtils.appendToLore(houseItem, "&8" + m.getName());
         });
      this.GUIHOUSEITEM = houseItem;
      return houseItem;
   }

   public void sendAllMessage(String... message) {
      sendOwnerMessage(message);
      sendMembersAMessage(message);
   }

   public boolean doesPlayerOwnHouse(OfflinePlayer p) {
      return p.equals(owner);
   }

   public OfflinePlayer getMemberByName(String playerName){
      for(OfflinePlayer member : members)
         return member.getName().equalsIgnoreCase(playerName)? member : null;
      return null;
   }

   public boolean isPlayerOwner(Player p){
      return owner != null && p.getUniqueId().equals(owner.getUniqueId());
   }

   public boolean isPlayerAdded(String playerName){
      return members.stream().map(OfflinePlayer::getName).anyMatch(playerName::equalsIgnoreCase);
   }

   public boolean isPlayerAdded(OfflinePlayer p) {
      return members.contains(p);
   }

   public boolean isOwned() {
      return owner != null;
   }

   public int getDaysRemaining(){
      return (int) TimeUnit.MILLISECONDS.toDays(endTime - System.currentTimeMillis());
   }

   public List<Location> getChestLocations(){
      return chestLocations;
   }

   private static BlockFace[] radial = { BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST };

   public static BlockFace yawToFace(float yaw) {
      BlockFace f = radial[Math.round(yaw / 45f) & 0x7];
      if(f == BlockFace.SOUTH)
         return BlockFace.SOUTH_SOUTH_EAST;
      if(f == BlockFace.EAST)
         return BlockFace.WEST_NORTH_WEST;
      return f;
   }


   @Override
   public Map<String, Object> serialize() {
      Map<String, Object> map = new HashMap<>();
      map.put("sLocation", RegionUtils.locationSerializer(signLocation));
      map.put("name", name);
      map.put("price", price);
      map.put("startDays", startDays);
      map.put("chestLocations", chestLocations);

      if(startTime != -1)
         map.put("startTime", startTime);
      if(endTime != -1)
         map.put("endTime", endTime);
      if(owner != null)
         map.put("ownerUUID", owner.getUniqueId().toString());
      if(members != null && !members.isEmpty())
         map.put("memberUUIDs", members.stream().map(OfflinePlayer::getUniqueId).map(UUID::toString).collect(Collectors.toList()));
      if(spawnLocation != null)
         map.put("spawnLocation", RegionUtils.locationSerializer(spawnLocation));


      return map;
   }
}
