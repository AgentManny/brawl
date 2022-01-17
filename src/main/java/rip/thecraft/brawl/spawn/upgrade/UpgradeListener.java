package rip.thecraft.brawl.spawn.upgrade;

import gg.manny.streamline.npc.event.NPCInteractEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import rip.thecraft.brawl.spawn.challenges.menu.QuestMenu;
import rip.thecraft.brawl.spawn.upgrade.menu.UpgradeMenu;

public class UpgradeListener implements Listener {

    @EventHandler
    public void onCitizenInteract(NPCInteractEvent event) {
        Player player = event.getPlayer();
        if (event.isRightClicked()) {
            String id = event.getNpc().getName().toLowerCase();
            if (id.equals("upgrader")) {
                new UpgradeMenu().openMenu(player);
            } else if (id.equals("challenges") || id.equals("quests")) {
                new QuestMenu().open(player);
            }
        }
    }

}
