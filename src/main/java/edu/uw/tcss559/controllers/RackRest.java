package edu.uw.tcss559.controllers;

import static edu.uw.tcss559.common.Serializer.getJsonOutput;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import edu.uw.tcss559.store.RacksMySQLStore;
import edu.uw.tcss559.structures.Rack;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;

import java.io.IOException;
import java.util.List;

@Path("/racks")
public class RackRest {
	
private RacksMySQLStore rackStore;
	
	public RackRest() {
		this.rackStore = new RacksMySQLStore();
	}
	
	/**
	 *Verify all books location
	 * @return
	 */
	@Path("/verify")
	@GET
	@Produces("application/json")
	public Response findAllMisplacedBooks() throws IOException {

		 List<Rack> resultSet = this.rackStore.listAllMisplacedBooks();
		 StringBuilder body = new StringBuilder();
		 if(resultSet != null && resultSet.size() > 0) {
			 body.append("Please find the overall summary of misplaced books:\n");
			 for(int i=0; i < resultSet.size(); i++) {
				 body.append(" Book ID: " + resultSet.get(i).getBookID() + " Correct location: " + resultSet.get(i).getCorrectRackLocation() + " Current Location  " + resultSet.get(i).getCorrectRackLocation());
			}
		 }
		 else {
			 body.append("All books are placed at the correct positions. No misplaced book found");
		 }
		 
		 NotificationREST obj = new NotificationREST();
		 obj.sendEmail("Summary of misplaced books in the library", body.toString(), "ssukhija@uw.edu");
		 return Response.ok().entity(getJsonOutput(null)).build();
	}
	
	
	/**
	 *Verify all books kept at a given rack
	 * @return
	 */
	@Path("/verify/{rack_id}")
	@GET
	@Produces("application/json")
	public Response findRackMisplacedBooks(@PathParam("rack_id") String id) throws IOException {
		
		List<Rack> resultSet = this.rackStore.listRackMisplacedBooks(id);
		 StringBuilder body = new StringBuilder();
		 if(resultSet != null && resultSet.size() > 0) {
			 body.append("Please find the overall summary of misplaced books:\n");
			 for(int i=0; i < resultSet.size(); i++) {
				 body.append(" Book ID: " + resultSet.get(i).getBookID() + " Correct location: " + resultSet.get(i).getCorrectRackLocation() + " Current Location  " + resultSet.get(i).getCorrectRackLocation());
			}
		 }
		 else {
			 body.append("All books are placed at the correct positions. No misplaced book found");
		 }
		 
		 NotificationREST obj = new NotificationREST();
		 obj.sendEmail("Summary of misplaced books in the library", body.toString(), "ssukhija@uw.edu");
		 return Response.ok().entity(getJsonOutput(null)).build();
	}
	

	/**
	 *Verify a particular book kept at a given rack
	 * @return
	 */
	@Path("/verify/{rack_id}/{book_id}")
	@GET
	@Produces("application/json")
	public Response findRackMisplacedBooks(@PathParam("rack_id") String id, @PathParam("book_id") int bookID) throws IOException {
		
		List<Rack> resultSet = this.rackStore.findRackMisplacedBook(id, bookID);
		 StringBuilder body = new StringBuilder();
		 if(resultSet != null && resultSet.size() > 0) {
			 body.append("Please find the overall summary of misplaced books:\n");
			 for(int i=0; i < resultSet.size(); i++) {
				 body.append(" Book ID: " + resultSet.get(i).getBookID() + " Correct location: " + resultSet.get(i).getCorrectRackLocation() + " Current Location  " + resultSet.get(i).getCorrectRackLocation());
			}
		 }
		 else {
			 body.append("All books are placed at the correct positions. No misplaced book found");
		 }
		 
		 NotificationREST obj = new NotificationREST();
		 obj.sendEmail("Summary of misplaced books in the library", body.toString(), "ssukhija@uw.edu");
		 return Response.ok().entity(resultSet.size() > 0 ? getJsonOutput("Book location is incorrect. Correct Rack is " + resultSet.get(0).getCorrectRackLocation()) : 
			 getJsonOutput(null)).build();
	}
		
}
