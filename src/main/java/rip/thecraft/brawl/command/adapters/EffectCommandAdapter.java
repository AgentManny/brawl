package rip.thecraft.brawl.command.adapters;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.thecraft.spartan.command.ParameterType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EffectCommandAdapter implements ParameterType<Effect> {

    @Override
    public Effect transform(CommandSender sender, String source) {
        Effect effect = null;
        try {
            effect = Effect.valueOf(source.toUpperCase().replace(" ", "_"));
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Effect " + effect + " not found.");
            List<String> matching = new ArrayList<>();
            for (Effect value : Effect.values()) {
                if (value.name().contains(source)) {
                    matching.add(WordUtils.capitalizeFully(value.name().toLowerCase().replace("_", " ")));
                }
            }

            if (!matching.isEmpty()) {
                sender.sendMessage(ChatColor.RED + "Matching effects (" + matching.size() + "): " + ChatColor.YELLOW + StringUtils.join(matching.toArray(), ", "));
            }
        }
        return effect;
    }

    @Override
    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        List<String> completions = new ArrayList<>();
        for (Effect value : Effect.values()) {
            if (StringUtils.startsWithIgnoreCase(value.name(), source)) {
                completions.add(value.name());
            }
        }
        return completions;
    }
}
