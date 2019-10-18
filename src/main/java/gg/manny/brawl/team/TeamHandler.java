package gg.manny.brawl.team;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import gg.manny.brawl.Brawl;
import gg.manny.brawl.team.adapter.TeamTypeAdapter;
import gg.manny.brawl.team.command.TeamCommand;
import gg.manny.brawl.team.command.general.TeamAcceptCommand;
import gg.manny.brawl.team.command.general.TeamChatCommand;
import gg.manny.brawl.team.command.general.TeamCreateCommand;
import gg.manny.brawl.team.command.general.TeamLeaveCommand;
import gg.manny.brawl.team.command.info.TeamInfoCommand;
import gg.manny.brawl.team.command.info.TeamListCommand;
import gg.manny.brawl.team.command.leader.*;
import gg.manny.brawl.team.command.manager.*;
import gg.manny.brawl.team.command.staff.*;
import gg.manny.pivot.Pivot;
import gg.manny.pivot.util.chatcolor.CC;
import gg.manny.quantum.Quantum;
import lombok.Getter;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class TeamHandler {

    private volatile ConcurrentHashMap<ObjectId, Team> teamUniqueIdMap = new ConcurrentHashMap<>();
    private volatile ConcurrentHashMap<String, Team> teamNameMap = new ConcurrentHashMap<>();
    private volatile ConcurrentHashMap<UUID, Team> uuidTeamMap = new ConcurrentHashMap<>();

    private MongoCollection<Document> collection;
    private boolean loading = false;

    public TeamHandler() {
        this.collection = Brawl.getInstance().getMongoDatabase().getCollection("teams");

        this.registerCommands();
        this.loadTeams();

        new TeamSaveTask().runTaskTimerAsynchronously(Brawl.getInstance(), 6000L, 6000L);
    }

    public void registerCommands() {
        Quantum quantum = Pivot.getInstance().getQuantum();

        quantum.registerParameterType(Team.class, new TeamTypeAdapter());

        Arrays.asList(
                new ForceDisbandAllCommand(),
                new ForceDisbandCommand(),
                new ForceJoinCommand(),
                new ForceKickCommand(),
                new ForceLeaveCommand(),
                new ForceLeaveCommand(),
                new ForceNameCommand(),
                new SaveTeamCommand(),

                new TeamPasswordCommand(),

                new TeamManagerCommand(),

                new TeamAcceptCommand(),
                new TeamAnnouncementCommand(),
                new TeamChatCommand(),
                new TeamCommand(),
                new TeamCreateCommand(),
                new TeamDemoteCommand(),
                new TeamDisbandCommand(),
                new TeamCommand(),
                new TeamInfoCommand(),
                new TeamInviteCommand(),
                new TeamInvitesCommand(),
                new TeamKickCommand(),
                new TeamLeaderCommand(),
                new TeamLeaveCommand(),
                new TeamListCommand(),
                new TeamPromoteCommand(),
                new TeamRenameCommand(),
                new TeamUninviteCommand()

        ).forEach(quantum::registerCommand);
    }

    public List<Team> getTeams() {
        return new ArrayList<>(this.teamNameMap.values());
    }

    public void setTeam(UUID uniqueId, Team team) {
        this.uuidTeamMap.put(uniqueId, team);
    }

    public Team getTeam(String teamName) {
        return this.teamNameMap.get(teamName.toLowerCase());
    }

    public Team getTeam(ObjectId teamUniqueId) {
        if (teamUniqueId == null) return null;
        return this.teamUniqueIdMap.get(teamUniqueId);
    }

    private void loadTeams() {
        loading = true;
        long now = System.nanoTime();

        collection.find().iterator().forEachRemaining(data -> {
            ObjectId id = data.getObjectId("_id");

            Team team = new Team(id, data);
            teamNameMap.put(team.getName().toLowerCase(), team);
            teamUniqueIdMap.put(team.getUniqueId(), team);
            for (UUID member : team.getMembers()) {
                uuidTeamMap.put(member, team);
            }
        });

        loading = false;

        String nanosFancy = new DecimalFormat("#.##").format((System.nanoTime() - now) / 1E6D);
        System.out.println("{" + Brawl.getInstance().getName() + "} Successfully loaded "
                + this.getTeams().size() + " teams in " + nanosFancy + "ms");
    }

    public Team getPlayerTeam(UUID uuid) {
        if (!this.uuidTeamMap.containsKey(uuid)) {
            return null;
        }
        return this.uuidTeamMap.get(uuid);
    }

    public Team getPlayerTeam(Player player) {
        return this.getPlayerTeam(player.getUniqueId());
    }

    public void addTeam(Team team) {
        team.flagForSave();
        this.teamNameMap.put(team.getName().toLowerCase(), team);
        this.teamUniqueIdMap.put(team.getUniqueId(), team);
        for (UUID member : team.getMembers()) {
            this.uuidTeamMap.put(member, team);
        }
    }


    public int save(boolean forceAll) {
        System.out.println("Saving teams to Mongo...");
        int saved = 0;
        long startMs = System.currentTimeMillis();

        for (Team team : this.getTeams()) {
            if (team.isNeedsSave() || forceAll) {
                saved++;
                team.setNeedsSave(false);
                this.collection.replaceOne(Filters.eq("_id", team.getUniqueId()), team.serialize(), new ReplaceOptions().upsert(true));
            }
        }

        int time = (int) (System.currentTimeMillis() - startMs);
        if (saved > 0) {
            Brawl.broadcastOps(ChatColor.LIGHT_PURPLE + "Updated " + saved + " teams (Completed: " + CC.YELLOW + time + "ms" + CC.LIGHT_PURPLE + ")");
            System.out.println("Saved " + saved + " teams to Mongo in " + time + "ms.");
        }
        return saved;
    }

    private class TeamSaveTask extends BukkitRunnable {

        @Override
        public void run() {
            save(false);
        }

    }

}
