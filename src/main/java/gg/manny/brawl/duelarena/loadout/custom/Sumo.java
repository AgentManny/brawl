package gg.manny.brawl.duelarena.loadout.custom;

import gg.manny.brawl.duelarena.arena.ArenaType;
import gg.manny.brawl.duelarena.loadout.MatchLoadout;
import gg.manny.brawl.item.item.Armor;
import gg.manny.brawl.item.item.Items;
import gg.manny.brawl.kit.type.RefillType;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class Sumo extends MatchLoadout {

    @Override
    public ChatColor getColor() {
        return ChatColor.GOLD;
    }

    @Override
    public Material getIcon() {
        return Material.LEASH;
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
    public RefillType getRefillType() {
        return RefillType.NONE;
    }

    @Override
    public ArenaType getArena() {
        return ArenaType.SUMO;
    }

    @Override
    public int getHealingAmount() {
        return 8;
    }

    @Override
    public int getWeight() {
        return 5;
    }
}
