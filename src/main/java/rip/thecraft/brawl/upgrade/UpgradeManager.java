package rip.thecraft.brawl.upgrade;

import rip.thecraft.brawl.Brawl;

public class UpgradeManager {

    public static final int PERK_ENTITY_ID = 69;

    private final Brawl plugin;

    public UpgradeManager(Brawl plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(new UpgradeListener(), plugin);
    }

}
