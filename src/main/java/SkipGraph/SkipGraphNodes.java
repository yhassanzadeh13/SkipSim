package SkipGraph;
import Blockchain.LightChain.Transaction;
import Blockchain.LightChain.Transactions;
import DataTypes.Constants;
import DataTypes.Constants.SimulationType;
import Simulator.SkipSimParameters;

public abstract class SkipGraphNodes
{
    /**
     * Stores the Node in the given index into the database
     *
     * @param index         index of the Node to be inserted in the set
     * @param skipGraphNode an instance of the Skip Graph Node object
     */
    public abstract void setNode(int index, SkipGraphNode skipGraphNode);

    /**
     * @param index index of the to be returned Node
     * @return an instance of the Node saved in the location denoted by index
     */
    public abstract SkipGraphNode getNode(int index);

    /**
     * Handles the cooperative departure of the Node i.e., connecting Left and Right neighbors at all levels
     *
     * @param index index of the Node that departs the system
     */
    public void Departure(int index, Transactions transactions)
    {
        int lookupTablesize;
        /*
        Determining the lookup table size
         */
        if (this instanceof Nodes)
        {
            lookupTablesize = SkipSimParameters.getLookupTableSize();
        }
        else
        {
            lookupTablesize = Transaction.LOOKUP_TABLE_SIZE;
        }

        /*
        Perform cooperative departure by connecting neighbors at every level if the churn type is
        COOPERATIVE
         */
        if (SkipSimParameters.getChurnType().equals(Constants.Churn.Type.COOPERATIVE))
        {
            /*
             Connecting successor and predecessor SkipGraph.Nodes together
            */
            for (int i = 0; i < lookupTablesize; i++)
            {
                int leftNeighbor = getNode(index).getLookup(i, 0);
                int rightNeighbor = getNode(index).getLookup(i, 1);

                if (leftNeighbor != -1)
                {
                    getNode(leftNeighbor).setLookup(i, 1, rightNeighbor);
                }
                if (rightNeighbor != -1)
                {
                    getNode(rightNeighbor).setLookup(i, 0, leftNeighbor);
                }
            }
        }

        /*
        Deletes the lookup table
         */
        for (int i = 0; i < lookupTablesize; i++)
            for (int j = 0; j < 2; j++)
            {
                getNode(index).setLookup(i, j, -1);
            }


        /*
        Release the name ID if the Node is a Skip Graph peer
         */
        if (this instanceof Nodes)
        {
//                /*
//                release name ID
//                 */
//            getNode(index).setNameID(new String());

                /*
                make Node offline
                 */
            ((Node) getNode(index)).setOffline();

            /*
            Cooperative departure of blocks and transactions if the churn type is cooperative and
            simulation type is blockchain
             */
            if(SkipSimParameters.getSimulationType().equalsIgnoreCase(SimulationType.BLOCKCHAIN)
                    &&
                    SkipSimParameters.getChurnType().equals(Constants.Churn.Type.COOPERATIVE))
            {
                /*
                Cooperative departure of all the transactions that the Node possesses
                 */
                for(int tx: ((Node) getNode(index)).getTxSet())
                {
                    transactions.Departure(tx, null);
                }

//                /*
//                Cooperative departure of all the blocks that the Node possesses
//                 */
//                for(int tx: ((Node) getNode(index)).getBlockSet())
//                {
//                    blocks.Departure(tx, null, null);
//                }
            }
        }


    }



    public void printLookupNumID(int index)
    {
        for (int i = SkipSimParameters.getLookupTableSize() - 1; i >= 0; i--)
        {
            int right = -1;
            int left = -1;
            if (getNode(index).getLookup(i, 0) != -1)
                left = getNode(getNode(index).getLookup(i, 0)).getNumID();
            if (getNode(index).getLookup(i, 1) != -1)
                right = getNode(getNode(index).getLookup(i, 1)).getNumID();
            System.out.println("Level: " + i + "   Left: " + left + "   Right: " + right);
        }

    }
}
