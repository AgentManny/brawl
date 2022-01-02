package rip.thecraft.brawl.spawn.event.events;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.kit.ability.property.AbilityProperty;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.spawn.event.EventType;
import rip.thecraft.brawl.spawn.event.type.TimeEvent;

import java.util.ArrayList;
import java.util.List;

public class Warper extends TimeEvent {

    @AbilityProperty(id = "locations")
    public List<Location> locations = new ArrayList<>();

    public Warper(String name) {
        super(name, EventType.WARPER);
    }

    @Override
    public void start() {

    }

    @Override
    public void onSpawnLeave(Player player, PlayerData playerData) {
        Location location = locations.get(Brawl.RANDOM.nextInt(locations.size()));
        player.teleport(location);
        player.getWorld().playSound(location, Sound.ENDERMAN_TELEPORT, 1, 1.2F);
        player.getWorld().playEffect(location, Effect.ENDER_SIGNAL, 1);
    }

    @Override
    public boolean isMapsRequired() {
        return true;
    }

    @Override
    public boolean isSetup() {
        return !locations.isEmpty();
    }

    @Override
    public void finish() {

    }
}
