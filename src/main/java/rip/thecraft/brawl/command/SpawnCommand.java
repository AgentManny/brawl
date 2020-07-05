package rip.thecraft.brawl.command;

import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.duelarena.DuelArena;
import rip.thecraft.brawl.game.Game;
import rip.thecraft.brawl.game.GameElimination;
import rip.thecraft.brawl.game.GameHandler;
import rip.thecraft.brawl.game.team.GamePlayer;
import rip.thecraft.brawl.item.type.InventoryType;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.spectator.SpectatorManager;
import rip.thecraft.brawl.util.BrawlUtil;
import rip.thecraft.spartan.command.Command;
import rip.thecraft.spartan.nametag.NametagHandler;

@RequiredArgsConstructor
public class SpawnCommand {

    private final Brawl plugin;

    public static final String SPAWN_LOC = "SPAWN";
    public static final String ARENA_LOC = "DUEL_ARENA";

    @Command(names = "spawn")
    public void execute(Player sender) {
        Location spawn = plugin.getLocationByName(SPAWN_LOC);
        if(spawn == null) {
            sender.sendMessage(ChatColor.RED + "Uh oh! It looks like the Spawn hasn't been set. Please contact an administrator.");
            return;
        }

        PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(sender);
        if (playerData.isEvent()) {
            GameHandler gh = plugin.getGameHandler();
            SpectatorManager sm = plugin.getSpectatorManager();
            if (gh.getLobby() != null && gh.getLobby().getPlayers().contains(sender.getUniqueId())) {
                gh.getLobby().leave(sender.getUniqueId());
            }

            Game game = gh.getActiveGame();
            if (game != null) {
                if (game.containsPlayer(sender)) {
                    GamePlayer gamePlayer = game.getGamePlayer(sender);
                    if (gamePlayer.isAlive()) {
                        game.handleElimination(sender, sender.getLocation(), GameElimination.OTHER);
                    }
                }

                if (sm.inSpectator(sender)) {
                    sm.removeSpectator(sender.getUniqueId(), game, false);
                }
                NametagHandler.reloadPlayer(sender);
                NametagHandler.reloadOthersFor(sender);
            }
            return;
        }
        if (playerData.isDuelArena()) {
            DuelArena.leave(sender);
            return;
        }

        if (sender.getGameMode() != GameMode.CREATIVE && !sender.isOnGround()) {
            sender.sendMessage(ChatColor.RED + "You need to be on the ground to warp to spawn.");
            return;
        }


        playerData.warp("spawn", spawn, BrawlUtil.getNearbyPlayers(sender, 25).isEmpty() ? 3 : 5, () -> {
            playerData.setSpawnProtection(true);
            playerData.setDuelArena(false);
            if (playerData.getSelectedKit() == null) {
                if (!sender.hasMetadata("staffmode")) {
                    plugin.getItemHandler().apply(sender, InventoryType.SPAWN);
                    NametagHandler.reloadPlayer(sender);
                    NametagHandler.reloadOthersFor(sender);
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

}
