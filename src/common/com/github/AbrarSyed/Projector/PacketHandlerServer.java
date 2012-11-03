package com.github.AbrarSyed.Projector;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.WorldServer;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class PacketHandlerServer implements IPacketHandler {

	@Override
	public void onPacketData(NetworkManager manager, Packet250CustomPayload packet, Player playerFake)
	{
		try
		{
			ByteArrayInputStream streambyte = new ByteArrayInputStream(packet.data);
			DataInputStream stream = new DataInputStream(streambyte);
			
			EntityPlayer player = (EntityPlayer) playerFake;
			WorldServer world = (WorldServer) player.worldObj;
			
			int ID = stream.read();

			switch(ID)
			{
			case 0:
				int length = stream.readInt();
				byte[] array = new byte[length];
				ByteArrayInputStream streambyte2 = new ByteArrayInputStream(packet.data);
				ObjectInputStream streamO = new ObjectInputStream(streambyte2);
				FileSystem system = (FileSystem) streamO.readObject();
				ProjectorAPI.instance.schematicsDir = system;
				streamO.close();
				streambyte2.close();
				break;
			case 1:
				PacketSchematicFile.readServer(stream, world, player);
				break;
			case 2:
				PacketOpenGui.readServer(stream, world, player);
				break;
			case 3:
				PacketProjectorControl.readServer(stream, world, player);
				break;
			case 4:
				PacketProjectionTE.readServer(stream, world, player);
				break;
			case 5:
				PacketProjectorTE.readServer(stream, world, player);
				break;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
