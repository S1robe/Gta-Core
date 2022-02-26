package me.strobe.gang;

import lombok.Getter;
import me.strobe.gang.Commands.GangCommands;
import me.strobe.gang.Events.PlayerEvents;
import me.strobe.gang.Files.CustomFile;
import me.strobe.gang.Files.FileManager;
import me.strobe.gang.Utils.GangUtils;
import me.strobe.gang.Utils.MemberUtils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This is Main.java in Project: (Gta-Core) : But you already knew that
 * @author G.P of Prentice Productions
 * @version 1.0
 * Created On    : 2/24/2022 4:02 PM
 * Last Edit     : 2/24/2022 4:02 PM(Update Me!)
 * Time to Write : (Rough Estimate)
 *
 * (Class Description)
*/
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
      getServer().getPluginManager().registerEvents(new PlayerEvents(), this);
      getCommand("gang").setExecutor(new GangCommands());
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
