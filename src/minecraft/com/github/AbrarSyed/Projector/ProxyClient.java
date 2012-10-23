package com.github.AbrarSyed.Projector;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;
import net.minecraftforge.client.MinecraftForgeClient;

@SideOnly(value=Side.CLIENT)
public class ProxyClient extends ProxyCommon
{
	@Override
	public void loadTextureStuff()
	{
		// texture registers
		MinecraftForgeClient.preloadTexture("/projections/terrain.png");
		MinecraftForgeClient.preloadTexture("/projector/images.png");
		ProjectorMod.renderID = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(new ProjectionRenderrer());
	}
	
	@Override
	public Object getGui(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		try
		{
			switch(ID)
			{
			case 0: return new GuiProjectorLoad(player.inventory, (TileEntityProjector) world.getBlockTileEntity(x, y, z), FMLClientHandler.instance().getClient().currentScreen);
			case 1: return new GuiProjectorControl((TileEntityProjector) world.getBlockTileEntity(x, y, z));
			case 2: return new GuiRemoteControl(world, player.getCurrentEquippedItem());
			case 3: return new GuiSchematicChooser(player.getCurrentEquippedItem(), FMLClientHandler.instance().getClient().currentScreen, ProjectorAPI.getSchematicFileSystem());
			}
		}
		catch(Exception e)
		{
			return null;
		}

		return null;
	}
}
