package gg.manny.brawl.ability.type;

import gg.manny.brawl.ability.Ability;
import gg.manny.pivot.util.inventory.ItemBuilder;
import gg.manny.spigot.util.chatcolor.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Phantom extends Ability {

    public Phantom() {
        super("Phantom", new ItemBuilder(Material.POTION)
                .data((byte) 8270)
                .name(CC.GRAY + "\u00bb " + CC.BLUE + CC.BOLD + "Phantom" + CC.GRAY + " \u00ab")
                .create());
    }

    @Override
    public void onActivate(Player player) {
        if (this.hasCooldown(player, true)) return;
        this.addCooldown(player);

    }
}
