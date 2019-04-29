package Evaluation;

import Simulator.SkipSimParameters;
import SkipGraph.Landmarks;
import SkipGraph.Node;
import SkipGraph.Nodes;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

public class DelayEvaluationTest extends SkipSimParameters
{
    Landmarks mLandmarks;
    Nodes mNodes;

    @Before
    public void SetUp()
    {
        TopologyNumbers = 100;
        mLandmarks = new Landmarks();
        /*
        Generating landmarks
         */
        mLandmarks.generatingLandmarks();
        mNodes = new Nodes(this.getClass());
        for (int i = 0; i < SkipSimParameters.getSystemCapacity(); i++)
        {
            ((Node) mNodes.getNode(i)).setIndex(i);
            ((Node) mNodes.getNode(i)).setOnline();
        }
    }

    /**
     * One data owner, and all nodes are replicas of that data owner, hence,
     */
    @Test
    public void allNodesReplica()
    {
        /*
        Replication should be qos evaluated
         */
        Heterogeneous = true;
        sDataOwnerNumber = 1;
        CurrentTopologyIndex = 1;


        /*
        Evaluating the closest replica to every node
         */
        for (int i = 0; i < SkipSimParameters.getSystemCapacity(); i++)
        {
            Node node = ((Node) mNodes.getNode(i));
            node.setOnline();
            /*
            Setting the bandwidth capacity of all the nodes to 0.001
             */
            node.setBandwidthCapacity(0.001);
            /*
            Setting each node as a replica
             */
            boolean replicationResult = node.setAsReplica(0);
            Assert.assertTrue(replicationResult);
            Assert.assertTrue(((Node) mNodes.getNode(i)).isReplica(0));
        }

        /*
        Updating corresponding replica of all nodes for the data owner 0
         */
        mNodes.setCorrespondingReplica(0);

        /*
        Since each node is a replica, the average access delay should be zero
         */
        Assert.assertEquals(ReplicationEvaluation.AverageAccessDelay(mNodes, true, "TEST", 0, false), 0, 0);
        /*
        Since bandwidth of each node was set to 0, the QoS should also be zero
         */
        Assert.assertEquals(ReplicationEvaluation.AverageAccessDelay(mNodes, true, "TEST", 0, true), 0.001, 0.0000001);

        for (int i = 0; i < SkipSimParameters.getSystemCapacity(); i++)
        {
            Node node = ((Node) mNodes.getNode(i));
            /*
            Setting the bandwidth capacity of all the nodes to 1
             */
            node.setBandwidthCapacity(1);
        }
        /*
        Since bandwidth of each node was set to 1, and every node is a replica the QoS should also be 1
         */
        Assert.assertEquals(ReplicationEvaluation.AverageAccessDelay(mNodes, true, "TEST", 0, true), 1, 0);
    }

    /**
     * One data owner, and one replica, hence the average access delay should be equal to the average delay between the replica and all nodes
     */
    @Test
    public void oneNodesReplica()
    {
        /*
        Replication should be qos evaluated
         */
        Heterogeneous = true;
        Random random = new Random();
        sDataOwnerNumber = 1;
        CurrentTopologyIndex = 2;

        /*
        Replicating on a random node, and setting its bandwidth capacity to 1
         */
        int replicaIndex = random.nextInt(SkipSimParameters.getSystemCapacity() - 1);
        Node replica = (Node) mNodes.getNode(replicaIndex);
        boolean replicationResult = (replica).setAsReplica(0);
        replica.setBandwidthCapacity(1);
        Assert.assertTrue(replicationResult);
        Assert.assertTrue(replica.isReplica(0));

        /*
        Updating corresponding replica of all nodes for the data owner 0
         */
        mNodes.setCorrespondingReplica(0);
        /*
        Since there is only one node as replica, it should have all the nodes in the system assigned to itself
         */
        Assert.assertEquals(mNodes.numberOfDataRequesters(replicaIndex, 0, false), SkipSimParameters.getSystemCapacity());

        double averageAccessDelay = 0;
        /*
        Evaluating the closest replica to every node
         */
        for (int i = 0; i < SkipSimParameters.getSystemCapacity(); i++)
        {
            Node node = (Node) mNodes.getNode(i);
            Node assignedReplica = ((Node) mNodes.getNode(node.getCorrespondingReplica(0)));
            Assert.assertEquals(replica.getIndex(), assignedReplica.getIndex());
            averageAccessDelay += node.latencyTo(assignedReplica);
        }
        /*
        Average access delay evaluation
         */
        Assert.assertEquals(ReplicationEvaluation
                .AverageAccessDelay(mNodes, true, "TEST", 0, false), averageAccessDelay / SkipSimParameters.getSystemCapacity(), 0);

        /*
        Average QoS evaluation
         */
        double qos = 0;
        for (int i = 0; i < SkipSimParameters.getSystemCapacity(); i++)
        {
            Node node = (Node) mNodes.getNode(i);
            Node assignedReplica = ((Node) mNodes.getNode(node.getCorrespondingReplica(0)));
            qos += (double) 1 / (node.latencyTo(assignedReplica) + 1);
        }
        qos /= SkipSimParameters.getSystemCapacity();
        qos /= SkipSimParameters.getSystemCapacity();
        Assert.assertEquals(ReplicationEvaluation
                .AverageAccessDelay(mNodes, true, "TEST", 0, true), qos, 0);
    }

