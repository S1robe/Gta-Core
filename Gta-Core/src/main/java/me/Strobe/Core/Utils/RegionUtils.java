package me.Strobe.Core.Utils;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import me.Strobe.Core.Main;
import org.bukkit.*;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public final class RegionUtils {

   private RegionUtils() {
   }

   /**
    * check if a location allows pvp
    *
    * @param loc location to check
    *
    * @return location allows pvp
    */
   public static boolean allowsPVP(Location loc) {
      ApplicableRegionSet set = WGBukkit.getPlugin().getRegionManager(loc.getWorld()).getApplicableRegions(loc);
      return set.queryState(null, DefaultFlag.PVP) != StateFlag.State.DENY;
   }


   /**
    * play a broken block effect at a location (will also play broken block sound)
    *
    * @param loc     location to play effect
    * @param blockID block id for block particle
    */
   public static void playBlockEffect(Location loc, int blockID) {
      loc.getWorld().spigot().playEffect(loc, Effect.STEP_SOUND, blockID, 0, (float) 0, (float) 0, (float) 0, (float) 0.01, 5, 10);
   }

   /**
    * play global sound to all players near location played
    *
    * @param loc    location to play sound
    * @param sound  sound to play
    * @param volume volume of sound
    * @param pitch  pitch of sound
    *
    * @see Sound for more information
    */
   public static void playSound(Location loc, Sound sound, float volume, float pitch) {
      loc.getWorld().playSound(loc, sound, volume, pitch);
   }

   /**
    * play a particle effect to all players in a 10 block radius
    *
    * @param loc    location to play effect
    * @param effect particle effect to play
    * @param speed  speed of particle (amount of time before particle disappears)
    * @param amount amount of particles to spawn
    *
    * @see Effect for more information
    */
   public static void playEffect(Location loc, Effect effect, float speed, int amount) {
      loc.getWorld().spigot().playEffect(loc.add(0.5, 0, 0.5), effect, 0, 0, (float) 1.0, (float) 1.0, (float) 1.0, speed, amount, 10);
   }

   public static List<String> locationSerializer(List<Location> l) {
      List<String> x = new ArrayList<>();
      l.forEach(location -> x.add(locationSerializer(location)));
      return x;
   }

   public static String locationSerializer(Location l) {
      return l.getWorld().getName() + ";" + l.getX() + ";" + l.getY() + ";" + l.getZ();
   }

   public static List<Location> locationDeserializer(List<String> l) {
      List<Location> f = new ArrayList<>();
      l.forEach(string -> f.add(locationDeserializer(string)));
      return f;
   }

   //weird static access issue.
   public static Location locationDeserializer(String l) {
      String[] partsOfLoc = l.split(";");
      World world = Main.getMain().getServer().getWorld(partsOfLoc[0]);
      double x = Double.parseDouble(partsOfLoc[1]);
      double y = Double.parseDouble(partsOfLoc[2]);
      double z = Double.parseDouble(partsOfLoc[3]);
      return new Location(world, x, y, z);
   }

   public static boolean isSign(Block b) {
      Material m = b.getType();
      return m.equals(Material.SIGN) || m.equals(Material.SIGN_POST) || m.equals(Material.WALL_SIGN);
   }

}
