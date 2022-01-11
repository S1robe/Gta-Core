package me.strobe.nohitdelay;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

//Fire damage, lava damage,

public class NoHitDelay extends JavaPlugin implements Listener {
   public void onDisable() {
      Bukkit.getConsoleSender().sendMessage("v1.0 by Strobe disabled");
   }

   public void onEnable() {
      Bukkit.getConsoleSender().sendMessage("v1.0 by Strobe enabled");
      this.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)this);
      final File f = new File(this.getDataFolder() + "/config.yml");
      if (!f.exists()) {
         this.saveResource("config.yml", true);
         this.saveConfig();
      }
      this.setup(false);
   }

   void setup(final boolean deepSearch) {
      if (deepSearch) {
         this.reloadConfig();
      }
      try {
         for (final Player p : Bukkit.getOnlinePlayers()) {
            p.setMaximumNoDamageTicks(Integer.parseInt(this.getConfig().getString("hit-delay")));
         }
      }
      catch (Exception e) {
         Bukkit.getConsoleSender().sendMessage("'hit-delay' has been reset, no vailed 'hit-delay' set.");
         this.getConfig().set("hit-delay", (Object)7);
         this.saveConfig();
         for (final Player p2 : Bukkit.getOnlinePlayers()) {
            p2.setMaximumNoDamageTicks(Integer.parseInt(this.getConfig().getString("hit-delay")));
         }
      }
   }

   public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
      if (command.getName().equalsIgnoreCase("nohitdelay")) {
         if (!sender.hasPermission("NoHitDelay.admin")) {
            sender.sendMessage("don't have permission for this command.");
            return true;
         }
         if (args.length >= 1) {
            if (args.length == 2 && args[0].equalsIgnoreCase("setdelay")) {
               try {
                  Integer.parseInt(args[1]);
               }
               catch (Exception e) {
                  sender.sendMessage(args[1] + " is not a vailed number.");
                  return false;
               }
               final int i = Integer.parseInt(args[1]);
               this.getConfig().set("hit-delay", (Object)i);
               this.saveConfig();
               sender.sendMessage("has been set to " + i);
               this.setup(true);
            }
            else if (args.length == 2 && args[0].equalsIgnoreCase("setJoinmessage")) {
               try {
                  Boolean.parseBoolean(args[1].toLowerCase());
               }
               catch (Exception e) {
                  sender.sendMessage(args[1] + " is not 'true' or 'false'");
                  return false;
               }
               final boolean b = Boolean.parseBoolean(args[1].toLowerCase());
               this.getConfig().set("join-msg", (Object)b);
               this.saveConfig();
               sender.sendMessage("has been set to " + b);
            }
            else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
               sender.sendMessage("reloaded");
               this.setup(true);
            }
            else if (args.length == 1 && args[0].equalsIgnoreCase("reloadConfig")) {
               sender.sendMessage("reloaded");
               this.setup(true);
            }
            else {
               sender.sendMessage("");
               sender.sendMessage("setDelay in ticks");
               sender.sendMessage("setJoinMessage ");
               sender.sendMessage("reloadConfig");
               sender.sendMessage("");
            }
         }
         else {
            sender.sendMessage("");
            sender.sendMessage("setDelay in ticks");
            sender.sendMessage("setJoinMessage ");
            sender.sendMessage("reloadConfig");
            sender.sendMessage("");
         }
      }
      return true;
   }

   @EventHandler
   public void onPlayerJoin(final PlayerJoinEvent e) {
      e.getPlayer().setMaximumNoDamageTicks(Integer.parseInt(this.getConfig().getString("hit-delay")));
      if (this.getConfig().getBoolean("join-msg")) {
         final Player p = e.getPlayer();
         this.getServer().getScheduler().scheduleSyncDelayedTask((Plugin)this, () -> p.getPlayer().sendMessage("server is running NoHitDelay v2.2 developed by Strobe"), 3L);
      }
   }

   @EventHandler
   public void onHit(final EntityDamageByEntityEvent e) {
      if (e.getEntity() instanceof Player) {
         final Player attacked = (Player)e.getEntity();
         if (!(e.getDamager() instanceof Projectile)) {
            attacked.setMaximumNoDamageTicks(20);
            return;
         }
         attacked.setMaximumNoDamageTicks(Integer.parseInt(this.getConfig().getString("hit-delay")));
      }
   }
}
