package org.wyldmods.simpleachievements.common;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityEnchantmentTable;

public class TileEntityAchievementStand extends TileEntityEnchantmentTable
{

	public int page;

	@Override
	public void writeToNBT(NBTTagCompound par1)
	{
		par1.setInteger("sa:page", page);
		super.writeToNBT(par1);
	}

	@Override
	public void readFromNBT(NBTTagCompound par1)
	{
		page = par1.getInteger("sa:page");
		super.readFromNBT(par1);
	}

	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
	{
		readFromNBT(pkt.func_148857_g());
	}

	public void setPage(int par1)
	{
		page = par1;
	}
}
