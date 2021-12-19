package rip.thecraft.brawl.event.koth;

import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.event.Event;
import rip.thecraft.brawl.event.EventType;
import rip.thecraft.brawl.levels.ExperienceType;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.team.Team;
import rip.thecraft.brawl.util.EconUtil;
import rip.thecraft.brawl.util.cuboid.Cuboid;
import rip.thecraft.brawl.util.imagemessage.ImageMessage;
import rip.thecraft.falcon.staff.StaffMode;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.util.TimeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
@Deprecated
public class KOTH extends Event {

    public static final String PREFIX = CC.DARK_PURPLE + "[ControlPoint] " + CC.WHITE;

    public static int DEFAULT_CAPTURE_TIME = 90;

    private final String name;

    @Setter
    private Cuboid captureZone;

    private int captureTime;
    private Player cappingPlayer;

    private BukkitTask task;

    public KOTH(String name) {
        super(name, EventType.KING_OF_THE_HILL);
        this.name = name;
    }

    public void start(Player host) {
        new ImageMessage("controlpoint").appendText(" ", " ", PREFIX, ChatColor.WHITE + name + " Event", ChatColor.YELLOW + "can be contested now.").broadcast();

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
        Brawl.getInstance().getEventHandler().setActiveEvent(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void finish(Player winner) {
        if (winner != null) {
            PlayerData data = Brawl.getInstance().getPlayerDataHandler().getPlayerData(winner);

            for (int i = 0; i < 4; i++) {
                Bukkit.broadcastMessage(" ");
            }
            int credits = Math.round(ThreadLocalRandom.current().nextInt(100, 200));
            Team team = Brawl.getInstance().getTeamHandler().getPlayerTeam(winner);
            String name = (team == null ? "" : ChatColor.GRAY + "[" + team.getDisplayName(winner) + ChatColor.GRAY + "]") + ChatColor.WHITE + winner.getDisplayName();
            Bukkit.broadcastMessage(PREFIX + ChatColor.LIGHT_PURPLE + this.name + ChatColor.YELLOW + " has been controlled by " + name + ChatColor.YELLOW + "!");
            data.getLevel().addExp(winner, ExperienceType.KOTH_CAPTURE.getExperience(), ExperienceType.KOTH_CAPTURE, this.name);
            EconUtil.deposit(data, credits);

            winner.sendMessage(ChatColor.YELLOW + "You were awarded " + ChatColor.LIGHT_PURPLE + credits + " credits" + ChatColor.YELLOW + " for capturing " + ChatColor.LIGHT_PURPLE + this.name + ChatColor.YELLOW + ".");
        }

        if (task != null) {
            task.cancel();
        }

        Brawl.getInstance().getEventHandler().setActiveEvent(null);
    }

    @Override
    public void tick() {

    }

    @Override
    public boolean isSetup() {
        return false;
    }

    public List<String> getScoreboard(Player player) {
        List<String> lines = new ArrayList<>();
        lines.add(CC.WHITE + this.name + ": " + CC.RED + getTimeLeft());
        String capturing = captureTime < DEFAULT_CAPTURE_TIME - 30 ? CC.WHITE + cappingPlayer.getDisplayName() : CC.RED + "Unknown";
        lines.add(CC.LIGHT_PURPLE + " ● " + CC.WHITE + capturing);
        lines.add(ChatColor.YELLOW + " ");
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
    public Document serialize() {
        return new Document("name", this.name)
                .append("capture-zone", captureZone == null ? null : captureZone.toDocument());
    }

    public void deserialize(Document object) {
        if (object.get("capture-zone") != null) {
            this.captureZone = new Cuboid(Document.parse(object.get("capture-zone").toString()));
        }
    }
}
