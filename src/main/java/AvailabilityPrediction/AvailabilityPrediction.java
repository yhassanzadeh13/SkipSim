package AvailabilityPrediction;

import ChurnStabilization.ChurnStochastics;

public abstract class AvailabilityPrediction
{
    /**
     * Average prediction error of this DBG
     */
    protected double averagePredictionError;
    private double availabilityCounter;
    private String availabilityHistory;

    public AvailabilityPrediction()
    {
        availabilityCounter = 0;
        averagePredictionError = 0;
        availabilityHistory =  new String();
    }

    public abstract void updateState(boolean state, int currentTime);

    public abstract double getAvailabilityProbability();

    public double updatePredictionError(boolean state, int currentTime, double availabilityProbability, boolean recordedInStatistics)
    {

//        if(state)
//        {
//            availabilityCounter++;
//        }
        averagePredictionError = Math.abs(((state)? 1:0) - availabilityProbability);
//        if(currentTime > 0)
//        {
//            averagePredictionError += (((double) availabilityCounter / currentTime) - availabilityProbability);
//        }
        if (recordedInStatistics)
        {
            ChurnStochastics.updateAveragePredictionError(averagePredictionError, currentTime);
        }
        return averagePredictionError;
    }


    public double getAveragePredictionError()
    {
        return averagePredictionError;
    }

//    private double availabilityHistory()
//    {
//        int len = currentState.length();
//        int n = Integer.parseInt(currentState, 2);
//        int count = 0;
//        for (count = 0; n > 0; ++count)
//        {
//            n &= n - 1;
//        }
//        double inProb = ((double) count) / len;
//        averagePredictionError = Math.abs(inProb - availabilityProbability);
//        if (recordedInStatistics)
//        {
//            ChurnStochastics.updateAveragePredictionError(averagePredictionError, currentTime);
//        }
//        return averagePredictionError;
//    }
}
