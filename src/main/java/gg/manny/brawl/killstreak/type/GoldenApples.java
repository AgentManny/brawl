package gg.manny.brawl.killstreak.type;

import gg.manny.brawl.killstreak.Killstreak;
import gg.manny.brawl.player.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

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
