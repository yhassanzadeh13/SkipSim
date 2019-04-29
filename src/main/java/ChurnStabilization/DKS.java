package ChurnStabilization;

import DataTypes.Message;
import Simulator.SkipSimParameters;
import SkipGraph.Node;
import SkipGraph.Nodes;

import java.util.ArrayList;
import java.util.HashSet;

public class DKS extends Kademlia
{
    private static HashSet<Integer> updatedNodesSet = new HashSet<>();

    @Override
    public void insertIntoBucket(int neighborIndex, int neighborNumId, double onlineProbability, int startIndex, Nodes ns)
    {
        if(!updatedNodesSet.contains(startIndex))
        {
            for (int i = 0; i < SkipSimParameters.getLookupTableSize(); i++)
                for (int j = 0; j < 2; j++)
                {
                /*
                Discard this level and direction if the bucket if full
                 */
                    if (((Node) ns.getNode(startIndex)).getBucket(i, j).size() >= SkipSimParameters.getBackupTableEntrySize())
                        continue;
                    neighborIndex = ns.getNode(startIndex).getLookup(i, j);
                    if (neighborIndex == -1)
                        continue;
                    else
                    {

                        int neighborOfNeighborIndex = -1;
                        if (!((Node) ns.getNode(neighborIndex)).getBucket(i, j).isEmpty())
                        {
                            for (int pointerOnNeighborBucket = 0; pointerOnNeighborBucket < ((Node) ns.getNode(neighborIndex)).getBucket(i, j).size(); pointerOnNeighborBucket++)
                            {
                                neighborOfNeighborIndex = ((Node) ns.getNode(neighborIndex)).getBucket(i, j).get(pointerOnNeighborBucket).getNodeIndex();
                                if (neighborOfNeighborIndex != -1
                                        && !((Node) ns.getNode(startIndex)).bucketContains(neighborOfNeighborIndex)
                                        && !((Node) ns.getNode(startIndex)).lookupContains(neighborOfNeighborIndex))
                                {
                                    break;
                                }
                            }
                        }
                        else
                        {
                            neighborOfNeighborIndex = ns.getNode(neighborIndex).getLookup(i, j);
                        }

                    /*
                    If there exists a neighbor at distance 2 and it is not already in bucket and lookup table of the startIndex and also is not equal to the startIndex Node itself
                     */
                        if (neighborOfNeighborIndex != -1 && neighborOfNeighborIndex != startIndex)
                        {
                            ((Node) ns.getNode(startIndex)).getBucket(i, j).add(new BucketItem(neighborOfNeighborIndex, ns.getNode(neighborOfNeighborIndex).getNumID(), 0));
                            updatedNodesSet.add(startIndex);
                        }
                    }
                }
        }

    }

    @Override
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
            }
            else if (((Node) ns.getNode(e.getNodeIndex())).isOffline())
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
                ((Node) ns.getNode(startIndex)).getBucket(level, direction).remove(element);
            }
        }
        return returnIndex;
    }

    public DKS()
    {

    }

    public static void resetUpdateLock()
    {
        updatedNodesSet = new HashSet<>();
    }
}
