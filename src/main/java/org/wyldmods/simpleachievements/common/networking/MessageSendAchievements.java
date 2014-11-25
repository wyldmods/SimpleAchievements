package org.wyldmods.simpleachievements.common.networking;

import io.netty.buffer.ByteBuf;

import java.util.List;

import lombok.NoArgsConstructor;

import org.wyldmods.simpleachievements.common.data.Element;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

@NoArgsConstructor
public class MessageSendAchievements implements IMessage
{
    private static final Gson gson = new Gson();
    
    List<Element> list;
    
    public MessageSendAchievements(List<Element> list)
    {
        this.list = list;
    }
    
    @SuppressWarnings("serial")
    @Override
    public void fromBytes(ByteBuf buf)
    {
        String objStr = ByteBufUtils.readUTF8String(buf);
        
        this.list = gson.fromJson(objStr, new TypeToken<List<Element>>(){}.getType());
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, gson.toJson(list));
    }
}
