package gg.manny.brawl.command;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.duelarena.DuelArena;
import gg.manny.brawl.game.Game;
import gg.manny.brawl.game.GameHandler;
import gg.manny.brawl.game.team.GamePlayer;
import gg.manny.brawl.item.type.InventoryType;
import gg.manny.brawl.player.PlayerData;
import gg.manny.quantum.command.Command;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class SpawnCommand {

    private final Brawl plugin;
    private final String spawnName = "SPAWN";

    @Command(names = "spawn")
    public void execute(Player sender) {
        Location spawn = plugin.getLocationByName(this.spawnName);
        if(spawn == null) {
            sender.sendMessage(ChatColor.RED + "Location " + spawnName + " not found.");
            return;
        }

        PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(sender);
        if (playerData.isEvent()) {
            GameHandler gh = plugin.getGameHandler();
            if (gh.getLobby() != null && gh.getLobby().getPlayers().contains(sender.getUniqueId())) {
                gh.getLobby().leave(sender.getUniqueId());
            } else if (gh.getActiveGame() != null && gh.getActiveGame().containsPlayer(sender)) {
                Game game = gh.getActiveGame();
                GamePlayer gamePlayer = game.getGamePlayer(sender);
                if (gamePlayer.isAlive()) {
                    sender.sendMessage(ChatColor.RED + "Are you sure you want to leave? You'll get eliminated.");
                }
            }
            return;
        }
        if (playerData.isDuelArena()) {
            DuelArena.leave(sender);
            return;
        }

        playerData.warp("spawn", spawn, 10, () -> {
            playerData.setSpawnProtection(true);
            playerData.setDuelArena(false);
            if (playerData.getSelectedKit() == null) {
                if (!sender.hasMetadata("staffmode")) {
                    plugin.getItemHandler().apply(sender, InventoryType.SPAWN);
                }
            } else {
                sender.sendMessage(ChatColor.YELLOW + "Clear your kit by using " + ChatColor.LIGHT_PURPLE + "/clearkit" + ChatColor.YELLOW + ".");
            }
        });
    }

    @Command(names = { "1v1", "1vs1", "duelarena"})
    public void duelArena(Player sender) {
        DuelArena.join(sender);
    }

    @Command(names = "setspawn", permission = "brawl.command.setspawn")
    public void setspawn(CommandSender sender) {
        this.setspawn(sender, this.spawnName);
    }

    @Command(names = "setspawn", permission = "brawl.command.setspawn")
    public void setspawn(CommandSender sender, String spawnType) {
        sender.sendMessage(ChatColor.YELLOW + "Set location for " + ChatColor.LIGHT_PURPLE + spawnType + ChatColor.YELLOW + ".");
        plugin.setLocationByName(spawnType, ((Player)sender).getLocation());
    }

}
