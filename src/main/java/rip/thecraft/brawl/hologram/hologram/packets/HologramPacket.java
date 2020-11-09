package rip.thecraft.brawl.hologram.hologram.packets;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Getter
@AllArgsConstructor
public class HologramPacket {

    private List<PacketContainer> packets;
    private List<Integer> entityIds;

    public void sendToPlayer(Player player) {
        for (PacketContainer packetContainer : this.packets) {
            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, packetContainer);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

}
