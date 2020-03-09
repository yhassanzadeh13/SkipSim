package ChurnStabilization;

public class Tornado extends Interlace
{

//    public int rescueJump(int startIndex, int targetNumID, Message m, TopologyGenerator tg)
//    {
//        int returnIndex = -1;
//        Node startNode = tg.mNodeSet.getNode(startIndex);
//
//        ArrayList<Node> rescureCandidates = new ArrayList<>();
//        for (int i = 0; i < system.getLookupTableSize(); i++)
//        {
//            for (int j = 0; j < 2; j++)
//            {
//                LinkedList<AbstractMap.SimpleEntry<Integer, Integer>> bucket = startNode.getBucket(i, j);
//                for (AbstractMap.SimpleEntry<Integer, Integer> e : bucket)
//                {
//                    int nodeIndex = e.getKey();
//                    Node n = tg.mNodeSet.getNode(nodeIndex);
//                    if (!m.contains(nodeIndex) && n.isOnline())
//                        rescureCandidates.add(n);
//                }
//            }
//        }
//        returnIndex = contactCandidates(rescureCandidates, targetNumID, startIndex, 0, 0, tg.mNodeSet, m);
//        return returnIndex;
//    }

    public Tornado()
    {
    }
}
