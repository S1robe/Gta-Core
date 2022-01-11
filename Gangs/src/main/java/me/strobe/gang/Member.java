package me.strobe.gang;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.Map;
import java.util.UUID;

@SerializableAs("Member")
public class Member implements ConfigurationSerializable {

   @Getter private OfflinePlayer p;
   @Getter private UUID uuid;
   @Getter private Gang gang;
   @Getter private Gang.Rank rank;
   @Getter private double totalMoneyCont;
   @Getter private int totalKillCont;
   @Getter private int totalPointCont;
   @Getter @Setter private boolean inGangChat;

   public Member(OfflinePlayer p, Gang g, Gang.Rank r){
      this.p = p;
      this.uuid = p.getUniqueId();
      this.gang = g;
      this.rank = r;
   }


   public Member(Map<String, Object> serialized) {

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
      inGangChat = false;
   }

   public void toggleGangChat(){
      inGangChat = !inGangChat;
   }

   public void incKillCont(int amt){}
   public void incMoneyCont(double amt){}
   public void incPointCont(int amt){}

   //Minimum 0
   public void decKillCont(int amt){}
   public void decMoneyCont(double amt){}
   public void decPointCont(int amt){}


   public static Member deserialize(Map<String, Object> serialized){
      return new Member(serialized);
   }

   @Override
   public Map<String, Object> serialize() {
      return null;
   }
}
