package rip.thecraft.brawl.hologram.hologram;

import lombok.Getter;
import lombok.Setter;
import rip.thecraft.brawl.util.EntityUtils;

import java.util.Objects;

@Getter
@Setter
public class HologramLine {

    private final int skullId;
    private final int horseId;
    private String text;

    public HologramLine(String text) {
        this.skullId = EntityUtils.getFakeEntityId();
        this.horseId = EntityUtils.getFakeEntityId();
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HologramLine that = (HologramLine) o;
        return (skullId == that.skullId || horseId == that.horseId) && text.equals(that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(skullId, horseId, text);
    }
}
