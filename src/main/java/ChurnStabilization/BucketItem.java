package ChurnStabilization;

public class BucketItem
{
    int nodeIndex;
    int numericalID;
    double onlineProbability;

    public BucketItem()
    {
        nodeIndex = -1;
        numericalID = -1;
        onlineProbability = 0;
    }

    public BucketItem(int nodeIndex, int numericalID, double onlineProbability)
    {
        this.nodeIndex = nodeIndex;
        this.numericalID = numericalID;
        this.onlineProbability = onlineProbability;
    }

    public int getNodeIndex()
    {
        return nodeIndex;
    }

    public int getNumericalID()
    {
        return numericalID;
    }


    public double getOnlineProbability()
    {
        return onlineProbability;
    }
}
