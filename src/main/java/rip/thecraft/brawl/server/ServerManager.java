package rip.thecraft.brawl.server;

import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.server.region.Region;

public class ServerManager {

    /** Returns the region that has a multiplier */
    private Region multiplierRegion;

    /** Returns the value of the multiplier in that region */
    private double multiplier = 1.0;

    private Kit freeKit;

    public ServerManager() {

    }

    public boolean isMultiplierEnabled() {
        return multiplierRegion != null && multiplier <= 1.0;
    }



}
