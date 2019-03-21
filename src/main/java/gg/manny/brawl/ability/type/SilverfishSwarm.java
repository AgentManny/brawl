package gg.manny.brawl.ability.type;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.ability.Ability;
import gg.manny.pivot.util.PivotUtil;
import gg.manny.pivot.util.inventory.ItemBuilder;
import gg.manny.spigot.util.chatcolor.CC;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.List;

public class SilverfishSwarm extends Ability implements Listener {

    private final Brawl brawl;

    public SilverfishSwarm(Brawl brawl) {
        super("SilverfishSwarm", new ItemBuilder(Material.INK_SACK)
                .data((byte) 6)
                .name(CC.GRAY + "\u00bb " + CC.DARK_AQUA + CC.BOLD + "Silverfish Swarm" + CC.GRAY + " \u00ab")
                .create()
        );

        this.brawl = brawl;
    }

    @Override
    public void onActivate(Player player) {
        if (this.hasCooldown(player, true)) return;
        this.addCooldown(player);

        for (int i = 0; i < 5; i++) {
            Silverfish entity = (Silverfish) player.getWorld().spawnEntity(player.getLocation(), EntityType.SILVERFISH);
            entity.setMetadata("swarm", new FixedMetadataValue(brawl, player.getUniqueId().toString()));
            entity.setMaxHealth(20);
            entity.setHealth(20);
            PivotUtil.runLater(() -> {
                if (entity != null) {
                    entity.remove();
                }
            }, 300L, false);
        }

    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        if (event.getEntity() instanceof Silverfish && event.getEntity().hasMetadata("swarm") && event.getTarget() instanceof Player) {
            Player player = (Player) event.getTarget();
            List<MetadataValue> metaData = event.getEntity().getMetadata("swarm");
            if (player.getUniqueId().toString().equals(metaData.get(0).asString())) {
                event.setCancelled(true);
                event.setTarget(null);
            }
        }
    }
}
