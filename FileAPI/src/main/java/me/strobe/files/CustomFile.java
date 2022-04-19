package me.strobe.files;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;


public class CustomFile {

   //local reference for readability
   @Getter private final JavaPlugin plugin;
   //File's temporary name
   @Getter private final String fileName;
   //Current configFile rep
   @Getter private final File customConfigFile;
   //Current Bukkit representation
   private FileConfiguration customConfig;

   private CustomFile(Builder builder){
      this.plugin = builder.plugin;
      this.fileName = builder.fileName;

      this.customConfigFile = new File(this.plugin.getDataFolder(), this.fileName + ".yml");
      this.plugin.saveResource(this.fileName + ".yml", false);
      this.customConfig = YamlConfiguration.loadConfiguration(this.customConfigFile);
   }

   public void saveCustomConfig() {
       try { this.getCustomConfig().save(this.customConfigFile); }
       catch(IOException ex) {
          plugin.getLogger().log(Level.SEVERE, "Could not save config to " + this.customConfigFile, ex);
       }
   }

   public FileConfiguration getCustomConfig() {
      if(this.customConfig == null) {
         this.reloadCustomConfig();
      }
      return this.customConfig;
   }

   public void reloadCustomConfig() {
      if(!this.customConfigFile.exists()) {
         try {
            this.customConfigFile.createNewFile();
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
      this.customConfig = YamlConfiguration.loadConfiguration(this.customConfigFile);
      InputStreamReader defConfigStream = new InputStreamReader(this.plugin.getResource(this.fileName + ".yml"));
      YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
      this.customConfig.setDefaults(defConfig);
   }
   
   public static final class Builder{
      private JavaPlugin plugin;
      private String fileName;

      public Builder(@NotNull JavaPlugin plugin, @NotNull String name){
         if(name.isEmpty()) throw new IllegalArgumentException("Name cannot be empty.");
         this.plugin = plugin;
         this.fileName = name;
      }

      public Builder plugin(JavaPlugin plugin){
         this.plugin = plugin;
         return this;
      }

      public Builder name(String name){
         this.fileName = name;
         return this;
      }

      public CustomFile build(){
         return new CustomFile(this);
      }
   }
}

