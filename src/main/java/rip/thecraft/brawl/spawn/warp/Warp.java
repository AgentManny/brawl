package rip.thecraft.brawl.spawn.warp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

@Getter
@AllArgsConstructor
public final class Warp {

    private String name;
    @Setter private Location location;

    @Setter private String kit;

    @Getter @Setter private boolean enabled;

}
