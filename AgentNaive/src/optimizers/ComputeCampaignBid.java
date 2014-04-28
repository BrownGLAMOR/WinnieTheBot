package optimizers;

import java.util.HashMap;
import java.util.Set;

import models.CostModel;
import models.RevenueModel;


/* ComputeCampaignBid class calculates the new bid we place on subsequent campaign. */
public class ComputeCampaignBid extends CampaignBidOptimizer{

	public long solve (int day, CostModel[] cost_models, ProblemSetup setupNoCampaign, ProblemSetup setupWithCampaign){

		GreedyByEdges solver = new GreedyByEdges();
		//find Solution with the campaign
		OptimizationResults solutionWithCampaign = solver.solve(day,cost_models, setupWithCampaign.getMatches(), 
				setupWithCampaign.getCampaignReaches(), 
				setupWithCampaign.getStartsAndEnds(),setupWithCampaign.getImpsToGo(),
				setupWithCampaign.getCampaignBudgets());

		//find solution without the campaign
		OptimizationResults solutionNoCampaign = solver.solve(day,cost_models, 
				setupNoCampaign.getMatches(), setupNoCampaign.getCampaignReaches(), 
				setupNoCampaign.getStartsAndEnds(),setupNoCampaign.getImpsToGo(),
				setupNoCampaign.getCampaignBudgets());
		//double costDiff = calcCostDiff(setupNoCampaign,setupWithCampaign);
		//System.out.println("Cost w: "+solutionWithCampaign.getCost()+" Cost no: "+solutionNoCampaign.getCost());

		//Calculate the revenue with the campaign
		double with =this.computeNewRevenue(day, 
				solutionWithCampaign.getImpressionAssignments(), 
				setupNoCampaign.getCampaignReaches(), 
				setupNoCampaign.getCampaignIds(),
				setupNoCampaign.getImpsToGo(),
				setupNoCampaign.getStartsAndEnds(),
				setupNoCampaign.getCampaignBudgets());
		
		//Calculate the revenue without the 
		//double without = solutionNoCampaign.getRevenue();
		double without =this.computeNewRevenue(day, 
				solutionNoCampaign.getImpressionAssignments(), 
				setupNoCampaign.getCampaignReaches(), 
				setupNoCampaign.getCampaignIds(),
				setupNoCampaign.getImpsToGo(),
				setupNoCampaign.getStartsAndEnds(),
				setupNoCampaign.getCampaignBudgets()); 
		System.out.println("Rev w: "+with+" Rev no: "+without);
		//long bid = (long) Math.max(40, 
		//(solutionWithCampaign.getCost()-solutionNoCampaign.getCost()+without-with));
		long bid = (long) Math.max(300, 
				(without-with));
		return bid;
	}

	private double calcCostDiff(ProblemSetup setupNoCampaign,
			ProblemSetup setupWithCampaign) {
		double cost = 0.0;
		for(int id : setupWithCampaign.getCampaignIds()){
			if(!setupNoCampaign.getCampaignIds().contains(id)){
				cost = 0.00000000001*setupWithCampaign.getCampaignReaches().get(id);
			}
		}
		return cost;
	}

	/* returns the new revenue of all but new campaign with new campaign under consideration. */
	protected double computeNewRevenue(int day, HashMap<Integer, int[]> impressionAssignments, 
			HashMap<Integer,Long> campaignReaches, Set<Integer> campaign_ids, 
			HashMap<Integer,Integer> impsToGo, HashMap<Integer,int[]> startsAndEnds,
			HashMap<Integer, Double>campaignBudgets){
		
		double newRevenue = 0.0;
		RevenueModel rev_model = new RevenueModel();
		//for each campaign, if the campaign is still valid, add it's revenue to the total
		for (int cmpnID : campaign_ids){
			if(startsAndEnds.get(cmpnID)[1]>day){
				
				int total_imp_num = 0;
				for (int imp_num : impressionAssignments.get(cmpnID)){
					total_imp_num += imp_num;
				}
				Long reach = campaignReaches.get(cmpnID);
				int days = startsAndEnds.get(cmpnID)[1] - startsAndEnds.get(cmpnID)[0];
				long toAdd= Math.max(0, (int)(reach*(startsAndEnds.get(cmpnID)[1] - day+1)/days));
				//System.out.println("To Add: "+toAdd+" total_imp_num: "+total_imp_num+" reach: "+reach);
				double revFromModel = rev_model.get_total_revenue((int)(total_imp_num+toAdd+campaignReaches.get(cmpnID)
						-impsToGo.get(cmpnID)), campaignReaches.get(cmpnID),
						campaignBudgets.get(cmpnID));
				//System.out.println("revFromModel: "+revFromModel);
				newRevenue += revFromModel;
			}
		}
		return newRevenue;
	}

}
