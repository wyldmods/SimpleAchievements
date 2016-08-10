package org.wyldmods.simpleachievements.common;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityEnchantmentTable;

public class TileEntityAchievementStand extends TileEntityEnchantmentTable
{

	public int page;

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound par1)
	{
		par1.setInteger("sa:page", page);
		return super.writeToNBT(par1);
	}

	@Override
	public void readFromNBT(NBTTagCompound par1)
	{
		page = par1.getInteger("sa:page");
		super.readFromNBT(par1);
	}

	@Override
	public NBTTagCompound getUpdateTag()
	{
	    return writeToNBT(new NBTTagCompound());
	}

    @Override
	public SPacketUpdateTileEntity getUpdatePacket() 
	{
	    return new SPacketUpdateTileEntity(getPos(), getBlockMetadata(), getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
	{
		readFromNBT(pkt.getNbtCompound());	
	}

	public void setPage(int par1)
	{
		page = par1;
	}
}
