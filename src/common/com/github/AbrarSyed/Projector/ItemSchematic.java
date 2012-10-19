package com.github.AbrarSyed.Projector;

import java.io.File;
import java.util.List;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

import net.minecraft.src.*;

public class ItemSchematic extends Item
{
	public ItemSchematic(int par1) {
		super(par1);
		this.canRepair = false;
		this.maxStackSize = 1;
		this.setIconIndex(0);
		this.setNoRepair();
		this.setHasSubtypes(false);
		this.setTextureFile("/projector/images.png");
		this.setTabToDisplayOn(CreativeTabs.tabMisc);
		this.setItemName("schematic");
	}
	
	@Override
    public void addInformation(ItemStack stack, List list)
    {
    	if (stack.hasTagCompound())
    	{
    		File file = new File(Schematic.SCHEM_DIR, stack.getTagCompound().getString("schematic"));
    		list.add(file.getName());
    		if ((!file.exists() || !file.canRead()) && FMLCommonHandler.instance().getSide().isServer())
    			list.add("§4 -- file inaccessible --");
    	}
    	else
    		list.add(" -- Empty Schematic -- ");
    }
    
    @SideOnly(value = Side.CLIENT)
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
    	// TODO; FIX!
    	FMLClientHandler.instance().displayGuiScreen(player, new GuiSchematicChooser(stack, null, ProjectorAPI.getSchematicFileSystem()));
        return stack;
    }
}
