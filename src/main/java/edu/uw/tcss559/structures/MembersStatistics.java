package edu.uw.tcss559.structures;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MembersStatistics {
	
	private int totalNumberOfRegisteredMembers;
	private int totalNumberOfActiveMembers;
	private int totalNumberOfReaders;
	private int totalNumberOfLibrarians;
	private int totalNumberOfBooksIssued;

}
