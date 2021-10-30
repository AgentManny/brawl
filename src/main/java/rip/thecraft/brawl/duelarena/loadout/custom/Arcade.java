package rip.thecraft.brawl.duelarena.loadout.custom;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.duelarena.arena.ArenaType;
import rip.thecraft.brawl.duelarena.loadout.MatchLoadout;
import rip.thecraft.brawl.item.item.Armor;
import rip.thecraft.brawl.item.item.Items;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.kit.type.RefillType;

public class Arcade extends MatchLoadout {

    @Override
    public ChatColor getColor() {
        return ChatColor.LIGHT_PURPLE;
    }

    @Override
    public Material getIcon() {
        Kit kit = Brawl.getInstance().getKitHandler().getRandomAbilityKit();
        return kit.getIcon().getType();
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
