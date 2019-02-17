package gg.manny.brawl.game.type;

import gg.manny.brawl.Locale;
import gg.manny.brawl.game.Game;
import gg.manny.brawl.game.GameState;
import gg.manny.brawl.game.GameType;
import gg.manny.brawl.game.option.impl.StoreBlockOption;
import gg.manny.pivot.util.PivotUtil;
import gg.manny.pivot.util.PlayerUtils;
import gg.manny.pivot.util.inventory.ItemBuilder;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Spleef extends Game {

    public Spleef() {
        super(GameType.SPLEEF);

        this.getPlayers().forEach(player -> player.setAlive(true));
        this.getAlivePlayers().forEach(gamePlayer -> {
            Player player = gamePlayer.toPlayer();
            PlayerUtils.resetInventory(player, GameMode.SURVIVAL);

            player.getInventory().addItem(this.getItem());
            player.teleport(this.getRandomLocation());
        });

        this.startTimer(5, true);

    }

    @Override
    public void onStart() {
        this.setState(GameState.STARTED);
        this.setStartedAt(System.currentTimeMillis());

        this.broadcast(this.getVariables(Locale.GAME_START.get()));

        this.getOptions().add(new StoreBlockOption(Collections.singletonList(Material.SNOW_BLOCK)));
        this.getOptions().forEach(option -> option.onStart(this));
    }

    @Override
    public void onEnd(List<String> winners) {
        this.setEndedAt(System.currentTimeMillis());
        this.setState(GameState.FINISHED);

        
        PivotUtil.runLater(() -> this.getOptions().forEach(option -> option.onEnd(this)), TimeUnit.SECONDS.toMillis(3), false);
    }

    @Override
    public void destroy() {

    }

    @Override
    public void onEliminate(Player player) {

    }

    @Override
    public void handleElimination(Player player, Location location, boolean disconnected) {

    }

    private ItemStack getItem() {
        return new ItemBuilder(Material.DIAMOND_SPADE)
                .enchant(Enchantment.DIG_SPEED, 5).create();
    }
}
