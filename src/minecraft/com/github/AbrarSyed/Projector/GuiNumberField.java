package com.github.AbrarSyed.Projector;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.src.ChatAllowedCharacters;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.Gui;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.GuiTextField;
import net.minecraft.src.Tessellator;

public class GuiNumberField extends Gui
{
	private int xPos;
	private int yPos;
	private int width;
	private int height;

	public int minValue;
	public int maxValue;

	private int currentValue;

	private final FontRenderer fontRenderer;

	/** Have the current text beign edited on the textbox. */
	private String text = "";
	private int maxStringLength = 32;
	private int cursorCounter;

	/**
	 * if true the textbox can lose focus by clicking elsewhere on the screen
	 */
	private boolean canLoseFocus = true;

	/**
	 * If this value is true along isEnabled, keyTyped will process the keys.
	 */
	private boolean isFocused = false;

	/**
	 * If this value is true along isFocused, keyTyped will process the keys.
	 */
	private boolean isEnabled = true;
	// selection left
	private int field_50041_n = 0;

	private int cursorPosition = 0;

	// selection end
	private int selectionEnd = 0;

	private int enabledColor = 14737632;
	private int disabledColor = 7368816;

	public GuiNumberField(FontRenderer par1FontRenderer, int par2, int par3, int par4, int par5)
	{
		super();
		isEnabled = true;
		fontRenderer = par1FontRenderer;
		this.xPos = par2;
		this.yPos = par3;
		this.width = par4;
		this.height = par5;
		this.minValue = Integer.MIN_VALUE;
		this.maxValue = Integer.MAX_VALUE;
		currentValue = 0;
	}

	public GuiNumberField(FontRenderer par1FontRenderer, int par2, int par3, int par4, int par5, int min, int max)
	{
		super();
		isEnabled = true;
		fontRenderer = par1FontRenderer;
		this.xPos = par2;
		this.yPos = par3;
		this.width = par4;
		this.height = par5;
		this.minValue = min;
		this.maxValue = max;
		currentValue = 0;
	}

	public void drawNumberBox()
	{     
		if (isEnabled)
		{
			drawRect(this.xPos - 1, this.yPos - 1, this.xPos + this.width + 1, this.yPos + this.height + 1, -6250336);
			drawRect(this.xPos, this.yPos, this.xPos + this.width, this.yPos + this.height, -16777216);
		}

		int color = this.isEnabled ? this.enabledColor : this.disabledColor;
		int var2 = this.cursorPosition - this.field_50041_n;
		int var3 = this.selectionEnd - this.field_50041_n;
		String string = this.fontRenderer.trimStringToWidth(this.text.substring(this.field_50041_n), this.getWidth());
		boolean var5 = var2 >= 0 && var2 <= string.length();
		boolean var6 = this.isFocused && (this.cursorCounter / 6 % 2 == 0) && var5;
		int var7 = this.xPos + 4;
		int var8 = this.yPos + (this.height - 8) / 2;
		int var9 = var7;

		if (var3 > string.length())
		{
			var3 = string.length();
		}

		if (string.length() > 0)
		{
			String var10 = var5 ? string.substring(0, var2) : string;
			var9 = this.fontRenderer.drawStringWithShadow(var10, var7, var8, color);
		}

		boolean var13 = this.cursorPosition < this.text.length() || this.text.length() >= this.setMaxLength();
		int var11 = var9;

		if (!var5)
		{
			var11 = var2 > 0 ? var7 + this.width : var7;
		}
		else if (var13)
		{
			var11 = var9 - 1;
			--var9;
		}

		if (string.length() > 0 && var5 && var2 < string.length())
		{
			this.fontRenderer.drawStringWithShadow(string.substring(var2), var9, var8, color);
		}

		if (var6)
		{
			if (var13)
			{
				Gui.drawRect(var11, var8 - 1, var11 + 1, var8 + 1 + this.fontRenderer.FONT_HEIGHT, -3092272);
			}
			else
			{
				this.fontRenderer.drawStringWithShadow("_", var11, var8, color);
			}
		}

		if (var3 != var2)
		{
			int var12 = var7 + this.fontRenderer.getStringWidth(string.substring(0, var3));
			this.drawPointerVertical(var11, var8 - 1, var12 - 1, var8 + 1 + this.fontRenderer.FONT_HEIGHT);
		}
	}

