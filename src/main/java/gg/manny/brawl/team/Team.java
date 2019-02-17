package gg.manny.brawl.team;

import com.google.common.base.Strings;
import gg.manny.brawl.Brawl;
import gg.manny.brawl.Locale;
import gg.manny.pivot.Pivot;
import gg.manny.pivot.util.PivotUtil;
import gg.manny.spigot.util.chatcolor.CC;
import lombok.Data;
import lombok.NonNull;
import org.apache.commons.lang.StringUtils;
import org.bson.Document;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;


@Data
public class Team {

    private final UUID uniqueId;

    @NonNull
    private String name;
    @NonNull
    private UUID leader;

    private Set<UUID> members = new HashSet<>();
    private Set<UUID> coleaders = new HashSet<>();
    private Set<UUID> captains = new HashSet<>();

    private Set<UUID> invitations = new HashSet<>();

    private String announcement = null;

    private boolean loaded = false;

    public Team(String name, UUID leader) {
        this.name = name;
        this.leader = leader;
        this.uniqueId = UUID.randomUUID();
    }

    public Team(String name, UUID leader, UUID uniqueId) {
        this.name = name;
        this.leader = leader;
        this.uniqueId = uniqueId;
    }

    public void load(Document document) {
        this.fromJson(document);
        this.loaded = true;
    }

    public void fromJson(Document document) {
        this.members = Pivot.GSON.<List<String>>fromJson(document.getString("members"), PivotUtil.LIST_STRING).stream().map(UUID::fromString).collect(Collectors.toSet());
        this.coleaders = Pivot.GSON.<List<String>>fromJson(document.getString("coleaders"), PivotUtil.LIST_STRING).stream().map(UUID::fromString).collect(Collectors.toSet());
        this.captains = Pivot.GSON.<List<String>>fromJson(document.getString("captains"), PivotUtil.LIST_STRING).stream().map(UUID::fromString).collect(Collectors.toSet());

        this.invitations = Pivot.GSON.<List<String>>fromJson(document.getString("invitations"), PivotUtil.LIST_STRING).stream().map(UUID::fromString).collect(Collectors.toSet());

        this.announcement = document.getString("announcement");
        this.loaded = true;
    }

    public Document toJson() {
        return new Document("name", this.name).append("uniqueId", this.uniqueId.toString()).append("leader", this.leader.toString()).append("members", Pivot.GSON.toJson(this.members.stream().map(UUID::toString).collect(Collectors.toList()))).append("coleaders", Pivot.GSON.toJson(this.coleaders.stream().map(UUID::toString).collect(Collectors.toList()))).append("captains", Pivot.GSON.toJson(this.captains.stream().map(UUID::toString).collect(Collectors.toList()))).append("invitations", Pivot.GSON.toJson(this.invitations.stream().map(UUID::toString).collect(Collectors.toList()))).append("announcement", this.announcement);
    }

    public TeamRole getRole(UUID uniqueId) {
        if (this.leader.equals(uniqueId)) {
            return TeamRole.LEADER;
        } else if (this.coleaders.contains(uniqueId)) {
            return TeamRole.COLEADER;
        } else if (this.captains.contains(uniqueId)) {
            return TeamRole.CAPTAIN;
        }
        return TeamRole.MEMBER;
    }

    public List<UUID> getPlayers() {
        List<UUID> totalMembers = new ArrayList<>(this.members);
        totalMembers.addAll(this.captains);
        totalMembers.addAll(this.coleaders);
        totalMembers.add(this.leader);
        return totalMembers;
    }

    public List<Player> getOnlinePlayers() {
        return this.getPlayers()
                .stream()
                .map(Brawl.getInstance().getServer()::getPlayer)
                .filter(Objects::isNull)
                .collect(Collectors.toList());
    }

    public void broadcast(String message) {
        this.getOnlinePlayers().forEach(player -> player.sendMessage(message));
    }

    public void sendTeamInfo(CommandSender sender) {
        Player leader = Brawl.getInstance().getServer().getPlayer(this.leader);
        String leaderName = (leader == null ? CC.GRAY : leader.hasMetadata("hidden") ? CC.GRAY : CC.GREEN) + Pivot.getPlugin().getUuidCache().getName(this.uniqueId);

        StringBuilder coleaders = new StringBuilder(CC.YELLOW + "Coleaders: ");
        StringBuilder captains = new StringBuilder(CC.YELLOW + "Captains: ");
        StringBuilder members = new StringBuilder(CC.YELLOW + "Members: ");
        for (UUID uuid : this.coleaders) {
            Player member = Brawl.getInstance().getServer().getPlayer(uuid);
            String name = (member == null ? CC.GRAY : member.hasMetadata("hidden") ? CC.GRAY : CC.GREEN) + Pivot.getPlugin().getUuidCache().getName(uuid);
            coleaders.append(name).append(CC.YELLOW).append(" ");
        }

        for (UUID uuid : this.captains) {
            Player member = Brawl.getInstance().getServer().getPlayer(uuid);
            String name = (member == null ? CC.GRAY : member.hasMetadata("hidden") ? CC.GRAY : CC.GREEN) + Pivot.getPlugin().getUuidCache().getName(uuid);
            captains.append(name).append(CC.YELLOW).append(" ");
        }

        for (UUID uuid : this.members) {
            Player member = Brawl.getInstance().getServer().getPlayer(uuid);
            String name = (member == null ? CC.GRAY : member.hasMetadata("hidden") ? CC.GRAY : CC.GREEN) + Pivot.getPlugin().getUuidCache().getName(uuid);
            members.append(name).append(CC.YELLOW).append(" ");
        }

        for (String entry : Locale.TEAM_INFO.toList()) {
            if (entry.contains("{ANNOUNCEMENT") && this.announcement == null || entry.contains("{COLEADERS}") && this.coleaders.isEmpty() || entry.contains("{CAPTAINS}") && this.captains.isEmpty() || entry.contains("{MEMBERS}") && this.members.isEmpty()) continue;

            String line = entry.replace("{LINE}", CC.GRAY + CC.STRIKETHROUGH + Strings.repeat("-", 51))
                    .replace("{NAME}", this.name)
                    .replace("{ONLINE}", this.getOnlinePlayers().size() + "")
                    .replace("{SIZE}", this.getPlayers().size() + "")
                    .replace("{LEADER}", leaderName)
                    .replace("{COLEADERS}", StringUtils.join(coleaders.toString().split(" "), ", "))
                    .replace("{CAPTAINS}", StringUtils.join(captains.toString().split(" "), ", "))
                    .replace("{MEMBERS}", StringUtils.join(members.toString().split(" "), ", "))
                    .replace("{ANNOUNCEMENT}", this.announcement);
            sender.sendMessage(line);
        }
    }
}
