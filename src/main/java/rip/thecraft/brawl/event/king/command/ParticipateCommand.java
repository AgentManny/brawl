package rip.thecraft.brawl.event.king.command;

import rip.thecraft.brawl.Brawl;
import rip.thecraft.spartan.command.Command;
import org.bukkit.entity.Player;

public class ParticipateCommand  {

    private final Brawl plugin = Brawl.getInstance();

    @Command(names = "participate", description = "Execute this command and you will join the current KillTheKing game.")
    public void execute(Player player) {

    }
}
