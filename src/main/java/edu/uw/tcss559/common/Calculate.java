package edu.uw.tcss559.common;

import java.util.List;

public class Calculate {

	private Calculate() {}
	
	/**
	 * Calculates the standard deviation of given data points.
	 * @param dataPoints A list of data points whose standard deviation is to be computed
	 * @return Standard Deviation
	 */
    public static double standardDeviation(final List<Double> dataPoints) {
    	final double mean = dataPoints.stream().mapToDouble(a -> a).average().getAsDouble();
        double standardDeviation = 0.0;

        for(final Double dataPoint: dataPoints) {
            standardDeviation += Math.pow(dataPoint - mean, 2);
        }

        return Math.sqrt(standardDeviation/dataPoints.size());
    }	
    
    /**
     * Calculates the correlation factor between given data points
     * @param x a list of data points
     * @param y another list of data points
     * @return correlation coefficient
     */
    public static double correlation(final double[] x, final double[] y) {
    	if (x.length != y.length) {
    		return 0.0;
    	}
    	
    	final int n = x.length;
    	
    	double sumX = 0;
    	double sumY = 0;
    	double sumXY = 0;
    	double sumX2 = 0;
    	double sumY2 = 0;
    	
    	for (int i = 0; i < n; i++) {
    		sumX += x[i];
    		sumY += y[i];
    		sumXY += x[i] * y[i];
    		sumX2 += Math.pow(x[i], 2);
    		sumY2 += Math.pow(y[i], 2);
    	}
    	
    	final double numerator = (n * sumXY) - (sumX * sumY);
    	final double denomenator = Math
    			.sqrt(((n * sumX2) - Math.pow(sumX, 2)) * ((n * sumY2) - Math.pow(sumY, 2)));
    	
    	return numerator/denomenator;
    }

}
