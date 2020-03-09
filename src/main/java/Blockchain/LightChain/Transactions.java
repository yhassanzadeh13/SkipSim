package Blockchain.LightChain;

import SkipGraph.SkipGraphNode;
import SkipGraph.SkipGraphNodes;

import java.util.Hashtable;

public class Transactions extends SkipGraphNodes
{
    /**
     * Set of all generated transactions in system
     */
    private Hashtable<Integer, Transaction> mTransationSet;

    /**
     * points to the first available empty slot on the mTrasactionsSet
     */
    private int currentBlockSetIndex;

    public Transactions()
    {
        mTransationSet = new Hashtable<>();
        currentBlockSetIndex = 0;
    }

    public int addToSet(Transaction tx)
    {
        //TODO test for proper insertion i.e., avoid replacement
        tx.setIndex(currentBlockSetIndex);
        mTransationSet.put(currentBlockSetIndex, tx);
        currentBlockSetIndex++;
        return tx.getIndex();

    }

    public int getCurrentBlockSetIndex()
    {
        return currentBlockSetIndex;
    }

    public static int commonPrefixLength(String s1, String s2)
    {
        int k = 0;

        if (s1.length() > 0 && s2.length() > 0)
        {
            while (s1.charAt(k) == s2.charAt(k))
            {
                k++;
                if (k >= s1.length() || k >= s2.length())
                {
                    break;
                }
            }
        }

        return k;
    }

    /**
     * Re-writes the index of the transaction set with the denoted block or transaction (i.e., TXB)
     * @param index index to be re-written
     * @param TXB the transaction/block instance
     */
    @Override
    public void setNode(int index, SkipGraphNode TXB)
    {
        mTransationSet.put(index, (Transaction) TXB);
    }

    /**
     *
     * @param index index of the to be returned Node
     * @return instance of the transaction or block stored at index
     */
    @Override
    public SkipGraphNode getNode(int index)
    {
        return mTransationSet.get(index);
    }

    public int size() {
        return mTransationSet.size();
    }

}
