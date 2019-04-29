package AvailabilityPrediction;

import ChurnStabilization.ChurnStochastics;
import DataTypes.Constants;
import Simulator.SkipSimParameters;
import org.ujmp.core.Matrix;
import org.ujmp.core.SparseMatrix;

import java.util.Hashtable;
import java.util.Map;

public class BruijnGraph extends AvailabilityPrediction
{
    public static final double MINIMUM_PROB = 0.0001; //system.getChurnStabilizationParameter(0);
    //public static final double UNPREDICTABLE_PROB = -0.0000001; //system.getChurnStabilizationParameter(0);

    private String history;
    private int stateSize;
    //private int totalScore;
    private double resultSum = 0;
    private double mAveragePredictionError;

    /**
     * Binary string of size "stateSize" that holds the current
     */
    private String currentState;
    private Hashtable<String, Double> stateTable;
    private Hashtable<String, Integer> lastStateUpdate;
    private Hashtable<String, Integer> stateVisitTable;
    //private int updates;
    private double stationaryOnlineProbability;
    public BruijnGraph(int size)
    {
        this.stateSize = size;
        //this.totalScore = (int) Math.pow(2, size);
        stateTable = new Hashtable<>();
        lastStateUpdate = new Hashtable<>();
        this.stateVisitTable = new Hashtable<>();
        currentState = new String();
        stationaryOnlineProbability = 0;
        mAveragePredictionError = 0;
        history = new String();
    }

    public Hashtable<String, Integer> getStateVisitTable()
    {
        return stateVisitTable;
    }

    public void setStateVisitTable(Hashtable<String, Integer> stateVisitTable)
    {
        this.stateVisitTable = stateVisitTable;
    }

    public String getCurrentState()
    {
        return currentState;
    }

    public void setCurrentState(String currentState)
    {
        this.currentState = currentState;
    }

//    /**
//     * @return stationary probability of being online
//     */
//    public double getStationaryOnlineProbability()
//    {
//
//        return stationaryOnlineProbability;
//
//    }


    public int getStateSize()
    {
        return stateSize;
    }


    public void updateState(boolean status, int currentTime)
    {
        if (currentState.length() > 0)
        {
            //mAveragePredictionError = updatePredictionError(status, currentTime, stationaryOnlineProbability, SkipSimParameters.getAvailabilityPredictor().equalsIgnoreCase(Constants.Churn.AvailabilityPredictorAlgorithm.DBG));
            mAveragePredictionError = updatePredictionError(status, currentTime, stationaryOnlineProbability, SkipSimParameters.getAvailabilityPredictor().equalsIgnoreCase(Constants.Churn.AvailabilityPredictorAlgorithm.DBG));
        }
        if (currentState.length() == stateSize)
        {
            double score;
            int visitTimes;
            if (!stateTable.containsKey(currentState))
            {
                score = 0;
                visitTimes = 0;
            }
            else
            {
                score = stateTable.get(currentState);
                visitTimes = stateVisitTable.get(currentState);
            }
            if (status == Constants.Churn.ONLINE)
            {
                //if (score < totalScore)
                //{
                score++;
                //}
            }
            else
            {
                //if (score > 1)
                //{
                //score--;
                //}
            }
            /*
            Increase number of visits this Node have
             */
            visitTimes++;
            stateVisitTable.put(currentState, visitTimes);
            stateTable.put(currentState, score);
            lastStateUpdate.put(currentState, currentTime);
        }
        currentState = updateCurrentState(currentState, status);
        //if (status)
        //{
        stationaryOnlineProbability = stationaryOnlineProb(stateTable, stateVisitTable);
        //}

//        else if (status)
//        {
//            stationaryOnlineProbability = stationaryOnlineProb(stateTable);
//        }
    }

