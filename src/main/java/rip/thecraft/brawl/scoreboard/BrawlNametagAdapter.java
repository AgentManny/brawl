package rip.thecraft.brawl.scoreboard;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.duelarena.match.Match;
import rip.thecraft.brawl.game.Game;
import rip.thecraft.brawl.levels.Level;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.player.PlayerState;
import rip.thecraft.brawl.team.Team;
import rip.thecraft.falcon.Falcon;
import rip.thecraft.falcon.profile.Profile;
import rip.thecraft.server.util.chatcolor.CC;
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
        PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(toRefresh);

        String color = profile == null ? ChatColor.WHITE.toString() : CC.translate(profile.getColor());

        PlayerData refreshPlayerData = plugin.getPlayerDataHandler().getPlayerData(refreshFor);
        if (playerData == null || refreshPlayerData == null) return createNametag(color, "");

        PlayerState playerState = playerData.getPlayerState();
        PlayerState refreshPlayerState = refreshPlayerData.getPlayerState();
        boolean showLevel = true;
        switch (playerState) {
            case SPECTATING: {
                showLevel = false;
                break;
            }
            case MATCH: {
                Match match = plugin.getMatchHandler().getMatch(toRefresh);
                if (match.contains(refreshFor)) {
                    color = ChatColor.RED.toString();
                }
                break;
            }
            case GAME: {
                if (refreshPlayerState == PlayerState.GAME) {
                    Game game = plugin.getGameHandler().getActiveGame();
                    if (game != null) {
                        String prefix = game.handleNametag(toRefresh, refreshFor);
                        if (prefix != null) {
                            color = prefix;
                            showLevel = false;
                        }
                    }
                }
                break;
            }
            case GAME_LOBBY: {
                if (refreshPlayerState == PlayerState.GAME_LOBBY) {
                    color = ChatColor.LIGHT_PURPLE.toString();
                    showLevel = false;
                }
                break;
            }
        }

        if (plugin.getSpectatorManager().isSpectating(toRefresh)) {
            color = ChatColor.GRAY.toString();
        }

        Level level = playerData.getLevel();
        String levelPrefix = (showLevel ? Level.getColor(level.getCurrentLevel()) + level.getSimplePrefix() : "") + ChatColor.translateAlternateColorCodes('&', profile.getColor());

        Team team = Brawl.getInstance().getTeamHandler().getPlayerTeam(toRefresh);

        return createNametag(levelPrefix + color, team == null ? "" : team.getDisplayTagline());
    }
}
