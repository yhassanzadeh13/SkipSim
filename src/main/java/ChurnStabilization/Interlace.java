package ChurnStabilization;

import AvailabilityPrediction.BruijnGraph;
import DataTypes.Message;
import Simulator.SkipSimParameters;
import SkipGraph.Node;
import SkipGraph.Nodes;

import java.util.*;

public class Interlace extends Kademlia
{

    @Override
    public void insertIntoBucket(int neighborIndex, int neighborNumId, double onlineProbability, int startIndex, Nodes ns)
    {
        /*
        Do not store a Node in bucket if its availability probability is too low
         */
        if (onlineProbability <= Math.abs(BruijnGraph.MINIMUM_PROB + ((Node) ns.getNode(neighborIndex)).getAvailabilityPredictor().getAveragePredictionError())
                || onlineProbability == BruijnGraph.MINIMUM_PROB)
        {
            return;
        }
        /*
        Do not store a Node in bucket if premissible bucket size is zero
         */
        if (SkipSimParameters.getBackupTableEntrySize() == 0)
        {
            return;
        }

        /*
        To prevent self loop a Node cannot insert itself in its backup table
         */
        if (startIndex == neighborIndex)
        {
            return;
        }


        /*
        Direction of insertion (left or right)
         */
        final int direction;
        if (neighborNumId >= ns.getNode(startIndex).getNumID())
        {
            direction = 1;
        }
        else
        {
            direction = 0;
        }

        final int i = ns.commonBits(startIndex, neighborIndex);


        int currentBucketSize = ((Node) ns.getNode(startIndex)).removeFromBucket(neighborIndex);
        int maxBucketSize = 2 * SkipSimParameters.getBackupTableEntrySize() * SkipSimParameters.getLookupTableSize();
        if (currentBucketSize >= maxBucketSize)
        {
            // mNodeSet.getNode(startIndex).getBucket(i, direction).removeLast();
            //NormalDistribution d = new NormalDistribution(mNodeSet.getNode(startIndex).getNumID(), system.getBucketSD() * system.getSystemCapacity());
            //AbstractMap.SimpleEntry<Integer, Integer> minProb = null;
            //double minCDF = Double.MAX_VALUE;
            //ArrayList<AbstractMap.SimpleEntry<Integer, Integer>> seed = new ArrayList<>();
            //int startIndexNumID = mNodeSet.getNode(startIndex).getNumID();
            ArrayList<BucketItem> inputList = new ArrayList<>();
            for (BucketItem e : ((Node) ns.getNode(startIndex)).getBucket(i, direction))
            {
                inputList.add(e);
            }

            //inputList = huristicSort(inputList, mNodeSet.getNode(startIndex).getNumID(), false);
            /*
                if one direction keeps all the quota and the other one is empty, the full one should be refined
             */
            while (((Node) ns.getNode(startIndex))
                    .removeFromBucket(huristicSort(((Node) ns.getNode(startIndex)).bucketToArrayList(), ns.getNode(startIndex).getNumID(), ns, startIndex).get(maxBucketSize - 1).getNodeIndex()) >= maxBucketSize) ;
        }
        ((Node) ns.getNode(startIndex)).getBucket(i, direction).addFirst(new BucketItem(neighborIndex, neighborNumId, onlineProbability));
        ((Node) ns.getNode(startIndex)).increaseHistogram(neighborIndex);
        if (onlineProbability < 0)
        {
            System.err.println("Interlace.java/InsertIntoBucket negative online probability");
            System.exit(0);
        }

    }


    @Override
    protected int contactCandidates(ArrayList<BucketItem> candidatesToContact, int targetNumID, int startIndex, int level, int direction, Nodes ns, Message m, int currentTime)
    {
        /*
        A call to the LatencyTableUpdate that will update the latency experience of the Nodes.
         */
        ((Node) ns.getNode(startIndex)).LatencyTableUpdate(currentTime);

        /*
        Updating churn stochastics features
         */
        if (!candidatesToContact.isEmpty())
        {
            ChurnStochastics.updateAverageRoutingCandidates(candidatesToContact.size());
            ChurnStochastics.updateAverageOfflineRoutingCandidates(ns, candidatesToContact);
            ChurnStochastics.updateAverageBucketSize(((Node) ns.getNode(startIndex)).getBucket(level, direction).size());
        }

        /*
        A check for the num ID match
         */
        for (int i = 0; i < candidatesToContact.size(); i++)
        {
            if (candidatesToContact.get(i).getNumericalID() == targetNumID)
            {
                return candidatesToContact.get(i).getNodeIndex();
            }
        }

        candidatesToContact = huristicSort(candidatesToContact, targetNumID, ns, startIndex);


        while (!candidatesToContact.isEmpty())
        {

            Node mostLikelyOnline = (Node) ns.getNode(candidatesToContact.get(0).getNodeIndex());
            //if (mostLikelyOnline.isOnline() && mostLikelyOnline.getBucket(level,direction).size() > 0)
            if (mostLikelyOnline == null)
            {
                //mNodeSet.getNode(startIndex).setLastFailed(level, direction, targetNumID);
                return -1;
            }
            else if (mostLikelyOnline.isOnline())
            {
                //System.out.println("First was online");
                ((Node) ns.getNode(startIndex))
                        .bucketProbUpdate(mostLikelyOnline.getIndex(), true, ((Node) ns.getNode(startIndex)).getCoordinate().distance(mostLikelyOnline.getCoordinate()), currentTime);
                return mostLikelyOnline.getIndex();
            }
            else
            {
                //System.out.println("First was offline");
                candidatesToContact.remove(candidatesToContact.get(0));
                ((Node) ns.getNode(startIndex)).removeFromBucket(mostLikelyOnline.getIndex());
                ((Node) ns.getNode(startIndex))
                        .bucketProbUpdate(mostLikelyOnline.getIndex(), false, ((Node) ns.getNode(startIndex)).getCoordinate().distance(mostLikelyOnline.getCoordinate()), currentTime);
                ChurnStochastics.updateAverageTimeOuts();
            }

            /*
            adding contact delay for most likely online Node
             */
            ns.addTime(startIndex, mostLikelyOnline.getIndex());
        }
        //mNodeSet.getNode(startIndex).setLastFailed(level, direction, targetNumID);
        return -1;

    }

