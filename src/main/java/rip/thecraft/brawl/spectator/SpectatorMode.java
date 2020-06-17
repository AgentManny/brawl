package rip.thecraft.brawl.spectator;

import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.duelarena.match.Match;
import rip.thecraft.brawl.game.Game;
import rip.thecraft.brawl.game.lobby.GameLobby;
import rip.thecraft.brawl.item.type.InventoryType;
import rip.thecraft.spartan.nametag.NametagHandler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter @Setter
@RequiredArgsConstructor
public class SpectatorMode {

    private final UUID spectator;

    private SpectatorType spectating = SpectatorType.NONE;

    private UUID follow; // If they are following a player

    private Match match; // If they are spectating a match
    private Game game; // If they are spectating an event

    public static SpectatorMode init(Player spectator, Player spectating) {
        SpectatorMode mode = new SpectatorMode(spectator.getUniqueId());
        SpectatorType type = SpectatorType.NONE;
        Location location = Brawl.getInstance().getLocationByName("SPAWN");

        if (spectating != null) {
            Match match = Brawl.getInstance().getMatchHandler().containsPlayer(spectating, true);
            GameLobby lobby = Brawl.getInstance().getGameHandler().getLobby();
            Game game = Brawl.getInstance().getGameHandler().getActiveGame();

            if (match != null) {
                location = spectating.getLocation();

                mode.setMatch(match);
                match.getMatchData().getSpectators().add(spectator.getUniqueId());

                type = SpectatorType.MATCH;
            } else if (lobby != null) {
                type = SpectatorType.GAME_LOBBY;
                location = Brawl.getInstance().getLocationByName("GAME_LOBBY");
            } else if (game != null) {
                type = SpectatorType.GAME;
                location = game.getDefaultLocation();
            } else {
                type = SpectatorType.PLAYER;
            }
        }

        spectator.teleport(location);
        mode.setSpectating(type);

        Brawl.getInstance().getItemHandler().apply(spectator, InventoryType.SPECTATOR);
        NametagHandler.reloadPlayer(spectator);
        NametagHandler.reloadOthersFor(spectator);
        
        return mode;
    }

    public void spectate(SpectatorType type, Player spectating) {
        Location location = Brawl.getInstance().getLocationByName("SPAWN");

        switch (type) {
            case MATCH: {
                Match match = Brawl.getInstance().getMatchHandler().containsPlayer(spectating, true);
                if (match.getMatchData().getSpectators().contains(spectator)) return;

                location = spectating.getLocation();

            }
        }
    }

    public Player getSpectator() {
        return Bukkit.getPlayer(spectator);
    }

}
