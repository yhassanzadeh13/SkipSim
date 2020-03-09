package SimulationSchema;

import DataTypes.Constants;
import Simulator.SkipSimParameters;

public class Blockchain extends SkipSimParameters
{
    public Blockchain()
    {
        SimulationType = Constants.SimulationType.BLOCKCHAIN;
        ChurnType = Constants.Churn.Type.COOPERATIVE;
        SystemCapacity = 10000;
        LandmarksNum = 10;
        NameIDLength = 10;

        TopologyNumbers = 100;
        LifeTime = 168;
        MaliciousFraction = 0.16f;

        ValidatorThreshold = 12;
        SignatureThreshold = 1;

        SessionLengthScaleParameter = Constants.Churn.Model.Debian.Fast.SessionLength.Scale;
        SessionLengthShapeParameter = Constants.Churn.Model.Debian.Fast.SessionLength.Shape;
        InterarrivalScaleParameter = Constants.Churn.Model.Debian.Fast.SessionInterarrival.Scale;
        InterarrivalShapeParameter = Constants.Churn.Model.Debian.Fast.SessionInterarrival.Shape;
    }
}



