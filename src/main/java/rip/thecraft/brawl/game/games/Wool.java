package rip.thecraft.brawl.game.games;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.game.Game;
import rip.thecraft.brawl.game.GameFlag;
import rip.thecraft.brawl.game.GameType;
import rip.thecraft.brawl.game.option.impl.StoreBlockOption;
import rip.thecraft.spartan.util.ItemBuilder;

import java.util.Arrays;

/**
 * Created by Flatfile on 10/22/2021.
 */
public class Wool extends Game implements Listener {

    public Wool(){
        super(GameType.WOOL, GameFlag.PLAYER_ELIMINATE);
    }

    @Override
    public void setup() {
        this.getAlivePlayers().forEach(gamePlayer -> {
            Player player = gamePlayer.toPlayer();
            player.teleport(getLocationByName("Lobby"));
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 6, 2));
        });

        this.startTimer(5, true);
    }

    @Override
    public void start() {
        this.addOption(new StoreBlockOption(Arrays.asList(Material.WOOL, Material.SPONGE)).materials(Material.WOOL, Material.SPONGE).range(2));
        this.getOptions().values().forEach(option -> option.onStart(this));

        getAlivePlayers().forEach(gamePlayer -> addItems(gamePlayer.toPlayer()));
    }

    @Override
    public void addItems(Player player) {//todo clean this up
        ItemStack sword = new ItemBuilder(Material.DIAMOND_SWORD).enchant(Enchantment.DAMAGE_ALL, 1).build();
        ItemStack bow = new ItemBuilder(Material.BOW).enchant(Enchantment.ARROW_INFINITE, 1).enchant(Enchantment.ARROW_DAMAGE, 1).build();
        ItemStack wool = new ItemBuilder(Material.WOOL).data((byte) 14).amount(64).build();
        ItemStack sponge = new ItemBuilder(Material.SPONGE).amount(32).build();

        player.getInventory().setArmorContents(new ItemStack[]{new ItemStack(Material.IRON_BOOTS),
                new ItemStack(Material.IRON_LEGGINGS),
                new ItemStack(Material.IRON_CHESTPLATE),
                new ItemStack(Material.IRON_HELMET)});

        player.getInventory().setItem(0, sword);
        player.getInventory().setItem(1, bow);
        player.getInventory().setItem(2, wool);
        player.getInventory().setItem(3, sponge);
        player.getInventory().setItem(9, new ItemStack(Material.ARROW));


        for(int i = 0; i < player.getInventory().getSize(); i++){
            player.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP));
        }

    }

    @EventHandler
    public void onProjectileLand(ProjectileHitEvent event) {
        Projectile entity = event.getEntity();
        if (entity.getShooter() instanceof Player) {
            Player player = (Player) entity.getShooter();
            if (Brawl.getInstance().getGameHandler().getActiveGame() instanceof Wool) {
                if (containsPlayer(player)) {
                    if (entity.getLocation().getBlock() != null && event.getEntity().getLocation().getBlock().getType() == Material.WOOL) {
                        event.getEntity().getLocation().getBlock().breakNaturally();
                    }
                }
            }
        }
    }

}
