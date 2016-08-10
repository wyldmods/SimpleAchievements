package org.wyldmods.simpleachievements;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.wyldmods.simpleachievements.common.BlockAchievementStand;
import org.wyldmods.simpleachievements.common.CommonProxy;
import org.wyldmods.simpleachievements.common.ItemAchievementBook;
import org.wyldmods.simpleachievements.common.ItemBlockAchievementStand;
import org.wyldmods.simpleachievements.common.TileEntityAchievementStand;
import org.wyldmods.simpleachievements.common.config.ConfigHandler;
import org.wyldmods.simpleachievements.common.data.CommandFlush;
import org.wyldmods.simpleachievements.common.data.DataManager;
import org.wyldmods.simpleachievements.common.networking.PacketHandlerSA;

import static org.wyldmods.simpleachievements.SimpleAchievements.*;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = MODID, name = NAME, version = VERSION)
public class SimpleAchievements
{
    public static final String MODID = "SimpleAchievements";
    public static final String NAME = "Simple Achievements";
    public static final String VERSION = "@VERSION@";
    
	public static ArrayList<String> achievements = null;

	@Instance("SimpleAchievements")
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

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		configDir = new File(event.getSuggestedConfigurationFile().getParentFile().getAbsolutePath() + "/" + MODID);
		achievementConfig = new File(configDir.getAbsolutePath() + "/achievementList.txt");
		divConfig = new File(configDir.getAbsolutePath() + "/divConfig.json");

		try
		{
			create(achievementConfig, divConfig);
			DataManager.INSTANCE.initFormatting();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}

		ConfigHandler.init(event.getSuggestedConfigurationFile());
		
		PacketHandlerSA.init();

        achievementStand = new BlockAchievementStand();
        GameRegistry.register(achievementStand);
        GameRegistry.register(new ItemBlockAchievementStand(achievementStand).setRegistryName(achievementStand.getRegistryName()));
        GameRegistry.registerTileEntity(TileEntityAchievementStand.class, "sa.tileAchievementStand");

        decorationBlock = new Block(Material.WOOD){{setSoundType(SoundType.WOOD);}}
            .setCreativeTab(CreativeTabs.DECORATIONS).setHardness(1.5f).setUnlocalizedName("sa.decorativeWood").setRegistryName("decoration_block");
        GameRegistry.register(decorationBlock);
        GameRegistry.register(new ItemBlock(decorationBlock).setRegistryName(decorationBlock.getRegistryName()));

        achievementBook = new ItemAchievementBook();
        GameRegistry.register(achievementBook.setRegistryName("achievement_book"));
		
		proxy.registerRenderers();
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		ItemStack purpleDye = new ItemStack(Items.DYE, 1, 5);

		GameRegistry.addRecipe(new ItemStack(achievementStand), "dbd", "www", "www",

		'd', purpleDye, 'b', Items.BOOK, 'w', Blocks.PLANKS);

		GameRegistry.addRecipe(new ItemStack(achievementStand), " b ", "www", "www",

		'b', achievementBook, 'w', Blocks.PLANKS);

		GameRegistry.addRecipe(new ItemStack(achievementBook), "dbd",

		'd', purpleDye, 'b', Items.BOOK);

		GameRegistry.addRecipe(new ItemStack(decorationBlock, 5), "www", "wlw", "www",

		'w', Blocks.PLANKS, 'l', Items.LEATHER);
	}

	@EventHandler
	public void onServerStarting(FMLServerStartingEvent event)
	{
		ICommandManager server = event.getServer().getCommandManager();
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
