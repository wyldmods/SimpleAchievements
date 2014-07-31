package com.insane.simpleachievements.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.insane.simpleachievements.SimpleAchievements;
import com.insane.simpleachievements.data.Element;

public class ButtonCheckBox extends GuiButton
{
	private static final ResourceLocation texture = new ResourceLocation(SimpleAchievements.MODID.toLowerCase(), "textures/gui/checkboxes.png");
	
	private final Element element;
	
	public ButtonCheckBox(int id, int x, int y, int width, int height, Element achievement, GuiScreen parent)
	{
		super(id, x, y, width, height, achievement.getText());
		
		this.element = achievement;
		
		this.height = height + Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(achievement.getText(), width).size() * 3;
	}
	
	@Override
	public void drawButton(Minecraft par1Minecraft, int mouseX, int mouseY)
	{
		par1Minecraft.renderEngine.bindTexture(texture);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		int offsetX = 0, offsetY = 0;
		
		if (element.getState())
		{
			offsetY = 20;
		}
		
		if (mouseX >= xPosition && mouseX <= xPosition + width && mouseY >= yPosition && mouseY <= yPosition + height)
		{
			offsetX = 20;
		}
		
		drawTexturedModalRect(xPosition, yPosition + (height / 2) - 10, offsetX, offsetY, 20, 20);

		FontRenderer fnt = Minecraft.getMinecraft().fontRenderer;
		int lineNum = getExpectedLines(this.element, width);
		fnt.drawSplitString(element.getText(), xPosition + 25, (int) (yPosition + (height / 2) - lineNum * 4), this.width, element.getColorBasedOnState());
	}

	public int getHeight()
	{
		return this.height;
	}

    public static int getExpectedLines(Element ele, int width) {
        return Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(ele.getText(), width).size();
    }
}
