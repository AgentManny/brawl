package rip.thecraft.brawl.upgrade;

import lombok.Getter;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.spartan.Spartan;

import java.util.UUID;

public class UpgradeManager {

    public static final int PERK_ENTITY_ID = 69;

    private final Brawl plugin;

    @Getter private NPC npc;

    public UpgradeManager(Brawl plugin) {
        this.plugin = plugin;

        NPCRegistry registry = CitizensAPI.getNPCRegistry();
        this.npc = registry.getById(PERK_ENTITY_ID) == null ? registry.createNPC(EntityType.PLAYER, new UUID(Spartan.RANDOM.nextLong(), 0), PERK_ENTITY_ID, ChatColor.GOLD.toString() + ChatColor.BOLD + "UPGRADES") : registry.getById(PERK_ENTITY_ID);

        plugin.getServer().getPluginManager().registerEvents(new UpgradeListener(), plugin);
    }

}
