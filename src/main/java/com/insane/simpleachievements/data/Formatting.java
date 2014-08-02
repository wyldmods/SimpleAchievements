package com.insane.simpleachievements.data;

import com.insane.simpleachievements.data.Element.Alignment;

public class Formatting
{
	public boolean isAchievement = true;
	
	public int color = 0x000000, colorChecked = 0x009010;
	
	public Alignment align = Alignment.LEFT;
	
	public boolean shadow = false;
	
	public Formatting() {}
	
	public Formatting(boolean isAchievmenet, int color, int colorChecked, String align, boolean shadow)
	{
		this.isAchievement = isAchievmenet;
		this.color = color;
		this.colorChecked = colorChecked;
		this.align = Alignment.valueOf(align.toUpperCase());
		this.shadow = shadow;
	}

	public void applyTo(Element ele)
	{
		ele.color = color;
		ele.colorChecked = colorChecked;
		ele.isAchievement = isAchievement;
		ele.align = align;
		ele.shadow = shadow;
	}
}