	private void drawPointerVertical(int par1, int par2, int par3, int par4)
	{
		int var5;

		if (par1 < par3)
		{
			var5 = par1;
			par1 = par3;
			par3 = var5;
		}

		if (par2 < par4)
		{
			var5 = par2;
			par2 = par4;
			par4 = var5;
		}

		Tessellator var6 = Tessellator.instance;
		GL11.glColor4f(0.0F, 0.0F, 255.0F, 255.0F);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_COLOR_LOGIC_OP);
		GL11.glLogicOp(GL11.GL_OR_REVERSE);
		var6.startDrawingQuads();
		var6.addVertex((double)par1, (double)par4, 0.0D);
		var6.addVertex((double)par3, (double)par4, 0.0D);
		var6.addVertex((double)par3, (double)par2, 0.0D);
		var6.addVertex((double)par1, (double)par2, 0.0D);
		var6.draw();
		GL11.glDisable(GL11.GL_COLOR_LOGIC_OP);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public boolean textboxKeyTyped(char par1, int par2)
	{

		if (this.isEnabled && this.getIsFocused())
		{
			switch (par1)
			{
			case 1: //CTRL A - select all
				this.resetCursorPositionToMax();
				this.setSelectionEnd(0);
			return true;
			case 3: // Control C - copy
				GuiScreen.setClipboardString(this.getSelectedText());
				return true;
			case 22: // control V - paste
				this.writeText(GuiScreen.getClipboardString());
				return true;
			case 24: // Control X - cut
				GuiScreen.setClipboardString(this.getSelectedText());
				this.writeText("");
				return true;
			default:
				switch (par2)
				{
				case 14: //backspace
				if (GuiScreen.isCtrlKeyDown())
				{
					this.deleteToNextNthWord(-1);
				}
				else
				{
					this.deleteFromCursor(-1);
				}

				return true;
				case 199: //home
					if (GuiScreen.isShiftKeyDown())
					{
						this.setSelectionEnd(0);
					}
					else
					{
						this.resetCursorPositionToZero();
					}

					return true;
				case 203: //left
					if (GuiScreen.isShiftKeyDown())
					{
						if (GuiScreen.isCtrlKeyDown())
						{
							this.setSelectionEnd(this.getNthWordFromPos(-1, this.getSelectionEnd()));
						}
						else
						{
							this.setSelectionEnd(this.getSelectionEnd() - 1);
						}
					}
					else if (GuiScreen.isCtrlKeyDown())
					{
						this.setCursorPosition(this.getNthWordFromCursor(-1));
					}
					else
					{
						this.setCursorPositionFromSelection(-1);
					}

					return true;
				case 205: //right
					if (GuiScreen.isShiftKeyDown())
					{
						if (GuiScreen.isCtrlKeyDown())
						{
							this.setSelectionEnd(this.getNthWordFromPos(1, this.getSelectionEnd()));
						}
						else
						{
							this.setSelectionEnd(this.getSelectionEnd() + 1);
						}
					}
					else if (GuiScreen.isCtrlKeyDown())
					{
						this.setCursorPosition(this.getNthWordFromCursor(1));
					}
					else
					{
						this.setCursorPositionFromSelection(1);
					}

					return true;
				case 207: //end
					if (GuiScreen.isShiftKeyDown())
					{
						this.setSelectionEnd(this.text.length());
					}
					else
					{
						this.resetCursorPositionToMax();
					}

					return true;
				case 211: //delete
					if (GuiScreen.isCtrlKeyDown())
					{
						this.deleteToNextNthWord(1);
					}
					else
					{
						this.deleteFromCursor(1);
					}

					return true;
                default:
                    if (ChatAllowedCharacters.isAllowedCharacter(par1))
                    {
                        this.writeText(Character.toString(par1));
                        return true;
                    }
                    else
                    {
                        return false;
                    }
				}
			}
		}
		else
		{
			return false;
		}
	}

