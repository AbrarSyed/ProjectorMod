package com.github.AbrarSyed.Projector;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraft.src.WorldClient;
import net.minecraft.src.WorldServer;

public class PacketProjectionTE extends Packet250CustomPayload
{
	// TODO: not read yet
	public static final int packetID  = 4;
	
	public PacketProjectionTE(TileEntityProjection entity)
	{
		super();
		
		try
		{
			ByteArrayOutputStream streambyte = new ByteArrayOutputStream();
			DataOutputStream stream = new DataOutputStream(streambyte);
			
			stream.write(packetID);
			
			// TE coords.
			stream.writeInt(entity.xCoord);
			stream.writeInt(entity.yCoord);
			stream.writeInt(entity.zCoord);
			
			// TE projector coords.
			stream.writeInt(entity.getProjectorX());
			stream.writeInt(entity.getProjectorY());
			stream.writeInt(entity.getProjectorZ());
			
			stream.writeInt(entity.getHeldID());
			
			data = streambyte.toByteArray();
			length = data.length;
			channel = "projector";
			
			stream.close();
			streambyte.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void readServer(DataInputStream stream, WorldServer world, EntityPlayer player)
	{
		try
		{
			int x = stream.readInt();
			int y = stream.readInt();
			int z = stream.readInt();
			
			TileEntity entity = world.getBlockTileEntity(x, y, z);
			if (entity == null)
				return;
			
			TileEntityProjection projection = (TileEntityProjection) entity;
			
			int pX = stream.readInt();
			int pY = stream.readInt();
			int pZ = stream.readInt();
			int heldid = stream.readInt();
			
			projection.setData(heldid, pX, pY, pZ);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void readClient(DataInputStream stream, WorldClient world, EntityPlayer player)
	{
		try
		{
			int x = stream.readInt();
			int y = stream.readInt();
			int z = stream.readInt();
			
			TileEntity entity = world.getBlockTileEntity(x, y, z);
			if (entity == null)
				return;
			
			TileEntityProjection projection = (TileEntityProjection) entity;
			
			int pX = stream.readInt();
			int pY = stream.readInt();
			int pZ = stream.readInt();
			int heldid = stream.readInt();
			
			projection.setData(heldid, pX, pY, pZ);
			
			world.markBlockNeedsUpdate(x, y, z);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
