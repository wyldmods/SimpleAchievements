package org.wyldmods.simpleachievements.client.gui;

import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;

import org.wyldmods.simpleachievements.SimpleAchievements;
import org.wyldmods.simpleachievements.common.data.DataManager;
import org.wyldmods.simpleachievements.common.data.Element;

import static org.wyldmods.simpleachievements.SimpleAchievements.bookWidth;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class ButtonElement extends GuiButton
{
	static final @Nonnull ResourceLocation texture = new ResourceLocation(SimpleAchievements.MODID.toLowerCase(Locale.US), "textures/gui/checkboxes.png");

	private final Element element;

	public ButtonElement(int id, int x, int y, int width, Element ele, GuiSA parent)
	{
		super(id, x, y, width, ele.height, ele.getText());

		this.element = ele;

		this.height += (Minecraft.getMinecraft().fontRendererObj.listFormattedStringToWidth(ele.getText(), width).size() - 1) * 8;
	}

	@Override
	public void drawButton(Minecraft par1Minecraft, int mouseX, int mouseY)
	{
		par1Minecraft.renderEngine.bindTexture(texture);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		int offsetX = 0, offsetY = 0;

		// get offset for state and hover
		if (element.getState())
		{
			offsetY = 20;
		}
		if (mouseX >= xPosition && mouseX <= xPosition + width && mouseY >= yPosition && mouseY <= yPosition + height)
		{
			offsetX = 20;
		}

		Offset offset = getOffsetForPlayer(Minecraft.getMinecraft().thePlayer);
		offsetX += offset.x * 40;
		offsetY += offset.y * 40;

		// don't render icon if not achievement
		if (element.isAchievement)
		{
			drawTexturedModalRect(xPosition, yPosition + (height / 2) - 10, offsetX, offsetY, 20, 20);
		}

		FontRenderer fnt = Minecraft.getMinecraft().fontRendererObj;
		int lineNum = getExpectedLines(this.element, width);

		// render the text according to alignment
		switch (element.align)
		{
		case CENTER:
			List<String> lines = fnt.listFormattedStringToWidth(element.getText(), this.width);
			for (int i = 0; i < lines.size(); i++)
			{
				String s = lines.get(i);
				fnt.drawString(s, xPosition + getIconOffset() + (bookWidth / 4) - 20 - (fnt.getStringWidth(s) / 2), yPosition + (height / 2) - lineNum * 4 + i * 8,
						element.getColorBasedOnState(), element.shadow);
			}
			break;
		case LEFT:
			fnt.drawSplitString(element.getText(), xPosition + getIconOffset(), yPosition + (height / 2) - lineNum * 4, this.width, element.getColorBasedOnState());
			break;
		case RIGHT:
			lines = fnt.listFormattedStringToWidth(element.getText(), this.width);
			for (int i = 0; i < lines.size(); i++)
			{
				String s = lines.get(i);
				fnt.drawString(s, xPosition + 25 + this.width - fnt.getStringWidth(s), yPosition + (height / 2) - lineNum * 4 + i * 8, element.getColorBasedOnState(), element.shadow);
			}
			break;
		}
	}

	private int getIconOffset()
	{
		return element.isAchievement ? 25 : 0;
	}

	public int getHeight()
	{
		return this.height;
	}

	// don't allow the element to be clickable if not an achievement
	@Override
	public boolean mousePressed(Minecraft par1Minecraft, int par2, int par3)
	{
		if (element.isAchievement)
		{
			return super.mousePressed(par1Minecraft, par2, par3);
		}
		return false;
	}

	public static int getExpectedLines(Element ele, int width)
	{
		return Minecraft.getMinecraft().fontRendererObj.listFormattedStringToWidth(ele.getText(), width).size();
	}

	private static Offset getOffsetForPlayer(EntityPlayer player)
	{
		return DataManager.INSTANCE.getOffsetFor(player.getName());
	}
}
