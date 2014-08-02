package com.insane.simpleachievements.data;

import static com.insane.simpleachievements.data.Element.Alignment.LEFT;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;

import com.insane.simpleachievements.networking.IByteEncodable;

public class Element implements IByteEncodable<Element>
{
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
	
	public Element(){}
	
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

	@Override
	public byte[] encode()
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
		DataOutputStream out = new DataOutputStream(bos);

		try
		{
			out.writeUTF(text);
			out.writeBoolean(state);

			out.writeBoolean(isAchievement);

			out.writeInt(color);
			out.writeInt(height);
			out.writeInt(colorChecked);

			out.writeInt(align.ordinal());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return bos.toByteArray();
	}

	@Override
	public Element decode(DataInputStream data, EntityPlayer player)
	{
		try
		{
			text = data.readUTF();
			state = data.readBoolean();

			isAchievement = data.readBoolean();

			color = data.readInt();
			height = data.readInt();
			colorChecked = data.readInt();

			align = Alignment.values()[data.readInt()];
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return this;
	}
}
