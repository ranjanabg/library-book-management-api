package edu.uw.tcss559.structures;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Rack {
	private int bookID;
	private int rackID;
	private String correctRackLocation;
	private String currentRackLocation;
}
