package rip.thecraft.brawl.upgrade;

import lombok.Getter;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.spartan.Spartan;

import java.util.UUID;

public class UpgradeManager {

    public static final String NPC_UPGRADER_LOC = "NPC_UPGRADER";
    public static final int PERK_ENTITY_ID = 69;

    private final Brawl plugin;

    @Getter private NPC npc;

    public UpgradeManager(Brawl plugin) {
        this.plugin = plugin;

        NPCRegistry registry = CitizensAPI.getNPCRegistry();
        this.npc = registry.getById(PERK_ENTITY_ID) == null ? registry.createNPC(EntityType.PLAYER, new UUID(Spartan.RANDOM.nextLong(), 0), PERK_ENTITY_ID, ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "PERKS") : registry.getById(PERK_ENTITY_ID);
        Location loc = plugin.getLocationByName(NPC_UPGRADER_LOC);
        if (loc != null) {
            if (npc.isSpawned()) {
                npc.despawn();
            }
            npc.spawn(loc);
        }

        plugin.getServer().getPluginManager().registerEvents(new UpgradeListener(), plugin);
    }

}