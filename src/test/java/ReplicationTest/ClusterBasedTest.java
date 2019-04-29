package ReplicationTest;

import DataTypes.Constants;
import Replication.ClusterBased;
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

public class ClusterBasedTest extends SkipSimParameters
{
    ClusterBased mClusterBased;
    Nodes mNodes;

    /**
     * 8 nodes, storage and bandwidth capacity of 1,
     * FPTI is 3
     */
    @Before
    public void SetUp()
    {
        /*
        To take the storage and bandwidth into account
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

        mClusterBased = new ClusterBased();
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
    }

    /**
     * The clustering sub-routine of the ClusterBased replication is tested against the replication degrees of
     * 2, hence given the availability table of a data owner, which has 8 availability vectors, it should return two clusters
     */
    @Test
    public void clusteringTest()
    {
        ReplicationDegree = 2;
        Hashtable<String, double[]> qosTable = new Hashtable<>();
        qosTable.put("000", new double[]{0, 1, 1});
        qosTable.put("001", new double[]{0, 0, 0});
        qosTable.put("010", new double[]{0, 0, 0});
        qosTable.put("011", new double[]{0, 0, 0});
        qosTable.put("100", new double[]{0, 0, 0});
        qosTable.put("101", new double[]{1, 0.5, 1});
        qosTable.put("110", new double[]{0, 0, 0});
        qosTable.put("111", new double[]{0.3, 0.3, 0.3});
        /*
        Setting qosTable entries as the availability table of the node 1 as the data owner.
         */
        Node node = (Node) mNodes.getNode(1);
        for (String id : qosTable.keySet())
        {
            node.setAvailabilityTable(Integer.parseInt(id, 2), qosTable.get(id));
        }

        /*
        Replicating twice should result in both 101 and 000 that have the best replication degree
         */
        Hashtable<Integer, Integer> clusterIndex = mClusterBased.clustering(node);
        Assert.assertTrue(clusterIndex.size() == 8);
        /*
        000 and 101 should be in the same cluster
         */
        Assert.assertTrue(clusterIndex.get(0) == clusterIndex.get(5));

        /*
        Except 000 and 101, the rest should be in the same group
         */
        Assert.assertTrue(clusterIndex.get(1) == clusterIndex.get(2));
        Assert.assertTrue(clusterIndex.get(2) == clusterIndex.get(3));
        Assert.assertTrue(clusterIndex.get(3) == clusterIndex.get(4));
        Assert.assertTrue(clusterIndex.get(4) == clusterIndex.get(6));
        Assert.assertTrue(clusterIndex.get(6) == clusterIndex.get(7));

        /*
        These two groups should be disjoint i.e., 000 and 001 should not be in the same group
         */
        Assert.assertTrue(clusterIndex.get(1) != clusterIndex.get(0));
    }

    /**
     * This test evaluates the best match inside each cluster after splitting the clusters into two parts
     * it evaluates the best found match in each cluster. Then it also updates one of the best matches to no longer
     * be the best match, and checks to see whether it can find the best second match or not?
     */
    @Test
    public void bestMatchTest()
    {
        ReplicationDegree = 2;
        Hashtable<String, double[]> qosTable = new Hashtable<>();
        qosTable.put("000", new double[]{0, 1, 1});
        qosTable.put("001", new double[]{1, 0, 0});
        qosTable.put("010", new double[]{0.9, 0, 0});
        qosTable.put("011", new double[]{0.8, 0, 0});
        qosTable.put("100", new double[]{0.7, 0, 0});
        qosTable.put("101", new double[]{1, 0.5, 1});
        qosTable.put("110", new double[]{0.6, 0, 0});
        qosTable.put("111", new double[]{0.3, 0.3, 0.3});

        Node node = (Node) mNodes.getNode(1);
        for (String id : qosTable.keySet())
        {
            node.setAvailabilityTable(Integer.parseInt(id, 2), qosTable.get(id));
        }

        /*
        Replicating twice should result in both 101 and 000 that have the best replication degree
         */
        Hashtable<Integer, Integer> clusterIndex = mClusterBased.clustering(node);
        Assert.assertTrue(clusterIndex.size() == 8);
        /*
        000 and 101 should be in the same cluster
         */
        Assert.assertTrue(clusterIndex.get(0) == clusterIndex.get(5));

        /*
        Except 000 and 101, the rest should be in the same group
         */
        Assert.assertTrue(clusterIndex.get(1) == clusterIndex.get(2));
        Assert.assertTrue(clusterIndex.get(2) == clusterIndex.get(3));
        Assert.assertTrue(clusterIndex.get(3) == clusterIndex.get(4));
        Assert.assertTrue(clusterIndex.get(4) == clusterIndex.get(6));
        Assert.assertTrue(clusterIndex.get(6) == clusterIndex.get(7));

        /*
        These two groups should be disjoint i.e., 000 and 001 should not be in the same group
         */
        Assert.assertTrue(clusterIndex.get(1) != clusterIndex.get(0));
        /*
        The best node to replicate in the bigger cluster (i.e., cluster where node 1 exists), is the node 1 itself)
         */
        int bestInFirstCluster = mClusterBased.getBestMatch(clusterIndex.get(1), clusterIndex, node, mNodes);
        Assert.assertEquals(bestInFirstCluster, 1);

        /*
        Making node 1 as a replica, hence, it should no longer be a best replicating candidate, and rather node 2 (i.e.,
        010 should be)
         */
        node.setAsReplica(0);
        bestInFirstCluster = mClusterBased.getBestMatch(clusterIndex.get(1), clusterIndex, node, mNodes);
        Assert.assertEquals(bestInFirstCluster, 2);

        /*
        The best replicating candidate in the second cluster would be node 101 (i.e., node 5)
         */
        int bestInSecondCluster = mClusterBased.getBestMatch(clusterIndex.get(0), clusterIndex, node, mNodes);
        Assert.assertEquals(bestInSecondCluster, 5);

    }

    /**
     * One data owner, 8 nodes in the availability table of the data owner, and the replication degree of two.
     */
    @Test
    public void replicationTest()
    {
        ReplicationDegree = 2;
        Hashtable<String, double[]> qosTable = new Hashtable<>();
        qosTable.put("000", new double[]{0, 1, 1});
        qosTable.put("001", new double[]{1, 0, 0});
        qosTable.put("010", new double[]{0.9, 0, 0});
        qosTable.put("011", new double[]{0.8, 0, 0});
        qosTable.put("100", new double[]{1, 0.3, 1});
        qosTable.put("101", new double[]{1, 0.5, 1});
        qosTable.put("110", new double[]{1, 0.4, 1});
        qosTable.put("111", new double[]{0.3, 0.3, 0.3});

        /*
        Assuming node 1 is the data owner, and the replication degree is two, this puts qosTable as
        the availability table of node 1
         */
        Node node = (Node) mNodes.getNode(1);
        for (String id : qosTable.keySet())
        {
            node.setAvailabilityTable(Integer.parseInt(id, 2), qosTable.get(id));
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
            node = (Node) mNodes.getNode(i);
            node.setIndex(i);
            node.setOnline();
            int x = random.nextInt(SkipSimParameters.getDomainSize() - 1);
            int y = random.nextInt(SkipSimParameters.getDomainSize() - 1);
            node.setCoordinate(new Point(x, y));
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
        SkipGraphOperations sgo = new SkipGraphOperations(false);
        sgo.getTG().mNodeSet = mNodes;
        sgo.getTG().mLandmarks = new Landmarks();
        sgo.getTG().mLandmarks.generatingLandmarks();

        /*
        Doing the replication
         */
        mClusterBased.Algorithm(sgo, 1);
        for (int i = 0; i < SystemCapacity; i++)
        {
            Node n = (Node) mNodes.getNode(i);
            /*
            Nodes 1 and 5 are the bests of their cluster and should be chosen
             */
            if (i == 1 || i == 5)
                Assert.assertTrue(n.isReplica(1));
            else
                Assert.assertFalse(n.isReplica(1));
        }

        /*
        Adding node 0 as the other data owner, and the replication degree is two,
        with the same QoS table as node 1
         */
        node = (Node) mNodes.getNode(0);
        for (String id : qosTable.keySet())
        {
            node.setAvailabilityTable(Integer.parseInt(id, 2), qosTable.get(id));
        }

        /*
        Doing the replication for node 0
         */
        mClusterBased.Algorithm(sgo, 0);
        for (int i = 0; i < SystemCapacity; i++)
        {
            Node n = (Node) mNodes.getNode(i);
            /*
            Nodes 1 and 5 are the bests of their cluster, but have already chosen, and since the
            storage capacity is 1, they no longer can be chosen as another replica for another node.
            Rather nodes 2 and 6 should be chosen as the second bests
             */
            if (i == 2 || i == 6)
                Assert.assertTrue(n.isReplica(0));
            else
                Assert.assertFalse(n.isReplica(0));
        }

    }

}
