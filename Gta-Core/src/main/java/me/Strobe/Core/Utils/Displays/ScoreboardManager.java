package me.Strobe.Core.Utils.Displays;

import me.Strobe.Core.Utils.User;
import me.Strobe.Core.Utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;

public class ScoreboardManager {

    private static final HashMap<User, Scoreboard> playerScoreboards = new HashMap<>();

    public static void updateScoreboard(){
       playerScoreboards.forEach((u, b) -> {
          int board = u.getCurrentBoard();
          if(board == 0)
             allBoardText(u, b);
          else if(board == 1)
             PVPBoardText(u, b);
          else
             PVEBoardText(u, b);
       });
    }

    public static void createScoreBoard(User user){
        int board = user.getCurrentBoard();
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        scoreboard.registerNewObjective("ALL", "dummy");
        scoreboard.registerNewObjective("PVP", "dummy");
        scoreboard.registerNewObjective("PVE", "dummy");
        if(board == 0)
           allBoardText(user, scoreboard);
        else if(board == 1)
           PVPBoardText(user, scoreboard);
        else
           PVEBoardText(user, scoreboard);
       playerScoreboards.put(user, scoreboard);
       user.setScoreBoard(scoreboard);
       User.addUser(user);
    }

    public static Objective allBoardText(User u, Scoreboard b) {
        Objective obj = b.getObjective("ALL");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.setDisplayName(StringUtils.color("&bGTA-MC.net"));
        obj.getScore("§eBalance").setScore((int) u.getBalance());
        obj.getScore("§aKills").setScore(u.getPvpKills());
        obj.getScore("§cDeaths").setScore(u.getPvpDeaths());
        obj.getScore("§dKillstreak").setScore(u.getKillstreak());
       if( u.getPvpDeaths() == 0)
          obj.getScore("§dKDR").setScore((u.getPvpKills()));
       else
          obj.getScore("§dKDR").setScore((u.getPvpKills() / u.getPvpDeaths()));
        obj.getScore("§4Wanted Level").setScore(u.getWantedlevel());
        obj.getScore("§2Mob-Kills").setScore(u.getMobKills());
        obj.getScore("§2Mob-Deaths").setScore(u.getMobDeaths());
        obj.getScore("§aVillager-Kills").setScore(u.getVillagerKills());
        obj.getScore("§7Skeleton-Kills").setScore(u.getSkeletonKills());
        obj.getScore("§8WitherSkeleton-Kills").setScore(u.getWitherSkeletonKills());
        obj.getScore("§aZombie-Kills").setScore(u.getZombieNormalKills());
        obj.getScore("§dEnderman-Kills").setScore(u.getEndermanKills());
        obj.getScore("§bPigCop-Kills").setScore(u.getNpcCopKills());
        obj.getScore("§bCop-Kills").setScore(u.getCopKills());
        return obj;
    }

    public static Objective PVPBoardText(User u, Scoreboard b) {
        Objective obj = b.getObjective("PVP");

        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.setDisplayName(StringUtils.color("&bGTA-MC.net"));
        obj.getScore("§eBalance").setScore((int) u.getBalance());
        obj.getScore("§aKills").setScore(u.getPvpKills());
        obj.getScore("§cDeaths").setScore(u.getPvpDeaths());
        obj.getScore("§dKillstreak").setScore(u.getKillstreak());
        if( u.getPvpDeaths() == 0)
           obj.getScore("§dKDR").setScore((u.getPvpKills()));
        else
           obj.getScore("§dKDR").setScore((u.getPvpKills() / u.getPvpDeaths()));
        obj.getScore("§4Wanted Level").setScore(u.getWantedlevel());
        obj.getScore("§bCop-Kills").setScore(u.getCopKills());
       return obj;
    }

    public static Objective PVEBoardText(User u, Scoreboard b) {
        Objective obj = b.getObjective("PVE");

        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.setDisplayName(StringUtils.color("&bGTA-MC.net"));
        obj.getScore("§eBalance").setScore((int) u.getBalance());
        obj.getScore("§2Mobkills").setScore(u.getMobKills());
        obj.getScore("§2Mob-Deaths").setScore(u.getMobDeaths());
        obj.getScore("§aVillager-Kills").setScore(u.getVillagerKills());
        obj.getScore("§7Skeleton-Kills").setScore(u.getSkeletonKills());
        obj.getScore("§8Witherskeleton-Kills").setScore(u.getWitherSkeletonKills());
        obj.getScore("§aZombie-Kills").setScore(u.getZombieNormalKills());
        obj.getScore("§dEnderman-Kills").setScore(u.getEndermanKills());
        obj.getScore("§bPigCop-Kills").setScore(u.getNpcCopKills());
       return obj;
    }

     public static Scoreboard getBoardAndRemoveByUser(User u){
       return playerScoreboards.remove(u);
     }

}
