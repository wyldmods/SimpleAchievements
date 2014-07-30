package com.insane.simpleachievements.client.gui;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
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

    private static int maxStringLength=28;
	
	public ButtonCheckBox(int id, int x, int y, int width, int height, Element achievement, GuiScreen parent)
	{
		super(id, x, y, width, height, achievement.text);
		
		this.element = achievement;

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
		
		drawTexturedModalRect(xPosition, yPosition + ((height - 20) / 2), offsetX, offsetY, 20, 20);

        ArrayList<String> lines = getExpectedLines(element.text);
        switch (lines.size()) {
            case 1:
                par1Minecraft.fontRenderer.drawString(this.displayString, xPosition + 25, yPosition + (height / 2) - 4, element.getState() ? 0x009105 : 0x000000, false);
                break;
            case 2:
                par1Minecraft.fontRenderer.drawString(lines.get(0), xPosition + 25, yPosition + (height / 3) - 4, element.getState() ? 0x009105 : 0x000000, false);
                par1Minecraft.fontRenderer.drawString(lines.get(1), xPosition + 25, yPosition + 2*(height / 3) - 4, element.getState() ? 0x009105 : 0x000000, false);
                break;
            case 3:
                par1Minecraft.fontRenderer.drawString(lines.get(0), xPosition + 25, yPosition + (height / 3) - 6, element.getState() ? 0x009105 : 0x000000, false);
                par1Minecraft.fontRenderer.drawString(lines.get(1), xPosition + 25, yPosition + 2*(height / 3) - 6, element.getState() ? 0x009105 : 0x000000, false);
                par1Minecraft.fontRenderer.drawString(lines.get(2), xPosition + 25, yPosition + 3*(height / 3) - 6, element.getState() ? 0x009105 : 0x000000, false);
                break;
            default:
                System.out.println(lines);
                par1Minecraft.fontRenderer.drawSplitString(this.displayString, xPosition + 25, yPosition + (height / 2) - 4, this.width, element.getState() ? 0x009105 : 0x000000);
        }
	}

	public int getHeight()
	{
		return this.height;
	}

    public static ArrayList<String> getExpectedLines(String str) {
        String text = str;
        int numLines = (text.length()/maxStringLength)+1;

        int counter=0;
        ArrayList<String> result = new ArrayList<String>();
        String[] wordArray = text.split(" ");
        String current= "";
        for (int i=0; i<wordArray.length; i++) {
            if ((current+wordArray[i]).length()<maxStringLength) {
                current = current + " " + wordArray[i];
            } else {
                result.add(current);
                current=wordArray[i];
            }
        }
        if (!current.equals("")) {
            result.add(current);
        }

        return result;
    }
}
