package rip.thecraft.brawl.spectator;

import com.mongodb.lang.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.duelarena.DuelArena;
import rip.thecraft.brawl.duelarena.loadout.MatchLoadout;
import rip.thecraft.brawl.duelarena.match.Match;
import rip.thecraft.brawl.duelarena.match.queue.QueueType;
import rip.thecraft.brawl.game.Game;
import rip.thecraft.brawl.game.GameType;
import rip.thecraft.brawl.game.lobby.GameLobby;
import rip.thecraft.brawl.item.type.InventoryType;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.player.PlayerState;
import rip.thecraft.brawl.util.VisibilityUtils;
import rip.thecraft.brawl.util.location.LocationType;
import rip.thecraft.brawl.warp.Warp;
import rip.thecraft.spartan.nametag.NametagHandler;

import java.util.UUID;

@Getter @Setter
@RequiredArgsConstructor
public class SpectatorMode {

    private final UUID spectator;
    private final PlayerState lastState; // Used to track where they were last located

    private Location teleportTo; // Some modes such as games teleport you to your death location

    private boolean showSpectators = false; // If the the player wants to see other players

    private SpectatorType spectating = SpectatorType.NONE;

    private Player spectatedPlayer;
    private UUID follow; // If they are following a player

    private Warp warp; // If they are spectating a warp

    private Match match; // If they are spectating a match
    private GameLobby lobby; // If they spectating a game beforehand.
    private Game game; // If they are spectating an event

    protected static SpectatorMode init(Player spectator) {
        return init(spectator, spectator, null);
    }

    protected static SpectatorMode init(Player spectator, @Nullable Player spectating, Location location) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(spectator);
        boolean wasDuelArena = playerData.isDuelArena();

        SpectatorMode mode = new SpectatorMode(spectator.getUniqueId(), playerData.getPlayerState());
        if (location != null) {
            mode.setTeleportTo(location);
        } else {
            mode.setTeleportTo((wasDuelArena ? LocationType.ARENA : LocationType.SPAWN).getLocation());
        }

        playerData.setSpawnProtection(false);
        playerData.setDuelArena(false);
        if (playerData.getSelectedKit() != null) {
            playerData.setPreviousKit(playerData.getSelectedKit());
            playerData.setSelectedKit(null);
        }

