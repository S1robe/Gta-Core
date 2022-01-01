package me.strobe.bankrobberies;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import lombok.Setter;
import me.Strobe.Core.Utils.GenUtils;
import me.Strobe.Core.Utils.Looting.WeightedRandomBag;
import me.Strobe.Core.Utils.PlayerUtils;
import me.Strobe.Core.Utils.RegionUtils;
import me.Strobe.Core.Utils.User;
import me.strobe.bankrobberies.utils.RobItem;
import me.strobe.bankrobberies.utils.RobUtils;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

@SerializableAs("Robbery")
public class Robbery implements ConfigurationSerializable {

   @Getter @Setter private transient Player robber;

   public enum Phase{
      IDLE, ROBBING, ROBBED, CONTESTED;
   }

   @Getter private transient ProtectedRegion activeRegion;
   @Getter private transient WeightedRandomBag<RobItem> rewards = new WeightedRandomBag<>();
   @Getter private transient Location location;
   @Getter private transient int startrunID;
   @Getter private transient int endrunID;

   @Getter private String regID;
   @Getter private int id; //save as NPC ID
   @Getter private List<String> cmdOnSucc  = new ArrayList<>();
   @Getter private List<String> cmdOnStart = new ArrayList<>();
   @Getter private List<String> cmdOnFail = new ArrayList<>();
   @Getter private List<String> cmdOnEnd = new ArrayList<>();
   @Getter @Setter
   private Phase phase = Phase.IDLE;
   @Getter @Setter
   private int timeToRob = 60; //seconds
   @Getter @Setter
   private int timeToReset = 300; //seconds
   @Getter @Setter
   private int minRewards = 1;
   @Getter @Setter
   private int maxRewards = 2;

   public Robbery(ProtectedRegion pr, NPC npc, int timeToRob, int timeToReset, int min, int max){
      this.activeRegion  = pr;
      this.regID         = pr.getId();
      this.id            = npc.getId();
      this.location      = npc.getStoredLocation();
      this.timeToRob     = (timeToRob <= 0)? RobUtils.defaultTimeToRob : timeToRob;
      this.timeToReset   = (timeToReset <= 0)? RobUtils.defaultTimeToReset : timeToReset;
      this.minRewards    = (min <= 0) || (min > max)? RobUtils.defaultMinDrop : min;
      this.maxRewards    = (max <= 0) || (max < min)? RobUtils.defaultMaxDrop : max;
   }

   public Robbery(Map<String, Object> dict){
      this.id = (int) dict.get("id");
      this.location = RegionUtils.locationDeserializer((String) dict.get("location"));
      this.regID = (String) dict.get("regID");
      this.activeRegion = Main.getWG().getRegionManager(this.location.getWorld()).getRegion(regID);
      List<RobItem> x = (List<RobItem>) dict.get("rewards");
      for(RobItem e: x) {
         rewards.addEntry(e, e.getWeight());
      }
      this.cmdOnSucc = (List<String>) dict.get("cmdOnSucc");
      this.cmdOnStart = (List<String>) dict.get("cmdOnStart");
      this.cmdOnFail = (List<String>) dict.get("cmdOnFail");
      this.cmdOnEnd = (List<String>) dict.get("cmdOnEnd");
      this.phase = Phase.valueOf((String) dict.get("phase"));
      this.timeToRob = (int) dict.get("timeToRob");
      this.timeToReset = (int) dict.get("timeToReset");
      RobUtils.resetRob(this);
   }

   public static Robbery deserialize(Map<String, Object> dict){
      return new Robbery(dict);
   }

   @Override
   public Map<String, Object> serialize() {
      Map<String, Object> dict = new HashMap<>();
      dict.put("id", id);
      dict.put("location", RegionUtils.locationSerializer(location));
      dict.put("regID", regID);
      dict.put("cmdOnEnd", cmdOnEnd);
      dict.put("cmdOnSucc", cmdOnSucc);
      dict.put("cmdOnStart", cmdOnStart);
      dict.put("cmdOnFail", cmdOnFail);
      dict.put("phase", phase.toString());
      dict.put("timeToRob", timeToRob);
      dict.put("timeToReset", timeToReset);
      dict.put("rewards", rewards.getInnerObjects());
      return dict;
   }

