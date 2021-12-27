package rip.thecraft.brawl.event.events;

import org.bukkit.entity.Player;
import rip.thecraft.brawl.ability.property.AbilityProperty;
import rip.thecraft.brawl.event.EventType;
import rip.thecraft.brawl.event.type.TimeEvent;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.player.PlayerData;

import java.util.List;

public class KitFrenzy extends TimeEvent {

    @AbilityProperty(id = "abilities-only") public boolean abilitiesOnly = true; // Only allow kits that have abilities

    private transient List<Kit> kits;

    public KitFrenzy(String name) {
        super(name, EventType.KIT_FRENZY);
    }

    @Override
    public void start() {

    }

    @Override
    public void onSpawnLeave(Player player, PlayerData playerData) {

    }

    @Override
    public boolean isSetup() {
        return true;
    }

    @Override
    public void finish() {

    }
}
