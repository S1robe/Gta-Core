package me.strobe.bankrobberies.Commands;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.Strobe.Core.Utils.StringUtils;
import me.Strobe.Core.Utils.User;
import me.strobe.bankrobberies.Main;
import me.strobe.bankrobberies.Robbery;
import me.strobe.bankrobberies.utils.Displays.GUIS;
import me.strobe.bankrobberies.utils.RobItem;
import me.strobe.bankrobberies.utils.RobUtils;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RobCommands implements CommandExecutor {
   @Override
   public boolean onCommand(CommandSender sender, Command command, String cmd, String[] args) {
      if(sender instanceof Player){
         Player p = (Player) sender;
         User u = User.getByPlayer(p);
         if(cmd.equalsIgnoreCase("rnpc")){
            if(args.length != 0) {
               NPC npc = Main.getCitizens().getNPCSelector().getSelected(sender);
               switch(args[0].toLowerCase()) {
                  // /rnpc create <name> (time) (time)
                  case "create":
                     if(args.length == 2) {
                        return create(u, npc, args[1], 0, 0, 0, 0);
                     }
                     else if(args.length == 3) {
                        try {
                           return create(u, npc, args[1], Integer.parseInt(args[2]), 0, 0, 0);
                        }
                        catch(NumberFormatException e) {
                           u.sendPlayerMessage(StringUtils.Text.INVALID_AMOUNT.create(args[2]));
                        }
                     }
                     else if(args.length == 4){
                        try {
                           int timeToRob = Integer.parseInt(args[2]);
                           try{
                              return create(u, npc, args[1], timeToRob, Integer.parseInt(args[3]), 0, 0);
                           }
                           catch(NumberFormatException e){
                              u.sendPlayerMessage(StringUtils.Text.INVALID_AMOUNT.create(args[3]));
                           }
                        }
                        catch(NumberFormatException e) {
                           u.sendPlayerMessage(StringUtils.Text.INVALID_AMOUNT.create(args[2]));
                        }
                     }
                     else if(args.length == 5){
                        try {
                           int timeToRob = Integer.parseInt(args[2]);
                           try{
                              int timeToReset = Integer.parseInt(args[3]);
                              try{
                                 return create(u, npc, args[1], timeToRob, timeToReset, Integer.parseInt(args[4]), 0);
                              }
                              catch(NumberFormatException e){
                                 u.sendPlayerMessage(StringUtils.Text.INVALID_AMOUNT.create(args[4]));
                              }
                           }
                           catch(NumberFormatException e){
                              u.sendPlayerMessage(StringUtils.Text.INVALID_AMOUNT.create(args[3]));
                           }
                        }
                        catch(NumberFormatException e) {
                           u.sendPlayerMessage(StringUtils.Text.INVALID_AMOUNT.create(args[2]));
                        }
                     }
                     else if(args.length == 6){
                        try {
                           int timeToRob = Integer.parseInt(args[2]);
                           try{
                              int timeToReset = Integer.parseInt(args[3]);
                              try{
                                 int minDrops = Integer.parseInt(args[4]);
                                 try{
                                    return create(u, npc, args[1], timeToRob, timeToReset, minDrops, Integer.parseInt(args[5]));
                                 }
                                 catch(NumberFormatException e){
                                    u.sendPlayerMessage(StringUtils.Text.INVALID_AMOUNT.create(args[5]));
                                 }
                              }
                              catch(NumberFormatException e){
                                 u.sendPlayerMessage(StringUtils.Text.INVALID_AMOUNT.create(args[4]));
                              }
                           }
                           catch(NumberFormatException e){
                              u.sendPlayerMessage(StringUtils.Text.INVALID_AMOUNT.create(args[3]));
                           }
                        }
                        catch(NumberFormatException e) {
                           u.sendPlayerMessage(StringUtils.Text.INVALID_AMOUNT.create(args[2]));
                        }
                     }
                     else {
                        u.sendPlayerMessage(RobUtils.Text.ROB_CREATE_USAGE.create());
                     }
                     return false;
                  case "delete":
                     return delete(u, npc);
                  case "reset":
                     return reset(u, npc);
                     // /rnpc addreward (cmd) min max weight
                  case "addreward":
                     if(args.length == 4){
                        try{
                           int min = Integer.parseInt(args[1]);
                           try{
                              int max = Integer.parseInt(args[2]);
                              try{
                                 double weight = Double.parseDouble(args[3]);
                                 return addReward(u, npc, null, min, max, weight);
                              }
                              catch(NumberFormatException e){
                                 u.sendPlayerMessage(StringUtils.Text.INVALID_AMOUNT.create(args[3]));
                              }
                           }
                           catch(NumberFormatException e){
                              u.sendPlayerMessage(StringUtils.Text.INVALID_AMOUNT.create(args[2]));
                           }
                        }
                        catch(NumberFormatException e){
                           u.sendPlayerMessage(StringUtils.Text.INVALID_AMOUNT.create(args[1]));
                        }
                     }
                     if(args.length == 5){
                        try{
                           int min = Integer.parseInt(args[2]);
                           try{
                              int max = Integer.parseInt(args[3]);
                              try{
                                 double weight = Double.parseDouble(args[4]);
                                 return addReward(u, npc, args[1], min, max, weight);
                              }
                              catch(NumberFormatException e){
                                 u.sendPlayerMessage(StringUtils.Text.INVALID_AMOUNT.create(args[4]));
                              }
                           }
                           catch(NumberFormatException e){
                              u.sendPlayerMessage(StringUtils.Text.INVALID_AMOUNT.create(args[3]));
                           }
                        }
                        catch(NumberFormatException e){
                           u.sendPlayerMessage(StringUtils.Text.INVALID_AMOUNT.create(args[2]));
                        }
                     }
                     return false;
                  case "addcmd":
                     return addCommandToRob(u, npc, args[1], Boolean.parseBoolean(args[2]),
                                                             Boolean.parseBoolean(args[3]),
                                                             Boolean.parseBoolean(args[4]),
                                                             Boolean.parseBoolean(args[5]));
                  case "view":
                     return view(u, args[1]);
                  case "setrobtime":
                     try {
                        return setTimeToRob(u, npc, Integer.parseInt(args[1]));
                     }
                     catch(NumberFormatException e){
                        u.sendPlayerMessage(StringUtils.Text.INVALID_AMOUNT.create(args[1]));
                     }
                  case "setresettime":
                     try {
                        return setTimeToReset(u, npc, Integer.parseInt(args[1]));
                     }
                     catch(NumberFormatException e){
                        u.sendPlayerMessage(StringUtils.Text.INVALID_AMOUNT.create(args[1]));
                     }
               }
            }
            u.sendPlayerMessage(RobUtils.Text.ROB_ADDCMD_USAGE.create());
            u.sendPlayerMessage(RobUtils.Text.ROB_ADDREWARD_USAGE.create());
            u.sendPlayerMessage(RobUtils.Text.ROB_DELETE_USAGE.create());
            u.sendPlayerMessage(RobUtils.Text.ROB_CREATE_USAGE.create());
         }

      }
      return false;
   }


   private boolean create(User u, NPC npc, String regionName, int timeToRob, int timetoReset, int minDrops, int maxDrops){
      Player p = u.getPLAYER().getPlayer();
      Region sel = Main.getWE().getCachedPlayer(u.getPlayerUUID()).getSelection();
      if(sel != null) {
         BlockVector point1 = sel.getMinimumPoint().toBlockPoint();
         BlockVector point2 = sel.getMaximumPoint().toBlockPoint();
         ProtectedRegion pr = new ProtectedCuboidRegion(regionName, point1, point2);
         if(!Main.getWG().getRegionManager(p.getWorld()).hasRegion(regionName)) {
            Main.getWG().getRegionManager(p.getWorld()).addRegion(pr);
            Robbery r = new Robbery(pr, npc, timeToRob, timetoReset, minDrops, maxDrops);
            RobUtils.getRobberies().put(r.getRegID(), r);
            RobUtils.saveRob(pr.getId());
            u.sendPlayerMessage(RobUtils.Text.ROB_CREATED.create(regionName));
            return true;
         }
         else
            u.sendPlayerMessage(RobUtils.Text.REGION_EXISTS.create(regionName));
      }
      else
         u.sendPlayerMessage(RobUtils.Text.NO_SELECTION.create());
      return false;
   }
   private boolean delete(User u, NPC npc){
      Robbery r = RobUtils.getRobberyByNPC(npc);
      if(r == null){
         u.sendPlayerMessage(RobUtils.Text.ROB_DELETE_USAGE.create(npc.getId()));
         return false;
      }
      else{
         RobUtils.deleteRob(r);
         u.sendPlayerMessage(RobUtils.Text.ROB_DELETED.create(r.getRegID()));
         return true;
      }
   }
   private boolean reset(User u, NPC npc){
      Robbery r = RobUtils.getRobberyByNPC(npc);
      if(r == null){
         u.sendPlayerMessage(RobUtils.Text.NOT_A_ROB.create(npc.getId()));
         return false;
      }
      else{
         RobUtils.resetRob(r);
         u.sendPlayerMessage(RobUtils.Text.ROB_RESET.create(r.getRegID()));
         return true;
      }
   }
   private boolean addCommandToRob(User u, NPC npc, String cmd, boolean onStart, boolean onSuccess, boolean onFail, boolean onEnd){
      Robbery r = RobUtils.getRobberyByNPC(npc);
      if(r == null){
         u.sendPlayerMessage(RobUtils.Text.NOT_A_ROB.create(npc.getId()));
         return false;
      }
      else{
         if(cmd != null)
            if(r.addCommand(cmd, onStart, onSuccess, onFail, onEnd)){
               u.sendPlayerMessage(RobUtils.Text.CMD_ADD_SUCCESS.create(cmd, r.getRegID()));
               return true;
            }
            else{
               u.sendPlayerMessage(RobUtils.Text.ROB_ADDCMD_USAGE.create());
            }
         return false;
      }
   }

   private boolean addReward(User u, NPC npc, String optionalCommand, int min, int max, double weight){
      Robbery r = RobUtils.getRobberyByNPC(npc);
      if(r == null){
         u.sendPlayerMessage(RobUtils.Text.NOT_A_ROB.create());
      }
      else{
         //TODO: dont forget to parse commands later on when you see this in the various times for robbing.
         RobItem x = new RobItem(u.getPLAYER().getPlayer().getItemInHand(), min, max, weight, optionalCommand);
         if(r.addReward(x)){
            u.sendPlayerMessage(RobUtils.Text.SUCCESS_ITEM_ADD.create(r.getRegID()));
            return true;
         }
         else
            u.sendPlayerMessage(RobUtils.Text.FAIL_ITEM_ADD.create());
      }
      return false;
   }

   private boolean setTimeToRob(User u, NPC npc, int time){
      Robbery r = RobUtils.getRobberyByNPC(npc);
      if(r == null){
         u.sendPlayerMessage(RobUtils.Text.NOT_A_ROB.create());
      }
      else {
         if(time > 0) {
            r.setTimeToRob(time);
            u.sendPlayerMessage(RobUtils.Text.ROBTIME_UPDATED.create(r.getRegID(), time));
            return true;
         }
         else
            u.sendPlayerMessage(StringUtils.Text.INVALID_AMOUNT.create());
      }
      return false;
   }

   private boolean setTimeToReset(User u, NPC npc, int time){
      Robbery r = RobUtils.getRobberyByNPC(npc);
      if(r == null){
         u.sendPlayerMessage(RobUtils.Text.NOT_A_ROB.create());
      }
      else {
         if(time > 0) {
            r.setTimeToReset(time);
            u.sendPlayerMessage(RobUtils.Text.RESETTIME_UPDATED.create(r.getRegID(), time));
            return true;
         }
         else
            u.sendPlayerMessage(StringUtils.Text.INVALID_AMOUNT.create());
      }
      return false;
   }

   private boolean view(User u, String regID){
      u.getPLAYER().getPlayer().closeInventory();
      GUIS.firstPage(u.getPLAYER().getPlayer().getPlayer(), regID);
      return true;
   }


}
