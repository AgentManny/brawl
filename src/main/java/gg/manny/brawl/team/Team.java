package gg.manny.brawl.team;

import com.mongodb.client.model.Filters;
import gg.manny.brawl.Brawl;
import gg.manny.brawl.player.simple.SimpleOfflinePlayer;
import gg.manny.pivot.Pivot;
import gg.manny.pivot.util.PivotUtil;
import gg.manny.server.util.chatcolor.CC;
import lombok.Getter;
import lombok.Setter;
import mkremins.fanciful.FancyMessage;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class Team {

    public static final DecimalFormat HEALTH_FORMAT = new DecimalFormat("0.00");
    public static final int MAX_TEAM_SIZE = 5;

    private final ObjectId uniqueId;
    private UUID owner;

    private String name;

    private String announcement;

    private String password;

    private Set<UUID> managers = new HashSet<>();
    private Set<UUID> members = new HashSet<>();
    private Set<UUID> invitations = new HashSet<>();

    @Setter private boolean needsSave = false;
    private boolean loading = false;

    public Team(String name, UUID owner) {
        this.uniqueId = new ObjectId();

        this.name = name;
        this.owner = owner;
        this.members.add(owner);
    }

    public Team(ObjectId uniqueId, Document document) {
        this.uniqueId = uniqueId;
        this.owner = UUID.fromString(document.getString("owner"));

        this.name = document.getString("name");

        this.announcement = document.getString("announcement");

        this.password = document.getString("password");

        this.managers = Brawl.GSON.<List<String>>fromJson(document.getString("managers"), PivotUtil.LIST_STRING).stream().filter(Objects::nonNull).map(UUID::fromString).collect(Collectors.toSet());
        this.members = Brawl.GSON.<List<String>>fromJson(document.getString("members"), PivotUtil.LIST_STRING).stream().filter(Objects::nonNull).map(UUID::fromString).collect(Collectors.toSet());

        this.invitations = Brawl.GSON.<List<String>>fromJson(document.getString("invitations"), PivotUtil.LIST_STRING).stream().filter(Objects::nonNull).map(UUID::fromString).collect(Collectors.toSet());
    }

    public String getDisplayName(Player player) {
        return (isMember(player) ? CC.DARK_AQUA : CC.RED) + this.name;
    }


    public Document serialize() {
        return new Document("_id", this.uniqueId)
                .append("name", this.name)
                .append("owner", this.owner.toString())
                .append("managers", Brawl.GSON.toJson(this.managers.stream().map(UUID::toString).collect(Collectors.toList()), PivotUtil.LIST_STRING))
                .append("members", Brawl.GSON.toJson(this.members.stream().map(UUID::toString).collect(Collectors.toList()), PivotUtil.LIST_STRING))
                .append("invitations", Brawl.GSON.toJson(this.invitations.stream().map(UUID::toString).collect(Collectors.toList()), PivotUtil.LIST_STRING))
                .append("announcement", this.announcement)
                .append("password", this.password);
    }

    public void sendTeamInfo(CommandSender sender) {
        sender.sendMessage(CC.GRAY + "*** " + CC.DARK_AQUA + name + CC.GRAY + " ***");

        if (this.announcement != null && (sender.isOp() || sender.hasPermission("pivot.staff") || sender instanceof Player && isMember((Player) sender))) {
            sender.sendMessage(CC.DARK_AQUA + "Announcement: " + ChatColor.GRAY + announcement);
        }

        FancyMessage passwordBuilder = new FancyMessage("Password: ").color(ChatColor.DARK_AQUA).then(password == null ? "Not Set (Invite Only)" : "[Hidden]").color(ChatColor.GRAY);
        if (password != null) {
            passwordBuilder.tooltip(CC.GRAY + password);
        }

        passwordBuilder.send(sender);

        sender.sendMessage(ChatColor.DARK_AQUA + "Members (" + getOnlineMemberAmount() + "/" + getSize() + "):");
        List<UUID> players = new ArrayList<>();
        players.add(owner);
        players.addAll(managers);
        members.stream().filter(uuid -> !players.contains(uuid)).forEach(players::add);

        for (UUID uuid : players) {
            Player player = Brawl.getInstance().getServer().getPlayer(uuid);
            boolean online = (player != null && (!player.hasMetadata("hidden")));
            String role = isOwner(uuid) ? "**" : isManager(uuid) ? "*" : "";
            sender.sendMessage(CC.GRAY + " - " + (online ? CC.GREEN : CC.GRAY) + role + SimpleOfflinePlayer.getNameByUuid(uuid));
        }
    }

    public void disband() {
        for (Player player : this.getOnlineMembers()) {
            Pivot.getInstance().getNametagHandler().reloadPlayer(player);
        }

        for (UUID member : this.members) {
            Brawl.getInstance().getTeamHandler().getUuidTeamMap().remove(member);
        }

        Brawl.getInstance().getTeamHandler().getTeamNameMap().remove(this.name.toLowerCase());
        Brawl.getInstance().getTeamHandler().getTeamUniqueIdMap().remove(this.uniqueId);
        Brawl.getInstance().getTeamHandler().getCollection().deleteOne(Filters.eq("_id", this.uniqueId));
        this.needsSave = false;
    }

    public void rename(String newName) {
        String oldName = this.name;
        this.name = newName;

        Brawl.getInstance().getTeamHandler().getTeamNameMap().remove(oldName.toLowerCase());
        Brawl.getInstance().getTeamHandler().addTeam(this);
        this.flagForSave();
    }

    public void addMember(UUID uuid) {
        this.members.add(uuid);
        this.flagForSave();
    }

    public void addManager(UUID uuid) {
        this.managers.add(uuid);
        this.flagForSave();
    }

    public boolean isOwner(UUID uuid) {
        return this.owner.equals(uuid);
    }

    public boolean isMember(Player player) {
        return this.isMember(player.getUniqueId());
    }

    public boolean isMember(UUID uuid) {
        for (UUID member : this.members) {
            if (uuid.equals(member)) {
                return true;
            }
        }
        return false;
    }

    public boolean isManager(UUID uuid) {
        for (UUID captain : this.managers) {
            if (uuid.equals(captain)) {
                return true;
            }
        }
        return false;
    }

    public void removeManager(UUID uuid) {
        this.managers.removeIf(uuid::equals);
        this.flagForSave();
    }


    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
        this.flagForSave();
    }

    public void setOwner(UUID owner) {
        UUID oldOwner = this.owner;
        this.owner = owner;
        if (owner != null) {
            this.members.add(owner);
        }
        this.flagForSave();
    }

    public boolean removeMember(UUID uuid) {
        this.members.removeIf(uuid::equals);
        this.removeManager(uuid);
        if (this.isOwner(uuid)) {
            Iterator<UUID> iterator = this.members.iterator();
            if (iterator.hasNext()) {
                this.owner = iterator.next();
            } else {
                this.owner = null;
            }
        }
        this.flagForSave();
        return this.owner == null || this.members.size() == 0;
    }

    public int getOnlineMemberAmount() {
        int amount = 0;
        for (UUID uuid : this.members) {
            Player player = Brawl.getInstance().getServer().getPlayer(uuid);
            if (player != null && !player.hasMetadata("hidden")) {
                amount++;
            }
        }
        return amount;
    }

    public List<Player> getOnlineMembers() {
        List<Player> players = new ArrayList<>();
        for (UUID uuid : this.members) {
            if (uuid == null) continue;

            Player player = Brawl.getInstance().getServer().getPlayer(uuid);
            if (player == null || player.hasMetadata("hidden")) continue;
            players.add(player);
        }
        return players;
    }

    public List<UUID> getOfflineMembers() {
        List<UUID> players = new ArrayList<>();
        for (UUID uuid : this.getMembers()) {
            if (uuid == null) continue;

            Player player = Brawl.getInstance().getServer().getPlayer(uuid);
            if (player != null && !player.hasMetadata("hidden")) continue;

            players.add(uuid);
        }
        return players;
    }

    public int getSize() {
        return this.getMembers().size();
    }

    public void sendMessage(String message) {
        this.getOnlineMembers().forEach(player -> player.sendMessage(message));
    }

    public void sendMessage(FancyMessage message) {
        this.getOnlineMembers().forEach(message::send);
    }

    public void setPassword(String password) {
        this.password = password;
        this.flagForSave();
    }

    public void flagForSave() {
        this.needsSave = true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Team) {
            return ((Team) obj).getName().equals(this.getName());
        }
        return super.equals(obj);
    }
}
