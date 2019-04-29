package SkipGraphTest;

import DataTypes.Message;
import Evaluation.ReplicationEvaluation;
import Simulator.SkipSimParameters;
import SkipGraph.Landmarks;
import SkipGraph.Node;
import SkipGraph.Nodes;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

public class AvailabilityVectorTest extends SkipSimParameters
{
    Landmarks mLandmarks;
    Nodes mNodes;

    @Before
    public void SetUp()
    {
        TopologyNumbers = 100;
        sFPTI = 24;

        mNodes = new Nodes(this.getClass());
        for (int i = 0; i < SkipSimParameters.getSystemCapacity(); i++)
        {
            ((Node) mNodes.getNode(i)).setIndex(i);
            ((Node) mNodes.getNode(i)).setOnline();
        }
    }
    /**
     * All nodes are online and hence all them should have availability vectors of all 0
     */
    @Test
    public void allNodesOffline()
    {
        LifeTime = 48;
        CurrentTopologyIndex = 1;
        /*
        Replicating on a randomly chosen node for each data owner
         */

        for(int time = 0; time < LifeTime; time++)
        {
            for (int i = 0; i < SkipSimParameters.getSystemCapacity(); i++)
            {
                ((Node) mNodes.getNode(i)).setOffline();
                ((Node) mNodes.getNode(i)).updateAvailabilityState(time);
            }
        }
        for (int i = 0; i < SkipSimParameters.getSystemCapacity(); i++)
        {
            ((Node) mNodes.getNode(i)).setOffline();
            double[] av = ((Node) mNodes.getNode(i)).getAvailabilityVector();
            for(int time = 0; time < sFPTI; time++)
            {
                Assert.assertEquals(av[time], 0, 0);
            }
        }
    }

    /**
     * All nodes are online and hence all them should have availability vectors of all 1
     */
    @Test
    public void allNodesOnline()
    {
        LifeTime = 48;
        CurrentTopologyIndex = 1;
        /*
        Replicating on a randomly chosen node for each data owner
         */

        for(int time = 0; time < LifeTime; time++)
        {
            for (int i = 0; i < SkipSimParameters.getSystemCapacity(); i++)
            {
                ((Node) mNodes.getNode(i)).setOnline();
                ((Node) mNodes.getNode(i)).updateAvailabilityState(time);
            }
        }
        for (int i = 0; i < SkipSimParameters.getSystemCapacity(); i++)
        {
            ((Node) mNodes.getNode(i)).setOffline();
            double[] av = ((Node) mNodes.getNode(i)).getAvailabilityVector();
            for(int time = 0; time < sFPTI; time++)
            {
                Assert.assertEquals(av[time], 1, 0);
            }
        }
    }

    /**
     * All nodes are online only within the even time slots and offline within the odd time slots
     */
    @Test
    public void allNodesHalfOnline()
    {
        LifeTime = 48;
        CurrentTopologyIndex = 1;
        /*
        Replicating on a randomly chosen node for each data owner
         */

        for(int time = 0; time < LifeTime; time++)
        {
            for (int i = 0; i < SkipSimParameters.getSystemCapacity(); i++)
            {
                if(time % 2 == 0)
                    ((Node) mNodes.getNode(i)).setOnline();
                else
                    ((Node) mNodes.getNode(i)).setOffline();
                ((Node) mNodes.getNode(i)).updateAvailabilityState(time);
            }
        }
        for (int i = 0; i < SkipSimParameters.getSystemCapacity(); i++)
        {
            ((Node) mNodes.getNode(i)).setOffline();
            double[] av = ((Node) mNodes.getNode(i)).getAvailabilityVector();
            for(int time = 0; time < sFPTI; time++)
            {
                /*
                All nodes should have availability probability of 1 in even time slots
                 */
                if(time % 2 == 0)  Assert.assertEquals(av[time], 1, 0);
                /*
                All nodes should have availability probability of 0 in odd time slots
                 */
                else Assert.assertEquals(av[time], 0, 0);

            }
        }
    }


