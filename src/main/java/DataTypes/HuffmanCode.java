package DataTypes;

import SkipGraph.SkipGraphOperations;

import java.util.*;

//abstract class DataTypes.HuffmanTree implements Comparable<DataTypes.HuffmanTree>
//{
//    public final int frequency; // the frequency of this tree
//    public DataTypes.HuffmanTree(int freq) { frequency = freq; }
//
//    // compares on the frequency
//    public int compareTo(DataTypes.HuffmanTree tree) {
//        return frequency - tree.frequency;
//    }
//}
//
//class DataTypes.HuffmanLeaf extends DataTypes.HuffmanTree {
//    public final int value; // the character this leaf represents
//
//    public DataTypes.HuffmanLeaf(int freq, int val)
//    {
//        super(freq);
//        value = val;
//    }
//}
//
//class DataTypes.HuffmanNode extends DataTypes.HuffmanTree {
//    public final DataTypes.HuffmanTree left, right; // subtrees
//
//    public DataTypes.HuffmanNode(DataTypes.HuffmanTree l, DataTypes.HuffmanTree r) {
//        super(l.frequency + r.frequency);
//        left = l;
//        right = r;
//    }
//}

public class HuffmanCode
{
    // input is an array of frequencies, indexed by character code
    public HuffmanTree buildTree(int[] charFreqs)
    {
        PriorityQueue<HuffmanTree> trees = new PriorityQueue<HuffmanTree>();
        // initially, we have a forest of leaves
        // one for each non-empty character
        for (int i = 0; i < charFreqs.length; i++)
            if (charFreqs[i] > 0)
                trees.offer(new HuffmanLeaf(charFreqs[i], i));

        assert trees.size() > 0;
        // loop until there is only one tree left
        while (trees.size() > 1) {
            // two trees with least frequency
            HuffmanTree a = trees.poll();
            HuffmanTree b = trees.poll();

            // put into new SkipGraph.Node and re-insert into queue
            trees.offer(new HuffmanNode(a, b));
        }
        return trees.poll();
}

    public void printCodes(HuffmanTree tree, StringBuffer prefix, SkipGraphOperations sg) {
        assert tree != null;
        if (tree instanceof HuffmanLeaf)
        {
            HuffmanLeaf leaf = (HuffmanLeaf)tree;

            // print out character, frequency, and code for this leaf (which is just the prefix)
           // System.out.println(leaf.value + "\t" + leaf.frequency + "\t" + prefix + " x = " + SkipGraph.Landmarks.Set[leaf.value].x + "  y = " + SkipGraph.Landmarks.Set[leaf.value].y);
            sg.getTG().mLandmarks.setDynamicPrefix(leaf.value,prefix.toString());

        } else if (tree instanceof HuffmanNode) {
            HuffmanNode node = (HuffmanNode)tree;

            // traverse left
            prefix.append('0');
            printCodes(node.left, prefix,sg);
            prefix.deleteCharAt(prefix.length()-1);

            // traverse right
            prefix.append('1');
            printCodes(node.right, prefix,sg);
            prefix.deleteCharAt(prefix.length()-1);
        }
    }

//    public static void main(String[] args) {
//        String test = "this is an example for huffman encoding";
//
//        // we will assume that all our characters will have
//        // code less than 256, for simplicity
//        int[] charFreqs = new int[256];
//        // read each character and record the frequencies
//       // for (char c : test.toCharArray())
//        for (int i = 0 ; i < 5 ; i++)
//            charFreqs[i] = i;
//
//        // build tree
//        DataTypes.HuffmanTree tree = buildTree(charFreqs);
//
//        // print out results
//        System.out.println("SYMBOL\tWEIGHT\tHUFFMAN CODE");
//        printCodes(tree, new StringBuffer());
//    }

    public void buildThePrefix(int[] freq,SkipGraphOperations sg)
    {
    	HuffmanTree tree = buildTree(freq);
        //System.out.println("LandMark\tWEIGHT\tHUFFMAN CODE");
        printCodes(tree, new StringBuffer(),sg);
    }
}