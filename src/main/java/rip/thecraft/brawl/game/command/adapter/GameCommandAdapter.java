package rip.thecraft.brawl.game.command.adapter;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.game.GameType;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.command.ParameterType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GameCommandAdapter implements ParameterType<GameType> {

    @Override
    public GameType transform(CommandSender sender, String source) {
        GameType gameType = null;
        try {
            gameType = GameType.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException ignored) {
            sender.sendMessage(CC.RED + "Game " + source + " not found.");
        }
        return gameType;
    }

    @Override
    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        List<String> completions = new ArrayList<>();
        for (GameType gameType : GameType.values()) {
            if (StringUtils.startsWithIgnoreCase(gameType.getName(), source)) {
                completions.add(gameType.getName());
            }
        }
        return completions;
    }
}