    /**
     * One data owner, and many replicas, hence the average access delay should be equal to the average latency between
     * each node and its closest replica
     */
    @Test
    public void manyNodesReplica()
    {
        /*
        Replication should be qos evaluated
         */
        Heterogeneous = true;
        Random random = new Random();
        sDataOwnerNumber = 1;
        CurrentTopologyIndex = 3;

        int repNumber = random.nextInt(SkipSimParameters.getSystemCapacity());
        while (repNumber <= 0)
        {
            repNumber = random.nextInt(SkipSimParameters.getSystemCapacity());
        }

        for (int replica = 0; replica < repNumber; replica++)
        {
            /*
            Replicating on a random node
             */
            int replicaIndex = random.nextInt(SkipSimParameters.getSystemCapacity() - 1);
            while (((Node) mNodes.getNode(replicaIndex)).isReplica(0))
            {
                replicaIndex = random.nextInt(SkipSimParameters.getSystemCapacity() - 1);
            }
            boolean replicationResult = ((Node) mNodes.getNode(replicaIndex)).setAsReplica(0);
            Assert.assertTrue(replicationResult);
            Assert.assertTrue(((Node) mNodes.getNode(replicaIndex)).isReplica(0));
        }


        /*
        Updating corresponding replica of all nodes for the data owner 0
         */
        mNodes.setCorrespondingReplica(0);

        double averageAccessDelay = 0;
        /*
        Evaluating the closest replica to every node
         */
        for (int i = 0; i < SkipSimParameters.getSystemCapacity(); i++)
        {
            Node node = (Node) mNodes.getNode(i);
            Node replica = ((Node) mNodes.getNode(node.getCorrespondingReplica(0)));
            averageAccessDelay += node.getCoordinate().distance(replica.getCoordinate());
        }
        Assert.assertEquals(ReplicationEvaluation.AverageAccessDelay(mNodes, true, "TEST", 0, false), averageAccessDelay / SkipSimParameters.getSystemCapacity(), 0);
    }

