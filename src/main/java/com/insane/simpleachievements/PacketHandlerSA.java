package com.insane.simpleachievements;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

/**
 * Created by Michael on 29/07/2014.
 */
public class PacketHandlerSA implements IPacketHandler {
    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
        if (packet.channel.equals(SimpleAchievements.CHANNEL)) {
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(packet.data));
            AchievementHandler replace = disToAchievement(dis, (EntityPlayer)player);
            AchievementManager.instance().changeMap((EntityPlayer) player, replace);
        }
    }

    private AchievementHandler disToAchievement(DataInputStream dis, EntityPlayer player) {
        ArrayList<AchievementHandler.SimpleAchievement> newAchievements = new ArrayList<AchievementHandler.SimpleAchievement>();
        try {
            newAchievements.add(new AchievementHandler.SimpleAchievement(dis.readUTF(),dis.readBoolean()));
        } catch (IOException error) {
            System.out.print("Issue with packet");
            error.printStackTrace();
        }
        return new AchievementHandler(player.username, newAchievements);
    }
}
