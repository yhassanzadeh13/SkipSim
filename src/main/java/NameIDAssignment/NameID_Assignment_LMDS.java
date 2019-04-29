package NameIDAssignment;

import DataTypes.Constants;
import Simulator.SkipSimParameters;
import SkipGraph.Node;
import SkipGraph.SkipGraphOperations;

public class NameID_Assignment_LMDS extends NameIDAssignment
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
    public void nameIDGeneration(SkipGraphOperations fakeSgo)
    {
        for (int i = 0; i < nameSpace.length; i++)
        {
            int nodeIndex = findMin();
            sgo.getTG().mNodeSet.getNode(nodeIndex).setNameID(nameSpace[i]);
            //System.out.println("NameID for SkipGraph.Node " + " " + nodeIndex + " is = " + SkipGraph.Nodes.nodeSet[nodeIndex].nameID);
        }
    }

    @Override
    public String Algorithm(Node fakeNode, SkipGraphOperations sg)
    {

        sgo = sg;
        makingAdj();
        LMDS();
        //printADJ();
        nameIDGenerator(SkipSimParameters.getNameIDLength());
        nameIDGeneration(sg);

        for (int i = 1; i < SkipSimParameters.getSystemCapacity(); i++)
        {
            //System.out.println(i + " " + sgo.getTG().mNodeSet.getNode(i).nameID + " " + sgo.getTG().mNodeSet.getNode(i).getNumID());

            sgo.insert(sgo.getTG().mNodeSet.getNode(i), Constants.SkipGraphOperation.STATIC_SIMULATION_TIME, sgo.getTG().mNodeSet);
        }
        //System.out.println("done!");
        return new String();
    }
}
