package com.github.AbrarSyed.Projector;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.zip.GZIPOutputStream;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.src.CompressedStreamTools;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraft.src.NBTBase;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;

public class Schematic
{
	/**
	 * This is designed to be private. you should never just make a schematic.
	 * ALLWAYS create one using the static methods. 
	 * @param file
	 */
	private Schematic(File file)
	{
		sourceFile = file;
		if (file == null)
		{
			initEmpty();
		}
	}

	// ------------------------------------------------------------------------------------------------------------------------------
	// /////////////////////////////////////////////
	// Static Interface Functinality -|-|-|-|-|-|-|-|-|-|-|-|-|
	// /////////////////////////////////////////////
	// ------------------------------------------------------------------------------------------------------------------------------

	/**
	 * Gets the correct type of file and loads it.
	 * @param the file to be loaded
	 * @return a loaded Schematic
	 */
	public static Schematic createAndLoad(File file)
	{
		Schematic schematic = new Schematic(file);

		if (file == null)
		{
			schematic.initEmpty();
		}
		else if (schematic.sourceFile.getPath().endsWith("schematic"))
		{
			schematic.loadNormalSchematic();
			schematic.calcCenters();
			return schematic;
		}
		else if (schematic.sourceFile.getPath().endsWith("mcs2"))
		{
			schematic.loadMCS2();
			schematic.calcCenters();
			return schematic;
		}

		ModLoader.getLogger().log(Level.SEVERE, "Projector: unrecognized file type >> "+file.getPath());
		return schematic;
	}

	/**
	 * Gets the correct type of File and loads it from a schematic ItemStack.
	 * @param the Itemstack from which to load the file name.
	 * @return a loaded Schematic
	 */
	public static Schematic createAndLoad(ItemStack item)
	{
		if (item == null || !item.hasTagCompound())
		{
			Schematic lala = new Schematic(null);
			return lala;
		}
		return (createAndLoad(new File(SCHEM_DIR, item.getTagCompound().getString("schematic"))));
	}

	/**
	 * creates and saves a *.schematic file with the given data.
	 * @param name of the file. extension is automatically appended.
	 * @param sizes (x, y, z) format array of sizes;
	 * @param ids [x][y][z] 3d array of BlockIDs
	 * @param meta [x][y][z] 3d array of Metadata values
	 * @param TEs some type of List containing all NBT Compound tags for TileEntties
	 * @return A newly created and saved schematic file;
	 */
	public static Schematic createAndSaveNormalSchematic(String name, int[] sizes, int[][][] ids, int[][][] meta, HashMap<int[], NBTTagCompound> TEs)
	{
		Schematic schematic = new Schematic(new File(SCHEM_DIR, name+".schematic"));
		schematic.sizes = sizes;
		schematic.ids = ids;
		schematic.meta = meta;
		schematic.tileEntities = TEs;

		NBTTagCompound tag = new NBTTagCompound("Schematic");

		// sizes
		tag.setShort("Width", (short) sizes[0]);
		tag.setShort("Height", (short) sizes[1]);
		tag.setShort("Length", (short) sizes[2]);

		// create arrays of IDs and and metadata + populate
		byte[] setIDs = new byte[sizes[0]*sizes[1]*sizes[2]];
		byte[] setMetas = new byte[sizes[0]*sizes[1]*sizes[2]];
		int index = 0;

		for (int y = 0; y < sizes[1]; y++)
			for (int z = 0; z < sizes[2]; z++)
				for (int x = 0; x < sizes[0]; x++)
				{
					setIDs[index] = (byte) (ids[x][y][z] & 0xff);
					setMetas[index] = (byte) (ids[x][y][z] & 0xff);
					index++;
				}

		tag.setByteArray("Blocks", setIDs);
		tag.setByteArray("Data", setMetas);

		// populate TileEntity tagList
		NBTTagList list = new NBTTagList("TileEntities");
		for (int i = 0; i < TEs.size(); i++)
			list.appendTag(TEs.get(i));
		tag.setTag("TileEntities", list);

		// WRITE STUFF
		try
		{			
			// make temp file
			File temp = new File(schematic.sourceFile.getAbsolutePath() + "_tmp");
			if (temp.exists())
			{
				temp.delete();
			}

			// write temp file
			DataOutputStream stream = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(temp)));
			try
			{
				NBTBase.writeNamedTag(tag, stream);
			}
			finally
			{
				stream.close();
			}

