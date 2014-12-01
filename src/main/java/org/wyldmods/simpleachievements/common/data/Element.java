package org.wyldmods.simpleachievements.common.data;

import static org.wyldmods.simpleachievements.common.data.Element.Alignment.*;

import java.io.Serializable;

public class Element implements Serializable
{
    private static final long serialVersionUID = -6961457157774225518L;

    public enum Alignment
	{
		LEFT, CENTER, RIGHT;
	}

	public String text;
	public boolean state;

	public boolean isAchievement = false;

	public int color = 0x000000;
	public int height = 30;

	public int colorChecked = 0x009010;

	public static final String lineSplit = "|";

	public Alignment align = LEFT;

	public boolean shadow = false;

	public Element()
	{
	}

	public Element(String text)
	{
		this.text = text;
	}

	public boolean getState()
	{
		return state;
	}

	public void setState(boolean newState)
	{
		state = newState;
	}

	public void toggle()
	{
		state = !state;
	}

	public boolean isAchievement()
	{
		return isAchievement;
	}

	public int getColor()
	{
		return color;
	}

	public int getCheckedColor()
	{
		return colorChecked;
	}

	public int getBaseHeight()
	{
		return height;
	}

	public Alignment getAlign()
	{
		return align;
	}

	public String getText()
	{
		return text.replace(lineSplit, "\n");
	}

	public void setText(String s)
	{
		text = s;
	}

	public int getColorBasedOnState()
	{
		return getState() ? colorChecked : color;
	}
}
