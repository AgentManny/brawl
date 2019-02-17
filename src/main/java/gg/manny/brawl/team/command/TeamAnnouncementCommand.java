package gg.manny.brawl.team.command;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.Locale;
import gg.manny.brawl.team.Team;
import gg.manny.quantum.command.Command;
import gg.manny.spigot.util.chatcolor.CC;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class TeamAnnouncementCommand {

    private final Brawl brawl;

    @Command(names = {
            "team announcement", "t announcement", "faction announcement", "f announcement",
            "team description", "t description", "faction description", "f description",
            "team announce", "t announce", "faction announce", "f announce",
            "team desc", "t desc", "faction desc", "f desc"
    })
    public void announcement(Player sender, String message) {
        Team playerTeam = brawl.getTeamHandler().getTeamByUuid(sender.getUniqueId());
        if (playerTeam == null) {
            sender.sendMessage(Locale.TEAM_ERROR_PLAYER_NOT_FOUND.format());
            return;
        }

        playerTeam.setAnnouncement(message.equals("clear") ? null : message.replaceAll("(&([a-f0-9l-or]))", "\u00A7$2"));
        playerTeam.broadcast(Locale.TEAM_ANNOUNCEMENT.format(playerTeam.getAnnouncement() == null ? CC.RED + "None" : playerTeam.getAnnouncement()));

    }
}
