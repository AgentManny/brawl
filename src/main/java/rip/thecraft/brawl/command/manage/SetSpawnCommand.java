package rip.thecraft.brawl.command.manage;

import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.command.SpawnCommand;
import rip.thecraft.spartan.command.Command;

@RequiredArgsConstructor
public class SetSpawnCommand {

    private final Brawl plugin;

    @Command(names = "setspawn", permission = "brawl.command.setspawn")
    public void setspawn(Player player) {
        this.setspawn(player, SpawnCommand.SPAWN_LOC);
    }

    @Command(names = "setspawn", permission = "brawl.command.setspawn")
    public void setspawn(Player player, String spawnType) {
        if (!(spawnType.equalsIgnoreCase(SpawnCommand.ARENA_LOC) || spawnType.equalsIgnoreCase(SpawnCommand.SPAWN_LOC))) {
            player.sendMessage(ChatColor.RED + "Warning! Spawn location " + spawnType + " doesn't exist.");
            player.sendMessage(ChatColor.RED + "Valid (spawn) locations: [DUEL_ARENA, SPAWN]");
        }

        Location loc = player.getLocation();

        plugin.setLocationByName(spawnType, loc);
        player.sendMessage(ChatColor.GREEN + "Set location of " + ChatColor.WHITE + spawnType + ChatColor.GREEN + " to " + ChatColor.WHITE + "(" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")" + ChatColor.GREEN + ".");
    }

}
