package rip.thecraft.brawl.duelarena.loadout.custom;

import rip.thecraft.brawl.duelarena.arena.ArenaType;
import rip.thecraft.brawl.duelarena.loadout.MatchLoadout;
import rip.thecraft.brawl.server.item.item.Armor;
import rip.thecraft.brawl.server.item.item.Items;
import rip.thecraft.brawl.kit.type.RefillType;
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
