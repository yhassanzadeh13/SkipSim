# SkipSim
The offline and scalable SkipGraph simulator. 

SkipSim is a simulator that can be used for the design, implementation, and evaluation of the distributed Skip Graph-based protocols.

## Skip graph
Skip-graph(s)
are stored in a `SkipGraphOperations` object and all the operations regarding to skip-graph(s)
can be performed using an instance of that class.

## Usage of SkipGraphOperations
First, create a SkipGraphOperations object
```
SkipGraphOperations sgo = new SkipGraphOperations(true);
```
We've passed `true` to the constructor because we want a blockchain type structure 
where two skip-graph overlays are generated (one for peers, one for transactions.)
We can reach the peer skip-graph by doing `sgo.getTG().getNodeSet()` and transactions
skip-graph by doing `sgo.getTransactions()`.
### Searching by Numerical ID for peers
An initiator peer required to perform a search over the skip-graph. Here, we use the node
with the index of 0 as the initiator.
```java
Nodes peers = sgo.getTG().getNodeSet();
Node initiator = (Node)peers.getNode(0);
// We want the node with the num.id most similar to 42.
int targetNumID = 42;
int searchDirection;
// Determining the direction of search.
if(targetNumID > initiator.getNumID) {
    // Target is on the right of the initiator.
    searchDirection = SkipGraphOperations.RIGHT_SEARCH_DIRECTION
} else {
    // Target is on the left of the initiator.
    searchDirection = SkipGraphOperations.LEFT_SEARCH_DIRECTION
}
int result = sgo.SearchByNumID(
                targetNumID, // search for the node with num. id of 42
                initiator, // initiate the search from this node
                new Message(),
                SkipSimParameters.getLookupTableSize()-1, // initiate the search from the uppermost level
                0,
                peers, // search over the peers skip-graph
                searchDirection // the direction of the search
);
```
The result will store the node's index with the numerical id most similar to 42.
### Searching by Numerical ID for transactions
An initiator peer is required to perform a search over the skip-graph as well. Note that this initiator should own
at least one transaction. We use the node 0 as previously.  

We also need the search direction as we did previously. However, we cannot simply use the numerical id of the initiator, as we are looking for a transaction while the initiator 
is a peer. We need to search for the initiator's transaction that is **most similar to our target**. That transaction 
will be the starting point of our search. So, we use that transaction to determine the search direction as well.
```java
Nodes transactions = sgo.getTransactions();
Node initiator = (Node)peers.getNode(0);
// we want the *transaction* with the num.id most similar to 42.
int targetNumID = 42;
/*
Determining the direction of search.
 */
// First, we need to find the most similar transaction that is stored in the initiator.
int mostSimilarInInitiator = initiator
        .mostSimilarTXB(transactions, targetNumID,
                new Message(), SkipGraphOperations.LEFT_SEARCH_DIRECTION, 0);
if(mostSimilarInInitiator == -1) {
    mostSimilarInInitiator = nodeSearchInitiator
            .mostSimilarTXB(transactions, targetNumID,
                    new Message(), SkipGraphOperations.RIGHT_SEARCH_DIRECTION, 0);

}
// Now, we have the most similar transaction on the initiator node.
// The search will be performed starting from that transaction, so we
// needed to find this value explicitly in order to determine the search 
// direction.
int mostSimilarNumId = transactions.getNode(mostSimilarInInitiator).getNumID();
int searchDirection = (searchTargetTXBNumId < mostSimilarNumId)
        ? SkipGraphOperations.LEFT_SEARCH_DIRECTION
        : SkipGraphOperations.RIGHT_SEARCH_DIRECTION;

int result = sgo.SearchByNumID(
        targetNumID,
        initiator,
        new Message(),
        SkipSimParameters.getLookupTableSize() - 1,
        0,
        transactions,
        searchDirection);
```
Similarly, the result will store the transaction's index with the numerical id most similar to 42.


### Insertion into the peer skip-graph
Insertion into the peer skip-graph is relatively easy.
```java
// Determine the new index.
int newIndex = sgo.getTG.getNodeSet().size();
// Construct the new node.
Node newNode = new Node(newIndex);
// Insert the new node into the peer skip-graph
sgo.insert(newNode, 0, sgo.getTG().getNodeSet());
```
### Insertion into the transaction skip-graph
Insertion into the transaction skip-graph is easy as well. We only need an owner peer.
```java
// Choose the node with the index of 0 as the owner of the new transaction.
Node owner = (Node) sgo.getTG().getNodeSet().getNode(0);
// Construct the new transaction.
Transaction tx = new Transaction(0, owner.getIndex());
// Insert the transaction into the skip-graph.
sgo.addTXBtoLedger(tx, 0, true);
```


## Simulation schemas
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


## Tutorials
Please refer to the tutorials for getting familiar with SkipSim quickly:
1. [Performing an efficiency experiment](https://github.com/yhassanzadeh13/SkipSim/blob/master/Tutorial1.md)
2. [Performing randomized look-up tests](https://github.com/yhassanzadeh13/SkipSim/blob/master/Tutorial2.md)
3. [Using the skip-graph](https://github.com/yhassanzadeh13/SkipSim/blob/master/Tutorial3.md)