   public boolean addReward(RobItem reward){
      if(rewards.getInnerObjects().contains(reward))
         return false;
      rewards.addEntry(reward, reward.getWeight());
      RobUtils.saveRob(regID);
      return true;
   }

   public boolean addCommand(String cmd, boolean onStart, boolean onSuccess, boolean onFail, boolean onEnd){
      if(onStart && onSuccess && onFail && onEnd && !cmdOnStart.contains(cmd) && !cmdOnSucc.contains(cmd) && !cmdOnFail.contains(cmd) && !cmdOnEnd.contains(cmd) )
         return cmdOnStart.add(cmd) && cmdOnSucc.add(cmd) && cmdOnEnd.add(cmd) && cmdOnFail.add(cmd);

      else if(onStart && onSuccess && onFail && !cmdOnStart.contains(cmd) && !cmdOnSucc.contains(cmd) && !cmdOnFail.contains(cmd))
         return cmdOnStart.add(cmd) && cmdOnSucc.add(cmd) && cmdOnFail.add(cmd);

      else if(onStart && onSuccess && onEnd && !cmdOnStart.contains(cmd) && !cmdOnSucc.contains(cmd) && !cmdOnEnd.contains(cmd))
         return cmdOnStart.add(cmd) && cmdOnSucc.add(cmd) && cmdOnEnd.add(cmd);

      else if(onStart && onFail && onEnd && !cmdOnStart.contains(cmd) && !cmdOnFail.contains(cmd) && !cmdOnEnd.contains(cmd))
         return cmdOnStart.add(cmd) && cmdOnEnd.add(cmd) && cmdOnFail.add(cmd);

      else if(onSuccess && onFail && onEnd && !cmdOnSucc.contains(cmd) && !cmdOnFail.contains(cmd) && !cmdOnEnd.contains(cmd))
         return cmdOnSucc.add(cmd) && cmdOnEnd.add(cmd) && cmdOnFail.add(cmd);

      else if(onStart && onSuccess && !cmdOnStart.contains(cmd) && !cmdOnSucc.contains(cmd))
         return cmdOnStart.add(cmd) && cmdOnSucc.add(cmd);

      else if(onStart && onEnd && !cmdOnStart.contains(cmd) && !cmdOnEnd.contains(cmd))
         return cmdOnStart.add(cmd) && cmdOnEnd.add(cmd);

      else if(onFail && onEnd && !cmdOnFail.contains(cmd) && !cmdOnEnd.contains(cmd))
         return cmdOnEnd.add(cmd) && cmdOnFail.add(cmd);

      else if(onSuccess && onFail  && !cmdOnSucc.contains(cmd) && !cmdOnFail.contains(cmd))
         return cmdOnSucc.add(cmd) && cmdOnFail.add(cmd);

      else if(onStart && onFail && !cmdOnSucc.contains(cmd) && !cmdOnEnd.contains(cmd))
         return cmdOnStart.add(cmd) && cmdOnFail.add(cmd);

      else if(onSuccess && onEnd && !cmdOnSucc.contains(cmd) && !cmdOnEnd.contains(cmd))
         return cmdOnSucc.add(cmd) && cmdOnEnd.add(cmd);

      else if(onFail && !cmdOnFail.contains(cmd))
         return cmdOnFail.add(cmd);

      else if(onEnd && !cmdOnEnd.contains(cmd))
         return cmdOnEnd.add(cmd);

      else if(onStart && !cmdOnStart.contains(cmd))
         return cmdOnStart.add(cmd);

      else if(onSuccess && !cmdOnSucc.contains(cmd))
         return cmdOnSucc.add(cmd);
      RobUtils.saveRob(regID);
      return false;
   }

