package rip.thecraft.brawl.team.command.staff;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.team.Team;
import rip.thecraft.spartan.command.Command;

public class SaveTeamCommand {

    @Command(names = { "saveteam"}, permission = "op")
    public void execute(Player sender, Team team) {
        Brawl.getInstance().getTeamHandler().getCollection().replaceOne(Filters.eq("_id", team._id), Team.getAsDocument(team), new ReplaceOptions().upsert(true));
        sender.sendMessage(ChatColor.GREEN + "Saved team " + team.getName() + " to mongo.");
    }

}