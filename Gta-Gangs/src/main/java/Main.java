import lombok.*;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.*;

public class Main extends JavaPlugin {

   public static final String PLUGINPREFIX = "&9&l[&c&nGTA-GANGS&9&l]&7: ";

   @Getter
   static Main main;
   @Getter
   static PluginLogger logger;
   @Getter
   static PluginManager plMan;

   @Override
   public void onDisable() {

   }

   @Override
   public void onEnable() {
      main = this;
      logger = new PluginLogger(main);
      plMan = this.getServer().getPluginManager();
   }
}
