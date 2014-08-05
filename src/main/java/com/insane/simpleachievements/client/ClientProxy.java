package com.insane.simpleachievements.client;

import com.insane.simpleachievements.client.render.RenderAchievementBook;
import com.insane.simpleachievements.common.CommonProxy;
import com.insane.simpleachievements.common.BlockAchievementStand.TileEntityAchievementStand;

import cpw.mods.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy
{
	public void registerRenderers()
	{
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAchievementStand.class, new RenderAchievementBook());
	}
}
