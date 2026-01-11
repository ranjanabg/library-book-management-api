package edu.uw.tcss559.mcda;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.DoubleStream;

import edu.uw.tcss559.common.Calculate;
import edu.uw.tcss559.common.Transformer;
import edu.uw.tcss559.structures.mcda.Alternative;

public class CriticCore {
	
	private CriticCore() {}
	
	public static double[] computeWeights(final List<Alternative> alternatives) {
		return computeWeights(Transformer.buildMatrix(alternatives));
	}
	
	/**
	 * Computes the weights based on Critic method
	 * @param decisionMatrix Input decision matrix. 
	 * 		  That is list of alternatives and their criterion values
	 * @return Weights of each criterion or attribute
	 */
	public static double[] computeWeights(final double[][] decisionMatrix) {
		final int m = decisionMatrix.length; // rows
		final int n = decisionMatrix[0].length; // columns
		
		// Step 1: Normalize the decision matrix
		final double[] best = new double[n];
		final double[] worst = new double[n];
		for (int j = 0; j < n; j++) {
			best[j] = Double.MAX_VALUE;
			worst[j] = Double.MIN_VALUE;
			for (int i = 0; i < m; i++) {
				if (decisionMatrix[i][j] < best[j]) {
					best[j] = decisionMatrix[i][j];
				}
				
				if (decisionMatrix[i][j] > worst[j]) {
					worst[j] = decisionMatrix[i][j];
				}
			}
		}
		
		for (int j = 0; j < n; j++) {
			final double normalizerBase = best[j] - worst[j];
			for (int i = 0; i < m; i++) {
				decisionMatrix[i][j] -= worst[j];
				decisionMatrix[i][j] /= normalizerBase;
			}
		}
		
		// Step 2: Calculate standard deviation for each criteria
		final double[] sigma = new double[n];
		for (int j = 0; j < n; j++) {
			final List<Double> colData = new ArrayList<>();
			for (int i = 0; i < m; i++) {
				colData.add(decisionMatrix[i][j]);
			}
			sigma[j] = Calculate.standardDeviation(colData);
		}
		
		// Step 3: Determine the symmetric matrix
		final double[][] symmetricMatrix = new double[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (i == j) {
					symmetricMatrix[i][j] = 1.0;
					continue;
				}
				
				final double[] colI = new double[m];
				final double[] colJ = new double[m];
				for (int k = 0; k < m; k++) {
					colI[k] = decisionMatrix[k][i];
					colJ[k] = decisionMatrix[k][j];
				}
				
				symmetricMatrix[i][j] = Calculate.correlation(colI, colJ);
			}
		}
		
		// Step 4: Calculate measure of the conflict
		final double[] conflictMeasure = new double[n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {	
				symmetricMatrix[i][j] = 1.0 - symmetricMatrix[i][j];
				conflictMeasure[i] += symmetricMatrix[i][j];
			}
		}
		
		// Step 5: Determine the quantity of the information 
		// in relation to each criterion
		for (int j = 0; j < n; j++) {
			conflictMeasure[j] *= sigma[j];
		}
		final double conflictMeasureSum = DoubleStream.of(conflictMeasure).sum();
		
		// Step 6: Determine the objective weights
		final double[] weights = new double[n];
		for (int j = 0; j < n; j++) {
			weights[j] = conflictMeasure[j] / conflictMeasureSum;
		}
		
		return weights;
	}
	

}
