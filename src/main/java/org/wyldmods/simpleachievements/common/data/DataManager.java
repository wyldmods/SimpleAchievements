package org.wyldmods.simpleachievements.common.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.wyldmods.simpleachievements.SimpleAchievements;
import org.wyldmods.simpleachievements.client.gui.Offset;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

	private static DataManager instance = new DataManager();

	public static DataManager instance()
	{
		return instance;
	}

	private Map<String, DataHandler> map;
	private Map<Integer, Formatting> formats;
	private Map<String, Offset> specialUsers;
	private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private File saveDir, saveFile;
	
	private DataManager()
	{
		map = new HashMap<String, DataHandler>();
		formats = new HashMap<Integer, Formatting>();
		specialUsers = new HashMap<String, Offset>();
	}

	@SuppressWarnings("serial")
	public void initFormatting() throws FileNotFoundException
	{
		String s = "";
		Scanner scan = new Scanner(SimpleAchievements.divConfig);
		while (scan.hasNextLine())
		{
			s += scan.nextLine() + "\n";
		}

		formats = gson.fromJson(s, new TypeToken<Map<Integer, Formatting>>(){}.getType());
		if (formats == null)
		{
			formats = new HashMap<Integer, Formatting>();
			formats.put(0, new Formatting());
		}
		scan.close();
	}
	
	@SuppressWarnings("serial")
	public void initSpecialUsers() throws IOException
	{
		URL url = SimpleAchievements.class.getResource("/assets/simpleachievements/misc/" + "specialUsers.json");
		
		if (url == null) return;
		
		File file = new File(url.getFile());
			
		String s = "";
		Scanner scan = new Scanner(file);
		while (scan.hasNextLine())
		{
			s += scan.nextLine() + "\n";
		}
		
		specialUsers = gson.fromJson(s, new TypeToken<Map<String, Offset>>(){}.getType());
		if (specialUsers == null)
		{
			specialUsers = new HashMap<String, Offset>();
		}
		scan.close();
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
			
			initSpecialUsers();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void save()
	{
		saveMap(saveFile, this, this.map);
	}

	public void toggleAchievement(String username, int id)
	{
		checkMap(username);

		map.get(username).toggleAchievement(id);
	}

	public DataHandler getHandlerFor(String username)
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

	private static void saveMap(File file, DataManager instance, Object map)
	{
		String json = gson.toJson(map);

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

	public void changeMap(EntityPlayer player, DataHandler handler)
	{
		map.put(player.username, handler);
	}
	
	public Formatting getFormat(int div)
	{
		return formats.get(div);
	}

	private static final Offset defaultOffset = new Offset(0, 0);
	public Offset getOffsetFor(String username)
	{
		Offset offset = specialUsers.get(username);
		return offset == null ? defaultOffset : offset;
	}

	public void flush()
	{
		this.map.clear();
		this.formats.clear();
		this.specialUsers.clear();
				
		saveFile.delete();
	}
}
