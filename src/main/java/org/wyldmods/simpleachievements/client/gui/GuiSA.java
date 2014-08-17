package org.wyldmods.simpleachievements.client.gui;

import static org.wyldmods.simpleachievements.SimpleAchievements.bookHeight;
import static org.wyldmods.simpleachievements.SimpleAchievements.bookWidth;
import static org.wyldmods.simpleachievements.client.gui.GuiSA.Origin.BLOCK;
import static org.wyldmods.simpleachievements.client.gui.GuiSA.Origin.ITEM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.wyldmods.simpleachievements.SimpleAchievements;
import org.wyldmods.simpleachievements.common.NBTUtils;
import org.wyldmods.simpleachievements.common.TileEntityAchievementStand;
import org.wyldmods.simpleachievements.common.data.DataHandler;
import org.wyldmods.simpleachievements.common.data.DataManager;
import org.wyldmods.simpleachievements.common.data.Element;
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
	 * Page -> Start Index
	 * <p>
	 * A map to store all the start indeces of pages. Much better than the old
	 * system...trust me.
	 */
	private Map<Integer, Integer> pageMap;

	private int charHeight = 3;

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
			if (this.drawButton)
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
		elements = DataManager.instance().getHandlerFor(player.username);
		page = par1Page;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initGui()
	{
		super.initGui();

		if (pageMap == null)
		{
			buildPageMap();
		}

		clickDelay = 5;
		buttonList.clear();

		int idx = pageMap.get(page);
		getNextButtons(idx, buttonList);

		buttonList.add(new ButtonPage(elements.numElements(), startX + bookWidth - 22, startY + bookHeight - 23, true));
		buttonList.add(new ButtonPage(elements.numElements() + 1, startX, startY + bookHeight - 23, false));
	}

	private void buildPageMap()
	{
		List<ButtonElement> mockList = new ArrayList<ButtonElement>();
		pageMap = new HashMap<Integer, Integer>();

		int idx = 0;
		int page = 0;
		while (idx < elements.numElements())
		{
			pageMap.put(page, idx);
			idx = getNextButtons(idx, mockList);
			page++;
		}
	}

	private int getNextButtons(int startIndex, List<ButtonElement> buttons)
	{
		int baseHeight = 30;
		int yPos = startYAch;
		int width = bookWidth / 2 - 60;
		Element[] chievs = elements.getAchievementArr();

		// page 1
		for (; startIndex < chievs.length; startIndex++)
		{
			int height = baseHeight + (ButtonElement.getExpectedLines(chievs[startIndex], width) * charHeight);
			if (yPos < bookHeight - height - 10)
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
			if (yPos < bookHeight - height - 10)
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
		PacketHandlerSA.sendAchUpdateToServer(Minecraft.getMinecraft().thePlayer, id, elements.getAchievement(id).getState());
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
			EntityPlayer player2 = mc.thePlayer;
			PacketHandlerSA.sendTileUpdateToServer(player2, stand.page, stand.xCoord, stand.yCoord, stand.zCoord);
			break;
		case ITEM:
			EntityPlayer player = mc.thePlayer;
			player.getCurrentEquippedItem().getTagCompound().setInteger("sa:page", page);
			PacketHandlerSA.sendPageUpdateToServer(player, page);
			break;
		}
	}

	@Override
	public void setWorldAndResolution(Minecraft par1Minecraft, int par2, int par3)
	{
		this.mc = par1Minecraft;
		this.fontRenderer = par1Minecraft.fontRenderer;
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
