package rip.thecraft.brawl.kit.ability.abilities;

import com.comphenix.net.bytebuddy.utility.RandomString;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.kit.ability.Ability;
import rip.thecraft.brawl.kit.ability.handlers.BlockProjectileHitHandler;
import rip.thecraft.brawl.kit.ability.property.AbilityData;
import rip.thecraft.brawl.kit.ability.property.AbilityProperty;
import rip.thecraft.brawl.server.region.RegionType;
import rip.thecraft.brawl.util.ParticleEffect;
import gg.manny.streamline.util.moreprojectiles.event.BlockProjectileHitEvent;
import gg.manny.streamline.util.moreprojectiles.event.CustomProjectileHitEvent;
import gg.manny.streamline.util.moreprojectiles.projectile.BlockProjectile;

import java.util.ArrayList;
import java.util.List;

@AbilityData(
        name = "Melon Toss",
        description = "Throw a melon at your enemies tossing them in the air.",
        icon = Material.SPECKLED_MELON,
        color = ChatColor.GREEN
)
public class Melon extends Ability implements BlockProjectileHitHandler, Listener {

    private final static String MELON_METADATA = "Melon";

    @AbilityProperty(id = "throw-power", description = "Power of throwing the melon")
    public double throwPower = 0.85;

    @AbilityProperty(id = "power", description = "Power of velocity to hit player")
    public double powerMultiplier = 3.6;

    @AbilityProperty(id = "vertical", description = "Launch distance in the air")
    public double vertical = 2.4;

    @AbilityProperty(id = "damage", description = "Amount of damage a player should take from impact")
    public int damage = 6;

    @AbilityProperty(id = "fall-damage-reduction", description = "Reduces inflicted fall distance damage")
    public double fallDamageReduction = 3.5;

    @AbilityProperty(id = "max-fall-damage", description = "Max fall damage applied")
    public double maxFallDamage = 20;

    @Override
    public void onActivate(Player player) {
        if (hasCooldown(player, true)) return;
        addCooldown(player);

        BlockProjectile melon = new BlockProjectile(MELON_METADATA, player, Material.MELON_BLOCK.getId(), 0, (float) throwPower);
        melon.addTypedRunnable((block) ->
                ParticleEffect.BLOCK_DUST.display(new ParticleEffect.BlockData(Material.MELON_BLOCK, (byte) 0), 0, 0, 0, 0.1f, 5, block.getEntity().getLocation(), 30));
        player.playSound(player.getLocation(), Sound.SHOOT_ARROW, 1, 2);
    }

    @Override
    public boolean onBlockProjectileHit(Player shooter, Player victim, BlockProjectileHitEvent event) {
        if (event.getHitType() == CustomProjectileHitEvent.HitType.ENTITY && event.getProjectile().getProjectileName().equals(MELON_METADATA)) {
            if (shooter == victim || RegionType.SAFEZONE.appliesTo(victim.getLocation()) || victim.isDead()) return true;

            List<Item> items = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                ItemStack itemStack = new ItemStack(Material.MELON, 1);
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName(RandomString.make(12) + MELON_METADATA);
                itemStack.setItemMeta(itemMeta);
                Item item = victim.getWorld().dropItem(victim.getLocation(), itemStack);
                item.setPickupDelay(Integer.MAX_VALUE);
                Vector v = Vector.getRandom();
                v.setX(v.getX() - 0.25f);
                v.setZ(v.getZ() - 0.25f);
                item.setVelocity(v);
                items.add(item);
            }
            Bukkit.getScheduler().runTaskLater(Brawl.getInstance(), () -> items.forEach(Item::remove), 15L);

            victim.damage(damage, shooter);
            Vector unitVector = event.getProjectile().getEntity().getVelocity().normalize();
            victim.setVelocity(unitVector
                    .multiply(powerMultiplier)
                    .setY(vertical)
            );
            victim.getWorld().playSound(victim.getLocation(), Sound.EXPLODE, 1, 2);
            victim.setMetadata(MELON_METADATA, new FixedMetadataValue(Brawl.getInstance(), true));
        }
        return false;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            if (entity.hasMetadata(MELON_METADATA)) {
                event.setDamage(Math.min(maxFallDamage, event.getDamage() / fallDamageReduction));
                entity.removeMetadata(MELON_METADATA, Brawl.getInstance());
            }
        }
    }
}
