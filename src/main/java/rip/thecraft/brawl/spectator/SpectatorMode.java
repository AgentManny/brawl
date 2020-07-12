package rip.thecraft.brawl.spectator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.duelarena.DuelArena;
import rip.thecraft.brawl.duelarena.match.Match;
import rip.thecraft.brawl.game.Game;
import rip.thecraft.brawl.game.lobby.GameLobby;
import rip.thecraft.brawl.item.type.InventoryType;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.player.PlayerState;
import rip.thecraft.brawl.util.location.LocationType;
import rip.thecraft.spartan.nametag.NametagHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter @Setter
@RequiredArgsConstructor
public class SpectatorMode {

    private final UUID spectator;
    private final PlayerState lastState; // Used to track where they were last located

    private Location teleportTo; // Some modes such as games teleport you to your death location

    private boolean showSpectators = false; // If the the player wants to see other players

    private SpectatorType spectating = SpectatorType.NONE;

    private UUID follow; // If they are following a player

    private Match match; // If they are spectating a match
    private GameLobby lobby; // If they spectating a game beforehand.
    private Game game; // If they are spectating an event

    // Debug
    private List<UUID> hiddenPlayers = new ArrayList<>();

    public static SpectatorMode init(Player spectator) {
        return init(spectator, spectator, null);
    }

    public static SpectatorMode init(Player spectator, Player spectating, Location location) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(spectator);
        SpectatorMode mode = new SpectatorMode(spectator.getUniqueId(), playerData.getPlayerState());
        if (location != null) {
            mode.setTeleportTo(location);
        }
        mode.spectate(spectating);
        Brawl.getInstance().getItemHandler().apply(spectator, InventoryType.SPECTATOR);
        return mode;
    }


    public void spectate(Player spectating) {
        Player spectator = getPlayer();
        if (spectator == null) {
            leave();
            return;
        }

        cleanup();

        List<UUID> hiddenPlayers = new ArrayList<>();
        SpectatorType type = SpectatorType.NONE;
        Location location = Brawl.getInstance().getLocationByName("SPAWN");

        if (spectating != null) {
            Match match = Brawl.getInstance().getMatchHandler().containsPlayer(spectating, true);
            GameLobby lobby = Brawl.getInstance().getGameHandler().getLobby();
            Game game = Brawl.getInstance().getGameHandler().getActiveGame();

            if (match != null) {
                location = spectating.getLocation();

                this.match = match;
                match.getMatchData().getSpectators().add(this.spectator);

                type = SpectatorType.MATCH;
            } else if (lobby != null) {
                type = SpectatorType.GAME_LOBBY;
                location = Brawl.getInstance().getLocationByName("GAME_LOBBY");

                this.lobby = lobby;
            } else if (game != null) {
                type = SpectatorType.GAME;
                location = game.getDefaultLocation();
                this.game = game;

            } else {
                type = SpectatorType.PLAYER;
                location = spectator.getLocation();
            }
        }

        spectator.teleport(teleportTo == null ? location : teleportTo);
        this.teleportTo = null;

        this.spectating = type;

        // debug
        this.hiddenPlayers.addAll(hiddenPlayers);

        NametagHandler.reloadPlayer(spectator);
        NametagHandler.reloadOthersFor(spectator);
    }

    public void leave() {
        SpectatorManager specManager = Brawl.getInstance().getSpectatorManager();
        Player player = getPlayer();
        if (player != null) { // make sure they didn't disconnect
            PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);

            if (lastState == PlayerState.ARENA) {
                DuelArena.join(player);
            } else { // Don't care about other states we're just going to teleport them back to spawn
                playerData.spawn();
                player.teleport(LocationType.SPAWN.getLocation());
            }
        }

        specManager.spectators.remove(spectator);
    }

    private void cleanup() {
        this.match = null;
        this.lobby = null;
        this.game = null;
        this.follow = null;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(spectator);
    }

    public enum SpectatorType {

        MATCH,
        GAME,
        GAME_LOBBY,
        PLAYER,
        NONE

    }
}
