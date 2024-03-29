package rip.thecraft.brawl.spawn.team.command.staff;

import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.spawn.team.Team;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.command.Command;

import java.util.ArrayList;
import java.util.List;

public class ForceDisbandAllCommand {

    @Command(names = "team admin forcedisbandall", permission = "op")
    public static void forceDisband(Player sender) {
        ConversationFactory factory = new ConversationFactory(Brawl.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {
            public String getPromptText(ConversationContext context) {
                return "§aAre you sure you want to disband all teams? Type §byes§a to confirm or §cno§a to quit.";
            }
            
            public Prompt acceptInput(ConversationContext ChatColor, String s) {
                if (s.equalsIgnoreCase("yes")) {
                    List<Team> teams = new ArrayList<>(Brawl.getInstance().getTeamHandler().getTeams());
                    for (Team team : teams) {
                        team.disband();
                    }
                    Brawl.getInstance().getTeamHandler().getTeamNameMap().clear();
                    Brawl.getInstance().getTeamHandler().getTeamUniqueIdMap().clear();
                    Brawl.getInstance().getTeamHandler().getUuidTeamMap().clear();
                    Brawl.getInstance().getServer().broadcastMessage(CC.RED + CC.BOLD + "All factions have been forcibly disbanded!");
                    return Prompt.END_OF_CONVERSATION;
                }
                if (s.equalsIgnoreCase("no")) {
                    ChatColor.getForWhom().sendRawMessage(CC.GREEN + "Disbanding cancelled.");
                    return Prompt.END_OF_CONVERSATION;
                }
                ChatColor.getForWhom().sendRawMessage(CC.GREEN + "Unrecognized response. Type §byes§a to confirm or §cno§a to quit.");
                return Prompt.END_OF_CONVERSATION;
            }
        }).withEscapeSequence("/no").withTimeout(10).thatExcludesNonPlayersWithMessage("Go away evil console!");
        final Conversation con = factory.buildConversation(sender);
        sender.beginConversation(con);
    }
}