    /**
     * Two data owners each with randomly many replicas
     * The overal average access delay of the system is evaluated against the AverageAccessDelay function
     */
    @Test
    public void twoDataOwnerManyNodesReplica()
    {
        /*
        Replication should be qos evaluated
         */
        Heterogeneous = true;
        Random random = new Random();
        sDataOwnerNumber = 2;

        /*
        Assume that we are experimenting for the topology number 90, we later on check this in the delayDataSet of the
        replication evaluation class
         */
        CurrentTopologyIndex = 4;

        int repNumber = random.nextInt(SkipSimParameters.getSystemCapacity());

        /*
        Replicating for data owner number zero
         */
        while (repNumber <= 0)
        {
            repNumber = random.nextInt(SkipSimParameters.getSystemCapacity());
        }

        for (int replica = 0; replica < repNumber; replica++)
        {
            /*
            Replicating on a random node
             */
            int replicaIndex = random.nextInt(SkipSimParameters.getSystemCapacity() - 1);
            while (((Node) mNodes.getNode(replicaIndex)).isReplica(0))
            {
                replicaIndex = random.nextInt(SkipSimParameters.getSystemCapacity() - 1);
            }
            boolean replicationResult = ((Node) mNodes.getNode(replicaIndex)).setAsReplica(0);
            Assert.assertTrue(replicationResult);
            Assert.assertTrue(((Node) mNodes.getNode(replicaIndex)).isReplica(0));
        }

        /*
        Replicating for data owner number one
         */
        repNumber = random.nextInt(SkipSimParameters.getSystemCapacity());
        while (repNumber <= 0)
        {
            repNumber = random.nextInt(SkipSimParameters.getSystemCapacity());
        }

        for (int replica = 0; replica < repNumber; replica++)
        {
            /*
            Replicating on a random node
             */
            int replicaIndex = random.nextInt(SkipSimParameters.getSystemCapacity() - 1);
            while (((Node) mNodes.getNode(replicaIndex)).isReplica(1))
            {
                replicaIndex = random.nextInt(SkipSimParameters.getSystemCapacity() - 1);
            }
            boolean replicationResult = ((Node) mNodes.getNode(replicaIndex)).setAsReplica(1);
            Assert.assertTrue(replicationResult);
            Assert.assertTrue(((Node) mNodes.getNode(replicaIndex)).isReplica(1));
        }


        /*
        Updating corresponding replica of all nodes for the data owners 0 and 1
         */
        mNodes.setCorrespondingReplica(0);
        mNodes.setCorrespondingReplica(1);

        double averageAccessDelayOwnerOne = 0;
        double averageAccessDelayOwnerTwo = 0;


        /*
        Evaluating the closest replica to every node
         */
        for (int i = 0; i < SkipSimParameters.getSystemCapacity(); i++)
        {
            Node node = (Node) mNodes.getNode(i);
            Node replicaOne = ((Node) mNodes.getNode(node.getCorrespondingReplica(0)));
            Node replicaTwo = ((Node) mNodes.getNode(node.getCorrespondingReplica(1)));
            averageAccessDelayOwnerOne += node.getCoordinate().distance(replicaOne.getCoordinate());
            averageAccessDelayOwnerTwo += node.getCoordinate().distance(replicaTwo.getCoordinate());
        }
        double overalDelay = averageAccessDelayOwnerOne + averageAccessDelayOwnerTwo;
        overalDelay = (double) overalDelay / SkipSimParameters.getSystemCapacity();
        overalDelay = overalDelay / 2;
        Assert.assertEquals(ReplicationEvaluation.AverageAccessDelay(mNodes, true, "TEST", 0, false), overalDelay, 0);

        /*
        Checking the correctness of the log for the average access delay
         */
        Assert.assertEquals(ReplicationEvaluation.getDelayDataSet(CurrentTopologyIndex - 1), overalDelay, 0);
    }

