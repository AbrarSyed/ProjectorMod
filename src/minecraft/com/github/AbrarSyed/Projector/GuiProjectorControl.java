package com.github.AbrarSyed.Projector;

import java.io.File;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.network.PacketDispatcher;

import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.GuiTextField;
import net.minecraft.src.ModLoader;

public class GuiProjectorControl extends GuiScreen
{
	TileEntityProjector entity;
	GuiNumberField offsetX;
	GuiNumberField offsetY;
	GuiNumberField offsetZ;

	public GuiProjectorControl(TileEntityProjector entity)
	{
		super();
		this.entity = entity;
	}
	
	@Override
    public void initGui()
    {
        this.controlList.clear();
        // done button
        this.controlList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120, "Done"));
        // load button
        this.controlList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 90, "Load/Unload Schematic"));
        
        // project, stop, and refresh buttons
        this.controlList.add(new GuiButton(2, this.width / 2 + 30, this.height / 4 , 80, 20, "Project"));
        this.controlList.add(new GuiButton(3, this.width / 2 + 30, this.height / 4 + 30, 80, 20, "Refresh"));
        
        if (entity.getOffsets() == null)
        	entity.setOffsets(new int[] {0, 2, 0});
        
        // offset X buttons and feild
        offsetX = new GuiNumberField(this.fontRenderer, this.width / 2 - 70, this.height / 4 - 20, 40, 20);
        offsetX.setFocused(false);
        offsetX.setNumber(entity.getOffsets()[0]);
        this.controlList.add(new GuiButton(4, this.width / 2 - 90, this.height / 4 - 20, 20, 20, "X+"));
        this.controlList.add(new GuiButton(5, this.width / 2 - 30, this.height / 4 - 20, 20, 20, "-X"));
        
        // offset Y buttons and feild
        offsetY = new GuiNumberField(this.fontRenderer, this.width / 2 - 70, this.height / 4 + 5, 40, 20);
        offsetY.setFocused(false);
        offsetY.setNumber(entity.getOffsets()[1]);
        this.controlList.add(new GuiButton(6, this.width / 2 - 90, this.height / 4 + 5, 20, 20, "Y+"));
        this.controlList.add(new GuiButton(7, this.width / 2 - 30, this.height / 4 + 5, 20, 20, "-Y"));
        
        // offset Z buttons and feild
        offsetZ = new GuiNumberField(this.fontRenderer, this.width / 2 - 70, this.height / 4 + 30, 40, 20);
        offsetZ.setFocused(false);
        offsetZ.setNumber(entity.getOffsets()[2]);
        this.controlList.add(new GuiButton(8, this.width / 2 - 90, this.height / 4 + 30, 20, 20, "Z+"));
        this.controlList.add(new GuiButton(90, this.width / 2 - 30, this.height / 4 + 30, 20, 20, "-Z"));
    }
	
	public void updateScreen()
	{
        if (entity.isProjecting())
        {
        	((GuiButton) this.controlList.get(3)).enabled = true;
        	
        	offsetX.setEnabled(false);
        	offsetY.setEnabled(false);
        	offsetZ.setEnabled(false);
        	for (int i = 4; i < 10; i++)
        		((GuiButton) this.controlList.get(i)).enabled = false;
        	
        	((GuiButton) this.controlList.get(2)).displayString = "stop"; 
        	((GuiButton) this.controlList.get(2)).enabled = true;
        }
        else
        {
    		((GuiButton) this.controlList.get(3)).enabled = false;
    		
        	offsetX.setEnabled(true);
        	offsetY.setEnabled(true);
        	offsetZ.setEnabled(true);
        	for (int i = 4; i < 10; i++)
        		((GuiButton) this.controlList.get(i)).enabled = true;
        	
        	((GuiButton) this.controlList.get(2)).displayString = "project"; 
        	((GuiButton) this.controlList.get(2)).enabled = true;
        }
        
        if (!entity.canChangeState())
        {
        	((GuiButton) this.controlList.get(3)).enabled = false;
        	
        	offsetX.setEnabled(false);
        	offsetY.setEnabled(false);
        	offsetZ.setEnabled(false);
        	
        	for (int i = 4; i < 10; i++)
        		((GuiButton) this.controlList.get(i)).enabled = false;
        	
        	((GuiButton) this.controlList.get(2)).displayString = "pause"; 
        	((GuiButton) this.controlList.get(2)).enabled = true;
        }
        
        if (entity.getLoadedSchematic() == null || !entity.getLoadedSchematic().hasTagCompound() || entity.getLoadedSchematic().stackTagCompound.getString("schematic") == null)
        {
        	((GuiButton) this.controlList.get(3)).enabled = false;
        	
        	offsetX.setEnabled(true);
        	offsetY.setEnabled(true);
        	offsetZ.setEnabled(true);
        	
        	for (int i = 4; i < 10; i++)
        		((GuiButton) this.controlList.get(i)).enabled = true;
        	
        	((GuiButton) this.controlList.get(2)).displayString = "project";
        	((GuiButton) this.controlList.get(2)).enabled = true;
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
        	switch(par1GuiButton.id)
        	{
        	case 0: this.mc.displayGuiScreen(null); break;
        	case 1:
        		PacketDispatcher.sendPacketToServer(new PacketOpenGui(0, entity.xCoord, entity.yCoord, entity.zCoord));
        		break;
        	case 2:
				if (par1GuiButton.displayString.equals("project"))
				{
					entity.project();
					PacketDispatcher.sendPacketToServer(new PacketProjectorControl(1, entity.xCoord, entity.yCoord, entity.zCoord));
				}
				else if (par1GuiButton.displayString.equals("pause") && !entity.canChangeState())
				{
					entity.pauseTicking();
					PacketDispatcher.sendPacketToServer(new PacketProjectorControl(2, entity.xCoord, entity.yCoord, entity.zCoord));
				}
				else if (par1GuiButton.displayString.equals("stop"))
				{
					entity.endProjectionGradually();
					PacketDispatcher.sendPacketToServer(new PacketProjectorControl(3, entity.xCoord, entity.yCoord, entity.zCoord));
				}
				break;
        	case 3: 
        		entity.refreshProjection();
        		PacketDispatcher.sendPacketToServer(new PacketProjectorControl(4, entity.xCoord, entity.yCoord, entity.zCoord));
        		break;
        	case 4: 
        		offsetX.setNumber(offsetX.getNumber()+1);
        		break;
        	case 5:
        		offsetX.setNumber(offsetX.getNumber()-1);
        		break;
        	case 6:
        		offsetY.setNumber(offsetY.getNumber()+1);
        		break;
        	case 7:
        		offsetY.setNumber(offsetY.getNumber()-1);
        		break;
        	case 8:
        		offsetZ.setNumber(offsetZ.getNumber()+1);
        		break;
        	case 9:
        		offsetZ.setNumber(offsetZ.getNumber()-1);
        		break;
        	}
        }
        
        if (offsetX.getNumber() != entity.getOffsets()[0])
        {
        	entity.setOffsets(new int[] {offsetX.getNumber(), entity.getOffsets()[1], entity.getOffsets()[2]});
        	PacketDispatcher.sendPacketToServer(new PacketProjectorControl(entity.xCoord, entity.yCoord, entity.zCoord,offsetX.getNumber(), entity.getOffsets()[1], entity.getOffsets()[2]));
        }
        
        if (offsetY.getNumber() != entity.getOffsets()[1])
        {
        	entity.setOffsets(new int[] {entity.getOffsets()[0], offsetY.getNumber(), entity.getOffsets()[2]});
        	PacketDispatcher.sendPacketToServer(new PacketProjectorControl(entity.xCoord, entity.yCoord, entity.zCoord, entity.getOffsets()[0], offsetY.getNumber(), entity.getOffsets()[2]));
        }
        
        if (offsetZ.getNumber() != entity.getOffsets()[2])
        {
        	entity.setOffsets(new int[] {entity.getOffsets()[0], entity.getOffsets()[1], offsetZ.getNumber()});
        	PacketDispatcher.sendPacketToServer(new PacketProjectorControl(entity.xCoord, entity.yCoord, entity.zCoord, entity.getOffsets()[0], entity.getOffsets()[1], offsetZ.getNumber()));
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
        
        entity.setOffsets(new int[] {offsetX.getNumber(), offsetY.getNumber(), offsetZ.getNumber()});
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
        
        this.drawString(this.fontRenderer, "ProjectorLog : "+entity.getLogString(), this.width / 2 - 100, this.height / 4 + 70, 0xffffff);
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
