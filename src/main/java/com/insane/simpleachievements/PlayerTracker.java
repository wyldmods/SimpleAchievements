package com.insane.simpleachievements;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.IPlayerTracker;

/**
 * Created by Michael on 28/07/2014.
 */
@Deprecated
public class PlayerTracker implements IPlayerTracker
{
	@Override
	public void onPlayerLogin(EntityPlayer player)
	{
		if (player != null && !player.worldObj.isRemote)
		{
			NBTTagCompound entityData = player.getEntityData();
			NBTTagCompound d = entityData.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
			if (!d.hasKey(SimpleAchievements.getPersistentTagName())) // Set up the Achievements Compound.
			{
				System.out.println("[SA]: New Player Added!");
				d.setCompoundTag(SimpleAchievements.getPersistentTagName(), new AchievementHandler().getAchievements());
				entityData.setCompoundTag(EntityPlayer.PERSISTED_NBT_TAG, d);
			}
			else
			{ 
				System.out.println("[SA]: Checking Achievements for Username " + player.getDisplayName());
				NBTTagCompound currentList = d.getCompoundTag(SimpleAchievements.getPersistentTagName());
				ArrayList<String> holder = new AchievementHandler().getListOfAchievements();
				for (int i = 0; i < holder.size(); i++)
				{
					if (!currentList.hasKey(holder.get(i)))
					{
						currentList.setBoolean(holder.get(i), false);
					}
				}
				d.setCompoundTag(SimpleAchievements.MODID + "achievements", currentList);
			}
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
