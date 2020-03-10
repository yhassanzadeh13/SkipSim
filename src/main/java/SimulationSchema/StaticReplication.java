package SimulationSchema;

import DataTypes.Constants;
import Simulator.SkipSimParameters;

public class StaticReplication extends SkipSimParameters
{
    public StaticReplication()
    {
        SystemCapacity = 128;
        LandmarksNum = 7;
        NameIDLength = 7;
        //LOG = true;
        ReplicationDegree = 2;
        /*
        Replication
         */
        sDataOwnerNumber = 1;
        Topologies = 1;

        SimulationType = Constants.SimulationType.STATIC;
        ReplicationType = Constants.Replication.Type.PUBLIC;
        ReplicationAlgorithm = Constants.Replication.Algorithms.GLARAS;
        ReplicationLocalityAwarenessEvaluation = true;
        NameIDLocalityAwarenessEvaluatgion = false;
    }
}
