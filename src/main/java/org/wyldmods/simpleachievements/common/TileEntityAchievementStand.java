package org.wyldmods.simpleachievements.common;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityEnchantmentTable;

/**
 * Created by Michael on 13/08/2014.
 */
public class TileEntityAchievementStand extends TileEntityEnchantmentTable {

    public int page;

    @Override
    public void writeToNBT(NBTTagCompound par1) {
        super.writeToNBT(par1);
        par1.setInteger("sa:page",page);
    }

    @Override
    public void readFromNBT(NBTTagCompound par1) {
        super.readFromNBT(par1);
        par1.getInteger("sa:page");
    }

    public void setPage(int par1) {
        page=par1;
    }
}
