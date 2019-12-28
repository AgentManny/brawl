package gg.manny.brawl.event.king.runnables;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.event.king.KillTheKing;
import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class WaitingCountdownRunnable extends BukkitRunnable {

    private final KillTheKing game;
    private final Brawl brawl = Brawl.getInstance();

    private int secondsLeft = 60;

    @Override
    public void run() {
        switch (secondsLeft) {
            case 30:
                brawl.getServer().broadcastMessage("");
                break;
            case 10:
                break;
            case 5:
                break;
        }

        // Reduce 1 second every time this runnable is ran.
        secondsLeft -= 1;
    }

    @Override
    public void cancel() {
        game.runSetup();
        super.cancel();
    }
}
