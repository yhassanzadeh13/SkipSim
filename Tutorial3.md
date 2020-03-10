# Tutorial: Using the Skip-Graph
In this tutorial, we are going to develop a custom class, more specifically, a test class that checks the integrity of the skip-graph.
This tutorial will teach you the basics of using skip-graph operations.

This is the 3rd part of the tutorial series, and we assume that you've completed the previous two tutorials.
 
## Setting up
Because this is a test class, we need to specify the SkipSim parameters explicitly. We won't be calling the application directly, but
we will use the tools provided to us by SkipSim.
```java
SimulationType = Constants.SimulationType.BLOCKCHAIN;
SystemCapacity = 25;
```
We just need 25 nodes for starting, and simulation type is set to blockchain for now, although for our purposes, a static simulation would be enough.

Then, we want to give every node in the skip-graph a random name-id and numerical-id. For this we create two sets (one for num. ids and one for name ids),
filling them up with enough unique elements:
```java
Set<Integer> randomNumIdSet = new HashSet<>();
Set<String> randomNameIdSet = new HashSet<>();
int nameLength = (int)Math.ceil((Math.log(getSystemCapacity()) / Math.log(2)));

while(randomNumIdSet.size() < getSystemCapacity()) {
    randomNumIdSet.add(rGen.nextInt(getSystemCapacity()));
}
while(randomNameIdSet.size() < getSystemCapacity()) {
    randomNameIdSet.add(generateRandomNameId(nameLength, rGen));
}
```
For now, assume that `generateRandomNameId` takes a name-length and a random-generator, then returns a random name-id with the given length.

Now, let's create our SkipGraphOperations object, which is going to be used for performing operations on the skip-graph.
```java
mOperations = new SkipGraphOperations(true);
```
We pass `true` to the constructor, because we have decided to use blockchain simulation. This will provide churn and generate an additional skip-graph for transactions.

Now, it's finally time to generate the nodes.
```java
Nodes nodes = mOperations.getTG().getNodeSet();
for(int i = 0 ; i < getSystemCapacity(); i++) {
    nodes.getNode(i).setNumID((int)randomNumIdSet.toArray()[i]);
    nodes.getNode(i).setNameID((String)randomNameIdSet.toArray()[i]);
    // We won't need churn.
    ((Node)nodes.getNode(i)).setOnline();
    // Reinsertion.
    mOperations.insert(nodes.getNode(i), 0, nodes);
}
```
We store the peer skip-graph in the `nodes` variable, then iterate through each of the node, setting the appropriate fields.
Please note that after setting the numerical ids and name ids, we need to reinsert the node into the skip-graph.

We have successfully completed the `setUp` method.
```java
public class SearchTest extends SkipSimParameters {
    SkipGraphOperations mOperations;
    Random rGen = new Random();

    @Before
    public void setUp() {
        SimulationType = Constants.SimulationType.BLOCKCHAIN;
        SystemCapacity = 25;

        Set<Integer> randomNumIdSet = new HashSet<>();
        Set<String> randomNameIdSet = new HashSet<>();
        int nameLength = (int)Math.ceil((Math.log(getSystemCapacity()) / Math.log(2)));

        while(randomNumIdSet.size() < getSystemCapacity()) {
            randomNumIdSet.add(rGen.nextInt(getSystemCapacity()));
        }
        while(randomNameIdSet.size() < getSystemCapacity()) {
            randomNameIdSet.add(generateRandomNameId(nameLength, rGen));
        }

        mOperations = new SkipGraphOperations(true);
        Nodes nodes = mOperations.getTG().mNodeSet;

        for(int i = 0 ; i < getSystemCapacity(); i++) {
            nodes.getNode(i).setIndex(i);
            nodes.getNode(i).setNumID((int)randomNumIdSet.toArray()[i]);
            nodes.getNode(i).setNameID((String)randomNameIdSet.toArray()[i]);
            ((Node)nodes.getNode(i)).setOnline();
            mOperations.insert(nodes.getNode(i), 0, nodes);
        }
    }
    
    // Returns a random name-id.
    private String generateRandomNameId(int size, Random rGen) {
        StringBuilder s = new StringBuilder();
        for(int i = 0; i < size; i++) {
            s.append(rGen.nextBoolean() ? "1" : "0");
        }
        return s.toString();
    }
```

