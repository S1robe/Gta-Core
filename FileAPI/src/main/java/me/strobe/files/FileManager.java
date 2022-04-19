package me.strobe.files;

import org.bukkit.plugin.Plugin;

import java.util.HashMap;

public class FileManager {
   private final HashMap<Plugin, HashMap<String, CustomFile>> fileTable = new HashMap<>();

   public void registerFile(Plugin p, CustomFile customFile) {
      this.fileTable.putIfAbsent(p, new HashMap<>());
      this.fileTable.get(p).putIfAbsent(customFile.getFileName(), customFile);
   }

   public CustomFile getFile(Plugin p, String fileName) {
      return this.fileTable.get(p).get(fileName);
   }

   public boolean hasFile(Plugin p, String fileName) {
      return this.fileTable.get(p).containsKey(fileName);
   }

   public void saveAllFiles() {
      fileTable.values().forEach(map -> map.values().forEach(CustomFile::saveCustomConfig));
   }
}

