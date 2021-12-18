package rip.thecraft.brawl.util.player;

import com.comphenix.protocol.injector.BukkitUnwrapper;
import com.comphenix.protocol.reflect.accessors.Accessors;
import com.comphenix.protocol.reflect.accessors.ConstructorAccessor;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mongodb.lang.Nullable;
import gg.manny.hologram.HologramPlugin;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Skin;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_8_R3.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import rip.thecraft.brawl.util.ReflectionUtils;
import rip.thecraft.brawl.util.player.datawatcher.type.PlayerDataWatcherHelper;
import rip.thecraft.spartan.nametag.NametagHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class FakePlayer {

    /**
     * Returns display name of entity
     */
    private final String name;

    /**
     * Returns unique identifier of player
     */
    private final UUID id;

    /**
     * Returns the entity id of player
     */
    public final int entityId;

    @Nullable
    private ItemStack heldItem;

    private Location location;

    /**
     * Returns the skin of a player
     */
    private Skin skin;
    private GameProfile profile;

    /**
     * Returns the viewers who can view this fake player
     */
    protected List<UUID> viewers = new ArrayList<>();

    private static Entity fakeEntity = null;
    private static ConstructorAccessor eggConstructor = null;
    private static Entity fakeEntity() {
        if (fakeEntity != null) {
            return fakeEntity;
        }

        // We can create a fake egg without it affecting anything
        // Mojang added difficulty to lightning strikes, so this'll have to do
        if (eggConstructor == null) {
            eggConstructor = Accessors.getConstructorAccessor(
                    MinecraftReflection.getMinecraftClass("world.entity.projectile.EntityEgg", "EntityEgg"),
                    MinecraftReflection.getNmsWorldClass(), double.class, double.class, double.class
            );
        }

        Object world = BukkitUnwrapper.getInstance().unwrapItem(Bukkit.getWorlds().get(0));
        return fakeEntity = (Entity) eggConstructor.invoke(world, 0, 0, 0);
    }

    public FakePlayer(Player player) {
        this.name = "Clone";
        this.id = UUID.randomUUID();
        this.entityId = fakeEntity().getId();

        this.skin = player.getSkin();

        this.location = player.getLocation();
        this.heldItem = ((CraftItemStack) player.getItemInHand()).getHandle();

        FakePlayerHandler.players.add(this);
    }

    public List<Packet<?>> getSpawnPacket(boolean legacy) {
        List<Packet<?>> packets = new ArrayList<>();

        profile = new GameProfile(id, name);
        profile.getProperties().put("textures", new Property("textures", skin.getData(), skin.getSignature()));

        DataWatcher watcher = new DataWatcher(null);
        watcher.a(PlayerDataWatcherHelper.PLAYER_STATE.getId(), (byte) 0);
        watcher.a(1, (short) 0);
        watcher.a(8, (byte) 0);

        PacketPlayOutNamedEntitySpawn spawnEntity = new PacketPlayOutNamedEntitySpawn(
                entityId, id,
                location.getX(), location.getY(), location.getZ(), (byte) location.getYaw(), (byte) location.getPitch(),
                heldItem,
                watcher
        );

        packets.add(spawnEntity);
        if (!legacy) { // Players won't appear without this packet
            PacketPlayOutPlayerInfo playerInfo = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER);
            playerInfo.b.add(playerInfo.constructData(profile, 0, WorldSettings.EnumGamemode.SURVIVAL, new ChatComponentText(name)));
            packets.add(0, playerInfo);
        }

        return packets;
    }

    public void interact(EntityPlayer player, PacketPlayInUseEntity.EnumEntityUseAction action) {
        if (action == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) {
            attack(player);
        } else if (action == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT_AT) {

        }
    }

    public void attack(EntityPlayer player) {
        sendAnimation(AnimationType.DAMAGE);
        location.getWorld().playSound(location, Sound.BLAZE_HIT, 1f, 1f);
        if (!player.onGround) {
            sendAnimation(AnimationType.CRITICALS_PARTICLE);
        }
        addVelocity(new Vector(0.5, 1, 0.5));
    }


    private static String SCOREBOARD_TEAM = "FAKE_PLAYER";
    public void setVisibility(boolean visible) {
        Scoreboard scoreboard = ((CraftScoreboard) Bukkit.getScoreboardManager().getMainScoreboard()).getHandle();
        ScoreboardTeam team = scoreboard.getTeam(SCOREBOARD_TEAM);
        if (team == null) {
            team = new ScoreboardTeam(((CraftScoreboard) Bukkit.getScoreboardManager().getMainScoreboard()).getHandle(), SCOREBOARD_TEAM);
            team.setCanSeeFriendlyInvisibles(true);
            team.setNameTagVisibility(ScoreboardTeamBase.EnumNameTagVisibility.HIDE_FOR_OWN_TEAM);
        }
        for (UUID viewer : viewers) {
            Player player = Bukkit.getPlayer(viewer);
            if (player != null) {
                List<Packet> packets = new ArrayList<>();
                if (visible) {
                    packets.add(new PacketPlayOutScoreboardTeam(team, 1));
                    packets.add(new PacketPlayOutScoreboardTeam(team, 0));
                }
                packets.add(new PacketPlayOutScoreboardTeam(team, Arrays.asList(player.getName(), name), visible ? 3 : 4));
                packets.forEach(packet -> sendPacket(player, packet));
                if (!visible) { // Update back to their own team
                    NametagHandler.reloadPlayer(player);
                }
            }
        }
        this.wasVisible = visible;
    }

    public boolean wasVisible = true;

    public void tick(Player player) { // Debug
//        setPlayerStatus(6, player.isSneaking());
//        setSneaking(player.isSneaking());
//        boolean blocking = player.isSneaking();
//        if (wasBlocking && !blocking) {
////            setPlayerStatus(17, player.isSneaking());
//        }
//        wasBlocking = blocking;
//        if (blocking) {
////            setPlayerStatus(PlayerStatusDataWatcherHelper.BLOCK.getBitmask(), player.isSneaking());
//        }
//        boolean invisible = player.isSneaking();
//        setPlayerStatus(PlayerStatusDataWatcherHelper.INVISIBLE.getBitmask(), invisible);
////        setAnimation(AnimationType.DAMAGE);
//        if (invisible && !wasInvisible) {
//            setVisibility(true);
//            wasInvisible = true;
//        }
//        if (!invisible && wasInvisible) {
//            setVisibility(false);
//            wasInvisible = false;
//        }
    }

    public void sendTo(Player player) {
        boolean legacy = HologramPlugin.getInstance().onLegacyVersion(player);
        getSpawnPacket(legacy).forEach(packet -> sendPacket(player, packet));
        viewers.add(player.getUniqueId());
    }

    public void remove() {
        for (UUID viewer : viewers) {
            Player player = Bukkit.getPlayer(viewer);
            if (viewer != null) {
                boolean legacy = HologramPlugin.getInstance().onLegacyVersion(player);
                if (!legacy) {
                    PacketPlayOutPlayerInfo playerInfo = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER);
                    playerInfo.b.add(playerInfo.constructData(profile, 0, WorldSettings.EnumGamemode.SURVIVAL, new ChatComponentText(name)));
                    sendPacket(player, playerInfo);
                }
                if (!wasVisible) {
                    sendStatus(PlayerStatus.INVISIBLE, false);
                    setVisibility(false);
                }
                sendPacket(player, new PacketPlayOutEntityDestroy(entityId));
                NametagHandler.reloadPlayer(player);
            }
        }
        viewers.clear();
        FakePlayerHandler.players.remove(this);
    }

    public void teleport(Location location) {
        int x = MathHelper.floor(location.getX() * 32.0D);
        int y = MathHelper.floor(location.getY() * 32.0D);
        int z = MathHelper.floor(location.getZ() * 32.0D);
        byte yaw = (byte) ((int)(location.getYaw() * 256.0F / 360.0F));
        byte pitch = (byte) -((int)(location.getPitch() * 256.0F / 360.0F));
        PacketPlayOutEntityTeleport teleport = new PacketPlayOutEntityTeleport(
                entityId,
                x, y, z, yaw, pitch,
                true
        );
        setRotation(location.getYaw());
        sendPacket(viewers, teleport);
    }

    public void setRotation(float yaw) {
        PacketPlayOutEntityHeadRotation headRotation = new PacketPlayOutEntityHeadRotation();
        try {
            ReflectionUtils.setValue(headRotation, true, "a", entityId);
            ReflectionUtils.setValue(headRotation, true, "b", (byte) ((int)(yaw * 256.0F / 360.0F)));
            sendPacket(viewers, headRotation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addVelocity(Vector vector) {
        PacketPlayOutEntityVelocity velocity = new PacketPlayOutEntityVelocity(
                entityId,
                vector.getX(), vector.getY(), vector.getZ()
        );
        sendPacket(viewers, velocity);
    }

    public void setCrouching(boolean crouching) {
        sendStatus(PlayerStatus.CROUCHING, crouching);
    }

    public void setSprinting(boolean sprinting) {
        sendStatus(PlayerStatus.SPRINTING, sprinting);
    }

    @Deprecated
    public void move(Location location) { // No point listening
    }

    public void update(DataWatcher dataWatcher, boolean value) {
        sendPacket(viewers, new PacketPlayOutEntityMetadata(entityId, dataWatcher, value));
    }

    public void sendAnimation(AnimationType animation) {
        PacketPlayOutAnimation armAnimation = new PacketPlayOutAnimation();
        try {
            ReflectionUtils.setValue(armAnimation, true, "a", entityId);
            ReflectionUtils.setValue(armAnimation, true, "b", animation.id);
            sendPacket(viewers, armAnimation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendStatus(PlayerStatus status, boolean value) {
        DataWatcher watcher = new DataWatcher(null);
        watcher.a(0, (byte) status.getBitmask());
        update(watcher, value);
    }

    private static void sendPacket(List<UUID> players, Packet<?> packet) {
        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                sendPacket(player, packet);
            }
        }
    }

    public static void sendPacket(Player player, Packet<?> packet) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }
}
