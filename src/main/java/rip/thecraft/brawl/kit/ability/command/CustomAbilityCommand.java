package rip.thecraft.brawl.kit.ability.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.kit.ability.Ability;
import rip.thecraft.brawl.kit.ability.AbilityHandler;
import rip.thecraft.brawl.kit.ability.CustomAbility;
import rip.thecraft.brawl.kit.ability.menu.AbilityMenu;
import rip.thecraft.spartan.command.Command;

public class CustomAbilityCommand {

    @Command(names = "ability info", description = "Creates a custom ability", permission = "brawl.ability.manage")
    public static void abilities(Player player) {
        new AbilityMenu().openMenu(player);
    }

    @Command(names = "ability setweight", description = "Sets a weight for a custom ability", permission = "brawl.ability.manage")
    public static void setweight(CommandSender sender, CustomAbility ability, int weight) {
        ability.setWeight(weight);
        sender.sendMessage(ChatColor.GREEN + ability.getName() + " (child of " + ability.getParent().getName() + ") weight set to " + weight + ".");
    }

    @Command(names = "ability create", description = "Creates a custom ability", permission = "brawl.ability.manage")
    public static void createAbility(CommandSender sender, String name, Ability ability) {
        AbilityHandler abilityHandler = Brawl.getInstance().getAbilityHandler();
        if (abilityHandler.getAbilityByName(name) != null) {
            sender.sendMessage(ChatColor.RED + "Ability " + name + " already exists.");
            return;
        }

        if (abilityHandler.getCustomAbilityByName(name) != null) {
            sender.sendMessage(ChatColor.RED + "Custom ability " + name + " already exists.");
            return;
        }

        CustomAbility customAbility = new CustomAbility(name, ability);
        abilityHandler.registerCustomAbility(name, customAbility);
    }

}

