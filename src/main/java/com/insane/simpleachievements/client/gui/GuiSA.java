package com.insane.simpleachievements.client.gui;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.insane.simpleachievements.AchievementHandler;
import com.insane.simpleachievements.AchievementHandler.SimpleAchievement;
import com.insane.simpleachievements.AchievementManager;
import com.insane.simpleachievements.SimpleAchievements;

/**
 * Created by Michael on 29/07/2014.
 */
public class GuiSA extends GuiScreen
{
	private final int maxDelay = 5;
	private int clickDelay = maxDelay;

	public static final int GUI_ID = 20;

	private AchievementHandler achievements;

	private int page;
    private int entryCount;
    
    private int bookWidth = 417;
    private int bookHeight = 245;
    
    private int startX;
    private int startY = 2;
    
    private int startYAch = 15;
    
    private static ResourceLocation bgl = new ResourceLocation(SimpleAchievements.MODID.toLowerCase() + ":" + "textures/gui/bookgui_left.png");
    private static ResourceLocation bgr = new ResourceLocation(SimpleAchievements.MODID.toLowerCase() + ":" + "textures/gui/bookgui_right.png");

	public GuiSA(EntityPlayer player)
	{
		super();

		this.mc = Minecraft.getMinecraft();
		achievements = AchievementManager.instance().getAchievementsFor(player.username);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initGui()
	{
		super.initGui();

		clickDelay = 5;
		buttonList.clear();

		SimpleAchievement[] chievs = achievements.getAchievementArr();
		
		int achOffset = page * entryCount * 2;

		int height = 30;
		
		//page 1
		for (int i = achOffset; i < chievs.length; i++)
		{
			int yPos = startYAch + ((i - achOffset) * height);
			if (yPos < bookHeight - height)
			{
				buttonList.add(new ButtonCheckBox(i, startX + 25, startY + yPos, bookWidth / 2 - 45, height, chievs[i], this));
			}
		}
		
		// page 2
		for (int i = achOffset + entryCount; i < chievs.length; i++)
		{
			int yPos = startYAch + ((i - achOffset - entryCount) * height);
			if (yPos < bookHeight - height)
			{
				buttonList.add(new ButtonCheckBox(i, startX + 15 + (bookWidth / 2), startY + yPos, bookWidth / 2 - 45, height, chievs[i], this));
			}
		}

		buttonList.add(new GuiButton(chievs.length, width - 60, height - 30, 50, 20, "Next"));
		buttonList.add(new GuiButton(chievs.length + 1, 10, height - 30, 50, 20, "Prev"));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float par3)
	{
		clickDelay = Math.max(0, clickDelay - 1);
				
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                
        int newX = (this.width - this.bookWidth) / 2;
        int newY = (int) ((this.height - this.bookHeight) / 2.5);
        if (startX != newX || startY != newY)
        {
        	startX = newX;
        	startY = newY;
        	initGui();
        }
        
		this.mc.getTextureManager().bindTexture(bgl);
        this.drawTexturedModalRect(startX, startY, 0, 0, this.bookWidth / 2, this.bookHeight);
      
        this.mc.getTextureManager().bindTexture(bgr);
        this.drawTexturedModalRect(startX + this.bookWidth / 2, startY, 0, 0, this.bookWidth / 2, this.bookHeight);
        
        super.drawScreen(mouseX, mouseY, par3);
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if (button.id < achievements.numAchievements())
		{
			achievements.toggleAchievement(button.id);
		}
		else if (clickDelay == 0)
		{
			if (button.id == achievements.numAchievements())
			{
				page++;
				initGui();
			}
			else if (button.id == achievements.numAchievements() + 1)
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
    		if (button instanceof ButtonCheckBox)
    		{
    			count++;
    			
    			int height = ((ButtonCheckBox)button).getHeight();
    			
    			len += height;
    			
        		if (len > bookHeight - height - startYAch)
        		{
        			return count;
        		}
    		}
    	}
    	return count;
    }
}
