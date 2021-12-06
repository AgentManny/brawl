package rip.thecraft.brawl.item.type;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.duelarena.DuelArena;
import rip.thecraft.brawl.duelarena.match.queue.QueueType;
import rip.thecraft.brawl.duelarena.menu.LoadoutMenu;
import rip.thecraft.brawl.game.lobby.GameLobby;
import rip.thecraft.brawl.game.menu.GameSelectorMenu;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.kit.menu.KitSelectorMenu;
import rip.thecraft.brawl.leaderboard.menu.LeaderboardEloMenu;
import rip.thecraft.brawl.leaderboard.menu.LeaderboardMenu;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.spectator.SpectatorMode;
import rip.thecraft.brawl.spectator.menu.SpectatorPlayerMenu;
import rip.thecraft.server.util.chatcolor.CC;

import java.util.function.BiConsumer;

public enum MetadataType {

    KIT_SELECTOR((player, data) -> {
        new KitSelectorMenu().openMenu(player);
    }),
    PREVIOUS_KIT((player, playerData) -> {
        Kit kit = playerData.getPreviousKit() == null ? Brawl.getInstance().getKitHandler().getDefaultKit() : playerData.getPreviousKit();
        if(!kit.isEnabled()){
            player.sendMessage(ChatColor.RED + "This kit is currently disabled.");
            return;
        }

        if(playerData.hasKit(kit)){
            kit.apply(player, true, true);
        }else{
            Brawl.getInstance().getKitHandler().getDefaultKit().apply(player, true, true);
        }
    }),

    EVENT_SELECTOR((player, playerData) -> {
        new GameSelectorMenu().openMenu(player);
    }),
    EVENT_LEAVE((player, playerData) -> {
        GameLobby lobby = Brawl.getInstance().getGameHandler().getLobby();
        if (lobby != null && lobby.getPlayers().contains(player.getUniqueId())) {
            lobby.leave(player.getUniqueId());
            player.sendMessage(ChatColor.RED + "You have left the event.");
        }
    }),

    EVENT_VOTE,
    EVENT_VOTE_SELECTED((player, playerData) -> {
        player.sendMessage(ChatColor.RED + "You've already voted for this map.");
        player.updateInventory();
    }),

    SHOP((player, playerData) -> {
        player.sendMessage(ChatColor.RED + "Our shop system is still undergoing work. Please try again later");
    }),

    DUEL_ARENA((player, playerData) -> {
        DuelArena.join(player);
    }),
    DUEL_ARENA_LEAVE((player, playerData) -> {
        DuelArena.leave(player);
    }),
    DUEL_ARENA_RANKED((player, playerData) -> {
        new LoadoutMenu(QueueType.RANKED).openMenu(player);
    }),
    DUEL_ARENA_UNRANKED((player, playerData) -> {
        new LoadoutMenu(QueueType.UNRANKED).openMenu(player);
    }),
    DUEL_ARENA_QUICK_QUEUE((player, playerData) -> {
        Brawl.getInstance().getMatchHandler().joinQuickQueue(player);
    }),

    DUEL_ARENA_DUEL,

    QUEUE_LEAVE((player, playerData) -> {
        Brawl.getInstance().getMatchHandler().leaveQueue(player);
    }),

    SPECTATE_GAME((player, playerData) -> {
        SpectatorMode spectator = Brawl.getInstance().getSpectatorManager().getSpectator(player);
        if (spectator != null) { // Make sure they are actually spectating
            spectator.spectate(SpectatorMode.SpectatorType.GAME);
        }
    }),

    SPECTATE_ARENA((player, playerData) -> {
        SpectatorMode spectator = Brawl.getInstance().getSpectatorManager().getSpectator(player);
        if (spectator != null) { // Make sure they are actually spectating
            spectator.spectate(SpectatorMode.SpectatorType.DUEL_ARENA);
        }
    }),

    SPECTATE_SPAWN((player, playerData) -> {
        SpectatorMode spectator = Brawl.getInstance().getSpectatorManager().getSpectator(player);
        if (spectator != null) { // Make sure they are actually spectating
            spectator.spectate(SpectatorMode.SpectatorType.SPAWN);
        }
    }),

    SPECTATOR_PLAYER_MENU((player, playerData) -> {
        new SpectatorPlayerMenu().openMenu(player);
    }),
    SPECTATOR_LEAVE((player, playerData) -> {
        Brawl.getInstance().getSpectatorManager().removeSpectator(player);
    }),

    SPECTATOR_JOIN((player, playerData) -> {
        if (player.hasPermission("brawl.spectate")) {
            Brawl.getInstance().getSpectatorManager().addSpectator(player);
        } else {
            player.sendMessage(CC.RED + "You must have a donator rank to spectate.");
        }
    }),


    LEADERBOARDS((player, playerData) -> {
        new LeaderboardMenu().openMenu(player);
    }),
    LEADERBOARDS_ELO((player, playerData) -> {
        new LeaderboardEloMenu().openMenu(player);
    }),

    DISABLED;

    @Getter
    private boolean cancellable;

    @Getter
    private BiConsumer<Player, PlayerData> activate;

    MetadataType() {
        this.activate = null;
        this.cancellable = true;
    }

    MetadataType(BiConsumer<Player, PlayerData> activate) {
        this.activate = activate;
        this.cancellable = true;
    }

    MetadataType(BiConsumer<Player, PlayerData> activate, boolean cancellable) {
        this.activate = activate;
        this.cancellable = cancellable;
    }

    public String toMetadata() {
        return this.name();
    }

    public static MetadataType fromMetadata(String source) {
        return MetadataType.valueOf(source.toUpperCase());
    }

    public static boolean isMetadata(String source) {
        MetadataType type;
        try {
            type = fromMetadata(source);
        } catch (Exception ignored) {
            return false;
        }
        return type != null;
    }

}
