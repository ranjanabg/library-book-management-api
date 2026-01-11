package edu.uw.tcss559.structures.mcda;

import java.util.List;

import lombok.Data;

@Data
public class TopsisScores {
    public double avgPerformanceScore;
    public double standardDeviation;
	public List<AlternativeResult> alternativeResults;
}
