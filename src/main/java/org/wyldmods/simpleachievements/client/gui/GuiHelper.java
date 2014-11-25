package org.wyldmods.simpleachievements.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import org.wyldmods.simpleachievements.common.TileEntityAchievementStand;

public class GuiHelper
{
	public static void openSAGUI(World world, EntityPlayer player, int x, int y, int z)
	{
		Minecraft.getMinecraft().displayGuiScreen(new GuiSA(player, (TileEntityAchievementStand) world.getTileEntity(x, y, z)));
	}

	public static void openSAGUIBook(EntityPlayer player)
	{
		Minecraft.getMinecraft().displayGuiScreen(new GuiSA(player));
	}
}
