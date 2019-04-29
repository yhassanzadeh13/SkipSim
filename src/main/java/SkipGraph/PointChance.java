package SkipGraph;

import java.awt.*;

/**
 * PointChance class is used to represent a single point in the topology and is only employed by the
 * TopologyGenerator class
 */
public class PointChance
{

    private Point p;
    private double upperChance;
    private double lowerChance;
    private double Chance;
    private double prob;

    public PointChance()
    {
        p = new Point();

    }
    public Point getP()
    {
        return p;
    }

    public void setP(Point i)
    {
        p = i;
    }

    public double getProb()
    {
        return prob;
    }

    public void setProb(double i)
    {
        prob = i;
    }

    public double getChance()
    {
        return Chance;
    }

    public void setChance(double i)
    {
        Chance = i;
    }

    public double getLowerChance()
    {
        return lowerChance;
    }

    public void setLowerChance(double i)
    {
        lowerChance = i;
    }

    public double getUpperChance()
    {
        return upperChance;
    }

    public void setUpperChance(double i)
    {
        upperChance = i;
    }


}
