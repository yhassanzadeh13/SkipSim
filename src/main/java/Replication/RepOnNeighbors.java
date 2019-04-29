package Replication;

import Simulator.SkipSimParameters;
import SkipGraph.Node;
import SkipGraph.SkipGraphOperations;

public class Rep_Alg03_RepOnNeighbors extends Replication
{

    public void ReplicateOnNeighborsGenerator(int dataOwnerIndex)
    {
        int index = 0;
        while (index < SkipSimParameters.getSystemCapacity() && ((Node) sgo.getTG().mNodeSet.getNode(index)).neighborNumber() < SkipSimParameters.getReplicationDegree())
            index++;

        if (index >= SkipSimParameters.getSystemCapacity())
        {
            throw new IllegalStateException("Rep_Alg03_RepOnNeighbors.java: there is no node with " + SkipSimParameters.getReplicationDegree() + " neighbors" + "\n lower replication degree");
        }

        for (int i = 0, rep = 0; i < SkipSimParameters.getLookupTableSize() && rep <= SkipSimParameters.getReplicationDegree(); i++)
            for (int j = 0; j < 2 && rep <= SkipSimParameters.getReplicationDegree(); j++)
            {
                if (sgo.getTG().mNodeSet.getNode(index).getLookup(i, j) != -1)
                {
                    boolean replicationResult = ((Node) sgo.getTG().mNodeSet.getNode(((Node) sgo.getTG().mNodeSet.getNode(index)).getLookup(i, j))).setAsReplica(dataOwnerIndex);
                    if (replicationResult)
                        rep++;
                }

            }

        sgo.getTG().getNodeSet().setCorrespondingReplica(dataOwnerIndex);
    }

    @Override
    public void Algorithm(SkipGraphOperations inputSgo, int dataOwnerIndex)
    {
        sgo = inputSgo;
        reset();
        resetRep();
        ReplicateOnNeighborsGenerator(dataOwnerIndex);
    }
}