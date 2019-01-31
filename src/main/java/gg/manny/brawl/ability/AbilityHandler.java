package gg.manny.brawl.ability;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.ability.abilities.StomperAbility;
import gg.manny.spigot.GenericSpigot;
import gg.manny.spigot.handler.PacketHandler;
import gg.manny.spigot.handler.SimpleMovementHandler;
import lombok.Getter;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

public class AbilityHandler {

    private final Brawl plugin;

    @Getter
    private Map<String, Ability> abilities = new HashMap<>();

    public AbilityHandler(Brawl plugin) {
        this.plugin = plugin;
        this.registerAbilities(new StomperAbility(plugin));
    }

    private void registerAbilities(Ability... abilities) {
        for (Ability ability : abilities) {
            this.abilities.put(ability.getName(), ability);

            if (ability instanceof Listener) {
                plugin.getServer().getPluginManager().registerEvents((Listener) ability, plugin);
            }


            if (ability instanceof SimpleMovementHandler) {
                GenericSpigot.INSTANCE.addMovementHandler((SimpleMovementHandler) ability);
            }

            if (ability instanceof PacketHandler) {
                GenericSpigot.INSTANCE.addPacketHandler((PacketHandler) ability);
            }
        }
    }

    public Ability getAbilityByName(String name) {
        return this.abilities.get(name);
    }
}