    /**
     * All nodes are online only within the first 24 hours and then all are offline, so their availability probability
     * per each time slot should be 0.5
     */
    @Test
    public void allNodesHalfOnlinePerTimeSlot()
    {
        LifeTime = 48;
        CurrentTopologyIndex = 1;
        /*
        Replicating on a randomly chosen node for each data owner
         */

        for(int time = 0; time < LifeTime; time++)
        {
            for (int i = 0; i < SkipSimParameters.getSystemCapacity(); i++)
            {
                if(time < 24)
                    ((Node) mNodes.getNode(i)).setOnline();
                else
                    ((Node) mNodes.getNode(i)).setOffline();
                ((Node) mNodes.getNode(i)).updateAvailabilityState(time);
            }
        }
        for (int i = 0; i < SkipSimParameters.getSystemCapacity(); i++)
        {
            ((Node) mNodes.getNode(i)).setOffline();
            double[] av = ((Node) mNodes.getNode(i)).getAvailabilityVector();
            for(int time = 0; time < sFPTI; time++)
            {
                Assert.assertEquals(av[time], 0.5, 0);
            }
        }
    }

    /**
     * All nodes are only available within the first 24 hours of the enitre 72 hours life time, hence the availability probability
     * should be 0.3333
     */
    @Test
    public void allNodesOneThirdPerTimeSlot()
    {
        LifeTime = 72;
        CurrentTopologyIndex = 1;
        /*
        Replicating on a randomly chosen node for each data owner
         */

        for(int time = 0; time < LifeTime; time++)
        {
            for (int i = 0; i < SkipSimParameters.getSystemCapacity(); i++)
            {
                if(time < 24)
                    ((Node) mNodes.getNode(i)).setOnline();
                else
                    ((Node) mNodes.getNode(i)).setOffline();
                ((Node) mNodes.getNode(i)).updateAvailabilityState(time);
            }
        }
        for (int i = 0; i < SkipSimParameters.getSystemCapacity(); i++)
        {
            ((Node) mNodes.getNode(i)).setOffline();
            double[] av = ((Node) mNodes.getNode(i)).getAvailabilityVector();
            for(int time = 0; time < sFPTI; time++)
            {
                Assert.assertEquals(av[time], 0.33, 0.03);
            }
        }
    }


