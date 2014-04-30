package models;

public class RevenueModel {

	private double a = 4.08577;
	private double b = 3.08577;
	private double atan_neg_b = -1.2574074189761424;
	
	public double get_incremental_revenue(int numImpressions, double campaignReach, int increment, double budget) {
		// NOTE: numImpressions = effective impressions
		double a_div_campaignReach = a / campaignReach;
		
		double numerator = a_div_campaignReach * increment;
		double denominator =   (a_div_campaignReach * (numImpressions + increment) - b)
		                     * (a_div_campaignReach * numImpressions - b)
		                     + 1.0;
		
		double delArcTan = Math.atan(numerator / denominator);
		
		return (2.0/a) * delArcTan * budget;
	}
	
	public double get_total_revenue(int numImpressions, double campaignReach,double budget)
	{
		// NOTE: numImpressions = effective impressions
		double atan1 = Math.atan((a * numImpressions / campaignReach) - b);
		double currRev = (2.0/a) * (atan1 - atan_neg_b);

		return currRev*budget;
	}
	
	
	/*
	public static double get_incremental_revenue(int numImpressions, 
			double campaignReach, int increment, double budget)
	{
		// NOTE: numImpressions = effective impressions
		double atan1 = Math.atan((a * numImpressions / campaignReach) - b);
		double atan2 = Math.atan(-b);
		double currRev = (2/a) * (atan1 - atan2);

		double atan1Two = Math.atan((a * (numImpressions+increment) / campaignReach) - b);
		double atan2Two = Math.atan(-b);
		double newRev = (2/a) * (atan1Two - atan2Two);

		return (newRev - currRev)*budget;
	}


	public static double get_total_revenue(int numImpressions, double campaignReach,double budget)
	{
		// NOTE: numImpressions = effective impressions
		double atan1 = Math.atan((a * numImpressions / campaignReach) - b);
		double atan2 = Math.atan(-b);
		double currRev = (2/a) * (atan1 - atan2);

		return currRev*budget;
	}
	
	public static void main(String[] args) {
		int numImpressions = 17;
		double campaignReach = 3.0;
		int increment = 4;
		double budget = 37.0;
		
		System.out.println("curr");
		double curr = get_incremental_revenue(numImpressions, campaignReach, increment, budget);
		System.out.println(curr);
		curr = get_total_revenue(numImpressions, campaignReach, budget);
		System.out.println(curr);

		System.out.println("simple");
		double simple = get_incremental_revenue_simple(numImpressions, campaignReach, increment, budget);
		System.out.println(simple);
		simple = get_total_revenue_simple(numImpressions, campaignReach, budget);
		System.out.println(simple);
		
		// sanity check.  println conditions shouldn't trigger
		for (int i = 0; i < 10000; ++i) {
			int a = (int)Math.random();
			double b = Math.random()*1.0;
			int c = (int)Math.random();
			double d = Math.random()*1.0;
			
			double del = get_incremental_revenue_simple(a,b,c,d) - get_incremental_revenue(a,b,c,d);
			if (Math.abs(del) > 0.001) {
				System.out.println("noooooees!!!!");
			}
			del = get_total_revenue_simple(a,b,d) - get_total_revenue(a,b,d);
			if (Math.abs(del) > 0.001) {
				System.out.println("noooooees!!!!");
			}
		}
		
		// not the best benchmark, but demonstrative enough
		
		long startTime = System.nanoTime();
		long endTime = System.nanoTime();
		int num_runs = 10000000;
		
		int a = (int)Math.random();
		double b = Math.random()*1.0;
		int c = (int)Math.random();
		double d = Math.random()*1.0;
		
		startTime = System.nanoTime();
		for (int i = 0; i < num_runs; ++i) {
			double r = get_incremental_revenue_simple(a,b,c,d);
			double tr = get_total_revenue_simple(a,b,d);
		}
		endTime = System.nanoTime();
		double del_time = (endTime - startTime) / Math.pow(10.0, 9.0);
		System.out.println("new version takes " + del_time + " sec");
		
		startTime = System.nanoTime();
		for (int i = 0; i < num_runs; ++i) {
			double r = get_incremental_revenue(a,b,c,d);
			double tr = get_total_revenue(a,b,d);
		}
		endTime = System.nanoTime();
		double curr_del_time = (endTime - startTime) / Math.pow(10.0, 9.0);
		System.out.println("current version takes " + curr_del_time + " sec");
		
		System.out.println("speedup = " + curr_del_time / del_time);
	}
	*/
}

