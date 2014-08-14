package org.wyldmods.simpleachievements.common;

import org.wyldmods.simpleachievements.common.data.DataManager;
import org.wyldmods.simpleachievements.common.networking.PacketHandlerSA;

import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.network.Player;

public class PlayerTracker implements IPlayerTracker
{
	@Override
	public void onPlayerLogin(EntityPlayer player)
	{
		if (player != null && !player.worldObj.isRemote)
		{
			DataManager.instance().checkMap(player.username);
			PacketHandlerSA.sendToClient((Player) player, DataManager.instance().getHandlerFor(player.username));
		}
	}

	@Override
	public void onPlayerLogout(EntityPlayer player)
	{

	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player)
	{

	}

	@Override
	public void onPlayerRespawn(EntityPlayer player)
	{

	}
}
