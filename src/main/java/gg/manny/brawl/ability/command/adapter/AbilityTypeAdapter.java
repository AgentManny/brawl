package gg.manny.brawl.ability.command.adapter;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.ability.Ability;
import gg.manny.quantum.command.adapter.CommandTypeAdapter;
import gg.manny.spigot.util.chatcolor.CC;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class AbilityTypeAdapter implements CommandTypeAdapter<Ability> {

    private final Brawl plugin;

    @Override
    public Ability transform(CommandSender sender, String source) {
        Ability ability = plugin.getAbilityHandler().getAbilityByName(source);
        if (ability == null) {
            sender.sendMessage(CC.RED + "Ability " + source + " not found.");
        }
        return ability;
    }

    @Override
    public List<String> tabComplete(Player sender, String source) {
        List<String> completions = new ArrayList<>();
        for (Ability ability : plugin.getAbilityHandler().getAbilities().values()) {
            if (StringUtils.startsWithIgnoreCase(ability.getName(), source)) {
                completions.add(ability.getName());
            }
        }
        return completions;
    }
}
