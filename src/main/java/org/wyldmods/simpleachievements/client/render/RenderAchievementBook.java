package org.wyldmods.simpleachievements.client.render;

import org.lwjgl.opengl.GL11;
import org.wyldmods.simpleachievements.SimpleAchievements;
import org.wyldmods.simpleachievements.common.TileEntityAchievementStand;

import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RenderAchievementBook extends TileEntitySpecialRenderer<TileEntityAchievementStand>
{
	private static final ResourceLocation enchantingTableBookTextures = new ResourceLocation(SimpleAchievements.MODID.toLowerCase(), "textures/models/book.png");
	private ModelBook enchantmentBook = new ModelBook();

    @Override
    public void renderTileEntityAt(TileEntityAchievementStand te, double x, double y, double z, float partialTicks, int destroyStage) 
	{
		if (te.getBlockMetadata() == 0)
		{
		    GL11.glPushMatrix();
	        GL11.glTranslatef((float)x + 0.5F, (float)y + 0.75F, (float)z + 0.5F);
	        float f1 = (float)te.tickCount + partialTicks;
	        GL11.glTranslatef(0.0F, 0.1F + MathHelper.sin(f1 * 0.1F) * 0.01F, 0.0F);
	        float f2;

	        for (f2 = te.bookRotation - te.bookRotationPrev; f2 >= (float)Math.PI; f2 -= ((float)Math.PI * 2F))
	        {
	            ;
	        }

	        while (f2 < -(float)Math.PI)
	        {
	            f2 += ((float)Math.PI * 2F);
	        }

	        float f3 = te.bookRotationPrev + f2 * partialTicks;
	        GL11.glRotatef(-f3 * 180.0F / (float)Math.PI, 0.0F, 1.0F, 0.0F);
	        GL11.glRotatef(80.0F, 0.0F, 0.0F, 1.0F);
	        this.bindTexture(enchantingTableBookTextures);
	        float f4 = te.pageFlipPrev + (te.pageFlip - te.pageFlipPrev) * partialTicks + 0.25F;
	        float f5 = te.pageFlipPrev + (te.pageFlip - te.pageFlipPrev) * partialTicks + 0.75F;
	        f4 = (f4 - (float)MathHelper.truncateDoubleToInt((double)f4)) * 1.6F - 0.3F;
	        f5 = (f5 - (float)MathHelper.truncateDoubleToInt((double)f5)) * 1.6F - 0.3F;

	        if (f4 < 0.0F)
	        {
	            f4 = 0.0F;
	        }

	        if (f5 < 0.0F)
	        {
	            f5 = 0.0F;
	        }

	        if (f4 > 1.0F)
	        {
	            f4 = 1.0F;
	        }

	        if (f5 > 1.0F)
	        {
	            f5 = 1.0F;
	        }

	        float f6 = te.bookSpreadPrev + (te.bookSpread - te.bookSpreadPrev) * partialTicks;
	        GL11.glEnable(GL11.GL_CULL_FACE);
	        this.enchantmentBook.render((Entity)null, f1, f4, f5, f6, 0.0F, 0.0625F);
	        GL11.glPopMatrix();
		}
	}
}
