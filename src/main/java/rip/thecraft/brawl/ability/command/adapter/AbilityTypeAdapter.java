package rip.thecraft.brawl.ability.command.adapter;

import lombok.RequiredArgsConstructor;
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

@RequiredArgsConstructor
public class AbilityTypeAdapter implements ParameterType<Ability> {

    private final Brawl plugin;

    @Override
    public Ability transform(CommandSender sender, String source) {
        Ability ability = plugin.getAbilityHandler().getAbilityByName(source);
        if (ability == null) {
            sender.sendMessage(ChatColor.RED + "Ability " + source + " not found.");
        }
        return ability;
    }

    @Override
    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        List<String> completions = new ArrayList<>();
        for (Ability ability : plugin.getAbilityHandler().getAbilities().values()) {
            if (StringUtils.startsWithIgnoreCase(ability.getName(), source)) {
                completions.add(ability.getName());
            }
        }
        return completions;
    }
}
