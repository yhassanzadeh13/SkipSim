package ChurnStabilization;

import DataTypes.Constants;
import DataTypes.Message;
import Simulator.SkipSimParameters;
import SkipGraph.Node;
import SkipGraph.Nodes;

import java.util.ArrayList;

public class Kademlia extends ChurnStabilization
{
    //protected static final double levelBucketSize = (double) (system.getBackupTableEntrySize() * system.getLookupTableSize() * 2) / (Math.pow(2, system.getLookupTableSize()+1) - 1);
    @Override
    public int resolveFailure(Nodes ns, int neighborIndex, int direction, int startIndex, int level, int targetNumId, Message m, int currentTime)
    {
        if (SkipSimParameters.getSimulationType().equalsIgnoreCase(Constants.SimulationType.DYNAMIC)
                && (neighborIndex == -1 || ((Node) ns.getNode(neighborIndex)).isOffline()))
//                ||
//                ((mNodeSet.getNode(neighborIndex).getNumID() > mNodeSet.getNode(startIndex).getNumID() && direction == 0)
//                        || (mNodeSet.getNode(neighborIndex).getNumID() < mNodeSet.getNode(startIndex).getNumID() && direction == 1))
//                        || (mNodeSet.getNode(neighborIndex).getNumID() < targetNumId && direction == 0)
//                        || (mNodeSet.getNode(neighborIndex).getNumID() > targetNumId && direction == 1)))

        {
//            if(mNodeSet.getNode(neighborIndex).isOffline())
//            {
//                new ChurnStochastics().updateAverageResolveFailureTimeOuts();
//            }
            //int direction =  (targetNumId < tg.mNodeSet.getNode(startIndex).getNumID()) ? 0:1;
            //System.out.println("SkipGraphOperations.java: Search for num ID returns " + startIndex);
            int backupIndex = retriveFromBucket(startIndex, ns, level, direction, targetNumId, m, currentTime);
            if (backupIndex >= 0)
            {
                return backupIndex;
            } else
            {
                return -1;
            }
        } else
        {
            if (((Node) ns.getNode(neighborIndex)).isOffline())
            {
                return -1;
            }
            return neighborIndex;
        }
    }

    @Override
    public void insertIntoBucket(int neighborIndex, int neighborNumId, double onlineProbability, int startIndex, Nodes ns)
    {

        if (SkipSimParameters.getBackupTableEntrySize() == 0)
        {
            return;
        }
        if (startIndex == neighborIndex) // to prevent self loop a Node cannot insert itself in its backup table
        {
            return;
        }

        int direction;
        if (neighborNumId >= ns.getNode(startIndex).getNumID())
        {
            direction = 1;
        } else
        {
            direction = 0;
        }

        int i = ns.commonBits(startIndex, neighborIndex);
        //for (int i = 0; i <= mNodeSet.commonPrefixLength(startIndex, neighborIndex); i++) //insert in buckets all over levels
        {

        /*
        Checking for duplicates, if happens does no insert
         */
            for (BucketItem e : ((Node) ns.getNode(startIndex)).getBucket(i, direction))
            {
                if (e.getNodeIndex() == neighborIndex)
                {
                    return;
                }
            }

            while (((Node) ns.getNode(startIndex)).getBucket(i, direction).size() >= SkipSimParameters.getBackupTableEntrySize())
            {
                ((Node) ns.getNode(startIndex)).getBucket(i, direction).removeLast();
//            NormalDistribution d = new NormalDistribution(numID, 10 * system.getSystemCapacity());
//            AbstractMap.SimpleEntry<Integer, Integer> minProb = null;
//            double minCDF = Double.MAX_VALUE;
//            for(AbstractMap.SimpleEntry<Integer, Integer> e : bucket[level][direction])
//            {
//                double cdf = d.cumulativeProbability(e.getKey());
//                if(cdf < minCDF)
//                {
//                    minCDF = cdf;
//                    minProb = e;
//                }
//
//
//            }
//            if(minProb != null)
//            {
//                bucket[level][direction].remove(minProb);
//            }

            }
            ((Node) ns.getNode(startIndex))
                    .getBucket(i, direction)
                    .addFirst(new BucketItem(neighborIndex, neighborNumId, ((Node) ns.getNode(neighborIndex)).getAvailabilityProbability()));
        }
    }

    /**
     * @param startIndex  index of the current Node
     * @param targetNumID the search target num ID
     * @param ns          Nodes object of Skip graph
     * @param level       current level number
     * @param direction   current search direction 0:left 1: right
     * @return the list of bucket Nodes who are legitimate to be on the search path
     */
    protected ArrayList<BucketItem> extractCandidatesToContact(int startIndex, int targetNumID, Nodes ns, int level, int direction)
    {
        ArrayList<BucketItem> candidatesToContact = new ArrayList<>();
        for (BucketItem e : ((Node) ns.getNode(startIndex)).getBucket(level, direction))
        {
            int nodeIndex = e.getNodeIndex();
            Node n = (Node) ns.getNode(nodeIndex);
            if ((n.getNumID() <= targetNumID && direction == 1) || (n.getNumID() >= targetNumID && direction == 0))
            {
                candidatesToContact.add(e);
            }
        }

        //System.out.println("kademlia.java: CandidateToContact size " + candidatesToContact.size());
        return candidatesToContact;
    }

