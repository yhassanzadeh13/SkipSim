package SkipGraphTest;

import Simulator.SkipSimParameters;
import SkipGraph.Landmarks;
import SkipGraph.Node;
import SkipGraph.Nodes;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

public class CorrespondingReplicaTest extends SkipSimParameters
{
    Landmarks mLandmarks;
    Nodes mNodes;
    @Before
    public void SetUp()
    {
        mLandmarks = new Landmarks();
        /*
        Generating landmarks
         */
        mLandmarks.generatingLandmarks();
        mNodes = new Nodes(this.getClass());
        for(int i = 0 ; i < SkipSimParameters.getSystemCapacity(); i++)
        {
            ((Node) mNodes.getNode(i)).setIndex(i);
            ((Node) mNodes.getNode(i)).setOnline();
        }
    }

    /**
     * In the following test, there is a single data owner, and a single replica for that data owner
     * first the replica is online and hence that is the corresponding replica for all the nodes
     * then that replica becomes offline and hence no node should have a corresponding replica for that data owner
     */
    @Test
    public void singleCorrespondingReplica()
    {
        Random random = new Random();
        sDataOwnerNumber = 1;
        /*
        Setting node 0 as a replica
         */
        int replicaIndex = random.nextInt(SkipSimParameters.getSystemCapacity() -1);
        boolean replicationResult = ((Node) mNodes.getNode(replicaIndex)).setAsReplica(0);
        Assert.assertTrue(replicationResult);
        Assert.assertTrue(((Node) mNodes.getNode(replicaIndex)).isReplica(0));

        /*
        Updating corresponding replica of all nodes for the data owner 0
         */
        mNodes.setCorrespondingReplica(0);

        /*
        Evaluating the closest replica to every node
         */
        for(int i = 0 ; i < SkipSimParameters.getSystemCapacity(); i++)
        {
            Node node = ((Node) mNodes.getNode(i));
            Assert.assertEquals(node.getCorrespondingReplica(0), replicaIndex);
        }

        /*
        Making that single replica (i.e., node 0) offline
         */
        ((Node) mNodes.getNode(replicaIndex)).setOffline();

        /*
        Updating corresponding replica of all nodes for the data owner 0
         */
        mNodes.setCorrespondingReplica(0);

        /*
        Evaluating the closest replica to every node
         */
        for(int i = 0 ; i < SkipSimParameters.getSystemCapacity(); i++)
        {
            Node node = ((Node) mNodes.getNode(i));
            Assert.assertEquals(node.getCorrespondingReplica(replicaIndex), -1);
        }

    }

    /**
     * In the following test, there is a single data owner, and two replicas for that data owner
     * first both replicas are online and hence the closest replica is the corresponding replica for each nodes
     * then replicaOne becomes offline and hence all nodes should have replicaTwo as their corresponding replica
     */
    @Test
    public void twoCorrespondingReplica()
    {
        Random random = new Random();
        sDataOwnerNumber = 1;
        /*
        Setting node two random nodes as replicas
         */
        int replicaOneIndex = random.nextInt(SkipSimParameters.getSystemCapacity() -1);
        Node replicaOne = ((Node) mNodes.getNode(replicaOneIndex));
        boolean replicationResult = replicaOne.setAsReplica(0);
        Assert.assertTrue(replicationResult);
        Assert.assertTrue(replicaOne.isReplica(0));
        Assert.assertTrue(replicaOne.isOnline());

        int replicaTwoIndex = random.nextInt(SkipSimParameters.getSystemCapacity() -1);
        Node replicaTwo = ((Node) mNodes.getNode(replicaTwoIndex));
        replicationResult = replicaTwo.setAsReplica(0);
        Assert.assertTrue(replicationResult);
        Assert.assertTrue(replicaTwo.isReplica(0));
        Assert.assertTrue(replicaTwo.isOnline());



        /*
        Updating corresponding replica of all nodes for the data owner 0
         */
        mNodes.setCorrespondingReplica(0);

        /*
        Evaluating the closest replica to every node
         */
        for(int i = 0 ; i < SkipSimParameters.getSystemCapacity(); i++)
        {
            Node node = ((Node) mNodes.getNode(i));
            int replicaIndex;
            if(node.getCoordinate().distance(replicaOne.getCoordinate()) < node.getCoordinate().distance(replicaTwo.getCoordinate()))
            {
                replicaIndex = replicaOneIndex;
            }
            else
            {
                replicaIndex = replicaTwoIndex;
            }
            Assert.assertTrue(node.isOnline());
            Assert.assertEquals(node.getCorrespondingReplica(0), replicaIndex);
        }

        /*
        Making that single replica (i.e., replicaOne) offline
         */
        replicaOne.setOffline();

        /*
        Updating corresponding replica of all nodes for the data owner 0
         */
        mNodes.setCorrespondingReplica(0);

        /*
        Evaluating the closest replica to every node, which should be replicaTwo for all the nodes
         */
        for(int i = 0 ; i < SkipSimParameters.getSystemCapacity(); i++)
        {
            if(i == replicaOneIndex)
                continue;
            Node node = ((Node) mNodes.getNode(i));
            Assert.assertEquals(node.getCorrespondingReplica(0), replicaTwoIndex);
        }

    }


