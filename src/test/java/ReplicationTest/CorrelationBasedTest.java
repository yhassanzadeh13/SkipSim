package ReplicationTest;

import DataTypes.Constants;
import Replication.CorrelationBased;
import Simulator.SkipSimParameters;
import SkipGraph.Landmarks;
import SkipGraph.Node;
import SkipGraph.Nodes;
import SkipGraph.SkipGraphOperations;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.util.Hashtable;
import java.util.Random;

public class CorrelationBasedTest extends SkipSimParameters
{
    CorrelationBased mCorrelationBased;
    Nodes mNodes;
    SkipGraphOperations sgo;

    /**
     * 8 nodes, storage and bandwidth capacity of 1,
     * FPTI is 3
     */
    @Before
    public void SetUp()
    {
        /*
        To take the storage and bandwidth capacity into account
         */
        Heterogeneous = true;
        SimulationType = Constants.SimulationType.DYNAMIC;
        /*
        Despite this assignment, later in this setup function
        we assign the storage capacity of the nodes as 1
         */
        sStorageCapacity = -1;
        SystemCapacity = 8;
        NameIDLength = 3;
        LandmarksNum = 3;
        TopologyNumbers = 100;
        sFPTI = 3;

        mCorrelationBased = new CorrelationBased();
        mNodes = new Nodes(this.getClass());
        for (int i = 0; i < SkipSimParameters.getSystemCapacity(); i++)
        {
            Node node = (Node) mNodes.getNode(i);
            node.setIndex(i);
            node.setOnline();
            /*
            Name ID of each node is its binary representation in 3-bits
             */
            String nameID = Integer.toBinaryString(i);
            while (nameID.length() < NameIDLength)
            {
                nameID = "0" + nameID;
            }
            node.setNameID(nameID);
            node.setBandwidthCapacity(1);
            node.setStorageCapacity(1);
        }

                /*
        Initializing the Skip Graph instance
         */
        SessionLengthScaleParameter = Constants.Churn.Model.Debian.Fast.SessionLength.Scale;
        SessionLengthShapeParameter = Constants.Churn.Model.Debian.Fast.SessionLength.Shape;
        InterarrivalScaleParameter = Constants.Churn.Model.Debian.Fast.SessionInterarrival.Scale;
        InterarrivalShapeParameter = Constants.Churn.Model.Debian.Fast.SessionInterarrival.Shape;
        Random random = new Random();
        for (int i = 0; i < SkipSimParameters.getSystemCapacity(); i++)
        {
            Node node = (Node) mNodes.getNode(i);
            node.setIndex(i);
            node.setOnline();
            int x = random.nextInt(SkipSimParameters.getDomainSize() - 1);
            int y = random.nextInt(SkipSimParameters.getDomainSize() - 1);
            node.setCoordinate(new Point(x,y));
            /*
            Closest landmark to each node is zero
             */
            node.setClosetLandmarkIndex(0);
            String nameID = Integer.toBinaryString(i);
            while (nameID.length() < NameIDLength)
            {
                nameID = "0" + nameID;
            }
            node.setNameID(nameID);
            node.setBandwidthCapacity(1);
        }
        sgo = new SkipGraphOperations(false);
        sgo.getTG().mNodeSet = mNodes;
        sgo.getTG().mLandmarks = new Landmarks();
        sgo.getTG().mLandmarks.generatingLandmarks();
        mCorrelationBased.setSgo(sgo);
    }

    /**
     *Testing the bestNode function to find the node with the best QoS.
     *Then the bestNode node is being replicated, and then the second best qos is being selected.
     */
    @Test
    public void bestQoSTest()
    {
        ReplicationDegree = 2;
        Hashtable<Integer, double[]> qosTable = new Hashtable<>();
        qosTable.put(0, new double[]{0, 1, 1});
        qosTable.put(1, new double[]{1, 0, 0});
        qosTable.put(2, new double[]{0.9, 0, 0});
        qosTable.put(3, new double[]{0.8, 0, 0});
        qosTable.put(4, new double[]{1, 0.3, 1});
        qosTable.put(5, new double[]{1, 0.5, 1});
        qosTable.put(6, new double[]{1, 0.4, 1});
        qosTable.put(7, new double[]{0.3, 0.3, 0.3});

        /*
        Setting qosTable entries as the availability table of the node 1 as the data owner.
         */
        Node node = (Node) sgo.getTG().mNodeSet.getNode(1);
        for(int id: qosTable.keySet())
        {
            node.setAvailabilityTable(id, qosTable.get(id));
        }


        /*
        Checking for the node with the best qos i.e., highest norm in availability vector
         */
        int bestQoSIndex = mCorrelationBased.bestNode(node);
        Assert.assertEquals(bestQoSIndex, 5);

        /*
        Replicating on the node 5, hence it should no longer be a
        replica, since in this test all nodes are assumed to have storage capacity of 1.
        Note that the node5 is supposed to take replica of another node (i.e., 2) and
        not our data owner of the test (i.e., 1)
         */
        Node node5 = (Node) sgo.getTG().mNodeSet.getNode(5);
        node5.setAsReplica(2);

        /*
        Node 6 should be the second best replica
         */
        bestQoSIndex = mCorrelationBased.bestNode(node);
        Assert.assertEquals(bestQoSIndex, 6);

    }

