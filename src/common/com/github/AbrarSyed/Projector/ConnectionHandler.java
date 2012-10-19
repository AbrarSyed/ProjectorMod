package com.github.AbrarSyed.Projector;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.ObjectOutputStream;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.NetHandler;
import net.minecraft.src.NetLoginHandler;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet1Login;
import net.minecraft.src.Packet250CustomPayload;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class ConnectionHandler implements IConnectionHandler {

	@Override
	public void playerLoggedIn(Player player, NetHandler netHandler, NetworkManager manager)
	{
	}

	@Override
	public String connectionReceived(NetLoginHandler netHandler, NetworkManager manager)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void connectionOpened(NetHandler netClientHandler, String server, int port, NetworkManager manager)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void connectionOpened(NetHandler netClientHandler, MinecraftServer server, NetworkManager manager)
	{
		Packet250CustomPayload packet = new Packet250CustomPayload();

		try
		{
			ByteArrayOutputStream streambyte = new ByteArrayOutputStream();
			DataOutputStream stream = new DataOutputStream(streambyte);
			
			stream.write(0);
			
			
			ByteArrayOutputStream streambyte2 = new ByteArrayOutputStream();
			ObjectOutputStream streamO = new ObjectOutputStream(streambyte2);
			streamO.writeObject(ProjectorAPI.getSchematicFileSystem());
			
			byte[] array = streambyte2.toByteArray();
			stream.writeInt(array.length);
			stream.write(streambyte2.toByteArray());
			
			streamO.close();
			streambyte2.close();
			
			
			packet.data = streambyte.toByteArray();
			packet.length = packet.data.length;
			packet.channel = "projector";
			
			stream.close();
			streambyte.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		PacketDispatcher.sendPacketToPlayer(packet, (Player)netClientHandler.getPlayer());
	}

	@Override
	public void connectionClosed(NetworkManager manager)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void clientLoggedIn(NetHandler clientHandler, NetworkManager manager, Packet1Login login)
	{
		// TODO Auto-generated method stub

	}

}
