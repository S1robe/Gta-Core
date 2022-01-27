package me.strobe.vinnyd;

import com.shampaggon.crackshot.CSDirector;
import com.shampaggon.crackshot.CSUtility;
import lombok.Getter;
import me.strobe.vinnyd.Files.CustomFile;
import me.strobe.vinnyd.Files.FileManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

   @Getter private static Main main;
   @Getter private Economy econ;
   @Getter private CSUtility csUtil;

   @Getter private FileManager fileMan;

   @Getter private CustomFile config;
   @Getter private CustomFile stock;
   @Getter private CustomFile upgrades;

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
      if(!hookCrackshot()) {
         System.out.println("VinnyD did not find any weapons, he will not offer upgrades.");
      }
      init();
   }

   @Override
   public void onDisable(){

   }

   private void init(){
      fileMan.registerFile(config);
      fileMan.registerFile(stock);
      fileMan.registerFile(upgrades);
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
      if(s == null)
         return false;
      csUtil = new CSUtility();
      return true;
   }



}