	/**
	 * Args: x, y, buttonClicked
	 */
	public void mouseClicked(int par1, int par2, int par3)
	{         
		boolean clickedInBox = (par1 >= this.xPos) && par1 < this.xPos + this.width && par2 >= this.yPos && par2 < this.yPos + this.height;

		if (this.canLoseFocus)
		{
			this.setFocused(this.isEnabled && clickedInBox);
		}

		if (this.isFocused && par3 == 0)
		{
			int mouseX = par1 - this.xPos;

			if (this.isEnabled)
			{
				// move the x over a bit
				mouseX -= 4;
			}

			String var6 = this.fontRenderer.trimStringToWidth(this.text.substring(this.field_50041_n), this.getWidth());
			this.setCursorPosition(this.fontRenderer.trimStringToWidth(var6, mouseX).length() + this.field_50041_n);
		}
	}


	//getSelectedText
	public String getSelectedText()
	{
		int var1 = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
		int var2 = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
		return this.text.substring(var1, var2);
	}

	// writeText - inserts text at cursor, replacing the selection if any
	public void writeText(String par1Str)
	{
		String var2 = "";
		String var3 = ChatAllowedCharacters.filerAllowedCharacters(par1Str);
		// min
		// whichever is lower, cursor Position or selection P ?
		int var4 = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
		// max
		int var5 = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
		int var6 = this.maxStringLength - this.text.length() - (var4 - this.selectionEnd);
		boolean var7 = false;

		// Insert the text as the right position
		if (this.text.length() > 0)
		{
			var2 = var2 + this.text.substring(0, var4);
		}

		int var8;

		if (var6 < var3.length())
		{
			var2 = var2 + var3.substring(0, var6);
			var8 = var6;
		}
		else
		{
			var2 = var2 + var3;
			var8 = var3.length();
		}

		if (this.text.length() > 0 && var5 < this.text.length())
		{
			var2 = var2 + this.text.substring(var5);
		}

		// Limit text values to numbers or the text "-"
		try {
			int va = Integer.parseInt(var2);
			if (va > maxValue)
			{
				va = maxValue;
				var2 = Integer.toString(maxValue);
			}
			if (va < minValue)
			{
				va = minValue;
				var2 = Integer.toString(minValue);
			}
			this.currentValue = va;
		} catch(NumberFormatException e) {
			if ( var2 != "-")
			{
				return; // Deny
			}
			// they typed a minus sign, so ignore it and let it pass
		}
		this.text = var2;
		this.setCursorPositionFromSelection(var4 - this.selectionEnd + var8);
	}

	// something
	// if called with a -1
	//deleteToNextNthWord - N can be positive (ctrl+del) OR negative (ctrl+bksp)
	public void deleteToNextNthWord(int par1)
	{
		if (this.text.length() != 0)
		{
			// gotta replace something
			if (this.selectionEnd != this.cursorPosition)
			{
				// empty it
				this.writeText("");
			}
			else
			{
				// otherwise....
				this.deleteFromCursor(this.getNthWordFromCursor(par1) - this.cursorPosition);
			}
		}
	}

	//something
	//deleteFromCursor - deletes the selected text if any, otherwise deletes n characters to either direction from cursor
	public void deleteFromCursor(int par1)
	{
		if (this.text.length() != 0)
		{
			if (this.selectionEnd != this.cursorPosition)
			{
				this.writeText("");
			}
			else
			{
				boolean var2 = par1 < 0;
				int var3 = var2 ? this.cursorPosition + par1 : this.cursorPosition;
				int var4 = var2 ? this.cursorPosition : this.cursorPosition + par1;
				String var5 = "";

				if (var3 >= 0)
				{
					var5 = this.text.substring(0, var3);
				}

				if (var4 < this.text.length())
				{
					var5 = var5 + this.text.substring(var4);
				}

				this.text = var5;

				if (var2)
				{
					this.setCursorPositionFromSelection(par1);
				}
			}
		}
	}

	// get selection Right side (end) 
	//getSelectionEnd
	public int getSelectionEnd()
	{
		return this.selectionEnd;
	}

	//something 
	//getNthNextWordFromCursor - see @getNthNextWordFromPos()
	//func_50028_c
	/**
	 * 
	 * @param par1
	 * @return
	 */
	public int getNthWordFromCursor(int par1)
	{
		return this.getNthWordFromPos(par1, this.getCursorPosition());
	}


