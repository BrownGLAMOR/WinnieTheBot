package AdSpaceBidOptimizer;

import java.util.HashMap;
import Models.CostModel;
import Models.RevenueModel;

public class Greedy_All_Edges extends Linear_Quadratic_Optimizer {

	public String get_name(){
		return "greedy all-edges";
	}


	public OptimizationResults solve(CostModel[] cost_models, HashMap<Integer, Boolean[]> connections, HashMap<Integer, Long> campaignReaches) {
		// testing
		final long startTime = System.currentTimeMillis();
		
		// constants
		int num_user_types = cost_models.length;		
		RevenueModel rev_model = new RevenueModel();
		int increment = 5;
		
		// to update
		double[] user_types_imp_count = new double[num_user_types];
		HashMap<Integer, Integer> campaigns_imp_count = new HashMap<Integer, Integer>();
		for (Integer campaignId : connections.keySet())
		{
			campaigns_imp_count.put(campaignId, 0);
		}
		Boolean still_profitable = true;
		
		// to return
		OptimizationResults results;
		double total_revenue = 0;
		double total_cost = 0;
		HashMap<Integer, int[]> allEdges = new HashMap<Integer, int[]>();
		for (Integer campaignId : connections.keySet())
		{
			allEdges.put(campaignId, new int[num_user_types]);
		}
	
		while(still_profitable){
			double best_profit = 0.0;
			double best_cost = 0.0;
			double best_revenue = 0.0;
			int best_user_type = -1;
			int best_campaign = -1;
			//select most profitable campaign to add to			
			for (int ut = 0; ut < num_user_types; ut++)
			{	
				// total impressions not a real number
				if (user_types_imp_count[ut] < cost_models[ut].total_impressions)
				{
					for (Integer campaignId : connections.keySet())
					{
						if (connections.get(campaignId)[ut])
						{		
							double new_rev = rev_model.get_incremental_revenue(campaigns_imp_count.get(campaignId), campaignReaches.get(campaignId), increment);
							double new_cost = cost_models[ut].get_incremental_cost(user_types_imp_count[ut], increment);
							double new_profit = new_rev - new_cost;
							
							if (new_profit > best_profit) {	
								best_user_type = ut;
								best_campaign = campaignId;
								best_profit = new_profit;
								best_revenue = new_rev;
								best_cost = new_cost;
							}
						}
					}
				}
			}
						
			if (best_profit > 0)
			{	
				user_types_imp_count[best_user_type] += 1;
				campaigns_imp_count.put(best_campaign, campaigns_imp_count.get(best_campaign)+1);
				
				int[] currArray = allEdges.get(best_campaign);
				currArray[best_user_type] += 1;
				allEdges.put(best_campaign, currArray);

				total_cost += best_cost;
				total_revenue += best_revenue;
			}
			else
			{
				still_profitable = false;
			}
		}
		final long endTime = System.currentTimeMillis();
		System.out.println("time = "+(endTime-startTime));
		results = new OptimizationResults(total_revenue, total_cost, allEdges);
		return results;
	}

}
