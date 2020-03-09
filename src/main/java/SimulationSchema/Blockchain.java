package SimulationSchema;

import DataTypes.Constants;
import Simulator.SkipSimParameters;

public class Blockchain extends SkipSimParameters
{
    public Blockchain()
    {
        LOG = true;
        SimulationType = Constants.SimulationType.BLOCKCHAIN;
        ChurnType = Constants.Churn.Type.ADVERSARIAL;
        SystemCapacity = 1024;

        TopologyNumbers = 100;
        LifeTime = 168;
        MaliciousFraction = 0.16f;

        ValidatorThreshold = 12;
        SignatureThreshold = 1;

        // Setting the churn model to FAST_DEBIAN
        SessionLengthScaleParameter = Constants.Churn.Model.Debian.Fast.SessionLength.Scale;
        SessionLengthShapeParameter = Constants.Churn.Model.Debian.Fast.SessionLength.Shape;
        InterarrivalScaleParameter = Constants.Churn.Model.Debian.Fast.SessionInterarrival.Scale;
        InterarrivalShapeParameter = Constants.Churn.Model.Debian.Fast.SessionInterarrival.Shape;
    }
}



