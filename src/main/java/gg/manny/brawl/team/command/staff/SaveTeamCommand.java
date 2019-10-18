package gg.manny.brawl.team.command.staff;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import gg.manny.brawl.Brawl;
import gg.manny.brawl.team.Team;
import gg.manny.quantum.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SaveTeamCommand {

    @Command(names = { "saveteam"}, permission = "op")
    public void execute(Player sender, Team team) {
        Brawl.getInstance().getTeamHandler().getCollection().replaceOne(Filters.eq("_id", team.getUniqueId()), team.serialize(), new ReplaceOptions().upsert(true));
        sender.sendMessage(ChatColor.GREEN + "Saved team " + team.getName() + " to mongo.");
    }

}
