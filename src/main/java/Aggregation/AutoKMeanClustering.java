package Aggregation;

import java.util.Hashtable;

public class AutoKMeanClustering
{

    public static Hashtable<String, Integer> AutoKMeanClustering(Hashtable<String, double[]> dataPoints, int clusterNum)
    {
        /*
        The returned value of the function that returns the mapping between each data point and its cluster
        as data ID --> cluster ID
         */
        Hashtable<String, Integer> clusterMap = new Hashtable<>();

        int k = 0;
        /*
        Hash table of the centers of the clusters
         */
        Hashtable<Integer, double[]> centeroids = new Hashtable<>();

        /*
        Hash table of the cluster sizes
         */
        Hashtable<Integer, Integer> clusterSize = new Hashtable<>();
        for (String center : dataPoints.keySet())
        {
            /*
            Initially put the data point as the kth key
             */
            k++;
            if(k > clusterNum)
                break;
            centeroids.put(k, dataPoints.get(center).clone());
            clusterSize.put(k, 0);

            /*
            Finding the cluster with the minumum error for each data point considering that we have k-many clusters
             */
            for(String key : dataPoints.keySet())
            {
                double minDistance = Double.MAX_VALUE;
                int minClusterIndex = -1;
                for(int clusterID : centeroids.keySet())
                {
                    double distance = distance(centeroids.get(clusterID).clone(), dataPoints.get(key).clone());
                    if(distance < minDistance)
                    {
                        minDistance = distance;
                        minClusterIndex = clusterID;
                    }
                }

               /*
               Adding the data point to the best match cluster
                */
                clusterMap.put(key, minClusterIndex);

               /*
               Updating the center of the cluster after adding the data point
                */
                double[] updatedCenters = centerUpdate(
                        centeroids.get(minClusterIndex).clone(),
                        dataPoints.get(key).clone(),
                        clusterSize.get(minClusterIndex));
                centeroids.put(minClusterIndex, updatedCenters);

               /*
               Updating size of the cluster after adding the data point
                */
                clusterSize.put(minClusterIndex, clusterSize.get(minClusterIndex) + 1);
            }

//            double clusterError = clusterError(dataPoints, clusterMap, centeroids);
//            if(clusterError == 0 ||
//                    oldClusterError == clusterError
//                    || ((oldClusterError != 0) && Math.abs(clusterError - oldClusterError)/oldClusterError < 0.5))
//            {
//                return clusterMap;
//            }
//            oldClusterError = clusterError;

        }
        return clusterMap;
    }

    protected static double clusterError(Hashtable<String, double[]> dataPoints, Hashtable<String, Integer> clusterMap, Hashtable<Integer, double[]> Centeriods)
    {
        /*
        The return value that corresponds to the clustering error of each cluster
         */
        Hashtable<Integer, Double> clusterError = new Hashtable<>();
        /*
        A hash table that keeps the track of each cluster size
         */
        Hashtable<Integer, Integer> clusterSize = new Hashtable<>();
       /*
       Initially all cluster errors are 0
        */
        for (int id : Centeriods.keySet())
        {
            clusterError.put(id, 0.0);
            clusterSize.put(id, 0);
        }

        /*
        Computing sum of error for each cluster
         */
        for(String key : dataPoints.keySet())
        {
            int clusterID = clusterMap.get(key);
            /*
            Updating size of the cluster
             */
            int size = clusterSize.get(clusterID);
            size += 1;
            clusterSize.put(clusterID,  size);

            /*
            Computing the error
             */
            double error = distance(Centeriods.get(clusterID).clone(), dataPoints.get(key).clone());
            double currentError = clusterError.get(clusterID);
            currentError += error;
            clusterError.put(clusterID, currentError);
        }

        /*
        Computing the average error for each cluster as well as the overall average
         */
        double overallError = 0;
        for(int id : clusterError.keySet())
        {
            double error = clusterError.get(id);
            error /= clusterSize.get(id);
            clusterError.put(id, error);
            overallError += error;
        }



        return overallError/clusterSize.size();
    }

    /**
     * Returns the Euclidean distance between two vectors x and y
     *
     * @param x the first vector
     * @param y the second vector
     * @return the Euclidean distance
     */
    protected static double distance(double[] x, double[] y)
    {
        if (x.length != y.length)
            throw new IllegalStateException("AutoKMeanClustering.java: unable to measure the distance of vectors with different dimensions.");

        double distance = 0;
        for (int i = 0; i < x.length; i++)
        {
            distance += Math.pow(x[i] - y[i], 2);
        }

        return Math.sqrt(distance);
    }


    protected static double[] centerUpdate(double[] center, final double[] data, int clusterSize)
    {
        if (center.length != data.length)
            throw new IllegalStateException("AutoKMeanClustering.java: unable to update the center of cluster with a data of a different dimension.");

        for (int i = 0; i < center.length; i++)
        {
            double d = data[i];
            center[i] = ((center[i] * clusterSize) + d)/(clusterSize + 1);
        }

        return center;
    }

}
