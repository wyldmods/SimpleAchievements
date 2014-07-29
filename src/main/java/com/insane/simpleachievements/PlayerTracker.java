package com.insane.simpleachievements;

import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.IPlayerTracker;

/**
 * Created by Michael on 28/07/2014.
 */
public class PlayerTracker implements IPlayerTracker
{
	@Override
	public void onPlayerLogin(EntityPlayer player)
	{
	    if (player!=null && !player.worldObj.isRemote) {
            AchievementManager.instance().checkMap(player.username);
            AchievementManager.instance().sendStructureToPlayer(player);
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
