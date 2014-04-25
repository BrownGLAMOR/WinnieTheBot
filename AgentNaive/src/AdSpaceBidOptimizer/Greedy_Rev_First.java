package AdSpaceBidOptimizer;

import java.util.HashMap;

import Models.CostModel;

//
public class Greedy_Rev_First extends Linear_Quadratic_Optimizer {

	@Override
	public OptimizationResults solve(int day, CostModel[] cost_models,
			HashMap<Integer, Boolean[]> connections,
			HashMap<Integer, Long> campaignReaches, 
			HashMap<Integer, int[]> startsAndEnds,
			HashMap<Integer, Integer> impsToGo, 
			HashMap<Integer, Double> campaignBudgets) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String get_name() {
		// TODO Auto-generated method stub
		return null;
	}
//	
//	public String get_name(){
//		return "greedy revenue-first";
//	}
//	
//
//	public int[][] solve(Cost_Model[] cost_models, Revenue_Model[] rev_models, Boolean[][] connections) {
//
//		int num_user_types = cost_models.length;
//		int num_campaigns = rev_models.length;
//
//		int[][] impression_assignments = new int[num_user_types][num_campaigns];
//
//		//these should save some time
//		int[] user_types_imp_count = new int[num_user_types];
//		int[] campaigns_imp_count = new int[num_campaigns];
//
//		Boolean still_profitable = true;
//
//		while(still_profitable){
//			double best_rev = 0.0;
//			double best_cost = Double.POSITIVE_INFINITY;
//			int best_user_type = -1;
//			int best_campaign = -1;
//			//select best revenue campaign to add to
//			for (int cmpn = 0; cmpn < num_campaigns; cmpn++){
//				double new_rev = rev_models[cmpn].get_incremental_revenue(campaigns_imp_count[cmpn]);
//				if (new_rev > best_rev) {
//					best_rev = new_rev;
//					best_campaign = cmpn;
//				}
//			}
//			//select cheapest user type to increment
//			for (int ut = 0; ut < num_user_types; ut++){
//				if ((best_campaign > -1) && (connections[ut][best_campaign])){
//					if (user_types_imp_count[ut] < cost_models[ut].total_impressions){
//							double new_cost = cost_models[ut].get_incremental_cost(user_types_imp_count[ut]);
//							if (new_cost < best_cost) {
//								best_cost = new_cost;
//								best_user_type = ut;
//							}
//						}
//					}
//				}
//			double profit = best_rev - best_cost;
//			if (profit > 0){
//				user_types_imp_count[best_user_type] += 1;
//				campaigns_imp_count[best_campaign] += 1;
//				impression_assignments[best_user_type][best_campaign] += 1;		
//			} else{
//				still_profitable = false;
//			}
//		}
//
//		return impression_assignments;
//	}
//
}
