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

public class PacketProjectorTE extends Packet250CustomPayload
{
	public static final int packetID  = 5;
	
	public PacketProjectorTE(TileEntityProjector entity)
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
			
			// stuff
			stream.writeBoolean(entity.isProjecting());
			stream.writeInt(entity.getCurrentY());
			
			// offsets
			int[] offsets = entity.getOffsets();
			stream.writeInt(offsets[0]);
			stream.writeInt(offsets[1]);
			stream.writeInt(offsets[2]);
			
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
			
			TileEntityProjector projector = (TileEntityProjector) entity;
			
			projector.readPacket(stream.readBoolean(), stream.readInt());
			projector.setOffsets(new int[] {stream.readInt(), stream.readInt(), stream.readInt()});
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
			
			TileEntityProjector projector = (TileEntityProjector) entity;
			
			projector.readPacket(stream.readBoolean(), stream.readInt());
			projector.setOffsets(new int[] {stream.readInt(), stream.readInt(), stream.readInt()});
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
