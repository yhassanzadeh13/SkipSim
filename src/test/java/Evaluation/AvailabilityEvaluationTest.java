package Evaluation;

import Simulator.SkipSimParameters;
import SkipGraph.Landmarks;
import SkipGraph.Node;
import SkipGraph.Nodes;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

public class AvailabilityEvaluationTest extends SkipSimParameters
{
    Landmarks mLandmarks;
    Nodes mNodes;

    @Before
    public void SetUp()
    {
        TopologyNumbers = 100;
        mLandmarks = new Landmarks();
        ReplicationTime = 0;
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


    @Test
    public void noReplicaOneDataOwner()
    {
        /*
        One data owner, no replica, hence we should have average availability of zero
         */
        sDataOwnerNumber = 1;
        LifeTime = 10;
        ReplicationEvaluation.availabilityEvaluation(0, mNodes, "Test");
        Assert.assertEquals(ReplicationEvaluation.getAverageAvailableReplicas(0), 0, 0);
    }

    @Test
    public void oneAlwaysAvailableReplicaOneDataOwner()
    {
        /*
        One data owner, one replica, hence we should have average availability of 1
         */
        sDataOwnerNumber = 1;
        LifeTime = 10;
        CurrentTopologyIndex = 1;
        Random random = new Random();
        int randomNodeIndex = random.nextInt(SkipSimParameters.getSystemCapacity() - 1);
        Node node = (Node) mNodes.getNode(randomNodeIndex);
        node.setAsReplica(0);
        for(int time = 0; time < LifeTime; time++)
        {
            ReplicationEvaluation.availabilityEvaluation(time, mNodes, "Test");
            Assert.assertEquals(ReplicationEvaluation.getAverageAvailableReplicas(time), 1, 0);
        }
        /*
        We assert the topology index against 0 due to the backward compatibility of SkipSim, i.e., topology indices start from 1
        but indexed from 0.
         */
        Assert.assertEquals(ReplicationEvaluation.getTopologyAverageAvailableReplicas(0), 1,0);
    }

    @Test
    public void oneHalfAvailableReplicaOneDataOwner()
    {
        /*
        One data owner, one replica, hence we should have average availability of 1
         */
        sDataOwnerNumber = 1;
        LifeTime = 10;
        CurrentTopologyIndex = 1;
        /*
        Replicating on a randomly chosen node
         */
        Random random = new Random();
        int randomNodeIndex = random.nextInt(SkipSimParameters.getSystemCapacity() - 1);
        Node node = (Node) mNodes.getNode(randomNodeIndex);
        node.setAsReplica(0);

        for(int time = 0; time < LifeTime; time++)
        {
            /*
            The node is fluctuating between the online and offline states half of the times i.e., online on even time slots
            and offline on the odd ones
             */
            if(time % 2 == 0)
                node.setOnline();
            else
                node.setOffline();
            ReplicationEvaluation.availabilityEvaluation(time, mNodes, "Test");
            Assert.assertEquals(ReplicationEvaluation.getAverageAvailableReplicas(time), node.isOnline()?1:0, 0);
        }
        /*
        We assert the topology index against 0 due to the backward compatibility of SkipSim, i.e., topology indices start from 1
        but indexed from 0.
         */
        Assert.assertEquals(ReplicationEvaluation.getTopologyAverageAvailableReplicas(0), 0.5,0);
    }

    @Test
    public void noReplicaMayDataOwner()
    {
        /*
        Two data owners, no replica, hence we should have average availability of zero
         */
        sDataOwnerNumber = 2;
        LifeTime = 10;
        ReplicationEvaluation.availabilityEvaluation(0, mNodes, "Test");
        Assert.assertEquals(ReplicationEvaluation.getAverageAvailableReplicas(0), 0, 0);
    }

    /**
     * Two data owners, and one always available replica for only one of them, no replica for the other one, hence the
     * average availability of 0.5 overall
     */
    @Test
    public void oneAlwaysAvailableReplicaTwoDataOwner()
    {
        /*
        Two data owners, one replica only for one of them, hence we should have average availability of 0.5
         */
        sDataOwnerNumber = 2;
        LifeTime = 10;
        CurrentTopologyIndex = 1;
        Random random = new Random();
        int randomNodeIndex = random.nextInt(SkipSimParameters.getSystemCapacity() - 1);
        Node node = (Node) mNodes.getNode(randomNodeIndex);
        node.setAsReplica(0);
        for(int time = 0; time < LifeTime; time++)
        {
            ReplicationEvaluation.availabilityEvaluation(time, mNodes, "Test");
            Assert.assertEquals(ReplicationEvaluation.getAverageAvailableReplicas(time), 0.5, 0);
        }
        /*
        We assert the topology index against 0 due to the backward compatibility of SkipSim, i.e., topology indices start from 1
        but indexed from 0.
         */
        Assert.assertEquals(ReplicationEvaluation.getTopologyAverageAvailableReplicas(0), 0.5,0);
    }


    /**
     * There are two data owners, one of them only is assigned a replica, and that replica is halfly available.
     * So, we should be having the average availability of 0.5 per each time slot, and 0.25 overall
     */
    @Test
    public void oneHalfAvailableReplicaTwoDataOwners()
    {
        /*
        Two data owners, one replica only for one of them
         */
        sDataOwnerNumber = 2;
        LifeTime = 10;
        CurrentTopologyIndex = 1;
        /*
        Replicating on a randomly chosen node
         */
        Random random = new Random();
        int randomNodeIndex = random.nextInt(SkipSimParameters.getSystemCapacity() - 1);
        Node node = (Node) mNodes.getNode(randomNodeIndex);
        node.setAsReplica(0);

        for(int time = 0; time < LifeTime; time++)
        {
            /*
            The replica is fluctuating between the online and offline states half of the times i.e., online on even time slots
            and offline on the odd ones
             */
            if(time % 2 == 0)
                node.setOnline();
            else
                node.setOffline();
            ReplicationEvaluation.availabilityEvaluation(time, mNodes, "Test");
            Assert.assertEquals(ReplicationEvaluation.getAverageAvailableReplicas(time), node.isOnline()?0.5:0, 0);
        }
        /*
        We assert the topology index against 0 due to the backward compatibility of SkipSim, i.e., topology indices start from 1
        but indexed from 0.
        Note: We put a non-zero delta (i.e., error) for the average availability since
         */
        Assert.assertEquals(ReplicationEvaluation.getTopologyAverageAvailableReplicas(0), 0.25,0.05);
    }


    /**
     * There are two data owners, and both of them have an always available replica each, hence we have the availability of 1 per
     * each time slot and overall.
     */
    @Test
    public void twoAlwaysAvailableReplicasTwoDataOwners()
    {
        /*
        Two data owners, one replica per each data owner
         */
        sDataOwnerNumber = 2;
        LifeTime = 10;
        CurrentTopologyIndex = 1;
        /*
        Replicating on a randomly chosen node for each data owner
         */
        Random random = new Random();
        int randomNodeIndex = random.nextInt(SkipSimParameters.getSystemCapacity() - 1);
        Node node1 = (Node) mNodes.getNode(randomNodeIndex);
        node1.setAsReplica(0);


        randomNodeIndex = random.nextInt(SkipSimParameters.getSystemCapacity() - 1);
        Node node2 = (Node) mNodes.getNode(randomNodeIndex);
        node2.setAsReplica(1);

        for(int time = 0; time < LifeTime; time++)
        {
            ReplicationEvaluation.availabilityEvaluation(time, mNodes, "Test");
            Assert.assertEquals(ReplicationEvaluation.getAverageAvailableReplicas(time), 1, 0);
        }
        /*
        We assert the topology index against 0 due to the backward compatibility of SkipSim, i.e., topology indices start from 1
        but indexed from 0.
        Note: We put a non-zero delta (i.e., error) for the average availability since
         */
        Assert.assertEquals(ReplicationEvaluation.getTopologyAverageAvailableReplicas(0), 1,0);
    }

    /**
     * There are two data owners, each with an anti-correlated replica assigned.
     * Each data owner has one replicas.
     * Two replicas have anti-correlated behavior with respect to each other, which
     * results in one being always online whenever the other one is offline. Hence, it
     * makes the average availability of 0.5 replica per each data owner at each time slot and overall.
     */
    @Test
    public void twoAntiCorrelatedReplicaTwoDataOwners()
    {
        /*
        Two data owners, one replica only for one of them
         */
        sDataOwnerNumber = 2;
        LifeTime = 10;
        CurrentTopologyIndex = 1;
        /*
        Replicating on a randomly chosen node for each data owner
         */
        Random random = new Random();
        int randomNodeIndex = random.nextInt(SkipSimParameters.getSystemCapacity() - 1);
        Node node1 = (Node) mNodes.getNode(randomNodeIndex);
        node1.setAsReplica(0);


        randomNodeIndex = random.nextInt(SkipSimParameters.getSystemCapacity() - 1);
        Node node2 = (Node) mNodes.getNode(randomNodeIndex);
        node2.setAsReplica(1);

        for(int time = 0; time < LifeTime; time++)
        {
            /*
            The replica is fluctuating between the online and offline states half of the times i.e., online on even time slots
            and offline on the odd ones
             */
            if(time % 2 == 0)
            {
                node1.setOnline();
                node2.setOffline();
            }
            else
            {
                node1.setOffline();
                node2.setOnline();
            }
            ReplicationEvaluation.availabilityEvaluation(time, mNodes, "Test");
            Assert.assertEquals(ReplicationEvaluation.getAverageAvailableReplicas(time), 0.5, 0);
        }
        /*
        We assert the topology index against 0 due to the backward compatibility of SkipSim, i.e., topology indices start from 1
        but indexed from 0.
        Note: We put a non-zero delta (i.e., error) for the average availability since
         */
        Assert.assertEquals(ReplicationEvaluation.getTopologyAverageAvailableReplicas(0), 0.5,0.05);
    }

    /**
     * There are one data owner, and two replicas for it.
     * Two replicas have anti-correlated behavior with respect to each other, which
     * results in one being always online whenever the other one is offline. Hence, it
     * makes the average availability of 1 replica per each data owner at each time slot and overall.
     */
    @Test
    public void twoAntiCorrolatedReplicasOneDataOwner()
    {
        /*
        Two data owners, one replica only for one of them
         */
        sDataOwnerNumber = 1;
        LifeTime = 10;
        CurrentTopologyIndex = 1;
        /*
        Replicating on a randomly chosen node for each data owner
         */
        Random random = new Random();
        int randomNodeIndex = random.nextInt(SkipSimParameters.getSystemCapacity() - 1);
        Node node1 = (Node) mNodes.getNode(randomNodeIndex);
        node1.setAsReplica(0);


        randomNodeIndex = random.nextInt(SkipSimParameters.getSystemCapacity() - 1);
        Node node2 = (Node) mNodes.getNode(randomNodeIndex);
        node2.setAsReplica(0);

        for(int time = 0; time < LifeTime; time++)
        {
            /*
            The replica is fluctuating between the online and offline states half of the times i.e., online on even time slots
            and offline on the odd ones
             */
            if(time % 2 == 0)
            {
                node1.setOnline();
                node2.setOffline();
            }
            else
            {
                node1.setOffline();
                node2.setOnline();
            }
            ReplicationEvaluation.availabilityEvaluation(time, mNodes, "Test");
            Assert.assertEquals(ReplicationEvaluation.getAverageAvailableReplicas(time), 1, 0);
        }
        /*
        We assert the topology index against 0 due to the backward compatibility of SkipSim, i.e., topology indices start from 1
        but indexed from 0.
        Note: We put a non-zero delta (i.e., error) for the average availability since
         */
        Assert.assertEquals(ReplicationEvaluation.getTopologyAverageAvailableReplicas(0), 1,0.05);
    }




}
