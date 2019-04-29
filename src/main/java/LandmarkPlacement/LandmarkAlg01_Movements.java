package LandmarkPlacement;

import Simulator.SkipSimParameters;
import SkipGraph.Node;
import SkipGraph.SkipGraphOperations;

import java.awt.*;

/**
 * Created by yhass on 3/20/2017.
 */
public class LandmarkAlg01_Movements extends LandmarkPlacementEvaluation
{

    public LandmarkAlg01_Movements()
    {

    }

    private Point[] moveAll(SkipGraphOperations sgo, double distanceThreshold)
        {

            double[] distanceToLandmark = new double[SkipSimParameters.getLandmarksNum()];
            double[] maxDistanceToLandmark = new double[SkipSimParameters.getLandmarksNum()];
            int[] coveringNodes = new int[SkipSimParameters.getLandmarksNum()];
            Point[] newLandmarks = new Point[SkipSimParameters.getLandmarksNum()];

            for(int i = 0; i < SkipSimParameters.getLandmarksNum() ; i++)
                {
                    maxDistanceToLandmark[i] = Double.MIN_VALUE;
                }

        /*
        Updating the closest landmark distance
         */
            sgo.getTG().mNodeSet.updateClosestLandmark(sgo.getTG().mLandmarks);

        /*
        Computing the average distance to the closest landmark
         */
            for (int i = 0; i < SkipSimParameters.getSystemCapacity(); i++)
                {
                    double nodeDistance = ((Node) sgo.getTG().mNodeSet.getNode(i)).getCoordinate().distance(sgo.getTG().mLandmarks.getLandmarkCoordination(((Node) sgo.getTG().mNodeSet.getNode(i)).getClosetLandmarkIndex(sgo.getTG().mLandmarks)).getLocation());
                    int closetLandmark = ((Node) sgo.getTG().mNodeSet.getNode(i)).getClosetLandmarkIndex(sgo.getTG().mLandmarks);
                    if(maxDistanceToLandmark[closetLandmark] < nodeDistance)
                        maxDistanceToLandmark[closetLandmark] = nodeDistance;
                    distanceToLandmark[closetLandmark] += nodeDistance;
                    coveringNodes[((Node) sgo.getTG().mNodeSet.getNode(i)).getClosetLandmarkIndex(sgo.getTG().mLandmarks)]++;
                }

            System.out.println("Average distance to a landmark");
            for(int j = 0; j < SkipSimParameters.getLandmarksNum() ; j++)
                {
                    if(coveringNodes[j] == 0)
                        System.out.print(0 + " ");
                    else
                        System.out.print((int) distanceToLandmark[j] / coveringNodes[j] + " ");
                }
            System.out.println();
            System.out.println("Max distance to a landmark");
            for(int j = 0; j < SkipSimParameters.getLandmarksNum() ; j++)
                {
                    System.out.print((int) maxDistanceToLandmark[j] + " ");
                }
            System.out.println();



            for (int j = 0; j < SkipSimParameters.getLandmarksNum(); j++)
                {
                    //System.out.print((int) distanceToLandmark[j] / coveringNodes[j] + " ");
                    if (distanceToLandmark[j] / coveringNodes[j] > distanceThreshold)
                        {
                            boolean findFlag = false;
                            for (int i = 0; i < SkipSimParameters.getSystemCapacity(); i++)
                                {
                                    if (!((Node) sgo.getTG().mNodeSet.getNode(i)).isTestedForALandmark()
                                            && ((Node) sgo.getTG().mNodeSet.getNode(i)).getClosetLandmarkIndex(sgo.getTG().mLandmarks) == j)
                                        {
                                            System.out.println("SkipGraph.Landmarks " + j + " is relocated");
                                            ((Node) sgo.getTG().mNodeSet.getNode(i)).setTestedForALandmark(true);
                                            newLandmarks[j] = ((Node) sgo.getTG().mNodeSet.getNode(i)).getCoordinate();
                                            findFlag = true;
                                            break;
                                        } else if (((Node) sgo.getTG().mNodeSet.getNode(i)).isTestedForALandmark())
                                        {
                                            //System.out.println("Cannot move a landmark to SkipGraph.Node " + i + " since it was already testet");
                                        }
                                }
                            if (!findFlag)
                                {
                                    newLandmarks[j] = sgo.getTG().mLandmarks.getLandmarkCoordination(j).getLocation();
                                }

                        } else
                        {
                            newLandmarks[j] = sgo.getTG().mLandmarks.getLandmarkCoordination(j).getLocation();
                        }
                }
            System.out.println(" ");

            return newLandmarks;

        }

    public void Algorithm(SkipGraphOperations sgo, double distanceThreshold)
        {
            int rc = 0;
            Point[] newLandmarks = moveAll(sgo, distanceThreshold);
            while(!terminationCheck(sgo, newLandmarks))
                {
                    sgo.getTG().mLandmarks.updateLandmarks(newLandmarks);
                    newLandmarks = moveAll(sgo, distanceThreshold);
                }

            landmarkEvaluation(sgo, "Alg01_MoveAll");
            relocationEvaluation(0, rc, "Alg01_MoveAll");
            System.out.println("Relocation is done after " + rc + " iterations");
        }
}
