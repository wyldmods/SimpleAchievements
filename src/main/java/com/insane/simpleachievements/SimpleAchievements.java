package com.insane.simpleachievements;

/**
 * Created by Michael on 28/07/2014.
 */

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

@Mod(modid=SimpleAchievements.MODID, name="Simple Achievements", version="1.0")
@NetworkMod(clientSideRequired=true)
public class SimpleAchievements {

    public static ArrayList<String> achievements = null;

    public static final String MODID = "SimpleAchievements";

    @Mod.Instance("SimpleAchievements")
    public static SimpleAchievements instance;
    @SidedProxy(clientSide="com.insane.simpleachievements.client.ClientProxy", serverSide="com.insane.simpleachievements.CommonProxy")
    public static CommonProxy proxy;

    public static File achievementList;

    public static Block achievementBlock;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        GameRegistry.registerPlayerTracker(new PlayerTracker());

        achievementList = new File(event.getSuggestedConfigurationFile().getParentFile().getAbsolutePath()+"/"+MODID+"/achievementList.txt");
        create(achievementList);


    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        NetworkRegistry.instance().registerGuiHandler(instance, proxy);

        achievementBlock = new BlockAchievementBlock(500);

        GameRegistry.registerBlock(achievementBlock,MODID+"achievementBlock");
    }

    public static ArrayList<String> readInAchievements() {
        try {
            Scanner scan = new Scanner(achievementList);
            ArrayList<String> list = new ArrayList<String>();
            while (scan.hasNextLine()) {
                String t = scan.nextLine();
                list.add(t);
            }

            return list;

        } catch (IOException error) {
            System.out.print("Something is derped with Achievement I/O. Reason: ");
            System.out.println(error);
            return null;
        }
    }


    private void create(File file) {
        if (!file.exists()) {
            try  {
                file.getParentFile().mkdirs();
                file.createNewFile();
                System.out.println(file.getAbsolutePath());
            } catch (IOException e)  {
                System.out.print("Could not create " + file.getAbsolutePath() + ". Reason: ");
                System.out.println(e);
            }
        }
    }

    public static int toHex(int r, int g, int b)
    {
        int hex = 0;
        hex = hex | ((r) << 16);
        hex = hex | ((g) << 8);
        hex = hex | ((b));
        return hex;
    }
}
