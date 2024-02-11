package me.iantapply.dungeons.developerkit.runnable;

import me.iantapply.dungeons.developerkit.GKBase;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class GKRunnable extends BukkitRunnable {

    static {
        plugin = GKBase.getPlugin();
    }

    private static final JavaPlugin plugin;

    public GKRunnable(Runnable task) {
        this.task = task;
    }

    public GKRunnable(Runnable task, Condition con) {
        this.task = task;
        this.con = con;
    }

    public GKRunnable(Runnable task, Condition con, Runnable taskAfter) {
        this.task = task;
        this.con = con;
        taskOnCancel = taskAfter;
    }

    public GKRunnable(Runnable task, long period) {
        this.task = task;
        this.period = period;

        cancelAfter(period);
    }

    public GKRunnable(Runnable task, long period, Runnable taskAfter) { //() ->
        this.task = task;
        this.period = period;
        taskOnCancel = taskAfter;

        cancelAfter(period);
    }

    public GKRunnable(Runnable task, long period, Runnable taskAfter, Condition con) { //() ->
        this.task = task;
        this.period = period;
        this.con = con;
        taskOnCancel = taskAfter;

        cancelAfter(period);
    }

    long period = -Integer.MAX_VALUE;

    Runnable task;
    Runnable taskOnCancel = null;

    Condition con;

    boolean isCancelled = false;

    @Override
    public void run() {
        task.run();
        if(con.compute()) {
            cancel();
        }
    }

    public GKRunnable cancelAfter(long period) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if(!isCancelled) {
                cancel();
            }
        }, period);
        return this;
    }

    @Override
    public void cancel() {
        isCancelled = true;
        super.cancel();
        if (taskOnCancel != null) {
            taskOnCancel.run();
        }
    }

    public GKRunnable cancelTask(Runnable taskAfter) {
         taskOnCancel = taskAfter;
         return this;
    }
}
