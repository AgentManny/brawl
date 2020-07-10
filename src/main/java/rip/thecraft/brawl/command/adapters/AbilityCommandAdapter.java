package rip.thecraft.brawl.command.adapters;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.spartan.command.ParameterType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AbilityCommandAdapter implements ParameterType<Ability> {

    @Override
    public Ability transform(CommandSender sender, String source) {
        Ability ability = Brawl.getInstance().getAbilityHandler().getAbilityByName(source);
        if (ability == null) {
            sender.sendMessage(ChatColor.RED + "Ability " + source + " not found.");
        }
        return ability;
    }

    @Override
    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        List<String> completions = new ArrayList<>();
        for (Ability ability : Brawl.getInstance().getAbilityHandler().getAbilities().values()) {
            if (StringUtils.startsWithIgnoreCase(ability.getName(), source)) {
                completions.add(ability.getName());
            }
        }
        return completions;
    }
}
