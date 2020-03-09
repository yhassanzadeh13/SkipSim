package Simulator;//package Simulator;
//
//import ChurnStabilization.Interlace;
//import DataBase.ChurnDBEntry;
//import DataTypes.Constants;
//import SkipGraph.*;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.Set;
//
//public class BlockchainSimulation
//{
//
//    SkipGraphOperations sgo;
//    private static double previousArrivalTime; //Keeps record of previous arrival time for churn statistics only when the topology is loaded
//    Set<Integer> arrivedNodes = new HashSet<>();
//
//    // TODO: This constructor had 1 parameter sgo, we added another one called bgo for the SkipGraph#2
//    public BlockchainSimulation(SkipGraph.SkipGraphOperations sgo)
//    {
//        if (!AlgorithmInvoker.isNameIDAssignmentDynamic())
//        {
//            System.err
//                    .println("Dynamic Simulation Error: Cannot perform dynamic simulation with static name id assignment " + "\n change the name id assignment in confix.txt to one of the dynamic algorithms, DPAD is recommended.");
//        }
//        else
//        {
//            this.sgo = sgo;
//        }
//    }
//
//    public boolean isFirstArrival(Node n)
//    {
//        return !arrivedNodes.contains(n.getNumID());
//    }
//
//    public SkipGraphOperations Simulate(boolean generatingTopology, int currentTime, ArrayList<ChurnDBEntry> churnLog)
//    {
//
//        //if (system.isLog())
//        System.out.println("Current time: " + currentTime + " Topology index " + system.getCurrentTopologyIndex());
//        /*
//        If session length and arrival times have already been loaded into the sgo
//         */
//        if (!generatingTopology)
//        {
//            if (currentTime == 0)
//            {
//                previousArrivalTime = 0;
//            }
//            for (ChurnDBEntry entery : churnLog)
//            {
//                int arrivalNodeIndex = entery.getNodeIndex();
//                Node arrivingNode = (Node) sgo.getTG().mNodeSet.getNode(arrivalNodeIndex);
//                arrivingNode.setOnline();
//                arrivingNode.setSessionLength(entery.getSessionLength(), currentTime);
//                ChurnStochastics.updateTotalAverageSessionLength(arrivingNode.getSessionLength());
//                arrivingNode.setNameID(AlgorithmInvoker.dynamicNameIDAssignment(arrivingNode, sgo, arrivalNodeIndex);
//                sgo.insert(arrivingNode, arrivalNodeIndex, AlgorithmInvoker.isNameIDAssignmentDynamic(), currentTime);
//                if (isFirstArrival(arrivingNode))
//                {
//                    arrivedNodes.add(arrivingNode.getNumID());
//                    bgo.getBs().generateBlocks(true, true, bgo, 10, arrivingNode, currentTime, sgo.getTG().mNodeSet);
//                }
//
//                //TODO: Since our simulation will be COOPERATIVE can we delete this IF STATEMENT.
//                if (system.getChurnType().equalsIgnoreCase(Constants.Churn.Type.ADVERSARIAL)
//                        && system.getChurnStabilizationAlgorithm().equalsIgnoreCase(Constants.Churn.ChurnStabilizationAlgorithm.INTERLLACED))
//                {
//                    Interlace interlace = new Interlace();
//                    interlace.rebalanceBucket(arrivingNode.getIndex(), sgo.getTG().mNodeSet);
//                    interlace.pullBucket(arrivingNode.getIndex(), sgo.getTG().mNodeSet);
//                }
//                if (system.isLog())
//                {
//                    arrivingNode.printAvailabilityInfo(currentTime, Constants.Churn.ARRIVAL);
//                    System.out.println("lookup table");
//                    arrivingNode.printLookup();
//                    System.out.println("--------------------------------");
//                }
//
//                ChurnStochastics.updateTotalAverageInterArrivalTime(currentTime - previousArrivalTime);
//                previousArrivalTime = currentTime;
//            }
//        }
//
//            /*
//            No Node has been loaded from database and hence the topology should be generated
//             */
//        else
//        {
//            if (currentTime == 0)
//            {
//                previousArrivalTime = 0;
//                System.out.println("DynamicSimulation.java: Generating the topology");
//                    /*
//                    Generating landmarks
//                    */
//                this.sgo.getTG().mLandmarks.generatingLandmarks();
//                    /*
//                    Generating Nodes
//                     */
//                this.sgo.getTG().mNodeSet.generateNodes(true, false, sgo, currentTime);
//            }
//
//                /*
//                If there exists any arrival at this time
//                 */
//            while (sgo.getTG().getNextArrivalTime() >= currentTime && sgo.getTG().getNextArrivalTime() < currentTime + 1)
//            {
//                if (sgo.getTG().mNodeSet.getNumberOfOfflineNodes() >= 0.01 * system.getSystemCapacity()) //arrival only if we have enough offline Nodes
//                {
//                    /*
//                    An arrival of a Node
//                     */
//                    int arrivalNodeIndex = sgo.getTG().randomlyPickOffline();
//                    Node arrivingNode = sgo.getTG().mNodeSet.getNode(arrivalNodeIndex);
//                    arrivingNode.setOnline();
//                    arrivingNode.setSessionLength(sgo.getTG().generateSessionLength(), currentTime);
//                    ChurnStochastics.updateTotalAverageSessionLength(arrivingNode.getSessionLength());
//                    arrivingNode.nameID = AlgorithmInvoker.dynamicNameIDAssignment(arrivingNode, sgo, arrivalNodeIndex);
//                    sgo.insert(arrivingNode, arrivalNodeIndex, AlgorithmInvoker.isNameIDAssignmentDynamic(), currentTime);
//                    if (isFirstArrival(arrivingNode))
//                    {
//                        arrivedNodes.add(arrivingNode.getNumID());
//                        bgo.getBs().generateBlocks(true, false, bgo, 5, arrivingNode, currentTime, sgo.getTG().mNodeSet);
//                    }
//                    churnLog.add(new ChurnDBEntry(arrivalNodeIndex, sgo.getTG().getNextArrivalTime(), arrivingNode.getSessionLength()));
//                    if (system.isLog())
//                    {
//                        arrivingNode.printAvailabilityInfo(currentTime, Constants.Churn.ARRIVAL);
//                        System.out.println("lookup table");
//                        arrivingNode.printLookup();
//                        System.out.println("--------------------------------");
//                    }
//                    ChurnStochastics.updateTotalAverageInterArrivalTime(currentTime - previousArrivalTime);
//                    previousArrivalTime = currentTime;
//                }
//
//                sgo.getTG().updateNextArrivalTime(currentTime);
//            }
//
//            //System.out.println("Departure update started");
//            //sgo.getTG().departureUpdate(currentTime);
//            //System.out.println("Departure update finished");
//
//        }
//        //TODO: WE WILL LOOK AT THIS PART
//        ble.lookupSuccessTest(bgo, sgo, currentTime);
//
//        //TODO dynamic replications
//        //If Simulator.system reaches the replication time, the dynamic replication algorithm is called given the replication time
////            if (Simulator.system.getCurrentTime() == Simulator.system.getRepTime())
////            {
////                System.out.println("Replication started");
////                long startTime = System.currentTimeMillis();
////                sgo = new Simulator.AlgorithmInvoker().dynamicReplication(sgo);
////                long stopTime = System.currentTimeMillis();
////                long elapsedTime = stopTime - startTime;
////                time += elapsedTime;
////                System.out.println("Replication has done");
////            }
////
////            if (Simulator.system.getCurrentTime() < Simulator.system.getRepTime())
////            {
////                System.out.println("Random lookup is started");
////                //DataTypes.nodesTimeTable.randomizedSearchForNumericalIDs();
////                //DataTypes.nodesTimeTable.allPairsLookup();
////                System.out.println("Random lookup has been done");
////            }
////
////            if (Simulator.system.getCurrentTime() >= Simulator.system.getRepTime())
////            {
////                System.out.println("Replication evaluation has been started");
////                //repEvaluation.numberOfReplicasDynamicReplicationEvaluation(Simulator.system.getCurrentTime());
////                System.out.println("Replication evaluation has been done");
////            }
////TODO ready to detach
////        double counter = 0;
////        //System.out.println("DynamicSimulation.java: Random searches started");
////        if (sgo.getTG().mNodeSet.getNumberOfOnlineNodes() > 20)
////        {
////            for (int i = 0; i < system.getSearchByNumericalID(); i++)
////            {
////                int searchInit = sgo.getTG().randomlyPickOnline();
////                int searchTarger = sgo.getTG().randomlyPickOnline();
////                while (searchTarger == searchInit)
////                {
////                    searchTarger = sgo.getTG().randomlyPickOnline();
////                }
////
////                Message m = new Message();
////                int searchResult = sgo.SearchByNumID(sgo.getTG().mNodeSet.getNode(searchTarger).getNumID(), searchInit, m, system.getLookupTableSize() - 1);
////                if (searchResult == searchTarger)
////                {
////                    counter++;
////                }
////                else
////                {
////                    if (system.isLog())
////                    {
////                        System.out
////                                .println("failed search, initiator: " + searchInit + " result: " + sgo.getTG().mNodeSet.getNode(searchResult).getNumID() + "  target: " + sgo.getTG().mNodeSet.getNode(searchTarger)
////                                                                                                                                                                                  .getNumID());
////                    }
////                    //m.printSearchPath(sgo.getTG().mNodeSet);
////                }
////
////            }
////
////            System.out.println("success ratio " + counter / system.getSearchByNumericalID());
////        }
////
//        sgo.getTG().departureUpdate(currentTime);
//        ChurnStochastics.updateTotalAverageOfOnlineNodes(sgo.getTG().mNodeSet.getNumberOfOnlineNodes());
//
//        //System.out.println("Average number of active SkipGraph.Node is started");
//        //TODO update the following function based on the new implementation
//        //System.out.println("Average number of SkipGraph.Node has been updated");
//
//                    /*
//                    Updating the availability vectors of SkipGraph.Nodes
//                     */
//        //sgo.getTG().mNodeSet.updateAvailabilityVectors();
//
//        if (currentTime == system.getLifeTime() - 1)
//        {
//            ChurnStochastics.flush();
//        }
//        if (currentTime == system.getLifeTime() - 1 && system.getCurrentTopologyIndex() == system.getTopologyNumbers())
//        {
//            sgo.getTG().printGeneratorStochastics();
//            ChurnStochastics.printChurnStochastics();
//            //repEvaluation.finalizingThisTopologyEvaluation();
//            //System.out.println("Average number of SkipGraph.Node has been updated");
//            //System.out.println("Last SkipGraph.Node came on " + Simulator.system.getLastArrivalTime());
//        }
//
//        return sgo;
//    }
//
//
//}
