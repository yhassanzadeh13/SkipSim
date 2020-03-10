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
        setChurnModel(Constants.Churn.Model.Debian.Fast.Name);

        ChurnType = Constants.Churn.Type.ADVERSARIAL;
        MaliciousFraction = 0.16f;
        LOG = true;
    }
}



