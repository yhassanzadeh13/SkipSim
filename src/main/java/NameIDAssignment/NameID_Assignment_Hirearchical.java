package NameIDAssignment;

import Simulator.SkipSimParameters;
import SkipGraph.Node;
import SkipGraph.SkipGraphOperations;

import java.util.ArrayList;
import java.util.Random;

public class NameID_Assignment_Hirearchical extends NameIDAssignment
{
    private static ArrayList<String> generatedNameIDs;
    private static Random random;
    @Override
    public void reset()
    {
        nameSpace = new String[SkipSimParameters.getSystemCapacity()];
        nameSpaceIndex = 0;
        initializationLock = true;
    }


    public String RandomNameIDAssignment2(int closestLandmarkIndex, int nodeIndex)
    {

		int index;

        boolean flag = false;
        do
		{
            index = random.nextInt(SkipSimParameters.getSystemCapacity()-1);
		}while (generatedNameIDs.contains(sgo.getTG().mLandmarks.getDynamicPrefix(closestLandmarkIndex) + nameSpace[index]));

        nameID = sgo.getTG().mLandmarks.getDynamicPrefix(closestLandmarkIndex) + nameSpace[index];
        generatedNameIDs.add(nameID);
        //System.out.println(nameID + " " + sgo.getTG().mLandmarks.getDynamicPrefix(closestLandmarkIndex) + " " + nodeIndex);
        return nameID;
    }

    @Override
    public String Algorithm(Node n, SkipGraphOperations sg, int index)
    {
        sgo = sg;
        if (initializationLock)
        {
            //sgo.getTG().mLandmarks.upDateDynamicPrefix();
            //new DataTypes.HuffmanCode().buildThePrefix(sg.getTG().mLandmarks.getFreq(),sg);
            sgo.getTG().mLandmarks.twoMeanClustringBasedOnPosition(new String(), -1);
            initializationLock = false;
            nameIDGenerator(SkipSimParameters.getLandmarksNum());
            generatedNameIDs = new ArrayList<>();
            random = new Random();
        }

        String nameID = RandomNameIDAssignment2(ClosestLandmark(n), index);

        if (SkipSimParameters.isStaticSimulation() && index == SkipSimParameters.getSystemCapacity() - 1)
        {
            initializationLock = true;
            reset();
        }
        return nameID;
    }
}