    /**
     * There is only one data owner, but two replicas, chosen randomly, each with bandwidth capacity of 0.5, and the
     * QoS of all the nodes with respect to each of them is evaluated.
     */
    @Test
    public void oneDataOwnerTwoReplicasQoS()
    {
        /*
        Replication should be qos evaluated
         */
        Heterogeneous = true;
        sDataOwnerNumber = 1;
        CurrentTopologyIndex = 5;
        /*
        Replicating on two randomly chosen nodes
         */
        Random random = new Random();
        int randomNodeIndex = random.nextInt(SkipSimParameters.getSystemCapacity() - 1);
        Node node1 = (Node) mNodes.getNode(randomNodeIndex);
        node1.setAsReplica(0);
        node1.setBandwidthCapacity(0.5);

        randomNodeIndex = random.nextInt(SkipSimParameters.getSystemCapacity() - 1);
        while (((Node) mNodes.getNode(randomNodeIndex)).isReplica(0))
            randomNodeIndex = random.nextInt(SkipSimParameters.getSystemCapacity() - 1);
        Node node2 = (Node) mNodes.getNode(randomNodeIndex);
        node2.setAsReplica(0);
        node2.setBandwidthCapacity(0.5);

        /*
        Updating corresponding replica of all nodes for the data owners 0 and 1
         */
        mNodes.setCorrespondingReplica(0);
        int dataReqNumRep1 = mNodes.numberOfDataRequesters(node1.getIndex(), 0, false);
        int dataReqNumRep2 = mNodes.numberOfDataRequesters(node2.getIndex(), 0, false);

        /*
        Evaluating the closest replica QoS to every node
         */
        double qos = 0;
        for (int i = 0; i < SkipSimParameters.getSystemCapacity(); i++)
        {
            Node node = (Node) mNodes.getNode(i);
            Node replica = ((Node) mNodes.getNode(node.getCorrespondingReplica(0)));
            if (replica.getIndex() == node1.getIndex())
            {
                //qos += 0.5 / (dataReqNumRep1 * (node.latencyTo(node1) + 1));
                qos += 0.5 / dataReqNumRep1;
            }
            else
                qos += 0.5 / dataReqNumRep2;
        }
        qos /= SkipSimParameters.getSystemCapacity();
        Assert.assertEquals(ReplicationEvaluation
                .AverageAccessDelay(mNodes, true, "TEST", 0, true), qos, 0);
        /*
        Testing the correctness of recording the qos of this topology
         */
        Assert.assertEquals(ReplicationEvaluation.getQosDataSet(SkipSimParameters.getCurrentTopologyIndex() - 1), qos, 0);
    }

    /**
     * There are two data owners, each with one replica, chosen randomly, each with bandwidth capacity of 0.5, and the
     * QoS of all the nodes with respect to each of them is evaluated.
     */
    @Test
    public void twoDataOwnersOneReplicasQoS()
    {
        /*
        Replication should be qos evaluated
         */
        Heterogeneous = true;
        sDataOwnerNumber = 2;
        CurrentTopologyIndex = 6;
        /*
        Replicating on a randomly chosen node for data owner 0
         */
        Random random = new Random();
        int randomNodeIndex = random.nextInt(SkipSimParameters.getSystemCapacity() - 1);
        Node node1 = (Node) mNodes.getNode(randomNodeIndex);
        node1.setAsReplica(0);
        node1.setBandwidthCapacity(0.5);

        /*
        Replicating on a randomly chosen node for data owner 1
         */
        randomNodeIndex = random.nextInt(SkipSimParameters.getSystemCapacity() - 1);
        Node node2 = (Node) mNodes.getNode(randomNodeIndex);
        node2.setAsReplica(1);
        node2.setBandwidthCapacity(0.5);

        /*
        Updating corresponding replica of all nodes for the data owners 0 and 1
         */
        mNodes.setCorrespondingReplica(0);
        mNodes.setCorrespondingReplica(1);
        int dataReqNumRep1 = mNodes.numberOfDataRequesters(node1.getIndex(), 0, false);
        int dataReqNumRep2 = mNodes.numberOfDataRequesters(node2.getIndex(), 1, false);

        /*
        Evaluating the closest replica QoS to every node
        qos1 for dataowner 1
        qos2 for dataowner 2
         */
        double qos1 = 0;
        double qos2 = 0;
        for (int i = 0; i < SkipSimParameters.getSystemCapacity(); i++)
        {
            Node node = (Node) mNodes.getNode(i);
            Node replica1 = ((Node) mNodes.getNode(node.getCorrespondingReplica(0)));
            Node replica2 = ((Node) mNodes.getNode(node.getCorrespondingReplica(1)));

            qos1 += 0.5 / dataReqNumRep1;

            qos2 += 0.5 / dataReqNumRep2;
        }
        qos1 /= SkipSimParameters.getSystemCapacity();
        qos2 /= SkipSimParameters.getSystemCapacity();
        double qos = (qos1 + qos2)/2;

        Assert.assertEquals(ReplicationEvaluation
                .AverageAccessDelay(mNodes, true, "TEST", 0, true), qos, 0);
        /*
        Testing the correctness of recording the qos of this topology
         */
        Assert.assertEquals(ReplicationEvaluation.getQosDataSet(SkipSimParameters.getCurrentTopologyIndex() - 1), qos, 0);
    }


}
