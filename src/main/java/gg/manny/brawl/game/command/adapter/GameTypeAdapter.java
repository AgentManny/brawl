package gg.manny.brawl.game.command.adapter;

import gg.manny.brawl.game.GameType;
import gg.manny.quantum.command.adapter.CommandTypeAdapter;
import gg.manny.spigot.util.chatcolor.CC;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class GameTypeAdapter implements CommandTypeAdapter<GameType> {

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
    public List<String> tabComplete(Player sender, String source) {
        List<String> completions = new ArrayList<>();
        for (GameType gameType : GameType.values()) {
            if (StringUtils.startsWithIgnoreCase(gameType.getName(), source)) {
                completions.add(gameType.getName());
            }
        }
        return completions;
    }
}