    /**
     * @return stationary online probability of DBG
     */
    @Override
    public double getAvailabilityProbability()
    {
//        if(Double.isNaN(stationaryOnlineProbability))
//        {
//            return UNPREDICTABLE_PROB;
//        }
//        else
//        {
        return stationaryOnlineProbability;
//        }
    }

//    private Hashtable<String, Integer> refineStateTable(Hashtable<String, Integer> inputStateTable, int currentTime)
//    {
//        Hashtable<String, Integer> outputStateTable = new Hashtable<>();
//        for (Map.Entry<String, Integer> e : inputStateTable.entrySet())
//        {
//            if (lastStateUpdate.containsKey(e.getKey()) && Math.abs(lastStateUpdate.get(e.getKey()) - currentTime) <= totalScore)
//            {
//                outputStateTable.put(e.getKey(), e.getValue());
//            }
//        }
//
//        for (Map.Entry<String, Integer> e : outputStateTable.entrySet())
//        {
//            if (!outputStateTable.containsKey(updateCurrentState(e.getKey(), Constants.Churn.ONLINE)) && e.getValue() > 0)
//            {
//                outputStateTable.put(e.getKey(), 0);
//            }
//
//            if (!outputStateTable.containsKey(updateCurrentState(e.getKey(), Constants.Churn.OFFLINE)) && e.getValue() == 0)
//            {
//                outputStateTable.put(e.getKey(), totalScore);
//            }
//        }
//
//        return outputStateTable;
//    }

    //TODO
    public double stationaryOnlineProb(Hashtable<String, Double> inputStateTable, Hashtable<String, Integer> inputStateVisitTable)
    {
        if (inputStateTable.size() == 0)
        {
            //if(currentState.endsWith("0"))
            return 0;
            //else return 1;
        }
        if (inputStateTable.size() == 1)
        {
            for (Map.Entry<String, Double> e : inputStateTable.entrySet())
            {
                if (e.getKey().endsWith("0"))
                {
                    return 0;
                }
                else
                {
                    return 1;
                }
            }
        }

        int MatrixSize = (int) Math.pow(2, stateSize);
        Matrix stationaryProbs = SparseMatrix.Factory.zeros(MatrixSize + 1, MatrixSize);

        boolean isZeroAbsorbing = false;
        boolean isOneAbsorbing = false;
        for (int i = 0; i < MatrixSize; i++)
        {
            if (stationaryProbs.getAsDouble(i, i) == 0)
            {
                stationaryProbs.setAsDouble(1, i, i);
            }
        }
        for (Map.Entry<String, Double> e : inputStateTable.entrySet())
        {
            int matrixIndex = Integer.parseInt(e.getKey(), 2);
            stationaryProbs.setAsDouble(1, MatrixSize, matrixIndex);
            if (stationaryProbs.getAsDouble(matrixIndex, matrixIndex) == 0)
            {
                stationaryProbs.setAsDouble(1, matrixIndex, matrixIndex);
            }


            String zeroTransitionState = e.getKey().substring(1, e.getKey().length()) + "0";
            String oneTransitionState = e.getKey().substring(1, e.getKey().length()) + "1";
            int zeroTransitionIndex = Integer.parseInt(zeroTransitionState, 2);
            int oneTransitionIndex = Integer.parseInt(oneTransitionState, 2);
            if (!inputStateVisitTable.containsKey(e.getKey()))
            {
                System.err.println("error");
            }
            double zeroTransitionProb = (double) (inputStateVisitTable.get(e.getKey()) - e.getValue()) / inputStateVisitTable.get(e.getKey());
            double oneTransitionProb = (double) e.getValue() / inputStateVisitTable.get(e.getKey());
            if (isAll("0", e.getKey()))
            {
                if (zeroTransitionProb > 0.99)
                {
                    isZeroAbsorbing = true;
                }
            }
            else if (isAll("1", e.getKey()))
            {
                if (oneTransitionProb > 0.99)
                {

                    isOneAbsorbing = true;
                }
            }
            stationaryProbs = updateMatrix(zeroTransitionProb, stationaryProbs, matrixIndex, MatrixSize, zeroTransitionIndex);
            stationaryProbs = updateMatrix(oneTransitionProb, stationaryProbs, matrixIndex, MatrixSize, oneTransitionIndex);
        }

        if (isZeroAbsorbing)
        {

            return 0;
        }
        if (isOneAbsorbing)
        {

            return 1;
        }

        // Last line of stationary matrix
        for (int i = 0; i < MatrixSize; i++)
        {
            stationaryProbs.setAsDouble(1, MatrixSize, i);
        }

        //Constants Matrix
        Matrix constants = SparseMatrix.Factory.zeros(MatrixSize + 1, 1);
        constants.setAsDouble(1, MatrixSize, 0);


        if (stationaryProbs.isSingular())
        {
            System.exit(0);
        }
        double sop = 0; //stationary online probability
        try
        {

            Matrix results = stationaryProbs.pinv().mtimes(constants);

            /*
    Checking the result's correctness
     */
            if (!(results.getValueSum() >= 0.9 && results.getValueSum() <= 1.0001))
            {
                System.err.println("BruijnGraph.java: stationary distribution total probability " + results.getValueSum());
                System.exit(0);
            }
            for (int i = 0; i < results.getRowCount(); i++)
            {
                String state = Integer.toBinaryString(i);
                while (state.length() < stateSize)
                {
                    state = "0" + state;
                }
                if (state.endsWith("1"))
                {
                    sop += results.getAsDouble(i, 0);
                }

            }
        }
        catch (RuntimeException ex)
        {
            //TODO this exception occurs because of infinity entries in stationaryProbs matrix
            //We need to investigate this issue, that why this happens
            //return UNPREDICTABLE_PROB;
            ex.printStackTrace();
            System.err.println("Matrix size at error state is " + MatrixSize);
            System.err.println(stationaryProbs.toStringMatrix());
            System.exit(0);
        }


        if (sop > 1.0001 || sop < -0.0001)
        {

            System.err.println("BruijnGraph.java: stationary probability " + sop);
            System.exit(0);
        }
        if (sop < 0.0001)
        {
            sop = 0;
        }
        return sop;
    }

