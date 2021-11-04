package rip.thecraft.brawl.duelarena.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.duelarena.match.invite.PlayerMatchInvite;
import rip.thecraft.brawl.duelarena.menu.DuelMenu;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.player.PlayerState;
import rip.thecraft.spartan.command.Command;

public class DuelCommand {

    @Command(names = "toggleduels")
    public static void toggle(Player sender) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(sender);

        boolean newValue = !playerData.isDuelsEnabled();
        playerData.setDuelsEnabled(newValue);

        sender.sendMessage((newValue ? ChatColor.GREEN : ChatColor.RED) + "Players are " + (newValue ? "now" : "no longer") + " able to send duel requests.");
    }

    @Command(names = "accept")
    public static void accept(Player sender, Player target) {
        if (sender == target) {
            sender.sendMessage(ChatColor.RED + "Nope! You cannot duel yourself.");
            return;
        }

        PlayerMatchInvite pmi = Brawl.getInstance().getMatchHandler().getPlayerInvite(target.getUniqueId(), sender.getUniqueId());
        if (pmi != null) {
            Brawl.getInstance().getMatchHandler().acceptInvitation(pmi);
        } else {
            sender.sendMessage(ChatColor.RED + "You don't have any duel requests from " + target.getName() + ".");
        }
    }

    @Command(names = "duel")
    public static void duel(Player sender, Player target) {
        if(sender == target) {
            sender.sendMessage(ChatColor.RED + "Nope! You cannot duel yourself.");
            return;
        }
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(sender);
        if (playerData.getPlayerState() != PlayerState.ARENA) {
            sender.sendMessage(ChatColor.RED + "You must be in the Duel Arena to send duel requests.");
            return;
        }

        PlayerData targetData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(target);
        if (targetData.getPlayerState() == PlayerState.ARENA) {
            PlayerMatchInvite pmi = Brawl.getInstance().getMatchHandler().getPlayerInvite(target.getUniqueId(), sender.getUniqueId());
            if (pmi != null) {
                Brawl.getInstance().getMatchHandler().acceptInvitation(pmi);
                return;
            }
            new DuelMenu(target).openMenu(sender);
        } else {
            sender.sendMessage(target.getDisplayName() + ChatColor.RED + " is not in the duel arena.");
        }
    }
}
