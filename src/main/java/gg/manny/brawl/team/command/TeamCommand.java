package gg.manny.brawl.team.command;

import com.google.common.base.Strings;
import gg.manny.brawl.Brawl;
import gg.manny.brawl.Locale;
import gg.manny.quantum.command.Command;
import gg.manny.spigot.util.chatcolor.CC;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;

@RequiredArgsConstructor
public class TeamCommand {

    private final Brawl brawl;

    @Command(names = { "team", "t", "faction", "f", "team help", "t help", "faction help", "f help" })
    public void execute(CommandSender sender) {
        Locale.TEAM_HELP.formatList(CC.GRAY + CC.STRIKETHROUGH + Strings.repeat("-", 51)).forEach(sender::sendMessage);
    }

}
