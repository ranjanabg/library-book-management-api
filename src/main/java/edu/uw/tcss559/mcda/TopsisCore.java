package edu.uw.tcss559.mcda;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import edu.uw.tcss559.common.Calculate;
import edu.uw.tcss559.common.Transformer;
import edu.uw.tcss559.common.Validator;
import edu.uw.tcss559.structures.mcda.Alternative;
import edu.uw.tcss559.structures.mcda.AlternativeResult;
import edu.uw.tcss559.structures.mcda.TopsisScores;
import edu.uw.tcss559.structures.mcda.DecisionInput;

public class TopsisCore {

	private TopsisCore() {}
	
	/**
	 * Computes performance scores based on TOPSIS Model
	 */
	public static TopsisScores compute(final DecisionInput input) {
		Validator.validate(input);
		
		final double[][] inputMatrix = Transformer.buildMatrix(input.getAlternatives());
		final int m = inputMatrix.length; // rows
		final int n = inputMatrix[0].length; // columns
		
		// Calculate normalized sum, i.e, square root of sum of square of attribute values
		double[] normalizedSum = new double[n];
		for (int j = 0; j < n; j++) {
			for (int i = 0; i < m; i++) {
				normalizedSum[j] += Math.pow(inputMatrix[i][j], 2);
			}
			normalizedSum[j] = Math.sqrt(normalizedSum[j]);
		}
		
		// Normalize attribute value, multiply attribute values with respective weights 
		// and computes the ideal best and worst values.
		// Finally, gives weighted normalized decision matrix
		final double[] idealBest = new double[n];
		final double[] idealWorst = new double[n];
		for (int j = 0; j < n; j++) {
			idealBest[j] = Double.MAX_VALUE;
			idealWorst[j] = Double.MIN_VALUE;
			for (int i = 0; i < m; i++) {
				inputMatrix[i][j] = (inputMatrix[i][j] / normalizedSum[j])
						* input.getAttributes().get(j).getWeight();
				
				if (inputMatrix[i][j] < idealBest[j]) {
					idealBest[j] = inputMatrix[i][j];
				}
				
				if (inputMatrix[i][j] > idealWorst[j]) {
					idealWorst[j] = inputMatrix[i][j];
				}
			}
		}
		
		// Calculate the euclidean distance from both ideal best and ideal worst.
		final double[] euclideanDistanceFromBest = new double[m];
		final double[] euclideanDistanceFromWorst = new double[m];
		for (int i = 0; i < m; i++) {
			double sPlus = 0;
			double sMinus = 0;
			
			for (int j = 0; j < n; j++) {
				sPlus += Math.pow(inputMatrix[i][j] - idealBest[j], 2);
				sMinus += Math.pow(inputMatrix[i][j] - idealWorst[j], 2);
			}
			
			euclideanDistanceFromBest[i] = Math.sqrt(sPlus); 
			euclideanDistanceFromWorst[i] = Math.sqrt(sMinus);
		}

		// Calculate the performance scores
		final double[] perfScores = new double[m];
		for (int i = 0; i < m; i++) {
			perfScores[i] = euclideanDistanceFromWorst[i] 
					/ (euclideanDistanceFromBest[i] + euclideanDistanceFromWorst[i]);
			
		}
		
		return buildModelResponse(input.getAlternatives(), perfScores);
	}
	
	/**
	 * Builds the response from a given list of performance scores
	 * @param alternatives List of alternatives
	 * @param perfScores respective performance scores
	 * @return Model response object
	 */
	private static TopsisScores buildModelResponse(final List<Alternative> alternatives,
											 final double[] perfScores) {
		final List<AlternativeResult> alternativeResults = new ArrayList<>();
		for (int i = 0; i < alternatives.size(); i++) {
			final AlternativeResult alternativeResult = new AlternativeResult();
			alternativeResult.setBook(alternatives.get(i).getBook());
			alternativeResult.setScore(perfScores[i]);
			
			alternativeResults.add(alternativeResult);
		}
		
		alternativeResults.sort(
				Comparator.comparingDouble(AlternativeResult::getScore).reversed());
		for (int rank = 1; rank <= alternativeResults.size(); rank++) {
			alternativeResults.get(rank - 1).setRanking(rank);
		}	
		
		final TopsisScores result = new TopsisScores();
		result.setAlternativeResults(alternativeResults);
		result.setAvgPerformanceScore(alternativeResults
				.stream().mapToDouble(a -> a.getScore()).average().getAsDouble());
		result.setStandardDeviation(Calculate.standardDeviation(alternativeResults
				.stream().map(a -> a.getScore()).collect(Collectors.toList())));
		
		return result;		
	}

}
