package gg.manny.brawl.player.simple.adapter;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.player.simple.SimpleOfflinePlayer;
import gg.manny.quantum.command.adapter.CommandTypeAdapter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class SimpleOfflinePlayerAdapter implements CommandTypeAdapter<SimpleOfflinePlayer> {

    @Override
    public SimpleOfflinePlayer transform(CommandSender sender, String source) {
        if (sender instanceof Player && (source.equalsIgnoreCase("self") || source.equals(""))) {
            return SimpleOfflinePlayer.getByUuid(((Player) sender).getUniqueId());
        }

        SimpleOfflinePlayer offlinePlayer = SimpleOfflinePlayer.getByName(source);
        if (offlinePlayer == null) {
            sender.sendMessage(ChatColor.RED + source + ChatColor.RED + " has not played before.");
        }
        return offlinePlayer;
    }

    @Override
    public List<String> tabComplete(Player sender, String source) {
        List<String> completions = new ArrayList<>();
        for (Player player : Brawl.getInstance().getServer().getOnlinePlayers()) {
            if (StringUtils.startsWithIgnoreCase(player.getName(), source) && sender.canSee(player)) {
                completions.add(player.getName());
            }
        }
        return completions;
    }
}
