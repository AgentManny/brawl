package rip.thecraft.brawl.scoreboard;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.game.Game;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.player.PlayerState;
import rip.thecraft.falcon.Falcon;
import rip.thecraft.falcon.profile.Profile;
import rip.thecraft.spartan.nametag.NametagInfo;
import rip.thecraft.spartan.nametag.NametagProvider;

public class BrawlNametagAdapter extends NametagProvider {

    private final Brawl plugin;

    public  BrawlNametagAdapter(Brawl plugin) {
        super("KitPvP", 75);

        this.plugin = plugin;
    }

    @Override
    public NametagInfo fetchNametag(Player toRefresh, Player refreshFor) {
        Profile profile = Falcon.getInstance().getProfileHandler().getByPlayer(toRefresh);
        String color = ChatColor.translateAlternateColorCodes('&', profile.getRank().getGameColor());

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
                    color = ChatColor.LIGHT_PURPLE.toString();
                }
                break;
            }
        }

        if (plugin.getSpectatorManager().inSpectator(toRefresh)) {
            color = ChatColor.GRAY.toString();
        }
        return createNametag(color, "");
    }
}
