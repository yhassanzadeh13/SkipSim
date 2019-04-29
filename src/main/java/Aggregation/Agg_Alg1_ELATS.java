package Aggregation;

import Simulator.SkipSimParameters;
import SkipGraph.Node;
import SkipGraph.SkipGraphOperations;

import java.util.ArrayList;

/**
 * Created by Yahya on 1/9/2017.
 */
public class Agg_Alg1_ELATS extends Aggregation
{
    //private int threshold = 3;
    private double w1 = 1;
    private double w2 = 1;

//        private double averageLatency;
//        private double averageEnergyCost;
//        private double numOfParents;
//        private double numOfChild;

    //private ArrayList<Integer> subTrees;

    public Agg_Alg1_ELATS(SkipGraphOperations isgo)
    {
        sgo = isgo;
        //subTrees = new ArrayList<>();
//                averageLatency = 0;
//                averageEnergyCost = 0;
//                numOfParents = 0;
//                numOfChild = 0;
        initiator = 0;
        Algorithm();


    }

    private void Algorithm()
    {
        System.out.println("ELATS has started....");

        //initiatorsList = initiators();
        //for(int i = 0 ; i < initiatorsList.size() ; i++)
        ELATS(initiator, new String(), 0, 0, 1, 0);
        //findRoot();
        energyEvaluation("ELATS ");
        latencyEvaluation("ELATS ");
                /*
                The ILP on the roots of subtrees
                 */
        //System.out.println("Merging subtrees");
        //ILP(subTrees);
    }

