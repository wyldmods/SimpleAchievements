package org.wyldmods.simpleachievements.common;

import org.wyldmods.simpleachievements.client.gui.GuiHelper;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemAchievementBook extends Item
{
	public ItemAchievementBook()
	{
		super();
		setCreativeTab(CreativeTabs.MISC);
		setUnlocalizedName("sa.achievementBook");
		setMaxStackSize(1);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand)
	{
		if (world.isRemote)
		{
			GuiHelper.openSAGUIBook(player, hand);
		}
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}
}
