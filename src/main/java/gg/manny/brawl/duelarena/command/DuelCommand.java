package gg.manny.brawl.duelarena.command;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.duelarena.match.invite.PlayerMatchInvite;
import gg.manny.brawl.duelarena.menu.DuelMenu;
import gg.manny.brawl.player.PlayerData;
import gg.manny.brawl.player.PlayerState;
import gg.manny.quantum.command.Command;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class DuelCommand {

    private final Brawl plugin;

    @Command(names = "toggleduels")
    public void toggle(Player sender) {
        PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(sender);

        boolean newValue = !playerData.isDuelsEnabled();
        playerData.setDuelsEnabled(newValue);

        sender.sendMessage((newValue ? ChatColor.GREEN : ChatColor.RED) + "Players are " + (newValue ? "now" : "no longer") + " able to send duel requests.");
    }

    @Command(names = "accept")
    public void accept(Player sender, Player target) {
        if (sender == target) {
            sender.sendMessage(ChatColor.RED + "Nope! You cannot duel yourself.");
            return;
        }

        PlayerMatchInvite pmi = Brawl.getInstance().getMatchHandler().getPlayerInvite(target.getUniqueId(), sender.getUniqueId());
        if (pmi != null) {
            plugin.getMatchHandler().acceptInvitation(pmi);
        } else {
            sender.sendMessage(ChatColor.RED + "You don't have any duel requests from " + target.getName() + ".");
        }
    }

    @Command(names = "duel")
    public void duel(Player sender, Player target) {
        if(sender == target) {
            sender.sendMessage(ChatColor.RED + "Nope! You cannot duel yourself.");
            return;
        }
        PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(sender);
        if (playerData.getPlayerState() != PlayerState.ARENA) {
            sender.sendMessage(ChatColor.RED + "You must be in the Duel Arena to send duel requests.");
            return;
        }

        PlayerData targetData = plugin.getPlayerDataHandler().getPlayerData(target);
        if (targetData.getPlayerState() == PlayerState.ARENA) {
            new DuelMenu(plugin, target).openMenu(sender);
        } else {
            sender.sendMessage(target.getDisplayName() + ChatColor.RED + " is not in the duel arena.");
        }
    }
}
