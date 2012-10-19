package com.github.AbrarSyed.Projector;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;
import net.minecraftforge.client.MinecraftForgeClient;

public class ProxyCommon
{
	public void loadTextureStuff()
	{
		// texture registers
		MinecraftForgeClient.preloadTexture("/projections/terrain.png");
		MinecraftForgeClient.preloadTexture("/projector/images.png");
	}

	public Object getGui(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		return null;
	}
}
