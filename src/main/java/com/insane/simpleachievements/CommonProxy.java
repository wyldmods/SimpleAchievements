package com.insane.simpleachievements;

import com.insane.simpleachievements.client.gui.GuiSA;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * Created by Michael on 28/07/2014.
 */
public class CommonProxy implements IGuiHandler {
    public void registerRenderers() {}

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == GuiSA.GUI_ID) {
            return new GuiSA(player);
        }
        return null;
    }
}
