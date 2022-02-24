package me.strobe.gang;

import lombok.Getter;
import me.strobe.gang.Files.CustomFile;
import me.strobe.gang.Files.FileManager;
import me.strobe.gang.Utils.GangUtils;
import me.strobe.gang.Utils.MemberUtils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

   @Getter
   static Main main;
   @Getter private CustomFile memberFile;
   @Getter private CustomFile gangFile;
   @Getter private FileManager fileMan;
   @Getter private Economy econ;

   Main(){
      ConfigurationSerialization.registerClass(Gang.class);
      ConfigurationSerialization.registerClass(Member.class);
   }

   @Override
   public void onEnable(){
      init();
      registerEvents();
   }

   @Override
   public void onDisable(){
      GangUtils.saveGangs();
      MemberUtils.saveMembers();
   }

   private void init(){
      fileMan = new FileManager();
      memberFile = new CustomFile("members");
      gangFile = new CustomFile("gangs");
      fileMan.registerFile(memberFile);
      fileMan.registerFile(gangFile);
      initUtils();
      if (!setupEconomy()) {
         System.out.println("Vault not found, some parts of this plugin will not work....");
      }
   }

   private void initUtils(){
      MemberUtils.init();
      GangUtils.init();
   }

   private void registerEvents() {

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
