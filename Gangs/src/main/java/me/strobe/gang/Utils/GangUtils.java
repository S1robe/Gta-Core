package me.strobe.gang.Utils;

import lombok.Getter;
import me.strobe.gang.Files.CustomFile;
import me.strobe.gang.Gang;
import me.strobe.gang.Main;
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
      return Pattern.matches(regexPattern, name) && !gangs.containsKey(name);
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

   public static void loadGangs(){
      gangConfig.getKeys(false).forEach(g -> {
         gangs.put(g, (Gang) gangConfig.get(g));
      });
   }

   public static void saveGang(Gang g){
      gangFile.reloadCustomConfig();
      gangConfig = gangFile.getCustomConfig();
      gangConfig.set(g.getName(), g);
      gangFile.saveCustomConfig();
   }

   public static void addGang(Gang g){
      gangs.putIfAbsent(g.getName(), g);
   }

   public static void saveGang(String s){
      Gang g = getGangByName(s);
      if(g != null)
         saveGang(g);
   }

   public static void deleteGang(Gang g){
      gangFile.reloadCustomConfig();
      gangConfig = gangFile.getCustomConfig();
      gangConfig.set(g.getName(), null);
      gangFile.saveCustomConfig();
      gangs.remove(g.getName());
   }

   public static void deleteGang(String s){
      Gang g = getGangByName(s);
      if(g != null)
         deleteGang(g);
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
      gangFile = Main.getMain().getGangFile();
      gangConfig = gangFile.getCustomConfig();
      loadGangs();
   }

}

