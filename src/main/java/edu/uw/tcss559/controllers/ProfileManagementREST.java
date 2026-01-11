package edu.uw.tcss559.controllers;

import static edu.uw.tcss559.common.Serializer.getJsonOutput;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import edu.uw.tcss559.client.MembersClient;
import edu.uw.tcss559.client.NotificationClient;
import edu.uw.tcss559.store.BookTransactionsMySQLStore;
import edu.uw.tcss559.store.MembersMySQLStore;
import edu.uw.tcss559.store.OverdueFeesMySQLStore;
import edu.uw.tcss559.structures.Book;
import edu.uw.tcss559.structures.Member;
import edu.uw.tcss559.structures.MemberStatus;
import edu.uw.tcss559.structures.MemberType;
import edu.uw.tcss559.structures.OverdueFees;
import edu.uw.tcss559.structures.Profile;

@Path("/profile")
public class ProfileManagementREST extends AbstractREST {

	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");
	
	private final List<String> mandatoryFields = Arrays.asList("rfid",
			"firstName", "lastName", "emailId", "password", "mobileNo",
			"dateOfBirth", "type", "uniquePin");
	
	private BookTransactionsMySQLStore bookTransactionsMySQLStore;
	private OverdueFeesMySQLStore overdueFeesStore;
	
	public ProfileManagementREST() {
		this.bookTransactionsMySQLStore = new BookTransactionsMySQLStore();
		this.overdueFeesStore = new OverdueFeesMySQLStore();
		this.bookTransactionsMySQLStore = new BookTransactionsMySQLStore();
	}
	
	/**
	 * Views the profile of given memberId
	 * @param memberId
	 * @return
	 */
	@Path("/view/{memberId}")
	@GET
	@Produces("application/json")
	public Response viewHandler(@PathParam("memberId") final String memberId) {
		try {
			// Get the member details
			final Member memberDetails = 
					MembersClient.getMember(memberId);
			
			// Get list of books checked out by member
			final List<Book> booksCheckedout = 
					this.bookTransactionsMySQLStore.memberCheckedOutBooks(memberId);
			
			// Get the overdue fees details
			final OverdueFees overdueFees = 
					this.overdueFeesStore.readOverdueFees(memberId);
			
			return buildJsonResponse(Profile.builder()
					.memberDetails(memberDetails)
					.booksCheckedOut(booksCheckedout)
					.overdueFees(overdueFees)
					.build());
		} catch (final Exception ex) {
			ex.printStackTrace();
			return Response.serverError().entity(ex).build();
		}
	}
	
	/**
	 * Registers a member with provided basic details
	 * @param headers
	 * @return
	 */
    @Path("/register")
    @POST
    @Produces("application/json")
	public Response registerHandler(@Context final HttpHeaders headers) {
		for (final String field: mandatoryFields) {
	    	if (headers.getRequestHeader(field) == null) {
	    		return Response
	    				.status(Status.BAD_REQUEST)
	    				.entity("Member '" + field + "' is not passed in the request header")
	    				.build();
	    	}	
		}
		
    	try {
        	// Create a member record
			final Member member = Member.builder()
					.rfid(headers.getRequestHeader("rfid").get(0))
					.firstName(headers.getRequestHeader("firstName").get(0))
					.lastName(headers.getRequestHeader("lastName").get(0))
					.emailId(headers.getRequestHeader("emailId").get(0))
					.password(headers.getRequestHeader("password").get(0))
					.mobileNo(headers.getRequestHeader("mobileNo").get(0))
					.dateOfBirth(DATE_FORMAT.parse(headers.getRequestHeader("dateOfBirth").get(0)))
					.type(MemberType.valueOf(headers.getRequestHeader("type").get(0)))
					.status(MemberStatus.ACTIVE)
					.uniquePin(Integer.parseInt(headers.getRequestHeader("uniquePin").get(0)))
					.build();	
			
			final Member addedMember = MembersClient.addMember(member);
			
        	// TODO:Ranjana Create a payment account
    		
    		// Notify the member about successful membership registration
        	NotificationClient.postNotification(String.valueOf(addedMember.getId()),
        			"Library Membership Registration",
        			"Congratulations! Membership registration successfully completed.");
        	
        	return Response.ok().build();	
    	} catch (final Exception ex) {
    		ex.printStackTrace();
    		return Response.serverError().entity(ex).build();
    	}
	}
	
    /**
     * Cancels the membership of given memberId along with sending notification 
     * @param memberId
     * @return
     */
    @Path("/cancel/{member_id}")
    @PUT
    @Produces("application/json")
	public Response cancelHandler(
			@PathParam("member_id") final String memberId) {
    	try {
			// TODO:Ranjana See if the member has any outstanding books to return
    		
			// Update the member `status` to INACTIVE
    		final Map<String, String> updateMap = new HashMap<>();
    		updateMap.put("status", "INACTIVE");
    		
    		MembersClient.updateMember(memberId, updateMap);
    		
    		// Notify the member about successful membership cancellation
			NotificationClient.postNotification(memberId,
					"Library Membership Cancellation",
					"Your membership cancellation is complete! Hope you return soon.");
 
			return Response.ok().build();
    	} catch (final Exception ex) {
    		ex.printStackTrace();
    		return Response.serverError().entity(ex).build();
    	}
	}

    /**
     * Increases or decreases the max books a member can borrow
     * @param memberId
     * @param headers
     * @return
     */
    @Path("/limit/{member_id}")
    @PUT
    @Produces("application/json")
	public Response changeBooksLimitHandler(
			@PathParam("member_id") final String memberId,
			@Context final HttpHeaders headers) {
    	if (headers.getRequestHeader("new_limit") == null) {
    		return Response
    				.status(Status.BAD_REQUEST)
    				.entity("'new_limit' is not passed in the request header")
    				.build();
    	}
    	
    	final String newLimit = headers.getRequestHeader("new_limit").get(0);
    	
    	try {
			// Update the member `booksLimit` to given limit
    		final Map<String, String> updateMap = new HashMap<>();
    		updateMap.put("booksLimit", newLimit);
    		
    		MembersClient.updateMember(memberId, updateMap);
    		
    		// Notify the member about successful books limit change
			NotificationClient.postNotification(memberId,
					"Library Membership Book Limit Change",
					"Successfully changed your membership books limit to " + newLimit);
			
			return Response.ok().build();
    	} catch (final Exception ex) {
    		ex.printStackTrace();
    		return Response.serverError().entity(ex).build();
    	}
	}
    
    
    @Path("/payDues/{member_id}")
    @POST
    @Produces("application/json")
	public Response paymentDue(
			@PathParam("member_id") final String memberId, @FormParam("cardNumber") String cardNumber, @FormParam("expiryDate") String expiryDate, @FormParam("amountDue") int amountDue) {
    	
  
    	StringBuilder error_msg = new StringBuilder();
    	
    	if(cardNumber == null)
    		error_msg.append("Please enter the card number!");
    	if(expiryDate == null)
    		error_msg.append("Please enter the expiration date!");
    	
    	MembersMySQLStore memberStore = new MembersMySQLStore();
    	
    	boolean result = memberStore.completeOverduePayment(memberId,amountDue);
    	System.out.println(result);
    	if(result == false) {
    		return Response
    				.status(Status.BAD_REQUEST)
    				.entity(getJsonOutput("No overdue found!"))
    				.build();
    	}
  
    	return Response.ok().entity(getJsonOutput(null)).build();
	}
}

