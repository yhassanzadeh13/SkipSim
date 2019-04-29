package LandmarkPlacement;

import Simulator.SkipSimParameters;
import SkipGraph.Node;
import SkipGraph.SkipGraphOperations;

import java.util.Arrays;

/**
 * Created by yhass on 3/20/2017.
 */
public class LandmarkAlg02_OneByOne extends LandmarkPlacementEvaluation
    {

        public LandmarkAlg02_OneByOne()
            {

            }

        private void moveOneByOne(SkipGraphOperations sgo, double distanceThreshold)
            {

               // double[] distanceToLandmark = new double[Simulator.system.getLandmarksNum()];
               // double[] maxDistanceToLandmark = new double[Simulator.system.getLandmarksNum()];
               // int[] coveringNodes = new int[Simulator.system.getLandmarksNum()];

                System.out.println("Initial statistics");
                updateAndComputeClosest(sgo);
                System.out.println("*************************");


                boolean flag = true;
                int[] correspondingNodeIndex = new int[SkipSimParameters.getLandmarksNum()];
                Arrays.fill(correspondingNodeIndex, -1);

                int iterationCounter = 1;
                while(flag)
                    {
                        System.out.println("Iteration #" + iterationCounter++);
                        if(iterationCounter > 10)
                            flag = false;
                        for (int j = 0; j < SkipSimParameters.getLandmarksNum(); j++)
                            {
                                double oldDistance = distanceToLandmark[j] / coveringNodes[j];
                                int landmarkCandidateIndex = 0;
                                for (int i = 0; i < SkipSimParameters.getSystemCapacity(); i++)
                                    {
                                        System.out.println(" ");
                                        if (!((Node) sgo.getTG().mNodeSet.getNode(i)).isTestedForALandmark())
                                            {
                                                System.out.println("SkipGraph.Landmarks " + j + " is relocated");
                                                landmarkCandidateIndex = i;
                                                sgo.getTG().mLandmarks.getLandmarkCoordination(j).setLocation(((Node) sgo.getTG().mNodeSet.getNode(i)).getCoordinate());
                                                updateAndComputeClosest(sgo);
                                                if(distanceToLandmark[j] / coveringNodes[j] < oldDistance || distanceToLandmark[j] / coveringNodes[j] < distanceThreshold)
                                                    {
                                                        if(correspondingNodeIndex[j] != -1)
                                                            {
                                                                ((Node) sgo.getTG().mNodeSet.getNode(correspondingNodeIndex[j])).setTestedForALandmark(false);
                                                            }
                                                        correspondingNodeIndex[j] = landmarkCandidateIndex;
                                                        ((Node) sgo.getTG().mNodeSet.getNode(landmarkCandidateIndex)).setTestedForALandmark(true);
                                                        System.out.println("SkipGraph.Landmarks " + j + " is fixed now ");
                                                        //if(Math.abs((distanceToLandmark[j] / coveringNodes[j]) - oldDistance) / Math.max(distanceToLandmark[j] / coveringNodes[j], oldDistance) > 0.20)
                                                          //  flag = true;
                                                        break;
                                                    }
                                            }

                                        System.out.println(" ");
                                    }
                            }
                    }



            }

        public void Algorithm(SkipGraphOperations sgo, double distanceThreshold)
            {
                moveOneByOne(sgo, distanceThreshold);
                landmarkEvaluation(sgo, "Alg01_MoveAll");
            }
    }
