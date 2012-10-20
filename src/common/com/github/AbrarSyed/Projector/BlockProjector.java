package com.github.AbrarSyed.Projector;

import net.minecraft.src.*;

import java.io.File;
import java.util.ArrayList;

import cpw.mods.fml.common.FMLCommonHandler;

public class BlockProjector extends BlockContainer
{
	public BlockProjector(int id)
	{
		super(id, 2, Material.wood);
		this.setHardness(1.5F);
		this.setCreativeTab(CreativeTabs.tabBlock);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world)
	{
		return new TileEntityProjector();
	}
	
	
	@Override
    public void addCreativeItems(ArrayList itemList)
    {
		itemList.add(new ItemStack(this));
    }
	
	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer player,  int par6, float par7, float par8, float par9)
	{
		player.openGui(ProjectorMod.instance, 1, world, i, j, k);
		return true;
	}
	
	@Override
    public void breakBlock(World world, int par2, int par3, int par4, int something1, int something2)
	{
		TileEntityProjector entity = (TileEntityProjector) world.getBlockTileEntity(par2, par3, par4);
		
		if (entity != null)
		{
			// end projection
			entity.endProjectionInstantly();
			
			if (entity.getLoadedSchematic() != null)
			{
				// drops the loaded Schematic
				EntityItem dropped = new EntityItem(world, par2, par3, par4, entity.getLoadedSchematic());
				dropped.setVelocity(0, .01F, 0);
				world.spawnEntityInWorld(dropped);
			}
		}
		
		// does whatever else MC does
		super.breakBlock(world, par2, par3, par4, something1, something2);
	}

}
