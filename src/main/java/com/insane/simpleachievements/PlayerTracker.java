package com.insane.simpleachievements;

import cpw.mods.fml.common.IPlayerTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Michael on 28/07/2014.
 */
public class PlayerTracker implements IPlayerTracker {
    @Override
    public void onPlayerLogin(EntityPlayer player) {
        if (player!=null && !player.worldObj.isRemote) {
            NBTTagCompound entityData = player.getEntityData();
            NBTTagCompound d = entityData.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
            if (!d.hasKey(SimpleAchievements.MODID + "achievements")) { //Set up the Achievements Compound.
                System.out.println("[SA]: New Player Added!");
                d.setCompoundTag(SimpleAchievements.MODID + "achievements",new AchievementHandler().getAchievements());
                entityData.setCompoundTag(EntityPlayer.PERSISTED_NBT_TAG,d);
            }
            else { //Player has achievements, but we should check if there are any new ones.
                System.out.println("[SA]: Checking Achievements for Username " + player.getDisplayName());
                NBTTagCompound currentList = d.getCompoundTag(SimpleAchievements.MODID + "achievements");
                ArrayList<String> holder = new AchievementHandler().getListOfAchievements();
                for (int i=0 ; i<holder.size(); i++) {
                    if (!currentList.hasKey(holder.get(i))) {
                        currentList.setBoolean(holder.get(i), false);
                    }
                }
                d.setCompoundTag(SimpleAchievements.MODID+"achievements",currentList);
            }
        }
    }

    @Override
    public void onPlayerLogout(EntityPlayer player) {

    }

    @Override
    public void onPlayerChangedDimension(EntityPlayer player) {

    }

    @Override
    public void onPlayerRespawn(EntityPlayer player) {

    }
}
