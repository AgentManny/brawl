package rip.thecraft.brawl.command.adapters;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.thecraft.spartan.command.ParameterType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SoundCommandAdapter implements ParameterType<Sound> {

    @Override
    public Sound transform(CommandSender sender, String source) {
        Sound sound = null;
        try {
            sound = Sound.valueOf(source.toUpperCase().replace(" ", "_"));
        } catch (EnumConstantNotPresentException e) {
            sender.sendMessage(ChatColor.RED + "Sound " + sound + " not found.");
        }
        return sound;
    }

    @Override
    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        List<String> completions = new ArrayList<>();
        for (Sound value : Sound.values()) {
            if (StringUtils.startsWithIgnoreCase(value.name(), source)) {
                completions.add(value.name());
            }
        }
        return completions;
    }
}
