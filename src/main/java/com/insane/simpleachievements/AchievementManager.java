package com.insane.simpleachievements;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.insane.simpleachievements.AchievementHandler;
import com.insane.simpleachievements.SimpleAchievements;

public class AchievementManager
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

	private static AchievementManager instance;

	public static AchievementManager instance()
	{
		return instance == null ? instance = new AchievementManager() : instance;
	}

	private Map<String, AchievementHandler> map;
	private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private File saveDir, saveFile;

	private AchievementManager()
	{
		map = new HashMap<String, AchievementHandler>();
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

	public AchievementHandler getAchievementsFor(String username)
	{
		checkMap(username);

		return map.get(username);
	}

	private void checkMap(String username)
	{
		if (!map.containsKey(username))
		{
			map.put(username, new AchievementHandler(username));
		}
	}

	@SuppressWarnings("serial")
	private static Map<String, AchievementHandler> loadMap(File file) throws FileNotFoundException
	{
		String json = "";

		Scanner scan = new Scanner(file);
		while (scan.hasNextLine())
		{
			json += scan.nextLine() + "\n";
		}
		scan.close();

		Map<String, AchievementHandler> ret = gson.fromJson(json, new TypeToken<Map<String, AchievementHandler>>() {}.getType());
		return ret == null ? new HashMap<String, AchievementHandler>() : ret;
	}

	private static void saveMap(File file, AchievementManager instance)
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

    protected void changeMap(EntityPlayer player, AchievementHandler handler) {
        map.put(player.username, handler);
    }


    public void sendStructureToPlayer(EntityPlayer player) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
        DataOutputStream outputStream = new DataOutputStream(bos);
        try {
            AchievementHandler chievs = map.get(player.username);
            for (int i = 0; i < chievs.numAchievements(); i++) {
                outputStream.writeUTF(chievs.getAchievementText(i));
                outputStream.writeBoolean(chievs.getAchievementState(i));
            }
        } catch (IOException error) {
            error.printStackTrace();
        }

        Packet250CustomPayload packet = new Packet250CustomPayload();
        packet.channel = SimpleAchievements.CHANNEL;
        packet.data = bos.toByteArray();
        packet.length = packet.data.length;

        PacketDispatcher.sendPacketToPlayer(packet, (Player)player);
    }
}
