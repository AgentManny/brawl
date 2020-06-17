package rip.thecraft.brawl.killstreak.type;

import rip.thecraft.brawl.killstreak.Killstreak;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class GoldenApples extends Killstreak {

    @Override
    public int[] getKills() {
        return new int[] { 5 };
    }

    @Override
    public String getName() {
        return "Golden Apples";
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.GOLD;
    }

    @Override
    public Material getType() {
        return Material.GOLDEN_APPLE;
    }

    @Override
    public int getAmount() {
        return 5;

    }

    @Override
    public boolean isInteractable() {
        return false;
    }

}
