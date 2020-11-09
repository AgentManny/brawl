package rip.thecraft.brawl.hologram.hologram.packets.v1_8;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import rip.thecraft.brawl.hologram.hologram.HologramLine;
import rip.thecraft.brawl.hologram.hologram.packets.HologramPacket;
import rip.thecraft.brawl.hologram.hologram.packets.HologramPacketProvider;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Minecraft18HologramPacketProvider implements HologramPacketProvider {

    @Override
    public HologramPacket getPacketsFor(Location location, HologramLine line) {
        List<PacketContainer> packets = Collections.singletonList(this.createArmorStandPacket(line.getSkullId(), line.getText(), location));
        return new HologramPacket(packets, Arrays.asList(line.getSkullId(), -1337));
    }

    protected PacketContainer createArmorStandPacket(int witherSkullId, String text, Location location) {
        PacketContainer displayPacket = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
        StructureModifier<Integer> ints = displayPacket.getIntegers();
        ints.write(0, witherSkullId);
        ints.write(1, 30);
        ints.write(2, (int) (location.getX() * 32.0));
        ints.write(3, (int) ((location.getY() - 2.0) * 32.0));
        ints.write(4, (int) (location.getZ() * 32.0));
        WrappedDataWatcher watcher = new WrappedDataWatcher();
        watcher.setObject(0, (byte )32);
        watcher.setObject(2, ChatColor.translateAlternateColorCodes('&', text));
        watcher.setObject(3, (byte )1);
        displayPacket.getDataWatcherModifier().write(0, watcher);
        return displayPacket;
    }

}
