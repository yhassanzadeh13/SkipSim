## Tutorial: Randomized Look-up Tests
In this tutorial, we are going to perform randomized lookup tests on our simulation. This tutorial assumes that you have completed the [first tutorial](#).

### Configuring
Open your Simulation-Schema file and add the following line:
```java
RandomizedLookupTests = true;
```
Now, your Simulation-Schema should look like this:
```java
package SimulationSchema;

import DataTypes.Constants;
import Simulator.SkipSimParameters;

public class Blockchain extends SkipSimParameters {
    public Blockchain() {
        SimulationType = Constants.SimulationType.BLOCKCHAIN;
        ChurnType = Constants.Churn.Type.COOPERATIVE;
        SystemCapacity = 1024;
        LandmarksNum = 10;
        NameIDLength = 10;
        TopologyNumbers = 1;
        LifeTime = 168;
        // ** Addition **
        RandomizedLookupTests = true;

        SessionLengthScaleParameter = Constants.Churn.Model.Debian.Fast.SessionLength.Scale;
        SessionLengthShapeParameter = Constants.Churn.Model.Debian.Fast.SessionLength.Shape;
        InterarrivalScaleParameter = Constants.Churn.Model.Debian.Fast.SessionInterarrival.Scale;
        InterarrivalShapeParameter = Constants.Churn.Model.Debian.Fast.SessionInterarrival.Shape;
    }
}
```

### Running
Running is the same as the previous tutorial.
Click on `Open` from the top menu, and choose `100_1024_DEBIAN_1W` from the drop-down menu. 
This simulation uses FAST_DEBIAN as a churn model, and it has 1024 nodes. The simulation is done over one week (168 hours).

Click on `OK` and watch as the simulation runs. The experiment results will be outputted on the standard output.
