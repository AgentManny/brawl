package gg.manny.brawl.event.king.command;

import gg.manny.brawl.Brawl;
import gg.manny.quantum.command.Command;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

public class ParticipateCommand  {

    private final Brawl plugin = Brawl.getInstance();

    @Command(names = "participate", description = "Execute this command and you will join the current KillTheKing game.")
    public void execute(Player player) {

    }
}
