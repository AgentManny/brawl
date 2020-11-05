package rip.thecraft.brawl.game.lobby;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import mkremins.fanciful.FancyMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.game.Game;
import rip.thecraft.brawl.game.GameType;
import rip.thecraft.brawl.game.map.GameMap;
import rip.thecraft.brawl.game.team.GameTeam;
import rip.thecraft.brawl.item.type.InventoryType;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.spectator.SpectatorMode;
import rip.thecraft.brawl.util.PlayerUtil;
import rip.thecraft.brawl.util.location.LocationType;
import rip.thecraft.spartan.nametag.NametagHandler;
import rip.thecraft.spartan.util.TimeUtils;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;


@Getter
public class GameLobby {

    private final Brawl brawl;

    @Setter
    @NonNull
    private final GameType gameType;

    @Setter private int startTime = 45;

    private List<UUID> players = new CopyOnWriteArrayList<>();

    private List<GameTeam> teams = new CopyOnWriteArrayList<>();
    private Set<GamePlayerInvite> invites = new HashSet<>();

    private Map<String, List<UUID>> voteMap = new HashMap<>();

    public GameLobby(Brawl brawl, GameType gameType) {
        this.brawl = brawl;
        this.gameType = gameType;

        this.voteMap.put("Random", new ArrayList<>());
        for (GameMap map : brawl.getGameHandler().getMapHandler().getMaps(gameType)) {
            this.voteMap.put(map.getName(), new ArrayList<>());
        }

        startTask();
    }

    public void join(Player player) {
        this.players.add(player.getUniqueId());

        PlayerData playerData = brawl.getPlayerDataHandler().getPlayerData(player);
        playerData.setSpawnProtection(false);
        playerData.setSelectedKit(null);
        playerData.setDuelArena(false);
        playerData.setEvent(true);


        this.broadcast(Game.PREFIX + ChatColor.WHITE + player.getDisplayName() + ChatColor.YELLOW + " has joined the event." + ChatColor.GRAY + " (" + this.players.size() + "/" + gameType.getMaxPlayers() + ")");

        player.teleport(LocationType.GAME_LOBBY.getLocation());
        PlayerUtil.resetInventory(player, GameMode.SURVIVAL);

        updateVotes();
        player.getInventory().setItem(8, brawl.getItemHandler().toItemStack("LANGUAGE.ITEM.GAME_LOBBY.LEAVE_EVENT", brawl.getConfig()));

        NametagHandler.reloadPlayer(player);
        NametagHandler.reloadOthersFor(player);

        player.updateInventory();
    }

