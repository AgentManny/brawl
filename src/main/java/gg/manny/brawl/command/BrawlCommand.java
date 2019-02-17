package gg.manny.brawl.command;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.Locale;
import gg.manny.quantum.command.Command;
import gg.manny.spigot.util.chatcolor.CC;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import java.io.IOException;

@RequiredArgsConstructor
public class BrawlCommand {

    private final Brawl plugin;

    @Command(names = "brawl")
    public void execute(Player sender) {
        sender.sendMessage(CC.GOLD + "Brawl " + CC.GRAY + "(Version: " + CC.WHITE + plugin.getDescription().getVersion() + CC.GRAY + ")");
        sender.sendMessage(CC.GOLD + "Created by " + CC.WHITE + String.join(", ", plugin.getDescription().getAuthors()));
    }

    @Command(names = "brawl reload", permission = "brawl.command.config")
    public void reload(CommandSender sender) {
        try {
            plugin.getMainConfig().getConfiguration().load(plugin.getMainConfig().getFile());
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            sender.sendMessage(Locale.COMMAND_RELOAD_ERROR.format());
        } finally {
            sender.sendMessage(Locale.COMMAND_RELOAD_SUCCESS.format());
        }
    }
}
