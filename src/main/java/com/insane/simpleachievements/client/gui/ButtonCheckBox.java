package com.insane.simpleachievements.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.insane.simpleachievements.AchievementHandler.SimpleAchievement;
import com.insane.simpleachievements.SimpleAchievements;

public class ButtonCheckBox extends GuiButton
{
	private final ResourceLocation texture = new ResourceLocation(SimpleAchievements.MODID.toLowerCase(), "texures/gui/checkBox.png");
	
	private final SimpleAchievement achievement;
	
	public ButtonCheckBox(int id, int x, int y, SimpleAchievement achievement, GuiScreen parent)
	{
		super(id, x, y, parent.width - x - 20, 20, achievement.text);
		
		this.achievement = achievement;
	}
	
	@Override
	public void drawButton(Minecraft par1Minecraft, int mouseX, int mouseY)
	{
		par1Minecraft.renderEngine.bindTexture(texture);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		drawTexturedModalRect(xPosition, yPosition, 0, 0, 20, 20);
		int stringOffset = 0;
		if (mouseX >= xPosition && mouseX <= xPosition + width && mouseY >= yPosition && mouseY <= yPosition + height)
		{
			stringOffset -= 1;
		}
		
		drawString(par1Minecraft.fontRenderer, this.displayString, xPosition + 30, yPosition + (height / 2) - 4 + stringOffset, achievement.getState() ? 0x00FF00 : 0xFF0000);
	}
}
