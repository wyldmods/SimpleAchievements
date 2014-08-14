package org.wyldmods.simpleachievements;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Logger;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;

import org.apache.commons.io.FileUtils;
import org.wyldmods.simpleachievements.common.BlockAchievementStand;
import org.wyldmods.simpleachievements.common.CommonProxy;
import org.wyldmods.simpleachievements.common.ItemAchievementBook;
import org.wyldmods.simpleachievements.common.ItemBlockAchievementStand;
import org.wyldmods.simpleachievements.common.PlayerTracker;
import org.wyldmods.simpleachievements.common.TileEntityAchievementStand;
import org.wyldmods.simpleachievements.common.config.ConfigHandler;
import org.wyldmods.simpleachievements.common.data.CommandFlush;
import org.wyldmods.simpleachievements.common.data.DataHandler;
import org.wyldmods.simpleachievements.common.data.DataManager;
import org.wyldmods.simpleachievements.common.data.Element;
import org.wyldmods.simpleachievements.common.networking.PacketHandlerSA;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = SimpleAchievements.MODID, name = "Simple Achievements", version = "1.0")
@NetworkMod(clientSideRequired = true, channels = PacketHandlerSA.CHANNEL, packetHandler = PacketHandlerSA.class)
public class SimpleAchievements
{
	public static ArrayList<String> achievements = null;

	public static final String MODID = "SimpleAchievements";

	@Mod.Instance("SimpleAchievements")
	public static SimpleAchievements instance;

	@SidedProxy(clientSide = "org.wyldmods.simpleachievements.client.ClientProxy", serverSide = "org.wyldmods.simpleachievements.common.CommonProxy")
	public static CommonProxy proxy;

	public static File configDir;
	public static File achievementConfig;
	public static File divConfig;

	public static Block achievementStand;
	public static Block decorationBlock;

	public static Item achievementBook;

	public static int bookWidth = 417;
	public static int bookHeight = 245;

	public static Logger logger = Logger.getLogger("SimpleAchievements");

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		configDir = new File(event.getSuggestedConfigurationFile().getParentFile().getAbsolutePath() + "/" + MODID);
		achievementConfig = new File(configDir.getAbsolutePath() + "/achievementList.txt");
		divConfig = new File(configDir.getAbsolutePath() + "/divConfig.json");

		try
		{
			create(achievementConfig, divConfig);
			DataManager.instance().initFormatting();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}

		ConfigHandler.init(event.getSuggestedConfigurationFile());

		PacketHandlerSA.registerSerializeable(DataHandler.class);
		PacketHandlerSA.registerSerializeable(Element.class);

		GameRegistry.registerPlayerTracker(new PlayerTracker());
		MinecraftForge.EVENT_BUS.register(DataManager.instance());
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event)
	{
		NetworkRegistry.instance().registerGuiHandler(instance, proxy);

		achievementStand = new BlockAchievementStand(ConfigHandler.standID);
		GameRegistry.registerBlock(achievementStand, ItemBlockAchievementStand.class, "sa.achievementStand");
		GameRegistry.registerTileEntity(TileEntityAchievementStand.class, "sa.tileAchievementStand");

		decorationBlock = new Block(ConfigHandler.decorationID, Material.wood).setStepSound(Block.soundWoodFootstep).setHardness(1.5f).setTextureName(MODID.toLowerCase() + ":stand_top")
				.setUnlocalizedName("sa.decorativeWood");
		GameRegistry.registerBlock(decorationBlock, "sa.decorationBlock");

		achievementBook = new ItemAchievementBook(ConfigHandler.bookID);
		GameRegistry.registerItem(achievementBook, "sa.achievementBook");

		ItemStack purpleDye = new ItemStack(Item.dyePowder, 1, 5);

		GameRegistry.addRecipe(new ItemStack(achievementStand), "dbd", "www", "www",

		'd', purpleDye, 'b', Item.book, 'w', Block.planks);

		GameRegistry.addRecipe(new ItemStack(achievementStand), " b ", "www", "www",

		'b', achievementBook, 'w', Block.planks);

		GameRegistry.addRecipe(new ItemStack(achievementBook), "dbd",

		'd', purpleDye, 'b', Item.book);

		GameRegistry.addRecipe(new ItemStack(decorationBlock, 5), "www", "wlw", "www",

		'w', Block.planks, 'l', Item.leather);

		proxy.registerRenderers();
	}

	@Mod.EventHandler
	public void onServerStarting(FMLServerStartingEvent event)
	{
		ICommandManager server = MinecraftServer.getServer().getCommandManager();
		((ServerCommandManager) server).registerCommand(new CommandFlush());
	}

	private void create(File... files) throws IOException
	{
		for (File file : files)
		{
			if (!file.exists())
			{
				file.getParentFile().mkdirs();
				copyFromJar(file);
				logger.info("Successfully loaded default file: " + file.getName());
			}
		}
	}

	private void copyFromJar(File file) throws IOException
	{
		String filename = file.getName();
		URL url = SimpleAchievements.class.getResource("/assets/simpleachievements/misc/" + filename);
		FileUtils.copyURLToFile(url, file);
	}

	public static int toHex(int r, int g, int b)
	{
		int hex = 0;
		hex = hex | ((r) << 16);
		hex = hex | ((g) << 8);
		hex = hex | ((b));
		return hex;
	}

	public static String getPersistentTagName()
	{
		return SimpleAchievements.MODID + "achievements";
	}
}
