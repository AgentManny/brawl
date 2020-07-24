package rip.thecraft.brawl.event.events;

import com.mongodb.BasicDBObject;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.event.Event;
import rip.thecraft.brawl.event.EventType;
import rip.thecraft.brawl.team.Team;
import rip.thecraft.brawl.util.cuboid.Cuboid;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.util.TimeUtils;

import java.util.List;

public class KillTheKing extends Event {



    @Setter private transient String lastDamage = null;
    @Getter private transient String king;
    @Getter private transient String lastSeen;
    @Getter private transient Location lastSeenLoc;

    public KillTheKing(String name) {
        super(name, EventType.KING_OF_THE_HILL);
    }

    @Override
    public void start() {

    }

    @Override
    public void finish(Player winner) {

    }

    @Override
    public void tick() {

    }

    @Override
    public void getScoreboard(Player player, List<String> entries) {
        entries.add(ChatColor.GOLD.toString() + ChatColor.BOLD + "Kill The King");
        if (killTheKing.getKing() != null) {
            Player king = Bukkit.getPlayer(killTheKing.getKing());
            if (king != null) {
                double health = Math.round(king.getHealth()) / 2D;

                ChatColor healthColor;
                if (health > 8) {
                    healthColor = ChatColor.GREEN;
                } else if (health > 6) {
                    healthColor = ChatColor.YELLOW;
                } else if (health > 4) {
                    healthColor = ChatColor.GOLD;
                } else if (health > 1) {
                    healthColor = ChatColor.RED;
                } else {
                    healthColor = ChatColor.DARK_RED;
                }

                scores.add(ChatColor.WHITE + king.getDisplayName() + "&7 - " + healthColor + health + "\u2764");
                if (killTheKing.getLastSeenLoc() != null && killTheKing.getLastSeen() != null) {
                    scores.add(ChatColor.RED + killTheKing.getLastSeen() + ChatColor.GRAY + " (" + killTheKing.getLastSeenLoc().getBlockX() + ", " + killTheKing.getLastSeenLoc().getBlockZ() + ")");
                }
            }
        }

        if (killTheKing.getKing() == null) {
            scores.add("&eStarting in &6" + killTheKing.getPickTime() + "s");
            scores.add("&e/king participate");
        }
    }

    @Override
    public boolean isSetup() {
        return captureZone != null;
    }

    @Override
    public BasicDBObject serialize() {
        return new BasicDBObject("name", this.name)
                .append("capture-zone", captureZone == null ? null : captureZone.toDocument());
    }

    @Override
    public void deserialize(BasicDBObject object) {
        if (object.get("capture-zone") != null) {
            this.captureZone = new Cuboid(Document.parse(object.get("capture-zone").toString()));
        }
    }

    private void sendMessage() {
        if (captureTime % 30 == 0) {
            Team team = Brawl.getInstance().getTeamHandler().getPlayerTeam(cappingPlayer);
            if (team != null) {
                team.sendMessage(type.getPrefix() + "Your team is controlling " + ChatColor.LIGHT_PURPLE + name + ChatColor.WHITE + ".");
            } else {
                cappingPlayer.sendMessage(type.getPrefix() + "You are controlling " + ChatColor.LIGHT_PURPLE + name + ChatColor.WHITE + ".");
            }

            if (captureTime != defaultCaptureTime) {
                String capturing = captureTime < defaultCaptureTime - 30 ? CC.WHITE + cappingPlayer.getDisplayName() : CC.WHITE + "Someone";
                broadcast(defaultCaptureTime + capturing + ChatColor.YELLOW + " is trying to capture " + CC.LIGHT_PURPLE + name + CC.YELLOW + "." + CC.GRAY + " (" + getTimeLeft() + ")");
            }
        }

    }

    public Player getCappingPlayer() {
        if (this.cappingPlayer != null) {
            if (this.captureZone.contains(cappingPlayer.getLocation())) {
                return cappingPlayer;
            }
            if (captureTime < defaultCaptureTime - 30) {
                broadcast(ChatColor.YELLOW + "Control of " + ChatColor.LIGHT_PURPLE + name + ChatColor.YELLOW + " lost.");
            }
            Team team = Brawl.getInstance().getTeamHandler().getPlayerTeam(cappingPlayer);
            if (team != null) {
                team.sendMessage(type.getPrefix() + "Your team is no longer controlling " + ChatColor.LIGHT_PURPLE + name + ChatColor.WHITE + ".");
            } else {
                cappingPlayer.sendMessage(type.getPrefix() + "You are no longer controlling " + ChatColor.LIGHT_PURPLE + name + ChatColor.WHITE + ".");
            }
            setCappingPlayer(null);
        }
        return null;
    }

    public Player setCappingPlayer(Player player) {
        cappingPlayer = player;
        if (player == null) {
            if (captureTime != defaultCaptureTime) {
                captureTime = defaultCaptureTime;
            }
        } else {
            player.sendMessage(type.getPrefix() + "Attempting to control " + ChatColor.LIGHT_PURPLE+ name + ChatColor.WHITE + ".");
        }
        return player;
    }

    public String getTimeLeft() {
        return TimeUtils.formatIntoMMSS(captureTime);
    }
}
