package rip.thecraft.brawl.kit.ability.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.kit.ability.Ability;
import rip.thecraft.brawl.kit.ability.property.AbilityData;
import rip.thecraft.brawl.kit.ability.property.AbilityProperty;
import rip.thecraft.brawl.kit.ability.task.AbilityTask;
import rip.thecraft.brawl.kit.KitHandler;
import rip.thecraft.brawl.player.protection.Protection;
import rip.thecraft.brawl.server.region.RegionType;
import rip.thecraft.brawl.util.BlockUtil;
import rip.thecraft.brawl.util.PlayerUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@AbilityData(
        name = "Time Lock",
        description = "Freeze nearby enemies in time.",
        color = ChatColor.GOLD,
        icon = Material.WATCH
)
public class TimeLock extends Ability {

    public static final String TIMELOCK_METADATA = "ABILITY_TIMELOCK";

    @AbilityProperty(id = "radius", description = "Radius of where it should slow")
    public int radius = 10;

    @AbilityProperty(id = "duration-ticks", description = "Freeze time for X ticks (1s = 20 tick)")
    public int durationTicks = 100;

//    @AbilityProperty(id = "jump-boost", description = "Apply jump boost to prevent jumping")
//    public boolean jumpBoost = false;
//
//    @AbilityProperty(id = "velocity-jump-prevent", description = "Apply a timer to prevent jumping with velocity")
//    public boolean velocityJumpPrevent = true;

    @AbilityProperty(id = "teleport", description = "Teleport a player back if they moved too far while slowed")
    public boolean teleport = true;

//    @AbilityProperty(id = "teleport-distance", description = "Distance teleport back")
//    public double teleportDistance  = 0.5;
//
//    @AbilityProperty(id = "velocity-downward-force", description = "Push players downwards force")
//    public double velocityPower = -.25;

    @Override
    public void onActivate(Player player) {
        if (hasCooldown(player, true)) return;
        addCooldown(player);

        List<Player> enemiesFrozen = new ArrayList<>();
//        int enemiesFrozen = 0;
        for (Player enemy : PlayerUtil.getNearbyPlayers(player, radius)) {
            if (!Protection.isAlly(player, enemy)) {
                Brawl.getInstance().getEffectRestorer().setRestoreEffect(enemy, new PotionEffect(PotionEffectType.SLOW, durationTicks, 120, false, true));
//                if (jumpBoost) {
//                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, durationTicks, 240, false, true));
//                }
                enemiesFrozen.add(enemy);
                enemy.setMetadata(TIMELOCK_METADATA, new FixedMetadataValue(Brawl.getInstance(), enemy.getLocation().clone()));
            }
        }
//        new TimeLockSlow(this, player, enemiesFrozen.iterator()).start();
        player.sendMessage(ChatColor.YELLOW + "You've froze " + ChatColor.AQUA + enemiesFrozen.size() + ChatColor.YELLOW + " nearby enemies.");
    }

    private class TimeLockSlow extends AbilityTask {

        private List<Player> players;

        protected TimeLockSlow(Ability ability, Player player, List<Player> players) {
            super(ability, player, TimeUnit.SECONDS.toMillis(durationTicks / 20), 2L);
            this.players = players;
        }

        @Override
        public void onTick() {
            for (Player affectedPlayer : players) {
                if (affectedPlayer == null || RegionType.SAFEZONE.appliesTo(affectedPlayer.getLocation()) || KitHandler.getEquipped(affectedPlayer) == null) {
                    continue;
                }
                for (PotionEffect activePotionEffect : affectedPlayer.getActivePotionEffects()) {
                    if (activePotionEffect.getType() == PotionEffectType.SLOW && activePotionEffect.getAmplifier() == 120) {
                        boolean onGround = BlockUtil.isOnGround(affectedPlayer.getLocation(), 1);
                        affectedPlayer.sendMessage(onGround + " YE");
                    }
                }
            }
        }

        @Override
        public void onCancel() {

        }
    }
}