package com.insane.simpleachievements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import com.insane.simpleachievements.common.BlockAchievementStand;
import com.insane.simpleachievements.common.BlockAchievementStand.TileEntityAchievementStand;
import com.insane.simpleachievements.common.CommonProxy;
import com.insane.simpleachievements.common.ItemAchievementBook;
import com.insane.simpleachievements.common.ItemBlockAchievementStand;
import com.insane.simpleachievements.common.PlayerTracker;
import com.insane.simpleachievements.common.config.ConfigHandler;
import com.insane.simpleachievements.common.data.DataHandler;
import com.insane.simpleachievements.common.data.DataManager;
import com.insane.simpleachievements.common.data.Element;
import com.insane.simpleachievements.common.networking.PacketHandlerSA;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
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

	@SidedProxy(clientSide = "com.insane.simpleachievements.client.ClientProxy", serverSide = "com.insane.simpleachievements.CommonProxy")
	public static CommonProxy proxy;

	public static File configDir;
	public static File achievementConfig;
	public static File divConfig;

	public static Block achievementStand;
	public static Block decorationBlock;
	
	public static Item achievementBook;

	public static int bookWidth = 417;
	public static int bookHeight = 245;

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
		
		decorationBlock = new Block(ConfigHandler.decorationID, Material.wood).setStepSound(Block.soundWoodFootstep).setHardness(1.5f).setTextureName(MODID.toLowerCase() + ":stand_top").setUnlocalizedName("sa.decorativeWood");
		GameRegistry.registerBlock(decorationBlock, "sa.decorationBlock");
		
		achievementBook = new ItemAchievementBook(ConfigHandler.bookID);
		GameRegistry.registerItem(achievementBook, "sa.achievementBook");
		
		System.out.println(Item.itemsList[achievementStand.blockID]);
		
		ItemStack purpleDye = new ItemStack(Item.dyePowder, 1, 5);
		
		GameRegistry.addRecipe(new ItemStack(achievementStand),
			"dbd",
			"www",
			"www",

			'd', purpleDye,
			'b', Item.book,
			'w', Block.planks
		);
		
		GameRegistry.addRecipe(new ItemStack(achievementStand),
			" b ",
			"www",
			"www",

			'b', achievementBook,
			'w', Block.planks
		);
		
		GameRegistry.addRecipe(new ItemStack(achievementBook), 
			"dbd",
			
			'd', purpleDye,
			'b', Item.book
		);
		
		GameRegistry.addRecipe(new ItemStack(decorationBlock, 5),
			"www",
			"wlw",
			"www",
			
			'w', Block.planks,
			'l', Item.leather
		);

		proxy.registerRenderers();
	}

	private void create(File... files) throws IOException
	{
		for (File file : files)
		{
			if (!file.exists())
			{
				file.getParentFile().mkdirs();
				file.createNewFile();
				System.out.println(file.getAbsolutePath());
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

	public static String getPersistentTagName()
	{
		return SimpleAchievements.MODID + "achievements";
	}
}
