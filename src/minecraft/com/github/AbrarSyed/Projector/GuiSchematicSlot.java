package com.github.AbrarSyed.Projector;

import java.io.File;
import java.util.List;

import net.minecraft.src.GuiSlot;
import net.minecraft.src.ModLoader;
import net.minecraft.src.RenderEngine;
import net.minecraft.src.Tessellator;
import net.minecraft.src.TexturePackBase;

import org.lwjgl.opengl.GL11;

class GuiSchematicSlot extends GuiSlot
{
    final GuiSchematicChooser parent;

    public GuiSchematicSlot(GuiSchematicChooser parent)
    {
        super(ModLoader.getMinecraftInstance(), parent.width, parent.height, 32, parent.height - 55 + 4, 36);
        this.parent = parent;
    }

    /**
     * Gets the size of the current slot list.
     */
    @Override
    protected int getSize()
    {
    	return parent.system.getFileList().size()+1;
    }

    /**
     * the element in the slot that was clicked, boolean for wether it was double clicked or not
     */
    @Override
    protected void elementClicked(int par1, boolean par2)
    {
    	parent.setSelectedIndex(par1);
    	
    	if (par2)
    	{
    		parent.clickDone();
    	}
    }

    /**
     * returns true if the element passed in is currently selected
     */
    @Override
    protected boolean isSelected(int par1)
    {
    	return par1 == parent.getSelectedIndex();
    }

    /**
     * return the height of the content being scrolled
     */
    @Override
    protected int getContentHeight()
    {
        return this.getSize() * 36;
    }

    @Override
    protected void drawBackground()
    {
        this.parent.drawDefaultBackground();
    }

    @Override
    protected void drawSlot(int par1, int par2, int par3, int par4, Tessellator par5Tessellator)
    {    	
    	if (par1 == 0)
    		GL11.glBindTexture(GL11.GL_TEXTURE_2D, ModLoader.getMinecraftInstance().renderEngine.getTexture("/gui/unknown_pack.png"));
    	else
        	ModLoader.getMinecraftInstance().texturePackList.getSelectedTexturePack().bindThumbnailTexture((ModLoader.getMinecraftInstance().renderEngine));
    	
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        par5Tessellator.startDrawingQuads();
        par5Tessellator.setColorOpaque_I(16777215);
        par5Tessellator.addVertexWithUV((double)par2, (double)(par3 + par4), 0.0D, 0.0D, 1.0D);
        par5Tessellator.addVertexWithUV((double)(par2 + 32), (double)(par3 + par4), 0.0D, 1.0D, 1.0D);
        par5Tessellator.addVertexWithUV((double)(par2 + 32), (double)par3, 0.0D, 1.0D, 0.0D);
        par5Tessellator.addVertexWithUV((double)par2, (double)par3, 0.0D, 0.0D, 0.0D);
        par5Tessellator.draw();
        
        // draw more packs
        if (par1 == 0)
        	this.parent.drawString(parent.getFontRenderrer(parent), "-- none --", par2 + 32 + 2, par3 + 1, 16777215);
        else
        {
        	File schematic = parent.system.getFileList().get(par1-1);
        	this.parent.drawString(parent.getFontRenderrer(parent), schematic.getName(), par2 + 32 + 2, par3 + 1, 16777215);
        }
    }
}
