package org.wyldmods.simpleachievements.common;

import javax.annotation.Nonnull;

import org.wyldmods.simpleachievements.SimpleAchievements;
import org.wyldmods.simpleachievements.client.gui.GuiHelper;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockAchievementStand extends Block implements ITileEntityProvider
{
    private static final @Nonnull AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
    
    public static final @Nonnull PropertyBool HAS_BOOK = PropertyBool.create("has_book");
    
	public BlockAchievementStand()
	{
		super(Material.WOOD);
		this.setUnlocalizedName("sa.achievementTable");
		this.setRegistryName("achievement_stand");
		setHardness(1.5f);
		setSoundType(SoundType.WOOD);
		setCreativeTab(CreativeTabs.MISC);
		setDefaultState(getDefaultState().withProperty(HAS_BOOK, true));
	}

	@Override
	protected BlockStateContainer createBlockState() {
	    return new BlockStateContainer(this, HAS_BOOK);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
	    return state.getValue(HAS_BOOK) ? 0 : 1;
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
	    return getDefaultState().withProperty(HAS_BOOK, meta == 0);
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
	    return BOUNDING_BOX;
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) 
	{
		TileEntityAchievementStand stand = (TileEntityAchievementStand) world.getTileEntity(pos);
		if (stand == null)
		{
		    return false;
		}
		
		if (state.getValue(HAS_BOOK))
		{
			if (player.isSneaking())
			{
				ItemStack bookToAdd = new ItemStack(SimpleAchievements.achievementBook);
				NBTTagCompound bookTag = NBTUtils.getTag(bookToAdd);
				bookTag.setInteger("sa:page", stand.page);
				bookToAdd.setTagCompound(bookTag);
				player.inventory.addItemStackToInventory(bookToAdd);
				world.setBlockState(pos, state.withProperty(HAS_BOOK, false));
			}
			else
			{
				if (world.isRemote)
				{
					GuiHelper.openSAGUI(world, player, pos);
				}
			}
			return true;
		}
		else
		{
			ItemStack stack = player.getHeldItem(hand);
            if (stack != null && stack.getItem() == SimpleAchievements.achievementBook) 
            {
                NBTTagCompound bookTag = NBTUtils.getTag(stack);
                stand.page = bookTag.getInteger("sa:page");
                player.inventory.decrStackSize(player.inventory.currentItem, 1);
                world.setBlockState(pos, state.withProperty(HAS_BOOK, true));
                return true;
            }
        }
		
		return false;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}
	
	@Override
	public boolean isFullCube(IBlockState state) 
	{
	    return false;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata)
	{
		return new TileEntityAchievementStand();
	}

	@Override
	public int damageDropped(IBlockState state) 
	{      
	    return getMetaFromState(state);
	}
}