			// change from temp to real
			if (schematic.sourceFile.exists())
			{
				schematic.sourceFile.delete();
			}

			if (schematic.sourceFile.exists())
			{
				throw new IOException("Failed to delete " + schematic.sourceFile);
			}
			else
			{
				temp.renameTo(schematic.sourceFile);
			}
		} 
		catch (IOException e)
		{
			System.out.println("write failed");
		}

		return schematic;
	}

	// ------------------------------------------------------------------------------------------------------------------------------
	// /////////////////////////////////////////////
	// Necessary for functionality -|-|-|-|-|-|-|-|-|-|-|-|-|
	// /////////////////////////////////////////////
	// ------------------------------------------------------------------------------------------------------------------------------

	private void loadNormalSchematic()
	{
		if (sourceFile == null || !sourceFile.getPath().endsWith("schematic"))
		{
			ModLoader.getLogger().log(Level.SEVERE, "Wrong file type");
			initEmpty();
			return;
		}
		NBTTagCompound nbt;
		try
		{
			nbt = CompressedStreamTools.readCompressed(new FileInputStream(sourceFile));

			sizes = new int[] {nbt.getShort("Width"), nbt.getShort("Height"), nbt.getShort("Length")};

			byte[] loadedIDs = nbt.getByteArray("Blocks");
			byte[] metaLoaded = nbt.getByteArray("Data");
			ids = new int[sizes[0]][sizes[1]][sizes[2]];
			meta = new int[sizes[0]][sizes[1]][sizes[2]];
			int index = 0;

			// populate int IDs
			for (int y = 0; y < sizes[1]; y++)
				for (int z = 0; z < sizes[2]; z++)
					for (int x = 0; x < sizes[0]; x++)
					{
						ids[x][y][z] = loadedIDs[index];
						meta[x][y][z] = metaLoaded[index];
						index++;
					}

			NBTTagList entities = nbt.getTagList("TileEntities");
			tileEntities = new HashMap<int[], NBTTagCompound>();
			for (int i = 0; i < entities.tagCount(); i++)
			{
				NBTTagCompound tag = (NBTTagCompound) entities.tagAt(i);

				if (ProjectorAPI.instance.TileEntityMap.contains(tag.getString("id")))
					tileEntities.put(new int[] {tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z")}, tag);
			}		
		}
		catch (IOException e)
		{
			ModLoader.getLogger().log(Level.SEVERE, "Projector: nbt failed reading", e);
			e.printStackTrace();
			initEmpty();
			return;
		}
	}

	private void loadMCS2()
	{
		if (sourceFile == null || !sourceFile.getPath().endsWith("mcs2"))
		{
			ModLoader.getLogger().log(Level.SEVERE, "Wrong file type");
			initEmpty();
			return;
		}

		try
		{
			NBTTagCompound nbt = CompressedStreamTools.readCompressed(new FileInputStream(sourceFile));

			sizes = new int[] {nbt.getShort("Width"), nbt.getShort("Height"), nbt.getShort("Length")};

			short[] loadedIDs = byteArrayToShortArray(nbt.getByteArray("Blocks"));
			byte[] metaLoaded = byteToNibbleArray(nbt.getByteArray("Data"), sizes[0]*sizes[1]*sizes[2]);
			ids = new int[sizes[0]][sizes[1]][sizes[2]];
			meta = new int[sizes[0]][sizes[1]][sizes[2]];
			int index = 0;

			desc = nbt.getString("description");
			if (desc == null)
				desc = "";

			thumbnail = nbt.getByteArray("image");

			for (int y = 0; y < sizes[1]; y++)
				for (int z = 0; z < sizes[2]; z++)
					for (int x = 0; x < sizes[0]; x++)
					{
						ids[x][y][z] = loadedIDs[index];
						meta[x][y][z] = metaLoaded[index];
						index++;
					}

			NBTTagList entities = nbt.getTagList("TileEntities");
			tileEntities = new HashMap<int[], NBTTagCompound>();
			for (int i = 0; i < entities.tagCount(); i++)
			{
				NBTTagCompound tag = (NBTTagCompound) entities.tagAt(i);

				if (ProjectorAPI.instance.TileEntityMap.contains(tag.getString("id")))
					tileEntities.put(new int[] {tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z")}, tag);
			}
		}
		catch (IOException e)
		{
			ModLoader.getLogger().log(Level.SEVERE, "Projector: nbt failed reading", e);
			e.printStackTrace();
			initEmpty();
			return;
		}
	}

	// ------------------------------------------------------------------------------------------------------------------------------
	// /////////////////////////////////////////////
	// Helper methods -|-|-|-|-|-|-|-|-|-|-|-|-|
	// /////////////////////////////////////////////
	// ------------------------------------------------------------------------------------------------------------------------------

	private void calcCenters()
	{
		centered = new int[] {-(sizes[0]/2), -(sizes[2]/2)};
	}
	
	/**
	 * Trims empty slices from all sides of the schematic.
	 *
	private void shaveAirSpace()
	{
		boolean empty = true;
		int numEmpties = 0;
		int minusBack = 0;
		int minusFront = 0;
		int[][][] newids;
		int[][][] newmetas;
		int index;

		// Y-Z slice
		boolean[] empties = new boolean[sizes[0]];
		Arrays.fill(empties, true);

		// x
		for (int i = 0; i < sizes[0]; i++)
		{
			// y
			for (int j = 0; j < sizes[1] && empty == true; j++)
			{
				// z
				for (int k = 0; k < sizes[2] && empty == true; k++)
				{
					if (ids[i][j][k] > 0)
						empty = false;
				}
			}
			if (empty = false)
				numEmpties++;
			empties[i] = empty;
			empty = true;
		}

		if (numEmpties > 0)
		{

			//calc from front
			for (int i = 0; i < empties.length; i++)
			{
				if (i > 0 && empties[i] == true && empties[i-1] == true)
					minusFront++;
				else if (i == 0 && empties[i] == true)
					minusFront++;
			}

			// calc from back
			for (int i = empties.length-1; i <= 0; i--)
			{
				if (i < empties.length-1 && empties[i] == true && empties[i+1] == true)
					minusBack++;
				else if (i == empties.length-1 && empties[i] == true)
					minusBack++;
			}

			//now..empties do stuff...
			newids = new int[sizes[0]-minusFront-minusBack][sizes[1]][sizes[2]];
			newmetas = new int[sizes[0]-minusFront-minusBack][sizes[1]][sizes[2]];
			index = 0;

			// x
			for (int i = minusFront; i < sizes[0]-minusBack; i++)
			{
				newids[index] = ids[i];
				newmetas[index] = meta[i];
				index++;
			}

			// set to default
			ids = newids;
			meta = newmetas;
			sizes[0] = sizes[0]-minusFront-minusBack;
		}

		// ------------------------------------------------------------------------
		// X END...   Y START   !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		// ------------------------------------------------------------------------

		empty = true;
		minusFront = 0;
		minusBack = 0;
		numEmpties = 0;

		// Z-X slice
		empties = new boolean[sizes[1]];

		// Y
		for (int i = 0; i < sizes[1]; i++)
		{
			// X
			for (int j = 0; j < sizes[0] && empty == true; j++)
			{
				// Z
				for (int k = 0; k < sizes[2] && empty == true; k++)
				{
					if (ids[j][i][k] > 0)
						empty = false;
				}
			}
			if (empty = false)
				numEmpties++;
			empties[i] = empty;
			empty = true;
		}

		if (numEmpties > 0)
		{

			//calc from front
			for (int i = 0; i < empties.length; i++)
			{
				if (i > 0 && empties[i] == true && empties[i-1] == true)
					minusFront++;
				else if (i == 0 && empties[i] == true)
					minusFront++;
			}


			// calc from back
			for (int i = empties.length-1; i <= 0; i--)
			{
				if (i < empties.length-1 && empties[i] == true && empties[i+1] == true)
					minusBack++;
				else if (i == empties.length-1 && empties[i] == true)
					minusBack++;
			}

			//now..empties do stuff...
			newids = new int[sizes[0]][sizes[1]-minusFront-minusBack][sizes[2]];
			newmetas = new int[sizes[0]][sizes[1]-minusFront-minusBack][sizes[2]];
			index = 0;


			for (int x = 0; x < sizes[0]; x++)
			{
				for (int y = minusFront; y < sizes[1]-minusBack; y++)
				{
					newids[x][index] = ids[x][y];
					newmetas[x][index] = meta[x][y];
					index++;
				}
				index = 0;
			}

			ids = newids;
			meta = newmetas;
			sizes[1] = sizes[1]-minusFront-minusBack;
		}

		// ------------------------------------------------------------------------
		// Y END....   Z START   !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		// ------------------------------------------------------------------------

		empty = true;
		minusFront = 0;
		minusBack = 0;
		numEmpties = 0;

		// Y-X slice
		empties = new boolean[sizes[2]];

		// z
		for (int i = 0; i < sizes[2]; i++)
		{
			// y
			for (int j = 0; j < sizes[1] && empty == true; j++)
			{
				// x
				for (int k = 0; k < sizes[0] && empty == true; k++)
				{
					if (ids[k][j][i] > 0)
						empty = false;
				}
			}
			if (empty = false)
				numEmpties++;
			empties[i] = empty;
			empty = true;
		}

		if (numEmpties > 0)
		{


			//calc from front
			for (int i = 0; i < empties.length; i++)
			{
				if (i > 0 && empties[i] == true && empties[i-1] == true)
					minusFront++;
				else if (i == 0 && empties[i] == true)
					minusFront++;
			}


			// calc from back
			for (int i = empties.length-1; i <= 0; i--)
			{
				if (i < empties.length-1 && empties[i] == true && empties[i+1] == true)
					minusBack++;
				else if (i == empties.length-1 && empties[i] == true)
					minusBack++;
			}

			//now..empties do stuff...
			newids = new int[sizes[0]][sizes[1]][sizes[2]-minusFront-minusBack];
			newmetas = new int[sizes[0]][sizes[1]][sizes[2]-minusFront-minusBack];
			index = 0;

			for (int x = 0; x < sizes[0]; x++)
			{
				for (int y = 0; y < sizes[1]; y++)
				{
					for (int z = minusFront; z < sizes[2]-minusBack; z++)
					{
						newids[x][y][index] = ids[x][y][z];
						newmetas[x][y][index] = meta[x][y][z];
						index++;
					}
					index = 0;
				}
			}

			ids = newids;
			meta = newmetas;
			sizes[2] = sizes[2]-minusFront-minusBack;
		}
	}
	*/

	/**
	 * This method is NOT for conventional byte-to-short conversion.
	 * This method constructs 3 bytes for every 2 shorts
	 * @param shorts array of shorts
	 * @return converted array of bytes
	 */
	private static byte[] shortArrayToByteArray(short[] shorts)
	{
		byte[] bytes = new byte[Math.round((shorts.length /2) * 3)];

		// to help set the number of itterations.
		int offset = (bytes.length % 3) > 0 ? 1 : 0;

		for (int i = 0; i < (bytes.length/3)+offset; i += 3)
		{
			bytes[i*3] = (byte) (shorts[i/2] & 0xff);
			bytes[(i*3)+1] = (byte) ((shorts[i/2] & 0xf00) >> 8);

			try
			{
				bytes[(i*3)+1] += (byte) (shorts[i/2 + 1] & 0xf << 4);
				bytes[(i*3)+2] = (byte) ((shorts[i/2 + 1] & 0xff0) >> 4);
			}
			catch(IndexOutOfBoundsException e)
			{
				continue;
			}
		}
		return bytes;
	}

	/**
	 * This method is NOT for conventional byte-to-short conversion.
	 * This method constructs 1 short for every 3/2 bytes.
	 * @param bytes the array of bytes to be converted
	 * @return an array of shorts after conversion
	 */
	private static short[] byteArrayToShortArray(byte[] bytes)
	{
		// # of shorts = (int) bytes.length* (2/3)
		short[] shorts = new short[(bytes.length / 3) * 2];

		// to help set the number of itterations.
		int offset = (bytes.length % 3) > 0 ? 1 : 0; 

		// i = itterations
		for (int i = 0; i < (bytes.length/3)+offset; i += 3)
		{
			try
			{
				// set short to the 1st byte and 1st part of the second byte.
				shorts[i/2] = bytes[i*3];
				shorts[i/2] += (short) ((bytes[(i*3)+1] & 0xf) << 8);
			}
			catch(IndexOutOfBoundsException e)
			{
				shorts[i] = 0;
				continue;
			}

			try
			{
				// set short to second part of 1st byte, and the 3rd byte.
				shorts[i/2 + 1] = (short) ((bytes[(i*3)+1] & 0xf0) >> 4);
				shorts[i/2 + 1] += (short) (bytes[(i*3)+2] << 4);
			}
			catch(IndexOutOfBoundsException e)
			{
				continue;
			}
		}
		return shorts;
	}

	/**
	 * used for converting compressed byte arrays to uncompressed 4-bit bytes.
	 *  -- see metadta --
	 * @return byte array with 4 bits used each.
	 */
	private static byte[] byteToNibbleArray(byte[] bytes, int numNibble)
	{
		byte[] nibbles = new byte[numNibble];

		for (int i = 0; i < bytes.length; i++)
		{
			nibbles[i*2] = (byte) (bytes[i] & 0xf);
			try
			{
				nibbles[(i*2)+1] = (byte) ((bytes[i] & 0xf0) >> 4);
			}
			catch(IndexOutOfBoundsException e)
			{
				continue;
			}
		}
		return nibbles;
	}

	/**
	 * compresses 4-bit arrays to a full 1/2 size byte array.
	 *  -- see metadta --
	 * @return compressed byte array
	 */
	private static byte[] nibbleArrayToBytes(byte[] nibbles)
	{
		int offset = nibbles.length % 2  == 0 ? 0 : 1; 

		byte[] bytes = new byte[(nibbles.length/2) + 1];

		for (int i = 0; i < bytes.length; i++)
		{
			bytes[i] = nibbles[i*2];
			try
			{
				// was >> 4 before
				bytes[i] += nibbles[(i*2)+1] << 4;
			}
			catch(IndexOutOfBoundsException e)
			{
				continue;
			}
		}
		return bytes;
	}

	private void initEmpty()
	{
		sourceFile = null;
		ids = new int[][][] { { {0} }};
		meta = new int[][][] { { {0} }};
		sizes = new int[] {0, 0, 0};
		centered = new int[] {0, 0};
		thumbnail = null;
		desc = "";
		tileEntities = new HashMap<int[], NBTTagCompound>();
	}

	/**
	 * checks if both arrays are equal
	 * @param one
	 * @param two
	 * @return if both arrays are equal
	 */
	private static boolean arraysEqual(int[] one, int[] two)
	{
		if (one.length == two.length)
		{
			boolean check = true;
			for (int i = 0; i < one.length || check == false; i++)
			{
				check = one[i] == two[i];
			}
			return check;
		}
		return false;
	}

	/**
	 * Checks if both 3d arrays are equal
	 * @param one
	 * @param two
	 * @return if both 3d arrays are equal
	 */
	private static boolean bigArraysEqual(int[][][] one, int[][][] two)
	{
		if (one.length == two.length)
		{
			boolean check = true;
			for (int i = 0; i < one.length || check == false; i++)
			{
				if (one[i].length == two[i].length)
				{
					for (int j = 0; j < one[i].length || check == false; j++)
					{
						check = arraysEqual(one[i][j], two[i][j]);
					}
				}
				else
					check = false;
			}
			return check;
		}
		return false;
	}


	// ------------------------------------------------------------------------------------------------------------------------------
	// /////////////////////////////////////////////
	// Accessors - Mutators -|-|-|-|-|-|-|-|-|-|-|-|-|
	// /////////////////////////////////////////////
	// ------------------------------------------------------------------------------------------------------------------------------

	/**
	 * @return the sizes int array (x, y, z) format
	 */
	public int[] getSizes()
	{
		return sizes;
	}

	/**
	 * @return the centered
	 */
	public int[] getCentered()
	{
		return centered;
	}

	/**
	 * @return the ids
	 */
	public int[][][] getIds()
	{
		return ids;
	}

	/**
	 * @return the meta
	 */
	public int[][][] getMeta()
	{
		return meta;
	}

	public String getFilePath()
	{
		return sourceFile.getPath();
	}

	public NBTTagCompound getTileEntityTag(int[] coords)
	{
		return tileEntities.get(coords);
	}

	// ------------------------------------------------------------------------------------------------------------------------------
	// /////////////////////////////////////////////
	// Feilds and stuff -|-|-|-|-|-|-|-|-|-|-|-|-|
	// /////////////////////////////////////////////
	// ------------------------------------------------------------------------------------------------------------------------------

	private int[] sizes = new int[3]; // (xyz)
	private int[] centered = new int[2]; // (xz)
	private int[][][] ids; // BlockIDs [x][y][z]
	private int[][][] meta; // metadata [x][y][z]
	private byte[] thumbnail;
	private String desc;
	private HashMap<int[], NBTTagCompound> tileEntities;
	private File sourceFile;

	public static final File SCHEM_DIR = new File(ModLoader.getMinecraftInstance().mcDataDir, "/schematics/"); // used everywhere
}
