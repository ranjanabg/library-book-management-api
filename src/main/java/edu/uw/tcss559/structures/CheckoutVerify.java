package edu.uw.tcss559.structures;

import lombok.Builder;
import lombok.Data;
import java.util.Date;

@Data
@Builder
public class CheckoutVerify {
	private int amountDue;
	private int booksIssued;
	private int booksLimit;	
	private String memberType;
	private int memberPin;
	private int memberID;
	private String memberRfID;
	private int countIssued;
	private Date dueDate;
	private Date currDate;
	private int bookStatusId;
	private int bookID;
}
