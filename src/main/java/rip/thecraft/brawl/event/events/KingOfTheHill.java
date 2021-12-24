package rip.thecraft.brawl.event.events;

import com.mongodb.lang.Nullable;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.property.AbilityProperty;
import rip.thecraft.brawl.event.Event;
import rip.thecraft.brawl.event.EventType;
import rip.thecraft.brawl.team.Team;
import rip.thecraft.brawl.util.cuboid.Cuboid;
import rip.thecraft.falcon.staff.StaffMode;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.util.TimeUtils;

import java.util.List;

public class KingOfTheHill extends Event {

    /** Returns the default capture time */
    @Setter
    @AbilityProperty(id = "capture-time")
    public int defaultCaptureTime = 75;

    /** Returns the region of the capture zone */
    @Setter
    @AbilityProperty(id = "capture-zone")
    public Cuboid captureZone;

    /** Returns the current capture time (if active) */
    private transient Integer captureTime;

    /** Returns the current capturing player (if active) */
    @Nullable
    private transient Player cappingPlayer;

    public KingOfTheHill(String name) {
        super(name, EventType.KING_OF_THE_HILL);
    }

    @Override
    public void start() {
        captureTime = defaultCaptureTime;
        cappingPlayer = null;
    }

    @Override
    public void finish(Player winner) {
        if (winner != null) {
            broadcast(ChatColor.YELLOW + this.name + ChatColor.GRAY + " has been controlled by " + ChatColor.YELLOW + winner.getDisplayName() + ChatColor.GRAY + "!");
            winners.clear();
            winners.add(winner);
        }

        end();
    }

    @Override
    public void tick() {
        if (getCappingPlayer() != null) {
            sendMessage();
            captureTime--;
        } else {
            setCappingPlayer(captureZone.getPlayers().stream().filter(player -> !StaffMode.hasStaffMode(player)).findAny().orElse(null));
        }

        if (captureTime <= 0) {
            finish(cappingPlayer);
        }
    }
//⬅⬆⬇⬈⬉⬉⬊⬋⬌⬍
    @Override
    public void getScoreboard(Player player, List<String> entries) {
        super.getScoreboard(player, entries);
        entries.add(ChatColor.WHITE + name + ": " + ChatColor.YELLOW + getTimeLeft());
    }

    @Override
    public Location getLocation() {
        return captureZone.getCenter();
    }

    @Override
    public boolean isSetup() {
        return captureZone != null;
    }

    private void sendMessage() {
        if (captureTime % 15 == 0) {
            Team team = Brawl.getInstance().getTeamHandler().getPlayerTeam(cappingPlayer);
            if (team != null) {
                team.sendMessage(type.getPrefix() + "Your team is controlling " + ChatColor.WHITE + name + ChatColor.GRAY + ".");
            } else {
                cappingPlayer.sendMessage(type.getPrefix() + "You are now controlling " + ChatColor.YELLOW + name + ChatColor.GRAY + "." + CC.RED + " (" + getTimeLeft() + ")");
            }

            if (captureTime != defaultCaptureTime) {
                String capturing = captureTime < defaultCaptureTime - 30 ? CC.WHITE + cappingPlayer.getDisplayName() : CC.WHITE + "Someone";
                broadcast(capturing + ChatColor.GRAY + " is trying to capture " + CC.YELLOW + name + CC.GRAY + "." + CC.RED + " (" + getTimeLeft() + ")");
            }
        }

    }

    public Player getCappingPlayer() {
        if (this.cappingPlayer != null) {
            if (this.captureZone.contains(cappingPlayer.getLocation())) {
                return cappingPlayer;
            }
            if (captureTime < defaultCaptureTime - 15) {
                broadcast(ChatColor.GRAY + "Control of " + ChatColor.YELLOW + name + ChatColor.GRAY + " lost." + ChatColor.RED + " (" + getTimeLeft() + ")");
            }
            Team team = Brawl.getInstance().getTeamHandler().getPlayerTeam(cappingPlayer);
            if (team != null) {
                team.sendMessage(type.getPrefix() + "Your team is no longer controlling " + ChatColor.YELLOW + name + ChatColor.GRAY + "." + ChatColor.RED + " (" + getTimeLeft() + ")");
            } else {
                cappingPlayer.sendMessage(type.getPrefix() + "You are no longer controlling " + ChatColor.YELLOW + name + ChatColor.GRAY + "." + ChatColor.RED + " (" + getTimeLeft() + ")");
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
            player.sendMessage(type.getPrefix() + "Attempting to control " + ChatColor.YELLOW + name + ChatColor.GRAY + ".");
        }
        return player;
    }

    public String getTimeLeft() {
        return TimeUtils.formatIntoMMSS(captureTime);
    }
}
