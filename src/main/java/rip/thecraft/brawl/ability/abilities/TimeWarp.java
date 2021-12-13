package rip.thecraft.brawl.ability.abilities;

import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.ability.property.AbilityData;
import rip.thecraft.brawl.ability.property.AbilityProperty;
import rip.thecraft.brawl.util.LinkedTimeCache;
import rip.thecraft.brawl.util.player.FakePlayer;
import rip.thecraft.brawl.util.player.PlayerStatus;

import java.util.*;
import java.util.concurrent.TimeUnit;

@AbilityData(
        name = "Time Warp",
        description = "Teleports you back to your last location.",
        color = ChatColor.GOLD,
        icon = Material.WATCH
)
public class TimeWarp extends Ability {

    @AbilityProperty(id = "expire-time", description = "Time before old locations expire")
    public long locExpiration = TimeUnit.SECONDS.toMillis(5);

    private Map<UUID, TimeData> locations = new HashMap<>();

    private boolean hasActivated(Player player) {
        return !locations.isEmpty() && locations.containsKey(player.getUniqueId());
    }

    @Override
    public void onApply(Player player) {
        if (hasActivated(player)) {
            TimeData timeData = locations.get(player.getUniqueId());
            timeData.clear();
        } else {
            locations.put(player.getUniqueId(), new TimeData(player));
        }
    }

    @Override
    public void onDeactivate(Player player) {
        if (hasActivated(player)) {
            TimeData timeData = locations.get(player.getUniqueId());
            timeData.cancel();
            locations.remove(player.getUniqueId());
        }
    }

    @Override
    public void onActivate(Player player) {
        if (hasCooldown(player, true)) return;
        addCooldown(player);

        if (hasActivated(player)) {
            TimeData timeData = locations.get(player.getUniqueId());
            Collection<Location> locations = timeData.getLocations();
            if (locations.size() < 15) {
                player.sendMessage(ChatColor.RED + "There isn't enough data to teleport you back to.");
                return;
            }

            if (timeData.teleporting) {
                player.sendMessage(ChatColor.RED + "You are already teleporting...");
                return;
            }
            player.playSound(player.getLocation(), Sound.PORTAL_TRIGGER, 1.75F, 1);
            timeData.teleporting = true;
            timeData.teleportLocations = locations.iterator();
            timeData.fakePlayer.sendTo(player); // TODO send visual to nearby players too
            timeData.fakePlayer.sendStatus(PlayerStatus.INVISIBLE, true);
            timeData.fakePlayer.setVisibility(true);

            if (!player.isOnGround()) {
                player.sendMessage(ChatColor.RED + "Mounting you to invisible entity :D");
                Location location = player.getLocation();
                ArmorStand entity = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
                entity.setGravity(false);
                entity.setCanMove(false);
                entity.setArms(false);
                entity.setSmall(true);
                entity.setVisible(false);
                entity.setPassenger(player);
                timeData.vehicle = entity;
            }

            player.sendMessage(ChatColor.YELLOW + "Teleporting...");

        }
    }

    private class TimeData extends BukkitRunnable {

        private final Player player;
        private final FakePlayer fakePlayer;

        private LinkedTimeCache<Location> locations = new LinkedTimeCache<>(75, locExpiration);

        private boolean teleporting = false;
        private Iterator<Location> teleportLocations;

        private Entity vehicle;
        private Location lastLocation;

        public TimeData(Player player) {
            this.player = player;
            this.fakePlayer = new FakePlayer(player);
            runTaskTimer(Brawl.getInstance(), 2L, 2L);
        }

        public void clear() {
            teleporting = false;
            teleportLocations = null;
            locations.clear();
        }

        @Override
        public void run() {
            if (player == null) {
                cancel();
                return;
            }

            if (teleporting && teleportLocations.hasNext()) {
                Location location = teleportLocations.next();
                fakePlayer.teleport(location);

                if (!teleportLocations.hasNext()) {
                    clear();
                    fakePlayer.remove();
                    if (vehicle != null) {
                        player.eject();
                        vehicle.remove();
                        vehicle = null;
                    }
                    player.teleport(location);
                    location.getWorld().playSound(location, Sound.ENDERMAN_TELEPORT, 1, 1.2F);
                    location.getWorld().playEffect(location, Effect.ENDER_SIGNAL, 1);

                    player.sendMessage(ChatColor.GREEN + "Teleported to your last location.");
                }
            } else {
                Location location = player.getLocation();
                if (lastLocation != null) {
                    boolean hasMoved = lastLocation.getBlockX() != location.getBlockX() || lastLocation.getBlockZ() != location.getBlockZ() || lastLocation.getBlockY() != location.getBlockY();
                    if (!hasMoved) { // Reduce amount of teleportation packets, this also speeds up movement
                        return;
                    }
                }
                locations.add(location);
                lastLocation = location;
            }
            player.sendActionBar(ChatColor.LIGHT_PURPLE + "Locations: " + locations.size() + "");
        }

        @Override
        public synchronized void cancel() throws IllegalStateException {
            super.cancel();
        }

        public List<Location> getLocations() {
            List<Location> values = locations.values();
            Collections.reverse(values);
            return values;
        }
    }
}