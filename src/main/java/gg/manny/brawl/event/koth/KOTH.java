package gg.manny.brawl.event.koth;

import com.mongodb.BasicDBObject;
import gg.manny.brawl.Brawl;
import gg.manny.brawl.event.Event;
import gg.manny.brawl.player.statistic.StatisticType;
import gg.manny.brawl.team.Team;
import gg.manny.brawl.util.cuboid.Cuboid;
import gg.manny.brawl.util.imagemessage.ImageMessage;
import gg.manny.pivot.staff.StaffMode;
import gg.manny.pivot.util.TimeUtils;
import gg.manny.pivot.util.chatcolor.CC;
import lombok.Data;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Data
public class KOTH extends Event {

    public static final String PREFIX = CC.DARK_PURPLE + "[KingOfTheHill] " + CC.WHITE;

    public static int DEFAULT_CAPTURE_TIME = (int) TimeUnit.MINUTES.toSeconds(3);

    private final String name;

    @Setter
    private Cuboid captureZone;

    private int captureTime;
    private Player cappingPlayer;

    private BukkitTask task;

    public KOTH(String name) {
        this.name = name;
    }

    @Override
    public void start(Player host) {
        new ImageMessage("koth").appendText(" ", " ", PREFIX, ChatColor.WHITE + name + " KOTH", ChatColor.YELLOW + "can be contested now.").broadcast();

        if (captureZone == null) {
            finish(null);
            return;
        }

        captureTime = DEFAULT_CAPTURE_TIME;
        task = new BukkitRunnable() {
            @Override
            public void run() {
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
        }.runTaskTimer(Brawl.getInstance(), 20L, 20L);
        Brawl.getInstance().getEventHandler().setActiveKOTH(this);
    }

    @Override
    public void finish(Player winner) {
        if (winner != null) {

            for (int i = 0; i < 4; i++) {
                Bukkit.broadcastMessage(" ");
            }
            int credits = Math.round(ThreadLocalRandom.current().nextInt(100, 500));
            Team team = Brawl.getInstance().getTeamHandler().getPlayerTeam(winner);
            String name = (team == null ? "" : ChatColor.GRAY + "[" + team.getDisplayName(winner) + ChatColor.GRAY + "]") + ChatColor.WHITE + winner.getDisplayName();
            Bukkit.broadcastMessage(PREFIX + ChatColor.LIGHT_PURPLE + this.name + ChatColor.YELLOW + " has been controlled by " + name + ChatColor.YELLOW + "!");
            Bukkit.broadcastMessage(PREFIX + CC.YELLOW + "Awarded " + ChatColor.YELLOW + credits + " credits" + ChatColor.YELLOW + " to " + name + ChatColor.YELLOW + ".");
            Brawl.getInstance().getPlayerDataHandler().getPlayerData(winner).getStatistic().add(StatisticType.CREDITS, credits);
        }

        if (task != null) {
            task.cancel();
        }

        Brawl.getInstance().getEventHandler().setActiveKOTH(null);
    }

    public List<String> getScoreboard(Player player) {
        List<String> lines = new ArrayList<>();
        lines.add(CC.GOLD + CC.SCOREBAORD_SEPARATOR);
        lines.add(CC.GOLD + CC.BOLD + this.name + ": " + CC.YELLOW + getTimeLeft());
        String capturing = captureTime < DEFAULT_CAPTURE_TIME - 30 ? CC.WHITE + cappingPlayer.getDisplayName() : CC.GRAY + "???";
        lines.add(CC.GOLD + "Capturing: " + CC.YELLOW + capturing);
        return lines;
    }

    private void sendMessage() {
        if (captureTime % 30 == 0) {
            Team team = Brawl.getInstance().getTeamHandler().getPlayerTeam(cappingPlayer);
            if (team != null) {
                team.sendMessage(PREFIX + "Your team is controlling " + ChatColor.LIGHT_PURPLE + name + ChatColor.WHITE + ".");
            } else {
                cappingPlayer.sendMessage(PREFIX + "You are controlling " + ChatColor.LIGHT_PURPLE + name + ChatColor.WHITE + ".");
            }

            if (captureTime != DEFAULT_CAPTURE_TIME) {
                String capturing = captureTime < DEFAULT_CAPTURE_TIME - 30 ? CC.WHITE + cappingPlayer.getDisplayName() : CC.WHITE + "Someone";
                Bukkit.broadcastMessage(PREFIX + capturing + ChatColor.YELLOW + " is trying to capture " + CC.LIGHT_PURPLE + name + CC.YELLOW + "." + CC.GRAY + " (" + getTimeLeft() + ")");
            }
        }

    }

    public Player getCappingPlayer() {
        if (this.cappingPlayer != null) {
            if (this.captureZone.contains(cappingPlayer.getLocation())) {
                return cappingPlayer;
            }
            if (captureTime < DEFAULT_CAPTURE_TIME - 30) {
                Bukkit.broadcastMessage(PREFIX + ChatColor.YELLOW + "Control of " + ChatColor.LIGHT_PURPLE + name + ChatColor.YELLOW + " lost.");
            }
            Team team = Brawl.getInstance().getTeamHandler().getPlayerTeam(cappingPlayer);
            if (team != null) {
                team.sendMessage(PREFIX + "Your team is no longer controlling " + ChatColor.LIGHT_PURPLE + name + ChatColor.WHITE + ".");
            } else {
                cappingPlayer.sendMessage(PREFIX + "You are no longer controlling " + ChatColor.LIGHT_PURPLE + name + ChatColor.WHITE + ".");
            }
            setCappingPlayer(null);
        }
        return null;
    }

    public Player setCappingPlayer(Player player) {
        cappingPlayer = player;
        if (player == null) {
            if (captureTime != DEFAULT_CAPTURE_TIME) {
                captureTime = DEFAULT_CAPTURE_TIME;
            }
        } else {
            player.sendMessage(PREFIX + "Attempting to control " + ChatColor.LIGHT_PURPLE+ name + ChatColor.YELLOW + ".");
        }
        return player;
    }

    public String getTimeLeft() {
        return TimeUtils.formatIntoMMSS(captureTime);
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
}
