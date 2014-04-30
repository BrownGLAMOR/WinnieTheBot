package models;

//ADD commons-math3-3.2.jar TO CLASSPATH WHEN COMPILING/RUNNING FOR THIS TO WORK

import org.apache.commons.math3.stat.regression.SimpleRegression;

public class CostModelOLS extends CostModelGeneral {

	private int totalImpressions;
	private int total_imps_young = 800;
	private int total_imps_old = 200;
	private int total_imps_female = 200;
	private int total_imps_male = 800;
	private int total_imps_high = 200;
	private int total_imps_low = 800;

	private SimpleRegression _regression = new SimpleRegression();

	private String _type;
	private int _size = 0;
	private double hardCodedCost = 0.001;

	public CostModelOLS(String type) {
		_type = type;
		if (type.equals("young"))
			totalImpressions = total_imps_young;
		if (type.equals("old"))
			totalImpressions = total_imps_old;
		if (type.equals("female"))
			totalImpressions = total_imps_female;
		if (type.equals("male"))
			totalImpressions = total_imps_male;
		if (type.equals("high_income"))
			totalImpressions = total_imps_high;
		if (type.equals("low_income"))
			totalImpressions = total_imps_low;
	}

	public String getType() {
		return _type;
	}
	
	public int getTotalImpressions()
	{
		return totalImpressions;
	}

	// cost for one more impressions
	public double get_incremental_cost(double numImpressions, int increment) {
		return hardCodedCost * increment;
	}

	// add known impression count : cost
	public void addDataPoint(double numImpressions, double cost) {
		_regression.addData(numImpressions, cost);
		_size++;
	}

	// it looked like this was just simple prediction, so I used the builtin
	// methods to do that
	// let me know if something different was needed
	public double get_incremental_cost_old(double numImpressions, int increment) {
		// technically, Double values can be passed as null, which then raise
		// NPE when they
		// are autoboxed into a double.
		if (_size < 10)// || numImpressions == null)
		{
			return hardCodedCost;
		}
		double toReturn = (_regression.predict(numImpressions + 1) - _regression
				.predict(numImpressions)) * increment;
		return toReturn;

	}

	/*
	 * //basic testing stuff feel free to comment out public static void main
	 * (String[] args) { double[][] testData = {
	 * {0,1},{1,2},{2,3},{3,4},{4,5},{7,8}}; CostModelOLS c = new
	 * CostModelOLS("type"); for (int i=0; i< testData.length; i++){ double[] d
	 * = testData[i]; c.addDataPoint(d[0],d[1]); }
	 * System.out.println(c.get_incremental_cost(6.0)); // outputs 7 }
	 */

}
