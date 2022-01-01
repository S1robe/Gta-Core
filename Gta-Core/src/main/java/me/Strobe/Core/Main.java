package me.Strobe.Core;

import lombok.Getter;
import me.Strobe.Core.Commands.LootCommands;
import me.Strobe.Core.Commands.UserRelatedCommands;
import me.Strobe.Core.Events.*;
import me.Strobe.Core.Files.CustomFile;
import me.Strobe.Core.Files.FileManager;
import me.Strobe.Core.Utils.CopUtils;
import me.Strobe.Core.Utils.Displays.ScoreboardManager;
import me.Strobe.Core.Utils.Looting.LootItem;
import me.Strobe.Core.Utils.LootingUtils;
import me.Strobe.Core.Utils.User;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.util.logging.Level;

public final class Main extends JavaPlugin {

   public static final String PLUGINPREFIX = "&9&l[&6&nGTA-MC&9&l]&7: ";

   @Getter
   static Main main;
   public final String playerDataFolder = getDataFolder() + System.getProperty("file.separator") + "PlayerData";
   private PluginLogger logger;
   @Getter
   private PluginManager plMan;
   @Getter
   private BukkitScheduler bukkitScheduler;
   @Getter
   private CustomFile mainDataFile;
   @Getter
   private CustomFile lootFile;
   @Getter
   private CustomFile copDataFile;
   @Getter
   private FileManager fileMan;
   @Getter
   private Economy econ;

   public Main() {
      main = this;
      ConfigurationSerialization.registerClass(LootItem.class, "Loot");
      ConfigurationSerialization.registerClass(User.class, "User");
   }

   @Override
   public void onDisable() {
      User.saveAllPlayerData();
      LootingUtils.saveAllLoot();
      LootingUtils.saveActiveWorlds();
      CopUtils.save();
      fileMan.saveAllFiles();
   }

   @Override
   public void onEnable() {
      init();
      registerEvents();
      startBoardUpdates();
   }

   private void init() {
      logger = new PluginLogger(main);
      plMan = getServer().getPluginManager();
      bukkitScheduler = getServer().getScheduler();
      if (!setupEconomy()) {
         System.out.println("Vault not found, some parts of this plugin will not work....");
      }
      loadFiles();
      initUtils();
   }

   private void registerEvents() {
      plMan.registerEvents(new PlayerEvents(), this);
      plMan.registerEvents(new PvpEvents(), this);
      plMan.registerEvents(new PveEvents(), this);
      plMan.registerEvents(new LootEvents(), this);
      plMan.registerEvents(new EntityEvents(), this);
      plMan.registerEvents(new CopEvents(), this);
      plMan.registerEvents(new GUIEvents(), this);
      getCommand("loot").setExecutor(new LootCommands());
      getCommand("stats").setExecutor(new UserRelatedCommands());
      getCommand("cop").setExecutor(new UserRelatedCommands());
      getCommand("gta").setExecutor(new UserRelatedCommands());
      getCommand("help").setExecutor(new UserRelatedCommands());
   }

   private void startBoardUpdates() {
      Bukkit.getOnlinePlayers().forEach(p -> User.userJoined(p.getUniqueId()));
      bukkitScheduler.runTaskTimer(this, ScoreboardManager::updateScoreboard, 0L, 20L);
      logger.log(Level.INFO, "Scoreboard runnable activated.");
   }

   private void loadFiles() {
      fileMan = new FileManager();
      mainDataFile = new CustomFile("config");
      lootFile = new CustomFile("loot");
      copDataFile = new CustomFile("copData");
      fileMan.registerFile(mainDataFile);
      fileMan.registerFile(lootFile);
      fileMan.registerFile(copDataFile);
      if(new File(playerDataFolder).mkdir())
         logger.log(Level.INFO, "PlayerFiles Data folder successfully created");
      else
         logger.log(Level.INFO, "PlayerFiles Folder not created because one already exists.");
   }

   public void initUtils() {
      LootingUtils.init();
      CopUtils.init();
   }

   private boolean setupEconomy() {
      if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
         return false;
      }
      RegisteredServiceProvider<Economy> rsp = this.getServer().getServicesManager().getRegistration(Economy.class);
      if (rsp == null) {
         return false;
      }
      this.econ = rsp.getProvider();
      return this.econ != null;
   }
}

/*
    Monsters are a faction, Unify Skeletons, Zombies;;  Enderman are separate;; Villagers, Witches;;
    Setup regions for these factions that could have different loot etc based on the area that is focused.

 */