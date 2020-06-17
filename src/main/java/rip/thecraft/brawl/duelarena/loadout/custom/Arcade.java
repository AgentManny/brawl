package rip.thecraft.brawl.duelarena.loadout.custom;

import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.duelarena.arena.ArenaType;
import rip.thecraft.brawl.duelarena.loadout.MatchLoadout;
import rip.thecraft.brawl.item.item.Armor;
import rip.thecraft.brawl.item.item.Items;
import rip.thecraft.brawl.kit.type.RefillType;
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
