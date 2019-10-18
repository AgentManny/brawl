package gg.manny.brawl.listener;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.player.PlayerData;
import gg.manny.brawl.team.Team;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

@RequiredArgsConstructor
public class TeamListener implements Listener {

    private final Brawl plugin;

    @EventHandler(priority = EventPriority.NORMAL)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(player);

        Team team = plugin.getTeamHandler().getPlayerTeam(player);

        boolean doTeamChat = event.getMessage().startsWith("@");
        boolean doGlobalChat = event.getMessage().startsWith("!");


        if (doTeamChat || doGlobalChat) {
            event.setMessage(event.getMessage().substring(1));
        }

        if ((doTeamChat || playerData.isTeamChat()) && !(doGlobalChat || team == null)) {
            team.sendMessage(ChatColor.DARK_AQUA + "(Team) " + event.getPlayer().getName() + ": " + ChatColor.YELLOW + event.getMessage());
            plugin.getServer().getLogger().info("[Team Chat] [" + team.getName() + "] " + event.getPlayer().getName() + ": " + event.getMessage());
            event.setCancelled(true);
        }
    }

}
