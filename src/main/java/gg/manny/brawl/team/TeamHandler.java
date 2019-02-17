package gg.manny.brawl.team;

import com.google.common.collect.ImmutableList;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import gg.manny.brawl.Brawl;
import gg.manny.brawl.player.PlayerData;
import org.bson.Document;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public class TeamHandler {

    private final Brawl brawl;

    private MongoCollection collection;

    private Map<UUID, Team> teamUniqueIdMap = new ConcurrentHashMap<>();
    private Map<String, UUID> teamNameMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private Map<UUID, UUID> playerTeamMap = new ConcurrentHashMap<>();

    public TeamHandler(Brawl brawl) {
        this.brawl = brawl;

        this.collection = brawl.getMongoDatabase().getCollection("teams");
        this.load();
    }

    private void load() {
        try (MongoCursor<Document> cursor = this.collection.find().iterator()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();

                String name = document.getString("name");
                UUID uniqueId = UUID.fromString(document.getString("uniqueId"));
                UUID leader = UUID.fromString(document.getString("leader"));

                Team team = new Team(name, uniqueId, leader);
                team.fromJson(document);

                if (team.isLoaded()) {
                    this.create(team, false);
                }
            }
        }
    }

    public void save() {
        for (Team team : this.teamUniqueIdMap.values()) {
            this.save(team);
        }
    }

    public void save(Team team) {
        this.collection.replaceOne(Filters.eq("uniqueId", team.getUniqueId()), team.toJson(), new ReplaceOptions().upsert(true));
    }

    public void create(Team team, boolean save) {
        this.teamUniqueIdMap.put(team.getUniqueId(), team);
        this.teamNameMap.put(team.getName(), team.getUniqueId());
        for (UUID playerUuid : team.getPlayers()) {
            this.playerTeamMap.put(playerUuid, playerUuid);
        }
        if (save) {
            brawl.getServer().getScheduler().runTaskAsynchronously(brawl, this::save);
        }
    }

    public void remove(Team team) {
        brawl.getServer().getScheduler().runTaskAsynchronously(brawl, () -> {
            this.teamUniqueIdMap.remove(team.getUniqueId());
            this.teamNameMap.remove(team.getName());
            for (UUID playerUuid : team.getPlayers()) {
                this.playerTeamMap.remove(playerUuid);
            }
            this.collection.deleteOne(Filters.eq("uniqueId", team.getUniqueId()));
        });
    }

    public Team getTeam(String teamName) {
        UUID uuid = teamNameMap.get(teamName);
        return uuid == null ? null : teamUniqueIdMap.get(uuid);
    }

    public Team getTeam(UUID teamUniqueId) {
        return teamUniqueIdMap.get(teamUniqueId);
    }

    public Team getTeamByUuid(UUID playerUniqueId) {
        return this.teamUniqueIdMap.get(this.playerTeamMap.get(playerUniqueId));
    }

    public Team getTeamByPlayerData(PlayerData playerData) {
        return this.getTeamByUuid(playerData.getUniqueId());
    }

    public Team getTeamByPlayer(Player player) {
        return this.getTeamByUuid(player.getUniqueId());
    }

    public ImmutableList<Team> getTeams() {
        return ImmutableList.copyOf(this.teamUniqueIdMap.values());
    }

}
