package AvailabilityBasedReplication;

import SkipGraph.Nodes;
import SkipGraph.SkipGraphOperations;

public abstract class AvailavilityBasedReplication
{
    public abstract void Algorithm(int dataOwnerIndex, int replicationDegree, SkipGraphOperations sgo);
}
