package rip.thecraft.brawl.spawn.killstreak;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.spawn.killstreak.type.*;
import rip.thecraft.server.CraftServer;
import rip.thecraft.server.handler.MovementHandler;
import rip.thecraft.server.handler.PacketHandler;

import java.util.HashMap;
import java.util.Map;

public class KillstreakHandler {

    private final Brawl plugin;

    @Getter
    private Map<String, Killstreak> killstreaks = new HashMap<>();

    @Getter
    private Map<Integer, Killstreak> streaks = new HashMap<>();

    public KillstreakHandler(Brawl plugin) {
        this.plugin = plugin;

        register(
                new GoldenApples(), // 5
                new FullRepair(), // 10
                new RegenV(), // 15
                new HorseSummoner(), // 20
                new AttackDogs(), // 25
                new Nuke() // 50
        );
    }

    private void register(Killstreak... killstreaks) {
        for (Killstreak killstreak : killstreaks) {
            this.killstreaks.put(killstreak.getName(), killstreak);

            for (int tracker : killstreak.getKills()) {
                streaks.put(tracker, killstreak);
            }

            if (killstreak instanceof Listener) {
                plugin.getServer().getPluginManager().registerEvents((Listener) killstreak, plugin);
            }

            if (killstreak instanceof MovementHandler) {
                CraftServer.getInstance().addMovementHandler((MovementHandler) killstreak);
            }

            if (killstreak instanceof PacketHandler) {
                CraftServer.getInstance().addPacketHandler((PacketHandler) killstreak);
            }
        }
    }

    public boolean hasKillstreakItem(Player player) {
        for (Killstreak killstreak : killstreaks.values()) {
            ItemStack icon = killstreak.getIcon();
            if (icon != null) {
                for (ItemStack itemStack : player.getInventory()) {
                    if (itemStack != null && itemStack.isSimilar(icon)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Killstreak getKillstreakByName(String name) {
        return this.killstreaks.get(name);
    }
}
