package rip.thecraft.brawl.game.team;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.server.item.type.InventoryType;
import rip.thecraft.brawl.util.VisibilityUtils;
import rip.thecraft.spartan.nametag.NametagHandler;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class GamePlayer {

    private final UUID uniqueId;
    private final String name;

    @Setter
    private boolean alive = false;

    @Setter
    private Location respawnTo;

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

    public void setExp(int level, float experience) {
        Player player = toPlayer();
        if (player != null) {
            player.setExp(experience);
            player.setLevel(level);
        }
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

        playerData.getLevel().updateExp(player);

        player.teleport(Brawl.getInstance().getLocationByName("SPAWN"));
        Brawl.getInstance().getItemHandler().apply(player, InventoryType.SPAWN);

        NametagHandler.reloadPlayer(player);
        NametagHandler.reloadOthersFor(player);
        VisibilityUtils.updateVisibility(player);
    }

    public String getDisplayName() {
        final Player player = this.toPlayer();

        return player == null ? this.getName() : player.getDisplayName();
    }

    public int getPing() {
        Player player = toPlayer();
        if (player == null) {
            return -1;
        }

        return ((CraftPlayer)player).getHandle().ping;
    }

}