package ChurnStabilization;

import DataTypes.Message;
import SkipGraph.Nodes;

public abstract class ChurnStabilization
{
    public abstract int resolveFailure(Nodes ns, int neighborIndex, int direction, int startIndex, int level, int targetNumId, Message m, int currentTime);
    public abstract void insertIntoBucket(int neighborIndex, int neighborNumId, double onlineProbability, int startIndex, Nodes ns);
    public abstract int  retriveFromBucket(int startIndex, Nodes ns, final int level, int direction, int targetNumID, Message m, int currentTime);


}
