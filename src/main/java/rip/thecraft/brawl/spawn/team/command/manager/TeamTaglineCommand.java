package rip.thecraft.brawl.spawn.team.command.manager;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.spawn.team.Team;
import rip.thecraft.spartan.command.Command;
import rip.thecraft.spartan.nametag.NametagHandler;

import java.util.regex.Pattern;

public class TeamTaglineCommand {

    public final static Pattern ALPHA_NUMERIC = Pattern.compile("[^a-zA-Z0-9&]");

    @Command(names = {"team tagline", "t tagline", "f tagline", "faction tagline", "fac tagline", "team tag", "t tag", "f tag", "faction tag", "fac tag"})
    public static void setTagline(Player sender, String tagline) {
        Team team = Brawl.getInstance().getTeamHandler().getPlayerTeam(sender.getUniqueId());
        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You're not in a team!");
            return;
        }

        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You haven't unlocked taglines!");
            return;
        }

        if (ALPHA_NUMERIC.matcher(tagline).find()) {
            sender.sendMessage(ChatColor.RED + "Taglines must be alphanumeric!");
            return;
        }

        if (tagline.length() > 5) {
            sender.sendMessage(ChatColor.RED + "Maximum tagline size is 5 characters!");
            return;
        }

        // Add cooldown for spam like 10m and cost credits to update it

        if (tagline.equalsIgnoreCase("clear") || tagline.equalsIgnoreCase("remove") || tagline.equalsIgnoreCase("reset")) {
            team.setTagline(null);
            team.sendMessage(ChatColor.DARK_AQUA + sender.getName() + " has cleared the team's tagline.");
            return;
        }



        team.setTagline(tagline);
        team.sendMessage(ChatColor.DARK_AQUA + sender.getName() + " has updated the team's tagline:");
        team.sendMessage(ChatColor.GRAY + team.getDisplayTagline());
        for (Player player : team.getOnlineMembers()) {
            NametagHandler.reloadOthersFor(player);
        }
    }
}
