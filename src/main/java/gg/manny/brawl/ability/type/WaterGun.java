package gg.manny.brawl.ability.type;

import com.google.gson.JsonObject;
import gg.manny.brawl.Brawl;
import gg.manny.brawl.ability.Ability;
import gg.manny.brawl.util.ParticleEffect;
import gg.manny.pivot.serialization.PotionEffectAdapter;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
public class WaterGun extends Ability implements Listener  {

    private final Brawl plugin;

    private PotionEffect potionEffect = new PotionEffect(PotionEffectType.SLOW, 120, 4);

    private ParticleEffect landParticle = ParticleEffect.SPLASH;
    private Sound landSound = Sound.SPLASH2;

    private ParticleEffect activateParticle = ParticleEffect.SPLASH;
    private Sound activateSound = Sound.SPLASH;

    private double radius = 1.6;
    private int delay = 5;

    @Override
    public Material getType() {
        return Material.INK_SACK;
    }

    @Override
    public byte getData() {
        return 12;
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.AQUA;
    }

    @Override
    public void onActivate(Player player) {
        if (this.hasCooldown(player, true)) return;
        this.addCooldown(player);

        FallingBlock block = player.getWorld().spawnFallingBlock(player.getEyeLocation(), Material.WATER, (byte)0);
        block.setMetadata("watergun", new FixedMetadataValue(plugin, player.getUniqueId()));
        block.setDropItem(false);
        block.setVelocity(player.getEyeLocation().getDirection().multiply(1.5));

        new BukkitRunnable() {

            long timestamp = System.currentTimeMillis();
            Player hit;

            @Override
            public void run() {
                if ((System.currentTimeMillis() - timestamp) > 750L) {
                    cancel();
                    return;
                }

                if (block.isDead()) {
                    cancel();
                    stuck(hit != null ? hit.getLocation() : block.getLocation());
                    return;
                }

                block.getNearbyEntities(1, 2, 1).stream().filter(other -> other instanceof Player && !player.equals(other)).findAny().ifPresent(player -> {
                    hit = (Player) player;
                    stuck(hit != null ? hit.getLocation() : block.getLocation());
                    cancel();
                });

            }

            @Override
            public synchronized void cancel() throws IllegalStateException {
                if (block == null || block.isDead() || !block.isValid()) {
                    block.remove();
                }
                super.cancel();
            }
        }.runTaskTimer(plugin, 2L, 2L);

    }

    private void stuck(Location location) {
        List<Location> locations = new ArrayList<>();
        locations.add(location.clone().add(1.0D, 1.0D, -1.0D));
        locations.add(location.clone().add(-1.0D, 1.0D, -1.0D));
        locations.add(location.clone().add(1.0D, 1.0D, 1.0D));
        locations.add(location.clone().add(-1.0D, 1.0D, 1.0D));
        locations.add(location.clone().add(0.0D, 1.0D, 0.0D));
        locations.add(location.clone().add(-1.0D, 1.0D, 0.0D));
        locations.add(location.clone().add(1.0D, 1.0D, 0.0D));
        locations.add(location.clone().add(0.0D, 1.0D, -1.0D));
        locations.add(location.clone().add(0.0D, 1.0D, 1.0D));

        for (Location loc : locations) {
            Block state = loc.getBlock();

            if (state.getType() == Material.AIR || state.getType() == Material.WATER || state.isLiquid() ) {
                state.setMetadata("watergun", new FixedMetadataValue(plugin, state.getType().name()));
                state.setType(Material.WATER);
            }
        }

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            for (Location loc : locations) {
                Block state = loc.getBlock();

                if (state.hasMetadata("watergun")) {
                    state.setType(Material.valueOf(state.getMetadata("watergun").get(0).asString()));
                }
            }
        }, 120L);

    }

    @Override
    public JsonObject toJson() {
        JsonObject object = super.toJson();
        object.addProperty("activateParticle", this.activateParticle == null ? null : this.activateParticle.name());
        object.addProperty("activateSound", this.activateSound == null ? null : this.activateSound.name());
        object.addProperty("landParticle", this.landParticle == null ? null : this.landParticle.name());
        object.addProperty("landSound", this.landSound == null ? null : this.landSound.name());
        object.addProperty("radius", this.radius);
        object.addProperty("delay", this.delay);
        object.add("potionEffect", PotionEffectAdapter.toJson(this.potionEffect));
        return object;
    }

    @Override
    public void fromJson(JsonObject object) {
        this.activateParticle = object.get("activateParticle") == null ? null : ParticleEffect.valueOf(object.get("activateParticle").getAsString());
        this.activateSound = object.get("activateSound") == null ? null : Sound.valueOf(object.get("activateSound").getAsString());

        this.landParticle = object.get("landParticle") == null ? null : ParticleEffect.valueOf(object.get("landParticle").getAsString());
        this.landSound = object.get("landSound") == null ? null : Sound.valueOf(object.get("landSound").getAsString());

        this.delay = object.get("delay").getAsInt();
        this.radius = object.get("radius").getAsDouble();

        this.potionEffect = PotionEffectAdapter.fromJson(object.get("potionEffect"));
    }
}
