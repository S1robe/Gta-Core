package me.Strobe.Housing.Events;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.SneakyThrows;
import me.Strobe.Core.Utils.RegionUtils;
import me.Strobe.Core.Utils.StringUtils;
import me.Strobe.Housing.House;
import me.Strobe.Housing.Main;
import me.Strobe.Housing.Utils.HouseUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerEvents implements Listener {

   @EventHandler
   public void onJoin(PlayerJoinEvent e) {
      Player p = e.getPlayer();
      House h = HouseUtils.getHouseByPlayer(p);
      if(h != null) {
         if(h.getDaysRemaining() <= 1) {
            StringUtils.sendMessage(p, me.Strobe.Housing.Utils.StringUtils.houseExpiringSoon);
         }
      }
   }

   @EventHandler @SneakyThrows
   public void onSignPlace(SignChangeEvent e){
      if(e.getLine(0).contains("FOR RENT")) {
         Player p = e.getPlayer();
         if(!p.hasPermission("houses.admin")) return;
         Region sel = Main.getFAWEPlugin().getCachedPlayer(p.getUniqueId()).getSelection();
         if(sel != null) {
            BlockVector point1 = sel.getMinimumPoint().toBlockPoint();
            BlockVector point2 = sel.getMaximumPoint().toBlockPoint();
            ProtectedRegion pr = new ProtectedCuboidRegion(e.getLine(1), point1, point2);
            if(!Main.getWG().getRegionManager(p.getWorld()).hasRegion(e.getLine(1))) {
               try {
                  Main.getWG().getRegionManager(p.getWorld()).addRegion(pr);
                  pr.setFlag(DefaultFlag.INVINCIBILITY, StateFlag.State.ALLOW);
                  pr.setFlag(DefaultFlag.MOB_SPAWNING, StateFlag.State.DENY);
                  pr.setFlag(DefaultFlag.PVP, StateFlag.State.DENY);
                  Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "rg flag " + pr.getId() + " -w " + p.getWorld().getName() + " chest-access -g NON_MEMBERS deny");
//                  Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "rg flag " + pr.getId() + " -w " + p.getWorld().getName() + " invincible allow");
//                  Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "rg flag " + pr.getId() + " -w " + p.getWorld().getName() + " pvp deny");
//                  Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "rg flag " + pr.getId() + " -w " + p.getWorld().getName() + " mob-spawning deny");
                  House h = new House(e.getBlock().getLocation(), Integer.parseInt(e.getLine(3)), Double.parseDouble(e.getLine(2)), pr, HouseUtils.findChests(p.getWorld(), pr));
                  e.setLine(0, "FOR RENT");
                  e.setLine(1, "&r" + h.getName());
                  e.setLine(2, "&r" + h.getStartDays() + " Days");
                  e.setLine(3, "&r$" + h.getPrice());
                  HouseUtils.addNewHouse(h);
                  me.Strobe.Core.Utils.StringUtils.sendMessage(p, me.Strobe.Housing.Utils.StringUtils.cmdCreateHouseSuccess.replace("{loc}", RegionUtils.locationSerializer(h.getSignLocation())));
                  Main.getWG().getRegionManager(p.getWorld()).saveChanges();
               }
               catch(NumberFormatException n) {
                  me.Strobe.Core.Utils.StringUtils.sendMessage(p, me.Strobe.Housing.Utils.StringUtils.cmdCreateHouseFailPriceOrDaysInvalid);
               }
            }
            else {
               me.Strobe.Core.Utils.StringUtils.sendMessage(p, me.Strobe.Housing.Utils.StringUtils.cmdCreateHouseFailRegionAlreadyExists);
            }
         }
         else {
            me.Strobe.Core.Utils.StringUtils.sendMessage(p, me.Strobe.Housing.Utils.StringUtils.cmdCreateHouseFailNoSelection);
         }
      }
   }
}