## Creating a search test
In our test, we want to perform searches from each node to every other node and assert that the results are as expected.
We start by iterating through each **ordered pair** of peers.
```java
Nodes nodeSet = mOperations.getTG().mNodeSet;
for(int i = 0; i < getSystemCapacity(); i++) {
    // Choose the initiator.
    Node initiator = (Node)nodeSet.getNode(i);
    for(int j = 0; j < getSystemCapacity(); j++) {
        if(i == j) continue;
        // Choose the target.
        Node target = (Node)nodeSet.getNode(j);
        // ...
    }
}
```
Next, we need to determine the search direction. While performing a search-by-numerical-id, we need to know the search direction first.
```java
 int searchDirection = (target.getNumID() > initiator.getNumID())
                        ? SkipGraphOperations.RIGHT_SEARCH_DIRECTION
                        : SkipGraphOperations.LEFT_SEARCH_DIRECTION;

```
If the target has a larger numerical id than the initiator, the search direction must be right. Otherwise, we set it to left.
Now we can perform the actual search.
```java
int numIdResult = mOperations.SearchByNumID(target.getNumID(), initiator, new Message(),
                        SkipSimParameters.getLookupTableSize() - 1, 0, nodeSet, searchDirection);
```
Please not that we initiate the search from the uppermost level of the skip-graph. We successfully acquired the index of the search-result in `numIdResult` variable.

Now, let's perform a name-id search as well. To perform this, we need the right and left neighbors of the initiator at the lowest level.
```java
int right = initiator.getLookup(0, 1);
int left = initiator.getLookup(0, 0);
```
We get the neighbors at the lowest layer because, as opposed to a numerical-id search, we initiate a name-id search from the lowest layer, as following:
```java
int nameIdResult = mOperations.SearchByNameID(target.getNameID(), initiator, nodeSet,
                        right, left, 0, new Message(), new ArrayList<>());
```
We have acquired the index of the resulting node from the name-id search in `nameIdResult` variable. Let's assert that the results that we have acquired were correct.
```java
Assert.assertEquals(target.getIndex(), numIdResult);
Assert.assertEquals(target.getIndex(), nameIdResult);
```
We have successfully completed the search test.

```java
// Performs searches from each node to every other node and asserts that the results are correct.
@Test
public void searchTest() {
    Nodes nodeSet = mOperations.getTG().mNodeSet;
    for(int i = 0; i < getSystemCapacity(); i++) {
        Node initiator = (Node)nodeSet.getNode(i);
        for(int j = 0; j < getSystemCapacity(); j++) {
            if(i == j) continue;
            Node target = (Node)nodeSet.getNode(j);
            int searchDirection = (target.getNumID() > initiator.getNumID())
                    ? SkipGraphOperations.RIGHT_SEARCH_DIRECTION
                    : SkipGraphOperations.LEFT_SEARCH_DIRECTION;
            int numIdResult = mOperations.SearchByNumID(target.getNumID(), initiator, new Message(),
                    SkipSimParameters.getLookupTableSize() - 1, 0, nodeSet, searchDirection);
            int right = initiator.getLookup(0, 1);
            int left = initiator.getLookup(0, 0);
            int nameIdResult = mOperations.SearchByNameID(target.getNameID(), initiator, nodeSet,
                    right, left, 0, new Message(), new ArrayList<>());
            Assert.assertEquals(target.getIndex(), numIdResult);
            Assert.assertEquals(target.getIndex(), nameIdResult);
        }
    }
}
```