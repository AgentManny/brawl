package rip.thecraft.brawl.spawn.event.events;

import rip.thecraft.brawl.spawn.event.EventType;
import rip.thecraft.brawl.spawn.event.type.TimeEvent;

public class FreeKits extends TimeEvent {

    public FreeKits(String name) {
        super(name, EventType.FREE_KITS);
    }

    @Override
    public void start() {

    }

    @Override
    public boolean isMapsRequired() {
        return false;
    }

    @Override
    public boolean isSetup() {
        return true;
    }

    @Override
    public void finish() {

    }
}
