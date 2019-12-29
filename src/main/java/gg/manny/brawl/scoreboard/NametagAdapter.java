package gg.manny.brawl.scoreboard;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.game.Game;
import gg.manny.brawl.player.PlayerData;
import gg.manny.brawl.player.PlayerState;
import gg.manny.pivot.Pivot;
import gg.manny.pivot.nametag.Nametag;
import gg.manny.pivot.nametag.NametagProvider;
import gg.manny.pivot.profile.Profile;
import gg.manny.pivot.util.chatcolor.CC;
import org.bukkit.entity.Player;

public class NametagAdapter extends NametagProvider {

    private final Brawl plugin;

    public NametagAdapter(Brawl plugin) {
        super("KitPvP", 75);

        this.plugin = plugin;
    }

    @Override
    public Nametag fetchNametag(Player toRefresh, Player refreshFor) {
        Profile profile = Pivot.getInstance().getProfileHandler().getProfile(toRefresh);
        String color = profile.getDisguisePlayer() != null ? profile.getDisguisePlayer().getRank().getColor() : profile.getRank().getColor();

        PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(toRefresh);

        PlayerData refreshPlayerData = plugin.getPlayerDataHandler().getPlayerData(refreshFor);
if (playerData == null || refreshPlayerData == null) return createNametag(color, "");
        switch (playerData.getPlayerState()) {
            case GAME: {
                if (refreshPlayerData.getPlayerState() == PlayerState.GAME) {
                    Game game = plugin.getGameHandler().getActiveGame();
                    if (game != null) {
                        String prefix = game.handleNametag(toRefresh, refreshFor);
                        if (prefix != null) {
                            color = prefix;
                        }
                    }
                }
                break;
            }
            case GAME_LOBBY: {
                if (refreshPlayerData.getPlayerState() == PlayerState.GAME_LOBBY) {
                    color = CC.LIGHT_PURPLE;
                }
                break;
            }
        }

        if (plugin.getSpectatorManager().inSpectator(toRefresh)) {
            color = CC.GRAY;
        }
        return createNametag(color, "");
    }

    /**
     * Reloads all OTHER players for the player provided.
     *
     * @param refreshFor The player who should have all viewable nametags refreshed.
     */
    public static void reloadOthersFor(Player refreshFor) {
        Pivot.getInstance().getNametagHandler().reloadOthersFor(refreshFor);
    }

    /**
     * Refreshes one player for all players online.
     * NOTE: This is not an instant refresh, this is queued and async.
     *
     * @param toRefresh The player to refresh.
     */
    public static void reloadPlayer(Player toRefresh) {
        Pivot.getInstance().getNametagHandler().reloadPlayer(toRefresh);
    }

    /**
     * Refreshes one player for another player only.
     * NOTE: This is not an instant refresh, this is queued and async.
     *
     * @param toRefresh  The player to refresh.
     * @param refreshFor The player to refresh toRefresh for.
     */
    public static void reloadPlayer(Player toRefresh, Player refreshFor) {
        Pivot.getInstance().getNametagHandler().reloadPlayer(toRefresh, refreshFor);
    }
}
