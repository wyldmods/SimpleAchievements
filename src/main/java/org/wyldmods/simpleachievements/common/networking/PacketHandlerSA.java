package org.wyldmods.simpleachievements.common.networking;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;

import org.apache.commons.lang3.ArrayUtils;
import org.wyldmods.simpleachievements.common.NBTUtils;
import org.wyldmods.simpleachievements.common.data.DataManager;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class PacketHandlerSA implements IPacketHandler
{
	private static BiMap<Byte, Class<? extends IByteEncodable<?>>> packetIdentifiers = HashBiMap.create(); 
	private static byte id = 0;
	
	public static final byte ID_BYTE_ENCODABLE = 0;
	public static final byte ID_ACHIEVEMENT_UPDATE = 1;
	public static final byte ID_PAGE_UDPATE = 2;
	
	public static void registerSerializeable(Class<? extends IByteEncodable<?>> clazz)
	{
		packetIdentifiers.put(id++, clazz);
	}
	
	public static final String CHANNEL = "SmplAchv";

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player)
	{
		if (packet.channel.equals(CHANNEL))
		{			
			byte lastByte = packet.data[packet.data.length - 1];
			switch(packet.data[0])
			{
			case ID_BYTE_ENCODABLE:
				Class<? extends IByteEncodable<?>> type = getType(lastByte);
				packet = strip(packet);
				
				IByteEncodable<?> inst;
				
				try
				{
					inst = type.newInstance();
				}
				catch (Exception e)
				{
					throw new InstantiationError("IByteEncodable instances must have default constructors.");
				}

				DataInputStream dis = new DataInputStream(new ByteArrayInputStream(packet.data));

				inst.decode(dis, (EntityPlayer) player);
				break;
			case ID_ACHIEVEMENT_UPDATE:
				packet.data = ArrayUtils.remove(packet.data, 0);
				
				ByteArrayInputStream bin = new ByteArrayInputStream(packet.data);
				DataInputStream in = new DataInputStream(bin);
				
				try
				{
					DataManager.instance().getHandlerFor(in.readUTF()).getAchievement(in.readInt()).setState(in.readBoolean());
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				break;
			case ID_PAGE_UDPATE:
				packet.data = ArrayUtils.remove(packet.data, 0);
				
				bin = new ByteArrayInputStream(packet.data);
				in = new DataInputStream(bin);
				
				try
				{
					NBTUtils.getTag(FMLCommonHandler.instance().getMinecraftServerInstance().worldServers[in.readInt()].getPlayerEntityByName(in.readUTF()).getCurrentEquippedItem()).setInteger("sa:page", in.readInt());
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				break;
			}
		}
	}

	public static void sendToClient(Player player, IByteEncodable<?> obj)
	{
		if (obj == null) { return; }
		
		byte[] data = obj.encode();
		
		data = ArrayUtils.add(data, 0, ID_BYTE_ENCODABLE);
		
		data = addIdent(data, obj);
		
		sendToClient(player, data);
	}
	
	public static void sendAchUpdateToServer(EntityPlayer player, int id, boolean state)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(bos);
		
		try
		{
			out.writeByte(ID_ACHIEVEMENT_UPDATE);
			out.writeUTF(player.username);
			out.writeInt(id);
			out.writeBoolean(state);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		sendToServer(bos.toByteArray());
	}
	
	public static void sendPageUpdateToServer(EntityPlayer player, int page)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(bos);
		
		try
		{
			out.writeByte(ID_PAGE_UDPATE);
			out.writeInt(player.worldObj.provider.dimensionId);
			out.writeUTF(player.username);
			out.writeInt(page);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		sendToServer(bos.toByteArray());
	}

	private static void sendToClient(Player player, byte[] data)
	{
		PacketDispatcher.sendPacketToPlayer(getPacketFor(data), player);
	}

// Not sure if we need this, not sure if it will work with our system, cross that bridge when we come to it
	
//	public static void sendToServer(IByteEncodable obj)
//	{
//		byte[] data = obj.encode();
//		sendToServer(data);
//	}
	
	private static void sendToServer(byte[] data)
	{
		PacketDispatcher.sendPacketToServer(getPacketFor(data));
	}

	private static Packet250CustomPayload getPacketFor(byte[] data)
	{
		Packet250CustomPayload packet = new Packet250CustomPayload();

		packet.data = data;
		packet.length = data.length;

		packet.channel = CHANNEL;

		return packet;
	}
	
	private static byte[] addIdent(byte[] data, IByteEncodable<?> obj)
	{
		Byte ident = packetIdentifiers.inverse().get(obj.getClass());
		
		if (ident == null)
		{
			throw new IllegalArgumentException("This IByteEncodable is not registered: " + obj.getClass().getName());
		}
		
		data = ArrayUtils.add(data, ident.byteValue());
		
		return data;
	}
	

	private Class<? extends IByteEncodable<?>> getType(byte lastByte)
	{
		byte ident = lastByte;
		Class<? extends IByteEncodable<?>> type = packetIdentifiers.get(ident);
		
		if (type == null)
		{
			throw new IllegalArgumentException("This id is not registered: " + ident);
		}
		
		return type;
	}
	
	private Packet250CustomPayload strip(Packet250CustomPayload packet)
	{
		packet.data = ArrayUtils.remove(packet.data, packet.data.length - 1);
		packet.data = ArrayUtils.remove(packet.data, 0);
		packet.length -= 2;
		return packet;
	}
}
