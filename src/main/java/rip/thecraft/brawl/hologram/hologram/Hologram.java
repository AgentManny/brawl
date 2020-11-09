package rip.thecraft.brawl.hologram.hologram;

import org.bukkit.Location;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface Hologram {

    UUID id();

    void send();

    void destroy();

    void addLines(String... lines);

    void setLine(int id, String line);

    void setLines(Collection<String> lines);

    List<String> getLines();

    Location getLocation();

}