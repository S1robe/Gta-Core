package me.strobe.bankrobberies.utils;

import com.sk89q.worldedit.BlockVector2D;
import lombok.Getter;
import me.Strobe.Core.Utils.StringUtils;
import me.Strobe.Core.Utils.User;
import me.strobe.bankrobberies.Files.CustomFile;
import me.strobe.bankrobberies.Main;
import me.strobe.bankrobberies.Robbery;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RobUtils {

   @Getter private static Map<String, Robbery> robberies = new HashMap<>();
   @Getter private static Map<Robbery, User> robbing = new HashMap<>();
   @Getter private static List<String> blacklistedCommands = new ArrayList<>();
   private static CustomFile robConfigFile;
   private static FileConfiguration fileConfiguration;

   public static int defaultTimeToRob;
   public static int defaultTimeToReset;
   public static int defaultMinDrop;
   public static int defaultMaxDrop;

   @Getter
   private static double maxDistanceHearShouts;

   private RobUtils(){}

   public static void saveAllRobs(){
      robberies.forEach((s, r) -> fileConfiguration.set(s, r));
   }
   public static void loadAllRobs(){
      robbing.clear();
      robberies.clear();
      robConfigFile.reloadCustomConfig();
      fileConfiguration = robConfigFile.getCustomConfig();
      fileConfiguration.getKeys(false).forEach(id -> {
         Robbery r = (Robbery) fileConfiguration.get(id);
         System.out.println(r);
         robberies.put(r.getRegID(), r);
      });
   }
   public static void saveRob(String id){
      fileConfiguration.set(id, robberies.get(id));
      robConfigFile.saveCustomConfig();
   }
   public static void loadRob(String id){
      robConfigFile.reloadCustomConfig();
      fileConfiguration = robConfigFile.getCustomConfig();
      robberies.put(id, (Robbery) fileConfiguration.get(id));
   }
   public static void deleteRob(Robbery r){
      robberies.remove(r.getRegID());
      fileConfiguration.set(r.getRegID(), null);
      robConfigFile.saveCustomConfig();
   }
   public static void resetRob(Robbery r){
      switch(r.getPhase()){
         case ROBBED:
            r.setPhase(Robbery.Phase.IDLE);
            Main.getCitizens().getNPCRegistry().getById(r.getId()).spawn(r.getLocation());
            r.setRobber(null);
            break;
         case ROBBING:
            robbing.remove(r);
            Bukkit.getScheduler().cancelTask(r.getEndrunID());
            Bukkit.getScheduler().cancelTask(r.getStartrunID());
            r.setPhase(Robbery.Phase.IDLE);
            r.setRobber(null);
            break;
      }
   }


   public static void init(){
      robConfigFile           = Main.getMain().getRobDataFile();
      fileConfiguration       = robConfigFile.getCustomConfig();
      maxDistanceHearShouts   = Main.getMain().getMainDataFile().getCustomConfig().getDouble("maxDistanceHearShouts");
      defaultTimeToRob        = Main.getMain().getMainDataFile().getCustomConfig().getInt("default-time-to-rob");
      defaultTimeToReset      = Main.getMain().getMainDataFile().getCustomConfig().getInt("default-time-to-reset");
      defaultMinDrop          = Main.getMain().getMainDataFile().getCustomConfig().getInt("default-min-drop");
      defaultMaxDrop          = Main.getMain().getMainDataFile().getCustomConfig().getInt("default-max-drop");
      loadAllRobs();
   }

   public static Robbery getRobberyByNPC(NPC npcClicked){
      for(Robbery robbery : robberies.values()) {
         if(robbery.getId() == npcClicked.getId())
            return robberies.get(robbery.getRegID());
      }
      return null;
   }

   public static Robbery getRobberyByID(int id){
      for(Robbery robbery : robberies.values()) {
         if(robbery.getId() == id)
            return robberies.get(robbery.getRegID());
      }
      return null;
   }

   public static Robbery getRobberyByRegID(String regID){
      return robberies.get(regID);
   }

   public static Robbery getRobberyByPlayerLocation(Location pLoc){
      for(Robbery robbery : robberies.values()) {
         if(robbery.getActiveRegion().contains(new BlockVector2D(pLoc.getX(), pLoc.getZ())))
            return robberies.get(robbery.getRegID());
      }
      return null;
   }

   public static Robbery getRobberyByRobber(Player p){
      for(Robbery robbery : robbing.keySet()) {
         if(robbery.getRobber().getUniqueId().equals(p.getUniqueId()))
            return robbery;
      }
      return null;
   }

   public static List<RobItem> getLootListByNPCID(int id){
      Robbery r = getRobberyByID(id);
      return r.getRewards().getInnerObjects();
   }

   public static List<RobItem> getLootListByRegID(String regID){
      Robbery r = getRobberyByRegID(regID);
      return r.getRewards().getInnerObjects();
   }

   public static void sendAllWithinRangeAMessage(double distance, Robbery rob, String message){
      User.users.values().stream()
                .filter(u -> u.getPLAYER().getPlayer().getLocation().distance(rob.getLocation()) > distance)
                .forEach(u -> u.sendPlayerMessage(message));
   }



   public enum Text{
      ROB_STARTED(false, "&5&l(!) &7You have started robbing {0}!"),
      ROB_STARTED_SHOUT(false, "&5&l(!) &b{0}&7 is being robbed -> (X: &b{1}&7, Z:&b{2}&7) | {3} min"),
      ROB_CANCELED_LOGGED_SHOUT(false, "&5&l(!) &7{0} robbing stopped because they logged out!"),
      ROB_CANCELED_DIED_SHOUT(false, "&5&l(!) &7{0} robbing stopped because they died!"),
      ROB_CANCELED_TELEPORT_SHOUT(false, "&5&l(!) &7{0} robbing stopped because they tp'ed out!"),
      ROB_CANCELED(false, "&5&l(!)&7 Robbery canceled."),
      ROB_FINISH_SUCCESS(false, "&5&l(!) Robbery successful, Run!"),
      ROB_PROGRESS_CHECK(false, "&5&l(!) Robbery {0}% done, hurry up!"),
      ROB_FINISHED_FAIL(false, "&5&l(!) Robbery failed! Get out while you can!"), //todo implement traping of robbers? force break glass?
      COMMAND_DENY_ROBBING(false, "&5&l(!) You cannot use this command while robbing!"),


      ROB_CREATED(true, "&5&l(!) The robbery with name {0} has been created!"),
      ROB_CREATE_USAGE(true, "&5&l(!) &7Use the rpnc create command like this:\n" +
                             " &b1. &7Make a world edit selection, and select an npc.\n" +
                             " &b/rnpc create <name> (timeToRob) &7:name is the rob/region name,\n" +
                             " &b-&7(timeTorob) is optional default is 60 (sec).\n"),
      ROB_DELETE_USAGE(true, "&5&l(!)&7 Use the rnpc delete command like this:\n" +
                             " &b1. &7Select an NPC,\n" +
                             " &b2. &7/rnpc delete.\n"),
      ROB_ADDREWARD_USAGE(true, "&5&l(!)&7 Use the rnpc"),
      ROB_ADDCMD_USAGE(true, "&5&l(!) &7Use the rnpc addcmd command list this:\n" +
                             " &b1. &7Select a RobNPC then run /rnpc addcmd <cmd> <start> <success>\n" +
                             " &b -&7<cmd> is the command without the '/' ex: gmc.\n" +
                             " &b -&7<start> is if the command runs at the start of the rob.\n" +
                             " &b -&7<success> is if the command runs on successful robs\n" +
                             " &b -&7<fail> is if the command runs when a rob fails\n" +
                             " &b -&7<end> is if the command runs when the rob ends.\n"),
      REGION_EXISTS(true, "&5&l(!) &7The region {0} already exists!"),
      NO_SELECTION(true, "&5&l(!) &7You must make a world edit selection before creating a robbery."),
      ROB_DELETED(true, "&5&l(!) &7The robbery {0} has been deleted!"),
      NOT_A_ROB(true, "&5&l(!) &7There was no rob with the NPC-id {0}"),
      ROB_RESET(true, "&5&l(!) &7The robbery {0} has been reset."),
      CMD_ADD_SUCCESS(true, "&5&l(!) &7The command {0} has been added to the robbery {1}."),
      CMD_ADD_FAIL(true, "&5&l(!) &7The command {0} failed to be added to robbery {1}."),
      SUCCESS_ITEM_ADD(true, "&5&l(!) &7The item in your hand has been added to robbery {0}."),
      FAIL_ITEM_ADD(true, "&5&l(!) &7The item in your hand could not be added to robbery {0}."),
      ROBTIME_UPDATED(true, "&5&l(!) &7The time to rob {0} is now {1}."),
      RESETTIME_UPDATED(true, "&5&l(!) &7The time to reset {0} is now {1}."),
      ALREADY_ROBBING(false, "&5&l(!) &7This is already being robbed!"),
      RECIEVED_REWARDS(false, "&5&l(!)&7 You stole {0} from {1}."),
      RECIEVED_REWARDS_DROPPED(false, "&5&l(!)&7 You stole {0} from {1} but dropped it cause your inventory is full!"),
      ;

      final String text;
      final boolean tagged;

      Text(boolean tag, String msg){
         this.tagged = tag;
         this.text = msg;
      }

      public String create(Object... replacements){
         String hold = MessageFormat.format(text, replacements);
         return StringUtils.color(hold);
      }
   }

}
