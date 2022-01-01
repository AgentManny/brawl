package rip.thecraft.brawl.duelarena.loadout;

import com.mongodb.lang.Nullable;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import rip.thecraft.brawl.duelarena.arena.ArenaType;
import rip.thecraft.brawl.server.item.item.Armor;
import rip.thecraft.brawl.server.item.item.Items;
import rip.thecraft.brawl.kit.type.RefillType;
import rip.thecraft.brawl.util.PlayerUtil;

public abstract class MatchLoadout implements Comparable<MatchLoadout> {

    public String getName() {
        return getClass().getSimpleName();
    }

    public abstract ChatColor getColor();

    public abstract Material getIcon();

    public byte getIconData() {
        return (byte) 0;
    }

    public abstract Armor getArmor();
    public abstract Items getItems();

    public PotionEffect[] getEffects() {
        return new PotionEffect[]{};
    }

    public abstract RefillType getRefillType();

    public boolean isRanked() {
        return false;
    }

    public int getHealingAmount() {
        return 0;
    }

    public ArenaType getArena() {
        return ArenaType.NORMAL;
    }

    public abstract int getWeight();

    public void apply(Player player) {
        PlayerUtil.resetInventory(player, GameMode.SURVIVAL);

        if (getArmor() != null) {
            getArmor().apply(player);
        }

        if (getItems() != null) {
            player.getInventory().setContents(getItems().getItems());
        }

        for (PotionEffect effect : getEffects()) {
            player.addPotionEffect(effect, true);
        }

        if (this.getRefillType().getItem().getType() != Material.AIR) {
            int healed = 0;
            while (player.getInventory().firstEmpty() != -1 && (getHealingAmount() == -1 || healed++ < getHealingAmount())) {
                player.getInventory().addItem(getRefillType().getItem());
            }
        }

        player.setFireTicks(0);
        player.getInventory().setHeldItemSlot(0);
        player.updateInventory();
        player.closeInventory();
    }

    @Override
    public int compareTo(@Nullable MatchLoadout matchLoadout) {
        if (matchLoadout != null) {
            return Integer.compare(this.getWeight(), matchLoadout.getWeight());
        }
        return -1;
    }
}
