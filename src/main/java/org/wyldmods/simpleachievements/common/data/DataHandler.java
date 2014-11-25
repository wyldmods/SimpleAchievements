package org.wyldmods.simpleachievements.common.data;

import java.util.Arrays;
import java.util.List;

import org.wyldmods.simpleachievements.common.config.ConfigHandler;

public class DataHandler
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
}
