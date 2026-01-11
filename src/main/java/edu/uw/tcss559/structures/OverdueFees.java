package edu.uw.tcss559.structures;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OverdueFees {
	private int memberID;
	private int bookID;
	private float amountDue;
	private boolean amountPaid;
	private Date datePaid;
}
