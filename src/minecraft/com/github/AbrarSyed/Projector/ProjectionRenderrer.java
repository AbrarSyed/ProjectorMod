package com.github.AbrarSyed.Projector;

import org.lwjgl.opengl.GL11;

import net.minecraft.src.Block;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.Tessellator;
import net.minecraft.src.TileEntity;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class ProjectionRenderrer implements ISimpleBlockRenderingHandler {

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer)
	{
		// do nothing
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
	{
		/*
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(48/255F, 161/255F, 207/255F, 0.1F);
		*/
		
		TileEntity entityTest = world.getBlockTileEntity(x, y, z);
		if (entityTest == null || !(entityTest instanceof TileEntityProjection))
			return false;
		
		TileEntityProjection entity = (TileEntityProjection)entityTest;
		
		int heldID = entity.getHeldID();
		
		if (heldID == 0)
			return false;
		
		Block newBlock = Block.blocksList[heldID];
		
		renderer.renderBlockAllFaces(newBlock, x, y, z);
		
		return true;
		//return renderCubeHologram(renderer, world, newBlock, x, y, z);
	}
	
	private boolean renderCubeHologram(RenderBlocks renderer, IBlockAccess world, Block newBlock, int x, int y, int z)
    {
        Tessellator var8 = Tessellator.instance;
        boolean var9 = false;
        
        float c_red = 48/255F;
        float c_green = 161/255F;
        float c_blue = 207/255F;
        float c_alpha = 100/255;
        
        float var10 = 0.5F;
        float var11 = 1.0F;
        float var12 = 0.8F;
        float var13 = 0.6F;
        float var14 = var11 * c_red;
        float var15 = var11 * c_green;
        float var16 = var11 * c_blue;
        float var17 = var10;
        float var18 = var12;
        float var19 = var13;
        float var20 = var10;
        float var21 = var12;
        float var22 = var13;
        float var23 = var10;
        float var24 = var12;
        float var25 = var13;

        if (newBlock != Block.grass)
        {
            var17 = var10 * c_red;
            var18 = var12 * c_red;
            var19 = var13 * c_red;
            var20 = var10 * c_green;
            var21 = var12 * c_green;
            var22 = var13 * c_green;
            var23 = var10 * c_blue;
            var24 = var12 * c_blue;
            var25 = var13 * c_blue;
        }

        int var26 = 0; //newBlock.getMixedBrightnessForBlock(world, x, y, y);

        if (newBlock.shouldSideBeRendered(world, x, y - 1, y, 0))
        {
            var8.setBrightness(newBlock.minY > 0.0D ? var26 : newBlock.getMixedBrightnessForBlock(world, x, y - 1, y));
            var8.setColorRGBA_F(var17, var20, var23, c_alpha);
            renderer.renderBottomFace(newBlock, (double)x, (double)y, (double)y, newBlock.getBlockTexture(world, x, y, y, 0));
            var9 = true;
        }

        if (newBlock.shouldSideBeRendered(world, x, y + 1, y, 1))
        {
            var8.setBrightness(newBlock.maxY < 1.0D ? var26 : newBlock.getMixedBrightnessForBlock(world, x, y + 1, y));
            var8.setColorRGBA_F(var14, var15, var16, c_alpha);
            renderer.renderTopFace(newBlock, (double)x, (double)y, (double)y, newBlock.getBlockTexture(world, x, y, y, 1));
            var9 = true;
        }

        int var28;

        if (newBlock.shouldSideBeRendered(world, x, y, y - 1, 2))
        {
            var8.setBrightness(newBlock.minZ > 0.0D ? var26 : newBlock.getMixedBrightnessForBlock(world, x, y, y - 1));
            var8.setColorRGBA_F(var18, var21, var24, c_alpha);
            var28 = newBlock.getBlockTexture(world, x, y, y, 2);
            renderer.renderEastFace(newBlock, (double)x, (double)y, (double)y, var28);

            if (Tessellator.instance.defaultTexture && var28 == 3 && renderer.overrideBlockTexture < 0)
            {
                var8.setColorRGBA_F(var18 * c_red, var21 * c_green, var24 * c_blue, c_alpha);
                renderer.renderEastFace(newBlock, (double)x, (double)y, (double)y, 38);
            }

            var9 = true;
        }

        if (newBlock.shouldSideBeRendered(world, x, y, y + 1, 3))
        {
            var8.setBrightness(newBlock.maxZ < 1.0D ? var26 : newBlock.getMixedBrightnessForBlock(world, x, y, y + 1));
            var8.setColorRGBA_F(var18, var21, var24, c_alpha);
            var28 = newBlock.getBlockTexture(world, x, y, y, 3);
            renderer.renderWestFace(newBlock, (double)x, (double)y, (double)y, var28);

            if (Tessellator.instance.defaultTexture && var28 == 3 && renderer.overrideBlockTexture < 0)
            {
                var8.setColorRGBA_F(var18 * c_red, var21 * c_green, var24 * c_blue, c_alpha);
                renderer.renderWestFace(newBlock, (double)x, (double)y, (double)y, 38);
            }

            var9 = true;
        }

        if (newBlock.shouldSideBeRendered(world, x - 1, y, y, 4))
        {
            var8.setBrightness(newBlock.minX > 0.0D ? var26 : newBlock.getMixedBrightnessForBlock(world, x - 1, y, y));
            var8.setColorRGBA_F(var19, var22, var25, c_alpha);
            var28 = newBlock.getBlockTexture(world, x, y, y, 4);
            renderer.renderNorthFace(newBlock, (double)x, (double)y, (double)y, var28);

            if (Tessellator.instance.defaultTexture && var28 == 3 && renderer.overrideBlockTexture < 0)
            {
                var8.setColorRGBA_F(var19 * c_red, var22 * c_green, var25 * c_blue, c_alpha);
                renderer.renderNorthFace(newBlock, (double)x, (double)y, (double)y, 38);
            }

            var9 = true;
        }

        if (newBlock.shouldSideBeRendered(world, x + 1, y, y, 5))
        {
            var8.setBrightness(newBlock.maxX < 1.0D ? var26 : newBlock.getMixedBrightnessForBlock(world, x + 1, y, y));
            var8.setColorRGBA_F(var19, var22, var25, c_alpha);
            var28 = newBlock.getBlockTexture(world, x, y, y, 5);
            renderer.renderSouthFace(newBlock, (double)x, (double)y, (double)y, var28);

            if (Tessellator.instance.defaultTexture && var28 == 3 && renderer.overrideBlockTexture < 0)
            {
                var8.setColorRGBA_F(var19 * c_red, var22 * c_green, var25 * c_blue, c_alpha);
                renderer.renderSouthFace(newBlock, (double)x, (double)y, (double)y, 38);
            }

            var9 = true;
        }

        return var9;
    }

	@Override
	public boolean shouldRender3DInInventory()
	{
		return false;
	}

	@Override
	public int getRenderId()
	{
		return ProjectorMod.renderID;
	}

}
