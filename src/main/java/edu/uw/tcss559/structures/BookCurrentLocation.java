package edu.uw.tcss559.structures;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookCurrentLocation {
	
	private int bookId;
	private int rackId;
	
}
