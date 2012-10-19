package com.github.AbrarSyed.Projector;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler
{

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		try
		{
			switch(ID)
			{
			case 0: 
				TileEntityProjector entity = (TileEntityProjector) world.getBlockTileEntity(x, y, z);
				return new ContainerProjector(player.inventory, entity);
			// case 1: null;   projector control
			// case 2: null;   remote control
			// case 3: null;   schematic choosing
			}
		}
		catch(Exception e)
		{
			return null;
		}
		
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		return ProjectorMod.proxy.getGui(ID, player, world, x, y, z);
	}

}
