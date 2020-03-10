package SimulationSchema;

import DataTypes.Constants;
import Simulator.SkipSimParameters;

public class Blockchain extends SkipSimParameters
{
    public Blockchain()
    {
        SimulationType = Constants.SimulationType.BLOCKCHAIN;
        Topologies = 100;
        SystemCapacity = 1024;
        LifeTime = 168;
        TXB_RATE = 1;
        BlockchainProtocol = Constants.Protocol.LIGHTCHAIN;

        // Setting the churn model to FAST_DEBIAN
        SessionLengthScaleParameter = Constants.Churn.Model.Debian.Fast.SessionLength.Scale;
        SessionLengthShapeParameter = Constants.Churn.Model.Debian.Fast.SessionLength.Shape;
        InterarrivalScaleParameter = Constants.Churn.Model.Debian.Fast.SessionInterarrival.Scale;
        InterarrivalShapeParameter = Constants.Churn.Model.Debian.Fast.SessionInterarrival.Shape;

        ChurnType = Constants.Churn.Type.ADVERSARIAL;
        MaliciousFraction = 0.16f;
        LOG = true;
    }
}



