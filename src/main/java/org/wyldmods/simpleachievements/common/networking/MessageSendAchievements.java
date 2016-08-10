package org.wyldmods.simpleachievements.common.networking;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.wyldmods.simpleachievements.common.data.Element;

import io.netty.buffer.ByteBuf;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

@NoArgsConstructor
public class MessageSendAchievements implements IMessage
{
    List<Element> list;

    public MessageSendAchievements(List<Element> list)
    {
        this.list = list;
    }

    @SuppressWarnings("unchecked")
    @Override
    @SneakyThrows
    public void fromBytes(ByteBuf buf)
    {
        short len = buf.readShort();
        byte[] compressedBody = new byte[len];

        for (short i = 0; i < len; i++) compressedBody[i] = buf.readByte();

        ObjectInputStream obj = new ObjectInputStream(new GZIPInputStream(new ByteArrayInputStream(compressedBody)));
        list = (List<Element>) obj.readObject();
        obj.close();
    }

    @Override
    @SneakyThrows
    public void toBytes(ByteBuf buf)
    {
        ByteArrayOutputStream obj = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(obj);
        ObjectOutputStream objStream = new ObjectOutputStream(gzip);
        objStream.writeObject(list);
        objStream.close();

        buf.writeShort(obj.size());
        buf.writeBytes(obj.toByteArray());
    }
}
