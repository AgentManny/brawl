package gg.manny.brawl.ability.command;

import gg.manny.brawl.ability.Ability;
import gg.manny.quantum.command.Command;
import gg.manny.spigot.util.chatcolor.CC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AbilityCommand {

    @Command(names = { "ability", "a" }, permission = "brawl.command.ability")
    public void execute(CommandSender sender, Ability ability) {
        sender.sendMessage(CC.YELLOW + "Activating " + ability.getName() + "...");
        ability.onActivate(((Player)sender));
    }
}
