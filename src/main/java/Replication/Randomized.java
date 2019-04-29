package Replication;

import Simulator.SkipSimParameters;
import SkipGraph.Node;
import SkipGraph.SkipGraphOperations;

import java.util.Random;


public class Randomized extends Replication
{

    protected void randomReplicaGenerator(int dataOwnerID)
    {
        System.out.println("Randomized.java: Randomized replication started.");
        Random random = new Random();
        int i = 0;
        while (i < SkipSimParameters.getReplicationDegree())
        {
            int index = random.nextInt(SkipSimParameters.getSystemCapacity() - 1);
            Node node = ((Node) sgo.getTG().mNodeSet.getNode(index));
            while (node.isReplica(dataOwnerID) || node.isOffline())
                index = random.nextInt(SkipSimParameters.getSystemCapacity() - 1);
            boolean replicationResult = ((Node) sgo.getTG().mNodeSet.getNode(index)).setAsReplica(dataOwnerID);
            if (replicationResult)
            {
                System.out.println("Randomized.java: new replica at " + " index = " + index + " total replicas till now " + i);
                i++;
            }
        }
        sgo.getTG().getNodeSet().setCorrespondingReplica(dataOwnerID);
    }

    @Override
    public void Algorithm(SkipGraphOperations sgo, int dataOwnerID)
    {
        this.sgo = sgo;
        reset();
        resetRep();
        randomReplicaGenerator(dataOwnerID);
    }

}