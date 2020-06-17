package rip.thecraft.brawl.duelarena.match.data;

import rip.thecraft.brawl.duelarena.match.Match;
import rip.thecraft.brawl.util.WrappedItemStack;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;
import java.util.UUID;

@Data
@RequiredArgsConstructor
public class PostMatchData {

    private WrappedItemStack[] armorContents;
    private WrappedItemStack[] inventoryContents;

    private int health;
    private Collection<PotionEffect> potionEffects;
    private int food;
    private int totalHits;
    private int longestCombo;
    private boolean soup;

    private UUID opponent;

    public static PostMatchData fromPlayer(Player player) {
        PostMatchData postMatchData = new PostMatchData();

        postMatchData.setArmorContents(player.getInventory().getArmorContents());
        postMatchData.setInventoryContents(player.getInventory().getContents());
        postMatchData.setHealth((int) Math.ceil(player.getHealth()));
        postMatchData.setFood(player.getFoodLevel());
        postMatchData.setPotionEffects(player.getActivePotionEffects());

        postMatchData.setTotalHits(0);

        postMatchData.setSoup(player.getInventory().contains(Material.MUSHROOM_SOUP));

        return (postMatchData);
    }

    public static void addData(Match match, Player loser, Player opponent) {
        PostMatchData playerData = fromPlayer(loser);
        playerData.setTotalHits(match.getMatchData().getTotalHits().getOrDefault(loser.getUniqueId(), 0));
        playerData.setLongestCombo(match.getMatchData().getLongestCombo().getOrDefault(loser.getUniqueId(), 0));

        PostMatchData opponentData = fromPlayer(opponent);
        opponentData.setTotalHits(match.getMatchData().getTotalHits().getOrDefault(opponent.getUniqueId(), 0));
        opponentData.setLongestCombo(match.getMatchData().getLongestCombo().getOrDefault(opponent.getUniqueId(), 0));


        playerData.setHealth(0);

        opponentData.setOpponent(loser.getUniqueId());
        playerData.setOpponent(opponent.getUniqueId());

        match.getMatchData().getInventories().put(loser.getUniqueId(),playerData);
        match.getMatchData().getInventories().put(opponent.getUniqueId(), opponentData);
    }

    public ItemStack[] getArmorContents() {
        return (WrappedItemStack.unbox(armorContents));
    }

    public void setArmorContents(ItemStack[] armorContents) {
        this.armorContents = WrappedItemStack.box(armorContents);

}
    public ItemStack[] getInventoryContents() {
        return (WrappedItemStack.unbox(inventoryContents));
    }

    public void setInventoryContents(ItemStack[] inventoryContents) {
        this.inventoryContents = WrappedItemStack.box(inventoryContents);
    }

}