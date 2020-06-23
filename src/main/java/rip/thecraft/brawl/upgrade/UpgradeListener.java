package rip.thecraft.brawl.upgrade;

import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import rip.thecraft.brawl.upgrade.menu.UpgradeMenu;

public class UpgradeListener implements Listener {

    @EventHandler
    public void onCitizenInteract(NPCRightClickEvent event) {
        Player player = event.getClicker();
        NPC npc = event.getNPC();

        if (npc.getId() == UpgradeManager.PERK_ENTITY_ID) {
            new UpgradeMenu().openMenu(player);
        }
    }

}
