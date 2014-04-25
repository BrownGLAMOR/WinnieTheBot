package AdSpaceBidOptimizer;

import java.util.HashMap;
import java.util.Set;

public class ProblemSetup {
	private HashMap<Integer, Boolean[]> matches;
	private HashMap<Integer,Long> campaign_reaches;
	private Set<Integer> campaignIds;
	private HashMap<Integer,Integer> impsToGo;
	private HashMap<Integer,Integer> daysToGo;
	private HashMap<Integer,int[]>startsAndEnds;
	private HashMap<Integer,Double> campaignBudgets;
	

	public ProblemSetup(HashMap<Integer, Boolean[]> matches, 
			HashMap<Integer,Long> campaign_reaches,Set<Integer> campaignIds,
			HashMap<Integer,Integer> effectiveToGo,
			HashMap<Integer,Integer> daysToGo, HashMap<Integer,
			int[]>startsAndEnds,HashMap<Integer,Double> campaignBudgets){
		this.matches = matches;
		this.campaign_reaches = campaign_reaches;
		this.campaignIds = campaignIds;
		this.impsToGo=effectiveToGo;
		this.daysToGo=daysToGo;
		this.startsAndEnds=startsAndEnds;
		this.campaignBudgets=campaignBudgets;
	}

	public HashMap<Integer,Long> getCampaignReaches(){
		return this.campaign_reaches;
	}

	public HashMap<Integer, Boolean[]> getMatches(){
		return this.matches;
	}
	public HashMap<Integer, Integer> getImpsToGo(){
		return this.impsToGo;
	}
	public HashMap<Integer, Integer> getDaysToGo(){
		return this.daysToGo;
	}
	public Set<Integer> getCampaignIds() {
		return this.campaignIds;
	}

	public HashMap<Integer, Double> getCampaignBudgets() {
		return this.campaignBudgets;
	}
	public HashMap<Integer, int[]> getStartsAndEnds() {
		
		return this.startsAndEnds;
	}
}	
