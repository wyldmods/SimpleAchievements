package com.insane.simpleachievements.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;

/**
 * Created by Michael on 29/07/2014.
 */
public class Gui extends GuiScreen {
    public static final int GUI_ID = 20;

    private int offset;
    private final int maxOffset=100;
    private boolean closing = false;

    private GuiTextField textField;

    public Gui() {
        super();

        this.mc= Minecraft.getMinecraft();
        offset = maxOffset;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui() {
        int x = this.width/2 - 70;
        int y = this.height - 50 + offset;

        textField = new GuiTextField(mc.fontRenderer, this.width/2 - 70, this.height-30, 150, 20);
        textField.setText("Testing");
        textField.setVisible(true);
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        this.drawRect(5,5, this.width-5, this.height-5,16777215);
        textField.drawTextBox();
        textField.setVisible(true);
    }
}
