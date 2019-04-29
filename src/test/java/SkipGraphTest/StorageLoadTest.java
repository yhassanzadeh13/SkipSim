package SkipGraphTest;

import Simulator.SkipSimParameters;
import SkipGraph.Node;
import SkipGraph.Nodes;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

public class StorageLoadTest extends SkipSimParameters
{
    Nodes mNodes;
    @Before
    public void setUp()
    {
        mNodes = new Nodes(this.getClass());
    }

    /**
     * Selects a random node, sets its storage load to 0, and replicates over that which should result in the rejection
     * of the replication, because the node gets out of capacity.
     */
    @Test
    public void zeroLoad()
    {
        Random random = new Random();
        int randomNodeIndex = random.nextInt(SkipSimParameters.getSystemCapacity() - 1);
        Node node1 = (Node) mNodes.getNode(randomNodeIndex);
        /*
        Making storage limit zero
         */
        node1.setStorageCapacity(0);

        /*
        Requests from data owner 0 to node1 to take its replicas should be then rejected with false
         */
        Assert.assertFalse(node1.setAsReplica(0));
        Assert.assertFalse(node1.isReplica(0));
    }

    /**
     * Selects a random node, sets its storage capacity to 10, replicates 10 times over that i.e., for data owners
     * between 0-9. Evaluates the correctness of the replication. Then replicates on the node for the 11th time with another
     * data owner id, which should result in the rejection of the replication because the node gets out of the storage load.
     */
    @Test
    public void replicatingToOverLoad()
    {
        Random random = new Random();
        int randomNodeIndex = random.nextInt(SkipSimParameters.getSystemCapacity() - 1);
        Node node1 = (Node) mNodes.getNode(randomNodeIndex);
        /*
        Making storage limit to 10
         */
        node1.setStorageCapacity(10);

        /*
        Requests from data owners 0-9 to node1 to take its replicas should be accepted
         */
        for(int dataOwner = 0; dataOwner < 10; dataOwner++)
        {
            Assert.assertTrue(node1.setAsReplica(dataOwner));
        }

        /*
        Evaluating that node1 is replicas of data owners 0-9 which is true
         */
        for(int dataOwner = 0; dataOwner < 10; dataOwner++)
        {
            Assert.assertTrue(node1.isReplica(dataOwner));
        }

        /*
        Replicating for another new data owner e.g., 10 should be rejected since the node goes out of the storage load
         */
        Assert.assertFalse(node1.setAsReplica(10));


    }
}
