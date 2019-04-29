package NameIDAssignment;

import DataTypes.Constants;
import Simulator.SkipSimParameters;
import SkipGraph.Node;
import SkipGraph.SkipGraphOperations;

public class NameID_Assignment_DPLMDS extends NameIDAssignment
{

    @Override
    public void reset()
    {
        adj = new double[SkipSimParameters.getSystemCapacity()][SkipSimParameters.getSystemCapacity()];
        LMDS = new double[SkipSimParameters.getSystemCapacity()];
        nameSpace = new String[SkipSimParameters.getSystemCapacity()];
        nameSpaceIndex = 0;
    }

    @Override
    public void nameIDGeneration(SkipGraphOperations fakesgo)
    {

        for (int i = 0; i < sgo.getTG().mNodeSet.nodeLength(); i++)
        {
            int nodeIndex = findMin();
            int prefix = ClosestLandmark((Node) sgo.getTG().mNodeSet.getNode(i));
            ((Node) sgo.getTG().mNodeSet.getNode(i)).setNameID(sgo.getTG().mLandmarks.getDynamicPrefix(prefix) + nameSpace[i]);
            //System.out.println("The name ID of the SkipGraph.Node " + i + "  is " + sgo.getTG().mNodeSet.getNode(i).nameID);
        }
    }


    @Override
    public String Algorithm(Node n, SkipGraphOperations sg)
    {
        sgo = sg;
        makingAdj();
        LMDS();
        //printADJ();
        //sg.getTG().mLandmarks.upDateDynamicPrefix();
        //new DataTypes.HuffmanCode().buildThePrefix(sg.getTG().mLandmarks.getFreq(), sg);
        sgo.getTG().mLandmarks.twoMeanClustringBasedOnPosition(new String(), -1);
        nameIDGenerator(SkipSimParameters.getNameIDLength());
        nameIDGeneration(sg);

        for (int i = 1; i < SkipSimParameters.getSystemCapacity(); i++)
        {
            //System.out.println(SkipGraph.Nodes.nodeSet[i].nameID);
            sg.insert((Node) sgo.getTG().mNodeSet.getNode(i), Constants.SkipGraphOperation.STATIC_SIMULATION_TIME, sgo.getTG().mNodeSet);
        }
        return new String();
    }
}
