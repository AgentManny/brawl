package gg.manny.brawl.player.adapter;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.player.PlayerData;
import gg.manny.quantum.command.adapter.CommandTypeAdapter;
import gg.manny.server.util.chatcolor.CC;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class PlayerDataTypeAdapter implements CommandTypeAdapter<PlayerData> {

    private final Brawl plugin;

    @Override
    public PlayerData transform(CommandSender sender, String source) {
        if (sender instanceof Player && source.equalsIgnoreCase("self")) {
            return plugin.getPlayerDataHandler().getPlayerData((Player) sender);
        }
        PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(source);
        if (playerData == null) {
            sender.sendMessage(CC.RED + "Player " + source + " not found.");
        }
        return playerData;
    }

    @Override
    public List<String> tabComplete(Player sender, String source) {
        List<String> completions = new ArrayList<>();
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (StringUtils.startsWithIgnoreCase(player.getName(), source) && sender.canSee(player)) {
                completions.add(player.getName());
            }
        }
        return completions;
    }
}
