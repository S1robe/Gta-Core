package me.Strobe.Core.Utils.Displays;

import me.Strobe.Core.Utils.Title;
import me.Strobe.Core.Utils.User;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TrackerRunnable extends BukkitRunnable
{
    private final Player copPlayer;
    private final User cop;
    private User tracked;

    //mainrunnable
    public TrackerRunnable(User cop){
       this.cop = cop;
       this.copPlayer = cop.getPLAYER().getPlayer();
       this.tracked = cop.getTracked();
    }

    //stops the tracker
    private void stopTracker(){
        this.cop.setTrackerID(-1);
        this.tracked = null;
        this.copPlayer.setCompassTarget(new Location(this.copPlayer.getWorld(), 0, 0, 0));
        Title.sendTitle(copPlayer, 5, 5, 5, "", "&c&lPlayer Lost!");
        this.cancel();
    }

    //repeatedly called to update and set location.
    private void findTracked(){
        Location trackedLocation = tracked.getPLAYER().getPlayer().getLocation();
        this.copPlayer.setCompassTarget(trackedLocation);
        Title.sendTitle(copPlayer, 5, 5, 5, "", "&7Tracking: &6" + this.tracked.getPlayer_Name() + " - &7" + String.format("%.0f", this.copPlayer.getLocation().distance(trackedLocation)));
    }

    public void run(){
        if(cop == null){
            this.cancel();
            return;
        }
        this.tracked = cop.getTracked();
        if(tracked == null || tracked.getWantedlevel() == 0 || !cop.getPLAYER().getPlayer().getWorld().equals(tracked.getPLAYER().getPlayer().getWorld())){
            stopTracker();
            return;
        }
        findTracked();

    }
}
