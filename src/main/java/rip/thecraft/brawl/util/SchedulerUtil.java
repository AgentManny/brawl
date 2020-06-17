package rip.thecraft.brawl.util;

import rip.thecraft.brawl.Brawl;

public class SchedulerUtil {

    public static void runTask(Runnable task, boolean async) {
        if (async) {
            Brawl.getInstance().getServer().getScheduler().runTaskAsynchronously(Brawl.getInstance(), task);
        } else {
            Brawl.getInstance().getServer().getScheduler().runTask(Brawl.getInstance(), task);
        }
    }

    public static void runTaskLater(Runnable task, long time, boolean async) {
        if (async) {
            Brawl.getInstance().getServer().getScheduler().runTaskLaterAsynchronously(Brawl.getInstance(), task, time);
        } else {
            Brawl.getInstance().getServer().getScheduler().runTaskLater(Brawl.getInstance(), task, time);
        }
    }

}
