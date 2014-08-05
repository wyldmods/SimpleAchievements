package com.insane.simpleachievements.client.gui;

import static com.insane.simpleachievements.SimpleAchievements.bookHeight;
import static com.insane.simpleachievements.SimpleAchievements.bookWidth;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.insane.simpleachievements.SimpleAchievements;
import com.insane.simpleachievements.common.data.DataHandler;
import com.insane.simpleachievements.common.data.DataManager;
import com.insane.simpleachievements.common.data.Element;

public class GuiSA extends GuiScreen
{
	private final int maxDelay = 5;
	private int clickDelay = maxDelay;

	public static final int GUI_ID = 20;

	private DataHandler elements;

	private int page;
    private int entryCount;
    
    private int startX;
    private int startY = 2;
    
    private int startYAch = 15;
    
    private int achOffset = 0;
    
    private int charHeight = 3;
    
    private static ResourceLocation bgl = new ResourceLocation(SimpleAchievements.MODID.toLowerCase() + ":" + "textures/gui/bookgui_left.png");
    private static ResourceLocation bgr = new ResourceLocation(SimpleAchievements.MODID.toLowerCase() + ":" + "textures/gui/bookgui_right.png");

	public GuiSA(EntityPlayer player)
	{
		super();

		this.mc = Minecraft.getMinecraft();
		elements = DataManager.instance().getHandlerFor(player.username);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initGui()
	{
		super.initGui();

		clickDelay = 5;
		buttonList.clear();

		Element[] chievs = elements.getAchievementArr();
		
		achOffset = page * entryCount * 2;

		int baseHeight = 30;
		int yPos = startYAch;
		int width = bookWidth / 2 - 60;

		//page 1
		for (int i = achOffset; i < chievs.length; i++)
		{
			int height = baseHeight + (ButtonElement.getExpectedLines(chievs[i], width) * charHeight);
			if (yPos < bookHeight - height - 10)
			{
				ButtonElement button = new ButtonElement(i, startX + 25, startY + yPos, width, chievs[i]);
				yPos += button.getHeight();
				buttonList.add(button);
			}
		}
		
		yPos = startYAch;
		
		// page 2
		for (int i = achOffset + entryCount; i < chievs.length; i++)
		{
			int height = baseHeight + (ButtonElement.getExpectedLines(chievs[i], width) * charHeight);
			if (yPos < bookHeight - height - 10)
			{
				ButtonElement button = new ButtonElement(i, startX + 10 + (bookWidth / 2), startY + yPos, width, chievs[i]);
				yPos += button.getHeight();
				buttonList.add(button);
			}
		}

		buttonList.add(new GuiButton(chievs.length, this.width - 60, height - 30, 50, 20, "Next"));
		buttonList.add(new GuiButton(chievs.length + 1, 10, height - 30, 50, 20, "Prev"));
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
			elements.toggleAchievement(button.id);
		}
		else if (clickDelay == 0)
		{
			if (button.id == elements.numElements())
			{
				page++;
				initGui();
			}
			else if (button.id == elements.numElements() + 1)
			{
				page = page == 0 ? 0 : page - 1;
				initGui();
			}
		}
	}

    @Override
    public void setWorldAndResolution(Minecraft par1Minecraft, int par2, int par3) {

        this.mc = par1Minecraft;
        this.fontRenderer = par1Minecraft.fontRenderer;
        this.width = par2;
        this.height = par3;
        this.buttonList.clear();
        this.initGui();
        this.entryCount = calculateNumberOfEntries();
        this.initGui();
    }

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

    @SuppressWarnings("unchecked")
	private int calculateNumberOfEntries() {
    	
    	int len = 0;
    	int count = 0;
    	for (GuiButton button : (List<GuiButton>) buttonList)
    	{
    		if (button instanceof ButtonElement)
    		{    	
        		count++;

    			int height = ((ButtonElement)button).getHeight();
    			
    			len += height;
    			
        		if (len > bookHeight - height - startYAch - 10)
        		{
        			return count;
        		}        		
    		}
    	}
    	return count;
    }
}
