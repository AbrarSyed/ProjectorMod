package com.github.AbrarSyed.Projector;

import net.minecraft.src.*;

import java.util.ArrayList;

public class BlockProjection extends BlockContainer
{
	public BlockProjection(int ID)
	{
		super(ID, ProjectorMod.proMaterial);
		this.blockIndexInTexture = 0;
		this.setLightOpacity(0);
		this.setBlockUnbreakable();
		this.setResistance(2000.0F);
		this.setStepSound(new StepSound("null", 0, 0));
	}
	
	public String getReplaceDescription(World world, int x, int y, int z)
	{
		TileEntityProjection entity = (TileEntityProjection) world.getBlockTileEntity(x, y, z);
		int id = entity.getHeldID();
		int meta = entity.getBlockMetadata();
		
		// Wooden Door
		if (id == Block.doorWood.blockID)
			return Item.doorWood.getItemName();
		// Iron Door
		else if (id == Block.doorSteel.blockID)
			return Item.doorSteel.getItemName();	
		// redstone
		else if (id == Block.redstoneWire.blockID)
			return Item.redstone.getItemName();
		// tilled field
		else if (id == Block.tilledField.blockID || id == Block.grass.blockID)
			return Block.dirt.getBlockName();
		// Furnaces
		else if (id == Block.stoneOvenActive.blockID)
			return Block.stoneOvenIdle.getBlockName();
		// redstone lamps
		else if (id == Block.redstoneLampActive.blockID)
			return Block.redstoneLampIdle.getBlockName();
		// redstone repeaters
		else if (id == Block.redstoneRepeaterActive.blockID || id == Block.redstoneRepeaterIdle.blockID)
			return Item.redstoneRepeater.getItemName();
		else if (id == Block.signPost.blockID || id == Block.signWall.blockID)
			return Item.sign.getItemName();
		// any other Block (modblocks are done before)
		else
			return Item.itemsList[id].getItemNameIS(new ItemStack(id, 0, meta));
	}
	
	@Override
	public int getBlockTexture(IBlockAccess world, int x, int y, int z, int side)
	{
		TileEntityProjection entity = (TileEntityProjection) world.getBlockTileEntity(x, y, z);
		
		if (entity == null)
			return 0;
		
		Block block = Block.blocksList[entity.getHeldID()];
		
		if (block == null)
			return 0;
		
		if (entity.getHeldID() == Block.grass.blockID)
		{
			if (side == 1)
				return 0;
			else if (side == 0)
				return 2;
			else
				return 3;
		}
		
		return block.getBlockTexture(world, x, y, z, side);		
	}        
	
