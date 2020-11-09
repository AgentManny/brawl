package rip.thecraft.brawl.scoreboard;

import me.activated.core.plugin.AquaCoreAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.game.Game;
import rip.thecraft.brawl.levels.Level;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.player.PlayerState;
import rip.thecraft.brawl.team.Team;
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
        PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(toRefresh);
        String color = AquaCoreAPI.INSTANCE.getPlayerNameColor(toRefresh.getUniqueId()).toString();

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

        if (plugin.getSpectatorManager().isSpectating(toRefresh)) {
            color = ChatColor.GRAY.toString();
        }

        Level level = playerData.getLevel();
        String levelPrefix = Level.getColor(level.getCurrentLevel()) + level.getSimplePrefix() + color;

        Team team = Brawl.getInstance().getTeamHandler().getPlayerTeam(toRefresh);

        return createNametag(levelPrefix + color, team == null ? "" : team.getDisplayTagline());
    }
}
