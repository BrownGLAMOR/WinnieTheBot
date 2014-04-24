package AdSpaceBidOptimizer;

import java.util.HashMap;
import java.util.Set;

import Models.CostModel;
import Models.RevenueModel;

/* ComputeCampaignBid class calculates the new bid we place on subsequent campaign. */
public class ComputeCampaignBid {
	
	public long solve (CostModel[] cost_models, ProblemSetup setupNoCampaign, ProblemSetup setupWithCampaign){
		Greedy_All_Edges solver = new Greedy_All_Edges();
		OptimizationResults solutionWithCampaign = solver.solve(cost_models, setupWithCampaign.getMatches(), setupWithCampaign.getCampaignReaches());
		OptimizationResults solutionNoCampaign = solver.solve(cost_models, setupNoCampaign.getMatches(), setupNoCampaign.getCampaignReaches());
		//System.out.println("Cost w: "+solutionWithCampaign.getCost());
		//System.out.println("Cost no: "+solutionNoCampaign.getCost());
		double temp =this.computeNewRevenue(solutionWithCampaign.getImpressionAssignments(), setupNoCampaign.getCampaignReaches(), setupNoCampaign.getCampaignIds()); 
		//System.out.println("Rev w: "+temp);
		//System.out.println("Rev no: "+solutionNoCampaign.getRevenue());
		long bid = (long) (solutionWithCampaign.getCost() - solutionNoCampaign.getCost() - 
					 temp + solutionNoCampaign.getRevenue());
		return bid;
	}
	
	/* returns the new revenue of all but new campaign with new campaign under consideration. */
	protected double computeNewRevenue(HashMap<Integer, int[]> impressionAssignments, HashMap<Integer,Long> reaches,Set<Integer> campaign_ids){
		double newRevenue = 0.0;
		RevenueModel rev_model = new RevenueModel();
		for (int cmpnID : campaign_ids){
			int total_imp_num = 0;
			for (int imp_num : impressionAssignments.get(cmpnID)){
				total_imp_num += imp_num;
			}
			newRevenue += rev_model.get_total_revenue(total_imp_num+100, reaches.get(cmpnID));
		}
		return newRevenue;
	}
	
}