    private Matrix updateMatrix(double transitionProb, Matrix stationaryProbs, int matrixIndex, int matrixSize, int transitionIndex)
    {
        if (transitionProb != 0)
        {
            if (transitionIndex == matrixIndex)
            {

                transitionProb = -(1 - transitionProb);
            }
            stationaryProbs.setAsDouble(-transitionProb, transitionIndex, matrixIndex);
            if (stationaryProbs.getAsDouble(transitionIndex, transitionIndex) == 0)
            {
                stationaryProbs.setAsDouble(1, transitionIndex, transitionIndex);
            }
            stationaryProbs.setAsDouble(1, matrixSize, transitionIndex);
        }

        return stationaryProbs;
    }

    private boolean isAll(String c, String state)
    {
        for (int i = 0; i < state.length(); i++)
        {
            if (!c.equals(state.substring(i, i + 1)))
            {
                return false;

            }

        }
        return true;

    }

    public void setStateTable(Hashtable<String, Double> stateTable)
    {
        this.stateTable = stateTable;
    }

    //TODO javadoc
    public Hashtable<String, Double> extendStateTable()
    {
        Hashtable<String, Double> newStateTable = new Hashtable<>();
        for (Map.Entry<String, Double> e : stateTable.entrySet())
        {
            newStateTable.put("0" + e.getKey(), stateTable.get(e.getKey()));
            newStateTable.put("1" + e.getKey(), stateTable.get(e.getKey()));
//            if (stateSize == 1)
//            {
//                double prob = 0;
//                if (stateTable.containsKey("0"))
//                {
//                    prob = stateTable.get("0");
//                }
//                newStateTable.put(e.getKey() + "0", prob);
//
//
//                prob = 0;
//                if (stateTable.containsKey("1"))
//                {
//                    prob = stateTable.get("1");
//
//                }
//                newStateTable.put(e.getKey() + "1", prob);
//
//            }
//            else
//            {
//                int visits;
//                String newKey = e.getKey() + "0";
//                String newKeyExceptMSB = (newKey).substring(1, newKey.length());
//                if (stateTable.containsKey(newKeyExceptMSB))
//                {
//                    newStateTable.put(newKey, stateTable.get(newKeyExceptMSB));
//                }
//                newKey = e.getKey() + "1";
//                newKeyExceptMSB = (newKey).substring(1, newKey.length());
//                if (stateTable.containsKey(newKeyExceptMSB))
//                {
//                    newStateTable.put(newKey, stateTable.get(newKeyExceptMSB));
//                }
//            }
        }

        return newStateTable;
    }


    /**
     * Given the state table, it extends the state visit table by extending each state with a prefix of 0 and
     * with a prefix of one. More details are available in our Interlaced paper.
     *
     * @return extended visit table
     */
    public Hashtable<String, Integer> extendStateVisitTable()
    {
        Hashtable<String, Integer> newStateVisitTable = new Hashtable<>();
        for (Map.Entry<String, Integer> e : stateVisitTable.entrySet())
        {
            newStateVisitTable.put("0" + e.getKey(), stateVisitTable.get(e.getKey()));
            newStateVisitTable.put("1" + e.getKey(), stateVisitTable.get(e.getKey()));
        }

        return newStateVisitTable;
    }


