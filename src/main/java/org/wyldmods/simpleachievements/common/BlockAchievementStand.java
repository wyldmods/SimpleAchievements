package org.wyldmods.simpleachievements.common;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

import org.wyldmods.simpleachievements.SimpleAchievements;
import org.wyldmods.simpleachievements.client.gui.GuiHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockAchievementStand extends Block implements ITileEntityProvider
{
	@SideOnly(Side.CLIENT)
	private Icon bottom, top;

	public BlockAchievementStand(int par1)
	{
		super(par1, Material.wood);
		this.setUnlocalizedName("sa.achievementTable");
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
		setHardness(1.5f);
		setStepSound(soundWoodFootstep);
		setCreativeTab(CreativeTabs.tabMisc);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9)
	{
		int meta = world.getBlockMetadata(x, y, z);
		TileEntityAchievementStand stand = (TileEntityAchievementStand) world.getBlockTileEntity(x, y, z);
		if (meta == 0)
		{
			if (player.isSneaking())
			{
				ItemStack bookToAdd = new ItemStack(SimpleAchievements.achievementBook);
				NBTTagCompound bookTag = NBTUtils.getTag(bookToAdd);
                bookTag.setInteger("sa:page", stand.page);
                bookToAdd.setTagCompound(bookTag);
                System.out.println(bookTag.getInteger("sa:page"));
				player.inventory.addItemStackToInventory(bookToAdd);
				world.setBlockMetadataWithNotify(x, y, z, 1, 3);
			}
			else
			{
				if (world.isRemote)
				{
					GuiHelper.openSAGUI(world, player, x, y, z);
				}
			}
			return true;
		}
		else if (meta == 1)
		{
			ItemStack stack = player.getCurrentEquippedItem();
			if (stack != null && stack.getItem() == SimpleAchievements.achievementBook)
			{
				TileEntityAchievementStand currTable = (TileEntityAchievementStand) world.getBlockTileEntity(x, y, z);
				NBTTagCompound bookTag = NBTUtils.getTag(stack);
                currTable.page = bookTag.getInteger("sa:page");
				player.inventory.decrStackSize(player.inventory.currentItem, 1);
				world.setBlockMetadataWithNotify(x, y, z, 0, 3);
				return true;
			}
		}
		return false;
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

	@Override
	public int damageDropped(int par1)
	{
		return par1;
	}

	@Override
	public int getDamageValue(World par1World, int par2, int par3, int par4)
	{
		return par1World.getBlockMetadata(par2, par3, par4);
	}
}
