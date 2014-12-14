package org.wyldmods.simpleachievements.common.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.world.WorldEvent;

import org.wyldmods.simpleachievements.SimpleAchievements;
import org.wyldmods.simpleachievements.client.gui.Offset;
import org.wyldmods.simpleachievements.common.config.ConfigHandler;
import org.wyldmods.simpleachievements.common.networking.MessageSendAchievements;
import org.wyldmods.simpleachievements.common.networking.PacketHandlerSA;

import tterrag.core.common.Handlers.Handler;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

@Handler
public class DataManager
{
	private static boolean noSave = false;

	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load load)
	{
		if (!load.world.isRemote)
			load();
	}

	@SubscribeEvent
	public void onWorldSave(WorldEvent.Save save)
	{
		if (!save.world.isRemote)
			save();
	}
	
    @SubscribeEvent
    public void onPlayerLogin(PlayerLoggedInEvent event)
    {
        EntityPlayer player = event.player;
        if (player != null && !player.worldObj.isRemote)
        {
            SimpleAchievements.logger.info("Sending " + player.getCommandSenderName() + " achievement list.");
            INSTANCE.checkMap(player.getCommandSenderName());
            PacketHandlerSA.INSTANCE.sendTo(new MessageSendAchievements(this.getHandlerFor(player.getCommandSenderName()).getAchievementList()), (EntityPlayerMP) player);
        }
	}

	public static final DataManager INSTANCE = new DataManager();

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
	public void initFormatting()
	{
        String s = "";
        try
        {
            Scanner scan = new Scanner(SimpleAchievements.divConfig);
            while (scan.hasNextLine())
            {
                s += scan.nextLine() + "\n";
            }
            scan.close();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

		formats = gson.fromJson(s, new TypeToken<Map<Integer, Formatting>>() {}.getType());
		if (formats == null)
		{
			formats = new HashMap<Integer, Formatting>();
			formats.put(0, new Formatting());
		}
	}

	@SuppressWarnings("serial")
	public void initSpecialUsers() throws IOException
	{
		String s = "";
		Scanner scan = new Scanner(SimpleAchievements.usersConfig);
		while (scan.hasNextLine())
		{
			s += scan.nextLine() + "\n";
		}

		specialUsers = gson.fromJson(s, new TypeToken<Map<String, Offset>>() {
		}.getType());
		if (specialUsers == null)
		{
			specialUsers = new HashMap<String, Offset>();
		}
		scan.close();
	}

	public void load()
	{
		noSave = false;

		saveDir = new File(DimensionManager.getCurrentSaveRootDirectory().getAbsolutePath() + "/" + SimpleAchievements.MODID);
		saveDir.mkdirs();
		saveFile = new File(saveDir.getAbsolutePath() + "/" + "achievements.json");


		try
		{
	        initSpecialUsers();

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
		if (!noSave)
		{
			saveMap(saveFile, this, this.map);
		}
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

		Map<String, DataHandler> ret = gson.fromJson(json, new TypeToken<Map<String, DataHandler>>() {
		}.getType());
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
			SimpleAchievements.logger.severe("Could not save achievements file!");
		}
	}

	public void changeMap(EntityPlayer player, DataHandler handler)
	{
		map.put(player.getCommandSenderName(), handler);
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
		noSave = true;

		this.map.clear();
		this.formats.clear();
		this.specialUsers.clear();

		saveFile.delete();
		
		ConfigHandler.flush();
		
		load();
	}
}