   public void start(Player r){
      this.robber = r;
      User u = User.getByPlayer(r);
      if(RobUtils.getRobbing().get(this) == null) {
         RobUtils.getRobbing().put(this, u);
         cmdOnStart.forEach(cmd -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd));
         Robbery.this.setPhase(Phase.ROBBING);
         BukkitRunnable start = new BukkitRunnable() {
            @Override
            public void run() {
               if(robber == null) {
                  this.cancel();
                  Bukkit.getScheduler().cancelTask(endrunID);
                  setPhase(Robbery.Phase.IDLE);
                  Main.getCitizens().getNPCRegistry().getById(getId()).spawn(r.getLocation());
                  setRobber(null);
                  startrunID = -1;
               }
               else if(!robber.isOnline()|| robber.isDead() || !isPlayerInStore(robber)) {
                  this.cancel();
                  Bukkit.getScheduler().cancelTask(endrunID);
                  setPhase(Robbery.Phase.IDLE);
                  Main.getCitizens().getNPCRegistry().getById(getId()).spawn(r.getLocation());
                  setRobber(null);
                  startrunID = -1;

                  robber.removeMetadata("ROBBING", Main.getMain());
                  u.sendPlayerMessage(RobUtils.Text.ROB_CANCELED.create());
               }
            }
         };
         BukkitRunnable reset = new BukkitRunnable() {
            @Override
            public void run() {
               RobUtils.resetRob(Robbery.this);
            }
         };
         BukkitRunnable end = new BukkitRunnable() {
            @Override
            public void run() {
               robber.removeMetadata("ROBBING", Main.getMain());
               Main.getCitizens().getNPCRegistry().getById(id).despawn();
               Bukkit.getScheduler().cancelTask(startrunID);
               cmdOnEnd.forEach(cmd -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd));
               Robbery.this.setPhase(Phase.ROBBED);
               robber = null;
               RobUtils.getRobbing().remove(this);
               endrunID = reset.runTaskLater(Main.getMain(), timeToReset * 20L).getTaskId();
               giveRewards(u);
               u.sendPlayerMessage(RobUtils.Text.ROB_FINISH_SUCCESS.create());
            }
         };
         startrunID = start.runTaskTimer(Main.getMain(), 20L, 20L).getTaskId();
         endrunID = end.runTaskLater(Main.getMain(), timeToRob*20L).getTaskId();
      }
   }

   public boolean isPlayerInStore(Player p){
      return location.getWorld().equals(p.getWorld()) &&
             activeRegion.contains(p.getLocation().getBlockX(),p.getLocation().getBlockY(), p.getLocation().getBlockZ());
   }

   public boolean isBeingRobbed(){
      return robber != null;
   }

   public void giveRewards(User p){
      if(rewards.getInnerObjects().isEmpty()) return;
      int num = GenUtils.getRandInt(minRewards, maxRewards);
      List<RobItem> rewards = new ArrayList<>();
      while(num >0){
         RobItem x = getRewards().getRandom();
         if(x.isCommand()){
            x.getCommands().forEach(cmd -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd));
            num--;
            continue;
         }
         rewards.add(x);
         num--;
      }
      rewards.stream().map(RobItem::getRandom).forEach(item -> {
         if(!PlayerUtils.offerPlayerItem(p.getPLAYER().getPlayer(), item)){
            p.getPLAYER().getPlayer().getLocation().getWorld().dropItem(p.getPLAYER().getPlayer().getLocation(), item);
            p.sendPlayerMessage(RobUtils.Text.RECIEVED_REWARDS_DROPPED.create((item.hasItemMeta() ? item.getItemMeta().getDisplayName() : item.getType()), regID));
         }
         else{
            p.sendPlayerMessage(RobUtils.Text.RECIEVED_REWARDS.create((item.hasItemMeta() ? item.getItemMeta().getDisplayName() : item.getType()), regID));
         }
      });
   }

   @Override
   public String toString(){
      World w = this.location.getWorld();
      Location l = location;
      return "Robbery : " +this.getRegID()+" \n" +
             "World: "+w.getName()+ " X: " +l.getBlockX() +" Y: " + l.getBlockY() +" Z: " + l.getBlockZ()+"\n" +
             "";
   }
}
