package gg.manny.brawl.command;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.Locale;
import gg.manny.quantum.command.Command;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class SpawnCommand {

    private final Brawl plugin;
    private final String spawnName = "SPAWN";

    @Command(names = "spawn")
    public void execute(CommandSender sender) {
        Location spawn = plugin.getLocationByName(this.spawnName);
        if(spawn == null) {
            sender.sendMessage(Locale.LOCATION_NOT_FOUND.format(this.spawnName));
            return;
        }

        ((Player)sender).teleport(spawn);

    }

    @Command(names = "setspawn", permission = "brawl.command.setspawn")
    public void setspawn(CommandSender sender) {
        sender.sendMessage(Locale.LOCATION_SET.format(this.spawnName));
        plugin.setLocationByName(this.spawnName, ((Player)sender).getLocation());
    }

    @Command(names = "setspawn", permission = "brawl.command.setspawn")
    public void setspawn(CommandSender sender, String spawnType) {
        sender.sendMessage(Locale.LOCATION_SET.format(spawnName));
        plugin.setLocationByName(spawnName, ((Player)sender).getLocation());
    }

}
