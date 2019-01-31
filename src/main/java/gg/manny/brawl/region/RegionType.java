package gg.manny.brawl.region;

import gg.manny.brawl.Brawl;
import org.bukkit.Location;

import java.util.List;
import java.util.stream.Collectors;

public enum RegionType {

    SAFEZONE;

    public List<Region> getRegions() {
        return Brawl.getInstance().getRegionHandler().getRegions()
                .stream()
                .filter(region -> region.getType() == this)
                .collect(Collectors.toList());
    }

    public boolean containsLocation(Location location) {
        return this.getRegions().stream().anyMatch(region -> region.contains(location));
    }

}
