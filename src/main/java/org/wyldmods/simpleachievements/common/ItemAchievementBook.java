package org.wyldmods.simpleachievements.common;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.wyldmods.simpleachievements.SimpleAchievements;
import org.wyldmods.simpleachievements.client.gui.GuiHelper;

public class ItemAchievementBook extends Item
{
	public ItemAchievementBook(int id)
	{
		super(id);
		setCreativeTab(CreativeTabs.tabMisc);
		setUnlocalizedName("sa.achievementBook");
		setTextureName(SimpleAchievements.MODID + ":book");
		setMaxStackSize(1);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World world, EntityPlayer player)
	{
		if (world.isRemote)
		{
			GuiHelper.openSAGUIBook(player);
		}
		return par1ItemStack;
	}
}
