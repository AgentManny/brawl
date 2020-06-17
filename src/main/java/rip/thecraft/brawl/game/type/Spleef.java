package rip.thecraft.brawl.game.type;

import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.game.Game;
import rip.thecraft.brawl.game.GameFlag;
import rip.thecraft.brawl.game.GameType;
import rip.thecraft.brawl.game.option.impl.StoreBlockOption;
import rip.thecraft.spartan.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

public class Spleef extends Game implements Listener {

    public Spleef() {
        super(GameType.SPLEEF, GameFlag.WATER_ELIMINATE, GameFlag.NO_FALL);
    }

    @Override
    public void setup() {
        this.getAlivePlayers().forEach(gamePlayer -> {
            Player player = gamePlayer.toPlayer();
            player.getInventory().addItem(this.getItem());
            player.teleport(this.getRandomLocation());
        });

        this.startTimer(5, true);
    }

    @Override
    public void start() {
        this.addOption(new StoreBlockOption(Collections.singletonList(Material.SNOW_BLOCK))
        .range(3)
        .materials(Material.SNOW_BALL));

        this.getOptions().values().forEach(option -> option.onStart(this));
    }



    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && !(event.getDamager() instanceof Snowball)) {
            Player player = (Player) event.getEntity();
            Game game = Brawl.getInstance().getGameHandler().getActiveGame();
            if (game instanceof Spleef) {
                if (player != null && this.containsPlayer(player) && this.getGamePlayer(player).isAlive()) {
                    event.setCancelled(true); // Only allows damage from snowballs
                }
            }
        }
    }

    @EventHandler
    public void onProjectileLand(ProjectileHitEvent event) {
        Entity entity = event.getEntity();
        if (entity.getLocation().getBlock() != null && event.getEntity().getLocation().getBlock().getType() == Material.SNOW_BLOCK) {
            if (this.containsOption(StoreBlockOption.class)) {
                StoreBlockOption option = (StoreBlockOption) this.getOptions().get(StoreBlockOption.class);
                option.getData().put(event.getEntity().getLocation(), event.getEntity().getLocation().getBlock().getState());

                event.getEntity().getLocation().getBlock().breakNaturally();
            }
        }
    }


    private ItemStack getItem() {
        return new ItemBuilder(Material.DIAMOND_SPADE)
                .enchant(Enchantment.DIG_SPEED, 5).create();
    }
}