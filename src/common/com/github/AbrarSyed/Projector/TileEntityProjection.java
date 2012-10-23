package com.github.AbrarSyed.Projector;

import net.minecraft.src.*;

public class TileEntityProjection extends TileEntity
{
    /**
     * Reads a tile entity from NBT.
     */
	@Override
    public void readFromNBT(NBTTagCompound nbt)
    {
    	super.readFromNBT(nbt);
    	heldID = nbt.getInteger("ID");
    	projectorX = nbt.getInteger("pX");
    	projectorY = nbt.getInteger("pY");
    	projectorZ = nbt.getInteger("pZ");
    }

    /**
     * Writes a tile entity to NBT.
     */
    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
    	super.writeToNBT(nbt);
    	nbt.setInteger("ID", heldID);
    	nbt.setInteger("pX", projectorX);
    	nbt.setInteger("pY", projectorY);
    	nbt.setInteger("pZ", projectorZ);
    }
    
    public void setData(int ID, int x, int y, int z)
    {
    	heldID = ID;
    	projectorX = x;
    	projectorY = y;
    	projectorZ = z;
    }
    
    public TileEntity getCopiedEntity()
    {
    	TileEntityProjector projector = (TileEntityProjector) worldObj.getBlockTileEntity(projectorX, projectorY, projectorZ);
    	
    	if (projector == null)
    		return null;
    	
    	NBTTagCompound tag = projector.getTagForEntity(xCoord, yCoord, xCoord);
    	
    	if (tag != null)
    		return TileEntity.createAndLoadEntity(tag);
    	
		return null;
    }
    
    public boolean hasProjectedThis(TileEntityProjector entity)
    {
    	return entity.xCoord == projectorX && entity.yCoord == projectorY && entity.zCoord == projectorZ;
    }
    
    public TileEntityProjector getProjectorEntity()
    {
    	return (TileEntityProjector) worldObj.getBlockTileEntity(projectorX, projectorY, projectorZ);
    }
    
    @Override
    public boolean canUpdate()
    {
        return false;
    }
    
    @Override
    public Packet getDescriptionPacket()
    {
        return new PacketProjectionTE(this);
    }
    
    /**
	 * @return the heldID
	 */
	public int getHeldID()
	{
		return heldID;
	}
	
    public int getProjectorX()
    {
		return projectorX;
	}

	public int getProjectorY()
	{
		return projectorY;
	}

	public int getProjectorZ()
	{
		return projectorZ;
	}

	private int heldID;
	private int projectorX;
    private int projectorY;
    private int projectorZ;
}
