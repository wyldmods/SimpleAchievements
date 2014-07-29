package com.insane.simpleachievements.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.AchievementMap;

import com.insane.simpleachievements.AchievementHandler;
import com.insane.simpleachievements.AchievementManager;
import com.insane.simpleachievements.SimpleAchievements;

/**
 * Created by Michael on 29/07/2014.
 */
public class GuiSA extends GuiScreen
{

	public static final int GUI_ID = 20;

	private AchievementHandler achievements;

	public GuiSA(EntityPlayer player)
	{
		super();

		this.mc = Minecraft.getMinecraft();
		achievements = AchievementManager.instance().getAchievementsFor(player.username);
	}

	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		drawRect(5, 5, this.width - 5, this.height - 5, 0xFA2E2E2E);

		for (int i = 0; i < achievements.numAchievements(); i++)
		{
			drawCenteredString(this.mc.fontRenderer, achievements.getAchievementText(i), this.width / 2, i * 8 + (this.height / 2 - ((achievements.numAchievements() * 8) / 2)), achievements.getAchievementState(i) ? 0x00FF00 : 0xFF0000);
		}
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}
}