    /**
     * This function is called during join and re-joining of a Node to the system, which pulls all the bucket elements
     * of its introducer a well as neighbors
     * @param nodeIndex
     * @param ns
     */
    public void pullBucket(int nodeIndex, Nodes ns)
    {
        Node n = (Node) ns.getNode(nodeIndex);
        LinkedList<BucketItem> bucekt = ((Node) ns.getNode(n.getIntroducer())).bucketToLinkedList();
        for (BucketItem e : bucekt)
        {
            insertIntoBucket(e.getNodeIndex(), e.getNumericalID(), e.getOnlineProbability(), nodeIndex, ns);
        }
        for (int i = 0; i < SkipSimParameters.getLookupTableSize(); i++)
            for (int j = 0; j < 2; j++)
            {
                if (n.getLookup(i, j) >= 0 && ((Node) ns.getNode(n.getLookup(i, j))).getBucket(i, j).size() >= 0)
                {
                    bucekt = ((Node) ns.getNode(n.getLookup(i, j))).bucketToLinkedList();
                    for (BucketItem e : bucekt)
                    {
                        insertIntoBucket(e.getNodeIndex(), e.getNumericalID(), e.getOnlineProbability(), nodeIndex, ns);
                    }
                }
            }
    }


    /**
     * This function is invoked upon re-joining of a Node to the system, as the Node may obtain a different name ID, its
     * backup table will be redistribute into different levels based on the new name ID of the Node
     *
     * @param nodeIndex the index of the Node
     * @param ns        a Nodes instance object that corresponds to the database of all the Nodes
     */
    public void rebalanceBucket(int nodeIndex, Nodes ns)
    {
        Node n = (Node) ns.getNode(nodeIndex);
        ArrayList<BucketItem> rebalancedNodes = new ArrayList<>();
        for (int i = 0; i < SkipSimParameters.getLookupTableSize(); i++)
            for (int j = 0; j < 2; j++)
            {
                if (n.getBucket(i, j).size() > 0)
                {
                    for (BucketItem e : n.getBucket(i, j))
                    {
                        if (ns.commonBits(e.getNodeIndex(), nodeIndex) != i)
                        {
                            rebalancedNodes.add(e);
                        }
                    }
                }
            }
        for (BucketItem e : rebalancedNodes)
        {
            for (int i = 0; i < SkipSimParameters.getLookupTableSize(); i++)
                for (int j = 0; j < 2; j++)
                {
                    if (n.getBucket(i, j).contains(e))
                    {
                        n.getBucket(i, j).remove(e);
                        break;
                    }
                }
            insertIntoBucket(e.getNodeIndex(), e.getNumericalID(), e.getOnlineProbability(), nodeIndex, ns);
        }
    }

    private ArrayList<BucketItem> huristicSort(ArrayList<BucketItem> inputList, int targetNumID, Nodes ns, int startIndex)
    {
        try
        {
            Collections.sort(inputList, new Comparator<BucketItem>()
            {

                @Override
                public int compare(BucketItem o1, BucketItem o2)
                {

                    double scoreO1 = 0;
                    double scoreO2 = 0;
                    //System.out.println(o1.getOnlineProbability() + " " + o2.getOnlineProbability());
                    scoreO1 = o1.getOnlineProbability() * (ns.commonBits(o1.nodeIndex, startIndex) + 1) / (Math.abs(o1.getNumericalID() - targetNumID) + 1);
                    scoreO2 = o2.getOnlineProbability() * (ns.commonBits(o2.nodeIndex, startIndex) + 1) / (Math.abs(o2.getNumericalID() - targetNumID) + 1);
                    if (scoreO1 > scoreO2)
                    {
                        return -1;
                    }
                    else if (scoreO1 < scoreO2)
                    {
                        return 1;
                    }
                    else
                    {
                        //return -Integer.compare(numIDDistanceRank.get(o1.getNodeIndex()), numIDDistanceRank.get(o2.getNodeIndex()));
                        return 0;
                    }

                }
            });
        }
        catch (IllegalArgumentException ex)
        {
            System.err.println("Interlace.java: huristicSort exception");
            ex.printStackTrace();
            System.exit(0);
        }

        return inputList;
}


}
