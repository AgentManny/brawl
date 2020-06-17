package rip.thecraft.brawl.game.team;

import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.item.type.InventoryType;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.player.cps.ClickTracker;
import rip.thecraft.spartan.nametag.NametagHandler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class GamePlayer {

    private final UUID uniqueId;
    private final String name;

    @Setter
    private boolean alive = false;

    public GamePlayer(Player player) {
        this.uniqueId = player.getUniqueId();
        this.name = player.getName();
    }

    public Player toPlayer() {
        return Bukkit.getPlayer(this.getUniqueId());
    }

    public PlayerData toPlayerData() {
        return Brawl.getInstance().getPlayerDataHandler().getPlayerData(this.uniqueId);
    }

    public void spawn() {
        Player player = toPlayer();
        if (player == null) return;

        PlayerData playerData = toPlayerData();

        player.setMaxHealth(20.0D);
        player.setHealth(20.0D);

        player.setAllowFlight(false);
        player.setFlying(false);

        playerData.getSpawnData().getDamageReceived().clear();
        playerData.setCombatTaggedTil(-1);

        playerData.setEvent(false);
        playerData.setSpawnProtection(true);

        player.teleport(Brawl.getInstance().getLocationByName("SPAWN"));
        Brawl.getInstance().getItemHandler().apply(player, InventoryType.SPAWN);

        NametagHandler.reloadPlayer(player);
        NametagHandler.reloadOthersFor(player);
    }

    public String getDisplayName() {
        final Player player = this.toPlayer();

        return player == null ? this.getName() : player.getDisplayName();
    }

    public int getCPS() {
        Player player = toPlayer();
        if (player == null) {
            return 0;
        }

        return ClickTracker.getCPS(player);
    }

    public int getPing() {
        Player player = toPlayer();
        if (player == null) {
            return -1;
        }

        return player.getPing();
    }

}