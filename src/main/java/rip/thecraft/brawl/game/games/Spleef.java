package rip.thecraft.brawl.game.games;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.game.Game;
import rip.thecraft.brawl.game.GameFlag;
import rip.thecraft.brawl.game.GameState;
import rip.thecraft.brawl.game.GameType;
import rip.thecraft.brawl.game.option.impl.StoreBlockOption;
import rip.thecraft.spartan.util.ItemBuilder;

import java.util.Collections;

public class Spleef extends Game implements Listener {

    public Spleef() {
        super(GameType.SPLEEF, GameFlag.WATER_ELIMINATE, GameFlag.NO_FALL, GameFlag.NO_PVP);
    }

    @Override
    public void start() {
        this.addOption(new StoreBlockOption(Collections.singletonList(Material.SNOW_BLOCK))
        .range(3)
        .materials(Material.SNOW_BALL));

        this.getOptions().values().forEach(option -> option.onStart(this));
    }

    @Override
    public void addItems(Player player) {
        player.getInventory().addItem(this.getItem());
    }

    @EventHandler
    public void onProjectileLand(ProjectileHitEvent event) {
        Projectile entity = event.getEntity();
        if (entity.getShooter() instanceof Player) {
            Player player = (Player) entity.getShooter();
            Game game = Brawl.getInstance().getGameHandler().getActiveGame();
            if (game instanceof Spleef && isAlive(player) && game.getState() == GameState.STARTED) {
                Block block = event.getHitBlock();
                if (block != null && block.getType() == Material.SNOW_BLOCK) {
                    if (containsOption(StoreBlockOption.class)) {
                        StoreBlockOption option = (StoreBlockOption) this.getOptions().get(StoreBlockOption.class);
                        option.getData().put(block.getLocation(), block.getState());
                        block.breakNaturally();
                    }
                }
            }
        }
    }

    private ItemStack getItem() {
        return new ItemBuilder(Material.DIAMOND_SPADE)
                .enchant(Enchantment.DIG_SPEED, 5).create();
    }
}