	// func_50024_a
	/**
	 * gets the position of the nth word. N may be negative, then it looks backwards.
	 * @param par1 N
	 * @param par2 position
	 * @return position of nth word
	 */
	public int getNthWordFromPos(int par1, int par2)
	{
		int var3 = par2;
		boolean var4 = par1 < 0;
		int var5 = Math.abs(par1);

		for (int var6 = 0; var6 < var5; ++var6)
		{
			if (var4)
			{
				while (var3 > 0 && this.text.charAt(var3 - 1) == 32)
				{
					--var3;
				}

				while (var3 > 0 && this.text.charAt(var3 - 1) != 32)
				{
					--var3;
				}
			}
			else
			{
				int var7 = this.text.length();
				var3 = this.text.indexOf(32, var3);

				if (var3 == -1)
				{
					var3 = var7;
				}
				else
				{
					while (var3 < var7 && this.text.charAt(var3) == 32)
					{
						++var3;
					}
				}
			}
		}

		return var3;
	}

	public void setSelectionEnd(int par1)
	{
		int var2 = this.text.length();

		if (par1 > var2)
		{
			par1 = var2;
		}

		if (par1 < 0)
		{
			par1 = 0;
		}

		this.selectionEnd = par1;

		if (this.fontRenderer != null)
		{
			if (this.field_50041_n > var2)
			{
				this.field_50041_n = var2;
			}

			int var3 = this.getWidth();
			String var4 = this.fontRenderer.trimStringToWidth(this.text.substring(this.field_50041_n), var3);
			int var5 = var4.length() + this.field_50041_n;

			if (par1 == this.field_50041_n)
			{
				this.field_50041_n -= this.fontRenderer.trimStringToWidth(this.text, var3, true).length();
			}

			if (par1 > var5)
			{
				this.field_50041_n += par1 - var5;
			}
			else if (par1 <= this.field_50041_n)
			{
				this.field_50041_n -= this.field_50041_n - par1;
			}

			if (this.field_50041_n < 0)
			{
				this.field_50041_n = 0;
			}

			if (this.field_50041_n > var2)
			{
				this.field_50041_n = var2;
			}
		}
	}

	// note that this moves the cursor relative to the other end of the selection, a bug
	public void setCursorPositionFromSelection(int par1)
	{
		this.setCursorPosition(this.selectionEnd + par1);
	}

	// STUFF BEYOND HERE IS FIGURED OUT
	// FIGURED

	public void setCursorPosition(int par1)
	{
		this.cursorPosition = par1;
		int var2 = this.text.length();

		if (this.cursorPosition < 0)
		{
			this.cursorPosition = 0;
		}

		if (this.cursorPosition > var2)
		{
			this.cursorPosition = var2;
		}

		this.setSelectionEnd(this.cursorPosition);
	}


	public void resetCursorPositionToZero()
	{
		this.setCursorPosition(0);
	}

	public void resetCursorPositionToMax()
	{
		this.setCursorPosition(this.text.length());
	}

	public int getCursorPosition()
	{
		return this.cursorPosition;
	}

	public boolean getIsFocused()
	{
		return isFocused;
	}

	private int setMaxLength()
	{
		return this.maxStringLength;
	}

	/**
	 * @return returns the width of the field
	 */
	 public int getWidth()
	{
		return this.isEnabled ? this.width - 8 : this.width;
	}

	/**
	 * if true the textbox can lose focus by clicking elsewhere on the screen
	 */
	 public void setCanLoseFocus(boolean par1)
	 {
		 this.canLoseFocus = par1;
	 }

	 public void setFocused(boolean b)
	 {
		 isFocused = b;
	 }

	 /**
	  * @return the isEnabled
	  */
	 public boolean isEnabled()
	 {
		 return isEnabled;
	 }

	 /**
	  * @param isEnabled the isEnabled to set
	  */
	 public void setEnabled(boolean isEnabled)
	 {
		 this.isEnabled = isEnabled;
	 }

	 public int getNumber()
	 {
		 return currentValue;
	 }
	 
	 public void setNumber(int i)
	 {
		 if (this.maxValue >= i  && i >= this.minValue)
		 {
			 this.currentValue = i;
			 this.text = this.fontRenderer.trimStringToWidth(currentValue+"", this.getWidth());
		 }
	 }

 	public void setMaxStringLength(int i)
	{
		this.maxStringLength = i;	
	}
}