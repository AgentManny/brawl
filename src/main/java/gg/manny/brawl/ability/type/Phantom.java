package gg.manny.brawl.ability.type;

import gg.manny.brawl.ability.Ability;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Phantom extends Ability {

    @Override
    public Material getType() {
        return Material.SULPHUR;
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.GRAY;
    }

    @Override
    public void onActivate(Player player) {
        if (this.hasCooldown(player, true)) return;
        this.addCooldown(player);

    }
}
