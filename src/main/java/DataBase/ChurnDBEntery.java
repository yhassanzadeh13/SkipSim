package DataBase;

public class ChurnDBEntery
{
    private int nodeIndex;
    private double arrivalTime;
    private double sessionLength;

    public ChurnDBEntery(int nodeIndex, double arrivalTime, double sessionLength)
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
