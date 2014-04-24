package Models;

import java.util.ArrayList;

public class CostModel {
	
	public int total_impressions = 1000;
	private ArrayList<Double[]> dataPoints = new ArrayList<Double[]>();
	private double hardCodedCost = 0.00000000001;
	
	private String _type;
	
	public CostModel(String type)
	{
		_type = type;
	}
	
	public String getType()
	{
		return _type;
	}
	
	// cost for one more impressions
	public double get_incremental_cost(double numImpressions, int increment)
	{
		return hardCodedCost * increment;
	}
	
	// add known impression count : cost
	public void addDataPoint(Double numImpressions, Double cost)
	{
		// x, y where x = num imps and y = cost
		Double[] point = new Double[2];
		point[0] = numImpressions;
		point[1] = cost;
		dataPoints.add(point);
	}
	
	public double get_incremental_cost_twoPts(double numImpressions, int increment)
	{
		if (dataPoints.size() < 2)
		{
			return hardCodedCost;
		}
		Double distPointLeft = Double.POSITIVE_INFINITY;
		Double[] pointLeft = new Double[2];
		Double distPointRight = Double.POSITIVE_INFINITY;
		Double[] pointRight = new Double[2];
		
		// find closest point on left and right of numImpressions
		for (int i = 0; i < dataPoints.size(); i++)
		{
			Double[] currPoint = dataPoints.get(i);
			double currDist = Math.abs(currPoint[0] - numImpressions);
			if (currDist < distPointLeft && currPoint[0] < numImpressions)
			{
				distPointLeft = currDist;
				pointLeft[0] = currPoint[0];
				pointLeft[1] = currPoint[1];
			}
			else if (currDist < distPointRight && currPoint[0] > numImpressions)
			{
				distPointRight = currDist;
				pointRight[0] = currPoint[0];
				pointRight[1] = currPoint[1];
			}
		}
		
		if (distPointLeft == Double.POSITIVE_INFINITY || distPointRight == Double.POSITIVE_INFINITY)
		{
			return hardCodedCost;
		}
				
		// find line between those points		
		double slope = (pointLeft[1] - pointRight[1]) / (pointLeft[0] - pointRight[0]);
		double b = pointLeft[1] - (slope * pointLeft[0]);
		double x = numImpressions;
		
		// calculate cost based on where numImpressions (aka x) hits that line
		double y = (slope * x) + b;
		
		// return cost for num impressions
		System.out.println("5");
		return y;
	}

	public double get_incremental_cost_fast(double numImpressions, int increment)
	{
		if (dataPoints.size() < 2)
		{
			return hardCodedCost;
		}
		Double distPointLeft = Double.POSITIVE_INFINITY;
		Double[] pointLeft = new Double[2];
		
		// find closest point on left and right of numImpressions
		for (int i = 0; i < dataPoints.size(); i++)
		{
			Double[] currPoint = dataPoints.get(i);
			double currDist = Math.abs(currPoint[0] - numImpressions);
			if (currDist < distPointLeft && currPoint[0] < numImpressions)
			{
				distPointLeft = currDist;
				pointLeft[0] = currPoint[0];
				pointLeft[1] = currPoint[1];
			}
		}
		
		if (distPointLeft == Double.POSITIVE_INFINITY)
		{
			return hardCodedCost;
		}

		// return cost for num impressions
		return pointLeft[1];
	}

}