package edu.uw.tcss559.structures;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Profile {
	
	private Member memberDetails;
	private List<Book> booksCheckedOut;
	private OverdueFees overdueFees;
	
}
