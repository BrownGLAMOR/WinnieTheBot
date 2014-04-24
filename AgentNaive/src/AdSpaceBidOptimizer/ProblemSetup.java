package AdSpaceBidOptimizer;

import java.util.HashMap;
import java.util.Set;

public class ProblemSetup {
	private HashMap<Integer, Boolean[]> matches;
	private HashMap<Integer,Long> campaign_reaches;
	private Set<Integer> campaignIds;

	public ProblemSetup(HashMap<Integer, Boolean[]> matches, HashMap<Integer,Long> campaign_reaches,Set<Integer> campaignIds){
		this.matches = matches;
		this.campaign_reaches = campaign_reaches;
		this.campaignIds = campaignIds;
	}

	public HashMap<Integer,Long> getCampaignReaches(){
		return this.campaign_reaches;
	}

	public HashMap<Integer, Boolean[]> getMatches(){
		return this.matches;
	}
	public Set<Integer> getCampaignIds() {
		return this.campaignIds;
	}
}	
