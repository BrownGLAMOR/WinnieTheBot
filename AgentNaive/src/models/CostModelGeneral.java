package models;

public abstract class CostModelGeneral {
	public abstract int getTotalImpressions();
	public abstract double get_incremental_cost(double numImpressions, int increment);
	public abstract String getType();
	public abstract void addDataPoint(double numImpressions, double cost);
}
