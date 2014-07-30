package com.insane.simpleachievements.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.insane.simpleachievements.SimpleAchievements;

public class DataManager
{
	@ForgeSubscribe
	public void onWorldLoad(WorldEvent.Load load)
	{
		if (!load.world.isRemote)
			load();
	}

	@ForgeSubscribe
	public void onWorldSave(WorldEvent.Save save)
	{
		if (!save.world.isRemote)
			save();
	}

	private static DataManager instance;

	public static DataManager instance()
	{
		return instance == null ? instance = new DataManager() : instance;
	}

	private Map<String, DataHandler> map;
	private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private File saveDir, saveFile;

	private DataManager()
	{
		map = new HashMap<String, DataHandler>();
	}

	public void load()
	{
		saveDir = new File(DimensionManager.getCurrentSaveRootDirectory().getAbsolutePath() + "/" + SimpleAchievements.MODID);
		saveDir.mkdirs();
		saveFile = new File(saveDir.getAbsolutePath() + "/" + "achievements.json");

		try
		{
			if (saveFile.createNewFile())
			{
				return;
			}
			else
			{
				map = loadMap(saveFile);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void save()
	{
		saveMap(saveFile, this);
	}

	public void toggleAchievement(String username, int id)
	{
		checkMap(username);

		map.get(username).toggleAchievement(id);
	}

	public DataHandler getAchievementsFor(String username)
	{
		checkMap(username);

		return map.get(username);
	}

	public void checkMap(String username)
	{
		if (!map.containsKey(username))
		{
			map.put(username, new DataHandler());
		}
	}

	@SuppressWarnings("serial")
	private static Map<String, DataHandler> loadMap(File file) throws FileNotFoundException
	{
		String json = "";

		Scanner scan = new Scanner(file);
		while (scan.hasNextLine())
		{
			json += scan.nextLine() + "\n";
		}
		scan.close();

		Map<String, DataHandler> ret = gson.fromJson(json, new TypeToken<Map<String, DataHandler>>() {}.getType());
		return ret == null ? new HashMap<String, DataHandler>() : ret;
	}

	private static void saveMap(File file, DataManager instance)
	{
		String json = gson.toJson(instance.map);

		try
		{
			FileWriter fw = new FileWriter(file);

			fw.write(json);

			fw.flush();
			fw.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.out.println("Could not save achievements file!");
		}
	}

    public void changeMap(EntityPlayer player, DataHandler handler) {
        map.put(player.username, handler);
    }
}