	@Override
    public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)
    {
		ItemStack item = par5EntityPlayer.getCurrentEquippedItem();
		TileEntityProjection entity = (TileEntityProjection) par1World.getBlockTileEntity(par2, par3, par4);
		int id = entity.getHeldID();
		
		if (item == null)
			return false;
		
		// Wooden Door
		if (id == Block.doorWood.blockID && item.getItem().shiftedIndex == Item.doorWood.shiftedIndex)
		{
	        if ((entity.getBlockMetadata() & 8) != 0)
	        {
	        	// top block
	        	par1World.setBlockAndMetadata(par2, par3, par4, Block.doorWood.blockID, entity.getBlockMetadata());
	        	par1World.setBlockAndMetadata(par2, par3-1, par4, Block.doorWood.blockID, ((TileEntityProjection) par1World.getBlockTileEntity(par2, par3-1, par4)).getBlockMetadata());
	        }
	        else
	        {
	        	// bottom block
	        	par1World.setBlockAndMetadata(par2, par3, par4, Block.doorWood.blockID, entity.getBlockMetadata());
	        	par1World.setBlockAndMetadata(par2, par3+1, par4, Block.doorWood.blockID, ((TileEntityProjection) par1World.getBlockTileEntity(par2, par3+1, par4)).getBlockMetadata());
	        }
	        item.stackSize--;
			return true;
		}
		
		// Iron Door
		else if (id == Block.doorSteel.blockID && item.getItem().shiftedIndex == Item.doorSteel.shiftedIndex)
		{
	        if ((entity.getBlockMetadata() & 8) != 0)
	        {
	        	// top block
	        	par1World.setBlockAndMetadata(par2, par3, par4, Block.doorSteel.blockID, entity.getBlockMetadata());
	        	par1World.setBlockAndMetadata(par2, par3-1, par4, Block.doorSteel.blockID, ((TileEntityProjection) par1World.getBlockTileEntity(par2, par3-1, par4)).getBlockMetadata());
	        }
	        else
	        {
	        	// bottom block
	        	par1World.setBlockAndMetadata(par2, par3, par4, Block.doorSteel.blockID, entity.getBlockMetadata());
	        	par1World.setBlockAndMetadata(par2, par3+1, par4, Block.doorSteel.blockID, ((TileEntityProjection) par1World.getBlockTileEntity(par2, par3+1, par4)).getBlockMetadata());
	        }
	        item.stackSize--;
			return true;
		}
		
		// redstone
		else if (id == Block.redstoneWire.blockID && item.getItem().shiftedIndex == Item.redstone.shiftedIndex)
		{
			par1World.setBlockAndMetadata(par2, par3, par4, Block.redstoneWire.blockID , entity.getBlockMetadata());
			item.stackSize--;
			return true;
		}
		
		// tilled field
		else if (id == Block.tilledField.blockID && item.getItem().shiftedIndex == Block.dirt.blockID)
		{
			par1World.setBlockAndMetadata(par2, par3, par4, Block.redstoneWire.blockID , entity.getBlockMetadata());
			item.stackSize--;
			return true;
		}
		
		// Furnaces
		else if ((id == Block.stoneOvenActive.blockID || id == Block.stoneOvenIdle.blockID) && item.getItem().shiftedIndex == Block.stoneOvenIdle.blockID)
		{
			par1World.setBlockAndMetadata(par2, par3, par4, Block.stoneOvenIdle.blockID , entity.getBlockMetadata());
			item.stackSize--;
			return true;
		}
		
		// redstone lamps
		else if ((id == Block.redstoneLampActive.blockID || id == Block.redstoneLampIdle.blockID) && item.getItem().shiftedIndex == Block.redstoneLampIdle.blockID)
		{
			par1World.setBlockAndMetadata(par2, par3, par4, Block.redstoneLampIdle.blockID , entity.getBlockMetadata());
			item.stackSize--;
			return true;
		}
		
		// redstone repeaters
		else if ((id == Block.redstoneRepeaterActive.blockID || id == Block.redstoneRepeaterIdle.blockID) && item.getItem().shiftedIndex == Block.redstoneRepeaterIdle.blockID)
		{
			par1World.setBlockAndMetadata(par2, par3, par4, Block.redstoneRepeaterIdle.blockID , entity.getBlockMetadata());
			item.stackSize--;
			return true;
		}
		
		// dirt and grass
		else if (id == Block.grass.blockID && item.getItem().shiftedIndex == Block.dirt.blockID)
		{
			par1World.setBlockAndMetadata(par2, par3, par4, Block.grass.blockID , entity.getBlockMetadata());
			item.stackSize--;
			return true;
		}
		
		// wool and subtype items.
		else if (item.getHasSubtypes())
		{
			if (item.getItem().shiftedIndex == entity.getHeldID() && item.getItemDamage() == entity.getBlockMetadata())
			{
				par1World.setBlockAndMetadata(par2, par3, par4, id, entity.getBlockMetadata());
				item.stackSize--;
				return true;
			}
		}
		
		// signs
		else if (item.getItem().shiftedIndex == Item.sign.shiftedIndex && (id == Block.signPost.blockID || id == Block.signWall.blockID))
		{
			par1World.setBlockAndMetadata(par2, par3, par4, id, entity.getBlockMetadata());
			item.stackSize--;
			par1World.setBlockTileEntity(par2, par3, par4, entity.getCopiedEntity());
			return true;
		}
		
		// note block
		else if (item.getItem().shiftedIndex == Block.music.blockID)
		{
			par1World.setBlockAndMetadata(par2, par3, par4, id, entity.getBlockMetadata());
			item.stackSize--;
			par1World.setBlockTileEntity(par2, par3, par4, entity.getCopiedEntity());
		}
		
		// any normal block
		else if (item.getItem().shiftedIndex == entity.getHeldID())
		{
			par1World.setBlockAndMetadata(par2, par3, par4, id, entity.getBlockMetadata());
			item.stackSize--;
			return true;
		}
		
        return true;
    }
	
	@Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
	{
		TileEntityProjection entity = (TileEntityProjection) world.getBlockTileEntity(x, y, z);
		
		if (entity == null || entity.getHeldID() == 0)
			return;
		
		Block block = Block.blocksList[entity.getHeldID()];
		block.setBlockBoundsBasedOnState(world, x, y, z);
	}
	
	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}
	
	@Override
	public int getRenderType()
	{
		return ProjectorMod.renderID;
	}
	
	@Override
    public int getRenderBlockPass()
    {
        return 1;
    }
	
	@Override
    public int getBlockColor()
    {
        return ProjectorMod.proColor.colorValue;
    }

	@Override
    public int getRenderColor(int par1)
    {
        return ProjectorMod.proColor.colorValue;
    }

	@Override
	public TileEntity createNewTileEntity(World world)
	{
		return new TileEntityProjection();
	}
	
	@Override
    public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
    	return super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, 1-par5);
    }
	
	@Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
        return null;
    }
    
	@Override
    public void addCreativeItems(ArrayList itemList)
    {    	
    }
}