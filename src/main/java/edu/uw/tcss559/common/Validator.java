package edu.uw.tcss559.common;

import edu.uw.tcss559.structures.mcda.Alternative;
import edu.uw.tcss559.structures.mcda.DecisionInput;

public class Validator {
	
	private Validator() {}
	
	/**
	 * Validates the input object
	 * @param input
	 */
	public static void validate(final DecisionInput input) {
		for (final Alternative alternative: input.getAlternatives()) {
			if (alternative.getAttributeValues().size() != input.getAttributes().size()) {
				throw new IllegalArgumentException(
					"Attributes count is not matching to Alternatives attributes");
			}
		}
	}
	
}