    @Override
    public int retriveFromBucket(int startIndex, Nodes ns, int level, int direction, int targetNumID, Message m, int currentTime)
    {
        int returnIndex = -1;

        if (((Node) ns.getNode(startIndex)).getBucket(level, direction).size() == 0)
        {
            return returnIndex;
        }

        ArrayList<BucketItem> candidatesToContact = extractCandidatesToContact(startIndex, targetNumID, ns, level, direction);
        returnIndex = contactCandidates(candidatesToContact, targetNumID, startIndex, level, direction, ns, m, currentTime);

//        if (returnIndex == -1 && level == 0)
//        {
//            candidatesToContact = rescueJump(startIndex, mNodeSet, level, direction, targetNumID, m);
//            returnIndex = contactCandidates(candidatesToContact, targetNumID, startIndex, level, direction, mNodeSet, m, currentTime);
//        }
        //System.out.println("bucket size " + mNodeSet.getNode(startIndex).getBucket(level, direction).size() + " candidates size " + candidatesToContact.size());


//        if(returnIndex == -1 && level == 0)
//        {
//            System.out.println(bucket[level][direction].toString() + " level " + level + " offlines " + offlineNodes.size() + " index " + index);
//        }
        return returnIndex;
    }

    protected int contactCandidates(ArrayList<BucketItem> candidatesToContact, int targetNumID, int startIndex, int level, int direction, Nodes ns, Message m, int currentTime)
    {
        int returnIndex = -1;
        ArrayList<BucketItem> offlineNodes = new ArrayList<>();
        if (!candidatesToContact.isEmpty())
        {
            ChurnStochastics.updateAverageRoutingCandidates(candidatesToContact.size());
            ChurnStochastics.updateAverageOfflineRoutingCandidates(ns, candidatesToContact);
            ChurnStochastics.updateAverageBucketSize(((Node) ns.getNode(startIndex)).getBucket(level, direction).size());
        }
        for (BucketItem e : candidatesToContact)
        {


            if (!m.contains(e.nodeIndex))
            {
                /*
                adding contact delay for most likely online Node
               */
                ns.addTime(startIndex, e.getNodeIndex());
                if (((Node) ns.getNode(e.getNodeIndex())).isOnline())
                {
                    returnIndex = e.getNodeIndex();
                    if (e.getNumericalID() == targetNumID)
                    {
                        break;
                    }
                }
            } else if (((Node) ns.getNode(e.getNodeIndex())).isOffline())
            {
                offlineNodes.add(e);
                ChurnStochastics.updateAverageTimeOuts();
            }


            ArrayList<BucketItem> toBeRemovedCandidates = new ArrayList<>();
            for (BucketItem element : ((Node) ns.getNode(startIndex)).getBucket(level, direction))
            {
                if (offlineNodes.contains(ns.getNode(e.getNodeIndex())))
                {
                    toBeRemovedCandidates.add(element);
                }
            }

            for (BucketItem element : toBeRemovedCandidates)
            {
                //new ChurnStochastics().updateAverageResolveFailureTimeOuts();
                //System.out.println(bucket[level][direction].toString());
                ((Node) ns.getNode(startIndex)).getBucket(level, direction).remove(element);
                //System.out.println(bucket[level][direction].toString());
                ((Node) ns.getNode(startIndex)).getBucket(level, direction).addLast(element);
                //
            }
        }
        return returnIndex;
    }

//    protected ArrayList<BucketItem> rescueJump(int startIndex, Nodes mNodeSet, int level, int direction, int targetNumID, Message m)
//    {
//        ArrayList<BucketItem> candidatesToContact = new ArrayList<>();
//        Node startNode = mNodeSet.getNode(startIndex);
//        for (int i = 0; i < system.getLookupTableSize(); i++)
//        {
//            if (i == level)
//            {
//                continue;
//            }
//            for (int j = 0; j < 2; j++)
//            {
//                for (BucketItem e : mNodeSet.getNode(startIndex).getBucket(i, j))
//                {
//                    int nodeIndex = e.getNodeIndex();
//                    if (m.contains(nodeIndex))
//                    {
//                        continue;
//                    }
//
//                    if ((e.getNumericalID() <= targetNumID && direction == 1) || (e.getNumericalID() >= targetNumID && direction == 0))
//                    {
//                        candidatesToContact.add(new BucketItem(e.getNodeIndex(), e.getNumericalID(), e.getOnlineProbability()));
//                    }
//                }
//            }
//        }
//            return candidatesToContact;
//    }


//    public void printBucket()
//    {
//        System.out.println("Bucket: " + index);
//        for (int level = system.getLookupTableSize() - 1; level >= 0; level--)
//        {
//            System.out.println(level + " " + bucket[level][0].toString() + " " + bucket[level][1].toString());
//        }
//    }
}

