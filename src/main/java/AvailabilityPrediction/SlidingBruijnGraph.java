package AvailabilityPrediction;

import ChurnStabilization.ChurnStochastics;

import java.util.Hashtable;

public class SlidingBruijnGraph extends AvailabilityPrediction
{
    /**
     * Improvement threshold, the current state window moves once the prediction error of right or left graph is IMPROVMENT_THRESHOLD better than the min of center and left or
     * min of center and right graphs, respectively.
     */
    private final double IMPROVMENT_THRESHOLD = 0.05;
    private final int MAX_RIGHT_SIZE = 20;

    /**
     * pointer to the left DBG in the current state window
     */
    private BruijnGraph leftGraph;
    /**
     * pointer to the central DBG in the current state window
     */
    private BruijnGraph centerGraph;
    /**
     * pointer to the right DBG in the current state window
     */
    private BruijnGraph rightGraph;

    /**
     * Best descriptive graph keeps a pointer to the DBG of current state window with minimum prediction error.
     **/
    private BruijnGraph bestDescriptiveBruijnGraph;


    public SlidingBruijnGraph()
    {
        leftGraph = new BruijnGraph(1);
        centerGraph = new BruijnGraph(2);
        rightGraph = new BruijnGraph(3);
        centerGraph.setCurrentState("0");
        rightGraph.setCurrentState("00");
        bestDescriptiveBruijnGraph = leftGraph;
    }

