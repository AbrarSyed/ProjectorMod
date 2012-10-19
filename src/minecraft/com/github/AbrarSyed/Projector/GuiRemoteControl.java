package com.github.AbrarSyed.Projector;

import java.awt.Color;

import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.GuiSmallButton;
import net.minecraft.src.GuiTextField;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.World;

public class GuiRemoteControl extends GuiScreen
{
	ItemStack stack;
	TileEntityProjector entity;
	GuiNumberField offsetX;
	GuiNumberField offsetY;
	GuiNumberField offsetZ;

	public GuiRemoteControl(World world, ItemStack stack)
	{
		super();
		this.stack = stack;
		NBTTagCompound tag = ItemRemote.getNBTStack(stack);
		if (stack.hasTagCompound() && tag.getBoolean("projectorSynced"))
		{
			int[] coords = tag.getIntArray("projectorCoords");
			
			if (world.blockExists(coords[0], coords[1], coords[2]))
				entity = (TileEntityProjector) world.getBlockTileEntity(coords[0], coords[1], coords[2]);
			else
			{
				tag.setBoolean("projectorSynced", false);
				tag.setIntArray("projectorCoords", new int[] {});
			}
		}
		
		offsetX = new GuiNumberField(this.fontRenderer, 0+ 20, 0, 40, 20);
		offsetY = new GuiNumberField(this.fontRenderer, 0 + 20, 0 + 25, 40, 20);
		offsetZ = new GuiNumberField(this.fontRenderer, 0+ 20, 0 + 50, 40, 20);
	}

	@Override
	public void initGui()
	{
		this.controlList.clear();

		int box1X = this.width/2 + 110;
		int box1Y = this.height/4 + 20;
		
		int box2X = this.width/2 + 20;
		int box2Y = box1Y;

		// done button 
		this.controlList.add(new GuiButton(0, this.width/2 - 100, this.height / 4 + 140, "Done"));

		//--
		// Projector stuff
		//--
		{
			int[] offsets = new int[] {0, 2, 0};
			if (entity != null && entity.getOffsets() != null)
				offsets = entity.getOffsets();

			// BOX 1
			{
				// project, stop, and refresh buttons
				this.controlList.add(new GuiSmallButton(1, box1X, box1Y + 25, 80, 20, "Project"));
				this.controlList.add(new GuiSmallButton(2, box1X, box1Y + 50, 80, 20, "Refresh"));
			}

			// BOX 2
			{
				// offset X buttons and feild
				offsetX = new GuiNumberField(this.fontRenderer, box2X + 20, box2Y, 40, 20);
				offsetX.setFocused(false);
				offsetX.setNumber(offsets[0]);
				this.controlList.add(new GuiSmallButton(3, box2X, box2Y, 20, 20, "X+"));
				this.controlList.add(new GuiSmallButton(4, box2X + 60, box2Y, 20, 20, "-X"));

				// offset Y buttons and feild
				offsetY = new GuiNumberField(this.fontRenderer, box2X + 20, box2Y + 25, 40, 20);
				offsetY.setFocused(false);
				offsetY.setNumber(offsets[1]);
				this.controlList.add(new GuiSmallButton(5, box2X, box2Y + 25, 20, 20, "Y+"));
				this.controlList.add(new GuiSmallButton(6, box2X + 60, box2Y + 25, 20, 20, "-Y"));

				// offset Z buttons and feild
				offsetZ = new GuiNumberField(this.fontRenderer, box2X + 20, box2Y + 50, 40, 20);
				offsetZ.setFocused(false);
				offsetZ.setNumber(offsets[2]);
				this.controlList.add(new GuiSmallButton(7, box2X, box2Y + 50, 20, 20, "Z+"));
				this.controlList.add(new GuiSmallButton(8, box2X + 60, box2Y + 50, 20, 20, "-Z"));
			}
			
			// de-sync Entity
			this.controlList.add(new GuiSmallButton(9, box1X + 15, box1Y, 50, 20, "UnSync"));
			this.controlList.add(new GuiButton(10, box2X + 15, box1Y + 80, 120, 20, "Force Unload"));
		}


	}

