package ReplicationTest;

import Replication.Awake;
import net.sf.javailp.Result;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Hashtable;

public class AwakeTest extends Awake
{

    @Test
    public void marginalTests()
    {
        /*
        On a null availability table Awake should also return null
         */
        Result result = this.ILP( 1, null);
        Assert.assertNull(result);

        /*
        On an empty availability table Awake should also return null
         */
        result = this.ILP( 1, new Hashtable<Integer, double[]>());
        Assert.assertNull(result);

        /*
        With replication degree of zero on an empty availability table Awake should also return null
         */
        result = this.ILP( 0, new Hashtable<>());
        Assert.assertNull(result);

        /*
        For replication degree of zero the result should be empty
         */
        double[] zeroNodeAvailability = {0, 0, 0};
        Hashtable<Integer, double[]> availabilityTable = new Hashtable<>();
        availabilityTable.put(0, zeroNodeAvailability);
        result = this.ILP( 0, availabilityTable);
        HashSet<Integer> replicaSet = this.replicaSetGenerator(result, 1, 0, this.getClass());
        Assert.assertTrue(replicaSet.isEmpty());


    }

    @Test
    public void oneRepDegree()
    {
        /*
        There is only one always unavailable node, but it should be selected as replica, because we have only that node
        in the system and the replication degree is zero
         */
        double[] zeroNodeAvailability = {0, 0, 0};
        Hashtable<Integer, double[]> availabilityTable = new Hashtable<>();
        availabilityTable.put(0, zeroNodeAvailability);
        Result result = this.ILP( 1, availabilityTable);
        HashSet<Integer> replicaSet = this.replicaSetGenerator(result, 1, 0, this.getClass());
        Assert.assertTrue(replicaSet.size() == 1);
        Assert.assertTrue(replicaSet.contains(0));


        /*
        Another node is added with slightly better availability vector, hence that new node should be selected
         */
        double[] firstNodeAvailability = {0.1, 0, 0};
        availabilityTable.put(1, firstNodeAvailability);
        result = this.ILP( 1, availabilityTable);
        replicaSet = this.replicaSetGenerator(result, 2, 0, this.getClass());
        Assert.assertTrue(replicaSet.size() == 1);
        Assert.assertTrue(replicaSet.contains(1));

        /*
        Another node is added with slightly better availability vector, hence that new node should be selected, since it
        covers all the time slots
         */
        double[] secondNodeAvailability = {0.03, 0.04, 0.04};
        availabilityTable.put(2, secondNodeAvailability);
        result = this.ILP( 1, availabilityTable);
        replicaSet = this.replicaSetGenerator(result, 3, 0, this.getClass());
        Assert.assertTrue(replicaSet.size() == 1);
        Assert.assertTrue(replicaSet.contains(2));
    }

    @Test
    public void twoRepDegree()
    {

        double[] zeroNodeAvailability = {1, 0, 1};
        Hashtable<Integer, double[]> availabilityTable = new Hashtable<>();
        availabilityTable.put(0, zeroNodeAvailability);

        double[] firstNodeAvailability = {0.5, 1, 0};
        availabilityTable.put(1, firstNodeAvailability);

        double[] secondNodeAvailability = {0, 1, 1};
        availabilityTable.put(2, secondNodeAvailability);

        Result result = this.ILP( 2, availabilityTable);
        HashSet<Integer> replicaSet = this.replicaSetGenerator(result, 3, 0, this.getClass());
        Assert.assertTrue(replicaSet.size() == 2);
        Assert.assertTrue(replicaSet.contains(0));
        Assert.assertTrue(replicaSet.contains(2));
    }
}
