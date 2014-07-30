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
	private static final ResourceLocation texture = new ResourceLocation(SimpleAchievements.MODID.toLowerCase(), "textures/gui/checkboxes.png");
	
	private final SimpleAchievement achievement;
	
	public ButtonCheckBox(int id, int x, int y, int width, int height, SimpleAchievement achievement, GuiScreen parent)
	{
		super(id, x, y, width, height, achievement.text);
		
		this.achievement = achievement;
	}
	
	@Override
	public void drawButton(Minecraft par1Minecraft, int mouseX, int mouseY)
	{
		par1Minecraft.renderEngine.bindTexture(texture);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		int offsetX = 0, offsetY = 0;
		
		if (achievement.getState())
		{
			offsetY = 20;
		}
		
		if (mouseX >= xPosition && mouseX <= xPosition + width && mouseY >= yPosition && mouseY <= yPosition + height)
		{
			offsetX = 20;
		}
		
		drawTexturedModalRect(xPosition, yPosition + ((height - 20) / 2), offsetX, offsetY, 20, 20);

		par1Minecraft.fontRenderer.drawSplitString(this.displayString, xPosition + 25, yPosition + (height / 2) - 4, this.width, achievement.getState() ? 0x009105 : 0x000000);
	}

	public int getHeight()
	{
		return this.height;
	}
}
