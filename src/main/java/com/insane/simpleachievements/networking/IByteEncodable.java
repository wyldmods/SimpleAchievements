package com.insane.simpleachievements.networking;

import java.io.DataInputStream;

import net.minecraft.entity.player.EntityPlayer;

public interface IByteEncodable
{
	public byte[] encode();
	
	public void decode(DataInputStream data, int length, EntityPlayer player);
}
