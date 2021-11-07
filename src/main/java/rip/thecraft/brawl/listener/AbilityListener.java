package rip.thecraft.brawl.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerOnGroundEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.projectiles.ProjectileSource;
import org.github.paperspigot.event.entity.ProjectileCollideEvent;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.ability.abilities.DoubleJump;
import rip.thecraft.brawl.ability.handlers.*;
import rip.thecraft.brawl.game.Game;
import rip.thecraft.brawl.game.GameFlag;
import rip.thecraft.brawl.game.team.GamePlayer;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.kit.KitHandler;
import rip.thecraft.brawl.util.moreprojectiles.event.BlockProjectileHitEvent;
import rip.thecraft.brawl.util.moreprojectiles.event.ItemProjectileHitEvent;

@RequiredArgsConstructor
public class AbilityListener implements Listener {

    private final Brawl plugin;

    @EventHandler
    public void onPlayerGround(PlayerOnGroundEvent event) {
        Player player = event.getPlayer();
        Kit selectedKit = KitHandler.getEquipped(player);

        Game game = Brawl.getInstance().getGameHandler().getActiveGame();
        if (game != null && game.containsPlayer(player)) {
            GamePlayer gamePlayer = game.getGamePlayer(player);
            if (gamePlayer.isAlive()) {
                if(game.getFlags().contains(GameFlag.DOUBLE_JUMP)){
                    Brawl.getInstance().getAbilityHandler().getAbilityByClass(DoubleJump.class)
                            .onGround(gamePlayer.toPlayer(), event.getOnGround());
                }
            }
        }

        if(selectedKit != null){
            selectedKit.getAbilities().forEach(ability -> {
                if (ability instanceof GroundHandler) {
                    ((GroundHandler) ability).onGround(player, event.getOnGround());
                }
            });
        }
    }

