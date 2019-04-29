package Aggregation;

import org.junit.Assert;
import org.junit.Test;

import java.util.Hashtable;

public class AutoKMeanClusteringTest
{
    /**
     * This function tests the auto K-Mean clustering for only one data point
     */
    @Test
    public void oneDataTest()
    {
        Hashtable<String, double[]> dataPoints = new Hashtable<>();
        dataPoints.put("000", new double[]{0,0});

        Hashtable<String, Integer> clusterMap = AutoKMeanClustering.AutoKMeanClustering(dataPoints, 1);
        /*
        The auto k-mean clustering of a single data point should have the size of 1
         */
        Assert.assertTrue(clusterMap.size() == 1);
        /*
        The data point should assign to cluster number 1
         */
        Assert.assertTrue(clusterMap.get("000") == 1);
    }

    /**
     * This function tests the K-Mean clustering for two very far data points from each others but clusters them into same group
     */
    @Test
    public void twoDataOneGroupTest()
    {
        Hashtable<String, double[]> dataPoints = new Hashtable<>();
        dataPoints.put("000", new double[]{0,0});
        dataPoints.put("001", new double[]{100,100});

        Hashtable<String, Integer> clusterMap = AutoKMeanClustering.AutoKMeanClustering(dataPoints, 1);
        /*
        The auto k-mean clustering of a single data point should have the size of 1
         */
        Assert.assertTrue(clusterMap.size() == 2);
        /*
        The data points should be assigned to the clusters number 1 and 2
         */
        Assert.assertTrue(clusterMap.get("000") == 1);
        Assert.assertTrue(clusterMap.get("001") == 1);
    }

    /**
     * This function tests the auto K-Mean clustering for three points and two clusters
     */
    @Test
    public void threeDataTwoClusterTest()
    {
        Hashtable<String, double[]> dataPoints = new Hashtable<>();
        dataPoints.put("000", new double[]{0,0});
        dataPoints.put("001", new double[]{100,100});
        dataPoints.put("010", new double[]{10,10});

        Hashtable<String, Integer> clusterMap = AutoKMeanClustering.AutoKMeanClustering(dataPoints, 2);
        /*
        The auto k-mean clustering of a single data point should have the size of 3
         */
        Assert.assertTrue(clusterMap.size() == 3);
        /*
        The data points 000 and 010 should be assigned to the same group
         */
        Assert.assertEquals(clusterMap.get("000"), clusterMap.get("010"));

        /*
        001 should be in an independent group lonely
         */
        Assert.assertFalse(clusterMap.get("000") == clusterMap.get("001"));
    }

    /**
     * This function tests the auto K-Mean clustering for 5 data points and 4 and 3 cluster sizes
     */
    @Test
    public void fiveDataTest()
    {
        Hashtable<String, double[]> dataPoints = new Hashtable<>();
        dataPoints.put("000", new double[]{0,0});
        dataPoints.put("001", new double[]{100,100});
        dataPoints.put("100", new double[]{50,50});
        dataPoints.put("110", new double[]{100,0});
        dataPoints.put("010", new double[]{10,10});

        /*
        Clustering into 4 groups
         */
        Hashtable<String, Integer> clusterMap = AutoKMeanClustering.AutoKMeanClustering(dataPoints, 4);
        /*
        The auto k-mean clustering of 5 data points point should have the size of 5
         */
        Assert.assertTrue(clusterMap.size() == 5);
        /*
        The data points should be assigned to the clusters number 1 and 2
         */
        Assert.assertEquals(clusterMap.get("000"), clusterMap.get("010"));
        /*
        001 should be added to another cluster alonely, and should be different from the rest
         */
        Assert.assertFalse(clusterMap.get("000") == clusterMap.get("001"));
        Assert.assertFalse(clusterMap.get("000") == clusterMap.get("110"));
        Assert.assertFalse(clusterMap.get("000") == clusterMap.get("100"));

        /*
        Now switching to have only 3 clusters, which should put 000, 010 into the same cluster and 100 and 001 into another
         */

        /*
        Clustering into 3 groups
         */
        clusterMap = AutoKMeanClustering.AutoKMeanClustering(dataPoints, 3);
        /*
        The auto k-mean clustering of 5 data points point should have the size of 5
         */
        Assert.assertTrue(clusterMap.size() == 5);
        /*
        000 and 010 should be into the same cluster
         */
        Assert.assertEquals(clusterMap.get("000"), clusterMap.get("010"));

        /*
        100 and 001 should be into another cluster
         */
        Assert.assertEquals(clusterMap.get("100"), clusterMap.get("001"));

        /*
        100 and 001 cluster should be different than 000 and 010
         */
        Assert.assertFalse(clusterMap.get("100") == clusterMap.get("010"));

        /*
        110 should be alone into another cluster
         */
        Assert.assertFalse(clusterMap.get("000") == clusterMap.get("110"));
        Assert.assertFalse(clusterMap.get("001") == clusterMap.get("110"));
    }
}