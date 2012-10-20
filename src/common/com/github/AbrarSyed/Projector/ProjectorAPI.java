package com.github.AbrarSyed.Projector;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.src.Block;
import net.minecraft.src.ModLoader;
import net.minecraftforge.common.Configuration;

import cpw.mods.fml.common.FMLCommonHandler;

public class ProjectorAPI
{
	protected boolean loaded = false;
	protected static ProjectorAPI instance;
	private static Logger logger;
	protected ArrayList<String> TileEntityMap;
	protected boolean[] allowedID;
	private Configuration config;
	protected FileSystem schematicsDir;
	protected static boolean projectUnob;
	
	// ------------------------------------------------------------------------------------------------------------------------------
	// /////////////////////////////////////////////
	// Initi and Process methods -|-|-|-|-|-|-|-|-|-|-|-|-|
	// /////////////////////////////////////////////
	// ------------------------------------------------------------------------------------------------------------------------------
	
	
	protected static void init()
	{
		// create instance variables
		instance = new ProjectorAPI();
		instance.logger = FMLCommonHandler.instance().getFMLLogger();
		instance.TileEntityMap = new ArrayList<String>();
		instance.config = new Configuration(new File(Minecraft.getMinecraftDir(), "/config/ProjectorAPI.cfg"));
		instance.allowedID = new boolean[4096];
		instance.schematicsDir = new FileSystem();
		
		if (!projectUnob)
		{
			disallowID(Block.snow.blockID);
			disallowID(Block.dragonEgg.blockID);
			disallowID(Block.oreCoal.blockID);
			disallowID(Block.mobSpawner.blockID);
			disallowID(Block.sponge.blockID);
			disallowID(Block.crops.blockID);
			disallowID(Block.endPortal.blockID);
			disallowID(Block.portal.blockID);
			disallowID(Block.oreRedstone.blockID);
			disallowID(Block.oreRedstoneGlowing.blockID);
			disallowID(Block.bedrock.blockID);
			disallowID(Block.silverfish.blockID);
		}
		
		Arrays.fill(instance.allowedID, true);
		
		// make sure that Schematic folder exists
		if (!Schematic.SCHEM_DIR.exists() || !Schematic.SCHEM_DIR.isDirectory())
			Schematic.SCHEM_DIR.mkdir();
		
		instance.schematicsDir.load(Schematic.SCHEM_DIR, new FileFilterSchematic());
		
		instance.logger.fine("Projector API initializing complete.");
	}
	
	// ------------------------------------------------------------------------------------------------------------------------------
	// /////////////////////////////////////////////
	// ID, TileEntity, and custom projection hooks -|-|-|-|-|-|-|-|-|-|-|-|-|
	// /////////////////////////////////////////////
	// ------------------------------------------------------------------------------------------------------------------------------
	
	
	/**
	 * Registers a TileEntity to be written to a Block when a pojection is replaced.
	 * Only register TEs that must loaded on-replacement. Such as Signs and Noteblocks.
	 * Keep in mind that this function may be abused for duplication with containers such as chests.
	 * @param ID of the TileEntity
	 */
	public static void registerTileEntity(String id)
	{
		if (!instance.TileEntityMap.contains(id))
			instance.TileEntityMap.add(id);
	}
	
	/**
	 * Makes a given ID not be projected.
	 * This is usually for unobtainables and stuff like Projection Blocks, that are never meant to be placed by the player.
	 * No vanilla Blocks are disallowed by default
	 * @param ID to disallow
	 */
	public static void disallowID(int id)
	{
		instance.allowedID[id] = false;
	}
	
	/**
	 * If a given ID was marked as not to be projected, this overrides it and forces it to be projected.
	 * @param ID to allow
	 */
	public static void forceAllowID(int id)
	{		
		if (!instance.allowedID[id])
			instance.allowedID[id] = true;
	}
	
	public static FileSystem getSchematicFileSystem()
	{
		return instance.schematicsDir;
	}
	
	// ------------------------------------------------------------------------------------------------------------------------------
	// /////////////////////////////////////////////
	// Logging -|-|-|-|-|-|-|-|-|-|-|-|-|
	// /////////////////////////////////////////////
	// ------------------------------------------------------------------------------------------------------------------------------
	
	public static void log(Object msg)
	{
		instance.logger.fine(msg.toString());
	}
	
	public static Logger getLogger()
	{
		return instance.logger;
	}
}
