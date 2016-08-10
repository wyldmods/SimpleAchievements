package org.wyldmods.simpleachievements.client;

import org.wyldmods.simpleachievements.SimpleAchievements;
import org.wyldmods.simpleachievements.client.render.RenderAchievementBook;
import org.wyldmods.simpleachievements.common.BlockAchievementStand;
import org.wyldmods.simpleachievements.common.CommonProxy;
import org.wyldmods.simpleachievements.common.TileEntityAchievementStand;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy
{
	@Override
	public void registerRenderers()
	{
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAchievementStand.class, new RenderAchievementBook());
		
		ModelLoader.setCustomStateMapper(SimpleAchievements.achievementStand, new StateMap.Builder().ignore(BlockAchievementStand.HAS_BOOK).build());
		
		ModelResourceLocation standNormal = new ModelResourceLocation(SimpleAchievements.achievementStand.getRegistryName(), "normal");
		Item standItem = Item.getItemFromBlock(SimpleAchievements.achievementStand);
		ModelLoader.setCustomModelResourceLocation(standItem, 0, standNormal);
		ModelLoader.setCustomModelResourceLocation(standItem, 1, standNormal);
		
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SimpleAchievements.decorationBlock), 0, new ModelResourceLocation(SimpleAchievements.decorationBlock.getRegistryName(), "normal"));
		
        ModelLoader.setCustomModelResourceLocation(SimpleAchievements.achievementBook, 0, new ModelResourceLocation(SimpleAchievements.achievementBook.getRegistryName(), "inventory"));
	}
	
	@Override
	public EntityPlayer getClientPlayer()
	{
	    return Minecraft.getMinecraft().thePlayer;
	}
}
