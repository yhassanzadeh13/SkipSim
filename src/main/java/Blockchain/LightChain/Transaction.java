package Blockchain.LightChain;

import DataTypes.Message;
import Simulator.SkipSimParameters;
import SkipGraph.Node;
import SkipGraph.Nodes;
import SkipGraph.SkipGraphNode;
import SkipGraph.SkipGraphOperations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Objects.hash;


/**
 * Represents a transaction in the block-chain.
 */
public class Transaction extends SkipGraphNode
{
    /*
    We consider the hashed value of the transactions and blocks in 32 bits java Object hashed values.
     */
    public static int NAME_ID_SIZE = 32;
    public static int LOOKUP_TABLE_SIZE = 32;

    // The list of validators this transaction has.
    private List<Node> validators = new ArrayList<>();
    // The list of signed validators this node has acquired.
    private List<Node> signedValidators = new ArrayList<>(SkipSimParameters.getSignatureThreshold());

    /**
     * Transaction wire or script i.e., the entire unique content of the transaction
     */
    protected int Wire;

//    /**
//     * Index of the transaction in the Transactions set, which acts as a centralized database for the sake
//     * of saving space.
//     */
//    protected int Index;

    /**
     * Index of the owner of the transaction, it is analogous to the IP address of the owner
     */
    protected int mOwnerIndex;

    public void setOwnerIndex(int ownerIndex)
    {
        mOwnerIndex = ownerIndex;
    }

    /**
     * The pointer to the previous Block on the blockchain, corresponds to the hashed value of the previous block, and not
     * the previous block's index. BE CAREFUL!!
     */
    protected int Previous;

//    /**
//     * Name id of the transaction, binary representation of the previous Block
//     */
//    protected String NameID;

//    /**
//     * Numerical id of the transaction, i.e., hashed value of the transaction
//     */
//    protected int NumID;

    /**
     * Indices set of the signers
     */
    protected HashSet<Integer> SignersSet;

    /**
     * Indices of the replicas of this transaction
     */
    protected HashSet<Integer> ReplicaSet;

    public Transaction(int previous, int owner)
    {
        super();
        this.mOwnerIndex = owner;
        this.Previous = previous;
        this.nameID = generateNameID(previous);

        this.Wire = sRandom.nextInt(Integer.MAX_VALUE);
        SignersSet = new HashSet<>();
        ReplicaSet = new HashSet<>();
        ReplicaSet.add(owner);

        lookup = new int[NAME_ID_SIZE][2];
        for(int i = 0; i < NAME_ID_SIZE; i++)
        {
            for(int j = 0 ; j < 2; j++)
            {
                lookup[i][j] = -1;
            }
        }
    }

    /**
     * Tries to find ValidatorThreshold many validators for this transaction.
     * @param sgo the operation object that the searches must be performed on.
     */
    public void acquireValidators(SkipGraphOperations sgo) {
        Set<Node> uniqueValidators = new HashSet<>();
        for(int i = 0, j = 0; i < SkipSimParameters.getValidatorThreshold(); i++) {
            if(i >= sgo.getTG().getNodeSet().getNumberOfOnlineNodes()) {
                System.err.println("Transaction.java: Validator threshold exceeds the online node amount.");
                break;
            }
            // Calculating hash(tx||i)
            String input = "" + getIndex() + (i + j);
            byte[] targetNumIdBytes = HashTools.hash(input);
            // Compressing the hash code into the [0, SystemCapacity-1].
            int targetNumId = HashTools.compressToInt(targetNumIdBytes);
            // Performing search over nodes using the result of (|hash(tx||i)| % P) % N
            Node owner = (Node) sgo.getTG().mNodeSet.getNode(getOwnerIndex());
            int searchDirection = (targetNumId < owner.getNumID())
                    ? SkipGraphOperations.LEFT_SEARCH_DIRECTION
                    : SkipGraphOperations.RIGHT_SEARCH_DIRECTION;
            int searchResultIndex = sgo.SearchByNumID(targetNumId,
                    owner,
                    new Message(),
                    SkipSimParameters.getLookupTableSize() - 1,
                    0,
                    sgo.getTG().getNodeSet(),
                    searchDirection);
            Node searchResult = (Node) sgo.getTG().getNodeSet().getNode(searchResultIndex);

            if(!uniqueValidators.contains(searchResult)) {
                uniqueValidators.add(searchResult);
                validators.add(searchResult);
            } else {
                // At the next iteration, i should stay the same, so we increase the next input by
                // increasing j by one.
                i--;
                j++;
            }
        }
        // Validators are acquired. Now we need to get T many signed validators out of them.
        Node owner = (Node)sgo.getTG().getNodeSet().getNode(getOwnerIndex());
        for(Node validator : validators) {
            boolean maliciousSuccess = owner.isMalicious() && validator.isMalicious();
            boolean honestSuccess = !owner.isMalicious() && !validator.isMalicious();
            if(maliciousSuccess || honestSuccess) {
                signedValidators.add(validator);
            }
            if(signedValidators.size() == SkipSimParameters.getSignatureThreshold())
                break;
        }

    }

    public List<Node> getValidators() {
        return validators;
    }

    public List<Node> getSignedValidators() {
        return signedValidators;
    }

    /**
     * Simulates the signing of the transaction, by adding index of the signer to the SignerSet
     * @param signerIndex index of the signer Node
     */
    public void Sign(int signerIndex)
    {
        SignersSet.add(signerIndex);
    }

    /**
     *
     * @param signerIndex index of the signer Node
     * @return True if the Node that is denoted by signerIndex has already signed the Block, and false otherwise
     */
    public boolean VerifySignature(int signerIndex)
    {
        return SignersSet.contains(signerIndex);
    }


    /**
     *
     * @param previous hashed value of the previous block
     * @return binary representation of the hashed value of the previous block within BlockNameIDSize bits
     */
    private String generateNameID(int previous)
    {
        String nameID = Integer.toBinaryString(previous);

        while (nameID.length() < NAME_ID_SIZE)
            nameID = "0" + nameID;

        return nameID;
    }


    public int getOwnerIndex()
    {
        return mOwnerIndex;
    }

    public int getPrevious()
    {
        return Previous;
    }


    /**
     * Adds address of the replica Node to the set of replicas for this transaction
     * @param replicaIndex the new replica Node's index
     */
    public void addToReplicaSet(int replicaIndex)
    {
        ReplicaSet.add(replicaIndex);
    }

//    @Override
//    public boolean equals(Object other) {
//        return (other instanceof Transaction) && hashCode() == other.hashCode();
//    }

    /**
     *
     * @param i a counter on the validator number
     * @return the computed numerical ID of the ith validator
     */
    public int getValidatorNumID(int i)
    {
        //TODO should be tested against the result
        return hash(Previous, Wire, i);
    }

    /**
     *
     * @param ns an instance of the Node set
     * @return number of online replica holders of the transaction
     */
    public int numberOfOnlineReplicaHolders(Nodes ns)
    {
        int counter = 0;
        for(int index: ReplicaSet)
        {
            if(((Node) ns.getNode(index)).isOnline())
            {
                counter++;
            }
        }
        return counter;
    }


    //    public boolean isOnline(Nodes mNodeSet)
//    {
//        if (ownerIndex == -1)
//        {
//            return false;
//        }
//        return mNodeSet.getNode(ownerIndex).isOnline();
//    }

}