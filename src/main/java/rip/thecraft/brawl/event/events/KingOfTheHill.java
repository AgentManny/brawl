package rip.thecraft.brawl.event.events;

import com.mongodb.lang.Nullable;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
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
import java.util.concurrent.TimeUnit;

public class KingOfTheHill extends Event {

    /** Returns the default capture time */
    @NonNull @Setter private int defaultCaptureTime = (int) TimeUnit.MINUTES.toSeconds(3);

    /** Returns the region of the capture zone */
    @Setter @AbilityProperty(id = "capture-zone") public Cuboid captureZone;

    /** Returns the current capture time (if active) */
    private transient int captureTime;

    /** Returns the current capturing player (if active) */
    @Nullable private transient Player cappingPlayer;

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
            for (int i = 0; i < 4; i++) {
                Bukkit.broadcastMessage(" ");
            }
            broadcast(ChatColor.LIGHT_PURPLE + this.name + ChatColor.YELLOW + " has been controlled by " + name + ChatColor.YELLOW + "!");
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

    @Override
    public void getScoreboard(Player player, List<String> entries) {
        entries.add(CC.GOLD + CC.SCOREBAORD_SEPARATOR);
        entries.add(CC.GOLD + CC.BOLD + this.name + ": " + CC.YELLOW + getTimeLeft());
        String capturing = captureTime < defaultCaptureTime - 30 ? CC.WHITE + cappingPlayer.getDisplayName() : CC.GRAY + "???";
        entries.add(CC.GOLD + "Capturing: " + CC.YELLOW + capturing);
    }

    @Override
    public boolean isSetup() {
        return captureZone != null;
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
