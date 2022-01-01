package rip.thecraft.brawl.kit.ability.abilities;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.kit.ability.Ability;
import rip.thecraft.brawl.kit.ability.property.AbilityData;
import rip.thecraft.brawl.kit.ability.property.AbilityProperty;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.server.region.RegionType;
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
public class TimeWarp extends Ability implements Listener {

    @AbilityProperty(id = "expire-time", description = "Time before old locations expire")
    public long locExpiration = TimeUnit.SECONDS.toMillis(5);

    @AbilityProperty(id = "slow-falling", description = "Should slow falling be enabled when teleporting")
    public boolean slowFalling = true;

    @AbilityProperty(id = "power", description = "Power of slow falling")
    public double power = 0.05;

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
    public void onRemove(Player player) {
        if (hasActivated(player)) {
            TimeData timeData = locations.get(player.getUniqueId());
            timeData.cancel();
            locations.remove(player.getUniqueId());
        }
    }

    @Override
    public void onActivate(Player player) {
        if (hasCooldown(player, true)) return;

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

            addCooldown(player);
            player.playSound(player.getLocation(), Sound.PORTAL_TRIGGER, 1.75f, 1);
            timeData.teleporting = true;
            timeData.onGround = slowFalling && isOnGround(player, 5);
            timeData.teleportLocations = locations.iterator();

            FakePlayer fakePlayer = timeData.fakePlayer;
            //fakePlayer.remove();

            fakePlayer.sendTo(player); // TODO send visual to nearby players too
            fakePlayer.sendStatus(PlayerStatus.INVISIBLE, true);
            fakePlayer.setVisibility(true);

            player.sendMessage(ChatColor.YELLOW + "Teleporting...");
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (hasActivated(player)) {
            TimeData timeData = locations.get(player.getUniqueId());
            timeData.clear();
        }
    }

    private class TimeData extends BukkitRunnable {

        private final Player player;
        private final FakePlayer fakePlayer;

        private LinkedTimeCache<Location> locations = new LinkedTimeCache<>(75, locExpiration);

        private boolean teleporting = false;
        private Iterator<Location> teleportLocations;

        private boolean onGround = false;
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
            onGround = false;
            locations.clear();
            fakePlayer.remove();
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

                if (!onGround) {
                    if (!player.isFlying()) {
                        if (!player.getAllowFlight()) {
                            player.setAllowFlight(true);
                        }
                        player.setFlying(true);
                    } else {
                        player.setVelocity(new Vector(0, power, 0));
                    }
                }

                if (!teleportLocations.hasNext()) {
                    clear();
                    if (vehicle != null) {
                        player.eject();
                        vehicle.remove();
                        vehicle = null;
                    }
                    if (!onGround) {
                        player.setFlying(false);
                        player.setAllowFlight(false);
                    }
                    player.setFallDistance(0);
                    player.teleport(location);
                    World world = location.getWorld();
                    world.playSound(location, Sound.ENDERMAN_TELEPORT, 1, 1.2F);
                    world.playEffect(location, Effect.ENDER_SIGNAL, 1);
                    player.sendMessage(ChatColor.GREEN + "Warped to your last location.");
                }
            } else {
                Location location = player.getLocation();
                PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
                if (playerData.isSpawnProtection() || playerData.isNoFallDamage() || playerData.isDuelArena() || RegionType.SAFEZONE.appliesTo(location)) {
                    return;
                }

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
            TimeWarp.this.locations.remove(player.getUniqueId());
            fakePlayer.remove();
        }

        public List<Location> getLocations() {
            List<Location> values = locations.values();
            Collections.reverse(values);
            return values;
        }
    }
}