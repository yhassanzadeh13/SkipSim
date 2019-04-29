package SkipGraphTest;

import SkipGraph.Nodes;
import org.junit.Assert;
import org.junit.Test;

public class NodesTest
{
    /**
     * Tests the extraction of the body from a name ID, where name ID is prefix + body, and it
     * returns body
     */
    @Test
    public void extractBodyTest()
    {
        String landmarkPrefix = "0";
        String nameID = "000";
        String body = Nodes.extractNameIDBody(landmarkPrefix,nameID);
        Assert.assertTrue(body.equals("00"));


        landmarkPrefix = "10";
        nameID = "1010";
        body = Nodes.extractNameIDBody(landmarkPrefix,nameID);
        Assert.assertTrue(body.equals("10"));

        landmarkPrefix = "101";
        nameID = "1010000";
        body = Nodes.extractNameIDBody(landmarkPrefix,nameID);
        Assert.assertTrue(body.equals("0000"));

        landmarkPrefix = "0100";
        nameID = "010010000";
        body = Nodes.extractNameIDBody(landmarkPrefix,nameID);
        Assert.assertTrue(body.equals("10000"));

    }
}
