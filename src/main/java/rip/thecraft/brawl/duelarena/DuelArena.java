package rip.thecraft.brawl.duelarena;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.server.item.type.InventoryType;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.player.PlayerState;
import rip.thecraft.brawl.spectator.SpectatorMode;
import rip.thecraft.brawl.util.location.LocationType;
import rip.thecraft.spartan.nametag.NametagHandler;

public class DuelArena {

    public static boolean DISABLED = false; // Todo make disabling core features configurable in game in case there is a bug

    public static void join(Player player) {
        if (DISABLED) {
            player.sendMessage(ChatColor.RED + "Duel arena is currently disabled. Please try again later.");
//            player.sendMessage(ChatColor.GRAY + "Note: We are working hard to ensure core gameplay is perfected before we re-enable Duel Arenas.");
            return;
        }
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        if (playerData.isDuelArena()) {
            player.sendMessage(ChatColor.RED + "You are already in the duel arena.");
            return;
        }

        if (playerData.isSpectating()) {
            SpectatorMode spectator = Brawl.getInstance().getSpectatorManager().getSpectator(player);
            spectator.spectate(SpectatorMode.SpectatorType.DUEL_ARENA);
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

        Location location = LocationType.ARENA.getLocation();
        int yaw = (Brawl.RANDOM.nextInt(4) - 2) * 90;
        if (yaw != 0) {
            location.setYaw(yaw);
        }
        player.teleport(location);
    }

    public static void respawn(Player player, boolean teleport) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        if (playerData.isSpawnProtection()) { // Hot fix LMFAO
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
        }

        if (teleport) {
            player.teleport(Brawl.getInstance().getLocationByName("DUEL_ARENA"));
        }
        NametagHandler.reloadPlayer(player);
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

        Brawl.getInstance().getMatchHandler().leaveQueue(player);

        playerData.setSpawnProtection(true);
        playerData.setDuelArena(false);
        if (!player.hasMetadata("staffmode")) {
            Brawl.getInstance().getItemHandler().apply(player, InventoryType.SPAWN);
            player.getInventory().setHeldItemSlot(0);
        }
        player.teleport(Brawl.getInstance().getLocationByName("SPAWN"));
    }

}
