package me.strobe.configs;

import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

   private final Plugin registeringPlugin;
   private final Map<String, Config> fileNametoConfigMap;
   private final File pFolder;

   public ConfigManager(Plugin registeringPlugin){
      this.registeringPlugin = registeringPlugin;
      fileNametoConfigMap = new HashMap<>();
      pFolder = registeringPlugin.getDataFolder();
   }

   public Config createNewConfig(String name){
      File f = new File(pFolder, name + ".yml");
      Config c = new Config(registeringPlugin, f);
      fileNametoConfigMap.put(name, c);
      return c;
   }

   public Config loadConfig(String path, String name){
      File f = new File(path, name + ".yml");
      Config c = new Config(registeringPlugin, f);
      fileNametoConfigMap.put(name, c);
      return c;
   }

   public Config loadConfig(File path, String name){
      File f = new File(path, name+ ".yml");
      Config c = new Config(registeringPlugin, f);
      fileNametoConfigMap.put(name, c);
      return c;
   }

   public Config loadConfig(File file){
      Config c = new Config(registeringPlugin, file);
      fileNametoConfigMap.put(file.getName(), c);
      return c;
   }



   public void saveConfig(String name){
      fileNametoConfigMap.get(name).save();
   }

   public void reloadConfig(String name){
      fileNametoConfigMap.get(name).reload();
   }

   public void deleteConfig(String name){
      fileNametoConfigMap.remove(name).delete();
   }

}
