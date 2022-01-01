package me.strobe.bankrobberies.Events;

import me.Strobe.Core.Utils.StringUtils;
import me.Strobe.Core.Utils.User;
import me.strobe.bankrobberies.Main;
import me.strobe.bankrobberies.Robbery;
import me.strobe.bankrobberies.utils.Displays.GUIS;
import me.strobe.bankrobberies.utils.RobUtils;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;

public class NPCEvents implements Listener {

   @EventHandler
   public void onNPCClick(NPCRightClickEvent e){
      Player p = e.getClicker();
      User u = User.getByPlayer(p);
      NPC npc = e.getNPC();
      Robbery r = RobUtils.getRobberyByNPC(npc);
      if(r == null) return;
      if(u.isCop()) {
         u.sendPlayerMessage(StringUtils.Text.DENY_ACTION.create());
         return;
      }
      if(!r.isBeingRobbed() && !p.isSneaking()) {
         p.setMetadata("ROBBING", new FixedMetadataValue(Main.getMain(), true));
         u.sendPlayerMessage(RobUtils.Text.ROB_STARTED.create(r.getRegID()));
         User.sendAllUsersMessage(RobUtils.Text.ROB_STARTED_SHOUT.create(r.getRegID(), r.getLocation().getBlockX(), r.getLocation().getBlockZ(), r.getTimeToRob() / 60));
         r.start(p);
      }
      else if(p.isSneaking() && p.hasMetadata("ROBBING")){
         Bukkit.getScheduler().cancelTask(r.getStartrunID());
         RobUtils.resetRob(r);
         RobUtils.getRobbing().remove(r);
         u.sendPlayerMessage(RobUtils.Text.ROB_CANCELED.create());
         p.removeMetadata("ROBBING", Main.getMain());
      }
      else if(r.isBeingRobbed()){
         u.sendPlayerMessage(RobUtils.Text.ALREADY_ROBBING.create());
      }
   }

   @EventHandler
   public void onNPCLeftClick(NPCLeftClickEvent e){
      Player p = e.getClicker();
      NPC npc = e.getNPC();
      Robbery r = RobUtils.getRobberyByNPC(npc);
      if(r == null) return;
      GUIS.firstPage(p, r.getRegID());
   }


}
