package SkipGraph;
import java.util.Random;


public abstract class LookupEvaluation
{
    public static final int SEARCH_FOR_NUMERICAL_ID = 0;
    public static final int SEARCH_FOR_NAME_ID = 1;

    /**
     * Keeps track of the total search time incured by randomized lookups for this topology
     */
    protected double topologySearchTime;

    /**
     * Keeps track of the total time of the successful searches for this topology
     */
    protected double topologySuccessfulSearchTime;

    /**
     * Keeps track of the total time of the failed searches for this topology
     */
    protected double topologyFailureSearchTime;

    public double getTopologySearchTime()
    {
        return topologySearchTime;
    }

    public double getTopologySuccessfulSearchTime()
    {
        return topologySuccessfulSearchTime;
    }

    public double getTopologyFailureSearchTime()
    {
        return topologyFailureSearchTime;
    }

    public LookupEvaluation()
    {
        topologySearchTime = 0;
        topologyFailureSearchTime = 0;
        topologySuccessfulSearchTime = 0;
    }

    /**
     * Resets all the time measurements, it is used mainly when SkipSim switches to another time slot.
     */
    protected void resetTimes()
    {
        topologySearchTime = 0;
        topologyFailureSearchTime = 0;
        topologySuccessfulSearchTime = 0;
    }

    /**
     * This function should be invoked at the end of each time slot to reset the time measurements for that time slot
     */
    public abstract void flush();

    /**
     * This function handles all the randomized searches for numerical IDs that are conducted at a single time slot. It
     * should be invoked as the part of randomziedLookupTests
     * @param skipGraphOperations the instance of the Skip Graph under simulation
     * @param random the random generator
     * @param currentTime current time slot of the simulation
     * @return
     */
    protected abstract double randomizedSearchForNumericalIDs(SkipGraphOperations skipGraphOperations, Random random, int currentTime);

    /**
     * This is a function being executed across a simulation to conduct randomized lookup tests, and keep the record of
     * statistics like average success ratio, average search time, average successful search time, average unsuccessful
     * search time, based on the search type, it either makes call to the randomziedSearchForNumericalID or randomizedSearchForNameID
     * @param sgo the instance of the Skip Graph under simulation
     * @param currentTime current time slot of the simulation
     * @param searchType type of the search which directly correlated with the following constant variables of this class:
     *                   SEARCH_FOR_NAME_ID and SEARCH_FOR_NUMERICAL_ID
     */
    public abstract void randomizedLookupTests(SkipGraphOperations sgo, int currentTime, int searchType);
}
