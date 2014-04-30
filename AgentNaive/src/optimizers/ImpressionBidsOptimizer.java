package optimizers;

import java.util.HashMap;

import models.CostModel;
import models.CostModelGeneral;
import models.RevenueModel;


/*
 * 
 * this is a stencil to solve the simplified adx problem with
 * quadratic revenue models and linear cost models
 * 
 * 
 */

public abstract class ImpressionBidsOptimizer {
	
	//takes as inputs an array of the cost models for each user type and an array of the revenue
	//models for each campaign
	//outputs a 2d array of integers telling how many impressions from each user type should
	//be assigned to each campaign
	//e.g. <solution>[2][3] = 10 means that 10 impressions
	//from the user type 2 should be assigned to campaign 3 (both then are indexed from zero)
	public abstract OptimizationResults solve(int day, CostModelGeneral[] cost_models, HashMap<Integer, Boolean[]> connections, 
			HashMap<Integer, Long> campaignReaches,HashMap<Integer, 
			int[]> startsAndEnds,HashMap<Integer, Integer> impsSoFar, 
			HashMap<Integer, Double> campaignBudgets);
	
	public abstract OptimizationResults solve(int day, CostModelGeneral[] cost_models, ProblemSetup problemSetup);
	
	public abstract String get_name();
	

}
