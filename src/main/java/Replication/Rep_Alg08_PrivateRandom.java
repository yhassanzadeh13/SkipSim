package Replication;

import Simulator.SkipSimParameters;
import SkipGraph.Node;
import SkipGraph.SkipGraphOperations;

import java.util.Random;


public class Rep_Alg08_PrivateRandom extends Replication
{


    public void randomReplicaGenerator(int dataOwnerID)
    {
        Random random = new Random();
        int repNum = 0;
        while (repNum < SkipSimParameters.getReplicationDegree())
        {
            int index = random.nextInt(SkipSimParameters.getSystemCapacity() - 1);
            while (((Node) sgo.getTG().mNodeSet.getNode(index)).isReplica(dataOwnerID))
                index = random.nextInt(SkipSimParameters.getSystemCapacity() - 1);
            boolean replicationResult = ((Node) sgo.getTG().mNodeSet.getNode(index)).setAsReplica(dataOwnerID);
            if (replicationResult)
            {
                repNum++;
            }
        }
        sgo.getTG().getNodeSet().setCorrespondingReplica(dataOwnerID);

    }

    @Override
    public void Algorithm(SkipGraphOperations inputSgo, int dataOwnerID)
    {
        sgo = inputSgo;
        System.out.println("The Private Randomized Replication Started....");
        reset();
        resetRep();
        randomReplicaGenerator(dataOwnerID);
    }
}