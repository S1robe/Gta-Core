package me.Strobe.Core.Utils;

import me.Strobe.Core.Main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static me.Strobe.Core.Utils.DelayedTeleport.TELEPORT_ACTION;
import static me.Strobe.Core.Utils.ItemUtils.createItem;


public final class CopUtils {

   public static final Map<UUID, User> cops = new HashMap<>();
   public static final Map<UUID, List<PigZombie>> plrPigmen = new HashMap<>();
   public static final Map<UUID, Queue<Integer>> playerRunnableIds = new HashMap<>();
   public static List<String> blackListedCommands;
   public static Location safeHouse;
   public static int modeChangeTimeout;
   public static int minCopSpawn;
   public static int maxCopSpawn;
   public static int lowWantedForCop;
   public static int highWantedForCop;
   public static double moneyForWanted;
   public static List<ItemStack> inventory;
   /**
    * percentage of money droped by a player on death
    */
   public static double percentMoneyToDropOnDeath;
   /**
    * money rewarded for killing a cop
    */
   public static double moneyForKillCop;

   private CopUtils() {}

   public static void spawnCopsOnPlayer(final User user, int num, int waves) {
      if(num >=1 && waves >= 1) {
         user.sendPlayerMessage(StringUtils.Text.COPS_ENGAGE.create());
         BukkitRunnable spawn =  new BukkitRunnable() {
            private int copOfWaves = waves;
            private final UUID uuid = user.getPlayerUUID();
            @Override
            public void run() {
               if(!LootingUtils.isWorldMobActive(user.getPLAYER().getPlayer().getWorld()) || copOfWaves == 0) {
                  this.cancel();
                  return;
               }
               if(!user.getPLAYER().isOnline())
                  return;

               plrPigmen.get(uuid).addAll(createPigmenCops(user, num));
               user.sendPlayerMessage(StringUtils.Text.COPS_BACKUP.create());
               copOfWaves--;
            }

            @Override
            public void cancel() {
               if(user.getPLAYER().isOnline())
                  user.sendPlayerMessage(StringUtils.Text.COPS_DISENGAGE.create());
               playerRunnableIds.get(uuid).poll();
               despawnAllCops(uuid);
               super.cancel();
            }
         };
         playerRunnableIds.get(user.getPlayerUUID()).add(spawn.runTaskTimer(Main.getMain(), 0L, 600L).getTaskId());
      }
   }

