package rip.thecraft.brawl.upgrade;

import gg.manny.streamline.npc.event.NPCInteractEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import rip.thecraft.brawl.upgrade.menu.UpgradeMenu;

public class UpgradeListener implements Listener {

    @EventHandler
    public void onCitizenInteract(NPCInteractEvent event) {
        Player player = event.getPlayer();
        if (event.isRightClicked() && event.getNpc().getName().toLowerCase().contains("upgrader")) {
            new UpgradeMenu().openMenu(player);
        }
    }

}
