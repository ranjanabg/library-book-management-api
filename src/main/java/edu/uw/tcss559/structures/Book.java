package edu.uw.tcss559.structures;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Book {
	
	private int id;
	private String isbn;
	private String rfid;
	private String title;
	private String description;
	private String authorName;
	private String publisherName;
	private String owner;
	private float price;
	private int pages;
	private int readCount;
	private float readersRating;
	private float criticsRating;
	private Integer maxIssueDays;
	private String rackId;
	
	private Date issuedDate;
	private Date dueDate;
	private int noOfTimesRenewed;
	
}
