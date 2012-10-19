package com.github.AbrarSyed.Projector;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.filechooser.FileFilter;

public class FileSystem implements Serializable
{
	private FileSystem father;
	private ArrayList<File> allFiles;
	private HashMap<File, FileSystem> directoryMap;

	public FileSystem(FileSystem father)
	{
		this.father = father;
		allFiles = new ArrayList<File>();
	}
	
	public FileSystem()
	{
		father = null;
		allFiles = new ArrayList<File>();
	}
	
	/**
	 * constructs the filesystem map. Hidden files and folders are ignored. This method searches all 
	 * @param file Path to a directory.
	 * @param filter a custom FileFilter. Null allows all files.
	 */
	public void load(File file, FileFilter filter)
	{
		if (!file.isDirectory() || !file.exists())
			throw new IllegalArgumentException("File must exist amd be a directory.");
		
		File[] files = Schematic.SCHEM_DIR.listFiles(new FileFilterSchematic());
		
		for (File read: files)
		{
			if (!read.exists() || read.isHidden())
				continue;
			allFiles.add(read);
			if (read.isDirectory())
			{
				FileSystem system = new FileSystem(this);
				system.load(read, filter);
				directoryMap.put(read, system);
			}
		}
	}
	
	/**
	 * @return a list of all the files and directories directories in this FileSystem. Not recursive.
	 */
	public ArrayList<File> getFileList()
	{
		return allFiles;
	}
	
	/**
	 * @return a list of the directories in this FileSystem. Not recursive.
	 */
	public ArrayList<File> getDirectoryList()
	{
		return new ArrayList<File>(directoryMap.keySet());
	}
	
	/**
	 * Gets the child FileSystem of a directory in this FileSystem's file list. Not Recursive.
	 * @param file File path of a directory that is in this FileSystem 
	 * @return The FileSystem instance of that directory's FileSystem instance.
	 */
	public FileSystem getDirSystem(File file)
	{
		return directoryMap.get(file);
	}
	
	/**
	 * 
	 * @param index Index number of the directory in the files list. Not recursive
	 * @return The FileSystem instance of that directory's FileSystem instance.
	 */
	public FileSystem getDirSystem(int index)
	{
		if (allFiles.size() > index)
			return directoryMap.get(allFiles.get(index));
		else
			return null;
	}
}
