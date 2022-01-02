package rip.thecraft.brawl.spawn.event.type;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.kit.ability.property.AbilityProperty;
import rip.thecraft.brawl.spawn.event.Event;
import rip.thecraft.brawl.spawn.event.EventType;
import rip.thecraft.brawl.util.DurationFormatter;

import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class TimeEvent extends Event {

    @AbilityProperty(id = "duration")
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
        return 20L;
    }

    @Override
    public void getScoreboard(Player player, List<String> entries) {
        super.getScoreboard(player, entries);
        entries.add("Time left: " + ChatColor.YELLOW + DurationFormatter.getRemaining(Math.max(0, expiring - System.currentTimeMillis())));
    }

    @Override
    public void tick() {
        if (expiring <= System.currentTimeMillis()) {
            finish(null);
            end();
            broadcast("Event is over.");
        }
    }

    public abstract void finish();
}
