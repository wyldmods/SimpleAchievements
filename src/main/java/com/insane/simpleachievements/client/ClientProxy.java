package com.insane.simpleachievements.client;

import com.insane.simpleachievements.BlockAchievementBlock.TileEntityAchievementStand;
import com.insane.simpleachievements.client.render.RenderAchievementBook;
import com.insane.simpleachievements.CommonProxy;

import cpw.mods.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy
{
	public void registerRenderers()
	{
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAchievementStand.class, new RenderAchievementBook());
	}
}
