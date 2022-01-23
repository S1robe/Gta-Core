package me.strobe.gang;

import lombok.Getter;
import lombok.Setter;
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

@SerializableAs("Member")
public class Member implements ConfigurationSerializable {

   public static final String inGangChatMeta = "GANGCHATMETA";

   @Getter private final OfflinePlayer p;
   @Getter private final UUID uuid;
   @Getter private final Gang gang;
   @Getter private Gang.Rank rank;
   @Getter private double totalMoneyCont = 0;
   @Getter private int totalKillCont = 0;
   @Getter private int totalPointCont = 0;
   @Getter @Setter private boolean inGangChat = false;

   protected Member(OfflinePlayer p, Gang g, Gang.Rank r){
      this.p = p;
      this.uuid = p.getUniqueId();
      this.gang = g;
      this.rank = r;
      MemberUtils.addNewMember(this);
   }

   public Member(Map<String, Object> x) {
      this.uuid = UUID.fromString((String) x.get("uuid"));
      this.p = Bukkit.getOfflinePlayer(uuid);
      this.gang = GangUtils.getGangByName((String) x.get("gang"));
      this.rank = (Gang.Rank) x.get(Gang.Rank.valueOf((String) x.get("rank")));
      this.totalMoneyCont = (double) x.get("totalMoneyCont");
      this.totalKillCont = (int) x.get("totalKillCont");
      this.totalPointCont = (int) x.get("totalPointCont");
   }

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

   public void setRank(Gang.Rank r){
      this.rank = r;
   }

   public void leaveGang(){
      gang.removeMember(this);
      p.getPlayer().removeMetadata(inGangChatMeta, Main.getMain());
   }

   public void toggleGangChat(){
      if(!inGangChat)
         p.getPlayer().setMetadata(inGangChatMeta, new FixedMetadataValue(Main.getMain(), true));
      else
         p.getPlayer().removeMetadata(inGangChatMeta, Main.getMain());

      inGangChat = !inGangChat;
   }

   public void incKillCont(int amt){
      this.totalKillCont += amt;
   }
   public void incMoneyCont(double amt){
      this.totalMoneyCont += amt;
   }
   public void incPointCont(int amt){
      this.totalPointCont += amt;
   }

   //Minimum 0
   public void decKillCont(int amt){
      this.totalKillCont = Math.max(0, totalKillCont - amt);
   }
   public void decMoneyCont(double amt){
      this.totalMoneyCont = Math.max(0, totalMoneyCont - amt);
   }
   public void decPointCont(int amt){
      this.totalPointCont = Math.max(0, totalPointCont - amt);
   }

   public static Member deserialize(Map<String, Object> serialized){
      return new Member(serialized);
   }

   public double getBalance(){
      return Main.getMain().getEcon().getBalance(this.p);
   }
   public void depositPersonal(double amt){
      Main.getMain().getEcon().depositPlayer(p, amt);
   }
   public void withdrawPersonal(double amt){
      Main.getMain().getEcon().withdrawPlayer(p, amt);
   }

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
}
