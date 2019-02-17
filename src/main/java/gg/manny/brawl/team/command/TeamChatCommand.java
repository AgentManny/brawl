package gg.manny.brawl.team.command;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.Locale;
import gg.manny.brawl.player.PlayerData;
import gg.manny.quantum.command.Command;
import gg.manny.spigot.util.chatcolor.CC;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class TeamChatCommand {

    private final Brawl brawl;

    @Command(names = { "team chat", "t chat", "faction chat", "f chat", "team c", "t c", "faction c", "f c" })
    public void chat(Player sender) {
        PlayerData playerData = brawl.getPlayerDataHandler().getPlayerData(sender);
        playerData.setTeamChat(!playerData.isTeamChat());
        sender.sendMessage(Locale.TEAM_CHAT_SWITCHED.format(playerData.isTeamChat() ? CC.DARK_AQUA + "Team" : CC.WHITE + "Public"));

    }
}
