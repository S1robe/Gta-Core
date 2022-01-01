package me.strobe.bankrobberies;

import com.boydti.fawe.Fawe;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import lombok.Getter;
import me.strobe.bankrobberies.Commands.RobCommands;
import me.strobe.bankrobberies.Events.NPCEvents;
import me.strobe.bankrobberies.Events.PlayerListener;
import me.strobe.bankrobberies.Files.CustomFile;
import me.strobe.bankrobberies.Files.FileManager;
import me.strobe.bankrobberies.utils.RobItem;
import me.strobe.bankrobberies.utils.RobUtils;
import net.citizensnpcs.Citizens;
import net.citizensnpcs.api.CitizensAPI;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class Main extends JavaPlugin {

   @Getter
   static Main main;

   @Getter
   private PluginManager plMan;
   @Getter
   private BukkitScheduler bukkitScheduler;
   @Getter
   private CustomFile mainDataFile;
   @Getter
   private CustomFile robDataFile;
   @Getter
   private FileManager fileMan;
   @Getter
   private Economy econ;
   @Getter
   static Fawe WE;
   @Getter
   static WorldGuardPlugin WG;
   @Getter
   static Citizens citizens;

   public Main() {
      main = this;
      ConfigurationSerialization.registerClass(Robbery.class, "Robbery");
      ConfigurationSerialization.registerClass(RobItem.class, "RobItem");
   }

   @Override
   public void onDisable(){
      RobUtils.saveAllRobs();
   }

   @Override
   public void onEnable(){
      init();
   }


   private void init(){
      plMan = getServer().getPluginManager();
      bukkitScheduler = getServer().getScheduler();
      citizens = (Citizens) CitizensAPI.getPlugin();
      WE = Fawe.get();
      WG = WorldGuardPlugin.inst();
      if (!setupEconomy()) {
         System.out.println("Vault not found, some parts of this plugin will not work....");
      }
      loadFiles();
      initUtils();
      registerEvents();
   }

   private void loadFiles() {
      fileMan = new FileManager();
      mainDataFile = new CustomFile("config");
      robDataFile = new CustomFile("robs");
      fileMan.registerFile(mainDataFile);
      fileMan.registerFile(robDataFile);
   }

   public void initUtils() {
      RobUtils.init();
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

   private void registerEvents(){
      plMan.registerEvents(new NPCEvents(), this);
      plMan.registerEvents(new PlayerListener(), this);
      getCommand("rnpc").setExecutor(new RobCommands());
   }
}