    /**
     *Testing the bestAntiCorrelated to find the bestAntiCorrelated candidate with respect to node 0.
     *Initially, it should be node 1, however, after replicating on node 1, it should be node 2.
     */
    @Test
    public void bestAntiCorrelated()
    {
        ReplicationDegree = 2;
        Hashtable<Integer, double[]> qosTable = new Hashtable<>();
        qosTable.put(0, new double[]{0, 1, 1});
        qosTable.put(1, new double[]{1, 0, 0});
        qosTable.put(2, new double[]{0.9, 0.2, 0});
        qosTable.put(3, new double[]{0.8, 0.1, 0});
        qosTable.put(4, new double[]{1, 0.3, 1});
        qosTable.put(5, new double[]{1, 0.5, 1});
        qosTable.put(6, new double[]{1, 0.4, 1});
        qosTable.put(7, new double[]{0.3, 0.3, 0.3});

        /*
        Setting qosTable as the availability table of node 1
         */
        Node node = (Node) sgo.getTG().mNodeSet.getNode(1);
        for (int id : qosTable.keySet())
        {
            node.setAvailabilityTable(id, qosTable.get(id));
        }

        /*
        Finding the best anti correlated candidate with node 0 (i.e., availability vector of (0,1,1)),
         initially it is node 4 (i.e., node)
         */
        int bestAntiCorrelatedIndex = mCorrelationBased.bestAntiCorrelated(new double[]{0, 1, 1}, node);
        Assert.assertEquals(bestAntiCorrelatedIndex, 4);
    }


    /**
     *Testing the bestAntiCorrelated to find the bestAntiCorrelated candidate with respect to node 0.
     *Initially, it should be node 1, however, after replicating on node 1, it should be node 2.
     */
    @Test
    public void replicationTest()
    {
        ReplicationDegree = 2;
        Hashtable<Integer, double[]> qosTable = new Hashtable<>();
        qosTable.put(0, new double[]{0, 1, 1});
        qosTable.put(1, new double[]{0.1, 0, 0});
        qosTable.put(2, new double[]{0.9, 0, 0.1});
        qosTable.put(3, new double[]{0.8, 0, 0.1});
        qosTable.put(4, new double[]{0.7, 0.3, 0.4});
        qosTable.put(5, new double[]{0.9, 0.5, 0.9});
        qosTable.put(6, new double[]{0.7, 0.4, 1});
        qosTable.put(7, new double[]{0.3, 0.3, 0.3});

        /*
        Setting qosTable as the availability table of both data owners.
         */
        Node dataOwner1 = (Node) sgo.getTG().mNodeSet.getNode(1);
        Node dataOwner2 = (Node) sgo.getTG().mNodeSet.getNode(7);
        for (int id : qosTable.keySet())
        {
            dataOwner1.setAvailabilityTable(id, qosTable.get(id));
            dataOwner2.setAvailabilityTable(id, qosTable.get(id));
        }

        /*
        Replication algorithm is executed on dataOwner 1, which should result in
        nodes 0 and 4 to be selected as the replicas
         */
        mCorrelationBased.Algorithm(sgo, 1);
        for(int i = 0; i < SystemCapacity; i++)
        {
            Node n = (Node) mNodes.getNode(i);
            /*
            Nodes 0 and 4 are the best anti-correlated nodes based on their utility, and should be
            selected as replicas
             */
            if(i == 0 || i == 4)
                Assert.assertTrue(n.isReplica(1));
            else
                Assert.assertFalse(n.isReplica(1));
        }


        /*
        Replication algorithm is executed on dataOwner 7, which should result in
        nodes 5 and 3 to be selected as the replicas
         */
        mCorrelationBased.Algorithm(sgo, 7);
        for(int i = 0; i < SystemCapacity; i++)
        {
            Node n = (Node) mNodes.getNode(i);
            /*
            Nodes 5 and 3 are the best anti-correlated nodes based on their QoS, and should be
            selected as replicas
             */
            if(i == 5 || i == 3)
                Assert.assertTrue(n.isReplica(7));
            else
                Assert.assertFalse(n.isReplica(7));
        }



    }
}
