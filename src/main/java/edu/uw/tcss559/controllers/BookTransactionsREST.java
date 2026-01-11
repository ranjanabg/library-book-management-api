package edu.uw.tcss559.controllers;

import static edu.uw.tcss559.common.Serializer.getJsonOutput;

import java.io.IOException;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import edu.uw.tcss559.client.NotificationClient;
import edu.uw.tcss559.store.BookTransactionsMySQLStore;
import edu.uw.tcss559.structures.CheckoutVerify;

@Path("/book/transaction")
public class BookTransactionsREST {
	
	private BookTransactionsMySQLStore bookTransactionsStore;
	
	public BookTransactionsREST() {
		this.bookTransactionsStore = new BookTransactionsMySQLStore();
	}

	/**
	 * Default page
	 * @return
	 */
    @Path("/")
    @GET
    @Produces("text/html")
    public Response getHandler() {
    	final StringBuilder outputHTML = new StringBuilder();
        outputHTML.append("<html>");
        outputHTML.append("	<body>");
        outputHTML.append(" <h1>Welcome to Library Books Management Service</h1>");
        outputHTML.append(" <h3>#1 REST Service for Books Management</h3>");
        outputHTML.append(" </body>");
        outputHTML.append("</html>");
        return Response.ok().entity(outputHTML.toString()).build();
    }

	/**
	 * Checkout book
	 * @return
	 * @throws Exception 
	 */
	@Path("/checkout/{bookRfID}")
	@POST
	@Produces("application/json")
	public Response checkoutBook(@PathParam("bookRfID") String bookRfID, 
			@FormParam("memberRfID") String memberRfID, @FormParam("pin") int pin, @Context final HttpHeaders headers) throws Exception {
		
		final String userRole = 
				headers.getRequestHeader("userRole") == null ? null : headers.getRequestHeader("userRole").get(0);
		
		if(bookRfID == null || memberRfID == null || userRole == null) {
			return Response
    				.status(Status.BAD_REQUEST)
    				.entity(getJsonOutput("Missing Book RFID or member RFID or member Role !"))
    				.build();
		}
		
		
		
		int id = this.bookTransactionsStore.getBookIdFromRFID(bookRfID);
		if(id == -1) {
			return Response
    				.status(Status.BAD_REQUEST)
    				.entity(getJsonOutput("Cannot associate the correct member."))
    				.build();
		}
		
		CheckoutVerify checkoutConstraints = this.bookTransactionsStore.verifyCheckoutConstraints(id, memberRfID);
		int memberId = checkoutConstraints.getMemberID();
		
		if(checkoutConstraints.getAmountDue() > 0) {
			return Response
    				.status(Status.BAD_REQUEST)
    				.entity(getJsonOutput("Please clear the dues before checking out any additional books"))
    				.build();
		}
		
		if(checkoutConstraints.getBooksIssued() + 1 > checkoutConstraints.getBooksLimit()) {
			return Response
    				.status(Status.BAD_REQUEST)
    				.entity(getJsonOutput("You already have the maximum books checked out."))
    				.build();
		}
				
		if(userRole.equals("READER") && checkoutConstraints.getMemberPin() != pin) {
			return Response
    				.status(Status.BAD_REQUEST)
    				.entity(getJsonOutput("Please provide a valid pin to proceed!"))
    				.build();
		}
		
		CheckoutVerify issueStatus = this.bookTransactionsStore.checkAlreadyIssued(id, memberId);
		if(issueStatus.getMemberID() == memberId) {
			return Response
    				.status(Status.BAD_REQUEST)
    				.entity(getJsonOutput("The requested copy of the book is already issued to you!"))
    				.build();
		}
		
		if(issueStatus.getMemberID() != 0 && issueStatus.getMemberID() != memberId) {
			return Response
    				.status(Status.BAD_REQUEST)
    				.entity(getJsonOutput("The requested copy of the book is currently held by another user!"))
    				.build();
		}
		
		
		this.bookTransactionsStore.completeCheckoutProcess(id, bookRfID, memberId, memberRfID, pin);
		NotificationClient.postNotification(String.valueOf(memberId), "Library: Review Checkout Activity",
				"A new book has been checked out by you. Please view your profile to get more information!");
	    return Response.ok().entity(getJsonOutput(null)).build();
	}
	
	
	/**
	 *Return  book
	 * @return
	 */
	@Path("/return/{bookRfID}")
	@POST
	@Produces("application/json")
	public Response returnBook(@PathParam("bookRfID") String bookRfID) throws IOException {
		
		int id = this.bookTransactionsStore.getBookIdFromRFID(bookRfID);
		
		CheckoutVerify returnDueCheck = this.bookTransactionsStore.returnExceedsDue(id);
		
		if(returnDueCheck == null) {
			return Response
    				.status(Status.BAD_REQUEST)
    				.entity(getJsonOutput("The given book is already returned"))
    				.build();
		}
		
		int memberId = returnDueCheck.getMemberID();
		int bookStatusId = returnDueCheck.getBookStatusId();
		
		// Initiate the current book as returned 
		this.bookTransactionsStore.completeReturnProcess(id,memberId);
		
		// If book is not being returned by the due date, calculate dues and send error message
		if(returnDueCheck.getCurrDate().compareTo(returnDueCheck.getDueDate()) >0 ) {
			// Calculate Pending Dues 
			this.bookTransactionsStore.calculatePendingDues(bookStatusId);
			
			return Response
    				.status(Status.BAD_REQUEST)
    				.entity(getJsonOutput("Return date exceeds the due date, please pay your dues inorder to checkout any new book"))
    				.build();
		}
	    return Response.ok().entity(getJsonOutput(null)).build();
	}
	
	/**
	 *Return  book
	 * @return
	 */
	@Path("/renew/{bookRfID}")
	@POST
	@Produces("application/json")
	public Response renewBook(@PathParam("bookRfID") String bookRfID) throws IOException {
		int id = this.bookTransactionsStore.getBookIdFromRFID(bookRfID);
		CheckoutVerify renewCheck = this.bookTransactionsStore.checkRenewStatus(id);
		
		if(renewCheck == null) {
			return Response
    				.status(Status.BAD_REQUEST)
    				.entity(getJsonOutput("The book has to be checked out inorder to renew it"))
    				.build();
		}
		
		String memberRfID = renewCheck.getMemberRfID();
		int bookStatusID = renewCheck.getBookStatusId();
		int memberId = renewCheck.getMemberID();
		
		CheckoutVerify checkoutConstraints = this.bookTransactionsStore.verifyCheckoutConstraints(id, memberRfID);
		if(checkoutConstraints.getAmountDue() > 0) {
			return Response
    				.status(Status.BAD_REQUEST)
    				.entity(getJsonOutput("Please clear the dues before renewing the book"))
    				.build();
		}
		
		if(checkoutConstraints.getBooksIssued() + 1 > checkoutConstraints.getBooksLimit()) {
			return Response
    				.status(Status.BAD_REQUEST)
    				.entity(getJsonOutput("You have the maximum books checked out."))
    				.build();
		}
				
		CheckoutVerify issueStatus = this.bookTransactionsStore.checkAlreadyIssued(id, memberId);		
		if(issueStatus.getMemberID() != 0 && issueStatus.getMemberID() != memberId) {
			return Response
    				.status(Status.BAD_REQUEST)
    				.entity(getJsonOutput("The requested copy of the book is currently held by another user!"))
    				.build();
		}
		
		System.out.print("Renew Completed");
		this.bookTransactionsStore.completeRenewProcess(bookStatusID, memberId);
		return Response.ok().entity(getJsonOutput(null)).build();
	}

}