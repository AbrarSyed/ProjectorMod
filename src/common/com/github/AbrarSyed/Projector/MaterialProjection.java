package com.github.AbrarSyed.Projector;

import net.minecraft.src.*;

import java.lang.reflect.Constructor;

public class MaterialProjection extends Material
{
    public MaterialProjection(MapColor par1MapColor)
    {
        super(par1MapColor);
        setNoPushMobility();
        setNoHarvest();
        this.setTranslucent();
    }

    @Override
    public boolean isSolid()
    {
        return false;
    }

    /**
     * Will prevent grass from growing on dirt underneath and kill any grass below it if it returns true
     */
    @Override
    public boolean getCanBlockGrass()
    {
        return false;
    }

    /**
     * Returns if this material is considered solid or not
     */
    @Override
    public boolean blocksMovement()
    {
        return false;
    }
    
    /**
     * makes the material translucent
     */
    public Material setTranslucent()
    {
        try {
			ModLoader.setPrivateValue(Material.class, this, "isTranslucent", new Boolean(true));
		}
        catch (Exception e)
		{
			System.out.println("DID NOT WORK");
		}
        return this;
    }
}