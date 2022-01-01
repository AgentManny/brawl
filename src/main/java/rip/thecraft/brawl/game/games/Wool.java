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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.game.Game;
import rip.thecraft.brawl.game.GameFlag;
import rip.thecraft.brawl.game.GameState;
import rip.thecraft.brawl.game.GameType;
import rip.thecraft.brawl.game.option.impl.StoreBlockOption;
import rip.thecraft.brawl.server.item.item.Armor;
import rip.thecraft.brawl.server.item.item.Items;
import gg.manny.streamline.util.ItemBuilder;

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
    public void addItems(Player player) {
        Items items = new Items(
                new ItemBuilder(Material.DIAMOND_SWORD).enchant(Enchantment.DAMAGE_ALL, 1).build(),
                new ItemBuilder(Material.BOW).enchant(Enchantment.ARROW_INFINITE, 1).enchant(Enchantment.ARROW_DAMAGE, 1).build(),
                new ItemBuilder(Material.WOOL).data((byte) 14).amount(64).build(),
                new ItemBuilder(Material.SPONGE).amount(32).build()
        );
        player.getInventory().setContents(items.getItems());

        Armor armor = new Armor(Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS);
        armor.apply(player);
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
            Game game = Brawl.getInstance().getGameHandler().getActiveGame();
            if (game instanceof Wool && isAlive(player) && game.getState() == GameState.STARTED) {
                Block block = event.getHitBlock();
                if (block != null) {
                    if(block.getType() == Material.WOOL || block.getType() == Material.SPONGE){
                        block.setType(Material.AIR);
                    }
                }
            }
        }
    }

}