    /**
     * All nodes are available all the time slots before the 24th one, and then get unavailable.
     * 4 nodes contribute to a search path at the times 24 and 72.
     * Correctness of the piggybacking of the availability vectors of all them is being evaluated
     */
    @Test
    public void piggyBackTest()
    {
        LifeTime = 72;
        CurrentTopologyIndex = 1;
        /*
        Replicating on a randomly chosen node for each data owner
         */

        Node node1 = (Node) mNodes.getNode(10);
        Node node2 = (Node) mNodes.getNode(20);
        Node node3 = (Node) mNodes.getNode(30);
        Node node4 = (Node) mNodes.getNode(40);


        double[] av24 = new double[sFPTI];
        Message message = new Message();

        for(int time = 0; time < LifeTime; time++)
        {
            for (int i = 0; i < SkipSimParameters.getSystemCapacity(); i++)
            {
                if(time < 24)
                {
                    ((Node) mNodes.getNode(i)).setOnline();
                }
                else
                    ((Node) mNodes.getNode(i)).setOffline();
                ((Node) mNodes.getNode(i)).updateAvailabilityState(time);
            }
            /*
            At the 24th time slot, there is a search starting from node1 to node4 passing from nodes 2 and 3 and
            hence their availability tables are being piggybacked
             */
            if(time == 23)
            {
                message.piggyback(node1.getIndex(), mNodes);
                message.piggyback(node2.getIndex(), mNodes);
                message.piggyback(node3.getIndex(), mNodes);
                message.piggyback(node4.getIndex(), mNodes);
                /*
                Availability vector of all the nodes should be av24
                 */
                av24 = node4.getAvailabilityVector().clone();
            }
        }

        /*
        Checking all the nodes are piggybacked successfully to the message
         */
        Assert.assertTrue(message.contains(node1.getIndex()));
        Assert.assertTrue(message.contains(node2.getIndex()));
        Assert.assertTrue(message.contains(node3.getIndex()));
        Assert.assertTrue(message.contains(node4.getIndex()));


        /*
        Node 1 should not have any update from any node since it is the started of the search path
         */
        Assert.assertFalse(node1.getAvailabilityTable().containsKey(node2.getIndex()));
        Assert.assertFalse(node1.getAvailabilityTable().containsKey(node3.getIndex()));
        Assert.assertFalse(node1.getAvailabilityTable().containsKey(node4.getIndex()));

        /*
        Node 2 should only have the piggybacked table of node 1, and its availability vector of node 1
        should be all one, because at the time of piggybacking, it was all one
         */
        Assert.assertTrue(node2.getAvailabilityTable().containsKey(node1.getIndex()));
        Assert.assertTrue(Arrays.equals(node2.getAvailabilityTable().get(node1.getIndex()), av24));
        Assert.assertFalse(node2.getAvailabilityTable().containsKey(node3.getIndex()));
        Assert.assertFalse(node2.getAvailabilityTable().containsKey(node4.getIndex()));

        /*
        Node 3 should only have the piggybacked table of node 1 and 2 and its availability vector of nodes 1 and 2
        should be all one, because at the time of piggybacking, it was all one
         */
        Assert.assertTrue(node3.getAvailabilityTable().containsKey(node1.getIndex()));
        Assert.assertTrue(Arrays.equals(node3.getAvailabilityTable().get(node1.getIndex()), av24));
        Assert.assertTrue(node3.getAvailabilityTable().containsKey(node2.getIndex()));
        Assert.assertTrue(Arrays.equals(node3.getAvailabilityTable().get(node2.getIndex()), av24));
        Assert.assertFalse(node3.getAvailabilityTable().containsKey(node4.getIndex()));

        /*
        Node 4 should have the piggybacked tables of nodes 1, 2, and 3 and its availability vector of these nodes
        should be all one, because at the time of piggybacking, it was all one
         */
        Assert.assertTrue(node4.getAvailabilityTable().containsKey(node1.getIndex()));
        Assert.assertTrue(Arrays.equals(node4.getAvailabilityTable().get(node1.getIndex()), av24));
        Assert.assertTrue(node4.getAvailabilityTable().containsKey(node2.getIndex()));
        Assert.assertTrue(Arrays.equals(node4.getAvailabilityTable().get(node2.getIndex()), av24));
        Assert.assertTrue(node4.getAvailabilityTable().containsKey(node3.getIndex()));
        Assert.assertTrue(Arrays.equals(node4.getAvailabilityTable().get(node3.getIndex()), av24));

        /*
        Another piggybacking that is happening at time 72
        A search message that is started from node1, passes nodes 2 and 3, and reaches node 4.
         */
        message = new Message();
        message.piggyback(node1.getIndex(), mNodes);
        message.piggyback(node2.getIndex(), mNodes);
        message.piggyback(node3.getIndex(), mNodes);
        message.piggyback(node4.getIndex(), mNodes);
        /*
        Availability vector of all the nodes should be the same as av72
         */
        double[] av72 = node4.getAvailabilityVector();
        Assert.assertTrue(Arrays.equals(av72, node1.getAvailabilityVector()));
        Assert.assertTrue(Arrays.equals(av72, node2.getAvailabilityVector()));
        Assert.assertTrue(Arrays.equals(av72, node3.getAvailabilityVector()));
        Assert.assertFalse(Arrays.equals(av24, node4.getAvailabilityVector()));
        Assert.assertFalse(Arrays.equals(av24, node1.getAvailabilityVector()));
        Assert.assertFalse(Arrays.equals(av24, node2.getAvailabilityVector()));
        Assert.assertFalse(Arrays.equals(av24, node3.getAvailabilityVector()));
        Assert.assertFalse(Arrays.equals(av24, node4.getAvailabilityVector()));

        /*
        Node 1 should not have any update from any node since it is the started of the search path
         */
        Assert.assertFalse(node1.getAvailabilityTable().containsKey(node2.getIndex()));
        Assert.assertFalse(node1.getAvailabilityTable().containsKey(node3.getIndex()));
        Assert.assertFalse(node1.getAvailabilityTable().containsKey(node4.getIndex()));

        /*
        Node 2 should only have the piggybacked table of node 1, and its availability vector of node 1
        should be all one, because at the time of piggybacking, it was all one. Also, for all the nodes, it should be
        having the latest piggybached availability vector, which is av72, and not av24
         */
        Assert.assertTrue(node2.getAvailabilityTable().containsKey(node1.getIndex()));
        Assert.assertTrue(Arrays.equals(node2.getAvailabilityTable().get(node1.getIndex()), av72));
        Assert.assertFalse(Arrays.equals(node2.getAvailabilityTable().get(node1.getIndex()), av24));
        Assert.assertFalse(node2.getAvailabilityTable().containsKey(node3.getIndex()));
        Assert.assertFalse(node2.getAvailabilityTable().containsKey(node4.getIndex()));

        /*
        Node 3 should only have the piggybacked table of node 1 and 2 and its availability vector of nodes 1 and 2
        should be all one, because at the time of piggybacking, it was all one. Also, for all the nodes, it should be
        having the latest piggybached availability vector, which is av72, and not av24.
         */
        Assert.assertTrue(node3.getAvailabilityTable().containsKey(node1.getIndex()));
        Assert.assertTrue(Arrays.equals(node3.getAvailabilityTable().get(node1.getIndex()), av72));
        Assert.assertFalse(Arrays.equals(node3.getAvailabilityTable().get(node1.getIndex()), av24));
        Assert.assertTrue(node3.getAvailabilityTable().containsKey(node2.getIndex()));
        Assert.assertTrue(Arrays.equals(node3.getAvailabilityTable().get(node2.getIndex()), av72));
        Assert.assertFalse(Arrays.equals(node3.getAvailabilityTable().get(node2.getIndex()), av24));
        Assert.assertFalse(node3.getAvailabilityTable().containsKey(node4.getIndex()));

        /*
        Node 4 should have the piggybacked tables of nodes 1, 2, and 3 and its availability vector of these nodes
        should be all one, because at the time of piggybacking, it was all one. Also, for all the nodes, it should be
        having the latest piggybached availability vector, which is av72, and not av24
         */
        Assert.assertTrue(node4.getAvailabilityTable().containsKey(node1.getIndex()));
        Assert.assertTrue(Arrays.equals(node4.getAvailabilityTable().get(node1.getIndex()), av72));
        Assert.assertFalse(Arrays.equals(node4.getAvailabilityTable().get(node1.getIndex()), av24));
        Assert.assertTrue(node4.getAvailabilityTable().containsKey(node2.getIndex()));
        Assert.assertTrue(Arrays.equals(node4.getAvailabilityTable().get(node2.getIndex()), av72));
        Assert.assertFalse(Arrays.equals(node4.getAvailabilityTable().get(node2.getIndex()), av24));
        Assert.assertTrue(node4.getAvailabilityTable().containsKey(node3.getIndex()));
        Assert.assertTrue(Arrays.equals(node4.getAvailabilityTable().get(node3.getIndex()), av72));
        Assert.assertFalse(Arrays.equals(node4.getAvailabilityTable().get(node3.getIndex()), av24));
    }

}
