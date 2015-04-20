package org.wyldmods.simpleachievements.client.gui;

import static org.wyldmods.simpleachievements.SimpleAchievements.*;
import static org.wyldmods.simpleachievements.client.gui.GuiSA.Origin.*;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.wyldmods.simpleachievements.SimpleAchievements;
import org.wyldmods.simpleachievements.common.NBTUtils;
import org.wyldmods.simpleachievements.common.TileEntityAchievementStand;
import org.wyldmods.simpleachievements.common.data.DataHandler;
import org.wyldmods.simpleachievements.common.data.DataManager;
import org.wyldmods.simpleachievements.common.data.Element;
import org.wyldmods.simpleachievements.common.networking.MessageAchievement;
import org.wyldmods.simpleachievements.common.networking.PacketHandlerSA;

public class GuiSA extends GuiScreen
{
	public enum Origin
	{
		ITEM, BLOCK
	}

	private Origin origin;

	private final int maxDelay = 5;
	private int clickDelay = maxDelay;

	public static final int GUI_ID = 20;

	private DataHandler elements;

	private int page;

	private int startX;
	private int startY = 2;

	private int startYAch = 15;

	/**
	 * A list of all page start indexes (index in the list == page number)
	 */
	private List<Integer> pages;

	private int charHeight = 8;

	private TileEntityAchievementStand stand;

	private static ResourceLocation bgl = new ResourceLocation(SimpleAchievements.MODID.toLowerCase() + ":" + "textures/gui/bookgui_left.png");
	private static ResourceLocation bgr = new ResourceLocation(SimpleAchievements.MODID.toLowerCase() + ":" + "textures/gui/bookgui_right.png");

	private class ButtonPage extends GuiButton
	{
		private boolean next;

		public ButtonPage(int id, int x, int y, boolean next)
		{
			super(id, x, y, 21, 21, next ? "next" : "prev");
			this.next = next;
		}

		@Override
		public void drawButton(Minecraft par1Minecraft, int par2, int par3)
		{
			if (this.visible)
			{
				par1Minecraft.getTextureManager().bindTexture(ButtonElement.texture);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

				boolean hover = par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;

				this.drawTexturedModalRect(this.xPosition, this.yPosition, next ? 234 : 203, hover ? 211 : 233, this.width, this.height);

				this.mouseDragged(par1Minecraft, par2, par3);
			}
		}

		@Override
		public boolean mousePressed(Minecraft par1Minecraft, int par2, int par3)
		{
			if (clickDelay <= 0)
			{
				return super.mousePressed(par1Minecraft, par2, par3);
			}
			return false;
		}
	}

	public GuiSA(EntityPlayer player)
	{
		this(player, NBTUtils.getTag(player.getCurrentEquippedItem()).getInteger("sa:page"));
		this.origin = ITEM;
	}

	public GuiSA(EntityPlayer player, TileEntityAchievementStand par2stand)
	{
		this(player, par2stand.page);
		this.origin = BLOCK;
		this.stand = par2stand;
	}