    private void ELATS(int executer, String subProblem, double averageEnergyCost, double averageLatency, int numOfParents, int numOfChild)
    //private void ELATS(int executer, String subProblem)
    {
        //ArrayList<Integer> subTrees = new ArrayList<>();
        String nameID = ((Node) sgo.getTG().mNodeSet.getNode(executer)).getNameID();
        double totalNameIDs = Math.pow(2, nameID.length() - subProblem.length());

        double coveringAllEnergyCost = (2 * E + ((totalNameIDs - 1) * (2 * E + (U - 1))) + (averageEnergyCost * numOfParents)) / numOfParents;
//            double coveringAllLatencyCost = (
//                    (averageLatency * numOfChild) + localLatencyApproximation(nameID.length() - subProblem.length())) / (numOfChild + totalNameIDs);
        double coveringAllMaxLatency = averageLatency + nameID.length() - subProblem.length();

        double coveringHalfEnergyCost = (2 * E + (totalNameIDs - 2) * (2 * E + (U - 4)) + (averageEnergyCost * numOfParents)) / (numOfParents + 1);
//            double coveringHalfLatencyCost =
//                    ((averageLatency * numOfChild) + 2 * localLatencyApproximation(nameID.length() - subProblem.length() - 1) + nameID.length() - subProblem.length()) / (numOfChild + 1);

        double coveringHalfMaxLatency = averageLatency + (nameID.length() - subProblem.length()) + (nameID.length() - nameID.length() - 1);

        //System.out.println("Energy Costs: Covering All " + coveringAllEnergyCost + " Covering Half " + coveringHalfEnergyCost);
        //System.out.println("Latency Costs: Covering All " + coveringAllLatencyCost + " Covering Half " + coveringHalfLatencyCost);
        double latencyEvaluation = (coveringAllMaxLatency - coveringHalfMaxLatency) / Math.max(coveringAllMaxLatency, coveringHalfMaxLatency);
        double energyEvaluation = (coveringAllEnergyCost - coveringHalfEnergyCost) / Math.max(coveringAllEnergyCost, coveringHalfEnergyCost);
        //double totalEvaluation = w1*latencyEvaluation + w2*energyEvaluation;
        System.out.println("Latency Evaluation " + latencyEvaluation + " Energy Evaluation " + energyEvaluation);
        if (latencyEvaluation < 0.01 || energyEvaluation < 0.01 || subProblem.length() == nameID.length())
        {
            if (energyEvaluation < 0.01)
            {
                System.out.println("Stopped for energy" + subProblem);
            }
            else if (latencyEvaluation < 0.01)
            {
                System.out.println("Stopped for latency" + subProblem);
            }
            else if (subProblem.length() == nameID.length())
            {
                System.out.println("Stopped for subproblem" + subProblem);
            }
            ArrayList<Integer> subDomain = extend(subProblem, nameID);
            for (int i = 0; i < subDomain.size(); i++)
            {
                //if(initiatorsList.contains(subDomain.get(i)))
                if (initiator == subDomain.get(i))
                {
                    continue;
                }
                if (((Node) sgo.getTG().mNodeSet.getNode(subDomain.get(i))).getParent() != -1)
                {
                    continue;
                }
                //System.out.println("Current parent of SkipGraph.Node " + i + " is SkipGraph.Node " + sgo.getTG().mNodeSet.getNode(subDomain.get(i)).getParent());
                ((Node) sgo.getTG().mNodeSet.getNode(executer)).addChildren(subDomain.get(i));
                ((Node) sgo.getTG().mNodeSet.getNode(subDomain.get(i))).setParent(executer);
                System.out.println("SkipGraph.Node " + executer + " became parent of SkipGraph.Node " + subDomain.get(i));
            }

            //averageEnergyCost = coveringAllEnergyCost;
            //averageLatency = coveringAllLatencyCost;
        }


        else
        {
            if (commonBits(nameID, subProblem + "0") == subProblem.length())
            {
                int alpha = -1;
                for (int i = 0; i < SkipSimParameters.getSystemCapacity(); i++)
                {
                    if (commonBits(((Node)sgo.getTG().mNodeSet.getNode(i)).getNameID(), subProblem + "0") > subProblem.length()
                            && ((Node) sgo.getTG().mNodeSet.getNode(i)).getParent() == -1 && i != initiator && i != executer)
                    {
                        alpha = i;
                        break;
                    }
                }
                if (alpha >= 0)
                {
                    ((Node) sgo.getTG().mNodeSet.getNode(executer)).addChildren(alpha);
                    ((Node) sgo.getTG().mNodeSet.getNode(alpha)).setParent(executer);
                    //System.out.println("SkipGraph.Node " + executer + " became parent of SkipGraph.Node " + alpha);
                    ELATS(alpha, subProblem + "0", coveringHalfEnergyCost, coveringHalfMaxLatency, numOfParents + 1, numOfChild + 1);
                    //ELATS(alpha, subProblem + "0");
                }
                else
                {
                    //System.out.println("No SkipGraph.Node alpha was found with prefix " + subProblem + "0");
                }
                ELATS(executer, subProblem + "1", coveringHalfEnergyCost, coveringHalfMaxLatency, numOfParents + 1, numOfChild + 1);
                //ELATS(executer, subProblem + "1");

            }
            else
            {
                int alpha = -1;
                for (int i = 0; i < SkipSimParameters.getSystemCapacity(); i++)
                {
                    if (commonBits(((Node) sgo.getTG().mNodeSet.getNode(i)).getNameID(), subProblem + "1") > subProblem.length()
                            && ((Node) sgo.getTG().mNodeSet.getNode(i)).getParent() == -1 && i != initiator && i != executer)
                    {
                        alpha = i;
                        break;
                    }
                }
                if (alpha >= 0)
                {
                    ((Node) sgo.getTG().mNodeSet.getNode(executer)).addChildren(alpha);
                    ((Node) sgo.getTG().mNodeSet.getNode(alpha)).setParent(executer);
                    //System.out.println("SkipGraph.Node " + executer + " became parent of SkipGraph.Node " + alpha);
                    ELATS(alpha, subProblem + "1", coveringHalfEnergyCost, coveringHalfMaxLatency, numOfParents + 1, numOfChild + 1);
                    //ELATS(alpha, subProblem + "1");
                }
                else
                {
                    //System.out.println("No SkipGraph.Node alpha was found with prefix " + subProblem + "1");
                }
                ELATS(executer, subProblem + "0", coveringHalfEnergyCost, coveringHalfMaxLatency, numOfParents + 1, numOfChild + 1);
                //ELATS(executer, subProblem + "0");
            }

        }


    }


    private ArrayList<Integer> extend(String subProblem, String eNameID)
    {
        ArrayList<Integer> subDomainIndices = new ArrayList<>();
        if (eNameID.equals(subProblem))
        {
            return subDomainIndices;
        }
        for (int j = 0; j < SkipSimParameters.getSystemCapacity(); j++)
        {
            if (subProblem.length() == 0)
            {
                subDomainIndices.add(j);
                continue;
            }
            if (((Node) sgo.getTG().mNodeSet.getNode(j)).getNameID().startsWith(subProblem))
            {//&& sgo.getTG().mNodeSet.getNode(j).getParent() == -1)
                //System.out.println("Node " + j + " with name id " + nameID + " is added to sub domain " + subProblem);
                subDomainIndices.add(j);
            }
        }
        //}
        return subDomainIndices;
    }


    private int localLatencyApproximation(int numberOfBits)
    {
        int localLatency = 0;
        for (int i = numberOfBits; i > 0; i--)
        {
            localLatency += i * Math.pow(2, i - 1);
        }

        return localLatency;
    }


}