    /**
     * In the following test, there is a single data owner, and three replicas for that data owner
     * first all replicas are online and hence the closest replica is the corresponding replica for each nodes
     * then replicaOne becomes offline and hence all nodes should have replicaTwo or replicaThree as their corresponding replica
     */
    @Test
    public void threeCorrespondingReplica()
    {
        Random random = new Random();
        sDataOwnerNumber = 1;
        /*
        Setting node three random nodes as replicas
         */
        int replicaOneIndex = random.nextInt(SkipSimParameters.getSystemCapacity() -1);
        Node replicaOne = ((Node) mNodes.getNode(replicaOneIndex));
        boolean replicationResult = replicaOne.setAsReplica(0);
        Assert.assertTrue(replicationResult);
        Assert.assertTrue(replicaOne.isReplica(0));
        Assert.assertTrue(replicaOne.isOnline());

        int replicaTwoIndex = random.nextInt(SkipSimParameters.getSystemCapacity() -1);
        Node replicaTwo = ((Node) mNodes.getNode(replicaTwoIndex));
        replicationResult = replicaTwo.setAsReplica(0);
        Assert.assertTrue(replicationResult);
        Assert.assertTrue(replicaTwo.isReplica(0));
        Assert.assertTrue(replicaTwo.isOnline());

        int replicaThreeIndex = random.nextInt(SkipSimParameters.getSystemCapacity() -1);
        Node replicaThree = ((Node) mNodes.getNode(replicaThreeIndex));
        replicationResult = replicaThree.setAsReplica(0);
        Assert.assertTrue(replicationResult);
        Assert.assertTrue(replicaThree.isReplica(0));
        Assert.assertTrue(replicaThree.isOnline());



        /*
        Updating corresponding replica of all nodes for the data owner 0
         */
        mNodes.setCorrespondingReplica(0);

        /*
        Evaluating the closest replica to every node
         */
        for(int i = 0 ; i < SkipSimParameters.getSystemCapacity(); i++)
        {
            Node node = ((Node) mNodes.getNode(i));
            int replicaIndex;
            if(node.getCoordinate().distance(replicaOne.getCoordinate()) < Math.min(node.getCoordinate().distance(replicaTwo.getCoordinate()), node.getCoordinate().distance(replicaThree.getCoordinate())))
            {
                replicaIndex = replicaOneIndex;
            }
            else if(node.getCoordinate().distance(replicaTwo.getCoordinate()) < Math.min(node.getCoordinate().distance(replicaOne.getCoordinate()), node.getCoordinate().distance(replicaThree.getCoordinate())))
            {
                replicaIndex = replicaTwoIndex;
            }
            else
            {
                replicaIndex = replicaThreeIndex;
            }
            Assert.assertTrue(node.isOnline());
            Assert.assertEquals(node.getCorrespondingReplica(0), replicaIndex);
        }

        /*
        Making that single replica (i.e., replicaOne) offline
         */
        replicaOne.setOffline();

        /*
        Updating corresponding replica of all nodes for the data owner 0
         */
        mNodes.setCorrespondingReplica(0);

        /*
        Evaluating the closest replica to every node, which should be replicaTwo for all the nodes
         */
        for(int i = 0 ; i < SkipSimParameters.getSystemCapacity(); i++)
        {
            if(i == replicaOneIndex)
                continue;
            Node node = ((Node) mNodes.getNode(i));
            int replicaIndex;
            if(node.getCoordinate().distance(replicaTwo.getCoordinate()) < node.getCoordinate().distance(replicaThree.getCoordinate()))
            {
                replicaIndex = replicaTwoIndex;
            }
            else
            {
                replicaIndex = replicaThreeIndex;
            }
            Assert.assertTrue(node.isOnline());
            Assert.assertEquals(node.getCorrespondingReplica(0), replicaIndex);
        }

    }

