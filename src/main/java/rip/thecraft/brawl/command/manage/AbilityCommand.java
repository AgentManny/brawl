package rip.thecraft.brawl.command.manage;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.spartan.command.Command;

public class AbilityCommand {

    @Command(names = { "ability", "a" }, permission = "brawl.command.ability")
    public static void execute(Player sender, Ability ability) {
        sender.sendMessage(ChatColor.GOLD + "Manually activated " + ChatColor.WHITE + ability.getName() + ChatColor.GOLD + "...");
        ability.onActivate(sender);
    }

    @Command(names = { "ability cooldown", "a cooldown" }, permission = "brawl.command.ability")
    public static void execute(Player sender, Ability ability, int cooldown) {
        sender.sendMessage(ChatColor.GOLD + "Changed " + ChatColor.WHITE + ability.getName() + ChatColor.GOLD + " cooldown to " + cooldown + " seconds.");
        ability.setCooldown(cooldown);
        Brawl.getInstance().getAbilityHandler().save();
    }
}
