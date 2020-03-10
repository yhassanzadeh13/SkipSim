package LandmarkPlacement;

import Simulator.SkipSimParameters;
import SkipGraph.Node;
import SkipGraph.SkipGraphOperations;
import Simulator.AlgorithmInvoker;

import java.awt.*;
import java.util.Random;

/**
 * Created by yhass on 3/20/2017.
 */
public class landmarkSimulation
{


    public landmarkSimulation(SkipGraphOperations sgo1, boolean generateCoordination)
    {
        Simulation(sgo1, generateCoordination);
    }

    private void Simulation(SkipGraphOperations sgo, boolean generateCoordination)
    {
        SkipSimParameters.incrementSimIndex();
        if (SkipSimParameters.getCurrentTopologyIndex() > SkipSimParameters.getTopologies())
        {
            System.exit(0);
        }
        sgo.getTG().mLandmarks.landmarkGeneration();
        int relocationCounter = 0;


        if (generateCoordination)
        {
                /*
                Generate Nodes
                 */
            for (int i = 0; i < SkipSimParameters.getLandmarksNum(); i++)
            {
                Random random = new Random();
                Point p = new Point();
                p.x = random.nextInt((int) (SkipSimParameters.getDomainSize()));
                p.y = random.nextInt((int) (SkipSimParameters.getDomainSize()));
                sgo.getTG().mLandmarks.setLandmarkCoordination(i, p);
                System.out.println("Generate landmark" + i + "x = " + p.x + "y = " + p.y);

                //System.out.println("SkipGraph.Landmarks" + Simulator.system.getIndex() + "x = " + p.x + "y = " + p.y);
            }

                /*
                Generate Landmarks
                 */
            for (int i = 0; i < SkipSimParameters.getSystemCapacity(); i++)
            {
                Node n = new Node(i);
                Point p;
                p = sgo.getTG().UniformRandomNodeGenerator();
                n.getCoordinate().x = p.x;
                n.getCoordinate().y = p.y;

                n.setIndex(i);
                sgo.getTG().mNodeSet.setNode(i, n);
//                        System.out.println("Generate a SkipGraph.Node coordination, index = " + i +
//                                " X = " + sgo.getTG().mNodeSet.getNode(i).mCoordinate.getX() + " Y = " + sgo.getTG().mNodeSet.getNode(i).mCoordinate.getY());
            }
        }


        AlgorithmInvoker AI = new AlgorithmInvoker();
        AI.landmarkPlacement(sgo);

    }


}
