package rip.thecraft.brawl.duelarena;

import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.item.type.InventoryType;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.player.PlayerState;
import rip.thecraft.spartan.nametag.NametagHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DuelArena {

    public static void join(Player player) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        if (playerData.isDuelArena()) {
            player.sendMessage(ChatColor.RED + "You are already in the duel arena.");
            return;
        }

        if (!playerData.isSpawnProtection()) {
            player.sendMessage(ChatColor.RED + "You must have spawn protection to warp to duel arena.");
            return;
        }

        playerData.setSpawnProtection(false);
        playerData.setDuelArena(true);
        if (playerData.getSelectedKit() != null) {
            playerData.setPreviousKit(playerData.getSelectedKit());
            playerData.setSelectedKit(null);
        }

        player.setMaxHealth(20.0D);
        player.setHealth(20.0D);

        if (!player.hasMetadata("staffmode")) {
            Brawl.getInstance().getItemHandler().apply(player, InventoryType.ARENA);
            playerData.getQueueData().updateQuickQueue(player);
            player.getInventory().setHeldItemSlot(0);
            NametagHandler.reloadPlayer(player);
            NametagHandler.reloadOthersFor(player);
        }

        player.teleport(Brawl.getInstance().getLocationByName("DUEL_ARENA"));
    }

    public static void respawn(Player player, boolean teleport) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);

        playerData.setSpawnProtection(false);
        playerData.setDuelArena(true);

        if (playerData.getSelectedKit() != null) {
            playerData.setPreviousKit(playerData.getSelectedKit());
            playerData.setSelectedKit(null);
        }

        player.setMaxHealth(20.0D);
        player.setHealth(20.0D);

        if (!player.hasMetadata("staffmode")) {
            Brawl.getInstance().getItemHandler().apply(player, InventoryType.ARENA);
            playerData.getQueueData().updateQuickQueue(player);
            player.getInventory().setHeldItemSlot(0);
        }

        if (teleport) {
            player.teleport(Brawl.getInstance().getLocationByName("DUEL_ARENA"));
        }
    }

    public static void leave(Player player) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        if (!playerData.isDuelArena() || playerData.isSpawnProtection()) {
            player.sendMessage(ChatColor.RED + "You aren't in the duel arena.");
            return;
        }

        if (playerData.getPlayerState() == PlayerState.MATCH) {
            player.sendMessage(ChatColor.RED + "You cannot warp while in a match.");
            return;
        }

        playerData.setSpawnProtection(true);
        playerData.setDuelArena(false);
        if (!player.hasMetadata("staffmode")) {
            Brawl.getInstance().getItemHandler().apply(player, InventoryType.SPAWN);
            player.getInventory().setHeldItemSlot(0);
        }
        player.teleport(Brawl.getInstance().getLocationByName("SPAWN"));
    }

}