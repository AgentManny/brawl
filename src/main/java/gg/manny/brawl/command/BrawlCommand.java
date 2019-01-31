package gg.manny.brawl.command;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.Locale;
import gg.manny.quantum.command.Command;
import gg.manny.spigot.util.chatcolor.CC;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.IOException;

@RequiredArgsConstructor
public class BrawlCommand {

    private final Brawl plugin;

    @Command(names = "brawl")
    public void execute(CommandSender sender) {
        sender.sendMessage(CC.GREEN + "Brawl v" + plugin.getDescription().getVersion() + " by Mannys.");
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
