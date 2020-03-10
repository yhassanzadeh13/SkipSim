package SimulationSchema;

import DataTypes.Constants;
import Simulator.SkipSimParameters;

public class MultiObjectiveReplication extends SkipSimParameters
{
    public MultiObjectiveReplication()
    {
        SimulationType = Constants.SimulationType.DYNAMIC;
        SystemCapacity = 4096;
        LandmarksNum = 12;
        NameIDLength = 12;
        //LOG = true;
        /*
        Load
         */
        sStorageCapacity = 3;
        sBandwidthCapacityRate = 5; //To generate an exponential distribution with the average of 0.2 out of 1
        ReplicationDegree = 1;

        /*
        Aggregation
         */
        AvailabilityAggregationDomainSize = 4;
        sFPTI = 24;
        /*
        Replication
         */
        sDataOwnerNumber = 10;
        Topologies = 1;
        LifeTime = 2160;
        SimulationType = Constants.SimulationType.DYNAMIC;
        ReplicationType = Constants.Replication.Type.PUBLIC;
        ReplicationTime = 168;
        ReplicationAlgorithm = Constants.Replication.Algorithms.PYRAMID;

        /*
        For pyramid
         */
        sSearchForUtilityAlpha = 1;
        /*
        Churn model fast debian, with each region its own churn
         */
        SessionLengthScaleParameter = Constants.Churn.Model.Debian.Fast.SessionLength.Scale;
        SessionLengthShapeParameter = Constants.Churn.Model.Debian.Fast.SessionLength.Shape;
        InterarrivalScaleParameter = Constants.Churn.Model.Debian.Fast.SessionInterarrival.Scale;
        InterarrivalShapeParameter = Constants.Churn.Model.Debian.Fast.SessionInterarrival.Shape;
        MultipleInterArrivalDistribution = true;


        /*
        Evaluations
         */
        Heterogeneous = true;
        ReplicationLoadEvaluation = true;
        ReplicationLocalityAwarenessEvaluation = true;
        ReplicationAvailabilityAwarenessEvaluation = true;


    }
}
