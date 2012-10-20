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

public class PacketProjectorControl extends Packet250CustomPayload
{
	// TODO: not read yet
	public static final int packetID  = 3;
	
	/**
	 * 0 = set offsets
	 * 1 = project
	 * 2 = pause
	 * 3 = stop
	 * 4 = refresh
	 */
	public PacketProjectorControl(int guiID, int x, int y, int z)
	{
		super();
		
		try
		{
			ByteArrayOutputStream streambyte = new ByteArrayOutputStream();
			DataOutputStream stream = new DataOutputStream(streambyte);
			
			stream.write(packetID);
			
			// control action ID
			stream.write(guiID);
			
			// TE coords.
			stream.writeInt(x);
			stream.writeInt(y);
			stream.writeInt(z);
			
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
	
	/**
	 * @param x
	 * @param y
	 * @param z
	 * @param offsetX new X offset
	 * @param offsetY new Y offset
	 * @param offsetZ new Z Offset
	 */
	public PacketProjectorControl(int x, int y, int z, int offsetX, int offsetY, int offsetZ)
	{
		super();
		
		try
		{
			ByteArrayOutputStream streambyte = new ByteArrayOutputStream();
			DataOutputStream stream = new DataOutputStream(streambyte);
			
			stream.write(packetID);
			
			// control action ID
			stream.write(0);
			
			// TE coords.
			stream.writeInt(x);
			stream.writeInt(y);
			stream.writeInt(z);
			
			// offsets
			stream.writeInt(offsetX);
			stream.writeInt(offsetY);
			stream.writeInt(offsetZ);
			
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
			int id = stream.read();
			int x = stream.readInt();
			int y = stream.readInt();
			int z = stream.readInt();
			
			TileEntity entity = world.getBlockTileEntity(x, y, z);
			if (entity == null)
				return;
			
			TileEntityProjector projector = (TileEntityProjector) entity;
			
			switch(id)
			{
				case 0: 
					int offsetX = stream.readInt();
					int offsetY = stream.readInt();
					int offsetZ = stream.readInt();
					projector.setOffsets(new int[] {offsetX, offsetY, offsetZ});
					break;
				case 1:
					projector.project();
					break;
				case 2:
					projector.pauseTicking();
					break;
				case 3:
					projector.endProjectionGradually();
					break;
				case 4:
					projector.refreshProjection();
					break;
			}
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
			int id = stream.read();
			int x = stream.readInt();
			int y = stream.readInt();
			int z = stream.readInt();
			
			TileEntity entity = world.getBlockTileEntity(x, y, z);
			if (entity == null)
				return;
			
			TileEntityProjector projector = (TileEntityProjector) entity;
			
			switch(id)
			{
				case 0: 
					int offsetX = stream.readInt();
					int offsetY = stream.readInt();
					int offsetZ = stream.readInt();
					projector.setOffsets(new int[] {offsetX, offsetY, offsetZ});
					break;
				case 1:
					projector.project();
					break;
				case 2:
					projector.pauseTicking();
					break;
				case 3:
					projector.endProjectionGradually();
					break;
				case 4:
					projector.refreshProjection();
					break;
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
