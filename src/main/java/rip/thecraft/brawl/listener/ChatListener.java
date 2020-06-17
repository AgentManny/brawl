package rip.thecraft.brawl.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.player.PlayerData;

public class ChatListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);

        event.setFormat(playerData.getLevel().getPrefix() + event.getFormat());
    }

}