    @Override
    public void updateState(boolean state, int currentTime)
    {
        leftGraph.updateState(state, currentTime);
        centerGraph.updateState(state, currentTime);
        rightGraph.updateState(state, currentTime);


        ChurnStochastics.updateAveragePredictionError(bestDescriptiveBruijnGraph.getAveragePredictionError(), currentTime);
        double leftGraphPredictionError = leftGraph.getAveragePredictionError();
        double rightGraphPredictionError = rightGraph.getAveragePredictionError();
        double centerGraphPredictionError = centerGraph.getAveragePredictionError();
//          int onlineStatus = (state)?1:0;
//          double leftGraphPredictionError = Math.abs(onlineStatus - leftGraph.getAvailabilityProbability());
//          double rightGraphPredictionError = Math.abs(onlineStatus - rightGraph.getAvailabilityProbability());
//          double centerGraphPredictionError = Math.abs(onlineStatus - centerGraph.getAvailabilityProbability());

        /*
        Skip moving the graph if the Node is offline
         */
        if (!state)
            return;

        if (leftGraphPredictionError < -0.001 || rightGraphPredictionError < -0.001 || centerGraphPredictionError < -0.001)
        {
            System.err.println("SlidingBruijnGraph.java: negative score");
            System.exit(0);
        }

        boolean extendedState = false;
        boolean firstExtension = true;
//        while(leftGraph.getAvailabilityProbability() == BruijnGraph.UNPREDICTABLE_PROB
//                || centerGraph.getAvailabilityProbability() == BruijnGraph.UNPREDICTABLE_PROB
//                || rightGraphPredictionError + IMPROVMENT_THRESHOLD  < centerGraphPredictionError
//                || centerGraphPredictionError + IMPROVMENT_THRESHOLD  < leftGraphPredictionError )
//        while(leftGraph.getAvailabilityProbability() == BruijnGraph.UNPREDICTABLE_PROB
//                || centerGraph.getAvailabilityProbability() == BruijnGraph.UNPREDICTABLE_PROB
//                || (rightGraphPredictionError + IMPROVMENT_THRESHOLD  < centerGraphPredictionError
//                && centerGraphPredictionError + IMPROVMENT_THRESHOLD  < leftGraphPredictionError ))
        while (rightGraphPredictionError + IMPROVMENT_THRESHOLD < centerGraphPredictionError
                && centerGraphPredictionError + IMPROVMENT_THRESHOLD < leftGraphPredictionError)
        {
            if (leftGraph.getStateSize() == 1 && firstExtension)
            {
                firstExtension = false;
                //bestDescriptiveBruijnGraph = centerGraph;
            }
            else if (rightGraph.getStateSize() < MAX_RIGHT_SIZE)
            {
                BruijnGraph buffer = new BruijnGraph(rightGraph.getStateSize() + 1);
                Hashtable<String, Double> buffeStateTable = rightGraph.extendStateTable();
                if (buffeStateTable.size() == rightGraph.getStateSize())
                    break;
                Hashtable<String, Integer> buffeStateVisitTable = rightGraph.extendStateVisitTable();
                buffer.setStateVisitTable(buffeStateVisitTable);
                buffer.setStateTable(buffeStateTable);
                buffer.setCurrentState(rightGraph.getCurrentState());


                /*
                Shifting to the right
                 */
                leftGraph = centerGraph;
                centerGraph = rightGraph;
                rightGraph = buffer;
            }
            else
            {

                break;
            }
            //System.out.println(this + " size increased to " + rightGraph.getStateSize());
            leftGraphPredictionError = leftGraph.getAveragePredictionError();
            centerGraphPredictionError = centerGraph.getAveragePredictionError();
            rightGraphPredictionError = rightGraph.getAveragePredictionError();
            extendedState = true;
        }

//        while (!extendedState && (rightGraph.getAvailabilityProbability() == BruijnGraph.UNPREDICTABLE_PROB
//                || centerGraph.getAvailabilityProbability() == BruijnGraph.UNPREDICTABLE_PROB
//                || leftGraphPredictionError    <= centerGraphPredictionError
//                || centerGraphPredictionError  <= rightGraphPredictionError))
//        while (!extendedState && (rightGraph.getAvailabilityProbability() == BruijnGraph.UNPREDICTABLE_PROB
//                || centerGraph.getAvailabilityProbability() == BruijnGraph.UNPREDICTABLE_PROB
//                || (leftGraphPredictionError    <= centerGraphPredictionError
//                && centerGraphPredictionError  <= rightGraphPredictionError)))
        while (!extendedState &&
                (leftGraphPredictionError + IMPROVMENT_THRESHOLD < centerGraphPredictionError
                        && centerGraphPredictionError + IMPROVMENT_THRESHOLD < rightGraphPredictionError))
        {
            if (leftGraph.getStateSize() > 1)
            {
                BruijnGraph buffer = new BruijnGraph(leftGraph.getStateSize() - 1);
                Hashtable<String, Double> buffeStateTable = leftGraph.shrinkStateTable();
                Hashtable<String, Integer> bufferStateVisitTable = leftGraph.shrinkStateVisitTable();
                buffer.setStateTable(buffeStateTable);
                buffer.setStateVisitTable(bufferStateVisitTable);
                buffer.setCurrentState(leftGraph.getCurrentState().substring(1));


                /*
                Shifting to the left
                 */
                rightGraph = centerGraph;
                centerGraph = leftGraph;
                leftGraph = buffer;
            }
            else
            {
                break;
            }
            leftGraphPredictionError = leftGraph.getAveragePredictionError();
            centerGraphPredictionError = centerGraph.getAveragePredictionError();
            rightGraphPredictionError = rightGraph.getAveragePredictionError();
            //System.out.println(this + " size shrinked to " + leftGraph.getStateSize());
        }


        /*
        Updating the best predictive graph based on the average prediction error
         */
        if (leftGraph.getAveragePredictionError() < Math.min(rightGraph.getAveragePredictionError(), centerGraph.getAveragePredictionError()))
        {
            bestDescriptiveBruijnGraph = leftGraph;
        }
        else if (rightGraph.getAveragePredictionError() < Math.min(leftGraph.getAveragePredictionError(), centerGraph.getAveragePredictionError()))
        {
            bestDescriptiveBruijnGraph = rightGraph;
        }
        else
        {
            bestDescriptiveBruijnGraph = centerGraph;
        }

        ChurnStochastics.uptadeAverageBruijnStateSize(bestDescriptiveBruijnGraph.getStateSize());

    }

    @Override
    public double getAvailabilityProbability()
    {
        return bestDescriptiveBruijnGraph.getAvailabilityProbability();
    }

}
