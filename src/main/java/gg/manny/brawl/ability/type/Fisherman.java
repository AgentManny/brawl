package gg.manny.brawl.ability.type;

import gg.manny.brawl.ability.Ability;
import gg.manny.pivot.util.PivotUtil;
import gg.manny.pivot.util.inventory.ItemBuilder;
import gg.manny.spigot.util.chatcolor.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.util.Vector;

public class Fisherman extends Ability implements Listener {

    public Fisherman() {
        super("Fisherman", new ItemBuilder(Material.FISHING_ROD)
                .name(CC.GRAY + "\u00bb " + CC.BLUE + CC.BOLD + "Fishing Hook" + CC.GRAY + " \u00ab")
                .create());
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        if(this.hasEquipped(player)) {
            if (event.getCaught() instanceof Player) {
                if (this.hasCooldown(player, true)) return;
                this.addCooldown(player);

                Player caught = (Player) event.getCaught();
                caught.teleport(player);
                caught.damage(0, player);
                PivotUtil.run(() ->  caught.setVelocity(new Vector()), false);
            }
        }
    }
}
