package com.github.AbrarSyed.Projector;

import net.minecraft.src.*;

import org.lwjgl.opengl.GL11;

public class GuiProjectorLoad extends GuiContainer
{
	TileEntityProjector entity;
	GuiScreen screen;

	public GuiProjectorLoad(IInventory player, TileEntityProjector entity, GuiScreen prevScreen)
	{
		super(new ContainerProjector(player, entity));
		
		this.entity = entity;
		this.screen = prevScreen;
	}
	
    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    public boolean doesGuiPauseGame()
    {
        return false;
    }
	
   @Override
   protected void mouseClicked(int par1, int par2, int par3)
   {
       super.mouseClicked(par1, par2, par3);
       
       int var4 = (this.width - this.xSize) / 2;
       int var5 = (this.height - this.ySize) / 2;
       
       int var7 = par1 - (var4 + 90);
       int var8 = par2 - (var5 + 14 + 23);

       if (var7 >= 0 && var8 >= 0 && var7 < 60 && var8 < 19)
       {
    	   mc.displayGuiScreen(screen);
       }
   }
	
	@Override
    protected void drawGuiContainerForegroundLayer()
    {
        this.fontRenderer.drawString("Schematic", 8, 8, 0);
        this.fontRenderer.drawString("BACK", 105, 42, 0);
        this.fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 94, 4210752);
    }
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3)
	{
        int var4 = this.mc.renderEngine.getTexture("/projector/projectorLoader.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(var4);
        int var5 = this.guiLeft;
        int var6 = (this.height - this.ySize) / 2;
        
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize);
	}
}
