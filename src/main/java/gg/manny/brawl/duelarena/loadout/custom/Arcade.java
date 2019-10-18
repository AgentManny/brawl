package gg.manny.brawl.duelarena.loadout.custom;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.duelarena.arena.ArenaType;
import gg.manny.brawl.duelarena.loadout.MatchLoadout;
import gg.manny.brawl.item.item.Armor;
import gg.manny.brawl.item.item.Items;
import gg.manny.brawl.kit.type.RefillType;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class Arcade extends MatchLoadout {

    @Override
    public ChatColor getColor() {
        return ChatColor.LIGHT_PURPLE;
    }

    @Override
    public Material getIcon() {
        return Brawl.getInstance().getKitHandler().getKits().get(Brawl.RANDOM.nextInt(Brawl.getInstance().getKitHandler().getKits().size())).getIcon().getType();
    }

    @Override
    public Armor getArmor() {
        return null;
    }

    @Override
    public Items getItems() {
        return null;
    }

    @Override
    public boolean isRanked() {
        return true;
    }

    @Override
    public ArenaType getArena() {
        return ArenaType.ARCADE;
    }

    @Override
    public RefillType getRefillType() {
        return RefillType.SOUP;
    }

    @Override
    public int getHealingAmount() {
        return 8;
    }

    @Override
    public int getWeight() {
        return 6;
    }
}
