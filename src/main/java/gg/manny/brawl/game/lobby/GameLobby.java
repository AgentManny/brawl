package gg.manny.brawl.game.lobby;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.Locale;
import gg.manny.brawl.game.GameType;
import gg.manny.brawl.game.map.GameMap;
import gg.manny.brawl.game.team.GameTeam;
import gg.manny.brawl.item.type.InventoryType;
import gg.manny.brawl.player.PlayerData;
import gg.manny.pivot.Pivot;
import gg.manny.pivot.util.PlayerUtils;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.WordUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;


@Getter
@RequiredArgsConstructor
public class GameLobby {

    private final Brawl brawl;

    @Setter
    @NonNull
    private final GameType gameType;

    private int startTime = 45;

    private List<UUID> players = new ArrayList<>();

    private List<GameTeam> teams = new CopyOnWriteArrayList<>();
    private Set<GamePlayerInvite> invites = new HashSet<>();

    private Map<UUID, GameMap> playerMap = new HashMap<>();

    public void join(Player player) {
        this.players.add(player.getUniqueId());

        this.broadcast(Locale.GAME_LOBBY_JOIN.format(player.getDisplayName(), this.players.size(), gameType.getMaxPlayers()));

        player.teleport(this.getLocation());
        PlayerUtils.resetInventory(player, GameMode.SURVIVAL);
        int i = 0;

        GameMap selectedVote = this.playerMap.get(player.getUniqueId());
        for (GameMap map : brawl.getGameHandler().getMapHandler().getMaps(gameType)) {
            ItemStack item = brawl.getItemHandler().toItemStack("LANGUAGE.GAME.LOBBY.INVENTORY." + (selectedVote != null && selectedVote == map ? "VOTE_SELECTED" : "VOTE"), brawl.getMainConfig().getConfiguration());
            if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(meta.getDisplayName().replace("{MAP_NAME}", WordUtils.capitalizeFully(map.getName().replace("_", " ").toLowerCase())));
                item.setItemMeta(meta);
            }
            player.getInventory().setItem(i, item);
            i++;
        }
        player.getInventory().setItem(8, brawl.getItemHandler().toItemStack("LANGUAGE.GAME.LOBBY.INVENTORY.LEAVE_ITEM", brawl.getMainConfig().getConfiguration()));
        Pivot.getPlugin().getNametagHandler().reloadPlayer(player);
    }

    public void leave(UUID uuid) {
        this.players.remove(uuid);
        Player player = brawl.getServer().getPlayer(uuid);
        if (player != null) {
            Pivot.getPlugin().getNametagHandler().reloadPlayer(player);
            PlayerData playerData = brawl.getPlayerDataHandler().getPlayerData(player);
            playerData.setSpawnProtection(true);
            player.teleport(brawl.getLocationByName("SPAWN"));
            brawl.getItemHandler().apply(player, InventoryType.SPAWN);
        }

        GameTeam team = this.getTeamByPlayer(uuid);
        if (team != null) {
            team.broadcast(Locale.GAME_TEAM_REMOVED.format(player == null ? "someone" : player.getName()));
            this.teams.remove(team);
        }
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

    public Location getLocation() {
        String key = gameType.name() + "_LOBBY";
        Location location = brawl.getLocationByName(key);
        if (location == null) {
            brawl.getLogger().severe("Location " + key + " could not be found, falling back to default game location.");
            return this.getFallbackLocation();
        }
        return location;
    }

    private Location getFallbackLocation() {
        String key = "GAME_LOBBY";
        Location location = brawl.getLocationByName(key);
        if (location == null) {
            brawl.getLogger().severe("Location " + key + " could not be found!");
        }
        return location;
    }

}
