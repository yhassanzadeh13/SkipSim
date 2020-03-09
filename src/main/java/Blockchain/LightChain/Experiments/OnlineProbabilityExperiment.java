package Blockchain.LightChain.Experiments;

import Simulator.SkipSimParameters;
import SkipGraph.SkipGraphOperations;

import java.util.LinkedList;
import java.util.List;

/**
 * This static class is used to measure the probability of a node being online. This is done by collecting
 * the number of online nodes at each time slot and taking its ratio to the system capacity.
 */
public class OnlineProbabilityExperiment {

    // A list of online node amounts for each time slot.
    private static List<Integer> onlineNodeAmounts = new LinkedList<>();

    /**
     * Calculates the online probability for the given time slot. This method must be called at the
     * end of each time slot.
     * @param sgo SkipGraphOperations object that the calculations should be performed on.
     * @param time current time slot.
     */
    public static void calculateResults(SkipGraphOperations sgo, int time) {
        // Get the current number of online nodes.
        int onlineNodes = sgo.getTG().getNodeSet().getNumberOfOnlineNodes();
        onlineNodeAmounts.add(onlineNodes);
        // Calculate and report the current online probability.
        double probCurrent = (double)onlineNodes/ SkipSimParameters.getSystemCapacity();
        System.out.println("Online Prob. Experiment: For t=" + time + " the prob. of a node being online is " + probCurrent);
        // Calculate and report the overall online probability (over all the time slots).
        double probOverall = onlineNodeAmounts.stream()
                .mapToDouble(x -> (double)x/SkipSimParameters.getSystemCapacity())
                .average()
                .orElse(0);
        System.out.println("Online Prob. Experiment: Overall probability of a node being online is " + probOverall);
    }
}
