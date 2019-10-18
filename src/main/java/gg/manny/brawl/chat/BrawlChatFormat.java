package gg.manny.brawl.chat;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.player.PlayerData;
import gg.manny.pivot.Pivot;
import gg.manny.pivot.chat.impl.DefaultChatFormat;
import org.bukkit.entity.Player;

public class BrawlChatFormat extends DefaultChatFormat {

    public BrawlChatFormat(Pivot plugin) {
        super(plugin);
    }

    @Override
    public String format(Player sender, Player receiver, String message) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(sender);
        return playerData.getLevel().getPrefix() + super.format(sender, receiver, message);
    }
}
