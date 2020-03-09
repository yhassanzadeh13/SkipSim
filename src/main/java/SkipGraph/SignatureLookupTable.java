package SkipGraph;

import Blockchain.LightChain.Block;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SignatureLookupTable {
    // TODO: Keys are numID's of Nodes, values are set of numID's of blocks that Node stores.
    //private static Map<Integer, Set<Integer>> signatureLookupTable;
    protected static Map<Integer, Set<Block>> signatureLookupTable;
    public SignatureLookupTable(){

        signatureLookupTable = new HashMap<>();
    }

    public static void sign(Block b, Node n){
        //int blockNumID = b.getNumID();
        int nodeNumID = n.getNumID();

        if(!signatureLookupTable.containsKey(nodeNumID)){
            signatureLookupTable.put(nodeNumID, new HashSet<>());
        }

        signatureLookupTable.get(nodeNumID).add(b);
    }

    public static boolean verify(Block b, Node n){
        int blockNumID = b.getNumID();
        int nodeNumID = n.getNumID();

        if(!signatureLookupTable.containsKey(nodeNumID)) return false;

        return signatureLookupTable.get(nodeNumID).contains(b);
    }


}
