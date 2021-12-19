package rip.thecraft.brawl.event.events;

import rip.thecraft.brawl.ability.property.AbilityProperty;
import rip.thecraft.brawl.event.EventType;
import rip.thecraft.brawl.event.type.TimeEvent;

public class KitFrenzy extends TimeEvent {

    @AbilityProperty(id = "abilities-only") public boolean abilitiesOnly = true; // Only allow kits that have abilities

    public KitFrenzy(String name) {
        super(name, EventType.KIT_FRENZY);
    }

    @Override
    public void start() {

    }

    @Override
    public boolean isSetup() {
        return true;
    }

    @Override
    public void finish() {

    }
}
