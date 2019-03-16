package gg.manny.brawl.ability.type;

import com.google.gson.JsonObject;
import gg.manny.brawl.Brawl;
import gg.manny.brawl.ability.Ability;
import gg.manny.pivot.util.inventory.ItemBuilder;
import gg.manny.pivot.util.serialization.PotionEffectAdapter;
import gg.manny.spigot.util.chatcolor.CC;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.inventivetalent.particle.ParticleEffect;

import java.util.ArrayList;
import java.util.List;


public class WaterGun extends Ability implements Listener  {

    private final Brawl brawl;

    private PotionEffect potionEffect = new PotionEffect(PotionEffectType.SLOW, 120, 4);

    private ParticleEffect landParticle = ParticleEffect.WATER_SPLASH;
    private Sound landSound = Sound.SPLASH2;

    private ParticleEffect activateParticle = ParticleEffect.WATER_SPLASH;
    private Sound activateSound = Sound.SPLASH;




    private double radius = 1.6;
    private int delay = 5;

    public WaterGun(Brawl brawl) {
        super("WaterGun", new ItemBuilder(Material.INK_SACK)
                .data((byte) 12)
                .name(CC.GRAY + "\u00bb " + CC.AQUA + CC.BOLD + "Water Gun" + CC.GRAY + " \u00ab")
                .create()
        );
        this.brawl = brawl;
    }

    @Override
    public void onActivate(Player player) {
        if (this.hasCooldown(player, true)) return;
        this.addCooldown(player);

        FallingBlock block = player.getWorld().spawnFallingBlock(player.getEyeLocation(), Material.WATER, (byte)0);
        block.setMetadata("watergun", new FixedMetadataValue(brawl, player.getUniqueId()));
        block.setDropItem(false);
        block.setVelocity(player.getEyeLocation().getDirection().multiply(2));
    }

    @EventHandler
    public void onEntityChangeBlockEvent(EntityChangeBlockEvent event) {
        if (event.getEntityType() == EntityType.FALLING_BLOCK && event.getEntity().hasMetadata("watergun")) {
            event.getEntity().remove();
            event.setCancelled(true);

            Location location = event.getEntity().getLocation().clone();
            if (location.getBlock().isLiquid()) {
                location.add(0, 1, 0);
            }
            List<BlockState> blockData = new ArrayList<>();
            for (Location loc : this.getLocations(location)) {
                for(Entity entity : event.getEntity().getNearbyEntities(radius, radius + .5, radius)) {
                    if(entity instanceof Player) {
                        ((Player)entity).addPotionEffect(potionEffect);
                    }
                }

                if (loc.getBlock().getType() == Material.AIR) {
                    blockData.add(loc.getBlock().getState());
                    loc.getBlock().setType(Material.STATIONARY_WATER);
                    loc.getBlock().setMetadata("watergun", new FixedMetadataValue(brawl, null));
                }
            }
            new BukkitRunnable() {

                @Override
                public void run() {
                    blockData.forEach(blockData -> {
                        Block block = blockData.getBlock();
                        block.setType(Material.AIR);
                        block.removeMetadata("watergun", brawl);
                    });
                }

            }.runTaskLater(brawl, (delay * 20L));

        }
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        Block block = event.getBlock();
        if (block.isLiquid() && block.hasMetadata("watergun")) {
            event.setCancelled(true);
        }
    }

    private ArrayList<Location> getLocations(Location location) {
        ArrayList<Location> locations = new ArrayList<>();
        locations.add(location.clone().add(1.0D, 1.0D, -1.0D));
        locations.add(location.clone().add(-1.0D, 1.0D, -1.0D));
        locations.add(location.clone().add(1.0D, 1.0D, 1.0D));
        locations.add(location.clone().add(-1.0D, 1.0D, 1.0D));
        locations.add(location.clone().add(0.0D, 1.0D, 0.0D));
        locations.add(location.clone().add(-1.0D, 1.0D, 0.0D));
        locations.add(location.clone().add(1.0D, 1.0D, 0.0D));
        locations.add(location.clone().add(0.0D, 1.0D, -1.0D));
        locations.add(location.clone().add(0.0D, 1.0D, 1.0D));
        return locations;
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
