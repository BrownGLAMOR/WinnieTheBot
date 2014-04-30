package optimizers;

import java.util.HashMap;

import models.CostModel;
import models.CostModelGeneral;
import models.RevenueModel;

public class GreedyByEdges extends ImpressionBidsOptimizer {
	
	int increment = 5;
	
	public OptimizationResults solve(int day, CostModelGeneral[] cost_models, ProblemSetup problemSetup) {
		
		return solve(day,cost_models, problemSetup.getMatches(), problemSetup.getCampaignReaches(),
				problemSetup.getStartsAndEnds(),
				problemSetup.getImpsToGo(), problemSetup.getCampaignBudgets());
		
	}
	
	public OptimizationResults solve(int day, CostModelGeneral[] costModels, HashMap<Integer, Boolean[]> connections, 
			HashMap<Integer, Long> campaignReaches, HashMap<Integer, int[]> startsAndEnds,
			HashMap<Integer, Integer> impsToGo, HashMap<Integer, Double> campaignBudgets) {
		// testing
		final long startTime = System.currentTimeMillis();
		
		// constants
		int num_user_types = costModels.length;		
		RevenueModel rev_model = new RevenueModel();
		
		// to update
		double total_revenue = 0;
		double[] userTypesImpCount = new double[num_user_types];
		HashMap<Integer, Integer> campaigns_imp_count = new HashMap<Integer, Integer>();
		
		
		//multiday fix, assume each day is the last and that the agent
		//has already won some impressions
		HashMap<Integer, Integer> phantomImps = new HashMap<Integer, Integer>();
		//construct calculate data
		for (Integer campaignId : connections.keySet())
		{
			//number assigned to this contract for today
		    campaigns_imp_count.put(campaignId, 0);
		    //goal number of impressions for this campaign
		    Long reach = campaignReaches.get(campaignId);
		    //days in campaign
		    
		    if (startsAndEnds.containsKey(campaignId) && startsAndEnds.get(campaignId) != null)
		    {
		    	int days = startsAndEnds.get(campaignId)[1] - startsAndEnds.get(campaignId)[0];	
		    	//fake impressions to include
			    phantomImps.put(campaignId, Math.max(0, (int)(reach*(startsAndEnds.get(campaignId)[1] - day)/days)));
			    //revenue from former and phantom imps
			    //total_revenue+= rev_model.get_total_revenue((int)(phantomImps.get(campaignId)+campaignReaches.get(campaignId)-impsToGo.get(campaignId)), reach, campaignBudgets.get(campaignId));
		    } 

		}
		Boolean still_profitable = true;
		
		// to return
		OptimizationResults results;
		
		double total_cost = 0;
		HashMap<Integer, int[]> allEdges = new HashMap<Integer, int[]>();
		//initialize edges
		for (Integer campaignId : connections.keySet())
		{
			//num=num+1;
			allEdges.put(campaignId, new int[num_user_types]);
		}
		//while some edge is profitable, increment the most profitable edge
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
				if (userTypesImpCount[ut] < costModels[ut].getTotalImpressions())
				{
					//for each campaign
					for (int campaignId : connections.keySet())
					{
						//if there is an edge here
						if (connections.get(campaignId)[ut])// && phantomImps.containsKey(campaignId))
						{		
							//number of imps before (won so far + phantom imps+ added by alg. already)
							//int numImpsStart = (int)(campaignReaches.get(campaignId)-impsToGo.get(campaignId))+phantomImps.get(campaignId)+campaigns_imp_count.get(campaignId);
							int numImpsStart = campaigns_imp_count.get(campaignId);
							System.out.println("numImpsStart = "+numImpsStart);
							System.out.println("reach = "+campaignReaches.get(campaignId));
							System.out.println("budget = "+campaignBudgets.get(campaignId)); // NULL!!!
							double new_rev = rev_model.get_incremental_revenue(numImpsStart, 
											campaignReaches.get(campaignId), increment,campaignBudgets.get(campaignId));
							double new_cost = costModels[ut].get_incremental_cost(userTypesImpCount[ut], increment);
							double new_profit = new_rev - new_cost;
							//System.out.println("rev: "+new_rev);
							//System.out.println("cost: "+new_cost);
							//System.out.println("profit: "+new_profit);
							//if best so far, track it
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
			//if found an edge to increment, make increment
			if (best_profit > 0)
			{	
				userTypesImpCount[best_user_type] += increment;
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
		System.out.println("time = "+(endTime-startTime));//+" num: "+num);
		results = new OptimizationResults(total_revenue, total_cost, allEdges);
		return results;
	}
	
	public String get_name(){
		return "greedy all-edges";
	}

}
