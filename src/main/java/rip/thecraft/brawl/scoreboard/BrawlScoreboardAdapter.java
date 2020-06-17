package rip.thecraft.brawl.scoreboard;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.duelarena.loadout.MatchLoadout;
import rip.thecraft.brawl.duelarena.match.Match;
import rip.thecraft.brawl.duelarena.match.MatchState;
import rip.thecraft.brawl.duelarena.queue.QueueSearchTask;
import rip.thecraft.brawl.game.Game;
import rip.thecraft.brawl.game.lobby.GameLobby;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.levels.Level;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.player.cps.ClickTracker;
import rip.thecraft.brawl.player.statistic.PlayerStatistic;
import rip.thecraft.brawl.player.statistic.StatisticType;
import rip.thecraft.brawl.util.DurationFormatter;
import rip.thecraft.spartan.scoreboard.ScoreboardAdapter;
import rip.thecraft.spartan.util.LinkedList;
import rip.thecraft.spartan.util.TimeUtils;
import rip.thecraft.spartan.uuid.MUUIDCache;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class BrawlScoreboardAdapter implements ScoreboardAdapter {

    public static final String LINE_SEPARATOR = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------";
    
    private final Brawl plugin;

    @Override
    public String getTitle(Player player) {
        if (Brawl.getInstance().getGameHandler().getLobby() != null && Brawl.getInstance().getGameHandler().getLobby().getPlayers().contains(player.getUniqueId())) {
            return ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + Brawl.getInstance().getGameHandler().getLobby().getGameType().getShortName().toUpperCase();
        }
        return ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "THE CRAFT";
    }

    @Override
    public void getLines(LinkedList<String> lines, Player player) {
        PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(player);
        if (playerData == null) return;

        lines.add(LINE_SEPARATOR);
        switch (playerData.getPlayerState()) {
            case GAME: {
                Game game = plugin.getGameHandler().getActiveGame();
                if (game != null) {
                    lines.addAll(game.getSidebar(player));
                }
                break;
            }
            case GAME_LOBBY: {
                this.getLobbyGame(player, playerData, lines);
                break;
            }
            case MATCH: {
                this.getMatchArena(player, playerData, lines);
                break;
            }
            case ARENA: {
                this.getDuelArena(player, playerData, lines);
                break;
            }
            default: {
                this.getSpawn(player, playerData, lines);
                break;
            }
        }
        lines.add(ChatColor.WHITE + LINE_SEPARATOR);
    }

    private List<String> getSpawn(Player player, PlayerData playerData, List<String> toReturn) {
        PlayerStatistic statistic = playerData.getStatistic();
        Kit kit = playerData.getSelectedKit();
        Level level = playerData.getLevel();

        toReturn.add(ChatColor.DARK_PURPLE  + "Kills: " + ChatColor.LIGHT_PURPLE + (int) statistic.get(StatisticType.KILLS));
        toReturn.add(ChatColor.DARK_PURPLE + "Deaths: " + ChatColor.LIGHT_PURPLE + (int) statistic.get(StatisticType.DEATHS));
        toReturn.add(ChatColor.DARK_PURPLE + "Killstreak: " + ChatColor.LIGHT_PURPLE + (int) statistic.get(StatisticType.KILLSTREAK));
        if (playerData.isSpawnProtection()) {
            toReturn.add(ChatColor.DARK_PURPLE  + "Highest Killstreak: " + ChatColor.LIGHT_PURPLE  + (int) statistic.get(StatisticType.HIGHEST_KILLSTREAK));
            toReturn.add(ChatColor.DARK_PURPLE + "KDR: " + ChatColor.LIGHT_PURPLE  + (int) statistic.get(StatisticType.KDR));
            toReturn.add(ChatColor.DARK_PURPLE + "Credits: " + ChatColor.LIGHT_PURPLE  + (int) statistic.get(StatisticType.CREDITS));
            toReturn.add(ChatColor.DARK_PURPLE + "Level: " + ChatColor.LIGHT_PURPLE + level.getCurrentLevel() + " (" + level.getCurrentExp() + "/" + level.getMaxExperience() + " XP)");
        }

        if (playerData.hasCooldown("ENDERPEARL")) {
            toReturn.add(ChatColor.DARK_RED + "Enderpearl: " + ChatColor.RED + DurationFormatter.getRemaining(playerData.getCooldown("ENDERPEARL").getRemaining()));
        }

        if (kit != null) {
            for (Ability ability : kit.getAbilities()) {
                ability.getProperties(player).forEach((key, value) -> toReturn.add(ChatColor.GOLD + key + ": " + ChatColor.YELLOW + value));
                if (ability.hasCooldown(playerData.getPlayer(), false)) {
                    toReturn.add(ChatColor.DARK_RED + ability.getName() + ": " + ChatColor.RED + DurationFormatter.getRemaining(ability.toCooldown(playerData).getRemaining()));
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
        toReturn.add(ChatColor.DARK_PURPLE + "Players: " + ChatColor.LIGHT_PURPLE + lobby.getPlayers().size() + "/" + lobby.getGameType().getMaxPlayers());
        toReturn.add(ChatColor.DARK_PURPLE + "Starting in " + ChatColor.LIGHT_PURPLE + lobby.getStartTime() + "s");
        toReturn.add("  ");
        toReturn.add(ChatColor.DARK_PURPLE + "Map Votes:");
        int i = 0;
        for (Map.Entry<String, Integer> entry : lobby.getSortedVotes().entrySet()) {
            toReturn.add(ChatColor.DARK_PURPLE + " - " + ChatColor.WHITE + entry.getKey() + " " + ChatColor.LIGHT_PURPLE + entry.getValue());
            if (i++ == 5) break;

        }
        return toReturn;
    }

    private List<String> getMatchArena(Player player, PlayerData playerData, List<String> toReturn) {
        Match match = plugin.getMatchHandler().getMatch(player);
        toReturn.add(ChatColor.DARK_PURPLE + "Kit: " + ChatColor.LIGHT_PURPLE + (match.getKit() != null && match.getState() == MatchState.GRACE_PERIOD ? "???" : match.getLoadout().getName()));
        if (match.getState() == MatchState.FINISHED) {
            toReturn.add(ChatColor.DARK_PURPLE + "Winner: " + ChatColor.LIGHT_PURPLE + match.getWinnerName());
        } else {
            toReturn.add(ChatColor.DARK_PURPLE + "Opponent: " + ChatColor.LIGHT_PURPLE + MUUIDCache.name(match.getOpposite(player.getUniqueId())));

            Player opponent = Bukkit.getPlayer(match.getOpposite(player.getUniqueId()));

            if (opponent != null) {
                toReturn.add("  ");
                toReturn.add(ChatColor.DARK_PURPLE + "(" + ChatColor.LIGHT_PURPLE + ClickTracker.getCPS(player) + "CPS" + ChatColor.DARK_PURPLE + ") vs. (" + ChatColor.LIGHT_PURPLE + ClickTracker.getCPS(opponent) + "CPS" + ChatColor.DARK_PURPLE + ")");
                toReturn.add(ChatColor.DARK_PURPLE + "(" + ChatColor.LIGHT_PURPLE + player.getPing() + "ms" + ChatColor.DARK_PURPLE + ") vs. (" + ChatColor.LIGHT_PURPLE + opponent.getPing() + "ms" + ChatColor.DARK_PURPLE + ")");
            }
        }
        return toReturn;
    }
    private List<String> getDuelArena(Player player, PlayerData playerData, List<String> toReturn) {
        PlayerStatistic statistic = playerData.getStatistic();
        for (Map.Entry<MatchLoadout, Integer> entry : statistic.getArenaStatistics().entrySet()) {
            MatchLoadout loadout = entry.getKey();
            toReturn.add(loadout.getColor() + loadout.getName() + ": " + ChatColor.WHITE + entry.getValue());
        }
        toReturn.add(ChatColor.WHITE.toString() + ChatColor.GRAY + LINE_SEPARATOR);
        if (plugin.getMatchHandler().isInQueue(player)) {
            toReturn.add(ChatColor.DARK_PURPLE + plugin.getMatchHandler().getFriendlyQueue(player));
            toReturn.add(ChatColor.DARK_PURPLE + "Time: " + ChatColor.LIGHT_PURPLE + TimeUtils.formatIntoMMSS((int) TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - playerData.getQueueData().getQueueTime())));

            QueueSearchTask task = playerData.getQueueData().getTask();
            if (task != null) {
                toReturn.add(ChatColor.DARK_PURPLE + "Elo range: " + ChatColor.LIGHT_PURPLE + "[" + task.getMinRange() + " -> " + task.getMaxRange() + "]");
            }
        } else {
            toReturn.add(ChatColor.DARK_PURPLE + "Global Elo: " + ChatColor.LIGHT_PURPLE + statistic.getGlobalElo());
        }
        return toReturn;
    }
}