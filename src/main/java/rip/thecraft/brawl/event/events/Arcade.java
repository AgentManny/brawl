package rip.thecraft.brawl.event.events;

import rip.thecraft.brawl.event.EventType;
import rip.thecraft.brawl.event.type.TimeEvent;

public class Arcade extends TimeEvent {

    public Arcade(String name) {
        super(name, EventType.ARCADE);
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
