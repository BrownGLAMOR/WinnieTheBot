package Models;
//ADD commons-math3-3.2.jar TO CLASSPATH WHEN COMPILING/RUNNING FOR THIS TO WORK

import org.apache.commons.math3.stat.regression.SimpleRegression;

public class CostModelOLS {

  public int total_impressions = 8000;
  private SimpleRegression _regression = new SimpleRegression();

  private String _type;
  private int _size=0;

  public CostModelOLS(String type)
  {
    _type = type;
  }

  public String getType()
  {
    return _type;
  }

  // cost for one more impressions
  public double get_incremental_cost_old(int numImpressions)
  {
    return 0.0000001;
  }

  // add known impression count : cost
  public void addDataPoint(Double numImpressions, Double cost)
  {
    _regression.addData(numImpressions, cost);
    _size++;
  }

  //it looked like this was just simple prediction, so I used the builtin
  //methods to do that
  //let me know if something different was needed
  public double get_incremental_cost(Double numImpressions)
  {
    //technically, Double values can be passed as null, which then raise NPE when they
    //are autoboxed into a double.
    if (_size < 2 || numImpressions == null)
    {
      return 0.0000001;
    }

    return _regression.predict(numImpressions);

  }
  //basic testing stuff feel free to comment out
  public static void main (String[] args) {
    double[][] testData = { {0,1},{1,2},{2,3},{3,4},{4,5},{7,8}};
    CostModelOLS c = new CostModelOLS("type");
    for (int i=0; i< testData.length; i++){
      double[] d = testData[i];
      c.addDataPoint(d[0],d[1]);
    }
    System.out.println(c.get_incremental_cost(6.0)); // outputs 7
  }

}
