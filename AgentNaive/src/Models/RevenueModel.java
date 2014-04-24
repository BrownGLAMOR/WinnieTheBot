package Models;

public class RevenueModel {
	
	double a = 4.08577;
	double b = 3.08577;
	
	public double get_incremental_revenue(int numImpressions, double campaignReach, int increment)
	{
		// NOTE: numImpressions = effective impressions
		double atan1 = Math.atan((a * numImpressions / campaignReach) - b);
		double atan2 = Math.atan(-b);
		double currRev = (2/a) * (atan1 - atan2);
		
		double atan1Two = Math.atan((a * (numImpressions+1) / campaignReach) - b);
		double atan2Two = Math.atan(-b);
		double newRev = (2/a) * (atan1Two - atan2Two);
		
		return (newRev - currRev)*increment;
	}

	
	public double get_total_revenue(int numImpressions, double campaignReach)
	{
		// NOTE: numImpressions = effective impressions
		double atan1 = Math.atan((a * numImpressions / campaignReach) - b);
		double atan2 = Math.atan(-b);
		double currRev = (2/a) * (atan1 - atan2);
		
		return currRev;
	}
	
}