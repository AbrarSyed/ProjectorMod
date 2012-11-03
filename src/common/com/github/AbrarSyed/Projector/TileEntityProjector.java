package com.github.AbrarSyed.Projector;

import net.minecraft.src.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import cpw.mods.fml.common.modloader.ModLoaderHelper;

public class TileEntityProjector extends TileEntity implements IInventory
{
	// necessary
	public TileEntityProjector()
	{
		super();
		this.logString = "idling";
		offsets = new int[] { 0, 2, 0};
	}
	
	// ------------------------------------------------------------------------------------------------------------------------------
	// /////////////////////////////////////////////
	// Necessary for functionality -|-|-|-|-|-|-|-|-|-|-|-|-|
	// /////////////////////////////////////////////
	// ------------------------------------------------------------------------------------------------------------------------------
	
	@Override
	public Packet getDescriptionPacket()
	{
		return new PacketProjectorTE(this);
	}
	
    /**
     * Reads a tile entity from NBT.
     */
    public void readFromNBT(NBTTagCompound nbt)
    {
    	super.readFromNBT(nbt);
    	
    	boolean hasItem = nbt.getBoolean("hasItem");
    	projecting = nbt.getBoolean("projecting");
    	offsets = new int[] {
    			nbt.getInteger("offsetX"),
    			nbt.getInteger("offsetY"),
    			nbt.getInteger("offsetZ")
    	};
    	currentY = nbt.getInteger("currentY");
    	
    	if (hasItem)
    		loadedItem = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("Item"));
    	
