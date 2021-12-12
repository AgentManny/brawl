package rip.thecraft.brawl.command.adapters;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.CustomAbility;
import rip.thecraft.spartan.command.ParameterType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CustomAbilityCommandAdapter implements ParameterType<CustomAbility> {

    @Override
    public CustomAbility transform(CommandSender sender, String source) {
        CustomAbility ability = Brawl.getInstance().getAbilityHandler().getCustomAbilityByName(source);
        if (ability == null) {
            sender.sendMessage(ChatColor.RED + "Ability variant " + source + " not found.");
        }
        return ability;
    }

    @Override
    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        List<String> completions = new ArrayList<>();
        for (CustomAbility ability : Brawl.getInstance().getAbilityHandler().getCustomAbilities().values()) {
            if (StringUtils.startsWithIgnoreCase(ability.getName().replace(" ", ""), source)) {
                completions.add(ability.getName());
            }
        }
        return completions;
    }
}
