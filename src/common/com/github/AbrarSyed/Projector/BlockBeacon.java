package com.github.AbrarSyed.Projector;

import java.util.ArrayList;

import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;

public class BlockBeacon extends Block {

	public BlockBeacon(int par1)
	{
		super(par1, Material.iron);
		this.setTextureFile("/projector/images.png");
		blockIndexInTexture = 1;
	}
	
    /**
     * Called when a new CreativeContainer is opened, populate the list 
     * with all of the items for this block you want a player in creative mode
     * to have access to.
     * 
     * @param itemList The list of items to display on the creative inventory.
     */
	@Override
    public void addCreativeItems(ArrayList itemList)
    {
		itemList.add(new ItemStack(this));
    }
	
    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
	@Override
    public boolean isOpaqueCube()
    {
        return false;
    }
	
	@Override
	public int getRenderType()
	{
		return 1;
	}

}
