package AdSpaceBidOptimizer;

import java.util.HashMap;
import java.util.Set;

import Models.CostModel;
import Models.RevenueModel;

/* ComputeCampaignBid class calculates the new bid we place on subsequent campaign. */
public class ComputeCampaignBid {

	public long solve (int day, CostModel[] cost_models, ProblemSetup setupNoCampaign, ProblemSetup setupWithCampaign){
		Greedy_All_Edges solver = new Greedy_All_Edges();
		OptimizationResults solutionWithCampaign = solver.solve(day,cost_models, setupWithCampaign.getMatches(), 
				setupWithCampaign.getCampaignReaches(), 
				setupWithCampaign.getStartsAndEnds(),setupWithCampaign.getImpsToGo(),
				setupWithCampaign.getCampaignBudgets());
		
		OptimizationResults solutionNoCampaign = solver.solve(day,cost_models, 
				setupNoCampaign.getMatches(), setupNoCampaign.getCampaignReaches(), 
				setupWithCampaign.getStartsAndEnds(),setupWithCampaign.getImpsToGo(),
				setupWithCampaign.getCampaignBudgets());
		
		System.out.println("Cost w: "+solutionWithCampaign.getCost());
		System.out.println("Cost no: "+solutionNoCampaign.getCost());
		double temp =this.computeNewRevenue(day, 
				solutionWithCampaign.getImpressionAssignments(), 
				setupNoCampaign.getCampaignReaches(), 
				setupNoCampaign.getCampaignIds(),
				setupNoCampaign.getImpsToGo(),
				setupNoCampaign.getStartsAndEnds(),
				setupNoCampaign.getCampaignBudgets()); 
		System.out.println("Rev w: "+temp);
		System.out.println("Rev no: "+solutionNoCampaign.getRevenue());
		long bid = (long) Math.max(0, (solutionWithCampaign.getCost() - solutionNoCampaign.getCost() 
				+Math.abs(solutionNoCampaign.getRevenue()-temp)));
		return bid;
	}

	/* returns the new revenue of all but new campaign with new campaign under consideration. */
	protected double computeNewRevenue(int day, HashMap<Integer, int[]> impressionAssignments, 
			HashMap<Integer,Long> campaignReaches, Set<Integer> campaign_ids, 
			HashMap<Integer,Integer> impsToGo, HashMap<Integer,int[]> startsAndEnds,
			HashMap<Integer, Double>campaignBudgets){
		double newRevenue = 0.0;
		RevenueModel rev_model = new RevenueModel();
		for (int cmpnID : campaign_ids){
			int total_imp_num = 0;
			for (int imp_num : impressionAssignments.get(cmpnID)){
				total_imp_num += imp_num;
			}
			Long reach = campaignReaches.get(cmpnID);
			int days = startsAndEnds.get(cmpnID)[1] - startsAndEnds.get(cmpnID)[0];
			long toAdd= Math.max(0, (int)(reach*(startsAndEnds.get(cmpnID)[1] - day)/days));
			System.out.println("To Add: "+toAdd+" total_imp_num: "+total_imp_num+" reach: "+reach);
			double revFromModel = rev_model.get_total_revenue((int)(total_imp_num+toAdd), campaignReaches.get(cmpnID),
					campaignBudgets.get(cmpnID));
			System.out.println("revFromModel: "+revFromModel);
			newRevenue += revFromModel;
		}
		return newRevenue;
	}

}
