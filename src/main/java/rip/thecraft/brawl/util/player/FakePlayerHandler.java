package rip.thecraft.brawl.util.player;

import net.minecraft.server.v1_8_R3.*;
import rip.thecraft.server.CraftServer;
import rip.thecraft.server.handler.PacketHandler;

import java.util.ArrayList;
import java.util.List;

public class FakePlayerHandler implements PacketHandler {

    protected static List<FakePlayer> players = new ArrayList<>();

    public FakePlayerHandler() {
        CraftServer.getInstance().getPacketHandlers().clear();
        CraftServer.getInstance().addPacketHandler(this);
    }

    @Override
    public void handleReceivedPacket(PlayerConnection playerConnection, Packet packet) {
        String packetName = packet.getClass().getSimpleName();
        if (packetName.equals("PacketPlayInUseEntity")) {
            PacketPlayInUseEntity useEntity = (PacketPlayInUseEntity) packet;
            EntityPlayer player = playerConnection.player;
            for (FakePlayer fakePlayer : players) {
                if (fakePlayer.viewers.contains(player.getUniqueID())) {
                    if (useEntity.getEntityId() == fakePlayer.entityId) {
                        fakePlayer.interact(player, useEntity.a());
                        player.sendMessage(new ChatComponentText("Action: " + useEntity.a().name()));
                    }
                }
            }
        }
    }

    @Override
    public void handleSentPacket(PlayerConnection playerConnection, Packet packet) {

    }
}
