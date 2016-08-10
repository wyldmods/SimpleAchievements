package org.wyldmods.simpleachievements.client.gui;

import javax.annotation.Nonnull;

import org.wyldmods.simpleachievements.common.TileEntityAchievementStand;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GuiHelper
{
	public static void openSAGUI(World world, EntityPlayer player, @Nonnull BlockPos pos)
	{
		Minecraft.getMinecraft().displayGuiScreen(new GuiSA(player, (TileEntityAchievementStand) world.getTileEntity(pos)));
	}

	public static void openSAGUIBook(EntityPlayer player, EnumHand hand)
	{
		Minecraft.getMinecraft().displayGuiScreen(new GuiSA(player, hand));
	}
}
