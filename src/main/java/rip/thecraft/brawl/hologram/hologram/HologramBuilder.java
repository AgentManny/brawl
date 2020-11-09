package rip.thecraft.brawl.hologram.hologram;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

import java.util.*;

@Getter
@RequiredArgsConstructor
public class HologramBuilder {

    private final Collection<UUID> viewers;

    private Location location;
    protected List<String> lines = new ArrayList<>();

    public HologramBuilder addLines(Iterable<String> lines) {
        for (String line : lines) {
            this.lines.add(line);
        }
        return this;
    }

    public HologramBuilder addLines(String... lines) {
        this.lines.addAll(Arrays.asList(lines));
        return this;
    }

    public HologramBuilder at(Location location) {
        this.location = location;
        return this;
    }

    public UpdatingHologramBuilder updates() {
        return new UpdatingHologramBuilder(this);
    }

    public Hologram build() {
        return new BaseHologram(this);
    }

}