    /**
     * In the following test, there are two data owners, and two replicas for each data owner
     * the corresponding replica with respect to each data owner is evaluated
     */
    @Test
    public void twoDataOwnerTwoCorrespondingReplica()
    {
        Random random = new Random();
        sDataOwnerNumber = 2;
        /*
        Setting node two random nodes as replicas for data owner zero
         */
        int OwnerOneReplicaOneIndex = random.nextInt(SkipSimParameters.getSystemCapacity() -1);
        Node OwnerOneReplicaOne = ((Node) mNodes.getNode(OwnerOneReplicaOneIndex));
        boolean replicationResult = OwnerOneReplicaOne.setAsReplica(0);
        Assert.assertTrue(replicationResult);
        Assert.assertTrue(OwnerOneReplicaOne.isReplica(0));
        Assert.assertTrue(OwnerOneReplicaOne.isOnline());

        int OwnerOneReplicaTwoIndex = random.nextInt(SkipSimParameters.getSystemCapacity() -1);
        Node OwnerOneReplicaTwo = ((Node) mNodes.getNode(OwnerOneReplicaTwoIndex));
        replicationResult = OwnerOneReplicaTwo.setAsReplica(0);
        Assert.assertTrue(replicationResult);
        Assert.assertTrue(OwnerOneReplicaTwo.isReplica(0));
        Assert.assertTrue(OwnerOneReplicaTwo.isOnline());


        /*
        Setting node two random nodes as replicas for data owner one
         */
        int OwnerTwoReplicaOneIndex = random.nextInt(SkipSimParameters.getSystemCapacity() -1);
        Node OwnerTwoReplicaOne = ((Node) mNodes.getNode(OwnerTwoReplicaOneIndex));
        replicationResult = OwnerTwoReplicaOne.setAsReplica(1);
        Assert.assertTrue(replicationResult);
        Assert.assertTrue(OwnerTwoReplicaOne.isReplica(1));
        Assert.assertTrue(OwnerTwoReplicaOne.isOnline());

        int OwnerTwoReplicaTwoIndex = random.nextInt(SkipSimParameters.getSystemCapacity() -1);
        Node OwnerTwoReplicaTwo = ((Node) mNodes.getNode(OwnerTwoReplicaTwoIndex));
        replicationResult = OwnerTwoReplicaTwo.setAsReplica(1);
        Assert.assertTrue(replicationResult);
        Assert.assertTrue(OwnerTwoReplicaTwo.isReplica(1));
        Assert.assertTrue(OwnerTwoReplicaTwo.isOnline());



        /*
        Updating corresponding replica of all nodes for the data owners zero and one
         */
        mNodes.setCorrespondingReplica(0);
        mNodes.setCorrespondingReplica(1);

        /*
        Evaluating the closest replica to every node with respect to the data owners 0 and 1
         */
        for(int i = 0 ; i < SkipSimParameters.getSystemCapacity(); i++)
        {
            Node node = ((Node) mNodes.getNode(i));
            int replicaIndex;
            if(node.getCoordinate().distance(OwnerOneReplicaOne.getCoordinate()) < node.getCoordinate().distance(OwnerOneReplicaTwo.getCoordinate()))
            {
                replicaIndex = OwnerOneReplicaOneIndex;
            }
            else
            {
                replicaIndex = OwnerOneReplicaTwoIndex;
            }
            Assert.assertEquals(node.getCorrespondingReplica(0), replicaIndex);

            if(node.getCoordinate().distance(OwnerTwoReplicaOne.getCoordinate()) < node.getCoordinate().distance(OwnerTwoReplicaTwo.getCoordinate()))
            {
                replicaIndex = OwnerTwoReplicaOneIndex;
            }
            else
            {
                replicaIndex = OwnerTwoReplicaTwoIndex;
            }
            Assert.assertEquals(node.getCorrespondingReplica(1), replicaIndex);
        }

    }


}
