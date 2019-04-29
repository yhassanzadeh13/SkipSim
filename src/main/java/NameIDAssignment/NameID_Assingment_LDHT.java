package NameIDAssignment;

import Simulator.SkipSimParameters;
import SkipGraph.Node;
import SkipGraph.SkipGraphOperations;

public class NameID_Assingment_LDHT extends NameIDAssignment
{

    @Override
    public void reset()
    {
        nameSpace = new String[SkipSimParameters.getSystemCapacity()];
        nameSpaceIndex = 0;
        initializationLock = true;
    }


    public String RandomNameIDAssignment2(int closestLandmarkIndex, int nodeIndex)
    {
        String nameID = nameSpace[nodeIndex];
//		nameSpace[index] = null;
        String prefix = sgo.getTG().mLandmarks.getPrefix(closestLandmarkIndex).
                substring((int) (sgo.getTG().mLandmarks.getPrefix(closestLandmarkIndex).length() - Math.ceil(Math.log(SkipSimParameters.getLandmarksNum()))) - 1, sgo.getTG().mLandmarks.getPrefix(closestLandmarkIndex).length());
        nameID = prefix + nameID;
        System.out.println(nameID + " " + prefix + " " + nodeIndex);
        return nameID;
    }

    @Override
    public String Algorithm(Node n, SkipGraphOperations sg, int index)
    {
        sgo = sg;
        if (initializationLock)
        {
            nameIDGenerator(SkipSimParameters.getLandmarksNum());
            initializationLock = false;
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