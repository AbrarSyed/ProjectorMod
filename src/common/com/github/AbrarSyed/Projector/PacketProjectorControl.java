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
import net.minecraft.src.WorldClient;
import net.minecraft.src.WorldServer;

public class PacketProjectorControl extends Packet250CustomPayload
{
	// TODO: not read yet
	public static final int packetID  = 3;
	
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
	
	public static void readServer(DataInputStream stream, WorldServer world, EntityPlayer player)
	{	
		// TODO: fix.
		try
		{
			int id = stream.read();
			int x = stream.readInt();
			int y = stream.readInt();
			int z = stream.readInt();
			
			player.openGui(ProjectorMod.instance, id, world, x, y, z);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void readClient(DataInputStream stream, WorldClient world, EntityPlayer player)
	{
		// TODO fix.
		// do nothing.
	}
}
