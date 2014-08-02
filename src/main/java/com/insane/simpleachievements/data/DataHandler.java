package com.insane.simpleachievements.data;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;

import com.insane.simpleachievements.config.ConfigHandler;
import com.insane.simpleachievements.networking.IByteEncodable;

public class DataHandler implements IByteEncodable<DataHandler>
{
	private Element[] elements;

	public DataHandler(List<Element> listOfElements)
	{
		elements = new Element[listOfElements.size()];
		for (int i = 0; i < listOfElements.size(); i++)
		{
			elements[i] = listOfElements.get(i);
		}
	}

	public DataHandler()
	{
		this(ConfigHandler.defaultElements);
	}

	public void toggleAchievement(Element ach)
	{
		ach.toggle();
	}

	public void toggleAchievement(int id)
	{
		toggleAchievement(elements[id]);
	}

	public Element getAchievement(int id)
	{
		return elements[id];
	}

	public String getAchievementText(int id)
	{
		return elements[id].getText();
	}

	public boolean getAchievementState(int id)
	{
		return elements[id].getState();
	}

	public List<Element> getAchievementList()
	{
		return Arrays.asList(elements);
	}

	public Element[] getAchievementArr()
	{
		return Arrays.copyOf(elements, elements.length);
	}

	public int numElements()
	{
		return elements.length;
	}

	@Override
	public byte[] encode()
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
		DataOutputStream outputStream = new DataOutputStream(bos);
		try
		{
			for (IByteEncodable<Element> ele : elements)
			{
				outputStream.write(ele.encode());
			}
		}
		catch (IOException error)
		{
			error.printStackTrace();
		}

		return bos.toByteArray();
	}

	@Override
	public DataHandler decode(DataInputStream dis, EntityPlayer player)
	{
		ArrayList<Element> newElements = new ArrayList<Element>();

		try
		{
			while (dis.available() > 0)
			{
				newElements.add(new Element().decode(dis, player));
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		this.elements = newElements.toArray(new Element[] {});

		DataManager.instance().changeMap(player, this);

		return this;
	}
}