    public void updateVotes() {
        for (UUID uuid : players) {
            int i = 0;
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;

            for (Map.Entry<String, List<UUID>> entry : voteMap.entrySet()) {
                String map = entry.getKey();

                boolean selected = entry.getValue().contains(player.getUniqueId());
                ItemStack item = brawl.getItemHandler().toItemStack("LANGUAGE.GAME.LOBBY.INVENTORY." + (selected ? "VOTE_SELECTED" : "VOTE"), brawl.getConfig());
                if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(meta.getDisplayName().replace("{MAP_NAME}", map));
                    item.setItemMeta(meta);
                }
                item.setAmount(Math.max(entry.getValue().size(), 1));
                player.getInventory().setItem(i, item);
                player.updateInventory();
                i++;
            }

        }
    }

    public void removeVote(UUID uuid) {
        for (Map.Entry<String, List<UUID>> entry : voteMap.entrySet()) {
            entry.getValue().remove(uuid);
        }
    }

    public void leave(UUID uuid) {
        this.players.remove(uuid);
        Player player = brawl.getServer().getPlayer(uuid);
        if (player != null) {
            NametagHandler.reloadPlayer(player);
            PlayerData playerData = brawl.getPlayerDataHandler().getPlayerData(player);
            playerData.setSpawnProtection(true);
            playerData.setEvent(false);
            playerData.setSelectedKit(null);
            player.teleport(brawl.getLocationByName("SPAWN"));
            brawl.getItemHandler().apply(player, InventoryType.SPAWN);
            NametagHandler.reloadPlayer(player);
            NametagHandler.reloadOthersFor(player);
        }
        removeVote(uuid);
        GameTeam team = this.getTeamByPlayer(uuid);
        if (team != null) {
            team.broadcast(Game.PREFIX_ERROR + ChatColor.RED + "Team disbanded as " + (player == null ? "someone" : player.getDisplayName()) + " left.");
            this.teams.remove(team);
        }


    }

    public Map<String, Integer> getSortedVotes() {
        return voteMap.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().size())).entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(
                        Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                LinkedHashMap::new));
    }

    public void start() {
        if (this.players.size() < gameType.getMinPlayers()) {
            Bukkit.broadcastMessage(Game.PREFIX + ChatColor.WHITE + gameType.getName() + ChatColor.YELLOW + " did not reach its required players.");
            stop();
            return;
        }

        Bukkit.broadcastMessage(Game.PREFIX + ChatColor.WHITE + gameType.getName() + ChatColor.YELLOW + " has started." + ChatColor.GRAY + " (" + getPlayers().size() + "/" + gameType.getMaxPlayers() + ")");
        Game game = brawl.getGameHandler().getGames().get(gameType);

        if (game == null) {
            stop();
            return;
        }

        game.cleanup(); // Prevents any supposed memory leaks

        GameMap map;
        String highestMap = getSortedVotes().keySet().iterator().next();
        if (highestMap.equals("Random")) {
            Collection<GameMap> maps = brawl.getGameHandler().getMapHandler().getMaps(gameType);
            map = maps.stream().skip((int) (maps.size() * Math.random())).findFirst().orElse(null);

        } else {
            map = brawl.getGameHandler().getMapHandler().getMapByName(gameType, highestMap);
        }

        if (map == null) {
            stop();
            Bukkit.broadcastMessage(Game.PREFIX_ERROR + "No maps found.");
            return;
        }

        for (Location location : map.getLocations().values()) {
            if (!location.getChunk().isLoaded()) {
                location.getChunk().load();
            }
        }

        game.setMap(map);
        game.init(this);

        brawl.getGameHandler().setActiveGame(game);
        brawl.getGameHandler().setLobby(null);

        for (SpectatorMode spectator : brawl.getSpectatorManager().getSpectators()) {
            if (spectator.getSpectating() == SpectatorMode.SpectatorType.GAME_LOBBY) {
                spectator.spectateGame(); // Re-spectate to game
            }
        }

        game.setup();

    }


    public void stop() {
        brawl.getSpectatorManager().removeSpectators(SpectatorMode.SpectatorType.GAME_LOBBY, this);
        this.players.forEach(this::leave);
        brawl.getGameHandler().setLobby(null);
    }

    public void startTask() {
        new BukkitRunnable() {
            public void run() {
                if (startTime == 0) {
                    start();
                    this.cancel();
                }
                switch (startTime) {
                    case 5 * 60:
                    case 4 * 60:
                    case 3 * 60:
                    case 2 * 60:
                    case 60:
                    case 30:
                    case 20:
                    case 15:
                    case 10:
                    case 5:
                    case 4:
                    case 3:
                    case 2:
                    case 1:
                        for(Player player : brawl.getServer().getOnlinePlayers()) {
                            new FancyMessage(Game.PREFIX + ChatColor.WHITE + gameType.getName() + ChatColor.YELLOW + " will be starting in " + ChatColor.LIGHT_PURPLE + TimeUtils.formatIntoDetailedString(startTime) + ChatColor.YELLOW + "." + ChatColor.GRAY + " (Click to join)").tooltip(Arrays.asList(ChatColor.YELLOW + "Click to join " + ChatColor.DARK_PURPLE + gameType.getName() + ChatColor.YELLOW + ".")).command("/join").send(player);
                        }
                        break;
                    default:
                        break;
                }

                startTime--;
            }

        }.runTaskTimer(brawl, 20L, 20L);
    }

    private GameTeam getTeamByPlayer(UUID uuid) {
        for(GameTeam team : teams) {
            if(team.getPlayers().contains(uuid)) {
                return team;
            }
        }
        return null;
    }

    private void broadcast(String message) {
        this.players.stream()
                .map(brawl.getServer()::getPlayer)
                .filter(Objects::nonNull)
                .forEach(player -> player.sendMessage(message));
    }
}
