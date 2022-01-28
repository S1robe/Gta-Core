package me.Strobe.Core.Utils;

import lombok.Getter;
import me.Strobe.Core.Main;
import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockChange;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public final class PlayerUtils {

   @Getter private static double unb1breakChance = 0;
   @Getter private static double unb2breakChance = 0;
   @Getter private static double unb3breakChance = 0;
   @Getter private static double unb4breakChance = 0;
   @Getter private static double unb5breakChance = 0;

   private PlayerUtils() {}

   /**
    * remove 1 item from players hand
    *
    * @param p player to remove item from
    */
   public static void removeItemFromPlayer(Player p) {
      ItemStack item = p.getItemInHand();
      if(item.getAmount() - 1 < 1) {
         p.setItemInHand(null);
      }
      else {
         item.setAmount(item.getAmount() - 1);
      }
   }

   /**
    * give player a potion effect
    *
    * @param p       player to give potion effect to
    * @param potion  potion effect to give player
    * @param seconds amount of seconds potion effect will last
    * @param level   level of potion
    *
    * @see PotionEffectType for more information
    */
   public static void givePotionEffect(Player p, PotionEffectType potion, int seconds, int level) {
      p.addPotionEffect(new PotionEffect(potion, seconds * 20, level - 1));
   }

   /**
    * count amount of empty slots player has in their inventory
    *
    * @param player player to check inventory of
    *
    * @return amount of empty slots players inventory has
    */
   public static int countEmptySlots(Player player) {
      return (int) Stream.of(player.getInventory().getContents()).filter(Objects::isNull).count();
   }

   public static void sendPacketToPlayer(Packet<?> packet, Player player) {
      ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
   }

   public static void saveInventory(Inventory inventory) {
      YamlConfiguration config = new YamlConfiguration();

      // Save every element in the list
      saveInventory(inventory, config);
   }

   public static void saveInventory(Inventory inventory, ConfigurationSection destination) {
      // Save every element in the list
      for(int i = 0; i < inventory.getSize(); i++) {
         ItemStack item = inventory.getItem(i);

         // Don't store NULL entries
         if(item != null) {
            destination.set(Integer.toString(i), item);
         }
      }
   }

   public static ItemStack[] loadInventory(String data) throws InvalidConfigurationException {
      YamlConfiguration config = new YamlConfiguration();

      // Load the string
      config.loadFromString(data);
      return loadInventory(config);
   }

   public static ItemStack[] loadInventory(ConfigurationSection source) throws InvalidConfigurationException {
      List<ItemStack> stacks = new ArrayList<>();

      try {
         // Try to parse this inventory
         for(String key : source.getKeys(false)) {
            int number = Integer.parseInt(key);

            // Size should always be bigger
            while(stacks.size() <= number) {
               stacks.add(null);
            }

            stacks.set(number, (ItemStack) source.get(key));
         }
      }
      catch(NumberFormatException e) {
         throw new InvalidConfigurationException("Expected a number.", e);
      }

      // Return result
      return stacks.toArray(new ItemStack[0]);
   }

   /**
    * This method will send a block change using packets, asynchronously, without actually changing the block. The block
    * will thus appear differently to the player, without actually changing altogether.
    *
    * @param player   The player to whom the packet will be sent.
    * @param location The location where the block should be changed.
    * @param material The NEW material to give the block.
    * @param data     The NEW data to give the block.
    */
   public static void maskBlock(Player player, Location location, Material material, byte data) {
      // As some have pointed out, it could be unwise to use this as a static. Consider using it as an instance var.
      new BukkitRunnable() {
         @Override
         public void run() {
            BlockPosition blockPosition = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
            PacketPlayOutBlockChange packet = new PacketPlayOutBlockChange(((CraftWorld) location.getWorld()).getHandle(), blockPosition);
            packet.block = Block.getByCombinedId(material.getId() + (data << 12));
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
         }
      }.runTaskAsynchronously(Main.getMain());
   }

   public static Player getPlayer(String playerName) {
      for(Player offlinePlayer : Bukkit.getOnlinePlayers()) {
         if(playerName.equalsIgnoreCase(offlinePlayer.getName())) {
            return offlinePlayer;
         }
      }
      return null;
   }

   public static OfflinePlayer getOfflinePlayer(String playerName){
      for(OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
         if(playerName.equalsIgnoreCase(offlinePlayer.getName())) {
            return offlinePlayer;
         }
      }
      return null;
   }

   /**
    * @param player The player receiving the item
    * @param item   The item to be offered.
    *
    * @return true if the item was given to the player, false otherwise.q
    */
   public static boolean offerPlayerItem(Player player, ItemStack item) {
      if(player.getInventory().firstEmpty() != -1) {
         player.getInventory().addItem(item);
         return true;
      }
      return false;
   }

   public static boolean doesPlayerHaveItems(Player p, ItemStack... items){
      return Arrays.asList(p.getInventory().getContents()).containsAll(Arrays.asList(items));
   }

   public static void takeItemsFromPlayer(Player p, ItemStack... items){
      Arrays.asList(p.getInventory().getContents()).removeAll(Arrays.asList(items));
      //TODO check if this takes every iteration of the item, or the first found.
   }

   // Plays a spigot effect at the the players given location
   public static void playBlockEffect(Player p, Location loc, int blockID) {
      p.getWorld().spigot().playEffect(loc, Effect.STEP_SOUND, blockID, 0, (float) 0, (float) 0, (float) 0, (float) 0.01, 5, 10);
   }

   public static List<ItemStack> purgeNullsFromInventory(List<ItemStack> inventory){
      List<ItemStack> list = new ArrayList<>();
      for(int i = 0; i < inventory.size(); i++)
            list.add(inventory.remove(i));

      switch(inventory.size()){
         case 0:
            list.add(null);
            list.add(null);
            list.add(null);
            list.add(null);
            break;
         case 1:
            list.add(null);
            list.add(null);
            list.add(null);
            break;
         case 2:
            list.add(null);
            list.add(null);
            break;
         case 3:
            list.add(null);
      }
      inventory.forEach(i -> {
         if(i != null) list.add(i);
      });
      return list;
   }

   public static List<ItemStack> purgeNullsFromLoot(List<ItemStack> inventory){
      List<ItemStack> list = new ArrayList<>();
      inventory.forEach(i -> {
         if(i != null) list.add(i);
      });
      return list;
   }

   public static void init(){
      FileConfiguration f = Main.getMain().getMainDataFile().getCustomConfig();
      unb1breakChance = f.getDouble("unbreaking-1-damage-chance");
      unb2breakChance = f.getDouble("unbreaking-2-damage-chance");
      unb3breakChance = f.getDouble("unbreaking-3-damage-chance");
      unb4breakChance = f.getDouble("unbreaking-4-damage-chance");
      unb5breakChance = f.getDouble("unbreaking-5-damage-chance");
   }
}