        mode.spectate(spectating);
        Brawl.getInstance().getItemHandler().apply(spectator, InventoryType.SPECTATOR);
        mode.setSpectating(wasDuelArena ? SpectatorType.DUEL_ARENA : SpectatorType.SPAWN);
        spectator.setAllowFlight(true);
        spectator.setFlying(true);
        return mode;
    }

    @Deprecated
    public void spectateGame() {
        Player spectator = getPlayer();
        GameLobby lobby = Brawl.getInstance().getGameHandler().getLobby();
        Game game = Brawl.getInstance().getGameHandler().getActiveGame();
        if (lobby == null && game == null) {
            spectator.sendMessage(ChatColor.RED + "There isn't any games to spectate.");
            return;
        }

        Location location = lobby != null ? LocationType.GAME_LOBBY.getLocation() : game.getDefaultLocation();
        GameType gameType = lobby != null ? lobby.getGameType() : game.getType();
        spectating = lobby != null ? SpectatorType.GAME_LOBBY : SpectatorType.GAME;

        this.lobby = lobby;
        this.game = game;

        spectator.teleport(location);
        spectator.sendMessage(ChatColor.GREEN + "You are now spectating: " + ChatColor.WHITE + gameType.getShortName() + (lobby != null ? " (Lobby)" : ""));
    }

    public int getMaxRadius() {
        return spectating.radius;
    }

    public void spectate(Player spectating) {
        spectate(spectating, SpectatorType.SPAWN);
    }

    public void spectate(Warp warp) {
        Player player = getPlayer();
        if (player == null) {
            leave();
            return;
        }

        cleanup();
        teleportTo = warp.getLocation();
        spectating = SpectatorType.WARP;

        teleport();
        player.sendMessage(ChatColor.GREEN + "You are now spectating: " + ChatColor.WHITE + "Warp (" + warp.getName() + ")");
    }

    public void spectate() {
        spectate(lastState == PlayerState.ARENA ? SpectatorType.DUEL_ARENA : SpectatorType.SPAWN);
    }

    public void spectate(SpectatorType spectatorType) {
        spectate(spectatorType, false);
    }

    public void spectate(SpectatorType spectatorType, boolean sendMessage) {
        Player player = getPlayer();
        if (player == null) {
            leave();
            return;
        }

        cleanup();

        Location location;
        String message = spectatorType.getName();
        switch (spectatorType) {
            case NONE:
            case SPAWN: {
                location = LocationType.SPAWN.getLocation();
                break;
            }
            case DUEL_ARENA: {
                location = LocationType.ARENA.getLocation();
                break;
            }
            case GAME_LOBBY:
            case GAME: {
                GameLobby lobby = Brawl.getInstance().getGameHandler().getLobby();
                Game game = Brawl.getInstance().getGameHandler().getActiveGame();
                if (lobby != null) {
                    this.lobby = lobby;
                    spectatorType = SpectatorType.GAME_LOBBY;
                    location = LocationType.GAME_LOBBY.getLocation();
                    message = "Game Lobby (" + lobby.getGameType().getShortName() + ")";
                } else if (game != null) {
                    this.game = game;
                    spectatorType = SpectatorType.GAME;
                    game.getSpectators().add(spectator);
                    location = game.getDefaultLocation();
                    message = game.getType().getShortName();
                } else {
                    player.sendMessage(ChatColor.RED + "There isn't any games to spectate.");
                    return;
                }
                break;
            }
            default: {
                player.sendMessage(ChatColor.RED + "You can't spectate a " + ChatColor.YELLOW + spectatorType.name.toLowerCase() + ChatColor.RED + " as it requires a parameter.");
                return;
            }
        }

        if (location != null) {
            teleportTo = location;
            spectating = spectatorType;

            teleport();
            if (sendMessage) {
                player.sendMessage(ChatColor.GREEN + "You are now spectating: " + ChatColor.WHITE + message);
            }
        }
    }

    public void teleport() {
        getPlayer().teleport(teleportTo);
    }

    public void spectate(Player spectating, SpectatorType type) {
        Player spectator = getPlayer();
        if (spectator == null) {
            leave();
            return;
        }

        if (spectator == spectating) {
            spectator.sendMessage(ChatColor.RED + "You can't spectate yourself!");
            return;
        }

        cleanup();

        Location location = LocationType.SPAWN.getLocation();
        String message = null;

        if (spectating != null) {
            Match match = Brawl.getInstance().getMatchHandler().containsPlayer(spectating, true);
            GameLobby lobby = Brawl.getInstance().getGameHandler().getLobby();
            Game game = Brawl.getInstance().getGameHandler().getActiveGame();

            if (match != null) {
                location = spectating.getLocation();

                this.match = match;
                match.getMatchData().getSpectators().add(this.spectator);

                Player[] players = match.getPlayers();

                Player playerOne = players[0];
                PlayerData playerOneData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(playerOne);

                Player playerTwo = players[1];
                PlayerData playerTwoData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(playerTwo);

                QueueType queue = match.getQueueType();
                MatchLoadout loadout = match.getLoadout();

                message =
                        playerOne.getName() + (queue == QueueType.RANKED ? " (" + playerOneData.getStatistic().get(loadout) + ")" : "") +
                                " vs. " +
                                playerTwo.getName() + (queue == QueueType.RANKED ? " (" + playerTwoData.getStatistic().getArenaStatistics().get(loadout) + ")" : "");

                type = SpectatorType.MATCH;
            } else if (lobby != null) {
                type = SpectatorType.GAME_LOBBY;
                location = Brawl.getInstance().getLocationByName("GAME_LOBBY");
                message = "Game Lobby (" + lobby.getGameType().getShortName() + ")";

                this.lobby = lobby;
            } else if (game != null) {
                type = SpectatorType.GAME;
                location = game.getDefaultLocation();
                message = game.getType().getShortName();
                this.game = game;
            } else {
                type = SpectatorType.PLAYER;
                spectatedPlayer = spectating;
                location = spectating.getLocation();
                message = spectating.getName();
            }
        }

        if (message != null) {
            spectator.sendMessage(ChatColor.GREEN + "You are now spectating: " + ChatColor.WHITE + message);
        }
        spectator.teleport(teleportTo == null ? location : teleportTo);
        this.spectating = type;

        NametagHandler.reloadPlayer(spectator);
        NametagHandler.reloadOthersFor(spectator);
    }

    public void leave() {
        SpectatorManager specManager = Brawl.getInstance().getSpectatorManager();
        specManager.spectators.remove(spectator);
        if (match != null) {
            match.getMatchData().getSpectators().remove(spectator);
        }
        if (game != null) {
            game.getSpectators().remove(spectator);
        }
        Player player = getPlayer();
        if (player != null) { // make sure they didn't disconnect
            player.setAllowFlight(false);
            player.setFlying(false);
            PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
            playerData.getLevel().updateExp(player);
            playerData.setSpawnProtection(true);
            if (lastState == PlayerState.ARENA) {
                DuelArena.join(player);
            } else { // Don't care about other states we're just going to teleport them back to spawn
                playerData.spawn();
                player.teleport(LocationType.SPAWN.getLocation());
            }
            VisibilityUtils.updateVisibility(player);

            NametagHandler.reloadPlayer(player);
            NametagHandler.reloadOthersFor(player);
        }
    }

    private void cleanup() {
        if (match != null) {
            match.getMatchData().getSpectators().remove(spectator);
            match = null;
        }
        if (game != null) {
            game.getSpectators().remove(spectator);
            game = null;
        }
        this.warp = null;
        this.lobby = null;
        this.follow = null;
        this.spectatedPlayer = null;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(spectator);
    }

    @Getter
    @AllArgsConstructor
    public enum SpectatorType {

        SPAWN("Warzone", 200),
        WARP("Warp", 100),

        DUEL_ARENA("Duel Arena", 100),
        GAME_LOBBY("Game Lobby", 50),

        MATCH("Match", 100),
        GAME("Game", 150),
        PLAYER("Player", 150),
        NONE("None", -1);

        private String name;
        private int radius;
    }
}
