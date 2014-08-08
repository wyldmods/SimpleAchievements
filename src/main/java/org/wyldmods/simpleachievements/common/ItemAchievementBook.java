package org.wyldmods.simpleachievements.common;

import org.wyldmods.simpleachievements.SimpleAchievements;
import org.wyldmods.simpleachievements.client.gui.GuiSA;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

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
        player.openGui(SimpleAchievements.instance, GuiSA.GUI_ID, world, (int) Math.floor(player.posX), (int) Math.floor(player.posY), (int) Math.floor(player.posZ));
        return par1ItemStack;
	}
}
