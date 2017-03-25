package org.wyldmods.simpleachievements.common.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import org.wyldmods.simpleachievements.SimpleAchievements;
import org.wyldmods.simpleachievements.client.gui.Offset;
import org.wyldmods.simpleachievements.common.config.ConfigHandler;
import org.wyldmods.simpleachievements.common.networking.MessageSendAchievements;
import org.wyldmods.simpleachievements.common.networking.PacketHandlerSA;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class DataManager
{
    private static class Reader implements JsonSerializer<Map<String, DataHandler>>, JsonDeserializer<Map<String, DataHandler>>
    {
        @Override
        public JsonElement serialize(Map<String, DataHandler> src, Type typeOfSrc, JsonSerializationContext context)
        {
            JsonObject root = new JsonObject();
            for (Entry<String, DataHandler> e : src.entrySet())
            {
                JsonArray arr = new JsonArray();
                for (Element ele : e.getValue().getAchievementArr())
                {
                    JsonObject val = new JsonObject();
                    val.addProperty(ele.text, ele.state);
                    arr.add(val);
                }
                root.add(e.getKey(), arr);
            }
            return root;
        }

        @Override
        public Map<String, DataHandler> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
        {
            JsonObject root = json.getAsJsonObject();
            Map<String, DataHandler> ret = new LinkedHashMap<String, DataHandler>();
            for (Entry<String, JsonElement> e : root.entrySet())
            {
                JsonArray arr = e.getValue().getAsJsonArray();
                List<Element> elements = Lists.newArrayList();
                for (int i = 0; i < arr.size(); i++)
                {
                    JsonObject val = arr.get(i).getAsJsonObject();
                    Entry<String, JsonElement> prop = val.entrySet().iterator().next();
                    Element def = new Element(ConfigHandler.idMap.get(prop.getKey()));
                    if (def.text != null) // Missing from defaults, so ignore it
                    {
                        def.setState(prop.getValue().getAsBoolean());
                        elements.add(def);
                    }
                }
                ret.put(e.getKey(), new DataHandler(elements));
            }
            return ret;
        }
    }


    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load load)
    {
        if (!load.getWorld().isRemote && load.getWorld().provider.getDimension() == 0)
            load();
    }

    @SubscribeEvent
    public void onWorldSave(WorldEvent.Save save)
    {
        if (!save.getWorld().isRemote && save.getWorld().provider.getDimension() == 0)
            save();
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerLoggedInEvent event)
    {
        EntityPlayer player = event.player;
        if (player != null && !player.worldObj.isRemote)
        {
            SimpleAchievements.logger.info("Sending " + player.getName() + " achievement list.");
            INSTANCE.checkMap(player.getName());
            PacketHandlerSA.INSTANCE.sendTo(new MessageSendAchievements(this.getHandlerFor(player.getName()).getAchievementList()),
                    (EntityPlayerMP) player);
        }
    }
    private static boolean noSave = false;
    private boolean isDirty = false;
    
    public static final DataManager INSTANCE = new DataManager();

    private Map<String, DataHandler> map;
    private Map<Integer, Formatting> formats;
    private Map<String, Offset> specialUsers;
    private static Gson dataReader = new GsonBuilder().registerTypeAdapter(Map.class, new Reader()).create();
    private File saveDir, saveFile;

    private DataManager()
    {
        map = new LinkedHashMap<String, DataHandler>();
        formats = new LinkedHashMap<Integer, Formatting>();
        specialUsers = new LinkedHashMap<String, Offset>();
        MinecraftForge.EVENT_BUS.register(this);
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

        formats = new Gson().fromJson(s, new TypeToken<Map<Integer, Formatting>>(){}.getType());
        if (formats == null)
        {
            formats = new LinkedHashMap<Integer, Formatting>();
            formats.put(0, new Formatting());
        }
    }

    @SuppressWarnings("serial")
    public void initSpecialUsers() throws IOException
    {
        specialUsers = new Gson().fromJson(
                new InputStreamReader(SimpleAchievements.class.getResourceAsStream("/assets/simpleachievements/misc/specialUsers.json")),
                new TypeToken<Map<String, Offset>>(){}.getType());
        if (specialUsers == null)
        {
            specialUsers = new LinkedHashMap<String, Offset>();
        }
    }

    public void load()
    {
        noSave = false;
        isDirty = false;

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
        if (!noSave && isDirty)
        {
            saveMap(saveFile, this, this.map);
            isDirty = false;
        }
    }
    
    public void markDirty()
    {
    	isDirty = true;
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

        Map<String, DataHandler> ret = dataReader.fromJson(json, new TypeToken<Map<String, DataHandler>>(){}.getType());
        return ret == null ? new LinkedHashMap<String, DataHandler>() : ret;
    }

    @SuppressWarnings("serial")
    private static void saveMap(File file, DataManager instance, Map<String, DataHandler> map)
    {
        String json = dataReader.toJson(map, new TypeToken<Map<String, DataHandler>>(){}.getType());

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
        map.put(player.getName(), handler);
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
        isDirty = false;

        this.map.clear();
        this.formats.clear();
        this.specialUsers.clear();

        saveFile.delete();

        ConfigHandler.flush();

        load();
    }
}
