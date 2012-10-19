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

public class PacketSchematicFile extends Packet250CustomPayload
{
	public static final int packetID  = 1;
	
	public PacketSchematicFile(String path, EntityPlayer player)
	{
		super();
		
		try
		{
			ByteArrayOutputStream streambyte = new ByteArrayOutputStream();
			DataOutputStream stream = new DataOutputStream(streambyte);
			
			stream.write(packetID);
			stream.writeUTF(path);
			
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
			ItemStack stack = player.getCurrentEquippedItem();
			if (stack.itemID != ProjectorMod.schematic.shiftedIndex)
				return;
			
			String path = stream.readUTF();
			
			if (path.isEmpty())
			{
				stack.setTagCompound(null);
				return;
			}
			
			if (!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
			
			stack.stackTagCompound.setString("schematic", path);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void readClient(DataInputStream stream, WorldClient world, EntityPlayer player)
	{
		// do nothing.
	}
}
