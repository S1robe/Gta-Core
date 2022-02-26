package me.strobe.gang.Utils;

import me.strobe.gang.Files.CustomFile;
import me.strobe.gang.Gang;
import me.strobe.gang.Main;
import me.strobe.gang.Member;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class MemberUtils {

   private static final HashMap<UUID, Member> members = new HashMap<>();
   private static CustomFile memberFile;
   private static FileConfiguration memberConfig;

   private MemberUtils(){}

   public static void addNewMember(Member m){
      members.put(m.getUuid(), m);
   }

   public static void loadAllMembers(){
      memberConfig.getKeys(false).forEach(m -> {
         members.put(UUID.fromString(m), (Member) memberConfig.get(m));
      });
   }

   public static void saveMember(Member m){
      memberFile.reloadCustomConfig();
      memberConfig = memberFile.getCustomConfig();
      memberConfig.set(m.getUuid().toString(), null);
      memberFile.saveCustomConfig();
   }

   public static void saveMembers(){
      memberFile.reloadCustomConfig();
      memberConfig = memberFile.getCustomConfig();
      for(Gang value : GangUtils.getGangs().values()) {
         for(Member m : value.getMembers().values()){
            memberConfig.set(m.getUuid().toString(), m);
         }
      }
   }

   public static Member getMemberFromStringUUID(String uuid){
      return (Member) memberConfig.get(uuid);
   }

   public static Member getMemberFromUUID(UUID uuid){
      return members.get(uuid);
   }

   public static Member getMemberFromPlayer(OfflinePlayer p){
      return members.get(p.getUniqueId());
   }

   public static void handleOnQuit(Player p){
      Member m = members.get(p.getUniqueId());
      saveMember(m);
      m.setInGangChat(false);
      p.removeMetadata(Member.inGangChatMeta, Main.getMain());
   }

   public static void deleteMember(Member m){
      memberFile.reloadCustomConfig();
      memberConfig = memberFile.getCustomConfig();
      memberConfig.set(m.getUuid().toString(), null);
      memberFile.saveCustomConfig();
   }

   public static void init(){
      memberFile = Main.getMain().getMemberFile();
      memberConfig = memberFile.getCustomConfig();
      loadAllMembers();
   }

}
