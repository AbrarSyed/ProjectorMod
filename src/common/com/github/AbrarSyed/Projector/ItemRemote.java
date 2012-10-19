package com.github.AbrarSyed.Projector;

import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemRenderer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.World;

public class ItemRemote extends Item
{

	public ItemRemote(int par1)
	{
		super(par1);
		this.setMaxDamage(101);
		this.setMaxStackSize(1);
		this.setTextureFile("/projector/images.png");
		this.setIconIndex(2);
		this.setFull3D();
	}
	
    /**
     * Called when item is crafted/smelted. Used only by maps so far.
     */
	@Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player)
    {
    	System.out.println("created");
    	NBTTagCompound tag = new NBTTagCompound();
    	tag.setIntArray("projectorCoords", new int[] {});
    	tag.setIntArray("beacon1Coords", new int[] {});
    	tag.setIntArray("beacon2Coords", new int[] {});
    	tag.setBoolean("beacon1ToSync", true);
    	tag.setBoolean("beacon1Synced", false);
    	tag.setBoolean("beacon2Synced", false);
    	tag.setBoolean("projectorSynced", false);
    	tag.setBoolean("talk", true);
    	stack.setTagCompound(tag);
    }
	
	public static NBTTagCompound getNBTStack(ItemStack stack)
	{
		if (stack == null || !(stack.getItem() instanceof ItemRemote))
			return null;
		else if (!stack.hasTagCompound() || stack.stackTagCompound == null)
		{
	    	NBTTagCompound tag = new NBTTagCompound();
	    	tag.setIntArray("projectorCoords", new int[] {});
	    	tag.setIntArray("beacon1Coords", new int[] {});
	    	tag.setIntArray("beacon2Coords", new int[] {});
	    	tag.setBoolean("beacon1ToSync", true);
	    	tag.setBoolean("beacon1Synced", false);
	    	tag.setBoolean("beacon2Synced", false);
	    	tag.setBoolean("projectorSynced", false);
	    	tag.setBoolean("talk", true);
	    	stack.setTagCompound(tag);
	    	return stack.getTagCompound();
		}
		else
			return stack.getTagCompound();
	}
	
	@Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
		// open the GUI
		ModLoader.openGUI(player, new GuiRemoteControl(world, stack));
        return stack;
    }
	
    /**
     * This is called when the item is used, before the block is activated.
     * @param stack The Item Stack
     * @param player The Player that used the item
     * @param world The Current World
     * @param X Target X Position
     * @param Y Target Y Position
     * @param Z Target Z Position
     * @param side The side of the target hit
     * @return Return true to prevent any further processing.
     */
	@Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int X, int Y, int Z, int side) 
    {
		if (!world.isRemote && !world.isAirBlock(X, Y, Z))
		{
			NBTTagCompound tag = ItemRemote.getNBTStack(stack);
			int id = world.getBlockId(X, Y, Z);
			if (id == ProjectorMod.projector.blockID)
			{
				tag.setIntArray("projectorCoords", new int[] {X, Y, Z});
				tag.setBoolean("projectorSynced", true);
				player.addChatMessage("§3Remote: Projector at ("+X+", "+Y+", "+Z+") synced.");
				return true;
			}
			else if (id == ProjectorMod.beacon.blockID)
			{
				if (tag.getBoolean("beacon1ToSync"))
				{
					tag.setIntArray("beacon1Coords", new int[] {X, Y, Z});
					tag.setBoolean("beacon1Synced", true);
					player.addChatMessage("§3Remote: Beacon at ("+X+", "+Y+", "+Z+") synced as Beacon 1.");
				}
				else
				{
					tag.setIntArray("beacon1Coords", new int[] {X, Y, Z});
					tag.setBoolean("beacon1Synced", true);
					player.addChatMessage("§3Remote: Beacon at ("+X+", "+Y+", "+Z+") synced as Beacon 1.");
				}
				
				return true;
			}
			else if (Block.blocksList[id] instanceof BlockProjection)
			{
				player.addChatMessage("§3Remote: This projection is replaceable with "+((BlockProjection) Block.blocksList[id]).getReplaceDescription(world, X, Y, Z));
				return true;
			}
		}
		return false;
    }
	
    /**
     * Called each tick as long the item is on a player inventory. Uses by maps to check if is on a player hand and
     * update it's contents.
     */
	@Override
    public void onUpdate(ItemStack stack, World world, Entity player, int par4, boolean par5)
	{
		NBTTagCompound tag = this.getNBTStack(stack);
		TileEntityProjector entity = null;

		boolean damaged = false;

		if (tag.getBoolean("projectorSynced"))
		{
			int[]coords = tag.getIntArray("projectorCoords");
			if (world.blockExists(coords[0], coords[1], coords[2]))
			{
				entity = (TileEntityProjector) world.getBlockTileEntity(coords[0], coords[1], coords[2]);
				if (entity != null)
				{
					if (!entity.canChangeState())
					{
						stack.setItemDamage(100-entity.getPercentComplete());
						damaged = true;
					}
				}
				else
				{
					stack.stackTagCompound.setBoolean("projectorSynced", false);
					stack.stackTagCompound.setIntArray("projectorCoords", new int[] {});
				}
			}
		}
		
		if (!damaged)
			stack.setItemDamage(0);
	}
	
	

}
