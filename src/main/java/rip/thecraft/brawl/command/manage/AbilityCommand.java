package rip.thecraft.brawl.command.manage;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.ability.AbilityHandler;
import rip.thecraft.spartan.command.Command;

public class AbilityCommand {

    private static final AbilityHandler ah = Brawl.getInstance().getAbilityHandler();

//    @Command(names = { "ability", "a" }, permission = "brawl.command.ability")
//    public static void execute(Player sender, Ability ability) {
//        sender.sendMessage(ChatColor.GOLD + "Manually activated " + ChatColor.WHITE + ability.getName() + ChatColor.GOLD + "...");
//        ability.onActivate(sender);
//    }
//
//    @Command(names = { "ability cooldown", "a cooldown" }, permission = "brawl.command.ability")
//    public static void execute(Player sender, Ability ability, int cooldown) {
//        sender.sendMessage(ChatColor.GOLD + "Changed " + ChatColor.WHITE + ability.getName() + ChatColor.GOLD + " cooldown to " + cooldown + " seconds.");
//        ability.setCooldown(cooldown);
//        Brawl.getInstance().getAbilityHandler().save();
//    }
//
//    @Command(names = { "a vampire power" }, permission = "op", description = "Sets the multiplier for Bat Booster")
//    public static void setVampirePower(Player sender, @Param(defaultValue = "0.4") double power) {
//        BatBlaster batBlaster = ah.getAbilityByClass(BatBlaster.class);
//        batBlaster.power = power;
//        sender.sendMessage("Set power to " + power);
//    }

    @Command(names = { "ability" }, permission = "op", description = "Manage abilities")
    public static void manage(Player sender, Ability ability) {
        sender.sendMessage(ability.getColor() + ability.getName() + ChatColor.YELLOW + " info:");
        ability.getProperties().forEach((name, property) -> {
            sender.sendMessage(ChatColor.YELLOW + " - " + WordUtils.capitalizeFully(name) + ": " + ChatColor.WHITE + property.toString());
        });
        sender.sendMessage(ChatColor.RED + "Usage: /ability set " + ability.getName() + " <property> <newValue>");
    }

    @Command(names = { "ability set" }, permission = "op", description = "Manage abilities")
    public static void set(Player sender, Ability ability, String property, String newValue) {
        sender.sendMessage(ChatColor.RED + "This isn't supported yet.");
    }



}
