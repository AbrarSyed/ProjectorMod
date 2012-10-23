package com.github.AbrarSyed.Projector;

import org.lwjgl.opengl.GL11;

import net.minecraft.src.Block;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.RenderBlocks;
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
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(48/255F, 161/255F, 207/255F, 0.1F);
		renderer.renderStandardBlock(block, x, y, z);
		return true;
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
