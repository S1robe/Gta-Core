package me.strobe.gang.Utils;

import me.strobe.gang.Files.CustomFile;
import me.strobe.gang.Gang;
import me.strobe.gang.Member;
import org.bukkit.configuration.file.FileConfiguration;

public class MemberUtils {

   private static CustomFile memberFile;
   private static FileConfiguration memberConfig;

   private MemberUtils(){}

   public static void updateMember(Member m, Gang g){

   }

   public static void saveMember(Member m){
      memberFile.reloadCustomConfig();
      memberConfig = memberFile.getCustomConfig();
      memberConfig.set(m.getUuid().toString(), null);
      memberFile.saveCustomConfig();
   }

   public static void saveAllMembers(){
      memberFile.reloadCustomConfig();
      memberConfig = memberFile.getCustomConfig();
      for(Gang value : GangUtils.getGangs().values()) {
         for(Member m : value.getMembers().values()){
            memberConfig.set(m.getUuid().toString(), m);
         }
      }
   }

   public static Member getMemberFromUUID(String uuid){
      return (Member) memberConfig.get(uuid);
   }


   public static void deleteMember(Member m){
      memberFile.reloadCustomConfig();
      memberConfig = memberFile.getCustomConfig();
      memberConfig.set(m.getUuid().toString(), null);
      memberFile.saveCustomConfig();
   }

   public static void init(){

   }

}
