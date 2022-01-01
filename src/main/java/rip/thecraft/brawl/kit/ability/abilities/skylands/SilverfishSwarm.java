package rip.thecraft.brawl.kit.ability.abilities.skylands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.kit.ability.Ability;
import rip.thecraft.brawl.kit.ability.property.AbilityData;
import rip.thecraft.brawl.kit.ability.property.AbilityProperty;
import rip.thecraft.brawl.util.SchedulerUtil;

import java.util.ArrayList;
import java.util.List;

@AbilityData(
        name = "Silverfish Swarm",
        description = "Spawn a swarm of silverfish to attack surrounding enemies.",
        color = ChatColor.DARK_AQUA,
        icon = Material.INK_SACK,
        data = 6
)
public class SilverfishSwarm extends Ability implements Listener {

    @AbilityProperty(id = "duration")
    public double duration = 10;

    @AbilityProperty(id = "slow-duration")
    public double slowDuration = 5;

    @AbilityProperty(id = "health", description = "Health of each silverfish")
    public double health = 15;

    @AbilityProperty(id = "radius")
    public double radius = 2.5;

    @Override
    public void onActivate(Player player) {
        if (hasCooldown(player, true)) return;
        addCooldown(player);

        List<Entity> entities = new ArrayList<>();
        Location location = player.getLocation().clone();
        for (int degree = 0; degree < 360; degree += 60) {
            double radians = Math.toRadians(degree);
            double x = Math.cos(radians) * radius;
            double z = Math.sin(radians) * radius;
            location.add(x, 0, z );

            Silverfish entity = (Silverfish) player.getWorld().spawnEntity(location, EntityType.SILVERFISH);
            entity.setMetadata("swarm", new FixedMetadataValue(Brawl.getInstance(), player.getUniqueId().toString()));
            entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
            entity.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1));
            entity.setMaxHealth(health);
            entity.setHealth(health);

            entities.add(entity);
            location.subtract(x, 0, z);
        }

        SchedulerUtil.runTaskLater(() -> {
            for (Entity entity : entities) {
                if (entity != null && !entity.isDead() && entity.isValid()) {
                    entity.remove();
                }
            }
        }, (long) (duration * 20L), false);

        player.addPotionEffect(PotionEffectType.SLOW.createEffect((int) (slowDuration * 40), 2));
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        if (event.getEntity() instanceof Silverfish && event.getEntity().hasMetadata("swarm") && event.getTarget() instanceof Player) {
            Player player = (Player) event.getTarget();
            List<MetadataValue> metaData = event.getEntity().getMetadata("swarm");
            if (player.getUniqueId().toString().equals(metaData.get(0).asString())) {
                event.setCancelled(true);
                event.setTarget(null);
            }
        }
    }
}
