package org.wyldmods.simpleachievements.common.networking;

import org.wyldmods.simpleachievements.SimpleAchievements;
import org.wyldmods.simpleachievements.common.data.DataHandler;
import org.wyldmods.simpleachievements.common.data.DataManager;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;


public class PacketHandlerSA implements IMessageHandler<MessageSendAchievements, IMessage>
{
    public static final String CHANNEL = "SmplAchv";

    public static final SimpleNetworkWrapper INSTANCE = new SimpleNetworkWrapper(CHANNEL);
    	
	public static void init()
	{
	    INSTANCE.registerMessage(PacketHandlerSA.class, MessageSendAchievements.class, 0, Side.CLIENT);
	    INSTANCE.registerMessage(MessageAchievement.class, MessageAchievement.class, 1, Side.SERVER);
	}

    @Override
    public IMessage onMessage(final MessageSendAchievements message, MessageContext ctx)
    {
        Minecraft.getMinecraft().addScheduledTask(new Runnable() {
            @Override
            public void run() {
                DataManager.INSTANCE.changeMap(SimpleAchievements.proxy.getClientPlayer(), new DataHandler(message.list));                
            }
        });
        return null;
    }
}
