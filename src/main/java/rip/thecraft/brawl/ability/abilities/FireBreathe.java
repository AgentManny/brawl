package rip.thecraft.brawl.ability.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.ability.handlers.ItemProjectileHitHandler;
import rip.thecraft.brawl.ability.property.AbilityData;
import rip.thecraft.brawl.ability.property.AbilityProperty;
import rip.thecraft.brawl.ability.task.AbilityTask;
import rip.thecraft.brawl.region.RegionType;
import rip.thecraft.brawl.util.ParticleEffect;
import rip.thecraft.brawl.util.moreprojectiles.event.CustomProjectileHitEvent;
import rip.thecraft.brawl.util.moreprojectiles.event.ItemProjectileHitEvent;
import rip.thecraft.brawl.util.moreprojectiles.projectile.ItemProjectile;

/**
 * Created by Flatfile on 11/3/2021.
 */
@AbilityData(
        name = "Fire Breathe",
        description = "Spit fire onto your enemies, burning them.",
        color = ChatColor.GOLD,
        icon = Material.FIREBALL
)
public class FireBreathe extends Ability implements ItemProjectileHitHandler {

    @AbilityProperty(id = "duration", description = "Duration of the task")
    public long taskDuration = 1500;

    @AbilityProperty(id = "fire-ticks", description = "Adjust amount of ticks player is set on fire for")
    public int fireTicks = 120;

    @AbilityProperty(id = "damage-value", description = "Adjust the amount of damage done to a player.")
    public double damageValue = 5.5D;

    public void onActivate(Player player) {
        if (hasCooldown(player, true)) return;
        addCooldown(player);

        new FireBreatheTask(this, player).start();
        player.playSound(player.getLocation(), Sound.ENDERDRAGON_GROWL, 10f, 2f);
    }

    @Override
    public boolean onItemProjectileHit(Player shooter, Player hit, ItemProjectileHitEvent event) {
        if(event.getHitType() == CustomProjectileHitEvent.HitType.ENTITY && event.getProjectile().getProjectileName().equals("firebreathe")){
            if(!canAttack(hit)) return true;
            if(hit == shooter) return true;

            if(!hit.isDead()){
                hit.damage(damageValue, shooter);
                hit.setFireTicks(fireTicks);
            }
        }
        return false;
    }

    private boolean canAttack(Player player){
        return !RegionType.SAFEZONE.appliesTo(player.getLocation());
    }

    private class FireBreatheTask extends AbilityTask {

        protected FireBreatheTask(FireBreathe fireBreathe, Player player){
            super(fireBreathe, player, taskDuration, 1L);
        }

        @Override
        public void onTick() {
            if(player.isOnline() && !player.isDead()){
                new ItemProjectile("firebreathe", player, new ItemStack(Material.BLAZE_POWDER), 1f){
                    @Override
                    public void t_() {
                        if(!getEntity().isDead()){
                            ParticleEffect.FLAME.display(0, 0, 0, 0, 1, getEntity().getLocation(), 30);
                            ParticleEffect.FLAME.display(1, 0, 1, 0, 1, getEntity().getLocation(), 30);
                            ParticleEffect.FLAME.display(-1, 0, -1, 0, 1, getEntity().getLocation(), 30);
                        }

                        super.t_();
                    }
                };
            }
        }

        @Override
        public boolean shouldCancel() {
            boolean cancel = super.shouldCancel();
            if (RegionType.SAFEZONE.appliesTo(player.getLocation())) {
                cancel = true;
            }
            return cancel;
        }

        @Override
        public void onCancel() {

        }
    }

}
