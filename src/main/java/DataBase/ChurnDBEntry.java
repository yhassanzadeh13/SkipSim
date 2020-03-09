package DataBase;

public class ChurnDBEntry
{
    private int nodeIndex;
    private double arrivalTime;
    private double sessionLength;

    public ChurnDBEntry(int nodeIndex, double arrivalTime, double sessionLength)
    {
        this.nodeIndex = nodeIndex;
        this.arrivalTime = arrivalTime;
        this.sessionLength = sessionLength;
    }

    public int getNodeIndex()
    {
        return nodeIndex;
    }

    public double getArrivalTime()
    {
        return arrivalTime;
    }

    public double getSessionLength()
    {
        return sessionLength;
    }
}
