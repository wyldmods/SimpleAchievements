package com.insane.simpleachievements;

import java.util.Arrays;
import java.util.List;

public class AchievementHandler
{
	public static class SimpleAchievement
	{
		public final String text;
		private boolean state;
		
		public SimpleAchievement(String text, boolean state)
		{
			this.text = text;
			this.state = state;
		}
		
		public SimpleAchievement(String text)
		{
			this(text, false);
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
	}
	
	private final SimpleAchievement[] achievements;

	public AchievementHandler(String username, List<SimpleAchievement> listOfAchievements)
	{		
		achievements = new SimpleAchievement[listOfAchievements.size()];
		for (int i = 0; i < listOfAchievements.size(); i++)
		{
			achievements[i] = listOfAchievements.get(i);
		}
	}
	
	public AchievementHandler(String username)
	{		
		this(username, SimpleAchievements.defaults);
	}

	public void toggleAchievement(SimpleAchievement ach)
	{
		ach.toggle();
	}
	
	public void toggleAchievement(int id)
	{
		toggleAchievement(achievements[id]);
	}

	public SimpleAchievement getAchievement(int id)
	{
		return achievements[id];
	}
	
	public String getAchievementText(int id)
	{
		return achievements[id].text;
	}
	
	public boolean getAchievementState(int id)
	{
		return achievements[id].getState();
	}

	public List<SimpleAchievement> getAchievementList()
	{
		return Arrays.asList(achievements);
	}
	
	public SimpleAchievement[] getAchievementArr()
	{
		return Arrays.copyOf(achievements, achievements.length);
	}

	public int numAchievements()
	{
		return achievements.length;
	}

}
