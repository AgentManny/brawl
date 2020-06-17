package rip.thecraft.brawl.player.adapter;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.spartan.command.ParameterType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class PlayerDataTypeAdapter implements ParameterType<PlayerData> {

    private final Brawl plugin;

    @Override
    public PlayerData transform(CommandSender sender, String source) {
        if (sender instanceof Player && source.equalsIgnoreCase("self")) {
            return plugin.getPlayerDataHandler().getPlayerData((Player) sender);
        }
        PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(source);
        if (playerData == null) {
            sender.sendMessage(ChatColor.RED + "Player " + source + " not found.");
        }
        return playerData;
    }

    @Override
    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        List<String> completions = new ArrayList<>();
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (StringUtils.startsWithIgnoreCase(player.getName(), source) && sender.canSee(player)) {
                completions.add(player.getName());
            }
        }
        return completions;
    }
}
