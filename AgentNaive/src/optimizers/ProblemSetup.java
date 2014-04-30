package optimizers;

import java.util.HashMap;
import java.util.Set;

public class ProblemSetup {
	private HashMap<Integer, Boolean[]> _matches;
	private HashMap<Integer,Long> _campaign_reaches;
	private Set<Integer> _campaignIds;
	private HashMap<Integer,Integer> _impsToGo;
	private HashMap<Integer,Integer> _daysToGo;
	private HashMap<Integer,int[]>_startsAndEnds;
	private HashMap<Integer,Double> _campaignBudgets;
	

	public ProblemSetup(HashMap<Integer, Boolean[]> matches, 
			HashMap<Integer,Long> campaign_reaches,Set<Integer> campaignIds,
			HashMap<Integer,Integer> effectiveToGo,
			HashMap<Integer,Integer> daysToGo, HashMap<Integer,
			int[]>startsAndEnds,HashMap<Integer,Double> campaignBudgets){
		this._matches = matches;
		
		for (int key : matches.keySet())
		{
			for (int i = 0; i < 168; i++)
			{
				if (matches.get(key)[i])
				{
					System.out.print(matches.get(key)[i]+" --  ");
				}
			}
			System.out.println();
		}
		
		for (int key : _matches.keySet())
		{
			for (int i = 0; i < 168; i++)
			{
				if (_matches.get(key)[i])
				{
					System.out.print(_matches.get(key)[i]+" ++  ");
				}
			}
			System.out.println();
		}
		this._campaign_reaches = campaign_reaches;
		this._campaignIds = campaignIds;
		this._impsToGo=effectiveToGo;
		this._daysToGo=daysToGo;
		this._startsAndEnds=startsAndEnds;
		this._campaignBudgets=campaignBudgets;
	}

	public HashMap<Integer,Long> getCampaignReaches(){
		return this._campaign_reaches;
	}

	public HashMap<Integer, Boolean[]> getMatches(){
		return this._matches;
	}
	public HashMap<Integer, Integer> getImpsToGo(){
		return this._impsToGo;
	}
	public HashMap<Integer, Integer> getDaysToGo(){
		return this._daysToGo;
	}
	public Set<Integer> getCampaignIds() {
		return this._campaignIds;
	}

	public HashMap<Integer, Double> getCampaignBudgets() {
		return this._campaignBudgets;
	}
	public HashMap<Integer, int[]> getStartsAndEnds() {
		
		return this._startsAndEnds;
	}
}	
