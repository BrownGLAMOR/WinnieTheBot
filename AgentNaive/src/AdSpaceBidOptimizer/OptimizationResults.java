package AdSpaceBidOptimizer;

import java.util.HashMap;

// data structure for storing the revenue, cost and impressions matrix 


public class OptimizationResults {
	private double revenue;
	private double cost;
	private HashMap<Integer, int[]> impression_assignments;
	
	OptimizationResults(double revenue, double cost, HashMap<Integer, int[]> impression_assignments){
		this.impression_assignments = impression_assignments;
		this.cost = cost;
		this.revenue = revenue;
	}	
		
	public double getRevenue(){
		return this.revenue;
	}
	
	public double getCost(){
		return this.cost;
	}
	
	public HashMap<Integer, int[]> getImpressionAssignments(){
		return this.impression_assignments;
	}
	
}