	public GuiSA(EntityPlayer player, int par1Page)
	{
		super();
		this.mc = Minecraft.getMinecraft();
		elements = DataManager.INSTANCE.getHandlerFor(player.getCommandSenderName());
		page = par1Page;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initGui()
	{
		super.initGui();

		if (pages == null)
		{
			initPages();
		}

		clickDelay = 5;
		buttonList.clear();

		int idx = getIndex();
		getNextButtons(idx, buttonList);

		buttonList.add(new ButtonPage(elements.numElements(), startX + bookWidth - 22, startY + bookHeight - 23, true));
		buttonList.add(new ButtonPage(elements.numElements() + 1, startX, startY + bookHeight - 23, false));
	}

    private int getIndex()
    {
        if (pages.size() <= 0)
        {
            mc.thePlayer.closeScreen();
            mc.thePlayer.addChatComponentMessage(new ChatComponentText("No achievements found - check file encoding."));
            return -1;
        }

        if (page >= pages.size())
        {
			page--;
			return getIndex();
		}
		else if (page < 0)
		{
			page = 0;
			return getIndex();
		}
		else
		{
			return pages.get(page);
		}
	}

	private void initPages()
	{
		List<ButtonElement> mockList = new ArrayList<ButtonElement>();
		pages = new ArrayList<Integer>();

		int idx = 0;
		while (idx < elements.numElements())
		{
			pages.add(idx);
			idx = getNextButtons(idx, mockList);
		}
	}

    private int getNextButtons(int startIndex, List<ButtonElement> buttons)
    {
        if (startIndex < 0)
        {
            return startIndex;
        }

		int baseHeight = 30;
		int yPos = startYAch;
		int width = bookWidth / 2 - 60;
		Element[] chievs = elements.getAchievementArr();

		// page 1
		for (; startIndex < chievs.length; startIndex++)
		{
			int height = baseHeight + (ButtonElement.getExpectedLines(chievs[startIndex], width) * charHeight);
			if (yPos < bookHeight - height)
			{
				ButtonElement button = new ButtonElement(startIndex, startX + 25, startY + yPos, width, chievs[startIndex], this);
				yPos += button.getHeight();
				buttons.add(button);
			}
			else
			{
				break;
			}
		}

		yPos = startYAch;

		// page 2
		for (; startIndex < chievs.length; startIndex++)
		{
			int height = baseHeight + (ButtonElement.getExpectedLines(chievs[startIndex], width) * charHeight);
			if (yPos < bookHeight - height)
			{
				ButtonElement button = new ButtonElement(startIndex, startX + 10 + (bookWidth / 2), startY + yPos, width, chievs[startIndex], this);
				yPos += button.getHeight();
				buttons.add(button);
			}
			else
			{
				break;
			}
		}

		return startIndex;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float par3)
	{
		clickDelay = Math.max(0, clickDelay - 1);

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		int newX = (this.width - bookWidth) / 2;
		int newY = (int) ((this.height - bookHeight) / 2.5);
		if (startX != newX || startY != newY)
		{
			startX = newX;
			startY = newY;
			initGui();
		}

		this.mc.getTextureManager().bindTexture(bgl);
		this.drawTexturedModalRect(startX, startY, 0, 0, bookWidth / 2, bookHeight);

		this.mc.getTextureManager().bindTexture(bgr);
		this.drawTexturedModalRect(startX + bookWidth / 2, startY, 0, 0, bookWidth / 2, bookHeight);

		super.drawScreen(mouseX, mouseY, par3);
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if (button.id < elements.numElements())
		{
			toggleAchievement(button.id);
		}
		else if (clickDelay == 0)
		{
			if (button.id == elements.numElements())
			{
				incrPage();
			}
			else if (button.id == elements.numElements() + 1)
			{
				decrPage();
			}
		}
	}

	private void toggleAchievement(int id)
	{
		elements.toggleAchievement(id);
		PacketHandlerSA.INSTANCE.sendToServer(new MessageAchievement(id, elements.getAchievementState(id)));
	}

	private void incrPage()
	{
		page++;
		setNBT();
		initGui();
	}

	private void decrPage()
	{
		page = page == 0 ? 0 : page - 1;
		setNBT();
		initGui();
	}

	private void setNBT()
	{
		switch (origin)
		{
		case BLOCK:
			stand.page = page;
			PacketHandlerSA.INSTANCE.sendToServer(new MessageAchievement(stand.page, stand.xCoord, stand.yCoord, stand.zCoord));
			break;
		case ITEM:
			EntityPlayer player = mc.thePlayer;
			NBTUtils.getTag(player.getCurrentEquippedItem()).setInteger("sa:page", page);
			PacketHandlerSA.INSTANCE.sendToServer(new MessageAchievement(page));
			break;
		}
	}

	@Override
	public void setWorldAndResolution(Minecraft par1Minecraft, int par2, int par3)
	{
		this.mc = par1Minecraft;
		this.fontRendererObj = par1Minecraft.fontRenderer;
		this.width = par2;
		this.height = par3;
		this.buttonList.clear();
		this.initGui();
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}
}
