package gg.manny.brawl.ability.command.adapter;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.ability.Ability;
import gg.manny.quantum.command.adapter.CommandTypeAdapter;
import gg.manny.spigot.util.chatcolor.CC;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;

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
}
