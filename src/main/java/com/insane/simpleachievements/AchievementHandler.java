package com.insane.simpleachievements;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;

import com.insane.simpleachievements.networking.IByteEncodable;

public class AchievementHandler implements IByteEncodable
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
	
	private SimpleAchievement[] achievements;

	public AchievementHandler(List<SimpleAchievement> listOfAchievements)
	{		
		achievements = new SimpleAchievement[listOfAchievements.size()];
		for (int i = 0; i < listOfAchievements.size(); i++)
		{
			achievements[i] = listOfAchievements.get(i);
		}
	}
	
	public AchievementHandler()
	{		
		this(SimpleAchievements.defaults);
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

	@Override
	public byte[] encode()
	{
        ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
        DataOutputStream outputStream = new DataOutputStream(bos);
        try {
            for (int i = 0; i < numAchievements(); i++) {
                outputStream.writeUTF(getAchievementText(i));
                outputStream.writeBoolean(getAchievementState(i));
            }
        } catch (IOException error) {
            error.printStackTrace();
        }

        return bos.toByteArray();
	}

	@Override
	public void decode(DataInputStream dis, int length, EntityPlayer player)
	{
		ArrayList<SimpleAchievement> newAchievements = new ArrayList<SimpleAchievement>();
		try
		{
			int counter = 0;
			while (counter < length)
			{
				newAchievements.add(new AchievementHandler.SimpleAchievement(dis.readUTF(), dis.readBoolean()));
				counter++;
			}
		}
		catch (IOException error)
		{
			System.out.print("Issue with packet");
			error.printStackTrace();
		}
		
		this.achievements = newAchievements.toArray(new SimpleAchievement[]{});
		
		AchievementManager.instance().changeMap(player, this);
	}

}
