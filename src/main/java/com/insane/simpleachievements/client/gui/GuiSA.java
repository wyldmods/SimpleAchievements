package com.insane.simpleachievements.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;

import com.insane.simpleachievements.AchievementHandler;
import com.insane.simpleachievements.AchievementManager;
import com.insane.simpleachievements.AchievementHandler.SimpleAchievement;

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
    private int AMOUNT_OF_ENTRIES;

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

		int HARDCODED_ENTRIES_PER_PAGE_FIX_THIS = 8;
		
		int achOffset = page * AMOUNT_OF_ENTRIES;

		for (int i = achOffset; i < chievs.length; i++)
		{
			int yPos = 10 + ((i - achOffset) * 25);
			if (yPos < height - 30)
			{
				buttonList.add(new ButtonCheckBox(i, 20, yPos, chievs[i], this));
			}
		}

		buttonList.add(new GuiButton(chievs.length, width - 60, height - 30, 50, 20, "Next"));
		buttonList.add(new GuiButton(chievs.length + 1, 10, height - 30, 50, 20, "Prev"));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float par3)
	{
		clickDelay = Math.max(0, clickDelay - 1);
		
		drawRect(5, 5, this.width - 5, this.height - 5, 0xFA2E2E2E);

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
        this.AMOUNT_OF_ENTRIES = calculateNumberOfEntries(par2,par3);
        this.initGui();
    }

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

    private int calculateNumberOfEntries(int width, int height) {
        int padding = 10;

        int result = ((height-30)-padding)/25;
        return result;
    }
}
