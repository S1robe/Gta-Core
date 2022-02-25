package me.strobe.vinnyd.Files;


import java.util.HashMap;

/**
 * This is FileManager.java in Project: (Gta-Core) : But you already knew that
 * @author G.P of Prentice Productions
 * @version 1.0
 * Created On    : 2/24/2022 4:08 PM
 * Last Edit     : 2/24/2022 4:08 PM(Update Me!)
 * Time to Write : (Rough Estimate)
 *
 * (Class Description)
*/
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

