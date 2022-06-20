package me.strobe.files;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main main;
    private static FileManager fileMan;

    public Main(){
        main = this;
        fileMan = new FileManager();
    }

    @Override
    public void onEnable(){

    }

    @Override
    public void onDisable(){

    }
}
