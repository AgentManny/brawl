package gg.manny.brawl.team.command.general;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.player.PlayerData;
import gg.manny.quantum.command.Command;
import gg.manny.server.util.chatcolor.CC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamChatCommand {

    @Command(names = { "team chat", "t chat", "faction chat", "f chat", "tean c", "t c", "faction c", "f c" })
    public void chat(Player sender) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(sender);
        playerData.setTeamChat(!playerData.isTeamChat());
        sender.sendMessage(ChatColor.YELLOW + "You are now talking in " + (playerData.isTeamChat() ? CC.DARK_AQUA + "Team" : CC.WHITE + "Public") + ChatColor.YELLOW + ".");
    }
}
