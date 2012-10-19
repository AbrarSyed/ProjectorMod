package com.github.AbrarSyed.Projector;

import java.lang.reflect.Constructor;

import net.minecraft.src.MapColor;
import net.minecraftforge.common.Configuration;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.NetworkMod.NULL;
import cpw.mods.fml.common.network.NetworkMod.SidedPacketHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@NetworkMod
	(
			clientSideRequired=true,
			serverSideRequired=true,
			serverPacketHandlerSpec = @SidedPacketHandler(channels = {"projector"}, packetHandler = PacketHandlerServer.class),
			clientPacketHandlerSpec = @SidedPacketHandler(channels = {"projector"}, packetHandler = PacketHandlerClient.class),
			connectionHandler = ConnectionHandler.class
	)
@Mod(modid = "Projector", name = "The Projector Mod", version = "0.3.0")
public class ProjectorMod
{
	@Instance(value="Projector")
	public static ProjectorMod instance;
	
	@SidedProxy(clientSide = "com.github.AbrarSyed.Projector.ProxyClient", serverSide = "com.github.AbrarSyed.Projector.ProxyServer")
	public static ProxyCommon proxy;
	
	@PreInit
	public void preInit(FMLPreInitializationEvent event)
	{
		// hack the MapColor constructor
		try
		{
			Constructor constructor = MapColor.class.getDeclaredConstructors()[0];
			constructor.setAccessible(true);
			proColor = (MapColor) constructor.newInstance(new Object[] {14, 0x30a1cf});
			proMaterial = (new MaterialProjection(proColor));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		// property stuff
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		int[] IDs = {
				config.getOrCreateBlockIdProperty("BlockProjector", 2000).getInt(),
				config.getOrCreateBlockIdProperty("BlockBeacon", 2001).getInt(),
				config.getOrCreateBlockIdProperty("BlockProjection", 2002).getInt(),
				config.getOrCreateIntProperty("ItemSchematic", config.CATEGORY_ITEM, 4096).getInt() - 256,
				config.getOrCreateIntProperty("ItemRemote", config.CATEGORY_ITEM, 4097).getInt() - 256,
		};
		ProjectorAPI.projectUnob = config.getOrCreateBooleanProperty("projectUnobtainables", Configuration.CATEGORY_GENERAL, true).getBoolean(true);
		config.save();
		
		IDS = IDs;
	}

	@Init
	public void init(FMLInitializationEvent event)
	{
		// texture and renderring stuff
		proxy.loadTextureStuff();
		
		// GUI stuff
		NetworkRegistry.instance().registerGuiHandler(this, new GuiHandler());

		// definitions
		projector = (BlockProjector) (new BlockProjector(IDS[0])).setBlockName("Projector");
		beacon = (BlockBeacon) (new BlockBeacon(IDS[1])).setBlockName("Beacon");
		projection = (BlockProjection) (new BlockProjection(IDS[2])).setBlockName("Hologram");
		schematic = (ItemSchematic) (new ItemSchematic(IDS[3])).setItemName("Schematic");
		remote = (ItemRemote) (new ItemRemote(IDS[4])).setItemName("Remote");
		
		// registers
		GameRegistry.registerBlock(projector);
		GameRegistry.registerBlock(beacon);
		GameRegistry.registerBlock(projection);
		
		// TE registers
		GameRegistry.registerTileEntity(TileEntityProjection.class, "Projection");
		GameRegistry.registerTileEntity(TileEntityProjector.class, "Projector");
		
		// names and stuff
		LanguageRegistry.addName(projector, "Projector");
		LanguageRegistry.addName(beacon, "Beacon");
		LanguageRegistry.addName(projection, "Hologram");
		LanguageRegistry.addName(schematic, "Schematic");
		LanguageRegistry.addName(remote, "Remote");
		
		// API
		ProjectorAPI.init();
	}
	
	//IDs
	int[] IDS;
	
	// necessary Block stuff
	public static MapColor proColor;
	public static MaterialProjection proMaterial;
	
	// actual Blocks.
	public static BlockProjector projector;
	public static BlockBeacon beacon;
	public static BlockProjection projection;
	
	// Items
	public static ItemSchematic schematic;
	public static ItemRemote remote;
}