	public void updateScreen()
	{
		// --
		// PROJECTOR STUFF
		// --
		{
			if (entity != null)
			{
				((GuiButton) this.controlList.get(9)).enabled = true;
				
				if (entity.isProjecting())
				{
					((GuiButton) this.controlList.get(2)).enabled = true;

					offsetX.setEnabled(false);
					offsetY.setEnabled(false);
					offsetZ.setEnabled(false);
					for (int i = 3; i < 9; i++)
						((GuiButton) this.controlList.get(i)).enabled = false;
					
		        	((GuiButton) this.controlList.get(1)).displayString = "stop"; 
		        	((GuiButton) this.controlList.get(1)).enabled = true;
				}
				else
				{
					((GuiButton) this.controlList.get(2)).enabled = false;

					offsetX.setEnabled(true);
					offsetY.setEnabled(true);
					offsetZ.setEnabled(true);
					for (int i = 3; i < 9; i++)
						((GuiButton) this.controlList.get(i)).enabled = true;
					
		        	((GuiButton) this.controlList.get(1)).displayString = "project"; 
		        	((GuiButton) this.controlList.get(1)).enabled = true;
		        	
					((GuiButton) this.controlList.get(10)).enabled = true;
				}

				if (!entity.canChangeState())
				{
					((GuiButton) this.controlList.get(2)).enabled = false;

					offsetX.setEnabled(false);
					offsetY.setEnabled(false);
					offsetZ.setEnabled(false);

					for (int i = 3; i < 9; i++)
						((GuiButton) this.controlList.get(i)).enabled = false;
					
		        	((GuiButton) this.controlList.get(1)).displayString = "pause";
		        	((GuiButton) this.controlList.get(1)).enabled = true;
		        	
					((GuiButton) this.controlList.get(10)).enabled = false;
				}
				
				if (!entity.hasLoaded() || entity.getLoadedSchematic() == null || !entity.getLoadedSchematic().hasTagCompound() || entity.getLoadedSchematic().stackTagCompound.getString("schematic") == null)
				{
					((GuiButton) this.controlList.get(1)).enabled = false;
					((GuiButton) this.controlList.get(2)).enabled = false;

					offsetX.setEnabled(true);
					offsetY.setEnabled(true);
					offsetZ.setEnabled(true);

					for (int i = 3; i < 9; i++)
						((GuiButton) this.controlList.get(i)).enabled = true;
					
					((GuiButton) this.controlList.get(10)).enabled = false;
				}
				
				if (entity.getOffsets() != null)
				{
					if (offsetX.getNumber() != entity.getOffsets()[0])
						entity.setOffsets(new int[] {offsetX.getNumber(), entity.getOffsets()[1], entity.getOffsets()[2]});

					if (offsetY.getNumber() != entity.getOffsets()[1])
						entity.setOffsets(new int[] {entity.getOffsets()[0], offsetY.getNumber(), entity.getOffsets()[2]});

					if (offsetZ.getNumber() != entity.getOffsets()[2])
						entity.setOffsets(new int[] {entity.getOffsets()[0], entity.getOffsets()[1], offsetZ.getNumber()});
				}
			}
			else
			{
				((GuiButton) this.controlList.get(1)).enabled = false;
				((GuiButton) this.controlList.get(2)).enabled = false;
				((GuiButton) this.controlList.get(3)).enabled = false;

				offsetX.setEnabled(false);
				offsetY.setEnabled(false);
				offsetZ.setEnabled(false);

				for (int i = 3; i < 9; i++)
					((GuiButton) this.controlList.get(i)).enabled = false;
				
				((GuiButton) this.controlList.get(9)).enabled = false;
				((GuiButton) this.controlList.get(10)).enabled = false;
			}
		}
	}

	/**
	 * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
	 */
	@Override
	public void actionPerformed(GuiButton par1GuiButton)
	{
		if (par1GuiButton.enabled)
		{
			//--
			// For All
			// --
			if (par1GuiButton.id == 0)
				this.mc.displayGuiScreen(null);
			
			//--
			// PROJECTOR STUFF
			// --
			if (par1GuiButton.id >= 0 && par1GuiButton.id <= 10)
			{
				switch(par1GuiButton.id)
				{
				case 1:
					if (par1GuiButton.displayString.equals("project"))
						entity.project();
					else if (par1GuiButton.displayString.equals("pause") && !entity.canChangeState())
						entity.pauseTicking();
					else if (par1GuiButton.displayString.equals("stop"))
						entity.endProjectionGradually();
					break;
				case 2: 
					entity.refreshProjection();
					break;
				case 3: 
					offsetX.setNumber(offsetX.getNumber()+1);
					break;
				case 4:
					offsetX.setNumber(offsetX.getNumber()-1);
					break;
				case 5:
					offsetY.setNumber(offsetY.getNumber()+1);
					break;
				case 6:
					offsetY.setNumber(offsetY.getNumber()-1);
					break;
				case 7:
					offsetZ.setNumber(offsetZ.getNumber()+1);
					break;
				case 8:
					offsetZ.setNumber(offsetZ.getNumber()-1);
					break;
				case 9:
					entity = null;
					stack.stackTagCompound.setBoolean("projectorSynced", false);
					stack.stackTagCompound.setIntArray("projectorCoords", new int[] {});
				case 10:
					entity.unload();
				}
			}
		}
	}

	@Override
	public void mouseClicked(int par1, int par2, int par3)
	{
		super.mouseClicked(par1, par2, par3);
		offsetX.mouseClicked(par1, par2, par3);
		offsetY.mouseClicked(par1, par2, par3);
		offsetZ.mouseClicked(par1, par2, par3);
	}

	/**
	 * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
	 */
	@Override
	public void keyTyped(char par1, int par2)
	{
		//--
		// Projector stuff
		//--
		if (offsetX.getIsFocused())
		{
			offsetX.textboxKeyTyped(par1, par2);
		}
		else if (offsetY.getIsFocused())
		{
			offsetY.textboxKeyTyped(par1, par2);
		}
		else if (offsetZ.getIsFocused())
		{
			offsetZ.textboxKeyTyped(par1, par2);
		}
	}

	/**
	 * Draws the screen and all the components in it.
	 */
	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		this.drawDefaultBackground();

		// buttons and stuff
		super.drawScreen(par1, par2, par3);

		offsetX.drawNumberBox();
		offsetY.drawNumberBox();
		offsetZ.drawNumberBox();
		
		this.drawCenteredString(this.fontRenderer, "Projector Controls", this.width / 2 + 100, this.height / 4 - 20, 0xffffff);
		
		if (entity != null)
		{
			this.drawCenteredString(this.fontRenderer, "Projector at ("+entity.xCoord+", "+entity.yCoord+", "+entity.zCoord+") ", this.width / 2 + 100, this.height / 4 - 10, 0x7F7F7F);
			this.drawCenteredString(this.fontRenderer, entity.getLogString(), this.width / 2 + 100, this.height / 4, 0xffffff);
		}
		else
		{
			this.drawCenteredString(this.fontRenderer, "No Projector Paired", this.width / 2 + 100, this.height / 4- 10, 0x7F7F7F);
		}
	}

	/**
	 * Returns true if this GUI should pause the game when it is displayed in single-player
	 */
	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

}
