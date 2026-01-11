package edu.uw.tcss559.common;

import java.util.ArrayList;
import java.util.List;

import edu.uw.tcss559.structures.Book;
import edu.uw.tcss559.structures.mcda.Alternative;
import edu.uw.tcss559.structures.mcda.Attribute;
import edu.uw.tcss559.structures.mcda.AttributesResponse;

public class Transformer {
	
	private Transformer() {}

	/**
	 * Builds a 2-D array (matrix) from given list of alternatives.
	 * This matrix makes TOPSIS calculation easy.
	 * @param alternatives A list of alternatives from which matrix is built
	 * @return 2-D array (matrix)
	 */
	public static double[][] buildMatrix(final List<Alternative> alternatives) {
		final int m = alternatives.size();
		final int n = alternatives.get(0).getAttributeValues().size();
		
		final double[][] matrix = new double[m][n];
		
		for (int j = 0; j < n; j++) {
			for (int i = 0; i < m; i++) {
				matrix[i][j] = alternatives.get(i).getAttributeValues().get(j);
			}
		}
		
		return matrix;
	}
	
	/**
	 * Builds the attribute response from updated attribute weights
	 * @param attributes
	 * @param weights
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static AttributesResponse buildAttributesResponse(
			final List<Attribute> attributes, final double[] weights)
					throws IllegalArgumentException {
		return new AttributesResponse(updateAttributeWeights(attributes, weights));
	}
	
	/**
	 * Build alternatives list from the given list of books
	 * @return books
	 */
	public static List<Alternative> buildAlternatives(
			final List<Book> books) {
		final List<Alternative> alternatives = new ArrayList<>();
		
		books.forEach(book -> {
			final List<Integer> attributeValues = new ArrayList<>();
			
			attributeValues.add(Math.round(book.getPrice()));
			attributeValues.add(book.getPages());
			attributeValues.add(book.getReadCount());
			attributeValues.add(Math.round(book.getReadersRating()));
			attributeValues.add(Math.round(book.getCriticsRating()));
			
			final Alternative alternative = new Alternative();
			alternative.setBook(book);
			alternative.setAttributeValues(attributeValues);
			
			alternatives.add(alternative);
		});
		
		return alternatives;
	}
	
	/**
	 * Updates the attributes weights with given weights
	 * @param attributes
	 * @param weights
	 * @throws IllegalArgumentException
	 */
	public static List<Attribute> updateAttributeWeights(
			final List<Attribute> attributes, final double[] weights) 
					throws IllegalArgumentException {
		if (attributes.size() != weights.length) {
			throw new IllegalArgumentException("Lengths do not match!");
		}
		
		final List<Attribute> updatedAttributes = new ArrayList<>();

		for (int i = 0; i < weights.length; i++) {
			updatedAttributes.add(
					new Attribute(
							attributes.get(i).getName(),
							weights[i]));
		}
		
		return updatedAttributes;
	}
	
}
