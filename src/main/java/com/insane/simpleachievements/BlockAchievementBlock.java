package com.insane.simpleachievements;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

import com.insane.simpleachievements.client.gui.GuiSA;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockAchievementBlock extends Block implements ITileEntityProvider{

	@SideOnly(Side.CLIENT)
	private Icon bottom, top;
	
    public BlockAchievementBlock(int par1) {
        super(par1, Material.wood);
        this.setUnlocalizedName("sa.achievementTable");
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
    }

    @Override
    public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer player, int par6, float par7, float par8, float par9) {
        player.openGui(SimpleAchievements.instance, GuiSA.GUI_ID, par1World, par2, par3, par4);
        return true;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister register)
    {
    	this.blockIcon = register.registerIcon(SimpleAchievements.MODID + ":" + "stand_side");
    	this.bottom = register.registerIcon(SimpleAchievements.MODID + ":" + "stand_bottom");
    	this.top = register.registerIcon(SimpleAchievements.MODID + ":" + "stand_top");
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public Icon getIcon(int side, int meta)
    {
    	return side > 1 ? this.blockIcon : side == 1 ? top : bottom;
    }
    
    @Override
    public boolean isOpaqueCube()
    {
    	return false;
    }
    
    @Override
    public boolean renderAsNormalBlock()
    {
    	return false;
    }

	@Override
	public TileEntity createNewTileEntity(World world)
	{
		return new TileEntityAchievementStand();
	}
	
	public static class TileEntityAchievementStand extends TileEntityEnchantmentTable
	{
		;
	}
}
