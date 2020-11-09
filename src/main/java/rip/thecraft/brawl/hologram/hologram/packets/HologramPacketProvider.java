package rip.thecraft.brawl.hologram.hologram.packets;

import rip.thecraft.brawl.hologram.hologram.HologramLine;
import org.bukkit.Location;

public interface HologramPacketProvider {

    HologramPacket getPacketsFor(Location location, HologramLine line);

}
