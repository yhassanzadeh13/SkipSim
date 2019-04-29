package AvailabilityPrediction;

public class LifeTimePredictor extends AvailabilityPrediction
{
    private double totalUpTime;
    private double availabilityProbability;
    @Override
    public void updateState(boolean state, int currentTime)
    {
        if(currentTime != 0)
        {
            availabilityProbability = totalUpTime/currentTime;
            updatePredictionError(state, currentTime, availabilityProbability, true);
        }
        totalUpTime += (state) ? 1:0;

    }

    @Override
    public double getAvailabilityProbability()
    {
        return availabilityProbability;
    }

    public LifeTimePredictor()
    {
        super();
        this.totalUpTime = 0;
        this.availabilityProbability = 0;
    }
}
