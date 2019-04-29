package SkipGraphTest;

import Simulator.SkipSimParameters;
import SkipGraph.Nodes;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BandwidthCapacityTest extends SkipSimParameters
{
    Nodes mNodes;
    @Before
    public void setUp()
    {
        sBandwidthCapacityRate = 20;
        mNodes = new Nodes(this.getClass());
    }

    @Test
    public void bandwidthMeanTest()
    {
        Assert.assertEquals(Nodes.getOveralAverageBandwidthCapacity(), (double) 1/sBandwidthCapacityRate,  0.01);
    }



}
