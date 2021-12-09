package rip.thecraft.brawl.scoreboard.type;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.scoreboard.ScoreboardProvider;
import rip.thecraft.falcon.Falcon;
import rip.thecraft.falcon.chat.ChatHandler;
import rip.thecraft.falcon.profile.Profile;
import rip.thecraft.falcon.profile.data.ProfileData;
import rip.thecraft.falcon.staff.StaffMode;
import rip.thecraft.falcon.util.BukkitReflection;
import rip.thecraft.server.util.chatcolor.CC;

import java.util.List;

public class StaffModeScoreboardProvider implements ScoreboardProvider {

    @Override
    public List<String> getLines(Player player, PlayerData playerData, List<String> lines) {
        Profile profile = Falcon.getInstance().getProfileHandler().getByPlayer(player);
        ProfileData profileData = profile.getProfileData();
        lines.add(CC.GRAY + CC.SCOREBAORD_SEPARATOR);
        lines.add(ChatColor.WHITE + "Players: " + ChatColor.LIGHT_PURPLE + Bukkit.getOnlinePlayers().size());
        lines.add(ChatColor.WHITE + "Status: " + (StaffMode.hasStaffMode(player) && StaffMode.getStaffModeMap().get(player.getUniqueId()).isHidden() ? ChatColor.RED + "Hidden" : ChatColor.GREEN + "Visible"));
        ChatHandler chatHandler = Falcon.getInstance().getChatHandler();
        boolean chatMuted = chatHandler.isChatMuted();
        boolean chatSlowed = chatHandler.getChatDelay() > 0;
        lines.add(ChatColor.WHITE + "Chat: " + ChatColor.LIGHT_PURPLE + (profileData.isStaffChat() ? "Staff" : chatMuted ? "Muted" : chatSlowed ? "Slowed (" + chatHandler.getChatDelay() + "s" + ")" : "Regular"));
        lines.add(ChatColor.WHITE + "Ping: " + ChatColor.LIGHT_PURPLE + BukkitReflection.getPing(player) + "ms");
        lines.add(ChatColor.WHITE + "TPS: " + ChatColor.LIGHT_PURPLE + format(Bukkit.spigot().getTPS()[0]));
        lines.add(CC.WHITE + CC.SCOREBAORD_SEPARATOR);
        return lines;
    }

    private static String format(double tps) {
        return (tps > 18.0D ? ChatColor.GREEN : (tps > 16.0D ? ChatColor.YELLOW : ChatColor.RED)).toString() + (tps >= 20.0D ? "*" : "") + Math.min((double)Math.round(tps * 100.0D) / 100.0D, 20.0D);
    }


}