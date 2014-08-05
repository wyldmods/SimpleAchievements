package com.insane.simpleachievements.common.networking;

import java.io.DataInputStream;

import net.minecraft.entity.player.EntityPlayer;

public interface IByteEncodable<T>
{
	public byte[] encode();
	
	public T decode(DataInputStream data, EntityPlayer player);
}
