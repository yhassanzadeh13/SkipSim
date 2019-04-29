package SkipGraph;

import DataTypes.Constants;
import Simulator.SkipSimParameters;

public abstract class SkipGraphNode
{
    private int introducer;
    protected int index;
    /**
     * Numerical ID of the Nodes, non-negative integers
     */
    protected int numID;

    /**
     * name ID of the Nodes
     */
    protected String nameID;
    /**
     * The lookup table of the Nodes
     * lookup[i][0] means level i and left direction,
     * lookup[i][1] means level i and right direction
     */
    protected int[][] lookup;

    public SkipGraphNode()
    {
        nameID = new String();
        //lookup = new int[system.getLookupTableSize()][2];
    }

    public int getIndex()
    {
        return index;
    }

    public void setIndex(int index)
    {
        this.index = index;
    }

    public int getIntroducer()
    {
        return introducer;
    }
    public void setIntroducer(int index)
    {
        this.introducer = index;
    }

    public int getNumID()
    {
        return numID;
    }

    public void setNumID(int numID)
    {
        this.numID = numID;
    }

    public int getLookup(int i, int j)
    {
        return lookup[i][j];
    }

    /**
     * @param i       level number
     * @param j       0: left neighbor and 1: right neighbor
     * @param address address of the neighbor to be inserted in the lookup table
     */
    public void setLookup(int i, int j, int address)
    {
        if (SkipSimParameters.getSimulationType().equalsIgnoreCase(Constants.SimulationType.DYNAMIC)
                || SkipSimParameters.getSimulationType().equalsIgnoreCase(Constants.SimulationType.BLOCKCHAIN))
        {
            /*
            Preventing self loops in dynamic simulation
             */
            if (address == index)
            {
                lookup[i][j] = -1;
                return;
            }
        }

        lookup[i][j] = address;
    }

    public String getNameID()
    {
        return nameID;
    }

    public void setNameID(String nameID)
    {
        this.nameID = nameID;
    }

    /**
     * @return false if at least one entry is not equal to -1, otherwise returns true
     */
    public boolean isLookupTableEmpty(int lookupTableSize)
    {
        if(lookup == null)
            return true;

        for (int i = lookupTableSize - 1; i >= 0; i--)
            for (int j = 0; j < 2; j++)
                if (lookup[i][j] != -1)
                {
                    return false;
                }
        return true;

    }

    /**
     * Prints the lookup table of the Node
     */
    public void printLookup()
    {
        for (int i = SkipSimParameters.getLookupTableSize() - 1; i >= 0; i--)
            System.out.println("Level: " + i + "   Left: " + lookup[i][0] + "   Right: " + lookup[i][1]);
    }

    @Override
    public String toString()
    {
        return "SkipGraphNode: Index =  " + index + " Name ID = " + nameID  + " Numerical ID = " + numID ;
    }
}