    	if (projecting)
    		loadedStructure = Schematic.createAndLoad(loadedItem);
    }

    /**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(NBTTagCompound nbt)
    {
    	super.writeToNBT(nbt);
    	
    	nbt.setBoolean("projecting", projecting);
    	
    	nbt.setInteger("offsetX", offsets[0]);
    	nbt.setInteger("offsetY", offsets[1]);
    	nbt.setInteger("offsetZ", offsets[2]);
    	
    	nbt.setInteger("currentY", currentY);
    	
    	if (loadedItem == null)
    		nbt.setBoolean("hasItem", false);
    	else
    	{
    		nbt.setBoolean("hasItem", true);
    		NBTTagCompound tag = new NBTTagCompound();
    		loadedItem.writeToNBT(tag);
    		nbt.setCompoundTag("Item", tag);
    	}
    }
    
    protected void readPacket(boolean projecting, int offsetY)
    {
    	this.projecting = projecting;
    	this.currentY = offsetY;
    }
	
	/**
	 * loads schematic and sets projection blocks.
	 */
	public void project()
	{
		loadedStructure = Schematic.createAndLoad(loadedItem);
		state = 1;
		projecting = true;
	}
	
	/**
	 * Destroys the projections as if they never existed gradually.
	 */
	public void endProjectionGradually()
	{
		if (projecting)
		{
			if (currentY == 0)
				currentY = this.loadedStructure.getSizes()[1]-1;
			
			state = 3;
		}
	}
	
	/**
	 * Destroys the projections as if they never existed instantly.
	 */
	public void endProjectionInstantly()
	{
		if (projecting)
		{
			for (int y = 0; y < this.loadedStructure.getSizes()[1]; y++)
				this.endProjectionSlice(y);
			projecting = false;
		}
	}
	
	public void pauseTicking()
	{
		if (state == 3)
			projecting = true;
		state = 0;
		logString += " §7-- Stopped --";
	}
	
	public void refreshProjection()
	{
		state = 2;
	}
	
	public NBTTagCompound getTagForEntity(int x, int y, int z)
	{
		if (loadedStructure == null)
			return null;
		return loadedStructure.getTileEntityTag(new int[] {x, y, z});
	}
	
	public NBTTagCompound getTagForEntity(int[] coords)
	{
		return loadedStructure.getTileEntityTag(coords);
	}
	
    /**
     * Allows the entity to update its state. Overridden in most subclasses, e.g. the mob spawner uses this to count
     * ticks and creates a new spawn inside its implementation.
     */
	@Override
    public void updateEntity()
    {
		tick++;
		if (tick == tickRate && state > 0)
		{
			int[] sizes = loadedStructure.getSizes();

			switch(state)
			{
			case 1:
				{
					setProjectionSlice(currentY);
					currentY++;
					percent = (int)((double)currentY/sizes[1] * 100);
					logString = "Projecting: "+percent+"% ";
					if (currentY >= sizes[1]-1)
					{
						logString = "Projection Complete: idling";
						currentY = 0;
						state = 0;
					}
					break;
				}
			case 2:
				refreshProjectionSlice(currentY);
				currentY++;
				percent = (int)((double)currentY/sizes[1] * 100);
				logString = "Refreshing projection: "+percent+"% ";
				if (currentY >= sizes[1]-1)
				{
					logString = "Refreshing Complete: idling";
					currentY = 0;
					state = 0;
				}
				break;
			case 3:
				endProjectionSlice(currentY);
				currentY--;
				percent = 100-(int)((double)currentY/sizes[1] * 100);
				logString = "Stopping projection: "+(100-percent)+"% ";
				if (currentY < 0)
				{
					logString = "Stopping Complete: idling";
					currentY = 0;
					state = 0;
					projecting = false;
				}
				break;
			}
			
		}
		
		if (tick == tickRate)
			tick = 0;
    }
	
	// ------------------------------------------------------------------------------------------------------------------------------
	// /////////////////////////////////////////////
	// Helpers -|-|-|-|-|-|-|-|-|-|-|-|-|
	// /////////////////////////////////////////////
	// ------------------------------------------------------------------------------------------------------------------------------
	
	/**
	 * Destroys the projections as if they never existed.
	 * @param sliceY the Y slice to destroy
	 */
	private void endProjectionSlice(int sliceY)
	{
		int[] sizes = loadedStructure.getSizes();
		int[][][] ids = loadedStructure.getIds();
		int[] centers = loadedStructure.getCentered();

		for (int x = 0; x < sizes[0]; x++)
			for (int z = 0; z < sizes[2]; z++)
			{

				if (ids[x][sliceY][z] == 0)
					continue;

				int id = this.worldObj.getBlockId(x+offsets[0]+xCoord+centers[0], sliceY+offsets[1]+yCoord, z+offsets[2]+zCoord+centers[1]);

				if (id == 0)
					continue;

				if (Block.blocksList[id] instanceof BlockProjection)
				{						
					TileEntityProjection entity = (TileEntityProjection) worldObj.getBlockTileEntity(x+offsets[0]+xCoord+centers[0], sliceY+offsets[1]+yCoord, z+offsets[2]+zCoord+centers[1]);

					if (entity.getHeldID() == ids[x][sliceY][z] && entity.hasProjectedThis(this))
						worldObj.setBlock(x+offsets[0]+xCoord+centers[0], sliceY+offsets[1]+yCoord, z+offsets[2]+zCoord+centers[1], 0);
				}
			}
	}
	
	/**
	 * @param sliceY the Y slice to refresh
	 */
	private void refreshProjectionSlice(int sliceY)
	{
		int[] sizes = loadedStructure.getSizes();
		int[] centers = loadedStructure.getCentered();
		int[][][] ids = loadedStructure.getIds();
		int[][][] meta = loadedStructure.getMeta();
		
		for (int x = 0; x < sizes[0]; x++)
				for (int z = 0; z < sizes[2]; z++)
				{	
					// if 0, do nothing.
					if (ids[x][sliceY][z] == 0)
						continue;
					
					// if NOT zero...
					int id = this.worldObj.getBlockId(x+offsets[0]+xCoord+centers[0], sliceY+offsets[1]+yCoord, z+offsets[2]+zCoord+centers[1]);
					
					// if destination = 0, place the block.
					if (id == 0)
						if (meta[x][sliceY][z] > 0)
							setProjectionWithMeta(x+offsets[0]+xCoord+centers[0], sliceY+offsets[1]+yCoord, z+offsets[2]+zCoord+centers[1], ids[x][sliceY][z], meta[x][sliceY][z]);
						else
							setProjection(x+offsets[0]+xCoord+centers[0], sliceY+offsets[1]+yCoord, z+offsets[2]+zCoord+centers[1], ids[x][sliceY][z]);
					else if (Block.blocksList[id] instanceof BlockProjection)
					{
						// check if correct Block and tileEntity
						TileEntityProjection entity = (TileEntityProjection) worldObj.getBlockTileEntity(x+offsets[0]+xCoord+centers[0], sliceY+offsets[1]+yCoord, z+offsets[2]+zCoord+centers[1]);
						
						if (entity == null)
							continue;
						
						// doesn't match up? replace.
						if (entity.getHeldID() != ids[x][sliceY][z] || !entity.hasProjectedThis(this))
						{
							if (meta[x][sliceY][z] > 0)
								setProjectionWithMeta(x+offsets[0]+xCoord+centers[0], sliceY+offsets[1]+yCoord, z+offsets[2]+zCoord+centers[1], ids[x][sliceY][z], meta[x][sliceY][z]);
							else
								setProjection(x+offsets[0]+xCoord+centers[0], sliceY+offsets[1]+yCoord, z+offsets[2]+zCoord+centers[1], ids[x][sliceY][z]);
						}
					}
				}
	}
	
	/**
	 * @param sliceY the Y slice to project
	 */
	private void setProjectionSlice(int sliceY)
	{
		int[][][] ids = loadedStructure.getIds();
		int[][][] meta = loadedStructure.getMeta();
		int[] sizes = loadedStructure.getSizes();
		int[] centers = loadedStructure.getCentered();

		for (int x = 0; x < sizes[0]; x++)
			for (int z = 0; z < sizes[2]; z++)
				if (meta[x][sliceY][z] > 0)
					setProjectionWithMeta(x+offsets[0]+xCoord+centers[0], sliceY+offsets[1]+yCoord, z+offsets[2]+zCoord+centers[1], ids[x][sliceY][z], meta[x][sliceY][z]);
				else
					setProjection(x+offsets[0]+xCoord+centers[0], sliceY+offsets[1]+yCoord, z+offsets[2]+zCoord+centers[1], ids[x][sliceY][z]);
	}
	
	/**
	 * set a projection block. Metadata sensative version
	 * @param x coordinate X
	 * @param y coordinate Y 
	 * @param z coordinate Z
	 * @param ID projection of this ID
	 * @param meta metadata required for this projection or block.
	 */
	private void setProjectionWithMeta(int x, int y, int z, int ID, int meta)
	{
		if (this.worldObj.getBlockId(x,  y, z) > 0 || ID == 0)
			return;
		worldObj.setBlockAndMetadata(x, y, z, ProjectorMod.projection.blockID, meta);
		TileEntityProjection entity = (TileEntityProjection) worldObj.getBlockTileEntity(x, y, z);
		entity.setData(ID, xCoord, yCoord, zCoord);
	}

	/**
	 * set a projection block.
	 * @param x coordinate X
	 * @param y coordinate Y 
	 * @param z coordinate Z
	 * @param ID projection of this ID
	 */
	private void setProjection(int x, int y, int z, int ID)
	{
		if (this.worldObj.getBlockId(x,  y, z) > 0 || ID == 0)
			return;
		worldObj.setBlock(x, y, z, ProjectorMod.projection.blockID);
		TileEntityProjection entity = (TileEntityProjection) worldObj.getBlockTileEntity(x, y, z);
		entity.setData(ID, xCoord, yCoord, zCoord);
	}
	
	public void unload()
	{
		this.endProjectionInstantly();
		EntityItem entity = new EntityItem(worldObj, xCoord, yCoord+1, zCoord, getStackInSlot(0));
		entity.setVelocity(0, .5, 0);
		this.worldObj.spawnEntityInWorld(entity);
		this.setInventorySlotContents(0, null);
		loadedItem = null;
		loadedStructure = null;
	}
    
	
	// ------------------------------------------------------------------------------------------------------------------------------
	// /////////////////////////////////////////////
	// Accessors and Mutators -|-|-|-|-|-|-|-|-|-|-|-|-|
	// /////////////////////////////////////////////
	// ------------------------------------------------------------------------------------------------------------------------------
	
    /**
	 * @return the loadedSchematic
	 */
	public ItemStack getLoadedSchematic()
	{
		return loadedItem;
	}
	
	/**
	 * @return the projecting
	 */
	public boolean isProjecting()
	{
		return projecting;
	}
	
	/**
	 * Makes sure the Projector is not in the middle of doing something.
	 * @return if you can do somethign else with the projector
	 */
	public boolean canChangeState()
	{
		return state == 0;
	}
	
	/**
	 * sets the offsets array (x, y, z) format
	 * @param newOffsets
	 */
	public void setOffsets(int[] newOffsets)
	{
		offsets = newOffsets;
	}
	
	/**
	 * @return the offsets array (x, y, z) format
	 */
	public int[] getOffsets()
	{
		return offsets;
	}
	
	/**
	 * @return the logString
	 */
	public String getLogString()
	{
		return logString;
	}
	
	public void setLogString(String str)
	{
		logString = str;
	}
	
	public int getPercentComplete()
	{
		return percent;
	}
	
	public boolean hasLoaded()
	{
		return loadedItem != null;
	}
	
	public int getCurrentY()
	{
		return currentY;
	}
	
	// ------------------------------------------------------------------------------------------------------------------------------
	// /////////////////////////////////////////////
	// Variables -|-|-|-|-|-|-|-|-|-|-|-|-|
	// /////////////////////////////////////////////
	// ------------------------------------------------------------------------------------------------------------------------------

	private ItemStack loadedItem;
	private boolean projecting = false;
	
	// 0 = idle, 1 = projecting, 2 = refreshing, 3 = stopping 
	private int state;
	
	// used to stagger updates.
	private int tick = 0;
	
	// how many ticks to go before doing the next slice
	private static final int tickRate = 2;
	
	/// the percent complete of whatever its doing.
	private int percent;
	
	// the current Y slice bieng placed
	private int currentY;
	
	private String logString;
	
	private Schematic loadedStructure;
	private int[] offsets;
	
	// ------------------------------------------------------------------------------------------------------------------------------
	// /////////////////////////////////////////////
	// INVENTORY STUFF -|-|-|-|-|-|-|-|-|-|-|-|-|
	// /////////////////////////////////////////////
	// ------------------------------------------------------------------------------------------------------------------------------
	
	@Override
	public int getSizeInventory()
	{
		return 1;
	}
	@Override
	public void onInventoryChanged()
	{
		super.onInventoryChanged();
		
		if (loadedItem != null && !(loadedItem.getItem() instanceof ItemSchematic))
		{
			unload();
			return;
		}
		
		loadedItem = this.getStackInSlot(0);
		
		logString = "Schematic loaded";
	}

	@Override
	public ItemStack getStackInSlot(int var1)
	{
		if (var1 > 0)
			return null;
		
		return loadedItem;
	}

	@Override
	public ItemStack decrStackSize(int var1, int var2)
	{
		if (var1 > 0)
			return null;
		
        ItemStack var3;

        if (loadedItem.stackSize <= var2)
        {
            var3 = loadedItem;
            loadedItem = null;
            this.onInventoryChanged();
            return var3;
        }
        else
        {
            var3 = loadedItem.splitStack(var2);

            if (loadedItem.stackSize == 0)
            {
                this.loadedItem = null;
            }

            this.onInventoryChanged();
            return var3;
        }
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int var1)
	{
		if (var1 > 0)
			return null;
		
		return loadedItem;
	}

	@Override
	public void setInventorySlotContents(int var1, ItemStack var2)
	{
		if (var1 > 0)
			return;
		
		loadedItem = var2;
	}

	@Override
	public String getInvName()
	{
		return "Projector";
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 1;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer var1)
	{
		return true;
	}

	@Override
	public void openChest()
	{
		
	}

	@Override
	public void closeChest()
	{
		
	}
}
