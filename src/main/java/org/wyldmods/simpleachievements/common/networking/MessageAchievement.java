package org.wyldmods.simpleachievements.common.networking;

import org.wyldmods.simpleachievements.common.NBTUtils;
import org.wyldmods.simpleachievements.common.TileEntityAchievementStand;
import org.wyldmods.simpleachievements.common.data.DataManager;

import io.netty.buffer.ByteBuf;
import lombok.NoArgsConstructor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

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
    private BlockPos pos;
    private MessageType type;
    
    public MessageAchievement(int id)
    {
        this(id, false, new BlockPos(0, 0, 0), MessageType.PAGE);
    }
    
    public MessageAchievement(int id, boolean state)
    {
        this(id, state, new BlockPos(0, 0, 0), MessageType.TOGGLE);
    }
    
    public MessageAchievement(int page, BlockPos pos)
    {
        this(page, false, pos, MessageType.TILE);
    }
    
    private MessageAchievement(int id, boolean state, BlockPos pos, MessageType type)
    {
        this.data = id;
        this.state = state;
        this.pos = pos;
        this.type = type;
    }
    
    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.data = buf.readInt();
        this.state = buf.readBoolean();
        this.pos = BlockPos.fromLong(buf.readLong());
        this.type = VALUES[buf.readInt()];
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(data);
        buf.writeBoolean(state);
        buf.writeLong(pos.toLong());
        buf.writeInt(type.ordinal());
    }

    @Override
    public IMessage onMessage(final MessageAchievement message, final MessageContext ctx)
    {
        FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(new Runnable() {
            
            @Override
            public void run() {
                EntityPlayer player = ctx.getServerHandler().playerEntity;

                switch(message.type)
                {
                case PAGE:
                    NBTUtils.getTag(player.getHeldItemMainhand()).setInteger("sa:page", message.data);
                    break;
                case TILE:
                    TileEntity te = player.worldObj.getTileEntity(message.pos);
                    if (te instanceof TileEntityAchievementStand)
                    {
                        ((TileEntityAchievementStand)te).setPage(message.data);
                        IBlockState state = player.worldObj.getBlockState(message.pos);
                        player.worldObj.notifyBlockUpdate(message.pos, state, state, 8);
                    }
                    break;
                case TOGGLE:
                    DataManager.INSTANCE.getHandlerFor(player.getName()).getAchievement(message.data).state = message.state;
                    break;
                }
            }
        });
        return null;
    }

}
