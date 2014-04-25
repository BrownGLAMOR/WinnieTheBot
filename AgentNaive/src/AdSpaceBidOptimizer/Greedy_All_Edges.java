package AdSpaceBidOptimizer;

import java.util.HashMap;
import Models.CostModel;
import Models.RevenueModel;

public class Greedy_All_Edges extends Linear_Quadratic_Optimizer {

	public String get_name(){
		return "greedy all-edges";
	}

	public OptimizationResults solve(int day, CostModel[] cost_models, HashMap<Integer, Boolean[]> connections, 
			HashMap<Integer, Long> campaignReaches, HashMap<Integer, int[]> startsAndEnds,
			HashMap<Integer, Integer> impsToGo, HashMap<Integer, Double> campaignBudgets) {
		// testing
		final long startTime = System.currentTimeMillis();
		
		// constants
		int num_user_types = cost_models.length;		
		RevenueModel rev_model = new RevenueModel();
		int increment = 50;
		
		// to update
		double total_revenue = 0;
		double[] user_types_imp_count = new double[num_user_types];
		HashMap<Integer, Integer> campaigns_imp_count = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> phantomImps = new HashMap<Integer, Integer>();
		for (Integer campaignId : connections.keySet())
		{
		    campaigns_imp_count.put(campaignId, 0);
		    Long reach = campaignReaches.get(campaignId);
            int days = startsAndEnds.get(campaignId)[1] - startsAndEnds.get(campaignId)[0];
            
		    phantomImps.put(campaignId, Math.max(0, (int)(reach*(startsAndEnds.get(campaignId)[1] - day)/days)));
		    total_revenue+= rev_model.get_total_revenue(phantomImps.get(campaignId), reach, campaignBudgets.get(campaignId));
		}
		Boolean still_profitable = true;
		
		// to return
		OptimizationResults results;
		
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
							double new_rev = 
									rev_model.get_incremental_revenue((int)(campaignReaches.get(campaignId)-impsToGo.get(campaignId))+phantomImps.get(campaignId)+campaigns_imp_count.get(campaignId), 
											campaignReaches.get(campaignId), increment,campaignBudgets.get(campaignId));
							double new_cost = cost_models[ut].get_incremental_cost_twoPts(user_types_imp_count[ut], increment);
							//System.out.println("cost: "+new_cost);
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
				user_types_imp_count[best_user_type] += increment;
				campaigns_imp_count.put(best_campaign, campaigns_imp_count.get(best_campaign)+increment);
				
				int[] currArray = allEdges.get(best_campaign);
				currArray[best_user_type] += increment;
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
