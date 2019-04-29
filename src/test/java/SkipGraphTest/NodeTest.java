package SkipGraphTest;

import SkipGraph.Landmarks;
import SkipGraph.Node;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;

public class NodeTest
{
    Landmarks mLandmarks;
    @Before
    public void SetUp()
    {
        mLandmarks = new Landmarks();
        /*
        Generating landmarks
         */
        mLandmarks.generatingLandmarks();
    }

    @Test
    public void closestLandmarkTest()
    {
        /*
        Defining a new node
         */
        Node node = new Node(0);
        /*
        Adjusting the coordinate of new node on the landmark 0
         */
        node.setCoordinate(mLandmarks.getLandmarkCoordination(0));
        Assert.assertEquals(node.getClosetLandmarkIndex(mLandmarks), 0);
        /*
        Slightly moving the coordinate of new from the landmark 0
         */
        node.setCoordinate(new Point(mLandmarks.getLandmarkCoordination(0).x - 1,  mLandmarks.getLandmarkCoordination(0).y - 1));
        Assert.assertEquals(node.getClosetLandmarkIndex(mLandmarks), 0);

        /*
        We need to redeclare node to reset its state of closest landmark
         */
        node = new Node(0);
        /*
        Slightly moving the coordinate of new from the landmark 1
         */
        node.setCoordinate(new Point(mLandmarks.getLandmarkCoordination(1).x - 1,  mLandmarks.getLandmarkCoordination(1).y - 1));
        Assert.assertEquals(node.getClosetLandmarkIndex(mLandmarks), 1);
    }

    /**
     * Evaluates the normalized storage capacity of the node after getting replicas
     */
    @Test
    public void storageCapacity()
    {
        /*
        Defining a new node
         */
        Node node = new Node(0);
        /*
        Adding the storage capacity of 3 to the node
         */
        node.setStorageCapacity(3);

        /*
        Making the node replicas of two other nodes
         */
        node.setAsReplica(1);
        node.setAsReplica(2);

        /*
        Since the node is the replica of two other nodes, and has storage of 3, its available
        normalized storage capacity should be 0.33
         */
        Assert.assertEquals(node.getNormalizedStorageCapacity(), 0.33, 0.01);

    }
}
