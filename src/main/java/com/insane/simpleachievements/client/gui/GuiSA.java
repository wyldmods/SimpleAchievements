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
import com.insane.simpleachievements.common.NBTUtils;
import com.insane.simpleachievements.common.data.DataHandler;
import com.insane.simpleachievements.common.data.DataManager;
import com.insane.simpleachievements.common.data.Element;
import com.insane.simpleachievements.common.networking.PacketHandlerSA;

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
		super();

		this.mc = Minecraft.getMinecraft();
		elements = DataManager.instance().getHandlerFor(player.username);
		page = NBTUtils.getTag(player.getCurrentEquippedItem()).getInteger("sa:page");
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
		
		if (achOffset >= chievs.length && page > 0)
		{
			decrPage();
			return;
		}

		//page 1
		for (int i = achOffset; i < chievs.length; i++)
		{
			int height = baseHeight + (ButtonElement.getExpectedLines(chievs[i], width) * charHeight);
			if (yPos < bookHeight - height - 10)
			{
				ButtonElement button = new ButtonElement(i, startX + 25, startY + yPos, width, chievs[i], this);
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
				ButtonElement button = new ButtonElement(i, startX + 10 + (bookWidth / 2), startY + yPos, width, chievs[i], this);
				yPos += button.getHeight();
				buttonList.add(button);
			}
		}

		buttonList.add(new ButtonPage(chievs.length, startX + bookWidth - 22, startY + bookHeight - 23, true));
		buttonList.add(new ButtonPage(chievs.length + 1, startX, startY + bookHeight - 23, false));
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
				incrPage();
			}
			else if (button.id == elements.numElements() + 1)
			{
				decrPage();
			}
		}
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
		Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem().getTagCompound().setInteger("sa:page", page);
		PacketHandlerSA.sendPageUpdateToServer(Minecraft.getMinecraft().thePlayer, page);
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
