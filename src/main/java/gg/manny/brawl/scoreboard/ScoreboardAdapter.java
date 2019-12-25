package gg.manny.brawl.scoreboard;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.ability.Ability;
import gg.manny.brawl.duelarena.loadout.MatchLoadout;
import gg.manny.brawl.duelarena.match.Match;
import gg.manny.brawl.duelarena.match.MatchState;
import gg.manny.brawl.duelarena.queue.QueueSearchTask;
import gg.manny.brawl.game.Game;
import gg.manny.brawl.game.lobby.GameLobby;
import gg.manny.brawl.kit.Kit;
import gg.manny.brawl.levels.Level;
import gg.manny.brawl.player.PlayerData;
import gg.manny.brawl.player.cps.ClickTracker;
import gg.manny.brawl.player.simple.SimpleOfflinePlayer;
import gg.manny.brawl.player.statistic.PlayerStatistic;
import gg.manny.brawl.player.statistic.StatisticType;
import gg.manny.brawl.util.DurationFormatter;
import gg.manny.construct.ConstructAdapter;
import gg.manny.pivot.util.TimeUtils;
import gg.manny.server.util.chatcolor.CC;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class ScoreboardAdapter implements ConstructAdapter {

    private final Brawl plugin;

    @Override
    public String getTitle(Player player) {
        if (Brawl.getInstance().getGameHandler().getLobby() != null && Brawl.getInstance().getGameHandler().getLobby().getPlayers().contains(player.getUniqueId())) {
            return CC.DARK_PURPLE + CC.BOLD + Brawl.getInstance().getGameHandler().getLobby().getGameType().getShortName().toUpperCase();
        }
        return CC.DARK_PURPLE + CC.BOLD + "THE CRAFT";
    }

    @Override
    public List<String> getLines(Player player) {
        List<String> toReturn = new ArrayList<>();
        PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(player);
        toReturn.add(CC.SCOREBAORD_SEPARATOR);
        switch (playerData.getPlayerState()) {
            case GAME: {
                Game game = plugin.getGameHandler().getActiveGame();
                if (game != null) {
                    toReturn.addAll(game.getSidebar(player));
                }
                break;
            }
            case GAME_LOBBY: {
                this.getLobbyGame(player, playerData, toReturn);
                break;
            }
            case MATCH: {
                this.getMatchArena(player, playerData, toReturn);
                break;
            }
            case ARENA: {
                this.getDuelArena(player, playerData, toReturn);
                break;
            }
            default: {
                this.getSpawn(player, playerData, toReturn);
                break;
            }
        }
        toReturn.add(CC.WHITE + CC.SCOREBAORD_SEPARATOR);
        return toReturn;
    }



    private List<String> getSpawn(Player player, PlayerData playerData, List<String> toReturn) {
        PlayerStatistic statistic = playerData.getStatistic();
        Kit kit = playerData.getSelectedKit();
        Level level = playerData.getLevel();

        toReturn.add(CC.DARK_PURPLE  + "Kills: " + CC.LIGHT_PURPLE + (int) statistic.get(StatisticType.KILLS));
        toReturn.add(CC.DARK_PURPLE + "Deaths: " + CC.LIGHT_PURPLE + (int) statistic.get(StatisticType.DEATHS));
        toReturn.add(CC.DARK_PURPLE + "Killstreak: " + CC.LIGHT_PURPLE + (int) statistic.get(StatisticType.KILLSTREAK));
        if (playerData.isSpawnProtection()) {
            toReturn.add(CC.DARK_PURPLE  + "Highest Killstreak: " + CC.LIGHT_PURPLE  + (int) statistic.get(StatisticType.HIGHEST_KILLSTREAK));
            toReturn.add(CC.DARK_PURPLE + "KDR: " + CC.LIGHT_PURPLE  + (int) statistic.get(StatisticType.KDR));
            toReturn.add(CC.DARK_PURPLE + "Credits: " + CC.LIGHT_PURPLE  + (int) statistic.get(StatisticType.CREDITS));
        }

        // Remove for beta for now
        //toReturn.add(CC.DARK_PURPLE + "Level: " + CC.WHITE + level.getCurrentLevel() + " (" + level.getCurrentExp() + "/" + level.getMaxExperience() + " XP)");

        if (playerData.hasCooldown("ENDERPEARL")) {
            toReturn.add(CC.YELLOW + "Enderpearl: " + CC.RED + DurationFormatter.getRemaining(playerData.getCooldown("ENDERPEARL").getRemaining()));
        }

        if (kit != null) {
            for (Ability ability : kit.getAbilities()) {
                ability.getProperties(player).forEach((key, value) -> toReturn.add(CC.GOLD + key + ": " + CC.YELLOW + value));
                if (ability.hasCooldown(playerData.getPlayer(), false)) {
                    toReturn.add(CC.DARK_PURPLE + ability.getName() + ": " + CC.RED + DurationFormatter.getRemaining(ability.toCooldown(playerData).getRemaining()));
                }
            }
        }
        if (plugin.getEventHandler().getActiveKOTH() != null) {
            toReturn.addAll(plugin.getEventHandler().getActiveKOTH().getScoreboard(player));
        }
        return toReturn;
    }

    private List<String> getFighting(Player player, PlayerData playerData, List<String> toReturn) {
        return this.getSpawn(player, playerData, toReturn);
    }

    private List<String> getLobbyGame(Player player, PlayerData playerData, List<String> toReturn) {
        GameLobby lobby = Brawl.getInstance().getGameHandler().getLobby();
        toReturn.add(CC.DARK_PURPLE + "Players: " + CC.LIGHT_PURPLE + lobby.getPlayers().size() + "/" + lobby.getGameType().getMaxPlayers());
        toReturn.add(CC.DARK_PURPLE + "Starting in " + CC.LIGHT_PURPLE + lobby.getStartTime() + "s");
        toReturn.add("  ");
        toReturn.add(CC.DARK_PURPLE + "Map Votes:");
        int i = 0;
        for (Map.Entry<String, Integer> entry : lobby.getSortedVotes().entrySet()) {
            toReturn.add(CC.DARK_PURPLE + " - " + CC.WHITE + entry.getKey() + " " + CC.LIGHT_PURPLE + entry.getValue());
            if (i++ == 5) break;

        }
        return toReturn;
    }

    private List<String> getMatchArena(Player player, PlayerData playerData, List<String> toReturn) {
        Match match = plugin.getMatchHandler().getMatch(player);
        toReturn.add(CC.DARK_PURPLE + "Kit: " + CC.LIGHT_PURPLE + (match.getKit() != null && match.getState() == MatchState.GRACE_PERIOD ? "???" : match.getLoadout().getName()));
        if (match.getState() == MatchState.FINISHED) {
            toReturn.add(CC.DARK_PURPLE + "Winner: " + CC.LIGHT_PURPLE + match.getWinnerName());
        } else {
            toReturn.add(CC.DARK_PURPLE + "Opponent: " + CC.LIGHT_PURPLE + SimpleOfflinePlayer.getNameByUuid(match.getOpposite(player.getUniqueId())));

            Player opponent = Bukkit.getPlayer(match.getOpposite(player.getUniqueId()));

            if (opponent != null) {
                toReturn.add("  ");
                toReturn.add(CC.DARK_PURPLE + "(" + CC.LIGHT_PURPLE + ClickTracker.getCPS(player) + "CPS" + CC.DARK_PURPLE + ") vs. (" + CC.LIGHT_PURPLE + ClickTracker.getCPS(opponent) + "CPS" + CC.DARK_PURPLE + ")");
                toReturn.add(CC.DARK_PURPLE + "(" + CC.LIGHT_PURPLE + player.getPing() + "ms" + CC.DARK_PURPLE + ") vs. (" + CC.LIGHT_PURPLE + opponent.getPing() + "ms" + CC.DARK_PURPLE + ")");
            }
        }
        return toReturn;
    }
    private List<String> getDuelArena(Player player, PlayerData playerData, List<String> toReturn) {
        PlayerStatistic statistic = playerData.getStatistic();
        for (Map.Entry<MatchLoadout, Integer> entry : statistic.getArenaStatistics().entrySet()) {
            MatchLoadout loadout = entry.getKey();
            toReturn.add(loadout.getColor() + loadout.getName() + ": " + CC.WHITE + entry.getValue());
        }
        toReturn.add(CC.WHITE + CC.GRAY + CC.SCOREBAORD_SEPARATOR);
        if (plugin.getMatchHandler().isInQueue(player)) {
            toReturn.add(CC.DARK_PURPLE + plugin.getMatchHandler().getFriendlyQueue(player));
            toReturn.add(CC.DARK_PURPLE + "Time: " + CC.LIGHT_PURPLE + TimeUtils.formatIntoMMSS((int) TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - playerData.getQueueData().getQueueTime())));

            QueueSearchTask task = playerData.getQueueData().getTask();
            if (task != null) {
                toReturn.add(CC.DARK_PURPLE + "Elo range: " + CC.LIGHT_PURPLE + "[" + task.getMinRange() + " -> " + task.getMaxRange() + "]");
            }
        } else {
            toReturn.add(CC.DARK_PURPLE + "Global Elo: " + CC.LIGHT_PURPLE + statistic.getGlobalElo());
        }
        return toReturn;
    }
}
