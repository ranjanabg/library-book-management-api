package edu.uw.tcss559.structures;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {
	
	private int id;
	private String rfid;
	private String firstName;
	private String lastName;
	private String emailId;
	private String password;
	private String mobileNo;
	private Date dateOfBirth;
	private MemberType type;
	private MemberStatus status;
	private Date createdOn;
	private int uniquePin;
    private Integer booksIssued;
    private Integer booksLimit;

}
