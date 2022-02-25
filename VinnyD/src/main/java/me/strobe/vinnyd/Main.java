package me.strobe.vinnyd;

import com.shampaggon.crackshot.CSDirector;
import com.shampaggon.crackshot.CSUtility;
import lombok.Getter;
import me.strobe.vinnyd.Events.VinnyEvents;
import me.strobe.vinnyd.Files.CustomFile;
import me.strobe.vinnyd.Files.FileManager;
import me.strobe.vinnyd.Utils.VinnyUtils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This is Main.java in Project: (Gta-Core) : But you already knew that
 * @author G.P of Prentice Productions
 * @version 1.0
 * Created On    : 2/24/2022 4:07 PM
 * Last Edit     : 2/24/2022 4:07 PM(Update Me!)
 * Time to Write : (Rough Estimate)
 *
 * (Class Description)
*/
public class Main extends JavaPlugin {

   @Getter private static Main main;
   @Getter private Economy econ;
   @Getter private CSUtility csUtil;

   @Getter private final FileManager fileMan;

   @Getter private final CustomFile config;
   @Getter private final CustomFile stock;
   @Getter private final CustomFile upgrades;

   public Main(){
      main = this;
      fileMan = new FileManager();
      config = new CustomFile("config");
      stock = new CustomFile("stock");
      upgrades = new CustomFile("upgrades");
   }

   @Override
   public void onEnable(){
      if(!setupEconomy()) {
         getServer().getPluginManager().disablePlugin(this);
         System.out.println("VinnyD was disabled due to no Vault.jar in plugins folder");
      }
      init();

      if(VinnyUtils.doesVinnySpawnToday())
         VinnyUtils.spawnVinny(null);
   }

   @Override
   public void onDisable(){
      VinnyUtils.saveState();
   }

   private void init(){
      fileMan.registerFile(config);
      fileMan.registerFile(stock);
      fileMan.registerFile(upgrades);
      VinnyUtils.init(hookCrackshot());
      registerEvents();
   }

   private void registerEvents(){
      getServer().getPluginManager().registerEvents(new VinnyEvents(), this);
   }

   private boolean setupEconomy() {
      RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
      if(rsp == null) {
         return false;
      }
      econ = rsp.getProvider();
      return econ != null;
   }

   private boolean hookCrackshot(){
      CSDirector s = (CSDirector) getServer().getPluginManager().getPlugin("CrackShot");
      if(s == null) {
         System.out.println("VinnyD did not find any weapons, he will not offer upgrades.");
         return false;
      }
      csUtil = new CSUtility();
      return true;
   }
}


