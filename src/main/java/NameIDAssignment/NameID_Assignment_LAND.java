package NameIDAssignment;

import Simulator.SkipSimParameters;

import java.util.Random;


public class NameID_Assignment_LAND extends NameIDAssignment
{
    @Override
    public void reset()
    {
        nameSpace = new String[SkipSimParameters.getSystemCapacity()];
        nameSpaceIndex = 0;
        initializationLock = true;
    }


    public String randomizedAssingment(int nindex)
    {
        //System.out.print("LAND");
        if (initializationLock)
        {
            nameIDGenerator(SkipSimParameters.getLandmarksNum());
            initializationLock = false;
        }
        Random random = new Random();
        int index = random.nextInt(SkipSimParameters.getSystemCapacity() - 1);

        int counter = 0;
        while (nameSpace[index] == null && counter < 100)
        {
            index = random.nextInt(SkipSimParameters.getSystemCapacity() - 1);
            counter++;
        }
        if (counter >= 100) for (int i = 0; i < SkipSimParameters.getSystemCapacity(); i++)
        {
            if (nameSpace[i] != null)
            {
                index = i;
                break;
            }

        }

        String nameID = nameSpace[index];
        nameSpace[index] = null;
        if (SkipSimParameters.isStaticSimulation() && nindex == SkipSimParameters.getSystemCapacity() - 1)
        {
            initializationLock = true;
            reset();
        }
        return nameID;
    }
}