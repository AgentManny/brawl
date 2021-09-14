package rip.thecraft.brawl.command.manage;

import mkremins.fanciful.FancyMessage;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.ability.AbilityHandler;
import rip.thecraft.brawl.ability.property.AbilityProperty;
import rip.thecraft.brawl.ability.property.exception.PropertyParseException;
import rip.thecraft.spartan.command.Command;

public class AbilityCommand {

    private static final AbilityHandler ah = Brawl.getInstance().getAbilityHandler();

    @Command(names = { "ability" }, permission = "op", description = "Manage abilities")
    public static void manage(Player sender, Ability ability) {
        sender.sendMessage(ability.getColor() + ability.getName() + ChatColor.GOLD + " properties:");
        ability.getProperties().forEach((name, property) -> {
            new FancyMessage(ChatColor.GRAY + " - " + ChatColor.GOLD + WordUtils.capitalizeFully(name) + ": " + ChatColor.WHITE + property.toString())
                    .tooltip(ChatColor.GRAY + (property.getDescription() == null ? "No description provided" : property.getDescription()))
                    .suggest("/ability set " + ability.getName().toLowerCase() + " " + name + " " + property.toString())
                            .send(sender);
        });
        sender.sendMessage(ChatColor.GRAY + "Hover a property for more information");
        sender.sendMessage(ChatColor.RED + "Usage: /ability set " + ability.getName().toLowerCase() + " <property> <newValue>");
    }

    @Command(names = { "ability debug" }, permission = "op", description = "Enable debug ability")
    public static void debug(Player sender, Ability ability) {
        boolean newValue = !Ability.DEBUG;
        sender.sendMessage(ChatColor.GOLD + "Set ability debug mode: " + ChatColor.WHITE + newValue);
        Ability.DEBUG = newValue;
    }

    @Command(names = { "ability set" }, permission = "op", description = "Manage abilities")
    public static void set(Player sender, Ability ability, String property, String newValue) {
        String key = property.toLowerCase();
        if (ability.getProperties().containsKey(key)) {
            AbilityProperty<?> abilityProperty = ability.getProperties().get(key);
            try {
                Object oldValue = abilityProperty.value();
                sender.sendMessage(ability.getColor() + ability.getName() + ChatColor.GOLD + " set value of " + ChatColor.WHITE + WordUtils.capitalizeFully(property) + ChatColor.GOLD + ": " + ChatColor.RED + oldValue + ChatColor.WHITE + " --> " + ChatColor.GREEN + newValue);
                ability.getProperties().put(key, abilityProperty.parse(newValue));
            } catch (PropertyParseException e) {
                sender.sendMessage(ChatColor.RED + e.getMessage());
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Property " + property + " not found for " + ability.getName() + ".");
        }
    }



}
