package rip.thecraft.brawl.event.type;

import rip.thecraft.brawl.ability.property.AbilityProperty;
import rip.thecraft.brawl.event.Event;
import rip.thecraft.brawl.event.EventType;

import java.util.concurrent.TimeUnit;

public abstract class TimeEvent extends Event {

    @AbilityProperty
    public long duration = TimeUnit.SECONDS.toMillis(15);

    private transient long expiring;

    public TimeEvent(String name, EventType type) {
        super(name, type);
    }

    @Override
    public void setup() {
        expiring = System.currentTimeMillis() + duration;
    }

    @Override
    public long getUpdateInterval() {
        return 200L;
    }

    @Override
    public void tick() {
        if (expiring <= System.currentTimeMillis()) {
            finish(null);
            broadcast("Event is over.");
        }
    }

    public abstract void finish();
}
