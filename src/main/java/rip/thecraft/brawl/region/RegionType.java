package rip.thecraft.brawl.region;

import org.bukkit.Location;
import rip.thecraft.brawl.Brawl;

import java.util.List;
import java.util.stream.Collectors;

public enum RegionType {

    SAFEZONE,
    NO_ABILITY_ZONE,
    NO_PERK_ZONE;

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
