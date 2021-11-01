package rip.thecraft.brawl.util;

import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityStatus;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.region.RegionType;
import rip.thecraft.falcon.util.EntityUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class PlayerUtil {

    /**
     * Returns nearby players from a player.
     *
     * @param player Target player
     * @param radius Radius to check
     *
     * @return Returns list of nearby players
     */
    public static List<Player> getNearbyPlayers(LivingEntity player, double radius) {
        List<Player> players = new ArrayList<>();
        for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
            if (entity instanceof Player) {
                players.add((Player) entity);
            }
        }
        return players;
    }

    /**
     * Returns nearby players from a location.
     *
     * @param location Target location
     * @param radius Radius to check
     *
     * @return Returns list of nearby players
     */
    public static List<Player> getNearbyPlayers(Location location, double radius) {
        List<Player> players = new ArrayList<>();
        for (Entity entity : location.getNearbyEntities(radius, radius, radius)) {
            if (entity instanceof Player) {
                players.add((Player) entity);
            }
        }
        return players;
    }
    /**
     * Resets a player's inventory (and other associated data, such as health, food, etc) to their default state.
     *
     * @param player The player to reset
     */
    public static void resetInventory(Player player) {
        resetInventory(player, null);
    }

    /**
     * Resets a player's inventory (and other associated data, such as health, food, etc) to their default state.
     *
     * @param player   The player to reset
     * @param gameMode The gamemode to reset the player to. null if their current gamemode should be kept.
     */
    public static void resetInventory(Player player, GameMode gameMode) {
        player.closeInventory(); // fixes kit mixing bug
        player.getInventory().clear();
        player.setMaxHealth(20.0D);
        player.setHealth(player.getMaxHealth());
        player.setFallDistance(0.0f);
        player.setFoodLevel(20);
        player.setSaturation(10.0f);

//        player.setLevel(0);
//        player.setExp(0.0f);

        if (!player.hasMetadata("modmode")) {
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
        }

        player.setFireTicks(0);

        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(potionEffect.getType());
        }

        if (gameMode != null && player.getGameMode() != gameMode) {
            player.setGameMode(gameMode);
        }
    }

    /**
     * Get amount in players inventory of Material
     *
     * @param inv      - Inventory to check
     * @param material - Material to check the amount
     * @return
     */
    public static int getAmountInInventory(Inventory inv, Material material) {
        int amount = 0;
        ItemStack[] contents = inv.getContents();

        for (ItemStack item : contents) {
            if (item != null && item.getType() == material) {
                amount = amount + item.getAmount();
            }
        }

        return amount;
    }

    /**
     * Get amount in players inventory of Material
     *
     * @param inv      - Inventory to check
     * @param material - Material to check the amount
     * @return
     */
    public static int getAmountInInventory(Inventory inv, Material material, short data) {
        int amount = 0;
        ItemStack[] contents = inv.getContents();

        for (ItemStack item : contents) {
            if (item != null && item.getType() == material && item.getDurability() == data) {
                amount = amount + item.getAmount();
            }
        }

        return amount;
    }

    public static void giveItemSafely(Player player, ItemStack item) {
        if(player.getInventory().firstEmpty() == -1) {
            player.getWorld().dropItemNaturally(player.getLocation(), item);
        }
        else {
            player.getInventory().addItem(item);
        }
    }

    public static Player getPlayerByEyeLocation(Player player, double distance) {
        Location observerPos = player.getEyeLocation();
        Vector3D observerDir = new Vector3D(observerPos.getDirection());

        Vector3D observerStart = new Vector3D(observerPos);
        Vector3D observerEnd = observerStart.add(observerDir.multiply(distance));

        Player hit = null;

        // Get nearby entities
        for (Player target : player.getWorld().getPlayers()) {
            // Bounding box of the given player
            Vector3D targetPos = new Vector3D(target.getLocation());
            Vector3D minimum = targetPos.add(-0.5, 0, -0.5);
            Vector3D maximum = targetPos.add(0.5, 1.67, 0.5);

            if (target != player && hasIntersection(observerStart, observerEnd, minimum, maximum)) {
                if (hit == null ||
                        hit.getLocation().distanceSquared(observerPos) >
                                target.getLocation().distanceSquared(observerPos)) {

                    hit = target;
                }
            }
        }

        return hit;
    }

    public static boolean hit(Entity entity, Entity target) {
        Location observerPos = entity.getLocation();
        Vector3D targetPos = new Vector3D(target.getLocation());

        // Bounding box of the given player
        Vector3D minimum = targetPos.add(-0.5, 0, -0.5);
        Vector3D maximum = targetPos.add(0.5, 1.67, 0.5);

        Vector3D observerStart = new Vector3D(observerPos);

        return !RegionType.SAFEZONE.appliesTo(target.getLocation()) && target != entity && hasIntersection(observerStart, targetPos, minimum, maximum) && target.getLocation().distanceSquared(observerPos) >
                entity.getLocation().distanceSquared(observerPos);
    }

    private static boolean hasIntersection(Vector3D p1, Vector3D p2, Vector3D min, Vector3D max) {
        final double epsilon = 0.0001f;

        Vector3D d = p2.subtract(p1).multiply(0.5);
        Vector3D e = max.subtract(min).multiply(0.5);
        Vector3D c = p1.add(d).subtract(min.add(max).multiply(0.5));
        Vector3D ad = d.abs();

        if (Math.abs(c.x) > e.x + ad.x)
            return false;
        if (Math.abs(c.y) > e.y + ad.y)
            return false;
        if (Math.abs(c.z) > e.z + ad.z)
            return false;

        if (Math.abs(d.y * c.z - d.z * c.y) > e.y * ad.z + e.z * ad.y + epsilon)
            return false;
        if (Math.abs(d.z * c.x - d.x * c.z) > e.z * ad.x + e.x * ad.z + epsilon)
            return false;
        if (Math.abs(d.x * c.y - d.y * c.x) > e.x * ad.y + e.y * ad.x + epsilon)
            return false;

        return true;
    }

    private static Field STATUS_PACKET_ID_FIELD;
    private static Field STATUS_PACKET_STATUS_FIELD;
    private static Field SPAWN_PACKET_ID_FIELD;

    static {
        try {
            STATUS_PACKET_ID_FIELD = PacketPlayOutEntityStatus.class.getDeclaredField("a");
            STATUS_PACKET_ID_FIELD.setAccessible(true);
            STATUS_PACKET_STATUS_FIELD = PacketPlayOutEntityStatus.class.getDeclaredField("b");
            STATUS_PACKET_STATUS_FIELD.setAccessible(true);
            SPAWN_PACKET_ID_FIELD = PacketPlayOutNamedEntitySpawn.class.getDeclaredField("a");
            SPAWN_PACKET_ID_FIELD.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public static void animateDeath(Player player) {
        int entityId = EntityUtils.getFakeEntityId();
        PacketPlayOutNamedEntitySpawn spawnPacket = new PacketPlayOutNamedEntitySpawn(((CraftPlayer) player).getHandle());
        PacketPlayOutEntityStatus statusPacket = new PacketPlayOutEntityStatus();
        try {
            SPAWN_PACKET_ID_FIELD.set(spawnPacket, entityId);
            STATUS_PACKET_ID_FIELD.set(statusPacket, entityId);
            STATUS_PACKET_STATUS_FIELD.set(statusPacket, (byte) 3);
            int radius = MinecraftServer.getServer().getPlayerList().d();
            HashSet<Player> sentTo = new HashSet<>();
            for (Entity entity : player.getNearbyEntities((double) radius, (double) radius, (double) radius)) {
                Player watcher;
                if (!(entity instanceof Player) || (watcher = (Player) entity).getUniqueId().equals(player.getUniqueId()))
                    continue;
                ((CraftPlayer) watcher).getHandle().playerConnection.sendPacket(spawnPacket);
                ((CraftPlayer) watcher).getHandle().playerConnection.sendPacket(statusPacket);
                sentTo.add(watcher);
            }
            Bukkit.getScheduler().runTaskLater(Brawl.getInstance(), () -> {
                for (Player watcher : sentTo) {
                    ((CraftPlayer) watcher).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(entityId));
                }
            }, 40L);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void animateDeath(Player player, Player watcher) {
        int entityId = EntityUtils.getFakeEntityId();
        PacketPlayOutNamedEntitySpawn spawnPacket = new PacketPlayOutNamedEntitySpawn(((CraftPlayer) player).getHandle());
        PacketPlayOutEntityStatus statusPacket = new PacketPlayOutEntityStatus();
        try {
            SPAWN_PACKET_ID_FIELD.set(spawnPacket, entityId);
            STATUS_PACKET_ID_FIELD.set(statusPacket, entityId);
            STATUS_PACKET_STATUS_FIELD.set(statusPacket, (byte) 3);
            ((CraftPlayer) watcher).getHandle().playerConnection.sendPacket(spawnPacket);
            ((CraftPlayer) watcher).getHandle().playerConnection.sendPacket(statusPacket);
            Bukkit.getScheduler().runTaskLater(Brawl.getInstance(), () -> ((CraftPlayer) watcher).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(entityId)), 40L);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
