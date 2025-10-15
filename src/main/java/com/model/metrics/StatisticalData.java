package com.model.metrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatisticalData {
	
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(StatisticalData.class);
    
    private final String metricName;
    private int count;
    private double min;
    private double max;
    private double mean;
    private double median;
    private double stdDev;
    private double q1;
    private double q3;
    
    public StatisticalData(String metricName) {
        if (metricName == null || metricName.isBlank()) {
            throw new IllegalArgumentException("Metric name cannot be null or blank");
        }
        this.metricName = metricName.trim();
    }
    
    public String getMetricName() { return metricName; }
    public int getCount() { return count; }
    public double getMin() { return min; }
    public double getMax() { return max; }
    public double getMean() { return mean; }
    public double getMedian() { return median; }
    public double getStdDev() { return stdDev; }
    public double getQ1() { return q1; }
    public double getQ3() { return q3; }
    
    public double getRange() { 
        return max - min; 
    }
    
    public double getIQR() { 
        return q3 - q1; 
    }
    
    public void setCount(int count) { this.count = count; }
    public void setMin(double min) { this.min = min; }
    public void setMax(double max) { this.max = max; }
    public void setMean(double mean) { this.mean = mean; }
    public void setMedian(double median) { this.median = median; }
    public void setStdDev(double stdDev) { this.stdDev = stdDev; }
    public void setQ1(double q1) { this.q1 = q1; }
    public void setQ3(double q3) { this.q3 = q3; }

}
