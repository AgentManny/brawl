package rip.thecraft.brawl.region;

import org.bukkit.Location;
import rip.thecraft.brawl.Brawl;

import java.util.List;
import java.util.stream.Collectors;

public enum RegionType {

    /** Region where you can't activate abilities or damage players */
    SAFEZONE,

    /** Region where you can't activate abilities */
    NO_ABILITY_ZONE,

    /** Region where you can't activate perks */
    NO_PERK_ZONE,

    /** Region where a multiplier is applied */
    MULTIPLIER;

    public List<Region> getRegions() {
        return Brawl.getInstance().getRegionHandler().getRegions()
                .stream()
                .filter(region -> region.getType() == this)
                .collect(Collectors.toList());
    }

    public boolean appliesTo(Location location) {
        return this.getRegions().stream().anyMatch(region -> region.contains(location));
    }

}
