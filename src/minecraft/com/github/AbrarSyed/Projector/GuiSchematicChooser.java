package com.github.AbrarSyed.Projector;

import java.io.File;
import java.net.URI;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import net.minecraft.client.Minecraft;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.GuiSmallButton;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.StringTranslate;

import org.lwjgl.Sys;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.PacketDispatcher;

public class GuiSchematicChooser extends GuiScreen
{
	FileSystem system;
	
	ItemStack schematic;
	GuiScreen parent;
	private int selectedIndex = 0;
	GuiSchematicSlot slots;
    private int refreshTimer = -1;
    GuiButton done;
	
    public GuiSchematicChooser(ItemStack schematic, GuiScreen parent, FileSystem system)
    {
    	this.schematic = schematic;
    	this.parent = parent;
    	this.system = system;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    @Override
    public void initGui()
    {
        StringTranslate var1 = StringTranslate.getInstance();
        this.controlList.add(new GuiSmallButton(5, this.width / 2 - 154, this.height - 48, "Open Schematics Folder"));
        done = new GuiSmallButton(6, this.width / 2 + 4, this.height - 48, var1.translateKey("gui.done"));
        this.controlList.add(done);
        if (parent != null)
        	done.displayString = var1.translateKey("gui.back");
        
        slots = new GuiSchematicSlot(this);
        slots.registerScrollButtons(this.controlList, 7, 8);
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    @Override
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        if (par1GuiButton.enabled)
        {
            if (par1GuiButton.id == 5)
            {
                boolean var2 = false;

                try
                {
                    Class var3 = Class.forName("java.awt.Desktop");
                    Object var4 = var3.getMethod("getDesktop", new Class[0]).invoke((Object)null, new Object[0]);
                    var3.getMethod("browse", new Class[] {URI.class}).invoke(var4, new Object[] {(new File(Minecraft.getMinecraftDir(), "schematics")).toURI()});
                }
                catch (Throwable var5)
                {
                    var5.printStackTrace();
                    var2 = true;
                }

                if (var2)
                {
                    System.out.println("Opening via Sys class!");
                    Sys.openURL("file://" + Schematic.SCHEM_DIR);
                }
            }
            // DONE!
            else if (par1GuiButton.id == 6)
            {
            	if (selectedIndex == 0 || system.getFileList().get(this.selectedIndex-1) == null)
            	{
            		schematic.setTagCompound(null);
            		PacketDispatcher.sendPacketToServer(new PacketSchematicFile("", mc.thePlayer));
            	}
            	else if (schematic.hasTagCompound())
            	{
            		schematic.stackTagCompound.setString("schematic", (system.getFileList().get(this.selectedIndex-1)).getName());
            		PacketDispatcher.sendPacketToServer(new PacketSchematicFile(schematic.stackTagCompound.getString("schematic"), mc.thePlayer));
            	}
            	else
            	{
            		NBTTagCompound tag = new NBTTagCompound();
            		tag.setString("schematic", (system.getFileList().get(this.selectedIndex-1)).getName());
            		schematic.setTagCompound(tag);
            		PacketDispatcher.sendPacketToServer(new PacketSchematicFile(schematic.stackTagCompound.getString("schematic"), mc.thePlayer));
            	}
            	
                this.mc.displayGuiScreen(parent);
            }
        }
    }
    
    public void clickDone()
    {
    	if (parent != null && parent instanceof GuiSchematicChooser)
    	{
    		((GuiSchematicChooser) parent).clickDone();
    		return;
    	}
    	
    	this.actionPerformed(done);
    }
    
    public int getSelectedIndex()
    {
    	return selectedIndex;
    }
    
    public void setSelectedIndex(int num)
    {
    	selectedIndex = num;
    }

    /**
     * Called when the mouse is clicked.
     */
    @Override
    protected void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);
    }
    
    /**
     * Called from the main game loop to update the screen.
     */
    @Override
    public void updateScreen()
    {
        super.updateScreen();
        --this.refreshTimer;
    }

    
    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void drawScreen(int par1, int par2, float par3)
    {
    	slots.drawScreen(par1, par2, par3);
    	
    	if (system.getFileList().size() < 1)
    		this.drawCenteredString(this.fontRenderer, "The schematics folder is empty...", this.width / 2, this.height / 2 - 50, 8421504);

        if (this.refreshTimer <= 0)
        {
        	// reuqest refresh?
            //ProjectorAPI.updateFileList();
            this.refreshTimer += 20;
        }
    	
        this.drawCenteredString(this.fontRenderer, "Select a Schematic", this.width / 2, 16, 16777215);
        this.drawCenteredString(this.fontRenderer, "(Place schematic files here)", this.width / 2 - 77, this.height - 26, 8421504);
        super.drawScreen(par1, par2, par3);
    }
    
    public static FontRenderer getFontRenderrer(GuiSchematicChooser parent)
    {
    	return parent.fontRenderer;
    }
}
