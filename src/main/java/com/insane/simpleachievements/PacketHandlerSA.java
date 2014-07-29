package com.insane.simpleachievements;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by Michael on 29/07/2014.
 */
public class PacketHandlerSA implements IPacketHandler {
    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
        if (packet.channel.equals(SimpleAchievements.CHANNEL)) {
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(packet.data));

            try {
                System.out.println(dis.readUTF());
                System.out.println(dis.readBoolean());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
