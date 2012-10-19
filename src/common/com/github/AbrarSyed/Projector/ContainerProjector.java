package com.github.AbrarSyed.Projector;

import net.minecraft.src.*;

public class ContainerProjector extends Container {

	public ContainerProjector(IInventory player, TileEntityProjector entity)
	{
		this.entity = entity;
		
		this.addSlotToContainer(new Slot(entity, 0, 36, 37));
		
		// populate Players spots
        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(player, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new Slot(player, i, 8 + i * 18, 142));
        }
	}

	@Override
	public boolean canInteractWith(EntityPlayer var1)
	{
		return true;
	}
	
    public void putStackInSlot(int par1, ItemStack par2ItemStack)
    {
        this.getSlot(par1).putStack(par2ItemStack);
    }
	
	InventoryBasic schematicSlot;
	TileEntityProjector entity;
}
