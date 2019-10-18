package gg.manny.brawl.ability.command;

import gg.manny.brawl.ability.Ability;
import gg.manny.quantum.command.Command;
import gg.manny.server.util.chatcolor.CC;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public class AbilityCommand {

    @Command(names = { "ability", "a" }, permission = "brawl.command.ability")
    public void execute(Player sender, Ability ability) {
        sender.sendMessage(CC.GOLD + "Manually activated " + ChatColor.WHITE + ability.getName() + ChatColor.GOLD + "...");
        ability.onActivate(sender);
    }
}
