package me.strobe.vinnyd.Files;


import java.util.HashMap;

public class FileManager {
   private final HashMap<String, CustomFile> fileMap = new HashMap<>();

   public void registerFile(CustomFile customFile) {
      this.fileMap.putIfAbsent(customFile.getFileName(), customFile);
   }

   public CustomFile getFile(String fileName) {
      if(!this.hasFile(fileName)) {
         return null;
      }
      return this.fileMap.get(fileName);
   }

   public boolean hasFile(String fileName) {
      return this.fileMap.containsKey(fileName);
   }

   public void saveAllFiles() {
      for(CustomFile file : fileMap.values()) {
         file.saveCustomConfig();
      }
   }
}

