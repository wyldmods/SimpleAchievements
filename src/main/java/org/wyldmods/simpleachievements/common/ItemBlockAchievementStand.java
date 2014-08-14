package org.wyldmods.simpleachievements.common;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockAchievementStand extends ItemBlock
{
	public ItemBlockAchievementStand(int par1)
	{
		super(par1);
		setHasSubtypes(true);
		setCreativeTab(CreativeTabs.tabMisc);
	}

	@Override
	public int getMetadata(int par1)
	{
		return par1;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		par3List.add(new ItemStack(this, 1, 0));
		par3List.add(new ItemStack(this, 1, 1));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
	{
		par3List.add(par1ItemStack.getItemDamage() == 1 ? "Has no book." : "Has a book.");
	}
}
