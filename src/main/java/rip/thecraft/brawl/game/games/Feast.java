package rip.thecraft.brawl.game.games;

import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.game.Game;
import rip.thecraft.brawl.game.GameFlag;
import rip.thecraft.brawl.game.GameType;
import gg.manny.streamline.util.ItemBuilder;

import java.util.*;

/**
 * Created by Flatfile on 11/1/2021.
 */
public class Feast extends Game {

    public Feast(){
        super(GameType.FEAST, GameFlag.PLAYER_ELIMINATE, GameFlag.HUNGER, GameFlag.CRAFTING);
    }

    private List<Chest> chests;

    @Override
    public void setup() {
        chests = new ArrayList<>();

        this.getMap().getLocations().forEach((s, location) -> {
            if(s.toLowerCase().contains("chest")){
                if(location.getBlock().getState() instanceof Chest){
                    try{
                        chests.add((Chest) location.getBlock().getState());
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }

                    for(Chest chest : chests){
                        chest.getInventory().clear();

                        List<ItemStack> loot = getLootChest();
                        for(ItemStack item : loot){
                            int slot = -1;
                            while(slot == -1){
                                int r = getIntBetween(0, chest.getInventory().getSize());

                                if(chest.getInventory().getItem(r) == null || chest.getInventory().getItem(r).getType() == Material.AIR){
                                    slot = r;
                                }
                            }

                            chest.getInventory().setItem(slot, item);
                        }
                    }
                }
            }
        });

        this.getAlivePlayers().forEach(gamePlayer -> {
            Player player = gamePlayer.toPlayer();
            player.teleport(this.getLocationByName("Lobby"));
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 11, 2));
        });

        this.startTimer(10, true);
    }

    @Override
    public void start() {
        getAlivePlayers().forEach(gamePlayer -> addItems(gamePlayer.toPlayer()));
    }

    @Override
    public void addItems(Player player) {
        ItemStack redMush = new ItemBuilder(Material.RED_MUSHROOM).amount(16).build();
        ItemStack brownMush = new ItemBuilder(Material.BROWN_MUSHROOM).amount(16).build();
        ItemStack bowl = new ItemBuilder(Material.BOWL).amount(16).build();

        ItemStack sword = new ItemBuilder(Material.STONE_SWORD).build();

        player.getInventory().setItem(15, bowl);
        player.getInventory().setItem(16, brownMush);
        player.getInventory().setItem(17, redMush);

        player.getInventory().setItem(0, sword);
    }

    static int maxRating = 15;

    static Map<ItemStack, Integer> feastRatings = new HashMap<ItemStack, Integer>() {{
        put(new ItemBuilder(Material.COOKED_BEEF).amount(3).build(), 1);
        put(new ItemBuilder(Material.COOKED_CHICKEN).amount(3).build(), 2);
        put(new ItemBuilder(Material.IRON_SWORD).build(), 5);
        put(new ItemBuilder(Material.DIAMOND_SWORD).build(), 7);
        put(new ItemBuilder(Material.LEATHER_HELMET).build(), 6);
        put(new ItemBuilder(Material.LEATHER_CHESTPLATE).build(), 7);
        put(new ItemBuilder(Material.IRON_HELMET).build(), 8);
        put(new ItemBuilder(Material.IRON_CHESTPLATE).build(), 9);
        put(new ItemBuilder(Material.IRON_LEGGINGS).build(), 9);
        put(new ItemBuilder(Material.IRON_BOOTS).build(), 8);
        put(new ItemBuilder(Material.ENDER_PEARL).build(), 10);
        put(new ItemBuilder(Material.FISHING_ROD).build(), 3);
        put(new ItemBuilder(Material.DIAMOND_CHESTPLATE).build(), 11);
        put(new ItemBuilder(Material.POTION).data((short) 16420).build(), 8); //poison
        put(new ItemBuilder(Material.POTION).data((short) 16424).build(), 8); //weakness
        put(new ItemBuilder(Material.POTION).data((short) 16460).build(), 8); //harming
    }};

    private List<ItemStack> getLootChest() {
        List<ItemStack> loot = new ArrayList<>();
        int chessRating = 0, tries = 0;

        while (chessRating < maxRating && tries < 10) {
            tries = tries + 1;
            ItemStack item = (ItemStack) feastRatings.keySet().toArray()[getIntBetween(0, feastRatings.size())];
            int rating = feastRatings.get(item);

            if ((chessRating + rating) <= maxRating) {
                chessRating = chessRating + rating;
                loot.add(item);
            }
        }

        return loot;
    }

    private int getIntBetween(int x, int y) {
        return Brawl.RANDOM.nextInt(y-x) + x;
    }

}
