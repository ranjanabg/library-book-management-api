package edu.uw.tcss559.structures.mcda;

import edu.uw.tcss559.structures.Book;
import lombok.Data;

@Data
public class AlternativeResult {
	public Book book;
	public Double score;
	public int ranking;
}
