package rip.thecraft.brawl.upgrade;

import net.citizensnpcs.api.event.NPCClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class UpgradeListener implements Listener {

    @EventHandler
    public void onCitizenInteract(NPCClickEvent event) {
        NPC npc = event.getNPC();
        if (npc.getId() == UpgradeManager.PERK_ENTITY_ID) {

        }
    }

}
