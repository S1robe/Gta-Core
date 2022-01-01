package me.Strobe.Core.Utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

//starts a runnable timer to delay teleportation
public class DelayedTeleport extends BukkitRunnable {
    public static final String TELEPORT_ACTION = "GangDelayedTeleport";
    private final Player player;
    private final int x;
    private final int z;
    private final double health;
    private final Location to;
    private int counter;
    private final Plugin plugin;

    private DelayedTeleport(Plugin plugin, int seconds, @NotNull Player p, Location to) {
        this.player = p;
        this.x = p.getLocation().getBlockX();
        this.z = p.getLocation().getBlockZ();
        this.health = p.getHealth();
        this.to = to;
        this.plugin = plugin;
        this.counter = seconds;
    }

    //responbile for setting up the timer
    @Override
    public void run() {
        if(player == null) {this.cancel(); return;}
        if (Math.abs(player.getLocation().getBlockX() - this.x) < 2 || Math.abs(player.getLocation().getBlockZ() - this.z) < 2) {
            if(player.getHealth() >= this.health){
                --this.counter;
                if (this.counter == 0) {
                    StringUtils.sendMessage(player, "&6&lPoof!");
                    player.removeMetadata(TELEPORT_ACTION, this.plugin);
                    player.teleport(this.to);
                    this.cancel();
                }
            }
            else{
                StringUtils.sendMessage(player, "&c&l(!)&7You took damage, Teleport Canceled!");
                player.removeMetadata(TELEPORT_ACTION, this.plugin);
                player.removeMetadata("Successful Teleport",this.plugin);
                this.cancel();
            }
        }
        else {
            StringUtils.sendMessage(player, "&c&l(!)&7You moved, Teleport Canceled!");
            player.removeMetadata(TELEPORT_ACTION, this.plugin);
            player.removeMetadata("Successful Teleport",this.plugin);
            this.cancel();
        }
    }

    //runs the teleportation
    public static void doDelayedTeleport(Plugin plugin, Player p, Location to, int timeout) {
        StringUtils.sendMessage(p, StringUtils.Text.TELEPORTING.create( ""+timeout));
        DelayedTeleport dt = new DelayedTeleport(plugin, timeout, p, to);
        p.setMetadata(TELEPORT_ACTION, new FixedMetadataValue(plugin, dt));
        p.setMetadata("Successful Teleport", new FixedMetadataValue(plugin, dt));
        dt.runTaskTimer(plugin, 20L, 20L);
    }
}
