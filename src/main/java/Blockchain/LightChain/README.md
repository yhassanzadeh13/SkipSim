## LightChain
This package contains the tools/classes that are used related [LightChain](#) implementation in SkipSim.
### LookupEvaluation
This class is used to perform randomized lookup tests on the skip-graph. To perform randomized lookup tests at the beginning of
each time-slot, please refer to the [SkipSimParameters in the Simulator package](https://github.com/yhassanzadeh13/SkipSimWS/tree/Utkan-LightChain/src/main/java/Simulator).

### HashTools
Provides hashing tools to that were meant to be used for numerical id generation, validator acquisition at the consensus layer and randomized bootstrapping at the view layer.
However, these procedures can be used from other parts of the SkipSim as well. 
The algorithm that is used in hashing is SHA3-256.

##### `byte[] hash(String input)`
This method performs the hashing. It takes a string of variable length and returns the digest as a byte array.
##### `BigInteger compress(byte[] digest, BigInteger N)`
Compresses the given hash code uniformly into the range [0, N-1].
##### `int compressToInt(byte[] digest)`
Compresses the given hash code into an integer.
#### Usages of HashTools
##### Consensus Layer (Proof-of-Validation)
Please see "acquireValidators" method below.

##### View Layer (Randomized Bootstrapping)

When a new node goes online, it aims to find SignatureThreshold many honest view introducers over ValidatorThreshold many trials.
To do this, the node performs a search numerical id search over the skip-graph with the `Hash(numId||i) [where i is from 1 to ValidatorThreshold]` as the search targets.

### Transaction
This class represents a transaction in the blockchain. 
##### `void acquireValidators(SkipGraphOperations sgo)`
According to Proof-of-Validation protocol, when a transaction is generated, the owner of the transaction aims to find SignatureThreshold many 
honest nodes over ValidatorThreshold many trials. 
the node performs a search numerical id search over the skip-graph with the 
`Hash(numId||i) [where i is from 1 to ValidatorThreshold]` as the search target.
However, if a malicious node achieves SignatureThreshold many malicious validators, it is a
case of *malicious success*.
This method is called whenever a new transaction is generated if LightChain is activated.

### Experiments
The classes in this package are used to perform experiments to measure important parameters-of-interest, such as the average
number of honest nodes that are acquired in Proof-of-Validation. 
For example, the results of these experiment can be used to choose an admissible ValidatorThreshold, SignatureThreshold pair.
For more information please see the [LightChain paper.](#)
* **AvailabilityExperiment** is used to measure average availability of the replicas of a transaction. Please note that the honest validators
are taken as replicas.
* **EfficiencyExperiment** is used to measure the average efficiency of consensus protocol, i.e. the average number of honest validators that are acquired per transaction.
* **MaliciousSuccessExperiment** is used to measure the chance of a malicious success as defined above.
* **OnlineProbabilityExperiment** is used to measure the chance of a node being online.
* **BtsEfficiencyExperiment** is used to measure the average number of honest view introducers that are acquired per randomized bootstrapping instance.
* **BtsMaliciousSuccessExperiment** is used to measure the chance of a malicious success in the case of randomized bootstrapping.

To enable/disable the experiments, please refer to [SkipSimParameters under the Simulator package.](#).