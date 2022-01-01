package me.strobe.bankrobberies.Events;

import me.Strobe.Core.Utils.User;
import me.strobe.bankrobberies.Main;
import me.strobe.bankrobberies.Robbery;
import me.strobe.bankrobberies.utils.RobUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

   @EventHandler
   public void onQuit(PlayerQuitEvent e){
      Player p = e.getPlayer();
      Robbery r = RobUtils.getRobberyByRobber(p);
      if(r != null) {
         User.sendAllUsersMessage(RobUtils.Text.ROB_CANCELED_LOGGED_SHOUT.create(r.getRegID()));
         Bukkit.getScheduler().cancelTask(r.getStartrunID());
         RobUtils.resetRob(r);
         p.removeMetadata("ROBBING", Main.getMain());
      }
   }

   @EventHandler
   public void onDeath(PlayerDeathEvent e){
      Player p = e.getEntity();
      Robbery r = RobUtils.getRobberyByRobber(p);
      if(r != null) {
         User.sendAllUsersMessage(RobUtils.Text.ROB_CANCELED_DIED_SHOUT.create(r.getRegID()));
         Bukkit.getScheduler().cancelTask(r.getStartrunID());
         RobUtils.resetRob(r);
         p.removeMetadata("ROBBING", Main.getMain());
      }
   }

   @EventHandler
   public void onCommandSend(PlayerCommandPreprocessEvent e){
      Player p = e.getPlayer();
      if(p.hasMetadata("ROBBING") && RobUtils.getBlacklistedCommands().contains(e.getMessage().split(" ")[0].toLowerCase().replace("/", ""))){
         e.setCancelled(true);
         User.getByPlayer(p).sendPlayerMessage(RobUtils.Text.COMMAND_DENY_ROBBING.create());
      }
   }

   @EventHandler
   public void onInvClick(InventoryClickEvent e){
      if(e.getInventory().getTitle().contains("Rewards"))
         e.setCancelled(true);
   }


}
