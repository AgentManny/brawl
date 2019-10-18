package gg.manny.brawl.game.type;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.game.Game;
import gg.manny.brawl.game.GameFlag;
import gg.manny.brawl.game.GameState;
import gg.manny.brawl.game.GameType;
import gg.manny.brawl.game.option.impl.StoreBlockOption;
import gg.manny.brawl.game.team.GamePlayer;
import gg.manny.pivot.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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

public class FFA extends Game implements Listener {

    public FFA() {
        super(GameType.FFA, GameFlag.PLAYER_ELIMINATE);
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
        this.addOption(new StoreBlockOption(Collections.singletonList(Material.SNOW_BLOCK)));
        this.getOptions().values().forEach(option -> option.onStart(this));
    }

    @Override
    public boolean eliminate(Player player) {
        GamePlayer eliminated = getGamePlayer(player);
        if (eliminated == null || !eliminated.isAlive() || this.state == GameState.FINISHED) return false;

        eliminated.setAlive(false); // Died
        return true;
    }

    @Override
    public void handleElimination(Player player, Location location, boolean disconnected) {
        if (eliminate(player)) {
            broadcast(ChatColor.DARK_RED + player.getName() + ChatColor.RED + (disconnected ? " disconnected" : " has been eliminated") + ".");
            if (!disconnected) {
                player.setAllowFlight(true);
                player.setFlying(true);

                player.teleport(this.getRandomLocation().add(0, 1, 0));
            }

            // Find a winner
            if (this.getAlivePlayers().size() == 1) {
                GamePlayer winner = this.getAlivePlayers().get(0);
                this.winners.add(winner);

                this.end();
            }
        }
    }

    private ItemStack getItem() {
        return new ItemBuilder(Material.DIAMOND_SPADE)
                .enchant(Enchantment.DIG_SPEED, 5).create();
    }
}
