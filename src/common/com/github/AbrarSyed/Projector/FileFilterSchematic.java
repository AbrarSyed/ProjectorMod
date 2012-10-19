package com.github.AbrarSyed.Projector;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class FileFilterSchematic extends FileFilter implements java.io.FileFilter
{

	@Override
	public boolean accept(File f)
	{
		if (f.isDirectory())
			return true;
		String file = f.getPath().toLowerCase();
		if (file.endsWith(".schematic") || file.endsWith(".mcs2"))
			return true;
		return false;
	}

	@Override
	public String getDescription()
	{
		return "Schematic files (*.schematic, *.mcs2)";
	}
	
}