package me.strobe.gang.Utils;

import lombok.Getter;
import me.strobe.gang.Files.CustomFile;
import me.strobe.gang.Gang;
import me.strobe.gang.Member;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.regex.Pattern;

public class GangUtils {

   private GangUtils(){}
   private static final String regexPattern = "";
   @Getter private static final HashMap<String, Gang> gangs = new HashMap<>();
   private static CustomFile gangFile;
   private static FileConfiguration gangConfig;


   public static boolean validateName(String name){
      return Pattern.matches(regexPattern, name);
   }

   public static void updateGangName(Gang gang, String oldName, String newName) {
      gangFile.reloadCustomConfig();
      gangConfig = gangFile.getCustomConfig();
      gangConfig.set(oldName, null);
      gangConfig.set(newName, gang);
      gangFile.saveCustomConfig();
   }

   public static void saveGangs(){
      gangFile.reloadCustomConfig();
      gangConfig = gangFile.getCustomConfig();
      gangs.forEach((s, g) -> gangConfig.set(s, g));
      gangFile.saveCustomConfig();
   }

   public static void saveGang(Gang g){
      gangFile.reloadCustomConfig();
      gangConfig = gangFile.getCustomConfig();
      gangConfig.set(g.getName(), g);
      gangFile.saveCustomConfig();
   }

   public static void saveGang(String s){
      Gang g = getGangByName(s);
      if(g != null) {
         gangFile.reloadCustomConfig();
         gangConfig = gangFile.getCustomConfig();
         gangConfig.set(g.getName(), g);
         gangFile.saveCustomConfig();
      }
   }

   public static void deleteGang(Gang g){
      gangFile.reloadCustomConfig();
      gangConfig = gangFile.getCustomConfig();
      gangConfig.set(g.getName(), null);
      gangFile.saveCustomConfig();
   }

   public static void deleteGang(String s){
      Gang g = getGangByName(s);
      if(g != null) {
         gangFile.reloadCustomConfig();
         gangConfig = gangFile.getCustomConfig();
         gangConfig.set(g.getName(), null);
         gangFile.saveCustomConfig();
         gangs.remove(s);
      }
   }



   public static Gang getGangWithMember(Member m){
      for(Gang value : gangs.values()) {
         if(value.getMembers().containsValue(m))
            return value;
      }
      return null;
   }
   public static Gang getGangByName(String name){
      return gangs.get(name);
   }

   public static Gang getGangByPlayer(OfflinePlayer p){
      for(Gang value : gangs.values()) {
         if(value.getMembers().containsKey(p.getUniqueId()))
            return value;
      }
      return null;
   }


   public static void init(){

   }

}

