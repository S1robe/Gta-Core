package me.Strobe.Housing;

import com.boydti.fawe.Fawe;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import lombok.Getter;
import me.Strobe.Housing.Commands.HouseCommands;
import me.Strobe.Housing.Events.HouseEvents;
import me.Strobe.Housing.Events.PlayerEvents;
import me.Strobe.Housing.Utils.Files.CustomFile;
import me.Strobe.Housing.Utils.Files.FileManager;
import me.Strobe.Housing.Utils.HouseUtils;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public final class Main extends JavaPlugin {

   @Getter
   static Main main;

   @Getter
   static me.Strobe.Core.Main gtaMain;
   @Getter
   static WorldGuardPlugin WG;
   @Getter
   static Fawe FAWEPlugin;
   @Getter
   private PluginManager plMan;
   @Getter
   private BukkitScheduler bukkitScheduler;
   @Getter
   private CustomFile housesFile;
   @Getter
   private CustomFile mainDataFile;
   @Getter
   private FileManager fileMan;


   public Main() {
      main = this;
      ConfigurationSerialization.registerClass(House.class, "House");
   }

   @Override
   public void onDisable() {
      HouseUtils.saveAllHouses();
      plMan.disablePlugin(this);
   }

   @Override
   public void onEnable() {
      WG = WorldGuardPlugin.inst();
      FAWEPlugin = Fawe.get();
      plMan = getServer().getPluginManager();
      bukkitScheduler = getServer().getScheduler();
      loadFiles();
      initUtils();
      HouseUtils.loadAllHouses();
      registerEvents();
      startRunnables();
   }

   private void loadFiles() {
      fileMan = new FileManager();
      mainDataFile = new CustomFile("config");
      housesFile = new CustomFile("houses");
      fileMan.registerFile(mainDataFile);
      fileMan.registerFile(housesFile);
   }

   private void initUtils() {
      HouseUtils.maxNumDays = (int) Main.getMain().getMainDataFile().getCustomConfig().get("max-days-occupiable");
      HouseUtils.loadAllHouses();
   }

   private void registerEvents() {
      plMan.registerEvents(new HouseEvents(), this);
      plMan.registerEvents(new PlayerEvents(), this);
      getCommand("house").setExecutor(new HouseCommands());
   }

   private void startRunnables() {
      HouseUtils.updateHousesRunnable();
   }

   //TODO implement aucitoning of the house items (LATER)
}
