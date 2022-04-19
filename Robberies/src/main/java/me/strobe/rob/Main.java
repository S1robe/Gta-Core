package me.strobe.rob;

import com.boydti.fawe.Fawe;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import lombok.Getter;
import net.citizensnpcs.Citizens;
import net.citizensnpcs.api.CitizensAPI;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    //need npc, vault. core?
    @Getter private static Main instance;
    @Getter private static Citizens citInst;
    @Getter private static Fawe faweInst;
    @Getter private static Economy econInst;
    @Getter private static boolean isEconPresent;
    @Getter private static WorldGuardPlugin wgInst;

    public Main(){
        instance = this;
        citInst = (Citizens) CitizensAPI.getPlugin();
        if(citInst == null) this.getServer().getPluginManager().disablePlugin(this);

        faweInst = Fawe.get();
        if(faweInst == null) this.getServer().getPluginManager().disablePlugin(this);
        RegisteredServiceProvider<Economy> rsp = this.getServer().getServicesManager().getRegistration(Economy.class);
        econInst = rsp.getProvider();
        isEconPresent = econInst == null;
    }

    @Override
    public void onEnable(){

    }

    public void onDisable(){

    }
}
