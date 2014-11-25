package org.wyldmods.simpleachievements.common.networking;

import io.netty.buffer.ByteBuf;
import lombok.NoArgsConstructor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import org.wyldmods.simpleachievements.common.NBTUtils;
import org.wyldmods.simpleachievements.common.TileEntityAchievementStand;
import org.wyldmods.simpleachievements.common.data.DataManager;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

@NoArgsConstructor
public class MessageAchievement implements IMessage, IMessageHandler<MessageAchievement, IMessage>
{
    public enum MessageType
    {
        TOGGLE,
        PAGE,
        TILE        
    }
    
    private static final MessageType[] VALUES = MessageType.values();
        
    private int data;
    private boolean state;
    private int x, y, z;
    private MessageType type;
    
    public MessageAchievement(int id)
    {
        this(id, false, 0, 0, 0, MessageType.PAGE);
    }
    
    public MessageAchievement(int id, boolean state)
    {
        this(id, state, 0, 0, 0, MessageType.TOGGLE);
    }
    
    public MessageAchievement(int page, int x, int y, int z)
    {
        this(page, false, x, y, z, MessageType.TILE);
    }
    
    private MessageAchievement(int id, boolean state, int x, int y, int z, MessageType type)
    {
        this.data = id;
        this.state = state;
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = type;
    }
    
    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.data = buf.readInt();
        this.state = buf.readBoolean();
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.type = VALUES[buf.readInt()];
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(data);
        buf.writeBoolean(state);
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(type.ordinal());
    }

    @Override
    public IMessage onMessage(MessageAchievement message, MessageContext ctx)
    {
        EntityPlayer player = ctx.getServerHandler().playerEntity;
        if (player == null)
        {
            return null;
        }
        
        switch(message.type)
        {
        case PAGE:
            NBTUtils.getTag(player.getCurrentEquippedItem()).setInteger("sa:page", message.data);
            break;
        case TILE:
            TileEntity te = player.worldObj.getTileEntity(message.x, message.y, message.z);
            if (te instanceof TileEntityAchievementStand)
            {
                ((TileEntityAchievementStand)te).setPage(message.data);
                player.worldObj.markBlockForUpdate(message.x, message.y, message.z);
            }
            break;
        case TOGGLE:
            DataManager.instance().getHandlerFor(player.getCommandSenderName()).getAchievement(message.data).state = message.state;
            break;
        }
        return null;
    }

}
