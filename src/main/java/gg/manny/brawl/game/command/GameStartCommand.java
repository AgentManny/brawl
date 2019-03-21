package gg.manny.brawl.game.command;

import gg.manny.brawl.game.GameType;
import gg.manny.quantum.command.Command;
import org.bukkit.entity.Player;

public class GameStartCommand {

    @Command(names = "game start", permission = "op")
    public void execute(Player sender, GameType type) {
        sender.sendMessage("Starting " + type.getName() + " (" + type.getShortName() + ")");
    }

}