    //TODO javadoc
    public Hashtable<String, Double> shrinkStateTable()
    {
        Hashtable<String, Double> newStateTable = new Hashtable<>();
        for (Map.Entry<String, Double> e : stateTable.entrySet())
        {
            int keyLength = e.getKey().length();
            String shrinkedKey = e.getKey().substring(1, keyLength);
            if (stateTable.containsKey(shrinkedKey))
            {
                continue;
            }
            double score = 0;
            if (stateTable.containsKey("0" + shrinkedKey))
            {
                score += stateTable.get("0" + shrinkedKey);
            }
            if (stateTable.containsKey("1" + shrinkedKey))
            {
                score = (0.5 * score) + (0.5 * stateTable.get("1" + shrinkedKey));
            }
            newStateTable.put(shrinkedKey, Math.ceil(score));
        }
        return newStateTable;
    }

    //TODO javadoc
    public Hashtable<String, Integer> shrinkStateVisitTable()
    {
        Hashtable<String, Integer> newStateVisitTable = new Hashtable<>();
        for (Map.Entry<String, Integer> e : stateVisitTable.entrySet())
        {
            int keyLength = e.getKey().length();
            //String shrinkedKey = e.getKey().substring(0, keyLength - 1);
            String shrinkedKey = e.getKey().substring(1, keyLength);
            double visits = 0;
            if (stateVisitTable.containsKey(shrinkedKey))
            {
                continue;
            }

            String zeroPrefixKey = new String("0") + shrinkedKey;
            //int zeroHashedCode = zeroPrefixKey.hashCode();
            if (stateVisitTable.containsKey(zeroPrefixKey))
            {
                visits += stateVisitTable.get(zeroPrefixKey);
                //System.out.println(shrinkedKey + "  0 visits" + visits);
            }
            String onePrefixKey = new String("1") + shrinkedKey;
            //int oneHashedCode = (onePrefixKey).hashCode();
            if (stateVisitTable.containsKey(onePrefixKey))
            {
                visits = (0.5 * visits) + (0.5 * stateVisitTable.get(onePrefixKey));
                //System.out.println(shrinkedKey + "  1 visits" + visits);
            }

//            if(newStateVisitTable.containsKey(shrinkedKey))
//            {
//                visits += newStateVisitTable.get(shrinkedKey);
//            }
//            if(visits > 0 && visits < 1)
//            {
//                visits = 1;
//            }
            newStateVisitTable.put(shrinkedKey, (int) Math.ceil(visits));
        }
        return newStateVisitTable;
    }

    public double getAveragePredictionError()
    {
        return mAveragePredictionError;
    }

    public String updateCurrentState(String inputCurrentState, boolean status)
    {
        if (inputCurrentState.length() == stateSize)
        {
            if (inputCurrentState.length() == stateSize && stateSize != 1)
            {
                inputCurrentState = inputCurrentState.substring(1);
            }
            else if (stateSize == 1)
            {
                inputCurrentState = new String();
            }
            else if (inputCurrentState.length() > stateSize)
            {
                System.err.println("BruijnGraph.java: Violation, current state length is greater than the state size");
            }
        }

        if (status == Constants.Churn.ONLINE)
        {
            inputCurrentState = inputCurrentState + "1";
        }
        else
        {
            inputCurrentState = inputCurrentState + "0";
        }

        return inputCurrentState;
    }


    @Override
    public double updatePredictionError(boolean state, int currentTime, double availabilityProbability, boolean recordedInStatistics)
    {
        int len = currentState.length();
        int n = Integer.parseInt(currentState, 2);
        int count = 0;
        for (count = 0; n > 0; ++count)
        {
            n &= n - 1;
        }
        double inProb = ((double) count) / len;
        averagePredictionError = Math.abs(inProb - availabilityProbability);
        if (recordedInStatistics)
        {
            ChurnStochastics.updateAveragePredictionError(averagePredictionError, currentTime);
        }
        return averagePredictionError;
    }


}

