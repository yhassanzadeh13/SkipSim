package Evaluation;

import Simulator.SkipSimParameters;
import SkipGraph.Node;
import SkipGraph.Nodes;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class LoadEvaluationTest extends SkipSimParameters
{
    private Nodes mNodes;
    @Before
    public void setUp()
    {
        TopologyNumbers = 1;
        CurrentTopologyIndex = 1;
        mNodes = new Nodes(this.getClass());
        //SkipSimParameters.incrementSimIndex();
    }

    @Test
    public void LoadEvaluationNullInput()
    {
        double average = ReplicationEvaluation.loadEvaluation(null);
        Assert.assertEquals(average, -1, 0);
    }

    @Test
    public void LoadEvaluationEmptyInput()
    {

        double average = ReplicationEvaluation.loadEvaluation(mNodes);
        Assert.assertEquals(average, 0, 0);
    }

    @Test
    public void SingleDataOwnerSingleNodeSingleReplicaLoadEvaluation()
    {
        /*
        Only one data owner
         */
        sDataOwnerNumber = 1;
        /*
        Single node in system, and is the replica of the data owner
         */
        Node node1 = new Node(0);
        mNodes.setNode(0, node1);
        ((Node) mNodes.getNode(0)).setAsReplica(0);
        double average = ReplicationEvaluation.loadEvaluation(mNodes);
        Assert.assertEquals(average, 1, 0);
    }

    @Test
    public void SingleDataOwnerMultipleNodeSingleReplicaLoadEvaluation()
    {
        /*
        Only one data owner
         */
        sDataOwnerNumber = 1;
        /*
        3 nodes in the system and only the first node is a replica
         */
        Node node1 = new Node(0);
        Node node2 = new Node(1);
        Node node3 = new Node(2);
        mNodes.setNode(0, node1);
        mNodes.setNode(1, node2);
        mNodes.setNode(2, node3);
        ((Node) mNodes.getNode(0)).setAsReplica(0);
        double average = ReplicationEvaluation.loadEvaluation(mNodes);
        Assert.assertEquals(average, 1, 0);
    }


    @Test
    public void MultipleDataOwnerMultipleNodeMultipleReplicaLoadEvaluation()
    {
        /*
        Two data owners
         */
        sDataOwnerNumber = 2;
        /*
        3 nodes in the system, 1 node is the replica of both
         */
        Node node1 = new Node(0);
        Node node2 = new Node(1);
        Node node3 = new Node(2);
        mNodes.setNode(0, node1);
        mNodes.setNode(1, node2);
        mNodes.setNode(2, node3);
        ((Node) mNodes.getNode(0)).setAsReplica(0);
        ((Node) mNodes.getNode(0)).setAsReplica(1);
        ((Node) mNodes.getNode(1)).setAsReplica(0);
        ((Node) mNodes.getNode(2)).setAsReplica(1);
        double average = ReplicationEvaluation.loadEvaluation(mNodes);
        Assert.assertEquals(average, 1.33, 0.1);
    }

    @Test
    public void LastTopologyLoadEvaluation()
    {
        /*
        Two data owners
         */
        sDataOwnerNumber = 2;
        TopologyNumbers = 100;
        CurrentTopologyIndex  = 100;
        ReplicationEvaluation.setLoadDataSet(0, 0, this.getClass());

        for(int i = 0 ; i < TopologyNumbers - 1; i++)
        {
            ReplicationEvaluation.setLoadDataSet(i, 0, this.getClass());
        }

        Node node1 = new Node(0);
        ((Node) mNodes.getNode(0)).setAsReplica(0);
        double average = ReplicationEvaluation.loadEvaluation(mNodes);
        Assert.assertEquals(average, 0.01, 0.001);
    }
}

