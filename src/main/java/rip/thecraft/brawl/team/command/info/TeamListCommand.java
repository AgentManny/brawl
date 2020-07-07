package rip.thecraft.brawl.team.command.info;

import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.team.Team;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.command.Command;
import rip.thecraft.spartan.command.Param;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class TeamListCommand {

    @Command(names = { "team list", "t list", "f list", "faction list", "fac list" })
    public static void viewTeams(Player sender,@Param(defaultValue = "1") int page) {
        if (page < 1) {
            sender.sendMessage(ChatColor.RED + "You cannot view a page less than 1");
            return;
        }
        
        HashMap<Team, Integer> teamPlayerCount = new HashMap<>();
        for (Player player : Brawl.getInstance().getServer().getOnlinePlayers()) {
            if (!player.hasMetadata("hidden")) {
                Team playerTeam = Brawl.getInstance().getTeamHandler().getPlayerTeam(player);
                if (playerTeam != null) {
                    if (teamPlayerCount.containsKey(playerTeam)) {
                        teamPlayerCount.put(playerTeam, teamPlayerCount.get(playerTeam) + 1);
                    } else {
                        teamPlayerCount.put(playerTeam, 1);
                    }
                }
            }
        }
        
        int maxPages = teamPlayerCount.size() / 10;
        ++maxPages;
        if (page > maxPages) {
            page = maxPages;
        }
        LinkedHashMap<Team, Integer> sortedTeamPlayerCount = sortByValues(teamPlayerCount);
        int start = (page - 1) * 10;
        int index = 0;
        sender.sendMessage(CC.GRAY + "*** " + ChatColor.DARK_AQUA + "Team List " + page + "/" + maxPages + CC.GRAY + " ***");
        for (Map.Entry<Team, Integer> teamEntry : sortedTeamPlayerCount.entrySet()) {
            if (++index < start) {
                continue;
            }
            if (index > start + 10) {
                break;
            }

            Team team = teamEntry.getKey();
            String teamName = (team.isMember(sender) ? CC.DARK_AQUA : CC.GRAY) + team.getName();

            new FancyMessage(ChatColor.DARK_AQUA.toString() + index + ". ")
                    .then(teamName).tooltip(ChatColor.GREEN + "Click to view team info"
                    )
                    .command("/team info " + team.getName())
                    .then(ChatColor.GRAY + " (" + teamEntry.getValue() + "/" + teamEntry.getKey().getSize() + ")")
                    .send(sender);
        }
    }

    public static LinkedHashMap<Team, Integer> sortByValues(HashMap<Team, Integer> map) {
        LinkedList<Map.Entry<Team, Integer>> list = new LinkedList<>(map.entrySet());
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        LinkedHashMap<Team, Integer> sortedHashMap = new LinkedHashMap<>();
        for (Map.Entry<Team, Integer> entry : list) {
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }


}
