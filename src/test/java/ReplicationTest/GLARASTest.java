package ReplicationTest;

import Replication.LARAS;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import net.sf.javailp.*;


import java.util.ArrayList;
import java.util.Random;

public class GLARASTest extends LARAS
{
    LARAS mLARAS;

    @Before
    public void setUp()
    {
        mLARAS = new LARAS();
    }

    @Test
    public void twoNodeSetup()
    {
        int[][] L = {{0,1}, {2, 0}};
        Result result = mLARAS.ILP(L, 2, 1, 0, new ArrayList<>());
        Assert.assertTrue(result.toString().contains("Y0=1"));
    }

    @Test
    public void manyNodeOneReplica()
    {
        Random random = new Random();
        int size = random.nextInt(128);
        while (size == 0)
            size = random.nextInt(128);
        int[][] L = new int[size][size];
        int minSum = Integer.MAX_VALUE;
        int minIndex = 0;
        int sum = 0;
        for(int i = 0 ; i < size; i++)
        {
            for (int j = 0; j < size; j++)
            {
                if(i == 0)
                {
                    L[i][j] = 0;
                }
                else
                {
                    L[i][j] = random.nextInt();
                    if (L[i][j] < 0)
                        L[i][j] *= -1;
                    sum += L[i][j];
                }
            }
            if(sum < minSum)
            {
                minSum = sum;
                minIndex = i;
            }
        }

        Result result = mLARAS.ILP(L, size, 1, 0, new ArrayList<>());
        Assert.assertTrue(result.toString().contains("Y" + 0 +"=1"));
        for(int i = 1; i < size; i++)
        {
            Assert.assertTrue(result.toString().contains("Y" + i +"=0"));
        }
    }

    @Test
    public void manyNodesTwoReplica()
    {
        int inf = Integer.MAX_VALUE;
        int[][] L = {
                {0,0,0,0,inf,inf,inf,inf},
                {10,0,2,4,inf,inf,inf,inf},
                {12,5,0,22,inf,inf,inf,inf},
                {17,18,52,0,inf,inf,inf,inf},
                {inf,inf,inf,inf,0,0,0,0},
                {inf,inf,inf,inf,100,0,4,5},
                {inf,inf,inf,inf,1,200,0,6},
                {inf,inf,inf,inf,3,2,300,0}};
        Result result = mLARAS.ILP(L, 8, 2, 0, new ArrayList<>());
        Assert.assertTrue(result.toString().contains("Y0=1"));
        Assert.assertTrue(result.toString().contains("Y1=0"));
        Assert.assertTrue(result.toString().contains("Y2=0"));
        Assert.assertTrue(result.toString().contains("Y3=0"));
        Assert.assertTrue(result.toString().contains("Y4=1"));
        Assert.assertTrue(result.toString().contains("Y5=0"));
        Assert.assertTrue(result.toString().contains("Y6=0"));
        Assert.assertTrue(result.toString().contains("Y7=0"));
    }

}