   private static List<PigZombie> createPigmenCops(User target, int amtToSpawn) {
      List<PigZombie> pigList = new ArrayList<>();
      Player player = (Player) target.getPLAYER();
      int WL = target.getWantedlevel();
      for(; amtToSpawn > 0; amtToSpawn--) {
         PigZombie piggie = (PigZombie) ((Player) target.getPLAYER()).getWorld().spawnEntity(((Player) target.getPLAYER()).getLocation(), EntityType.PIG_ZOMBIE);
         piggie.getEquipment().setArmorContents(new ItemStack[]{ pigCopBoots(WL),
                                                                 pigCopPants(WL),
                                                                 pigCopChest(WL),
                                                                 pigCopHelm(WL) });
         piggie.getEquipment().setItemInHand(pigCopSword(WL));
         piggie.setCustomName(StringUtils.color("&c&lWanted: &6" + player.getName()));
         piggie.setMetadata(target.getPlayer_Name(), new FixedMetadataValue(Main.getMain(), true));
         piggie.setTarget(target.getPLAYER().getPlayer());
         piggie.setAngry(true);
         piggie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 240, 1, false, false));
         pigList.add(piggie);
      }
      return pigList;
   }

   private static ItemStack pigCopBoots(int wL) {
      ItemStack itemStack = createItem(Material.DIAMOND_BOOTS, 1);
      if(wL < 20)
         return itemStack;
      return applyCopEnchants(wL, itemStack);
   }

   private static ItemStack pigCopPants(int wL) {
      ItemStack itemStack;
      if(wL > 20)
         itemStack = createItem(Material.DIAMOND_LEGGINGS, 1);
      else
         itemStack = createItem(Material.IRON_LEGGINGS, 1);

      return applyCopEnchants(wL, itemStack);
   }

   private static ItemStack pigCopChest(int wL) {
      ItemStack itemStack;
      if(wL > 20)
         itemStack = createItem(Material.DIAMOND_CHESTPLATE, 1);
      else
         itemStack = createItem(Material.IRON_CHESTPLATE, 1);

      return applyCopEnchants(wL, itemStack);
   }

   private static ItemStack pigCopHelm(int wL) {
      ItemStack itemStack = createItem(Material.DIAMOND_HELMET, 1);
      return applyCopEnchants(wL, itemStack);
   }

   private static ItemStack pigCopSword(int wL) {
      ItemStack itemStack = createItem(Material.STONE_AXE, 1);
      if(wL >= 80)
         itemStack = createItem(Material.DIAMOND_SWORD, 1);
      else if(wL >= 70)
         itemStack = createItem(Material.DIAMOND_AXE, 1);
      else if(wL >= 60)
         itemStack = createItem(Material.IRON_SWORD, 1);
      else if(wL >= 50)
         itemStack = createItem(Material.IRON_SWORD, 1);
      else if(wL >= 40)
         itemStack = createItem(Material.IRON_AXE, 1);
      else if(wL >= 30)
         itemStack = createItem(Material.GOLD_SWORD, 1);
      else if(wL >= 20)
         itemStack = createItem(Material.GOLD_AXE, 1);
      else if(wL >= 10)
         itemStack = createItem(Material.STONE_SWORD, 1);

      if(wL > 100) {
         itemStack.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 6);
         return itemStack;
      }
      else
         itemStack.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2);


      return itemStack;
   }

   private static ItemStack applyCopEnchants(int wL, ItemStack itemStack) {
      if(wL > 100) {
         itemStack.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.PROTECTION_ENVIRONMENTAL, 5);
         itemStack.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.PROTECTION_PROJECTILE, 5);
         itemStack.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.PROTECTION_EXPLOSIONS, 5);
         return itemStack;
      }
      if(wL/20 == 0) return itemStack;
      itemStack.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.PROTECTION_ENVIRONMENTAL, wL / 20);
      itemStack.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.PROTECTION_PROJECTILE, wL / 20);
      itemStack.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.PROTECTION_EXPLOSIONS, wL / 20);
      return itemStack;
   }

   public static void save(){
      FileConfiguration f = Main.getMain().getCopDataFile().getCustomConfig();
      if(safeHouse !=null)
         f.set("safeHouse", RegionUtils.locationSerializer(safeHouse));
      f.set("changeModeTimeOut", modeChangeTimeout);
      f.set("cop-kill-money", CopUtils.moneyForKillCop);
      if(blackListedCommands !=null)
         f.set("cop-blacklisted-commands", blackListedCommands);
      f.set("min-cops", minCopSpawn);
      f.set("max-cops", maxCopSpawn);

      if(inventory !=null)
         f.set("inventory", inventory);
      f.set("low-WL-kill-Cop", lowWantedForCop);
      f.set("high-WL-kill-Cop", highWantedForCop);
      Main.getMain().getCopDataFile().saveCustomConfig();
   }

   public static boolean attemptChangeToFromCop(User u){
      if(u.getPLAYER().getPlayer().hasMetadata(TELEPORT_ACTION)) {
         u.sendPlayerMessage("&e&l(!)&7 Youre already changing mode!");
         return false;
      }
      if(safeHouse == null) {
         u.sendPlayerMessage("Unable to change mode, safehouse not set yet.");
         return false;
      }
      if(u.getWantedlevel() > 0){
         u.sendPlayerMessage("&e&l(!)&7 You are wanted by the police, you cant be one of them!");
         return false;
      }
      Player p =  u.getPLAYER().getPlayer();
      if(RegionUtils.allowsPVP(p.getLocation())){
         DelayedTeleport.doDelayedTeleport(Main.getMain(), p, safeHouse, modeChangeTimeout);
         new BukkitRunnable() {
            @Override
            public void run() {
               if(p.hasMetadata("Successful Teleport"))
                  switchCopMode(u);
               p.removeMetadata("Successful Teleport", Main.getMain());
            }
         }.runTaskLater(Main.getMain(), modeChangeTimeout * 20L);
         return true;

      }
      else {
         p.teleport(safeHouse);
         switchCopMode(u);
         return true;
      }
   }

   private static void switchCopMode(User u){
      Player p = u.getPLAYER().getPlayer();
      if(u.isCop()){
         u.setCop(false);
         setInventory(u.getSavedInventory(), p);
         u.setSavedInventory(new ArrayList<>());
         u.savePlayerData();
         cops.remove(u.getPlayerUUID());
      }
      else {
         u.setCop(true);
         List<ItemStack> inv = new ArrayList<>();
         inv.addAll(Arrays.asList(p.getInventory().getArmorContents()));
         inv.addAll(Arrays.asList(p.getInventory().getContents()));
         u.setSavedInventory(inv);

         setInventory(CopUtils.inventory.toArray(new ItemStack[0]), p);
         u.savePlayerData();
         cops.put(u.getPlayerUUID(), u);
      }
   }

   private static void setInventory(ItemStack[] inv, Player p){
      p.getInventory().clear();
      p.getInventory().setBoots(inv[0]);
      p.getInventory().setLeggings(inv[1]);
      p.getInventory().setChestplate(inv[2]);
      p.getInventory().setHelmet(inv[3]);
      p.getInventory().setContents(Arrays.copyOfRange(inv, 4, inv.length));
   }

   public static ItemStack wantedPlayersHead(User u) {
      ItemStack item = u.getUserItem();
      ItemUtils.appendToLore(item, "&dWanted Reward: &a&l$&a" + u.getWantedlevel() * moneyForWanted, "&7Click to select target wanted player.");
      return item;
   }

   public static ItemStack getTrackedPlayerItem(User u) {
      if(u != null) {
         if(u.getTracked() == null) {
            return createItem(Material.STAINED_GLASS_PANE, 1, (byte) 14, "&c&lNot Tracking Player");
         }
         else {
            User t = u.getTracked();

            ItemStack item = ItemUtils.getSkullOf(t.getPlayerUUID());
            ItemUtils.setDisplayName(item, "&c&l" + t.getPlayer_Name() + " (&7Lvl: " + t.getWantedlevel() + "&c&l)");
            ItemUtils.applyLore(item, "&dWanted Reward: &a&l$&a" + t.getWantedlevel() * moneyForWanted, "&7Click to deselect target wanted player.");
            return item;
         }
      }
      return createItem(Material.STAINED_GLASS_PANE, 1, (byte) 14, "&c&lNot Tracking Player");
   }


   public static void despawnAllCops(UUID pID){
      List<PigZombie> x = plrPigmen.get(pID);
      if(x != null)
         x.forEach(Entity::remove);
      plrPigmen.get(pID).clear();
   }

   public static void init(){
      FileConfiguration f = Main.getMain().getMainDataFile().getCustomConfig();
      blackListedCommands       = f.getStringList("cop-blacklisted-commands");
      percentMoneyToDropOnDeath = f.getDouble("percent-money-drop") / 100;
      f = Main.getMain().getCopDataFile().getCustomConfig();
      moneyForKillCop           = f.getDouble("cop-kill-money");
      lowWantedForCop           = f.getInt("low-WL-kill-Cop");
      if(lowWantedForCop <= 0) lowWantedForCop = 2;
      highWantedForCop          = f.getInt("high-WL-kill-Cop");
      if(highWantedForCop <= 0) highWantedForCop = 5;
      lowWantedForCop           = Math.min(lowWantedForCop, highWantedForCop);

      modeChangeTimeout         = f.getInt("changeModeTimeOut");
      maxCopSpawn               = f.getInt("max-cops");
      minCopSpawn               = f.getInt("min-cops");
      moneyForWanted            = f.getDouble("money-for-wanted");
      inventory                 = new ArrayList<>();
      String house              = f.getString("safeHouse");

      if(!house.equals("")) CopUtils.safeHouse = RegionUtils.locationDeserializer(house);
      Main.getMain().getCopDataFile().getCustomConfig().getList("inventory").forEach(e -> CopUtils.inventory.add((ItemStack) e));
   }

}
