package edu.uw.tcss559.structures;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookStatus {
	private int id;
	private int bookID;
	private int MemberID;
	private BookIssueType issueType;
	private Date issueDate;
	private Date dueDate;
	private Date returnDate;
	private int noOfTimesRenewed;
}
