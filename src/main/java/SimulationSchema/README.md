## SimulationSchema
This package contains the simulation-schemas. Simulation-schemas are classes that contain a specific set of configurations
for the simulation to be used by the SkipSim.

### Usage
In order to create a new simulation-schema, first we need to create a new class 
that extends from SkipSimParameters. In its constructor, we set the parameters. 
Then we need to call the constructor of this class from the constructor of the SchemaManager. 
SkipSim calls the SchemaManager's constructor once to initialize the simulation with correct configuration.

Please see Blockchain, StaticReplication and MultiObjectiveReplication classes for
reference simulation-schemas. If the user wishes, for example, to use the already provided
StaticReplication schema, they should call the constructor of that class from the constructor
of the SchemaManager, as following:

```java
package SimulationSchema;

public class SchemaManager {
    public SchemaManager() {
        // calling the constructor of the schema that we want to use
        new StaticReplication(); 
    }
}
```
