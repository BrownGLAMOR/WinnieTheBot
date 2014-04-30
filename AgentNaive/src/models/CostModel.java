package models;

import java.util.ArrayList;

public class CostModel extends CostModelGeneral {
	
	private int totalImpressions;
	private ArrayList<double[]> dataPoints = new ArrayList<double[]>();
	private double hardCodedCost = 0.001;
	
	
	private int total_imps_young = 800;
	private int total_imps_old = 200;
	private int total_imps_female = 200;
	private int total_imps_male = 800;
	private int total_imps_high = 200;
	private int total_imps_low = 800;
	
	private String _type;
	
	public CostModel(String type)
	{
		_type = type;
		if (type.equals("young")) totalImpressions = total_imps_young;
		if (type.equals("old")) totalImpressions = total_imps_old;
		if (type.equals("female")) totalImpressions = total_imps_female;
		if (type.equals("male")) totalImpressions = total_imps_male;
		if (type.equals("high_income")) totalImpressions = total_imps_high;
		if (type.equals("low_income")) totalImpressions = total_imps_low;
	}
	
	public String getType()
	{
		return _type;
	}
	
	public int getTotalImpressions()
	{
		return totalImpressions;
	}
	
	// cost for one more impressions
	public double get_incremental_cost_old(double numImpressions, int increment)
	{
		return hardCodedCost * increment;
	}
	
	// add known impression count : cost
	public void addDataPoint(double numImpressions, double cost)
	{
		// x, y where x = num imps and y = cost
		double[] point = new double[2];
		point[0] = numImpressions;
		point[1] = cost;
		dataPoints.add(point);
	}
	
	public double get_incremental_cost(double numImpressions, int increment)
	{
		if (dataPoints.size() < 2)
		{
			return hardCodedCost * increment;
		}
		double distPointLeft = Double.POSITIVE_INFINITY;
		double[] pointLeft = new double[2];
		double distPointRight = Double.POSITIVE_INFINITY;
		double[] pointRight = new double[2];
		
		// find closest point on left and right of numImpressions
		for (int i = 0; i < dataPoints.size(); i++)
		{
			double[] currPoint = dataPoints.get(i);
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
		double y2 = (slope * (x+1)) + b;
		
		// return cost for num impressions
		//System.out.println("5");
		return (y2 - y)*increment;
	}

	public double get_incremental_cost_fast(double numImpressions, int increment)
	{
		if (dataPoints.size() < 2)
		{
			return hardCodedCost;
		}
		double distPointLeft = Double.POSITIVE_INFINITY;
		double[] pointLeft = new double[2];
		
		// find closest point on left and right of numImpressions
		for (int i = 0; i < dataPoints.size(); i++)
		{
			double[] currPoint = dataPoints.get(i);
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
		return (pointLeft[1]/numImpressions)*increment;
	}

}
