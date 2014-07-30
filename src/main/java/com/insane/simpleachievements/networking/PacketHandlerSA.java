package com.insane.simpleachievements.networking;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class PacketHandlerSA implements IPacketHandler
{
	private static BiMap<Byte, Class<? extends IByteEncodable<?>>> packetIdentifiers = HashBiMap.create(); 
	private static byte id = 0;
	
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
			Class<? extends IByteEncodable<?>> type = getType(packet);
			packet = stripIdent(packet);
			
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
		}
	}

	public static void sendToClient(Player player, IByteEncodable<?> obj)
	{
		if (obj == null) { return; }
		
		byte[] data = obj.encode();
		
		data = addIdent(data, obj);
		
		sendToClient(player, data);
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
//	
//	private static void sendToServer(byte[] data)
//	{
//		PacketDispatcher.sendPacketToServer(getPacketFor(data));
//	}

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
	

	private Class<? extends IByteEncodable<?>> getType(Packet250CustomPayload packet)
	{
		byte ident = packet.data[packet.data.length - 1];
		Class<? extends IByteEncodable<?>> type = packetIdentifiers.get(ident);
		
		if (type == null)
		{
			throw new IllegalArgumentException("This id is not registered: " + ident);
		}
		
		return type;
	}
	
	private Packet250CustomPayload stripIdent(Packet250CustomPayload packet)
	{
		packet.data = ArrayUtils.remove(packet.data, packet.data.length - 1);
		packet.length--;
		return packet;
	}
}
