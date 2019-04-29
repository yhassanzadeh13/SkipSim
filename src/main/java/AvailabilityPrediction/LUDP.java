package AvailabilityPrediction;
import Simulator.SkipSimParameters;

public class LUDP extends AvailabilityPrediction
{
    /**
     * A counter that holds the number of incoming connections to the Node, it is only used in dynamic adversarial simulations
     * with LUDP as availability predictor, also it ONLY works for search for numerical ID.
     */
    private double incomingConnectionsNumber;
    private int lastUpdatedTime;
    private double availabilityProbability;
    /**
     * Age of the Node that corresponds to the overall time slots that it is online
     */
    private int age;

    @Override
    public void updateState(boolean state, int currentTime)
    {
        /*
        Increase the age if the Node is online
         */
        if(state)
        {
            age++;
        }
        if(lastUpdatedTime < currentTime)
        {
            lastUpdatedTime = currentTime;
            availabilityProbability = ((incomingConnectionsNumber * age)/ (SkipSimParameters.getSystemCapacity() * SkipSimParameters.getLifeTime()));
            incomingConnectionsNumber = 0;
        }
        updatePredictionError(state, currentTime, availabilityProbability, true);
    }

    @Override
    public double getAvailabilityProbability()
    {
        return availabilityProbability;
    }

    /**
     * Increase number of incoming connections by one, read the descriptions on incomming connections number
     */
    public void incrementIncomingConnections()
    {
        incomingConnectionsNumber++;
    }

    public double getIncomingConnectionsNumber()
    {
        return incomingConnectionsNumber;
    }

    public LUDP()
    {
        incomingConnectionsNumber = 0;
        lastUpdatedTime = 0;
        availabilityProbability = 0;
        age = 0;
    }
}
