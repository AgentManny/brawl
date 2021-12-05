package rip.thecraft.brawl.game.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.game.Game;
import rip.thecraft.brawl.game.GameType;
import rip.thecraft.brawl.game.menu.GameSelectorMenu;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.util.EconUtil;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.command.Command;
import rip.thecraft.spartan.util.TimeUtils;

import java.util.concurrent.TimeUnit;

public class HostCommand {

    @Command(names = { "host", "event", "game" }, description = "Host an event")
    public static void execute(Player sender) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(sender);
        if (playerData.isSpawnProtection()) new GameSelectorMenu().openMenu(sender);
        else {
            sender.sendMessage(ChatColor.RED + "You need to be in spawn to host events.");
        }
    }

    @Command(names = { "event host", "game host" }, description = "Host an event")
    public static void execute(Player player, GameType game) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        if (playerData.isSpawnProtection()) {
            if (Brawl.getInstance().getGameHandler().getMapHandler().getMaps(game).isEmpty()) {
                player.sendMessage(ChatColor.RED + "There aren't any maps available for this game.");
                return;
            }

            if (game.isDisabled()) {
                player.sendMessage(ChatColor.RED + "This game is currently disabled.");
                return;
            }

            boolean bypassRestrictions = player.hasPermission("brawl.game.bypass");
            long cooldown = Brawl.getInstance().getGameHandler().getCooldown().getOrDefault(game, 0L);
            if (!player.hasPermission("brawl.game.bypass") && System.currentTimeMillis() < cooldown) {
                player.sendMessage(ChatColor.RED + "This game is under cooldown for another " + TimeUtils.formatIntoDetailedString((int) TimeUnit.MILLISECONDS.toSeconds(cooldown - System.currentTimeMillis())) + ".");
                return;
            }

            if(playerData.hasGame(game)) {
                if(bypassRestrictions || EconUtil.canAfford(playerData, Game.HOST_CREDITS)) {
                    boolean start = Brawl.getInstance().getGameHandler().start(player, game);
                    if (!bypassRestrictions && start) {
                        EconUtil.withdraw(playerData, Game.HOST_CREDITS);
                        player.sendMessage(ChatColor.RED + " - " + ChatColor.BOLD + Game.HOST_CREDITS + ChatColor.RED + " Credits");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You need " + ChatColor.YELLOW + Game.HOST_CREDITS + " credits" + ChatColor.RED + " to host this game.");
                }
            } else {
                player.sendMessage(CC.RED  + "You don't have permission to use this game.");
            }
        } else {
            player.sendMessage(ChatColor.RED + "You need to be in spawn to host events.");
        }
    }

}