    @EventHandler
    public void onToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        Kit selectedKit = KitHandler.getEquipped(player);
        if (selectedKit != null) {
            selectedKit.getAbilities().forEach(ability -> {
                if (ability instanceof SneakHandler) {
                    ((SneakHandler) ability).onSneak(player, event.isSneaking());
                }
            });
        }
    }

    @EventHandler
    public void onToggleFlight(PlayerToggleFlightEvent event){
        Player player = event.getPlayer();
        Kit selectedKit = KitHandler.getEquipped(player);

        Game game = Brawl.getInstance().getGameHandler().getActiveGame();
        if (game != null && game.containsPlayer(player)) {
            GamePlayer gamePlayer = game.getGamePlayer(player);
            if (gamePlayer.isAlive()) {
                if(game.getFlags().contains(GameFlag.DOUBLE_JUMP)){
                    Brawl.getInstance().getAbilityHandler().getAbilityByClass(DoubleJump.class)
                            .onFlight(gamePlayer.toPlayer(), event.isFlying());
                }
            }
        }

        if(selectedKit != null){
            selectedKit.getAbilities().forEach(ability -> {
                if(ability instanceof ToggleFlightHandler){
                    ((ToggleFlightHandler) ability).onFlight(player, event.isFlying());
                }
            });
        }
    }

    @EventHandler
    public void onProject(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getProjectile() instanceof Arrow) {
                Arrow arrow = (Arrow) event.getProjectile();
                arrow.setMetadata("ShotFrom", new FixedMetadataValue(plugin, event.getEntity().getLocation()));
                arrow.setMetadata("Force", new FixedMetadataValue(plugin, event.getForce()));
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Kit selectedKit = KitHandler.getEquipped(player);
        if (event.hasItem() && event.getItem() != null && selectedKit != null && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {

            boolean cancelled = false;
            for (Ability ability : selectedKit.getAbilities()) {
                if (!cancelled && (ability instanceof InteractItemHandler && ((InteractItemHandler) ability).onInteractItem(player, event.getAction(), event.getItem()))) {
                    cancelled = true; // Allow continue iteration but also cancel if found a match
                }
            }

            if (cancelled) {
                event.setUseInteractedBlock(Event.Result.DENY);
                event.setUseItemInHand(Event.Result.DENY);
                event.setCancelled(true);
                player.updateInventory(); // prevent stupid glitches
            }
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        ProjectileSource shooter = event.getEntity().getShooter();
        if (shooter instanceof Player) {
            Player player = (Player) shooter;
            Kit selectedKit = KitHandler.getEquipped(player);
            if (selectedKit != null) {
                for (Ability ability : selectedKit.getAbilities()) {
                    if (ability instanceof ProjectileLaunchHandler) {
                        if (((ProjectileLaunchHandler) ability).onProjectileLaunch(player, event.getEntity())) {
                            event.setCancelled(true);
                            break;
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onProjectileCollide(ProjectileCollideEvent event) {
        Projectile projectile = event.getEntity();
        if (projectile.getShooter() instanceof Player) {
            Player player = (Player) projectile.getShooter();
            Entity victim = event.getCollidedWith();
            Kit selectedKit = KitHandler.getEquipped(player);
            if (selectedKit != null) {
                for (Ability ability : selectedKit.getAbilities()) {
                    if (ability instanceof ProjectileCollideHandler) {
                        if (((ProjectileCollideHandler) ability).onProjectileCollide(player, victim, projectile)) {
                            event.setCancelled(true);
                            break;
                        }
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || event.isCancelled()) return;

        Player shooter = null;
        if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();
            if (projectile.getShooter() instanceof Player) {
                shooter = (Player) projectile.getShooter();
            }
        }
        if (shooter != null) {
            Kit selectedKit = KitHandler.getEquipped(shooter);
            if (selectedKit != null) {
                for (Ability ability : selectedKit.getAbilities()) {
                    if (ability instanceof ProjectileHitHandler) {
                        if (((ProjectileHitHandler) ability).onProjectileHit(shooter, (Player) event.getEntity(), event)) {
                            event.setCancelled(true);
                            break;
                        }
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onItemProjectileHitEntity(ItemProjectileHitEvent event){
        if(event.isCancelled()) return;

        if(event.getHitBlock() != null){
            Player shooter = null;

            if(event.getProjectile() != null){
                if(event.getProjectile().getShooter() != null && event.getProjectile().getShooter() instanceof Player){
                    shooter = (Player) event.getProjectile().getShooter();
                }

                if(shooter != null){
                    Kit selectedKit = KitHandler.getEquipped(shooter);

                    if(selectedKit != null){
                        for(Ability ability : selectedKit.getAbilities()){
                            if(ability instanceof ItemProjectileHitBlockHandler){
                                if(((ItemProjectileHitBlockHandler) ability).onItemProjectileHitBlock(shooter, event)){
                                    event.setCancelled(true);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        if(event.getHitEntity() != null && event.getHitEntity() instanceof Player){
            Player shooter = null;

            if(event.getProjectile() != null){
                if(event.getProjectile().getShooter() != null && event.getProjectile().getShooter() instanceof Player){
                    shooter = (Player) event.getProjectile().getShooter();
                }

                if(shooter != null){
                    Kit selectedKit = KitHandler.getEquipped(shooter);

                    if(selectedKit != null){
                        for(Ability ability : selectedKit.getAbilities()){
                            if(ability instanceof ItemProjectileHitHandler){
                                if(((ItemProjectileHitHandler) ability).onItemProjectileHit(shooter, (Player) event.getHitEntity(), event)){
                                    event.setCancelled(true);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onBlockProjectileHitEntity(BlockProjectileHitEvent event){
        if(event.isCancelled()) return;

        if(event.getHitBlock() != null){
            Player shooter = null;

            if(event.getProjectile() != null){
                if(event.getProjectile().getShooter() != null && event.getProjectile().getShooter() instanceof Player){
                    shooter = (Player) event.getProjectile().getShooter();
                }

                if(shooter != null){
                    Kit selectedKit = KitHandler.getEquipped(shooter);

                    if(selectedKit != null){
                        for(Ability ability : selectedKit.getAbilities()){
                            if(ability instanceof BlockProjectileHitBlockHandler){
                                if(((BlockProjectileHitBlockHandler) ability).onBlockProjectileHitBlock(shooter, event)){
                                    event.setCancelled(true);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        if(event.getHitEntity() != null && event.getHitEntity() instanceof Player){
            Player shooter = null;

            if(event.getProjectile() != null){
                if(event.getProjectile().getShooter() != null && event.getProjectile().getShooter() instanceof Player){
                    shooter = (Player) event.getProjectile().getShooter();
                }

                if(shooter != null){
                    Kit selectedKit = KitHandler.getEquipped(shooter);

                    if(selectedKit != null){
                        for(Ability ability : selectedKit.getAbilities()){
                            if(ability instanceof BlockProjectileHitHandler){
                                if(((BlockProjectileHitHandler) ability).onBlockProjectileHit(shooter, (Player) event.getHitEntity(), event)){
                                    event.setCancelled(true);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
