package gg.manny.brawl.killstreak;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.killstreak.type.*;
import gg.manny.server.MineServer;
import gg.manny.server.handler.PacketHandler;
import gg.manny.server.handler.SimpleMovementHandler;
import lombok.Getter;
import org.bukkit.event.Listener;

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

            if (killstreak instanceof SimpleMovementHandler) {
                MineServer.getInstance().addMovementHandler((SimpleMovementHandler) killstreak);
            }

            if (killstreak instanceof PacketHandler) {
                MineServer.getInstance().addPacketHandler((PacketHandler) killstreak);
            }
        }
    }

    public Killstreak getKillstreakByName(String name) {
        return this.killstreaks.get(name);
    }
}
